package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.EducationForm;
import com.coungard.univer.dto.PairDto;
import com.coungard.univer.dto.SemesterType;
import com.coungard.univer.dto.WeekParity;
import com.coungard.univer.dto.WeekScheduleCycleStatus;
import com.coungard.univer.entity.BellScheduleEntry;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Department;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Program;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.entity.Teacher;
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
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.StudyYearRepository;
import com.coungard.univer.repository.TeacherRepository;
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

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private TeacherRepository teacherRepository;

  private UUID universityId;
  private UUID weekScheduleCycleId;
  private UUID courseId;
  private UUID group1Id;
  private UUID group2Id;

  @BeforeEach
  void setUp() {
    studentRepository.deleteAll();
    bellScheduleEntryRepository.deleteAll();
    pairRepository.deleteAll();
    teacherRepository.deleteAll();
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
    PairDto created = pairService.createPair(dto, null);
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
    PairDto created = pairService.createPair(dto, null);

    // Then
    assertThat(created.groupIds()).containsExactlyInAnyOrder(group1Id, group2Id);
  }

  @Test
  void shouldThrowExceptionWhenCreatingPairWithNonExistentCycle() {
    PairDto dto = createDto(Set.of(group1Id)).toBuilder().weekScheduleCycleId(UUID.randomUUID()).build();

    assertThatThrownBy(() -> pairService.createPair(dto, null))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenCreatingPairWithNonExistentCourse() {
    PairDto dto = createDto(Set.of(group1Id)).toBuilder().courseId(UUID.randomUUID()).build();

    assertThatThrownBy(() -> pairService.createPair(dto, null))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenCreatingPairWithNonExistentGroup() {
    PairDto dto = createDto(Set.of(UUID.randomUUID()));

    assertThatThrownBy(() -> pairService.createPair(dto, null))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenDayOfWeekIsWeekend() {
    PairDto dto = createDto(Set.of(group1Id)).toBuilder().dayOfWeek(DayOfWeek.SATURDAY).build();

    assertThatThrownBy(() -> pairService.createPair(dto, null))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowExceptionWhenEndTimeBeforeStartTime() {
    PairDto dto = createDto(Set.of(group1Id)).toBuilder()
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(9, 0))
        .build();

    assertThatThrownBy(() -> pairService.createPair(dto, null))
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
    pairService.createPair(createDto(Set.of(group1Id)), null);
    pairService.createPair(createDto(Set.of(group2Id)).toBuilder().dayOfWeek(DayOfWeek.TUESDAY).build(), null);

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<PairDto> result = pairService.getPairs(pageable);

    // Then
    assertThat(result.getContent()).hasSize(2);
  }

  @Test
  void shouldGetPairsByWeekScheduleCycle() {
    // Given
    pairService.createPair(createDto(Set.of(group1Id)), null);

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<PairDto> result = pairService.getPairsByWeekScheduleCycle(weekScheduleCycleId, pageable);

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldGetPairsByGroupIncludingStreamPairs() {
    // Given: поточная пара видна в расписании обеих групп
    pairService.createPair(createDto(Set.of(group1Id, group2Id)), null);

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
    PairDto original = pairService.createPair(createDto(Set.of(group1Id)), null);

    PairDto updateDto = original.toBuilder()
        .dayOfWeek(DayOfWeek.WEDNESDAY)
        .weekParity(WeekParity.EVEN)
        .groupIds(Set.of(group1Id, group2Id))
        .build();

    // When
    PairDto updated = pairService.updatePair(original.id(), updateDto, null);

    // Then
    assertThat(updated.dayOfWeek()).isEqualTo(DayOfWeek.WEDNESDAY);
    assertThat(updated.weekParity()).isEqualTo(WeekParity.EVEN);
    assertThat(updated.groupIds()).containsExactlyInAnyOrder(group1Id, group2Id);
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentPair() {
    PairDto dto = createDto(Set.of(group1Id));

    assertThatThrownBy(() -> pairService.updatePair(UUID.randomUUID(), dto, null))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldDeletePairById() {
    // Given
    PairDto pair = pairService.createPair(createDto(Set.of(group1Id)), null);

    // When
    pairService.deletePair(pair.id(), null);

    // Then
    assertThat(pairRepository.findById(pair.id())).isEmpty();
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentPair() {
    assertThatThrownBy(() -> pairService.deletePair(UUID.randomUUID(), null))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldPopulateStartAndEndTimeFromBellScheduleWhenOmitted() {
    // Given: справочная запись для этого университета и pairNumber=1
    saveBellScheduleEntry(universityId, 1, LocalTime.of(9, 0), LocalTime.of(10, 30));
    PairDto dto = createDto(Set.of(group1Id)).toBuilder().startTime(null).endTime(null).build();

    // When
    PairDto created = pairService.createPair(dto, null);

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
    PairDto created = pairService.createPair(dto, null);

    // Then
    assertThat(created.startTime()).isEqualTo(LocalTime.of(9, 0));
    assertThat(created.endTime()).isEqualTo(LocalTime.of(10, 30));
  }

  @Test
  void shouldThrowValidationExceptionWhenTimesOmittedAndNoBellScheduleEntryExists() {
    // Given: справочник пуст
    PairDto dto = createDto(Set.of(group1Id)).toBuilder().startTime(null).endTime(null).build();

    assertThatThrownBy(() -> pairService.createPair(dto, null))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldAllowExplicitTimesToOverrideBellScheduleEntry() {
    // Given: в справочнике другое время — явно заданное в DTO должно победить (перенос занятия)
    saveBellScheduleEntry(universityId, 1, LocalTime.of(9, 0), LocalTime.of(10, 30));
    PairDto dto = createDto(Set.of(group1Id));

    // When
    PairDto created = pairService.createPair(dto, null);

    // Then: остались явные времена из createDto (8:00-9:30), а не из справочника
    assertThat(created.startTime()).isEqualTo(LocalTime.of(8, 0));
    assertThat(created.endTime()).isEqualTo(LocalTime.of(9, 30));
  }

  // === STUDENT-ограничения: своя группа + цикл в DRAFT (issue #60/#64) ===

  @Test
  void shouldAllowStudentToCreatePairForOwnGroupWhenCycleIsDraft() {
    // Given
    UUID studentId = createStudent(group1Id);
    PairDto dto = createDto(Set.of(group1Id));

    // When
    PairDto created = pairService.createPair(dto, studentId);

    // Then
    assertThat(created.groupIds()).containsExactly(group1Id);
  }

  @Test
  void shouldRejectStudentCreatingPairForForeignGroup() {
    // Given
    UUID studentId = createStudent(group1Id);
    PairDto dto = createDto(Set.of(group2Id));

    // When & Then
    assertThatThrownBy(() -> pairService.createPair(dto, studentId))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("своей группы");
  }

  @Test
  void shouldRejectStudentCreatingStreamPairEvenIncludingOwnGroup() {
    // Given: студент не может создать поток на несколько групп, даже включив свою
    UUID studentId = createStudent(group1Id);
    PairDto dto = createDto(Set.of(group1Id, group2Id));

    // When & Then
    assertThatThrownBy(() -> pairService.createPair(dto, studentId))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("своей группы");
  }

  @Test
  void shouldRejectStudentCreatingPairWhenCycleIsAgreed() {
    // Given
    UUID studentId = createStudent(group1Id);
    agreeCycle(weekScheduleCycleId);
    PairDto dto = createDto(Set.of(group1Id));

    // When & Then
    assertThatThrownBy(() -> pairService.createPair(dto, studentId))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("согласовано");
  }

  @Test
  void shouldAllowAdminToCreatePairWhenCycleIsAgreed() {
    // Given
    agreeCycle(weekScheduleCycleId);
    PairDto dto = createDto(Set.of(group1Id));

    // When
    PairDto created = pairService.createPair(dto, null);

    // Then
    assertThat(created.groupIds()).containsExactly(group1Id);
  }

  @Test
  void shouldRejectStudentUpdatingPairOfForeignGroup() {
    // Given: пара создана администратором для чужой (для студента) группы
    PairDto pair = pairService.createPair(createDto(Set.of(group2Id)), null);
    UUID studentId = createStudent(group1Id);

    // When & Then
    assertThatThrownBy(() -> pairService.updatePair(pair.id(), pair, studentId))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("своей группы");
  }

  @Test
  void shouldRejectStudentUpdatingPairAfterCycleBecameAgreed() {
    // Given: пара своей группы создана, пока цикл был DRAFT, но затем согласована
    PairDto pair = pairService.createPair(createDto(Set.of(group1Id)), null);
    UUID studentId = createStudent(group1Id);
    agreeCycle(weekScheduleCycleId);

    // When & Then
    assertThatThrownBy(() -> pairService.updatePair(pair.id(), pair, studentId))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("согласовано");
  }

  @Test
  void shouldRejectStudentDeletingPairOfForeignGroup() {
    // Given
    PairDto pair = pairService.createPair(createDto(Set.of(group2Id)), null);
    UUID studentId = createStudent(group1Id);

    // When & Then
    assertThatThrownBy(() -> pairService.deletePair(pair.id(), studentId))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("своей группы");
  }

  @Test
  void shouldRejectStudentDeletingPairWhenCycleIsAgreed() {
    // Given
    PairDto pair = pairService.createPair(createDto(Set.of(group1Id)), null);
    UUID studentId = createStudent(group1Id);
    agreeCycle(weekScheduleCycleId);

    // When & Then
    assertThatThrownBy(() -> pairService.deletePair(pair.id(), studentId))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("согласовано");
  }

  @Test
  void shouldThrowExceptionWhenCallerStudentDoesNotExist() {
    PairDto dto = createDto(Set.of(group1Id));

    assertThatThrownBy(() -> pairService.createPair(dto, UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  // === Проверка конфликтов преподаватель/аудитория (issue #62) ===

  @Test
  void shouldRejectCreatingPairWithConflictingTeacher() {
    // Given: пара группы 1 с преподавателем в этот слот
    UUID teacherId = createTeacher();
    pairService.createPair(createDto(Set.of(group1Id)).toBuilder().teacherId(teacherId).room("101").build(), null);

    // When & Then: та же чётность/день/время, тот же преподаватель, другая аудитория, другая группа
    PairDto conflicting = createDto(Set.of(group2Id)).toBuilder().teacherId(teacherId).room("202").build();
    assertThatThrownBy(() -> pairService.createPair(conflicting, null))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("Конфликт расписания");
  }

  @Test
  void shouldRejectCreatingPairWithConflictingRoom() {
    // Given
    UUID teacher1Id = createTeacher();
    UUID teacher2Id = createTeacher();
    pairService.createPair(createDto(Set.of(group1Id)).toBuilder().teacherId(teacher1Id).room("101").build(), null);

    // When & Then: та же чётность/день/время, та же аудитория, другой преподаватель
    PairDto conflicting = createDto(Set.of(group2Id)).toBuilder().teacherId(teacher2Id).room("101").build();
    assertThatThrownBy(() -> pairService.createPair(conflicting, null))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("Конфликт расписания");
  }

  @Test
  void shouldAllowConflictingTeacherOnDifferentWeekParity() {
    // Given: тот же преподаватель/время/день, но чередующиеся недели — реального конфликта нет
    UUID teacherId = createTeacher();
    pairService.createPair(
        createDto(Set.of(group1Id)).toBuilder().teacherId(teacherId).weekParity(WeekParity.ODD).build(), null);

    // When
    PairDto created = pairService.createPair(
        createDto(Set.of(group2Id)).toBuilder().teacherId(teacherId).weekParity(WeekParity.EVEN).build(), null);

    // Then
    assertThat(created).isNotNull();
  }

  @Test
  void shouldAllowConflictingTeacherOnNonOverlappingTime() {
    // Given
    UUID teacherId = createTeacher();
    pairService.createPair(createDto(Set.of(group1Id)).toBuilder().teacherId(teacherId).build(), null);

    // When: тот же день/чётность, но другой временной слот (createDto — 8:00-9:30)
    PairDto created = pairService.createPair(
        createDto(Set.of(group2Id)).toBuilder()
            .teacherId(teacherId)
            .startTime(LocalTime.of(9, 40))
            .endTime(LocalTime.of(11, 10))
            .build(),
        null);

    // Then
    assertThat(created).isNotNull();
  }

  @Test
  void shouldNotConflictWithItselfWhenUpdatingPairUnchanged() {
    // Given
    UUID teacherId = createTeacher();
    PairDto original = pairService.createPair(
        createDto(Set.of(group1Id)).toBuilder().teacherId(teacherId).room("101").build(), null);

    // When: обновление без изменения слота — не должно конфликтовать само с собой
    PairDto updated = pairService.updatePair(original.id(), original, null);

    // Then
    assertThat(updated).isNotNull();
  }

  @Test
  void shouldRejectUpdatingPairIntoConflictWithAnotherPair() {
    // Given: пара 1 занимает слот с преподавателем; пара 2 — в другой день, без конфликта
    UUID teacherId = createTeacher();
    pairService.createPair(createDto(Set.of(group1Id)).toBuilder().teacherId(teacherId).build(), null);
    PairDto other = pairService.createPair(
        createDto(Set.of(group2Id)).toBuilder().dayOfWeek(DayOfWeek.TUESDAY).build(), null);

    // When & Then: переносим пару 2 в тот же слот с тем же преподавателем
    PairDto movedIntoConflict = other.toBuilder().dayOfWeek(DayOfWeek.MONDAY).teacherId(teacherId).build();
    assertThatThrownBy(() -> pairService.updatePair(other.id(), movedIntoConflict, null))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("Конфликт расписания");
  }

  // === Вспомогательные методы ===

  private UUID createTeacher() {
    Teacher teacher = new Teacher();

    String uniqueSuffix = UUID.randomUUID().toString();
    Person person = new Person();
    person.setUsername("teacher_" + uniqueSuffix);
    person.setEmail("teacher_" + uniqueSuffix + "@example.com");
    person.setFirstname("Пётр");
    person.setLastname("Петров");
    person.setFullname("Пётр Петров");
    teacher.setPerson(person);

    return teacherRepository.save(teacher).getId();
  }

  private UUID createStudent(UUID groupId) {
    Student student = new Student();
    student.setUniversity(universityRepository.getReferenceById(universityId));
    student.setEnrollmentDate(LocalDate.of(2026, 9, 1));
    if (groupId != null) {
      student.setGroup(groupRepository.getReferenceById(groupId));
    }

    String uniqueSuffix = UUID.randomUUID().toString();
    Person person = new Person();
    person.setUsername("student_" + uniqueSuffix);
    person.setEmail("student_" + uniqueSuffix + "@example.com");
    person.setFirstname("Иван");
    person.setLastname("Иванов");
    person.setFullname("Иван Иванов");
    student.setPerson(person);

    return studentRepository.save(student).getId();
  }

  private void agreeCycle(UUID cycleId) {
    WeekScheduleCycle cycle = weekScheduleCycleRepository.findById(cycleId)
        .orElseThrow(() -> new IllegalStateException("Цикл не найден в тесте: " + cycleId));
    cycle.setStatus(WeekScheduleCycleStatus.AGREED);
    weekScheduleCycleRepository.save(cycle);
  }

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
