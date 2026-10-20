package com.coungard.univer.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coungard.univer.TestRegions;
import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.SemesterType;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Lecture;
import com.coungard.univer.entity.LectureAttendance;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.entity.University;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.GroupRepository;
import com.coungard.univer.repository.LectureAttendanceRepository;
import com.coungard.univer.repository.LectureRepository;
import com.coungard.univer.repository.LectureSelfAttendanceRepository;
import com.coungard.univer.repository.RegionRepository;
import com.coungard.univer.repository.SemesterRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.StudyYearRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.security.KeycloakAdminService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Личные отметки посещаемости студента (issue #117): отметка своей начавшейся лекции, приоритет
 * отметки преподавателя, снятие отметки и сводка по курсу.
 *
 * <p>Токен не настоящий: {@link JwtDecoder} подменён и по строке токена отдаёт JWT с нужными
 * {@code sub} и ролями.
 */
@SpringBootTest(classes = UniverApplication.class)
@AutoConfigureMockMvc
@Testcontainers
class MyAttendanceControllerTest {

  private static final String STUDENT_TOKEN = "student-token";
  private static final String NO_GROUP_STUDENT_TOKEN = "no-group-student-token";
  private static final String ADMIN_TOKEN = "admin-token";

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
      .withDatabaseName("univer_test")
      .withUsername("postgres")
      .withPassword("postgres");

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private LectureSelfAttendanceRepository selfAttendanceRepository;

  @Autowired
  private LectureAttendanceRepository attendanceRepository;

  @Autowired
  private LectureRepository lectureRepository;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private SemesterRepository semesterRepository;

  @Autowired
  private StudyYearRepository studyYearRepository;

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private UniversityRepository universityRepository;

  @Autowired
  private RegionRepository regionRepository;

  @MockBean
  private JwtDecoder jwtDecoder;

  @MockBean
  private KeycloakAdminService keycloakAdminService;

  private UUID studentId;
  private UUID courseId;
  private Group group;
  private Group otherGroup;

  @BeforeEach
  void setUp() {
    selfAttendanceRepository.deleteAll();
    attendanceRepository.deleteAll();
    lectureRepository.deleteAll();
    studentRepository.deleteAll();
    groupRepository.deleteAll();
    semesterRepository.deleteAll();
    studyYearRepository.deleteAll();
    courseRepository.deleteAll();
    facultyRepository.deleteAll();
    universityRepository.deleteAll();

    University university = new University();
    university.setName("Test University");
    university.setRegion(TestRegions.create(regionRepository));
    university = universityRepository.save(university);

    Faculty faculty = facultyRepository.save(Faculty.builder().name("ФКТиЭ").university(university).build());

    StudyYear studyYear = new StudyYear();
    studyYear.setFacultyId(faculty.getId());
    studyYear.setYearNumber(1);
    studyYear = studyYearRepository.save(studyYear);

    // Семестр идёт сейчас: часть лекций уже прошла, часть впереди
    Semester semester = new Semester();
    semester.setStudyYear(studyYear);
    semester.setType(SemesterType.AUTUMN);
    semester.setStartDate(LocalDate.now().minusDays(30));
    semester.setEndDate(LocalDate.now().plusDays(60));
    semester = semesterRepository.save(semester);

    group = createGroup(semester, "У533");
    otherGroup = createGroup(semester, "У530");

    Course course = new Course();
    course.setTitle("Физика");
    courseId = courseRepository.save(course).getId();

    studentId = createStudent("ivan", group);
    when(jwtDecoder.decode(STUDENT_TOKEN)).thenReturn(jwt(studentId, "ROLE_STUDENT"));
    when(jwtDecoder.decode(NO_GROUP_STUDENT_TOKEN)).thenReturn(jwt(createStudent("petr", null), "ROLE_STUDENT"));
    when(jwtDecoder.decode(ADMIN_TOKEN)).thenReturn(jwt(UUID.randomUUID(), "ROLE_ADMIN"));
  }

  @Test
  void shouldMarkOwnStartedLectureAndChangeMark() throws Exception {
    UUID lectureId = createLecture(group, courseId, LocalDateTime.now().minusDays(1));

    mark(STUDENT_TOKEN, lectureId, true)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lectureId").value(lectureId.toString()))
        .andExpect(jsonPath("$.attended").value(true))
        .andExpect(jsonPath("$.source").value("STUDENT"));

    // Повторная отметка меняет прежнюю, а не добавляет вторую
    mark(STUDENT_TOKEN, lectureId, false)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.attended").value(false));

    mockMvc.perform(get("/api/v1/attendance/me").header(HttpHeaders.AUTHORIZATION, bearer(STUDENT_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].lectureId").value(lectureId.toString()))
        .andExpect(jsonPath("$.content[0].attended").value(false))
        .andExpect(jsonPath("$.content[0].source").value("STUDENT"));
  }

  @Test
  void shouldMarkLectureWithoutEnrollmentButOnlyOwnAndStarted() throws Exception {
    // Студент ни на что не зачислен — личной отметке это не мешает
    UUID ownStarted = createLecture(group, courseId, LocalDateTime.now().minusMinutes(5));
    mark(STUDENT_TOKEN, ownStarted, true).andExpect(status().isOk());

    // Лекция чужой группы
    UUID foreign = createLecture(otherGroup, courseId, LocalDateTime.now().minusDays(1));
    mark(STUDENT_TOKEN, foreign, true).andExpect(status().isUnprocessableEntity());

    // Ещё не началась
    UUID future = createLecture(group, courseId, LocalDateTime.now().plusHours(1));
    mark(STUDENT_TOKEN, future, true).andExpect(status().isUnprocessableEntity());

    // Студент без группы — своих лекций у него нет
    mark(NO_GROUP_STUDENT_TOKEN, ownStarted, true).andExpect(status().isUnprocessableEntity());

    mark(STUDENT_TOKEN, UUID.randomUUID(), true).andExpect(status().isNotFound());

    mockMvc.perform(put("/api/v1/attendance/me/lecture/{id}", ownStarted)
            .header(HttpHeaders.AUTHORIZATION, bearer(STUDENT_TOKEN))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isBadRequest());

    // Ни одна отклонённая отметка не сохранилась
    assertThat(selfAttendanceRepository.count()).isEqualTo(1);
  }

  @Test
  void shouldLetTeacherMarkOverridePersonalOne() throws Exception {
    UUID lectureId = createLecture(group, courseId, LocalDateTime.now().minusDays(1));
    mark(STUDENT_TOKEN, lectureId, false).andExpect(status().isOk());

    // Преподаватель отметил, что студент был
    teacherMark(lectureId, true);

    mockMvc.perform(get("/api/v1/attendance/me").header(HttpHeaders.AUTHORIZATION, bearer(STUDENT_TOKEN)))
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].attended").value(true))
        .andExpect(jsonPath("$.content[0].source").value("TEACHER"));

    // Личная отметка по-прежнему сохраняется, но ответ показывает итоговую — преподавателя
    mark(STUDENT_TOKEN, lectureId, false)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.attended").value(true))
        .andExpect(jsonPath("$.source").value("TEACHER"));

    // Выборки и статистика преподавателя личных отметок не видят
    mockMvc.perform(get("/api/v1/attendance/lecture/{id}/stats", lectureId)
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalMarked").value(1))
        .andExpect(jsonPath("$.attendedCount").value(1));
  }

  @Test
  void shouldUnmarkPersonalMarkOnly() throws Exception {
    UUID personal = createLecture(group, courseId, LocalDateTime.now().minusDays(2));
    UUID byTeacher = createLecture(group, courseId, LocalDateTime.now().minusDays(1));
    mark(STUDENT_TOKEN, personal, true).andExpect(status().isOk());
    mark(STUDENT_TOKEN, byTeacher, true).andExpect(status().isOk());
    teacherMark(byTeacher, false);

    unmark(personal).andExpect(status().isNoContent());
    unmark(byTeacher).andExpect(status().isNoContent());
    // Снимать уже снятую отметку не ошибка
    unmark(personal).andExpect(status().isNoContent());

    // Осталась только отметка преподавателя
    mockMvc.perform(get("/api/v1/attendance/me").header(HttpHeaders.AUTHORIZATION, bearer(STUDENT_TOKEN)))
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].lectureId").value(byTeacher.toString()))
        .andExpect(jsonPath("$.content[0].attended").value(false))
        .andExpect(jsonPath("$.content[0].source").value("TEACHER"));
  }

  @Test
  void shouldSummarizeCourseWithoutMarks() throws Exception {
    createLecture(group, courseId, LocalDateTime.now().minusDays(3));
    createLecture(group, courseId, LocalDateTime.now().minusDays(1));
    createLecture(group, courseId, LocalDateTime.now().plusDays(1));

    summary()
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pastLectures").value(2))
        .andExpect(jsonPath("$.attended").value(0))
        .andExpect(jsonPath("$.missed").value(0))
        .andExpect(jsonPath("$.unmarked").value(2));
  }

  @Test
  void shouldSummarizeCourseWithMixedMarks() throws Exception {
    UUID attendedByOwnMark = createLecture(group, courseId, LocalDateTime.now().minusDays(4));
    UUID missedByOwnMark = createLecture(group, courseId, LocalDateTime.now().minusDays(3));
    UUID missedByTeacher = createLecture(group, courseId, LocalDateTime.now().minusDays(2));
    createLecture(group, courseId, LocalDateTime.now().minusDays(1));
    mark(STUDENT_TOKEN, attendedByOwnMark, true).andExpect(status().isOk());
    mark(STUDENT_TOKEN, missedByOwnMark, false).andExpect(status().isOk());
    mark(STUDENT_TOKEN, missedByTeacher, true).andExpect(status().isOk());
    teacherMark(missedByTeacher, false);

    // Не в счёт: будущая лекция, лекция другого курса, лекция другой группы
    createLecture(group, courseId, LocalDateTime.now().plusDays(1));
    Course otherCourse = new Course();
    otherCourse.setTitle("Химия");
    UUID otherCourseLecture = createLecture(group, courseRepository.save(otherCourse).getId(),
        LocalDateTime.now().minusDays(1));
    mark(STUDENT_TOKEN, otherCourseLecture, true).andExpect(status().isOk());
    createLecture(otherGroup, courseId, LocalDateTime.now().minusDays(1));

    summary()
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pastLectures").value(4))
        .andExpect(jsonPath("$.attended").value(1))
        .andExpect(jsonPath("$.missed").value(2))
        .andExpect(jsonPath("$.unmarked").value(1));
  }

  @Test
  void shouldRequireStudentRole() throws Exception {
    UUID lectureId = createLecture(group, courseId, LocalDateTime.now().minusDays(1));

    mockMvc.perform(get("/api/v1/attendance/me")).andExpect(status().isUnauthorized());
    mark(ADMIN_TOKEN, lectureId, true).andExpect(status().isForbidden());
    mockMvc.perform(get("/api/v1/attendance/me/course/{id}/summary", courseId)
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
        .andExpect(status().isForbidden());
  }

  private ResultActions mark(String token, UUID lectureId, boolean attended) throws Exception {
    return mockMvc.perform(put("/api/v1/attendance/me/lecture/{id}", lectureId)
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"attended\": " + attended + "}"));
  }

  private ResultActions unmark(UUID lectureId) throws Exception {
    return mockMvc.perform(delete("/api/v1/attendance/me/lecture/{id}", lectureId)
        .header(HttpHeaders.AUTHORIZATION, bearer(STUDENT_TOKEN)));
  }

  private ResultActions summary() throws Exception {
    return mockMvc.perform(get("/api/v1/attendance/me/course/{id}/summary", courseId)
        .header(HttpHeaders.AUTHORIZATION, bearer(STUDENT_TOKEN)));
  }

  private void teacherMark(UUID lectureId, boolean attended) {
    LectureAttendance mark = new LectureAttendance();
    mark.setStudentId(studentId);
    mark.setLectureId(lectureId);
    mark.setAttended(attended);
    attendanceRepository.save(mark);
  }

  private Group createGroup(Semester semester, String name) {
    Group created = new Group();
    created.setSemester(semester);
    created.setName(name);
    return groupRepository.save(created);
  }

  private UUID createLecture(Group lectureGroup, UUID lectureCourseId, LocalDateTime scheduledTime) {
    Lecture lecture = new Lecture();
    lecture.setTitle("Занятие");
    lecture.setScheduledTime(scheduledTime);
    lecture.setCourse(courseRepository.getReferenceById(lectureCourseId));
    lecture.setGroups(new HashSet<>(Set.of(lectureGroup)));
    return lectureRepository.save(lecture).getId();
  }

  private UUID createStudent(String username, Group studentGroup) {
    Person person = new Person();
    person.setUsername(username);
    person.setFirstname(username);
    person.setLastname(username);
    person.setFullname(username);
    person.setEmail(username + "@test.com");

    Student student = new Student();
    student.setPerson(person);
    student.setGroup(studentGroup);
    return studentRepository.save(student).getId();
  }

  private static String bearer(String token) {
    return "Bearer " + token;
  }

  private static Jwt jwt(UUID subject, String role) {
    return Jwt.withTokenValue("token")
        .header("alg", "none")
        .subject(subject.toString())
        .claim("realm_access", Map.of("roles", List.of(role)))
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(300))
        .build();
  }
}
