package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.EducationForm;
import com.coungard.univer.dto.PairDto;
import com.coungard.univer.dto.SemesterType;
import com.coungard.univer.dto.WeekParity;
import com.coungard.univer.entity.BellScheduleEntry;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Department;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Program;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.entity.University;
import com.coungard.univer.entity.WeekScheduleCycle;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.repository.BellScheduleEntryRepository;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.DepartmentRepository;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.GroupRepository;
import com.coungard.univer.repository.PairRepository;
import com.coungard.univer.repository.ProgramRepository;
import com.coungard.univer.repository.SemesterRepository;
import com.coungard.univer.repository.StudyYearRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.repository.WeekScheduleCycleRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.Set;
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
class PairServiceTest {

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
  private PairService pairService;

  @Autowired
  private PairRepository pairRepository;

  @Autowired
  private WeekScheduleCycleRepository weekScheduleCycleRepository;

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private GroupRepository groupRepository;

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

  @Autowired
  private BellScheduleEntryRepository bellScheduleEntryRepository;

  private UUID universityId;
  private UUID weekScheduleCycleId;
  private UUID courseId;
  private UUID group1Id;
  private UUID group2Id;

  @BeforeEach
  void setUp() {
    bellScheduleEntryRepository.deleteAll();
    pairRepository.deleteAll();
    weekScheduleCycleRepository.deleteAll();
    groupRepository.deleteAll();
    semesterRepository.deleteAll();
    studyYearRepository.deleteAll();
    courseRepository.deleteAll();
    departmentRepository.deleteAll();
    programRepository.deleteAll();
    facultyRepository.deleteAll();
    universityRepository.deleteAll();

    University university = new University();
    university.setName("Test University");
    universityId = universityRepository.save(university).getId();

    Faculty faculty = Faculty.builder()
        .name("Faculty of Computer Science")
        .university(universityRepository.getReferenceById(universityId))
        .build();
    UUID facultyId = facultyRepository.save(faculty).getId();

    Department department = new Department();
    department.setName("Department of Algorithms");
    department.setFaculty(facultyRepository.getReferenceById(facultyId));
    UUID departmentId = departmentRepository.save(department).getId();

    Course course = new Course();
    course.setTitle("Algorithms and Data Structures");
    course.setDepartment(departmentRepository.getReferenceById(departmentId));
    courseId = courseRepository.save(course).getId();

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
    UUID studyYearId = studyYearRepository.save(studyYear).getId();

    Semester semester = new Semester();
    semester.setStudyYear(studyYearRepository.getReferenceById(studyYearId));
    semester.setType(SemesterType.AUTUMN);
    semester.setStartDate(LocalDate.of(2026, 9, 1));
    semester.setEndDate(LocalDate.of(2026, 12, 20));
    UUID semesterId = semesterRepository.save(semester).getId();

    WeekScheduleCycle cycle = new WeekScheduleCycle();
    cycle.setSemester(semesterRepository.getReferenceById(semesterId));
    weekScheduleCycleId = weekScheduleCycleRepository.save(cycle).getId();

    Group group1 = new Group();
    group1.setSemester(semesterRepository.getReferenceById(semesterId));
    group1.setName("У532 КСиТ");
    group1Id = groupRepository.save(group1).getId();

    Group group2 = new Group();
    group2.setSemester(semesterRepository.getReferenceById(semesterId));
    group2.setName("У533 КСиТ");
    group2Id = groupRepository.save(group2).getId();
  }

  @Test
  void shouldCreateAndRetrievePair() {
    // Given
    PairDto dto = createDto(Set.of(group1Id));

    // When
    PairDto created = pairService.createPair(dto);
    PairDto found = pairService.getPairById(created.id());

    // Then
    assertThat(found).isNotNull();
    assertThat(found.courseId()).isEqualTo(courseId);
    assertThat(found.groupIds()).containsExactlyInAnyOrder(group1Id);
  }

  @Test
  void shouldCreateStreamPairWithMultipleGroups() {
    // Given: поточная лекция сразу на две группы
    PairDto dto = createDto(Set.of(group1Id, group2Id));

    // When
    PairDto created = pairService.createPair(dto);

    // Then
    assertThat(created.groupIds()).containsExactlyInAnyOrder(group1Id, group2Id);
  }

