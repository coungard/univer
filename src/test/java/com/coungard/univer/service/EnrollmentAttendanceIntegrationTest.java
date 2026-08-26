package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.AttendanceStatsDto;
import com.coungard.univer.dto.EducationForm;
import com.coungard.univer.dto.EnrollmentDto;
import com.coungard.univer.dto.LectureAttendanceDto;
import com.coungard.univer.dto.LectureDto;
import com.coungard.univer.dto.SemesterType;
import com.coungard.univer.dto.WeekParity;
import com.coungard.univer.dto.request.GenerateLectureRequest;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Department;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Pair;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Program;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.entity.University;
import com.coungard.univer.entity.WeekScheduleCycle;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.DepartmentRepository;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.GroupRepository;
import com.coungard.univer.repository.PairRepository;
import com.coungard.univer.repository.ProgramRepository;
import com.coungard.univer.repository.SemesterRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.StudyYearRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.repository.WeekScheduleCycleRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Сквозной сценарий Этапа 5: зачисление всей группы на курс → генерация лекции из шаблона
 * {@code Pair} → отметка посещаемости → выборка статистики.
 */
@SpringBootTest(classes = UniverApplication.class)
@Testcontainers
class EnrollmentAttendanceIntegrationTest {

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
  private EnrollmentService enrollmentService;

  @Autowired
  private LectureAttendanceService attendanceService;

  @Autowired
  private LectureService lectureService;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private SemesterRepository semesterRepository;

  @Autowired
  private StudyYearRepository studyYearRepository;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private UniversityRepository universityRepository;

  @Autowired
  private WeekScheduleCycleRepository weekScheduleCycleRepository;

  @Autowired
  private PairRepository pairRepository;

  private static final LocalDate SEMESTER_START = LocalDate.of(2026, 9, 1);
  private static final LocalDate FIRST_MONDAY_ODD_WEEK = LocalDate.of(2026, 9, 7);

  private UUID universityId;
  private UUID courseId;
  private UUID groupId;
  private UUID pairId;
  private UUID student1Id;
  private UUID student2Id;

  @BeforeEach
  void setUp() {
    pairRepository.deleteAll();
    weekScheduleCycleRepository.deleteAll();
    studentRepository.deleteAll();
    groupRepository.deleteAll();
    semesterRepository.deleteAll();
    studyYearRepository.deleteAll();
    courseRepository.deleteAll();
    departmentRepository.deleteAll();
    programRepository.deleteAll();
    facultyRepository.deleteAll();
    universityRepository.deleteAll();

    University university = new University();
    university.setName("Test University");
    universityId = universityRepository.save(university).getId();

    Faculty faculty = Faculty.builder()
        .name("Faculty of Computer Science")
        .university(universityRepository.getReferenceById(universityId))
        .build();
    UUID facultyId = facultyRepository.save(faculty).getId();

    Department department = new Department();
    department.setName("Department of Algorithms");
    department.setFaculty(facultyRepository.getReferenceById(facultyId));
    UUID departmentId = departmentRepository.save(department).getId();

    Course course = new Course();
    course.setTitle("Algorithms and Data Structures");
    course.setDepartment(departmentRepository.getReferenceById(departmentId));
    courseId = courseRepository.save(course).getId();

    Program program = new Program();
    program.setFacultyId(facultyId);
    program.setCode("09.03.04");
    program.setName("Software Engineering");
    program.setEducationLevel("Bachelor");
    program.setEducationForm(EducationForm.FULL_TIME);
    program.setDurationOfStudy(Period.ofYears(4));
    UUID programId = programRepository.save(program).getId();

    StudyYear studyYear = new StudyYear();
    studyYear.setProgram(programRepository.getReferenceById(programId));
    studyYear.setYearNumber(1);
    UUID studyYearId = studyYearRepository.save(studyYear).getId();

    Semester semester = new Semester();
    semester.setStudyYear(studyYearRepository.getReferenceById(studyYearId));
    semester.setType(SemesterType.AUTUMN);
    semester.setStartDate(SEMESTER_START);
    semester.setEndDate(SEMESTER_START.plusMonths(4));
    UUID semesterId = semesterRepository.save(semester).getId();

    WeekScheduleCycle cycle = new WeekScheduleCycle();
    cycle.setSemester(semesterRepository.getReferenceById(semesterId));
    UUID cycleId = weekScheduleCycleRepository.save(cycle).getId();

    Group group = new Group();
    group.setSemester(semesterRepository.getReferenceById(semesterId));
    group.setName("У532 КСиТ");
    groupId = groupRepository.save(group).getId();

    Pair pair = new Pair();
    pair.setWeekScheduleCycle(weekScheduleCycleRepository.getReferenceById(cycleId));
    pair.setDayOfWeek(DayOfWeek.MONDAY);
    pair.setWeekParity(WeekParity.ODD);
    pair.setPairNumber(1);
    pair.setStartTime(LocalTime.of(8, 0));
    pair.setEndTime(LocalTime.of(9, 30));
    pair.setCourse(courseRepository.getReferenceById(courseId));
    pair.setGroups(Set.of(groupRepository.findById(groupId).orElseThrow()));
    pairId = pairRepository.save(pair).getId();

    student1Id = createStudent("ivanov", "Ivan", "Ivanov", groupRepository.getReferenceById(groupId));
    student2Id = createStudent("petrov", "Petr", "Petrov", groupRepository.getReferenceById(groupId));
  }

