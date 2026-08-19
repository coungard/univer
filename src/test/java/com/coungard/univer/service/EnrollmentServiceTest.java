package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.EducationForm;
import com.coungard.univer.dto.EnrollmentDto;
import com.coungard.univer.dto.EnrollmentStatus;
import com.coungard.univer.dto.SemesterType;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Department;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Program;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.DepartmentRepository;
import com.coungard.univer.repository.EnrollmentRepository;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.GroupRepository;
import com.coungard.univer.repository.ProgramRepository;
import com.coungard.univer.repository.SemesterRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.StudyYearRepository;
import com.coungard.univer.repository.UniversityRepository;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = UniverApplication.class)
@Testcontainers
class EnrollmentServiceTest {

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
  private EnrollmentRepository enrollmentRepository;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private UniversityRepository universityRepository;

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private SemesterRepository semesterRepository;

  @Autowired
  private StudyYearRepository studyYearRepository;

  @Autowired
  private ProgramRepository programRepository;

  private UUID universityId;
  private UUID courseId;
  private UUID groupId;
  private UUID studentInGroupId;
  private UUID studentOutOfGroupId;

  @BeforeEach
  void setUp() {
    enrollmentRepository.deleteAll();
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
    semester.setStartDate(LocalDate.of(2026, 9, 1));
    UUID semesterId = semesterRepository.save(semester).getId();

    Group group = new Group();
    group.setSemester(semesterRepository.getReferenceById(semesterId));
    group.setName("У532 КСиТ");
    groupId = groupRepository.save(group).getId();

    studentInGroupId = createStudent("ivanov", "Ivan", "Ivanov", groupRepository.getReferenceById(groupId));
    studentOutOfGroupId = createStudent("petrov", "Petr", "Petrov", null);
  }

  @Test
  void shouldEnrollStudentOnCourse() {
    // When
    EnrollmentDto enrolled = enrollmentService.enroll(enrollDto(studentOutOfGroupId, courseId));

    // Then
    assertThat(enrolled.studentId()).isEqualTo(studentOutOfGroupId);
    assertThat(enrolled.courseId()).isEqualTo(courseId);
    assertThat(enrolled.status()).isEqualTo(EnrollmentStatus.ACTIVE);
    assertThat(enrolled.enrolledAt()).isNotNull();
  }