  @Test
  void shouldThrowExceptionWhenCreatingPairWithNonExistentCycle() {
    PairDto dto = createDto(Set.of(group1Id)).toBuilder().weekScheduleCycleId(UUID.randomUUID()).build();

    assertThatThrownBy(() -> pairService.createPair(dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenCreatingPairWithNonExistentCourse() {
    PairDto dto = createDto(Set.of(group1Id)).toBuilder().courseId(UUID.randomUUID()).build();

    assertThatThrownBy(() -> pairService.createPair(dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenCreatingPairWithNonExistentGroup() {
    PairDto dto = createDto(Set.of(UUID.randomUUID()));

    assertThatThrownBy(() -> pairService.createPair(dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenDayOfWeekIsWeekend() {
    PairDto dto = createDto(Set.of(group1Id)).toBuilder().dayOfWeek(DayOfWeek.SATURDAY).build();

    assertThatThrownBy(() -> pairService.createPair(dto))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowExceptionWhenEndTimeBeforeStartTime() {
    PairDto dto = createDto(Set.of(group1Id)).toBuilder()
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(9, 0))
        .build();

    assertThatThrownBy(() -> pairService.createPair(dto))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowExceptionWhenPairNotFound() {
    assertThatThrownBy(() -> pairService.getPairById(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldGetPairs() {
    // Given
    pairService.createPair(createDto(Set.of(group1Id)));
    pairService.createPair(createDto(Set.of(group2Id)).toBuilder().dayOfWeek(DayOfWeek.TUESDAY).build());

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<PairDto> result = pairService.getPairs(pageable);

    // Then
    assertThat(result.getContent()).hasSize(2);
  }

  @Test
  void shouldGetPairsByWeekScheduleCycle() {
    // Given
    pairService.createPair(createDto(Set.of(group1Id)));

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<PairDto> result = pairService.getPairsByWeekScheduleCycle(weekScheduleCycleId, pageable);

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldGetPairsByGroupIncludingStreamPairs() {
    // Given: поточная пара видна в расписании обеих групп
    pairService.createPair(createDto(Set.of(group1Id, group2Id)));

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<PairDto> group1Schedule = pairService.getPairsByGroup(group1Id, pageable);
    Page<PairDto> group2Schedule = pairService.getPairsByGroup(group2Id, pageable);

    // Then
    assertThat(group1Schedule.getContent()).hasSize(1);
    assertThat(group2Schedule.getContent()).hasSize(1);
    assertThat(group1Schedule.getContent().get(0).id()).isEqualTo(group2Schedule.getContent().get(0).id());
  }

  @Test
  void shouldUpdatePair() {
    // Given
    PairDto original = pairService.createPair(createDto(Set.of(group1Id)));

    PairDto updateDto = original.toBuilder()
        .dayOfWeek(DayOfWeek.WEDNESDAY)
        .weekParity(WeekParity.EVEN)
        .groupIds(Set.of(group1Id, group2Id))
        .build();

    // When
    PairDto updated = pairService.updatePair(original.id(), updateDto);

    // Then
    assertThat(updated.dayOfWeek()).isEqualTo(DayOfWeek.WEDNESDAY);
    assertThat(updated.weekParity()).isEqualTo(WeekParity.EVEN);
    assertThat(updated.groupIds()).containsExactlyInAnyOrder(group1Id, group2Id);
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentPair() {
    PairDto dto = createDto(Set.of(group1Id));

    assertThatThrownBy(() -> pairService.updatePair(UUID.randomUUID(), dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldDeletePairById() {
    // Given
    PairDto pair = pairService.createPair(createDto(Set.of(group1Id)));

    // When
    pairService.deletePair(pair.id());

    // Then
    assertThat(pairRepository.findById(pair.id())).isEmpty();
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentPair() {
    assertThatThrownBy(() -> pairService.deletePair(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldPopulateStartAndEndTimeFromBellScheduleWhenOmitted() {
    // Given: справочная запись для этого университета и pairNumber=1
    saveBellScheduleEntry(universityId, 1, LocalTime.of(9, 0), LocalTime.of(10, 30));
    PairDto dto = createDto(Set.of(group1Id)).toBuilder().startTime(null).endTime(null).build();

    // When
    PairDto created = pairService.createPair(dto);

    // Then
    assertThat(created.startTime()).isEqualTo(LocalTime.of(9, 0));
    assertThat(created.endTime()).isEqualTo(LocalTime.of(10, 30));
  }

  @Test
  void shouldFallBackToDefaultBellScheduleEntryWhenNoUniversitySpecificOne() {
    // Given: только системная запись по умолчанию (university = null)
    saveBellScheduleEntry(null, 1, LocalTime.of(9, 0), LocalTime.of(10, 30));
    PairDto dto = createDto(Set.of(group1Id)).toBuilder().startTime(null).endTime(null).build();

    // When
    PairDto created = pairService.createPair(dto);

    // Then
    assertThat(created.startTime()).isEqualTo(LocalTime.of(9, 0));
    assertThat(created.endTime()).isEqualTo(LocalTime.of(10, 30));
  }

  @Test
  void shouldThrowValidationExceptionWhenTimesOmittedAndNoBellScheduleEntryExists() {
    // Given: справочник пуст
    PairDto dto = createDto(Set.of(group1Id)).toBuilder().startTime(null).endTime(null).build();

    assertThatThrownBy(() -> pairService.createPair(dto))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldAllowExplicitTimesToOverrideBellScheduleEntry() {
    // Given: в справочнике другое время — явно заданное в DTO должно победить (перенос занятия)
    saveBellScheduleEntry(universityId, 1, LocalTime.of(9, 0), LocalTime.of(10, 30));
    PairDto dto = createDto(Set.of(group1Id));

    // When
    PairDto created = pairService.createPair(dto);

    // Then: остались явные времена из createDto (8:00-9:30), а не из справочника
    assertThat(created.startTime()).isEqualTo(LocalTime.of(8, 0));
    assertThat(created.endTime()).isEqualTo(LocalTime.of(9, 30));
  }

  // === Вспомогательные методы ===

  private void saveBellScheduleEntry(UUID universityId, int pairNumber, LocalTime startTime, LocalTime endTime) {
    BellScheduleEntry entry = new BellScheduleEntry();
    entry.setUniversity(universityId != null ? universityRepository.getReferenceById(universityId) : null);
    entry.setPairNumber(pairNumber);
    entry.setStartTime(startTime);
    entry.setEndTime(endTime);
    bellScheduleEntryRepository.save(entry);
  }

  private PairDto createDto(Set<UUID> groupIds) {
    return PairDto.builder()
        .weekScheduleCycleId(weekScheduleCycleId)
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekParity(WeekParity.ODD)
        .pairNumber(1)
        .startTime(LocalTime.of(8, 0))
        .endTime(LocalTime.of(9, 30))
        .courseId(courseId)
        .groupIds(groupIds)
        .build();
  }
}
