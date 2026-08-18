package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.CourseDto;
import com.coungard.univer.entity.Department;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Teacher;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.DepartmentRepository;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.TeacherRepository;
import com.coungard.univer.repository.UniversityRepository;
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
class CourseServiceTest {

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
  private CourseService courseService;

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private UniversityRepository universityRepository;

  @Autowired
  private TeacherRepository teacherRepository;

  private UUID departmentId;
  private UUID teacherId;

  @BeforeEach
  void setUp() {
    courseRepository.deleteAll();
    teacherRepository.deleteAll();
    departmentRepository.deleteAll();
    facultyRepository.deleteAll();
    universityRepository.deleteAll();

    University university = new University();
    university.setName("Test University");
    UUID universityId = universityRepository.save(university).getId();

    Faculty faculty = Faculty.builder()
        .name("Faculty of Computer Science")
        .university(universityRepository.getReferenceById(universityId))
        .build();
    UUID facultyId = facultyRepository.save(faculty).getId();

    Department department = new Department();
    department.setName("Department of Algorithms");
    department.setFaculty(facultyRepository.getReferenceById(facultyId));
    departmentId = departmentRepository.save(department).getId();

    Teacher teacher = new Teacher();
    teacher.setFaculty(facultyRepository.getReferenceById(facultyId));
    teacher.setPosition("Professor");
    teacher.setRegistered(false);

    Person person = new Person();
    person.setUsername("teacher1");
    person.setFirstname("Ada");
    person.setLastname("Lovelace");
    person.setFullname("Ada Lovelace");
    person.setEmail("ada@example.com");
    teacher.setPerson(person);

    teacherId = teacherRepository.save(teacher).getId();
  }

  @Test
  void shouldCreateAndRetrieveCourse() {
    // Given
    CourseDto dto = createDto("Algorithms 101");

    // When
    CourseDto created = courseService.createCourse(dto);
    CourseDto found = courseService.getCourseById(created.id());

    // Then
    assertThat(found).isNotNull();
    assertThat(found.title()).isEqualTo("Algorithms 101");
    assertThat(found.departmentId()).isEqualTo(departmentId);
    assertThat(found.teacherId()).isEqualTo(teacherId);
  }

  @Test
  void shouldCreateCourseWithoutTeacher() {
    // Given
    CourseDto dto = CourseDto.builder()
        .title("Algorithms 101")
        .description("Intro to algorithms")
        .departmentId(departmentId)
        .build();

    // When
    CourseDto created = courseService.createCourse(dto);

    // Then
    assertThat(created.teacherId()).isNull();
  }

  @Test
  void shouldThrowExceptionWhenCreatingCourseWithNonExistentDepartment() {
    CourseDto dto = CourseDto.builder()
        .title("Algorithms 101")
        .departmentId(UUID.randomUUID())
        .build();

    assertThatThrownBy(() -> courseService.createCourse(dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenCreatingCourseWithNonExistentTeacher() {
    CourseDto dto = CourseDto.builder()
        .title("Algorithms 101")
        .departmentId(departmentId)
        .teacherId(UUID.randomUUID())
        .build();

    assertThatThrownBy(() -> courseService.createCourse(dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenCourseNotFound() {
    UUID randomId = UUID.randomUUID();
    assertThatThrownBy(() -> courseService.getCourseById(randomId))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldGetCourses() {
    // Given
    courseService.createCourse(createDto("Algorithms 101"));
    courseService.createCourse(createDto("Databases 101"));

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<CourseDto> result = courseService.getCourses(pageable);

    // Then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent())
        .extracting(CourseDto::title)
        .containsExactlyInAnyOrder("Algorithms 101", "Databases 101");
  }

  @Test
  void shouldGetCoursesByDepartment() {
    // Given
    courseService.createCourse(createDto("Algorithms 101"));

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<CourseDto> result = courseService.getCoursesByDepartment(departmentId, pageable);

    // Then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).title()).isEqualTo("Algorithms 101");
  }

  @Test
  void shouldUpdateCourse() {
    // Given
    CourseDto original = courseService.createCourse(createDto("Algorithms 101"));

    CourseDto updateDto = CourseDto.builder()
        .title("Advanced Algorithms")
        .description("Updated description")
        .departmentId(departmentId)
        .build();

    // When
    CourseDto updated = courseService.updateCourse(original.id(), updateDto);

    // Then
    assertThat(updated.title()).isEqualTo("Advanced Algorithms");
    assertThat(updated.description()).isEqualTo("Updated description");
    assertThat(updated.teacherId()).isNull();
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentCourse() {
    CourseDto dto = createDto("Algorithms 101");

    assertThatThrownBy(() -> courseService.updateCourse(UUID.randomUUID(), dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldDeleteCourseById() {
    // Given
    CourseDto course = courseService.createCourse(createDto("Algorithms 101"));

    // When
    courseService.deleteCourse(course.id());

    // Then
    assertThat(courseRepository.findById(course.id())).isEmpty();
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentCourse() {
    assertThatThrownBy(() -> courseService.deleteCourse(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  // === Вспомогательные методы ===

  private CourseDto createDto(String title) {
    return CourseDto.builder()
        .title(title)
        .description("Description of " + title)
        .departmentId(departmentId)
        .teacherId(teacherId)
        .build();
  }
}
