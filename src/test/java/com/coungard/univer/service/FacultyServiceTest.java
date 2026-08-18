package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.FacultyDto;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.repository.FacultyRepository;
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
class FacultyServiceTest {

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
  private FacultyService facultyService;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private UniversityRepository universityRepository;

  private UUID universityId;

  @BeforeEach
  void setUp() {
    facultyRepository.deleteAll();
    universityRepository.deleteAll();

    University university = new University();
    university.setName("Test University");
    universityId = universityRepository.save(university).getId();
  }

  @Test
  void shouldCreateAndRetrieveFaculty() {
    // Given
    FacultyDto dto = FacultyDto.builder()
        .name("Faculty of Computer Science")
        .description("CS faculty")
        .universityId(universityId)
        .build();

    // When
    FacultyDto created = facultyService.createFaculty(dto);
    FacultyDto found = facultyService.getFacultyById(created.id());

    // Then
    assertThat(found).isNotNull();
    assertThat(found.name()).isEqualTo("Faculty of Computer Science");
    assertThat(found.description()).isEqualTo("CS faculty");
    assertThat(found.universityId()).isEqualTo(universityId);
  }

  @Test
  void shouldThrowExceptionWhenCreatingFacultyWithNonExistentUniversity() {
    FacultyDto dto = FacultyDto.builder()
        .name("Faculty of Physics")
        .universityId(UUID.randomUUID())
        .build();

    assertThatThrownBy(() -> facultyService.createFaculty(dto))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("University not found");
  }

  @Test
  void shouldGetFacultiesByUniversity() {
    // Given
    createTestFaculty("Faculty of Mathematics");
    createTestFaculty("Faculty of Physics");

    University otherUniversity = new University();
    otherUniversity.setName("Other University");
    UUID otherUniversityId = universityRepository.save(otherUniversity).getId();
    createTestFaculty("Faculty of Law", otherUniversityId);

    // When
    Pageable pageable = PageRequest.of(0, 10);
    Page<FacultyDto> faculties = facultyService.getFacultiesByUniversity(universityId, pageable);

    // Then
    assertThat(faculties.getContent()).hasSize(2);
    assertThat(faculties.getContent())
        .extracting(FacultyDto::name)
        .containsExactlyInAnyOrder("Faculty of Mathematics", "Faculty of Physics");
  }

  @Test
  void shouldUpdateFaculty() {
    // Given
    FacultyDto original = createTestFaculty("Faculty of Chemistry");

    FacultyDto updateDto = FacultyDto.builder()
        .name("Faculty of Applied Chemistry")
        .description("Renamed faculty")
        .universityId(universityId)
        .build();

    // When
    FacultyDto updated = facultyService.updateFaculty(original.id(), updateDto);

    // Then
    assertThat(updated.name()).isEqualTo("Faculty of Applied Chemistry");
    assertThat(updated.description()).isEqualTo("Renamed faculty");
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentFaculty() {
    FacultyDto dto = FacultyDto.builder()
        .name("Faculty of Biology")
        .universityId(universityId)
        .build();

    assertThatThrownBy(() -> facultyService.updateFaculty(UUID.randomUUID(), dto))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Faculty not found");
  }

  @Test
  void shouldDeleteFacultyById() {
    // Given
    FacultyDto faculty = createTestFaculty("Faculty of History");

    // When
    facultyService.deleteFaculty(faculty.id());

    // Then
    assertThat(facultyRepository.findById(faculty.id())).isEmpty();
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentFaculty() {
    assertThatThrownBy(() -> facultyService.deleteFaculty(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Faculty not found");
  }

  @Test
  void shouldThrowExceptionWhenFacultyNotFound() {
    UUID randomId = UUID.randomUUID();
    assertThatThrownBy(() -> facultyService.getFacultyById(randomId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Faculty not found with id:");
  }

  // === Вспомогательные методы ===

  private FacultyDto createTestFaculty(String name) {
    return createTestFaculty(name, universityId);
  }

  private FacultyDto createTestFaculty(String name, UUID universityId) {
    University university = universityRepository.getReferenceById(universityId);

    Faculty faculty = Faculty.builder()
        .name(name)
        .university(university)
        .build();

    Faculty saved = facultyRepository.save(faculty);

    return FacultyDto.builder()
        .id(saved.getId())
        .name(saved.getName())
        .description(saved.getDescription())
        .universityId(saved.getUniversity().getId())
        .build();
  }
}
