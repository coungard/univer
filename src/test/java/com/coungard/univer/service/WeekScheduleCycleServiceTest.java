package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.EducationForm;
import com.coungard.univer.dto.SemesterType;
import com.coungard.univer.dto.WeekScheduleCycleDto;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Program;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.ProgramRepository;
import com.coungard.univer.repository.SemesterRepository;
import com.coungard.univer.repository.StudyYearRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.repository.WeekScheduleCycleRepository;
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
class WeekScheduleCycleServiceTest {

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
  private WeekScheduleCycleService weekScheduleCycleService;

  @Autowired
  private WeekScheduleCycleRepository weekScheduleCycleRepository;

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

  private UUID semesterId;

  @BeforeEach
  void setUp() {
    weekScheduleCycleRepository.deleteAll();
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
    studyYear.setYearNumber(5);
    UUID studyYearId = studyYearRepository.save(studyYear).getId();

    Semester semester = new Semester();
    semester.setStudyYear(studyYearRepository.getReferenceById(studyYearId));
    semester.setType(SemesterType.AUTUMN);
    semester.setStartDate(LocalDate.of(2026, 9, 1));
    semester.setEndDate(LocalDate.of(2026, 12, 20));
    semesterId = semesterRepository.save(semester).getId();
  }

  @Test
  void shouldCreateAndRetrieveWeekScheduleCycle() {
    // Given
    WeekScheduleCycleDto dto = WeekScheduleCycleDto.builder().semesterId(semesterId).build();

    // When
    WeekScheduleCycleDto created = weekScheduleCycleService.createWeekScheduleCycle(dto);
    WeekScheduleCycleDto found = weekScheduleCycleService.getWeekScheduleCycleById(created.id());

    // Then
    assertThat(found).isNotNull();
    assertThat(found.semesterId()).isEqualTo(semesterId);
  }

  @Test
  void shouldGetWeekScheduleCycleBySemester() {
    // Given
    WeekScheduleCycleDto created = weekScheduleCycleService.createWeekScheduleCycle(
        WeekScheduleCycleDto.builder().semesterId(semesterId).build());

    // When
    WeekScheduleCycleDto found = weekScheduleCycleService.getWeekScheduleCycleBySemester(semesterId);

    // Then
    assertThat(found.id()).isEqualTo(created.id());
  }

  @Test
  void shouldThrowExceptionWhenCreatingCycleWithNonExistentSemester() {
    WeekScheduleCycleDto dto = WeekScheduleCycleDto.builder().semesterId(UUID.randomUUID()).build();

    assertThatThrownBy(() -> weekScheduleCycleService.createWeekScheduleCycle(dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenCreatingDuplicateCycleForSameSemester() {
    // Given
    weekScheduleCycleService.createWeekScheduleCycle(WeekScheduleCycleDto.builder().semesterId(semesterId).build());

    WeekScheduleCycleDto duplicate = WeekScheduleCycleDto.builder().semesterId(semesterId).build();

    // When & Then
    assertThatThrownBy(() -> weekScheduleCycleService.createWeekScheduleCycle(duplicate))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowExceptionWhenCycleNotFound() {
    UUID randomId = UUID.randomUUID();
    assertThatThrownBy(() -> weekScheduleCycleService.getWeekScheduleCycleById(randomId))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldGetWeekScheduleCycles() {
    // Given
    weekScheduleCycleService.createWeekScheduleCycle(WeekScheduleCycleDto.builder().semesterId(semesterId).build());

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<WeekScheduleCycleDto> result = weekScheduleCycleService.getWeekScheduleCycles(pageable);

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldDeleteWeekScheduleCycleById() {
    // Given
    WeekScheduleCycleDto cycle = weekScheduleCycleService.createWeekScheduleCycle(
        WeekScheduleCycleDto.builder().semesterId(semesterId).build());

    // When
    weekScheduleCycleService.deleteWeekScheduleCycle(cycle.id());

    // Then
    assertThat(weekScheduleCycleRepository.findById(cycle.id())).isEmpty();
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentCycle() {
    assertThatThrownBy(() -> weekScheduleCycleService.deleteWeekScheduleCycle(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class);
  }
}