  @Test
  void shouldThrowExceptionWhenEnrollingSameStudentTwice() {
    // Given
    enrollmentService.enroll(enrollDto(studentOutOfGroupId, courseId));

    // When & Then
    assertThatThrownBy(() -> enrollmentService.enroll(enrollDto(studentOutOfGroupId, courseId)))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowExceptionWhenEnrollingNonExistentStudent() {
    assertThatThrownBy(() -> enrollmentService.enroll(enrollDto(UUID.randomUUID(), courseId)))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenEnrollingNonExistentCourse() {
    assertThatThrownBy(() -> enrollmentService.enroll(enrollDto(studentOutOfGroupId, UUID.randomUUID())))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldGetEnrollmentByStudentAndCourse() {
    // Given
    enrollmentService.enroll(enrollDto(studentOutOfGroupId, courseId));

    // When
    EnrollmentDto found = enrollmentService.getEnrollment(studentOutOfGroupId, courseId);

    // Then
    assertThat(found.studentId()).isEqualTo(studentOutOfGroupId);
    assertThat(found.courseId()).isEqualTo(courseId);
  }

  @Test
  void shouldThrowExceptionWhenEnrollmentNotFound() {
    assertThatThrownBy(() -> enrollmentService.getEnrollment(studentOutOfGroupId, courseId))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldGetEnrollmentsByStudent() {
    // Given
    enrollmentService.enroll(enrollDto(studentOutOfGroupId, courseId));
    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<EnrollmentDto> result = enrollmentService.getEnrollmentsByStudent(studentOutOfGroupId, pageable);

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldGetEnrollmentsByCourse() {
    // Given
    enrollmentService.enroll(enrollDto(studentInGroupId, courseId));
    enrollmentService.enroll(enrollDto(studentOutOfGroupId, courseId));
    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<EnrollmentDto> result = enrollmentService.getEnrollmentsByCourse(courseId, pageable);

    // Then
    assertThat(result.getContent()).hasSize(2);
  }

  @Test
  void shouldUpdateEnrollmentStatus() {
    // Given
    enrollmentService.enroll(enrollDto(studentOutOfGroupId, courseId));
    EnrollmentDto statusUpdate = EnrollmentDto.builder().status(EnrollmentStatus.COMPLETED).build();

    // When
    EnrollmentDto updated = enrollmentService.updateStatus(studentOutOfGroupId, courseId, statusUpdate);

    // Then
    assertThat(updated.status()).isEqualTo(EnrollmentStatus.COMPLETED);
  }

  @Test
  void shouldThrowExceptionWhenUpdatingStatusOfNonExistentEnrollment() {
    EnrollmentDto statusUpdate = EnrollmentDto.builder().status(EnrollmentStatus.DROPPED).build();

    assertThatThrownBy(() -> enrollmentService.updateStatus(studentOutOfGroupId, courseId, statusUpdate))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldUnenrollStudent() {
    // Given
    enrollmentService.enroll(enrollDto(studentOutOfGroupId, courseId));

    // When
    enrollmentService.unenroll(studentOutOfGroupId, courseId);

    // Then
    assertThat(enrollmentRepository.existsByStudentIdAndCourseId(studentOutOfGroupId, courseId)).isFalse();
  }

  @Test
  void shouldThrowExceptionWhenUnenrollingNonExistentEnrollment() {
    assertThatThrownBy(() -> enrollmentService.unenroll(studentOutOfGroupId, courseId))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldEnrollWholeGroupSkippingAlreadyEnrolledStudents() {
    // Given: студент вне группы уже зачислен, а студент из группы — ещё нет
    UUID studentInGroup2Id = createStudent("sidorov", "Sidor", "Sidorov", groupRepository.getReferenceById(groupId));

    // When
    List<EnrollmentDto> created = enrollmentService.enrollGroup(groupId, courseId);

    // Then
    assertThat(created).extracting(EnrollmentDto::studentId)
        .containsExactlyInAnyOrder(studentInGroupId, studentInGroup2Id);
    assertThat(enrollmentService.getEnrollmentsByCourse(courseId, PageRequest.of(0, 10)).getContent()).hasSize(2);
  }

  @Test
  void shouldSkipAlreadyEnrolledStudentsWhenEnrollingGroupAgain() {
    // Given
    enrollmentService.enrollGroup(groupId, courseId);

    // When: повторный вызов не должен создавать дубликаты и не должен падать
    List<EnrollmentDto> secondCall = enrollmentService.enrollGroup(groupId, courseId);

    // Then
    assertThat(secondCall).isEmpty();
    assertThat(enrollmentService.getEnrollmentsByCourse(courseId, PageRequest.of(0, 10)).getContent()).hasSize(1);
  }

  @Test
  void shouldThrowExceptionWhenEnrollingNonExistentGroup() {
    assertThatThrownBy(() -> enrollmentService.enrollGroup(UUID.randomUUID(), courseId))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  // === Вспомогательные методы ===

  private EnrollmentDto enrollDto(UUID studentId, UUID courseId) {
    return EnrollmentDto.builder()
        .studentId(studentId)
        .courseId(courseId)
        .build();
  }

  private UUID createStudent(String username, String firstName, String lastName, Group group) {
    Student student = new Student();

    Person person = new Person();
    person.setUsername(username);
    person.setFirstname(firstName);
    person.setLastname(lastName);
    person.setEmail((firstName + "." + lastName + "@test.com").toLowerCase());

    student.setPerson(person);
    student.setEnrollmentDate(LocalDate.of(2026, 9, 1));
    University university = new University();
    university.setId(universityId);
    student.setUniversity(university);
    student.setGroup(group);

    return studentRepository.save(student).getId();
  }
}
