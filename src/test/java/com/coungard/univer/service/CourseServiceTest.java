package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.TestRegions;
import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.CourseDto;
import com.coungard.univer.entity.Department;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Teacher;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ConflictException;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.DepartmentRepository;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.TeacherRepository;
import com.coungard.univer.repository.RegionRepository;
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
  private RegionRepository regionRepository;

  @Autowired
  private TeacherRepository teacherRepository;

  private UUID universityId;
  private UUID facultyId;
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
    university.setRegion(TestRegions.create(regionRepository));
    universityId = universityRepository.save(university).getId();

    Faculty faculty = Faculty.builder()
        .name("Faculty of Computer Science")
        .university(universityRepository.getReferenceById(universityId))
        .build();
    facultyId = facultyRepository.save(faculty).getId();

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

  // === Привязка к факультету (issue #97) ===

  @Test
  void shouldCreateCourseBoundToFacultyWithoutDepartment() {
    // When
    CourseDto created = courseService.createCourse(
        CourseDto.builder().title("  Физика ").facultyId(facultyId).build());

    // Then
    assertThat(created.title()).isEqualTo("Физика");
    assertThat(created.facultyId()).isEqualTo(facultyId);
    assertThat(created.departmentId()).isNull();
    assertThat(created.createdByStudentId()).isNull();
  }

  @Test
  void shouldGetCoursesOfFacultyBoundDirectlyAndThroughDepartmentSortedByTitle() {
    // Given: один предмет привязан через кафедру, второй — напрямую к факультету
    CourseDto throughDepartment = courseService.createCourse(createDto("Физика"));
    CourseDto direct = courseService.createCourse(
        CourseDto.builder().title("Алгебра").facultyId(facultyId).build());
    // ...и по одному чужому: другого факультета и вовсе без привязки
    UUID otherFacultyId = createFaculty("Faculty of Physics");
    courseService.createCourse(CourseDto.builder().title("Механика").facultyId(otherFacultyId).build());
    courseService.createCourse(CourseDto.builder().title("Философия").build());

    // When
    Page<CourseDto> page = courseService.getCoursesByFaculty(facultyId, PageRequest.of(0, 10));

    // Then
    assertThat(page.getContent()).extracting(CourseDto::id)
        .containsExactly(direct.id(), throughDepartment.id());
    assertThat(page.getTotalElements()).isEqualTo(2);
    assertThat(courseService.getCoursesByFaculty(UUID.randomUUID(), PageRequest.of(0, 10)).getContent())
        .isEmpty();
  }

  @Test
  void shouldRejectDuplicateTitleWithinFacultyIgnoringCaseAndSpaces() {
    // Given: предмет факультета, привязанный через кафедру
    CourseDto existing = courseService.createCourse(createDto("Математический анализ"));

    // When / Then
    assertThatThrownBy(() -> courseService.createCourse(
        CourseDto.builder().title(" математический АНАЛИЗ ").facultyId(facultyId).build()))
        .isInstanceOfSatisfying(ConflictException.class, ex -> {
          assertThat(ex.getField()).isEqualTo("title");
          assertThat(ex.getExistingId()).isEqualTo(existing.id());
        });
    assertThat(courseRepository.count()).isEqualTo(1);
  }

  @Test
  void shouldAllowSameTitleOnAnotherFacultyAndForCoursesWithoutFaculty() {
    // Given
    courseService.createCourse(CourseDto.builder().title("Физика").facultyId(facultyId).build());

    // When: то же название на другом факультете и у предметов без привязки к факультету
    courseService.createCourse(
        CourseDto.builder().title("Физика").facultyId(createFaculty("Faculty of Physics")).build());
    courseService.createCourse(CourseDto.builder().title("Физика").build());
    courseService.createCourse(CourseDto.builder().title("Физика").build());

    // Then
    assertThat(courseRepository.count()).isEqualTo(4);
  }

  @Test
  void shouldRejectDuplicateTitleWhenUpdatingButAllowKeepingOwnTitle() {
    // Given
    CourseDto physics = courseService.createCourse(
        CourseDto.builder().title("Физика").facultyId(facultyId).build());
    CourseDto algebra = courseService.createCourse(
        CourseDto.builder().title("Алгебра").facultyId(facultyId).build());

    // When / Then: чужое название занято...
    assertThatThrownBy(() -> courseService.updateCourse(algebra.id(),
        CourseDto.builder().title("физика").facultyId(facultyId).build()))
        .isInstanceOfSatisfying(ConflictException.class,
            ex -> assertThat(ex.getExistingId()).isEqualTo(physics.id()));

    // ...а своё остаётся доступным
    CourseDto updated = courseService.updateCourse(algebra.id(),
        CourseDto.builder().title("АЛГЕБРА").description("Линейная").facultyId(facultyId).build());
    assertThat(updated.title()).isEqualTo("АЛГЕБРА");
    assertThat(updated.description()).isEqualTo("Линейная");
  }

  @Test
  void shouldRejectFacultyThatDoesNotMatchDepartment() {
    // Given: кафедра относится к одному факультету, а в запросе указан другой
    CourseDto dto = CourseDto.builder()
        .title("Физика")
        .departmentId(departmentId)
        .facultyId(createFaculty("Faculty of Physics"))
        .build();

    // When / Then
    assertThatThrownBy(() -> courseService.createCourse(dto))
        .isInstanceOf(ValidationException.class);
    assertThat(courseRepository.count()).isZero();
  }

  @Test
  void shouldThrowExceptionWhenCreatingCourseWithNonExistentFaculty() {
    assertThatThrownBy(() -> courseService.createCourse(
        CourseDto.builder().title("Физика").facultyId(UUID.randomUUID()).build()))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  // === Вспомогательные методы ===

  private UUID createFaculty(String name) {
    Faculty faculty = Faculty.builder()
        .name(name)
        .university(universityRepository.getReferenceById(universityId))
        .build();
    return facultyRepository.save(faculty).getId();
  }

  private CourseDto createDto(String title) {
    return CourseDto.builder()
        .title(title)
        .description("Description of " + title)
        .departmentId(departmentId)
        .teacherId(teacherId)
        .build();
  }
}
