package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.StudentDto;
import com.coungard.univer.dto.registration.RegisterData;
import com.coungard.univer.dto.registration.RegisterStudentRequest;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.security.KeycloakAdminService;
import com.coungard.univer.security.Role;
import java.time.LocalDate;
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
class StudentServiceTest {

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
  private StudentService studentService;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private UniversityRepository universityRepository;

  @MockBean
  private KeycloakAdminService keycloakAdminService;

  private UUID universityId;

  @BeforeEach
  void setUp() {
    studentRepository.deleteAll();
    universityRepository.deleteAll();

    University university = new University();
    university.setName("Test University");
    universityId = universityRepository.save(university).getId();
  }

  @Test
  void shouldRegisterNewStudent() {
    // Given
    String mockKeycloakId = UUID.randomUUID().toString();
    RegisterStudentRequest registerDto = new RegisterStudentRequest(
        "ivan",
        "Иван",
        "Иванов",
        "Иванович",
        "ivan@example.com",
        "password123",
        LocalDate.now().minusYears(1),
        universityId
    );

    // When: Мокаем ответ Keycloak
    when(keycloakAdminService.createUser(any(RegisterData.class))).thenReturn(mockKeycloakId);

    StudentDto registered = studentService.registerStudent(registerDto);

    // Then
    assertThat(registered.firstname()).isEqualTo("Иван");
    assertThat(registered.email()).isEqualTo("ivan@example.com");
    assertThat(registered.universityId()).isEqualTo(universityId);

    assertThat(studentRepository.findById(UUID.fromString(mockKeycloakId))).isPresent();

    verify(keycloakAdminService).createUser(any(RegisterData.class));
    verify(keycloakAdminService).assignRole(eq(mockKeycloakId), eq(Role.ROLE_STUDENT));
  }

  @Test
  void shouldGetStudentsWithPaginationAndFiltering() {
    // Given
    createTestStudent("anna", "Анна", "Смирнова", LocalDate.of(2023, 9, 1));
    createTestStudent("ivan", "Иван", "Иванов", LocalDate.of(2023, 9, 1));

    Pageable pageable = PageRequest.of(0, 10);
    // When: Поиск по имени "Иван"
    Page<StudentDto> result = studentService.getStudents(pageable);

    // Then
    assertThat(result.getContent()).hasSize(2);
  }

  @Test
  void shouldUpdateStudent() {
    // Given
    StudentDto original = createTestStudent("ivan", "Иван", "Иванов", LocalDate.now());

    StudentDto updateDto = StudentDto.builder()
        .id(original.id())
        .firstname("Петр")
        .lastname("Петров")
        .email("petr@example.com")
        .enrollmentDate(LocalDate.now().minusDays(1))
        .universityId(universityId)
        .build();

    // When
    StudentDto updated = studentService.updateStudent(original.id(), updateDto);

    // Then
    assertThat(updated.firstname()).isEqualTo("Петр");
    assertThat(updated.lastname()).isEqualTo("Петров");
    assertThat(updated.email()).isEqualTo("petr@example.com");
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentStudent() {
    StudentDto dto = StudentDto.builder()
        .id(UUID.randomUUID())
        .username("petr")
        .firstname("Петр")
        .lastname("Петров")
        .email("petr@example.com")
        .enrollmentDate(LocalDate.now().minusDays(1))
        .universityId(universityId)
        .build();

    assertThatThrownBy(() -> studentService.updateStudent(dto.id(), dto))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Student not found");
  }

  @Test
  void shouldDeleteStudentById() {
    // Given
    StudentDto student = createTestStudent("ivan", "Иван", "Иванов", LocalDate.now());

    // When
    studentService.deleteStudentById(student.id());

    // Then
    assertThat(studentRepository.findById(student.id())).isEmpty();
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentStudent() {
    // When & Then
    assertThatThrownBy(() -> studentService.deleteStudentById(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Student not found");
  }

  // === Вспомогательные методы ===

  private StudentDto createTestStudent(String username, String firstName, String lastName, LocalDate enrollmentDate) {
    return createTestStudent(username, firstName, lastName, enrollmentDate, universityId);
  }

  private StudentDto createTestStudent(String username, String firstName, String lastName, LocalDate enrollmentDate,
      UUID universityId) {
    Student student = new Student();

    Person person = new Person();
    person.setUsername(username);
    person.setFirstname(firstName);
    person.setLastname(lastName);
    person.setEmail((firstName + "." + lastName + "@test.com").toLowerCase());

    student.setPerson(person);
    student.setEnrollmentDate(enrollmentDate);
    University uni = new University();
    uni.setId(universityId);
    student.setUniversity(uni);

    Student saved = studentRepository.save(student);

    return StudentDto.builder()
        .id(saved.getId())
        .username(saved.getPerson().getUsername())
        .firstname(saved.getPerson().getFirstname())
        .lastname(saved.getPerson().getLastname())
        .email(saved.getPerson().getEmail())
        .enrollmentDate(saved.getEnrollmentDate())
        .universityId(saved.getUniversity().getId())
        .build();
  }

  private University createOtherUniversity() {
    University university = new University();
    university.setName("Other University");
    return universityRepository.save(university);
  }
}