  @Test
  void shouldEnrollGroupGenerateLectureMarkAttendanceAndComputeStats() {
    // 1. Зачислить всю группу на курс
    List<EnrollmentDto> enrolled = enrollmentService.enrollGroup(groupId, courseId);
    assertThat(enrolled).extracting(EnrollmentDto::studentId)
        .containsExactlyInAnyOrder(student1Id, student2Id);

    // 2. Сгенерировать лекцию из шаблона Pair на первый понедельник семестра
    LectureDto lecture = lectureService.generateFromPair(new GenerateLectureRequest(pairId, FIRST_MONDAY_ODD_WEEK));
    assertThat(lecture.groupIds()).containsExactly(groupId);

    // 3. Отметить посещаемость: student1 присутствовал, student2 — нет
    attendanceService.markAttendance(LectureAttendanceDto.builder()
        .studentId(student1Id).lectureId(lecture.id()).attended(true).build());
    attendanceService.markAttendance(LectureAttendanceDto.builder()
        .studentId(student2Id).lectureId(lecture.id()).attended(false).build());

    // 4. Статистика по лекции: 2 отмечено, 1 присутствовал
    AttendanceStatsDto lectureStats = attendanceService.getLectureStats(lecture.id());
    assertThat(lectureStats.totalMarked()).isEqualTo(2);
    assertThat(lectureStats.attendedCount()).isEqualTo(1);
    assertThat(lectureStats.attendanceRate()).isEqualTo(0.5, offset(0.0001));

    // 5. Статистика по студенту в рамках курса
    AttendanceStatsDto student1Stats = attendanceService.getStudentCourseStats(student1Id, courseId);
    assertThat(student1Stats.totalMarked()).isEqualTo(1);
    assertThat(student1Stats.attendedCount()).isEqualTo(1);

    AttendanceStatsDto student2Stats = attendanceService.getStudentCourseStats(student2Id, courseId);
    assertThat(student2Stats.totalMarked()).isEqualTo(1);
    assertThat(student2Stats.attendedCount()).isEqualTo(0);

    // 6. Список посещаемости по лекции доступен через пагинацию
    assertThat(attendanceService.getAttendanceByLecture(lecture.id(), PageRequest.of(0, 10)).getContent())
        .hasSize(2);
  }

  // === Вспомогательные методы ===

  private UUID createStudent(String username, String firstName, String lastName, Group group) {
    Student student = new Student();

    Person person = new Person();
    person.setUsername(username);
    person.setFirstname(firstName);
    person.setLastname(lastName);
    person.setEmail((firstName + "." + lastName + "@test.com").toLowerCase());

    student.setPerson(person);
    student.setEnrollmentDate(SEMESTER_START);
    University university = new University();
    university.setId(universityId);
    student.setUniversity(university);
    student.setGroup(group);

    return studentRepository.save(student).getId();
  }
}
