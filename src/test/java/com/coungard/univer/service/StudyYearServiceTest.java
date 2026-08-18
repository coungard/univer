package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.EducationForm;
import com.coungard.univer.dto.StudyYearDto;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Program;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.ProgramRepository;
import com.coungard.univer.repository.StudyYearRepository;
import com.coungard.univer.repository.UniversityRepository;
import java.time.Period;
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
class StudyYearServiceTest {

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
  private StudyYearService studyYearService;

  @Autowired
  private StudyYearRepository studyYearRepository;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private UniversityRepository universityRepository;

  private UUID programId;

  @BeforeEach
  void setUp() {
    studyYearRepository.deleteAll();
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
    UUID facultyId = facultyRepository.save(faculty).getId();

    Program program = new Program();
    program.setFacultyId(facultyId);
    program.setCode("09.03.04");
    program.setName("Software Engineering");
    program.setEducationLevel("Bachelor");
    program.setEducationForm(EducationForm.FULL_TIME);
    program.setDurationOfStudy(Period.ofYears(4));
    programId = programRepository.save(program).getId();
  }

  @Test
  void shouldCreateAndRetrieveStudyYear() {
    // Given
    StudyYearDto dto = StudyYearDto.builder().programId(programId).yearNumber(1).build();

    // When
    StudyYearDto created = studyYearService.createStudyYear(dto);
    StudyYearDto found = studyYearService.getStudyYearById(created.id());

    // Then
    assertThat(found).isNotNull();
    assertThat(found.programId()).isEqualTo(programId);
    assertThat(found.yearNumber()).isEqualTo(1);
  }

  @Test
  void shouldThrowExceptionWhenCreatingStudyYearWithNonExistentProgram() {
    StudyYearDto dto = StudyYearDto.builder().programId(UUID.randomUUID()).yearNumber(1).build();

    assertThatThrownBy(() -> studyYearService.createStudyYear(dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenYearNumberExceedsProgramDuration() {
    // Программа рассчитана на 4 года, курс 5 не должен пройти валидацию
    StudyYearDto dto = StudyYearDto.builder().programId(programId).yearNumber(5).build();

    assertThatThrownBy(() -> studyYearService.createStudyYear(dto))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowExceptionWhenCreatingDuplicateYearNumberForSameProgram() {
    // Given
    studyYearService.createStudyYear(StudyYearDto.builder().programId(programId).yearNumber(1).build());

    StudyYearDto duplicate = StudyYearDto.builder().programId(programId).yearNumber(1).build();

    // When & Then
    assertThatThrownBy(() -> studyYearService.createStudyYear(duplicate))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowExceptionWhenStudyYearNotFound() {
    UUID randomId = UUID.randomUUID();
    assertThatThrownBy(() -> studyYearService.getStudyYearById(randomId))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldGetStudyYears() {
    // Given
    studyYearService.createStudyYear(StudyYearDto.builder().programId(programId).yearNumber(1).build());
    studyYearService.createStudyYear(StudyYearDto.builder().programId(programId).yearNumber(2).build());

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<StudyYearDto> result = studyYearService.getStudyYears(pageable);

    // Then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent())
        .extracting(StudyYearDto::yearNumber)
        .containsExactlyInAnyOrder(1, 2);
  }

  @Test
  void shouldGetStudyYearsByProgram() {
    // Given
    studyYearService.createStudyYear(StudyYearDto.builder().programId(programId).yearNumber(1).build());

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<StudyYearDto> result = studyYearService.getStudyYearsByProgram(programId, pageable);

    // Then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).yearNumber()).isEqualTo(1);
  }

  @Test
  void shouldUpdateStudyYear() {
    // Given
    StudyYearDto original = studyYearService.createStudyYear(
        StudyYearDto.builder().programId(programId).yearNumber(1).build());

    StudyYearDto updateDto = StudyYearDto.builder().programId(programId).yearNumber(2).build();

    // When
    StudyYearDto updated = studyYearService.updateStudyYear(original.id(), updateDto);

    // Then
    assertThat(updated.yearNumber()).isEqualTo(2);
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentStudyYear() {
    StudyYearDto dto = StudyYearDto.builder().programId(programId).yearNumber(1).build();

    assertThatThrownBy(() -> studyYearService.updateStudyYear(UUID.randomUUID(), dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldDeleteStudyYearById() {
    // Given
    StudyYearDto studyYear = studyYearService.createStudyYear(
        StudyYearDto.builder().programId(programId).yearNumber(1).build());

    // When
    studyYearService.deleteStudyYear(studyYear.id());

    // Then
    assertThat(studyYearRepository.findById(studyYear.id())).isEmpty();
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentStudyYear() {
    assertThatThrownBy(() -> studyYearService.deleteStudyYear(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class);
  }
}
