package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.EducationForm;
import com.coungard.univer.dto.SemesterDto;
import com.coungard.univer.dto.SemesterType;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Program;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.ProgramRepository;
import com.coungard.univer.repository.SemesterRepository;
import com.coungard.univer.repository.StudyYearRepository;
import com.coungard.univer.repository.UniversityRepository;
import java.time.LocalDate;
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
class SemesterServiceTest {

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
  private SemesterService semesterService;

  @Autowired
  private SemesterRepository semesterRepository;

  @Autowired
  private StudyYearRepository studyYearRepository;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private UniversityRepository universityRepository;

  private UUID studyYearId;

  @BeforeEach
  void setUp() {
    semesterRepository.deleteAll();
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
    UUID programId = programRepository.save(program).getId();

    StudyYear studyYear = new StudyYear();
    studyYear.setProgram(programRepository.getReferenceById(programId));
    studyYear.setYearNumber(1);
    studyYearId = studyYearRepository.save(studyYear).getId();
  }

  @Test
  void shouldCreateAndRetrieveSemester() {
    // Given
    SemesterDto dto = SemesterDto.builder()
        .studyYearId(studyYearId)
        .type(SemesterType.AUTUMN)
        .startDate(LocalDate.of(2026, 9, 1))
        .endDate(LocalDate.of(2026, 12, 20))
        .build();

    // When
    SemesterDto created = semesterService.createSemester(dto);
    SemesterDto found = semesterService.getSemesterById(created.id());

    // Then
    assertThat(found).isNotNull();
    assertThat(found.studyYearId()).isEqualTo(studyYearId);
    assertThat(found.type()).isEqualTo(SemesterType.AUTUMN);
    assertThat(found.startDate()).isEqualTo(LocalDate.of(2026, 9, 1));
    assertThat(found.endDate()).isEqualTo(LocalDate.of(2026, 12, 20));
  }

  @Test
  void shouldThrowExceptionWhenCreatingSemesterWithNonExistentStudyYear() {
    SemesterDto dto = SemesterDto.builder()
        .studyYearId(UUID.randomUUID())
        .type(SemesterType.AUTUMN)
        .startDate(LocalDate.of(2026, 9, 1))
        .endDate(LocalDate.of(2026, 12, 20))
        .build();

    assertThatThrownBy(() -> semesterService.createSemester(dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenEndDateNotAfterStartDate() {
    SemesterDto dto = SemesterDto.builder()
        .studyYearId(studyYearId)
        .type(SemesterType.AUTUMN)
        .startDate(LocalDate.of(2026, 9, 1))
        .endDate(LocalDate.of(2026, 9, 1))
        .build();

    assertThatThrownBy(() -> semesterService.createSemester(dto))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowExceptionWhenSemesterNotFound() {
    UUID randomId = UUID.randomUUID();
    assertThatThrownBy(() -> semesterService.getSemesterById(randomId))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldGetSemesters() {
    // Given
    semesterService.createSemester(SemesterDto.builder()
        .studyYearId(studyYearId).type(SemesterType.AUTUMN).startDate(LocalDate.of(2026, 9, 1))
        .endDate(LocalDate.of(2026, 12, 20)).build());
    semesterService.createSemester(SemesterDto.builder()
        .studyYearId(studyYearId).type(SemesterType.SPRING).startDate(LocalDate.of(2027, 2, 9))
        .endDate(LocalDate.of(2027, 6, 20)).build());

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<SemesterDto> result = semesterService.getSemesters(pageable);

    // Then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent())
        .extracting(SemesterDto::type)
        .containsExactlyInAnyOrder(SemesterType.AUTUMN, SemesterType.SPRING);
  }

  @Test
  void shouldGetSemestersByStudyYear() {
    // Given
    semesterService.createSemester(SemesterDto.builder()
        .studyYearId(studyYearId).type(SemesterType.AUTUMN).startDate(LocalDate.of(2026, 9, 1))
        .endDate(LocalDate.of(2026, 12, 20)).build());

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<SemesterDto> result = semesterService.getSemestersByStudyYear(studyYearId, pageable);

    // Then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).type()).isEqualTo(SemesterType.AUTUMN);
  }

  @Test
  void shouldUpdateSemester() {
    // Given
    SemesterDto original = semesterService.createSemester(SemesterDto.builder()
        .studyYearId(studyYearId).type(SemesterType.AUTUMN).startDate(LocalDate.of(2026, 9, 1))
        .endDate(LocalDate.of(2026, 12, 20)).build());

    SemesterDto updateDto = SemesterDto.builder()
        .studyYearId(studyYearId)
        .type(SemesterType.SPRING)
        .startDate(LocalDate.of(2027, 2, 9))
        .endDate(LocalDate.of(2027, 6, 20))
        .build();

    // When
    SemesterDto updated = semesterService.updateSemester(original.id(), updateDto);

    // Then
    assertThat(updated.type()).isEqualTo(SemesterType.SPRING);
    assertThat(updated.startDate()).isEqualTo(LocalDate.of(2027, 2, 9));
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentSemester() {
    SemesterDto dto = SemesterDto.builder()
        .studyYearId(studyYearId).type(SemesterType.AUTUMN).startDate(LocalDate.of(2026, 9, 1))
        .endDate(LocalDate.of(2026, 12, 20)).build();

    assertThatThrownBy(() -> semesterService.updateSemester(UUID.randomUUID(), dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldDeleteSemesterById() {
    // Given
    SemesterDto semester = semesterService.createSemester(SemesterDto.builder()
        .studyYearId(studyYearId).type(SemesterType.AUTUMN).startDate(LocalDate.of(2026, 9, 1))
        .endDate(LocalDate.of(2026, 12, 20)).build());

    // When
    semesterService.deleteSemester(semester.id());

    // Then
    assertThat(semesterRepository.findById(semester.id())).isEmpty();
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentSemester() {
    assertThatThrownBy(() -> semesterService.deleteSemester(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class);
  }
}
