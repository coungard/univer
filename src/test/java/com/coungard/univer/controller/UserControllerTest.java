package com.coungard.univer.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coungard.univer.TestRegions;
import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.AuditAction;
import com.coungard.univer.dto.AuditEntityType;
import com.coungard.univer.dto.EnrollmentStatus;
import com.coungard.univer.dto.FeedbackPlatform;
import com.coungard.univer.dto.FeedbackType;
import com.coungard.univer.dto.SemesterType;
import com.coungard.univer.dto.WeekParity;
import com.coungard.univer.entity.AuditEvent;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Enrollment;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Feedback;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Lecture;
import com.coungard.univer.entity.LectureAttendance;
import com.coungard.univer.entity.LectureSelfAttendance;
import com.coungard.univer.entity.Pair;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.entity.Teacher;
import com.coungard.univer.entity.University;
import com.coungard.univer.entity.UniversityRequest;
import com.coungard.univer.entity.WeekScheduleCycle;
import com.coungard.univer.repository.AuditEventRepository;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.EnrollmentRepository;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.FeedbackRepository;
import com.coungard.univer.repository.GroupRepository;
import com.coungard.univer.repository.LectureAttendanceRepository;
import com.coungard.univer.repository.LectureRepository;
import com.coungard.univer.repository.LectureSelfAttendanceRepository;
import com.coungard.univer.repository.PairRepository;
import com.coungard.univer.repository.RegionRepository;
import com.coungard.univer.repository.SemesterRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.StudyYearRepository;
import com.coungard.univer.repository.TeacherRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.repository.UniversityRequestRepository;
import com.coungard.univer.repository.WeekScheduleCycleRepository;
import com.coungard.univer.security.KeycloakAdminService;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import org.springframework.jdbc.core.JdbcTemplate;
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
 * Удаление аккаунта самим пользователем (issue #133): что удаляется и обезличивается у студента и
 * преподавателя, что остаётся у соседей, откат при недоступном Keycloak и права на путь.
 *
 * <p>Токен не настоящий: {@link JwtDecoder} подменён и по строке токена отдаёт JWT с нужными {@code sub} и
 * ролями. Keycloak тоже подменён — проверяется, что его просят удалить именно этого пользователя.
 */
@SpringBootTest(classes = UniverApplication.class)
@AutoConfigureMockMvc
@Testcontainers
class UserControllerTest {

  private static final String STUDENT_TOKEN = "student-token";
  private static final String TEACHER_TOKEN = "teacher-token";
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
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private AuditEventRepository auditEventRepository;

  @Autowired
  private FeedbackRepository feedbackRepository;

  @Autowired
  private UniversityRequestRepository universityRequestRepository;

  @Autowired
  private LectureSelfAttendanceRepository selfAttendanceRepository;

  @Autowired
  private LectureAttendanceRepository attendanceRepository;

  @Autowired
  private EnrollmentRepository enrollmentRepository;

  @Autowired
  private LectureRepository lectureRepository;

  @Autowired
  private PairRepository pairRepository;

  @Autowired
  private WeekScheduleCycleRepository weekScheduleCycleRepository;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private TeacherRepository teacherRepository;

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

  private University university;
  private Group group;
  private Semester semester;
  private Course course;
  private UUID studentId;
  private UUID otherStudentId;
  private UUID teacherId;

  @BeforeEach
  void setUp() {
    auditEventRepository.deleteAll();
    feedbackRepository.deleteAll();
    universityRequestRepository.deleteAll();
    selfAttendanceRepository.deleteAll();
    attendanceRepository.deleteAll();
    enrollmentRepository.deleteAll();
    lectureRepository.deleteAll();
    pairRepository.deleteAll();
    weekScheduleCycleRepository.deleteAll();
    studentRepository.deleteAll();
    courseRepository.deleteAll();
    teacherRepository.deleteAll();
    groupRepository.deleteAll();
    semesterRepository.deleteAll();
    studyYearRepository.deleteAll();
    facultyRepository.deleteAll();
    universityRepository.deleteAll();

    university = new University();
    university.setName("Test University");
    university.setRegion(TestRegions.create(regionRepository));
    university = universityRepository.save(university);

    Faculty faculty = facultyRepository.save(Faculty.builder().name("ФКТиЭ").university(university).build());

    StudyYear studyYear = new StudyYear();
    studyYear.setFacultyId(faculty.getId());
    studyYear.setYearNumber(1);
    studyYear = studyYearRepository.save(studyYear);

    semester = new Semester();
    semester.setStudyYear(studyYear);
    semester.setType(SemesterType.AUTUMN);
    semester.setStartDate(LocalDate.now().minusDays(30));
    semester.setEndDate(LocalDate.now().plusDays(60));
    semester = semesterRepository.save(semester);

    group = new Group();
    group.setSemester(semester);
    group.setName("У533");
    group = groupRepository.save(group);

    teacherId = createTeacher("teacher");

    course = new Course();
    course.setTitle("Физика");
    course.setTeacher(teacherRepository.getReferenceById(teacherId));
    course = courseRepository.save(course);

    studentId = createStudent("ivan");
    otherStudentId = createStudent("petr");

    when(jwtDecoder.decode(STUDENT_TOKEN)).thenReturn(jwt(studentId, "ROLE_STUDENT"));
    when(jwtDecoder.decode(TEACHER_TOKEN)).thenReturn(jwt(teacherId, "ROLE_TEACHER"));
    when(jwtDecoder.decode(ADMIN_TOKEN)).thenReturn(jwt(UUID.randomUUID(), "ROLE_ADMIN"));
    when(keycloakAdminService.deleteUserIfExists(anyString())).thenReturn(true);
  }

  @Test
  void shouldDeleteStudentAccountWithPersonalData() throws Exception {
    UUID lectureId = createLecture(null);
    for (UUID id : List.of(studentId, otherStudentId)) {
      enroll(id);
      teacherMark(id, lectureId);
      selfMark(id, lectureId);
      requestUniversity(id);
      createFeedback(id, "ivan");
    }

    deleteMe(STUDENT_TOKEN).andExpect(status().isNoContent());

    verify(keycloakAdminService).deleteUserIfExists(studentId.toString());

    assertThat(studentRepository.existsById(studentId)).isFalse();
    assertThat(countRows("persons", "email", "ivan@test.com")).isZero();
    assertThat(countRows("enrollment", "student_id", studentId)).isZero();
    assertThat(countRows("lecture_attendance", "student_id", studentId)).isZero();
    assertThat(countRows("lecture_self_attendance", "student_id", studentId)).isZero();
    assertThat(countRows("university_requests", "student_id", studentId)).isZero();

    // Данные другого студента не тронуты
    assertThat(studentRepository.existsById(otherStudentId)).isTrue();
    assertThat(countRows("enrollment", "student_id", otherStudentId)).isOne();
    assertThat(countRows("lecture_attendance", "student_id", otherStudentId)).isOne();
    assertThat(countRows("lecture_self_attendance", "student_id", otherStudentId)).isOne();
    assertThat(countRows("university_requests", "student_id", otherStudentId)).isOne();

    // Отзыв остаётся для разбора, но без автора
    assertThat(feedbackRepository.findAll()).hasSize(2)
        .filteredOn(feedback -> feedback.getUserId() == null).singleElement()
        .satisfies(feedback -> {
          assertThat(feedback.getUsername()).isNull();
          assertThat(feedback.getCreatedBy()).isNull();
          assertThat(feedback.getMessage()).isEqualTo("Не работает расписание");
        });

    AuditEvent event = auditEventRepository.findAll().stream()
        .filter(e -> e.getAction() == AuditAction.DELETE).findFirst().orElseThrow();
    assertThat(event.getEntityType()).isEqualTo(AuditEntityType.STUDENT);
    assertThat(event.getEntityId()).isEqualTo(studentId);
    assertThat(event.getActorId()).isEqualTo(studentId);
    assertThat(event.getDetails()).containsEntry("selfDeleted", true).containsEntry("enrollmentsDeleted", 1);

    // Старый access-токен ещё не истёк, но профиля за ним уже нет
    mockMvc.perform(get("/api/v1/students/me").header(HttpHeaders.AUTHORIZATION, bearer(STUDENT_TOKEN)))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldDeleteTeacherAccountKeepingGroupSchedule() throws Exception {
    UUID pairId = createPair();
    UUID lectureId = createLecture(teacherId);
    createFeedback(teacherId, "teacher");

    deleteMe(TEACHER_TOKEN).andExpect(status().isNoContent());

    verify(keycloakAdminService).deleteUserIfExists(teacherId.toString());

    assertThat(teacherRepository.existsById(teacherId)).isFalse();
    assertThat(countRows("persons", "email", "teacher@test.com")).isZero();

    // Пара, лекция и предмет остаются, только без преподавателя; группа по-прежнему в расписании
    assertThat(jdbcTemplate.queryForObject(
        "SELECT teacher_id FROM pairs WHERE id = ?", UUID.class, pairId)).isNull();
    assertThat(countRows("pair_groups", "pair_id", pairId)).isOne();
    assertThat(jdbcTemplate.queryForObject(
        "SELECT teacher_id FROM lectures WHERE id = ?", UUID.class, lectureId)).isNull();
    assertThat(jdbcTemplate.queryForObject(
        "SELECT teacher_id FROM courses WHERE id = ?", UUID.class, course.getId())).isNull();
    // Bulk-update минует JPA auditing — updated_by проставлен явно
    assertThat(jdbcTemplate.queryForObject(
        "SELECT updated_by FROM pairs WHERE id = ?", UUID.class, pairId)).isEqualTo(teacherId);

    assertThat(feedbackRepository.findAll()).singleElement()
        .satisfies(feedback -> assertThat(feedback.getUserId()).isNull());

    AuditEvent event = auditEventRepository.findAll().stream()
        .filter(e -> e.getAction() == AuditAction.DELETE).findFirst().orElseThrow();
    assertThat(event.getEntityType()).isEqualTo(AuditEntityType.TEACHER);
    assertThat(event.getDetails()).containsEntry("pairsDetached", 1).containsEntry("lecturesDetached", 1)
        .containsEntry("coursesDetached", 1);
  }

  @Test
  void shouldKeepDataWhenKeycloakIsUnavailable() throws Exception {
    enroll(studentId);
    createFeedback(studentId, "ivan");
    when(keycloakAdminService.deleteUserIfExists(studentId.toString()))
        .thenThrow(new RuntimeException("Connection refused"));

    deleteMe(STUDENT_TOKEN)
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value(
            "Не удалось удалить аккаунт: сервер авторизации недоступен. Данные не изменены, попробуйте позже"))
        .andExpect(jsonPath("$.requestId").isString());

    // Транзакция откатилась целиком: удаление можно просто повторить
    assertThat(studentRepository.existsById(studentId)).isTrue();
    assertThat(countRows("enrollment", "student_id", studentId)).isOne();
    assertThat(feedbackRepository.findAll()).singleElement()
        .satisfies(feedback -> assertThat(feedback.getUserId()).isEqualTo(studentId));
    assertThat(auditEventRepository.findAll()).noneMatch(e -> e.getAction() == AuditAction.DELETE);
  }

  @Test
  void shouldTreatRepeatedDeletionAsSuccess() throws Exception {
    deleteMe(STUDENT_TOKEN).andExpect(status().isNoContent());
    when(keycloakAdminService.deleteUserIfExists(studentId.toString())).thenReturn(false);

    // Например, первый ответ не дошёл до приложения, и оно повторило запрос
    deleteMe(STUDENT_TOKEN).andExpect(status().isNoContent());

    assertThat(auditEventRepository.findAll()).filteredOn(e -> e.getAction() == AuditAction.DELETE).hasSize(1);
  }

  @Test
  void shouldRejectAdminAndAnonymous() throws Exception {
    deleteMe(ADMIN_TOKEN).andExpect(status().isForbidden());
    mockMvc.perform(delete("/api/v1/users/me")).andExpect(status().isUnauthorized());

    verify(keycloakAdminService, never()).deleteUserIfExists(anyString());
    assertThat(studentRepository.existsById(studentId)).isTrue();
  }

  private ResultActions deleteMe(String token) throws Exception {
    return mockMvc.perform(delete("/api/v1/users/me").header(HttpHeaders.AUTHORIZATION, bearer(token)));
  }

  private UUID createStudent(String username) {
    Student student = new Student();
    student.setPerson(person(username));
    student.setUniversity(university);
    student.setGroup(group);
    return studentRepository.save(student).getId();
  }

  private UUID createTeacher(String username) {
    Teacher teacher = new Teacher();
    teacher.setPerson(person(username));
    teacher.setRegistered(true);
    return teacherRepository.save(teacher).getId();
  }

  private static Person person(String username) {
    Person person = new Person();
    person.setUsername(username);
    person.setFirstname(username);
    person.setLastname(username);
    person.setFullname(username);
    person.setEmail(username + "@test.com");
    return person;
  }

  private UUID createPair() {
    WeekScheduleCycle cycle = new WeekScheduleCycle();
    cycle.setSemester(semester);
    cycle = weekScheduleCycleRepository.save(cycle);

    Pair pair = new Pair();
    pair.setWeekScheduleCycle(cycle);
    pair.setDayOfWeek(DayOfWeek.MONDAY);
    pair.setWeekParity(WeekParity.ODD);
    pair.setPairNumber(1);
    pair.setStartTime(LocalTime.of(9, 0));
    pair.setEndTime(LocalTime.of(10, 30));
    pair.setCourse(course);
    pair.setTeacher(teacherRepository.getReferenceById(teacherId));
    pair.setGroups(new HashSet<>(Set.of(group)));
    return pairRepository.save(pair).getId();
  }

  private UUID createLecture(UUID lectureTeacherId) {
    Lecture lecture = new Lecture();
    lecture.setTitle("Занятие");
    lecture.setScheduledTime(LocalDateTime.now().minusDays(1));
    lecture.setCourse(course);
    if (lectureTeacherId != null) {
      lecture.setTeacher(teacherRepository.getReferenceById(lectureTeacherId));
    }
    lecture.setGroups(new HashSet<>(Set.of(group)));
    return lectureRepository.save(lecture).getId();
  }

  private void enroll(UUID id) {
    Enrollment enrollment = new Enrollment();
    enrollment.setStudentId(id);
    enrollment.setCourseId(course.getId());
    enrollment.setEnrolledAt(LocalDateTime.now());
    enrollment.setStatus(EnrollmentStatus.ACTIVE);
    enrollmentRepository.save(enrollment);
  }

  private void teacherMark(UUID id, UUID lectureId) {
    LectureAttendance mark = new LectureAttendance();
    mark.setStudentId(id);
    mark.setLectureId(lectureId);
    mark.setAttended(true);
    attendanceRepository.save(mark);
  }

  private void selfMark(UUID id, UUID lectureId) {
    LectureSelfAttendance mark = new LectureSelfAttendance();
    mark.setStudentId(id);
    mark.setLectureId(lectureId);
    mark.setAttended(true);
    selfAttendanceRepository.save(mark);
  }

  private void requestUniversity(UUID id) {
    UniversityRequest request = new UniversityRequest();
    request.setStudent(studentRepository.getReferenceById(id));
    request.setName("Новый вуз");
    universityRequestRepository.save(request);
  }

  private void createFeedback(UUID userId, String username) {
    Feedback feedback = new Feedback();
    feedback.setUserId(userId);
    feedback.setRole("STUDENT");
    feedback.setUsername(username);
    feedback.setType(FeedbackType.BUG);
    feedback.setMessage("Не работает расписание");
    feedback.setPlatform(FeedbackPlatform.IOS);
    feedbackRepository.save(feedback);
    // Вне запроса auditing не знает автора — проставляем created_by как при настоящей отправке
    jdbcTemplate.update("UPDATE feedback SET created_by = ? WHERE id = ?", userId, feedback.getId());
  }

  private int countRows(String table, String column, Object value) {
    Integer count = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM " + table + " WHERE " + column + " = ?", Integer.class, value);
    return count == null ? 0 : count;
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
