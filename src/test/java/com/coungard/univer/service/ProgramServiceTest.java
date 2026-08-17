package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.EducationForm;
import com.coungard.univer.dto.ProgramDto;
import com.coungard.univer.dto.StudyDuration;
import com.coungard.univer.dto.request.CreateProgramRequest;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.ProgramRepository;
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
class ProgramServiceTest {

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
  private ProgramService programService;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private UniversityRepository universityRepository;

  private UUID facultyId;

  @BeforeEach
  void setUp() {
    programRepository.deleteAll();
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
  void shouldCreateAndRetrieveProgram() {
    // Given
    CreateProgramRequest request = createRequest("Software Engineering", "09.03.04");

    // When
    ProgramDto created = programService.createProgram(request);
    ProgramDto found = programService.getProgramById(created.id());

    // Then
    assertThat(found).isNotNull();
    assertThat(found.name()).isEqualTo("Software Engineering");
    assertThat(found.code()).isEqualTo("09.03.04");
    assertThat(found.facultyId()).isEqualTo(facultyId);
    assertThat(found.educationForm()).isEqualTo(EducationForm.FULL_TIME);
    assertThat(found.durationOfStudy()).isEqualTo(new StudyDuration(4, 0, 0));
  }

  @Test
  void shouldThrowExceptionWhenCreatingProgramWithNonExistentFaculty() {
    CreateProgramRequest request = createRequest("Software Engineering", "09.03.04", UUID.randomUUID());

    assertThatThrownBy(() -> programService.createProgram(request))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenProgramNotFound() {
    UUID randomId = UUID.randomUUID();
    assertThatThrownBy(() -> programService.getProgramById(randomId))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldGetProgramsByFaculty() {
    // Given
    programService.createProgram(createRequest("Software Engineering", "09.03.04"));
    programService.createProgram(createRequest("Applied Mathematics", "01.03.02"));

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<ProgramDto> result = programService.getProgramsByFaculty(facultyId, pageable);

    // Then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent())
        .extracting(ProgramDto::name)
        .containsExactlyInAnyOrder("Software Engineering", "Applied Mathematics");
  }

  @Test
  void shouldGetAllPrograms() {
    // Given
    programService.createProgram(createRequest("Software Engineering", "09.03.04"));
    programService.createProgram(createRequest("Applied Mathematics", "01.03.02"));

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<ProgramDto> result = programService.getPrograms(pageable);

    // Then
    assertThat(result.getContent()).hasSize(2);
  }

  @Test
  void shouldUpdateProgram() {
    // Given
    ProgramDto original = programService.createProgram(createRequest("Software Engineering", "09.03.04"));

    CreateProgramRequest updateRequest = new CreateProgramRequest(
        facultyId,
        "09.03.04",
        "Software Engineering (Updated)",
        "Software Engineer",
        "IT",
        "Bachelor",
        EducationForm.PART_TIME,
        new StudyDuration(5, 0, 0),
        "Bachelor's degree"
    );

    // When
    ProgramDto updated = programService.updateProgram(original.id(), updateRequest);

    // Then
    assertThat(updated.name()).isEqualTo("Software Engineering (Updated)");
    assertThat(updated.educationForm()).isEqualTo(EducationForm.PART_TIME);
    assertThat(updated.durationOfStudy()).isEqualTo(new StudyDuration(5, 0, 0));
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentProgram() {
    CreateProgramRequest request = createRequest("Software Engineering", "09.03.04");

    assertThatThrownBy(() -> programService.updateProgram(UUID.randomUUID(), request))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenUpdatingProgramWithNonExistentFaculty() {
    ProgramDto original = programService.createProgram(createRequest("Software Engineering", "09.03.04"));
    CreateProgramRequest updateRequest = createRequest("Software Engineering", "09.03.04", UUID.randomUUID());

    assertThatThrownBy(() -> programService.updateProgram(original.id(), updateRequest))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldDeleteProgramById() {
    // Given
    ProgramDto program = programService.createProgram(createRequest("Software Engineering", "09.03.04"));

    // When
    programService.deleteProgram(program.id());

    // Then
    assertThat(programRepository.findById(program.id())).isEmpty();
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentProgram() {
    assertThatThrownBy(() -> programService.deleteProgram(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  // === Вспомогательные методы ===

  private CreateProgramRequest createRequest(String name, String code) {
    return createRequest(name, code, facultyId);
  }

  private CreateProgramRequest createRequest(String name, String code, UUID facultyId) {
    return new CreateProgramRequest(
        facultyId,
        code,
        name,
        "Software Engineer",
        "IT",
        "Bachelor",
        EducationForm.FULL_TIME,
        new StudyDuration(4, 0, 0),
        "Bachelor's degree"
    );
  }
}
