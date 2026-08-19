package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.AttendanceStatsDto;
import com.coungard.univer.dto.EnrollmentStatus;
import com.coungard.univer.dto.LectureAttendanceDto;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Department;
import com.coungard.univer.entity.Enrollment;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Lecture;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.DepartmentRepository;
import com.coungard.univer.repository.EnrollmentRepository;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.LectureAttendanceRepository;
import com.coungard.univer.repository.LectureRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.UniversityRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
class LectureAttendanceServiceTest {

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
  private LectureAttendanceService attendanceService;

  @Autowired
  private LectureAttendanceRepository attendanceRepository;

  @Autowired
  private EnrollmentRepository enrollmentRepository;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private LectureRepository lectureRepository;

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private UniversityRepository universityRepository;

  private UUID universityId;
  private UUID courseId;
  private UUID lectureId;
  private UUID enrolledStudentId;
  private UUID droppedStudentId;
  private UUID notEnrolledStudentId;

  @BeforeEach
  void setUp() {
    attendanceRepository.deleteAll();
    enrollmentRepository.deleteAll();
    lectureRepository.deleteAll();
    studentRepository.deleteAll();
    courseRepository.deleteAll();
    departmentRepository.deleteAll();
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

    Lecture lecture = new Lecture();
    lecture.setTitle("Introduction");
    lecture.setScheduledTime(LocalDateTime.of(2026, 9, 1, 8, 0));
    lecture.setCourse(courseRepository.getReferenceById(courseId));
    lectureId = lectureRepository.save(lecture).getId();

    enrolledStudentId = createStudent("ivanov", "Ivan", "Ivanov");
    droppedStudentId = createStudent("petrov", "Petr", "Petrov");
    notEnrolledStudentId = createStudent("sidorov", "Sidor", "Sidorov");

    enroll(enrolledStudentId, courseId, EnrollmentStatus.ACTIVE);
    enroll(droppedStudentId, courseId, EnrollmentStatus.DROPPED);
  }

  @Test
  void shouldMarkAttendanceForEnrolledStudent() {
    // When
    LectureAttendanceDto marked = attendanceService.markAttendance(attendanceDto(enrolledStudentId, lectureId, true));

    // Then
    assertThat(marked.studentId()).isEqualTo(enrolledStudentId);
    assertThat(marked.lectureId()).isEqualTo(lectureId);
    assertThat(marked.attended()).isTrue();
  }

  @Test
  void shouldThrowExceptionWhenStudentIsNotEnrolledOnCourse() {
    assertThatThrownBy(() -> attendanceService.markAttendance(attendanceDto(notEnrolledStudentId, lectureId, true)))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowExceptionWhenEnrollmentIsDropped() {
    assertThatThrownBy(() -> attendanceService.markAttendance(attendanceDto(droppedStudentId, lectureId, true)))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowExceptionWhenMarkingAttendanceForNonExistentStudent() {
    assertThatThrownBy(() -> attendanceService.markAttendance(attendanceDto(UUID.randomUUID(), lectureId, true)))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenMarkingAttendanceForNonExistentLecture() {
    assertThatThrownBy(
        () -> attendanceService.markAttendance(attendanceDto(enrolledStudentId, UUID.randomUUID(), true)))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldUpdateExistingAttendanceRecordInsteadOfDuplicating() {
    // Given
    attendanceService.markAttendance(attendanceDto(enrolledStudentId, lectureId, false));

    // When
    LectureAttendanceDto updated = attendanceService.markAttendance(attendanceDto(enrolledStudentId, lectureId, true));

    // Then
    assertThat(updated.attended()).isTrue();
    assertThat(attendanceRepository.countByLectureId(lectureId)).isEqualTo(1);
  }

  @Test
  void shouldGetAttendanceByStudent() {
    // Given
    attendanceService.markAttendance(attendanceDto(enrolledStudentId, lectureId, true));
    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<LectureAttendanceDto> result = attendanceService.getAttendanceByStudent(enrolledStudentId, pageable);

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldGetAttendanceByLecture() {
    // Given
    attendanceService.markAttendance(attendanceDto(enrolledStudentId, lectureId, true));
    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<LectureAttendanceDto> result = attendanceService.getAttendanceByLecture(lectureId, pageable);

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldComputeLectureStats() {
    // Given: один студент присутствовал, для второго нужна ещё одна ACTIVE-запись
    UUID secondActiveStudentId = createStudent("kuznetsov", "Kuzma", "Kuznetsov");
    enroll(secondActiveStudentId, courseId, EnrollmentStatus.ACTIVE);

    attendanceService.markAttendance(attendanceDto(enrolledStudentId, lectureId, true));
    attendanceService.markAttendance(attendanceDto(secondActiveStudentId, lectureId, false));

    // When
    AttendanceStatsDto stats = attendanceService.getLectureStats(lectureId);

    // Then
    assertThat(stats.totalMarked()).isEqualTo(2);
    assertThat(stats.attendedCount()).isEqualTo(1);
    assertThat(stats.attendanceRate()).isEqualTo(0.5, offset(0.0001));
  }

  @Test
  void shouldThrowExceptionWhenGettingStatsForNonExistentLecture() {
    assertThatThrownBy(() -> attendanceService.getLectureStats(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldComputeStudentCourseStats() {
    // Given
    attendanceService.markAttendance(attendanceDto(enrolledStudentId, lectureId, true));

    // When
    AttendanceStatsDto stats = attendanceService.getStudentCourseStats(enrolledStudentId, courseId);

    // Then
    assertThat(stats.totalMarked()).isEqualTo(1);
    assertThat(stats.attendedCount()).isEqualTo(1);
    assertThat(stats.attendanceRate()).isEqualTo(1.0, offset(0.0001));
  }

  @Test
  void shouldThrowExceptionWhenGettingStudentCourseStatsForNonExistentStudent() {
    assertThatThrownBy(() -> attendanceService.getStudentCourseStats(UUID.randomUUID(), courseId))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  // === Вспомогательные методы ===

  private LectureAttendanceDto attendanceDto(UUID studentId, UUID lectureId, boolean attended) {
    return LectureAttendanceDto.builder()
        .studentId(studentId)
        .lectureId(lectureId)
        .attended(attended)
        .build();
  }

  private void enroll(UUID studentId, UUID courseId, EnrollmentStatus status) {
    Enrollment enrollment = new Enrollment();
    enrollment.setStudentId(studentId);
    enrollment.setCourseId(courseId);
    enrollment.setEnrolledAt(LocalDateTime.now());
    enrollment.setStatus(status);
    enrollmentRepository.save(enrollment);
  }

  private UUID createStudent(String username, String firstName, String lastName) {
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

    return studentRepository.save(student).getId();
  }
}
