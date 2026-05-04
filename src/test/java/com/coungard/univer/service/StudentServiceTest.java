package com.coungard.univer.service;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.RegisterStudentDto;
import com.coungard.univer.dto.StudentDto;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.UniversityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        RegisterStudentDto registerDto = new RegisterStudentDto(
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
        when(keycloakAdminService.createUser(registerDto))
                .thenReturn(mockKeycloakId);

        StudentDto registered = studentService.registerStudent(registerDto);

        // Then
        assertThat(registered.firstname()).isEqualTo("Иван");
        assertThat(registered.email()).isEqualTo("ivan@example.com");
        assertThat(registered.universityId()).isEqualTo(universityId);

        assertThat(studentRepository.findById(UUID.fromString(mockKeycloakId))).isPresent();

        verify(keycloakAdminService).createUser(any(RegisterStudentDto.class));
        verify(keycloakAdminService).assignStudentRole(eq(mockKeycloakId));
    }

    @Test
    void shouldGetStudentsWithPaginationAndFiltering() {
        // Given
        createTestStudent("anna", "Анна", "Смирнова", LocalDate.of(2023, 9, 1));
        createTestStudent("ivan", "Иван", "Иванов", LocalDate.of(2023, 9, 1));
        createTestStudent("ivanpetrov", "Иван", "Петров", LocalDate.of(2024, 1, 15));

        // When: Поиск по имени "Иван"
        Page<StudentDto> result = studentService.getStudents(
                "Иван", null, null, 0, 10, "lastname", "asc"
        );

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(StudentDto::firstname)
                .containsOnly("Иван");
    }

    @Test
    void shouldFilterStudentsByUniversity() {
        // Given
        UUID otherUniversityId = createOtherUniversity().getId();
        createTestStudent("ivan", "Иван", "Иванов", LocalDate.now(), universityId);
        createTestStudent("petr", "Петр", "Петров", LocalDate.now(), otherUniversityId);

        // When
        Page<StudentDto> result = studentService.getStudents(
                null, universityId, null, 0, 10, "id", "asc"
        );

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).firstname()).isEqualTo("Иван");
    }

    @Test
    void shouldFilterStudentsByEnrollmentDate() {
        // Given
        createTestStudent("ivan", "Иван", "Иванов", LocalDate.of(2023, 9, 1));
        createTestStudent("petr", "Петр", "Петров", LocalDate.of(2024, 1, 15));

        // When
        Page<StudentDto> result = studentService.getStudents(
                null, null, LocalDate.of(2023, 9, 1), 0, 10, "id", "asc"
        );

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).firstname()).isEqualTo("Иван");
    }

    @Test
    void shouldUpdateStudent() {
        // Given
        StudentDto original = createTestStudent("ivan", "Иван", "Иванов", LocalDate.now());

        StudentDto updateDto = StudentDto.builder()
                .id(original.id())
                .username("petr")
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

    private StudentDto createTestStudent(String username, String firstName, String lastName, LocalDate enrollmentDate, UUID universityId) {
        Student student = new Student();
        student.setUsername(username);
        student.setFirstname(firstName);
        student.setLastname(lastName);
        student.setEmail((firstName + "." + lastName + "@test.com").toLowerCase());
        student.setEnrollmentDate(enrollmentDate);
        University uni = new University();
        uni.setId(universityId);
        student.setUniversity(uni);

        Student saved = studentRepository.save(student);

        return StudentDto.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .firstname(saved.getFirstname())
                .lastname(saved.getLastname())
                .email(saved.getEmail())
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