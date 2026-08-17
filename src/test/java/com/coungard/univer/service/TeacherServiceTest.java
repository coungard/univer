package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.TeacherDto;
import com.coungard.univer.dto.registration.RegisterTeacherRequest;
import com.coungard.univer.entity.Department;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Teacher;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.repository.DepartmentRepository;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.TeacherRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.security.KeycloakAdminService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
class TeacherServiceTest {

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
  private TeacherService teacherService;

  @Autowired
  private TeacherRepository teacherRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private UniversityRepository universityRepository;

  @MockBean
  private KeycloakAdminService keycloakAdminService;

  private UUID facultyId;

  @BeforeEach
  void setUp() {
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
    facultyId = facultyRepository.save(faculty).getId();
  }

  @Test
  void shouldGetTeacherById() {
    // Given
    TeacherDto created = createTestTeacher("ivanov", "Иван", "Иванов", "ivanov@example.com");

    // When
    TeacherDto found = teacherService.getTeacherById(created.id());

    // Then
    assertThat(found.firstname()).isEqualTo("Иван");
    assertThat(found.lastname()).isEqualTo("Иванов");
    assertThat(found.facultyId()).isEqualTo(facultyId);
  }

  @Test
  void shouldThrowExceptionWhenTeacherNotFound() {
    UUID randomId = UUID.randomUUID();
    assertThatThrownBy(() -> teacherService.getTeacherById(randomId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Преподаватель не найден");
  }

  @Test
  void shouldGetTeachersWithPagination() {
    // Given
    createTestTeacher("ivanov", "Иван", "Иванов", "ivanov@example.com");
    createTestTeacher("petrov", "Петр", "Петров", "petrov@example.com");

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<TeacherDto> result = teacherService.getTeachers(pageable);

    // Then
    assertThat(result.getContent()).hasSize(2);
  }

  @Test
  void shouldCreateTeacher() {
    // Given
    TeacherDto dto = TeacherDto.builder()
        .username("smirnov")
        .firstname("Алексей")
        .lastname("Смирнов")
        .email("smirnov@example.com")
        .facultyId(facultyId)
        .position("Доцент")
        .build();

    // When
    TeacherDto created = teacherService.createTeacher(dto);

    // Then
    assertThat(created.id()).isNotNull();
    assertThat(created.firstname()).isEqualTo("Алексей");
    assertThat(created.position()).isEqualTo("Доцент");
    assertThat(created.registered()).isFalse();
    assertThat(teacherRepository.findById(created.id())).isPresent();
  }

  @Test
  void shouldThrowExceptionWhenCreatingTeacherWithDuplicateEmail() {
    // Given
    createTestTeacher("ivanov", "Иван", "Иванов", "ivanov@example.com");

    TeacherDto dto = TeacherDto.builder()
        .username("ivanov2")
        .firstname("Иван")
        .lastname("Иванов")
        .email("ivanov@example.com")
        .facultyId(facultyId)
        .position("Доцент")
        .build();

    // When & Then
    assertThatThrownBy(() -> teacherService.createTeacher(dto))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowExceptionWhenCreatingTeacherWithNonExistentFaculty() {
    TeacherDto dto = TeacherDto.builder()
        .username("smirnov")
        .firstname("Алексей")
        .lastname("Смирнов")
        .email("smirnov@example.com")
        .facultyId(UUID.randomUUID())
        .position("Доцент")
        .build();

    assertThatThrownBy(() -> teacherService.createTeacher(dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldUpdateTeacher() {
    // Given
    TeacherDto original = createTestTeacher("ivanov", "Иван", "Иванов", "ivanov@example.com");

    TeacherDto updateDto = TeacherDto.builder()
        .firstname("Пётр")
        .lastname("Петров")
        .email("petrov@example.com")
        .facultyId(facultyId)
        .position("Профессор")
        .build();

    // When
    TeacherDto updated = teacherService.updateTeacher(original.id(), updateDto);

    // Then
    assertThat(updated.firstname()).isEqualTo("Пётр");
    assertThat(updated.lastname()).isEqualTo("Петров");
    assertThat(updated.email()).isEqualTo("petrov@example.com");
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentTeacher() {
    TeacherDto dto = TeacherDto.builder()
        .firstname("Пётр")
        .lastname("Петров")
        .email("petrov@example.com")
        .facultyId(facultyId)
        .position("Профессор")
        .build();

    assertThatThrownBy(() -> teacherService.updateTeacher(UUID.randomUUID(), dto))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Преподаватель не найден");
  }

  @Test
  void shouldThrowExceptionWhenUpdatingTeacherWithNonExistentFaculty() {
    TeacherDto original = createTestTeacher("ivanov", "Иван", "Иванов", "ivanov@example.com");

    TeacherDto updateDto = TeacherDto.builder()
        .firstname("Пётр")
        .lastname("Петров")
        .email("petrov@example.com")
        .facultyId(UUID.randomUUID())
        .position("Профессор")
        .build();

    assertThatThrownBy(() -> teacherService.updateTeacher(original.id(), updateDto))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Faculty not found");
  }

  @Test
  void shouldDeleteTeacherById() {
    // Given
    TeacherDto teacher = createTestTeacher("ivanov", "Иван", "Иванов", "ivanov@example.com");

    // When
    teacherService.deleteTeacherById(teacher.id());

    // Then
    assertThat(teacherRepository.findById(teacher.id())).isEmpty();
    verify(keycloakAdminService).deleteUser(teacher.id().toString());
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentTeacher() {
    assertThatThrownBy(() -> teacherService.deleteTeacherById(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Преподаватель не найден");
  }

  /**
   * Документирует текущий баг: {@code RegisterTeacherRequest.departmentId} проверяется через
   * {@code DepartmentRepository}, но {@code TeacherServiceImpl.registerTeacher} тут же ищет по
   * этому же ID факультет через {@code FacultyRepository}. В реальных данных ID кафедры и ID
   * факультета не совпадают, поэтому регистрация падает даже на валидном запросе.
   * См. issue #28 на исправление.
   */
  @Test
  void shouldFailToRegisterTeacherBecauseDepartmentIdIsLookedUpAsFacultyId() {
    // Given: валидная кафедра существует, но её ID не совпадает с ID факультета
    Department department = new Department();
    department.setName("Department of Algorithms");
    department.setFaculty(facultyRepository.getReferenceById(facultyId));
    UUID departmentId = departmentRepository.save(department).getId();

    RegisterTeacherRequest request = RegisterTeacherRequest.builder()
        .username("newteacher")
        .firstname("Новый")
        .lastname("Преподаватель")
        .password("password123")
        .email("newteacher@example.com")
        .departmentId(departmentId)
        .position("Доцент")
        .build();

    // When & Then
    assertThatThrownBy(() -> teacherService.registerTeacher(request))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Faculty not found");
  }

  // === Вспомогательные методы ===

  private TeacherDto createTestTeacher(String username, String firstName, String lastName, String email) {
    Teacher teacher = new Teacher();

    Person person = new Person();
    person.setUsername(username);
    person.setFirstname(firstName);
    person.setLastname(lastName);
    person.setEmail(email);

    teacher.setPerson(person);
    teacher.setPosition("Доцент");
    teacher.setRegistered(false);
    teacher.setFaculty(facultyRepository.getReferenceById(facultyId));

    Teacher saved = teacherRepository.save(teacher);

    return TeacherDto.builder()
        .id(saved.getId())
        .username(saved.getPerson().getUsername())
        .firstname(saved.getPerson().getFirstname())
        .lastname(saved.getPerson().getLastname())
        .email(saved.getPerson().getEmail())
        .facultyId(saved.getFaculty().getId())
        .position(saved.getPosition())
        .registered(saved.isRegistered())
        .build();
  }
}
