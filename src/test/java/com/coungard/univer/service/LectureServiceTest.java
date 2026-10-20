package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.TestRegions;
import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.AuditAction;
import com.coungard.univer.dto.AuditEntityType;
import com.coungard.univer.dto.LectureDto;
import com.coungard.univer.dto.PairDto;
import com.coungard.univer.dto.SemesterType;
import com.coungard.univer.dto.WeekParity;
import com.coungard.univer.dto.request.GenerateLectureRequest;
import com.coungard.univer.entity.AuditEvent;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Department;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Lecture;
import com.coungard.univer.entity.LectureAttendance;
import com.coungard.univer.entity.LectureSelfAttendance;
import com.coungard.univer.entity.Pair;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.entity.University;
import com.coungard.univer.entity.WeekScheduleCycle;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.repository.AuditEventRepository;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.DepartmentRepository;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.GroupRepository;
import com.coungard.univer.repository.LectureAttendanceRepository;
import com.coungard.univer.repository.LectureSelfAttendanceRepository;
import com.coungard.univer.repository.LectureRepository;
import com.coungard.univer.repository.PairRepository;
import com.coungard.univer.repository.SemesterRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.StudyYearRepository;
import com.coungard.univer.repository.RegionRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.repository.WeekScheduleCycleRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = UniverApplication.class)
@Testcontainers
class LectureServiceTest {

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
  private LectureService lectureService;

  @Autowired
  private LectureRepository lectureRepository;

  @Autowired
  private AuditEventRepository auditEventRepository;

  @Autowired
  private LectureAttendanceRepository lectureAttendanceRepository;

  @Autowired
  private LectureSelfAttendanceRepository lectureSelfAttendanceRepository;

  @Autowired
  private PairService pairService;

  @Autowired
  private PairRepository pairRepository;

  @Autowired
  private WeekScheduleCycleRepository weekScheduleCycleRepository;

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private SemesterRepository semesterRepository;

  @Autowired
  private StudyYearRepository studyYearRepository;

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private UniversityRepository universityRepository;

  @Autowired
  private RegionRepository regionRepository;

  @Autowired
  private StudentRepository studentRepository;

  // Семестр начинается в понедельник 2026-09-07. Недели календарные (пн–вс): 7-13 сент. - неделя 1
  // (нечётная), 14-20 сент. - неделя 2 (чётная). Первый понедельник семестра — 2026-09-07 (нечётная
  // неделя), второй — 2026-09-14 (чётная), третий — 2026-09-21 (снова нечётная). Семестр заканчивается
  // 2026-09-21 — ровно три понедельника в диапазоне для тестов массовой генерации. Семестр, начавшийся не
  // в понедельник, — см. тесты «семестр со среды» ниже.
  private static final LocalDate SEMESTER_START = LocalDate.of(2026, 9, 7);
  private static final LocalDate SEMESTER_END = LocalDate.of(2026, 9, 21);
  private static final LocalDate FIRST_MONDAY_ODD_WEEK = LocalDate.of(2026, 9, 7);
  private static final LocalDate SECOND_MONDAY_EVEN_WEEK = LocalDate.of(2026, 9, 14);
  private static final LocalDate THIRD_MONDAY_ODD_WEEK = LocalDate.of(2026, 9, 21);

  private UUID universityId;
  private UUID semesterId;
  private UUID courseId;
  private UUID group1Id;
  private UUID group2Id;
  private UUID pairId; // Понедельник, нечётная неделя
  private UUID cycleId;

  @BeforeEach
  void setUp() {
    auditEventRepository.deleteAll();
    lectureAttendanceRepository.deleteAll();
    lectureSelfAttendanceRepository.deleteAll();
    studentRepository.deleteAll();
    lectureRepository.deleteAll();
    pairRepository.deleteAll();
    weekScheduleCycleRepository.deleteAll();
    groupRepository.deleteAll();
    semesterRepository.deleteAll();
    studyYearRepository.deleteAll();
    courseRepository.deleteAll();
    departmentRepository.deleteAll();
    facultyRepository.deleteAll();
    universityRepository.deleteAll();

    University university = new University();
    university.setName("Test University");
    university.setRegion(TestRegions.create(regionRepository));
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

    StudyYear studyYear = new StudyYear();
    studyYear.setFacultyId(facultyId);
    studyYear.setYearNumber(1);
    UUID studyYearId = studyYearRepository.save(studyYear).getId();

    Semester semester = new Semester();
    semester.setStudyYear(studyYearRepository.getReferenceById(studyYearId));
    semester.setType(SemesterType.AUTUMN);
    semester.setStartDate(SEMESTER_START);
    semester.setEndDate(SEMESTER_END);
    semesterId = semesterRepository.save(semester).getId();

    WeekScheduleCycle cycle = new WeekScheduleCycle();
    cycle.setSemester(semesterRepository.getReferenceById(semesterId));
    cycleId = weekScheduleCycleRepository.save(cycle).getId();

    Group group1 = new Group();
    group1.setSemester(semesterRepository.getReferenceById(semesterId));
    group1.setName("У532 КСиТ");
    group1Id = groupRepository.save(group1).getId();

    Group group2 = new Group();
    group2.setSemester(semesterRepository.getReferenceById(semesterId));
    group2.setName("У533 КСиТ");
    group2Id = groupRepository.save(group2).getId();

    Pair pair = new Pair();
    pair.setWeekScheduleCycle(weekScheduleCycleRepository.getReferenceById(cycleId));
    pair.setDayOfWeek(DayOfWeek.MONDAY);
    pair.setWeekParity(WeekParity.ODD);
    pair.setPairNumber(1);
    pair.setStartTime(LocalTime.of(8, 0));
    pair.setEndTime(LocalTime.of(9, 30));
    pair.setCourse(courseRepository.getReferenceById(courseId));
    pair.setGroups(Set.of(
        groupRepository.findById(group1Id).orElseThrow(),
        groupRepository.findById(group2Id).orElseThrow()));
    pairId = pairRepository.save(pair).getId();
  }

  @Test
  void shouldCreateAndRetrieveLecture() {
    // Given
    LectureDto dto = createDto(Set.of(group1Id));

    // When
    LectureDto created = lectureService.createLecture(dto);
    LectureDto found = lectureService.getLectureById(created.id());

    // Then
    assertThat(found).isNotNull();
    assertThat(found.courseId()).isEqualTo(courseId);
    assertThat(found.groupIds()).containsExactlyInAnyOrder(group1Id);
    assertThat(found.sourcePairId()).isNull();
  }

  @Test
  void shouldCreateStreamLectureWithMultipleGroups() {
    // Given
    LectureDto dto = createDto(Set.of(group1Id, group2Id));

    // When
    LectureDto created = lectureService.createLecture(dto);

    // Then
    assertThat(created.groupIds()).containsExactlyInAnyOrder(group1Id, group2Id);
  }

  @Test
  void shouldThrowExceptionWhenCreatingLectureWithNonExistentCourse() {
    LectureDto dto = createDto(Set.of(group1Id)).toBuilder().courseId(UUID.randomUUID()).build();

    assertThatThrownBy(() -> lectureService.createLecture(dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenCreatingLectureWithNonExistentGroup() {
    LectureDto dto = createDto(Set.of(UUID.randomUUID()));

    assertThatThrownBy(() -> lectureService.createLecture(dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenLectureNotFound() {
    assertThatThrownBy(() -> lectureService.getLectureById(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldGetLectures() {
    // Given
    lectureService.createLecture(createDto(Set.of(group1Id)));
    lectureService.createLecture(createDto(Set.of(group2Id)));

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<LectureDto> result = lectureService.getLectures(pageable);

    // Then
    assertThat(result.getContent()).hasSize(2);
  }

  @Test
  void shouldGetLecturesByCourse() {
    // Given
    lectureService.createLecture(createDto(Set.of(group1Id)));

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<LectureDto> result = lectureService.getLecturesByCourse(courseId, pageable);

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldGetLecturesByGroupIncludingStreamLectures() {
    // Given: поточная лекция видна в расписании обеих групп
    lectureService.createLecture(createDto(Set.of(group1Id, group2Id)));

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<LectureDto> group1Lectures = lectureService.getLecturesByGroup(group1Id, pageable);
    Page<LectureDto> group2Lectures = lectureService.getLecturesByGroup(group2Id, pageable);

    // Then
    assertThat(group1Lectures.getContent()).hasSize(1);
    assertThat(group2Lectures.getContent()).hasSize(1);
    assertThat(group1Lectures.getContent().get(0).id()).isEqualTo(group2Lectures.getContent().get(0).id());
  }

  @Test
  void shouldGetMyLecturesOrderedByScheduledTimeForStudentWithGroup() {
    // Given: студент привязан к group1Id; лекции создаются в порядке, обратном хронологическому,
    // чтобы тест проверял реальную сортировку, а не порядок вставки в БД
    UUID studentId = createStudent(group1Id);

    LectureDto laterDto = createDto(Set.of(group1Id)).toBuilder()
        .scheduledTime(LocalDateTime.of(2026, 9, 8, 10, 0))
        .build();
    LectureDto earlierDto = createDto(Set.of(group1Id)).toBuilder()
        .scheduledTime(LocalDateTime.of(2026, 9, 1, 8, 0))
        .build();
    LectureDto otherGroupDto = createDto(Set.of(group2Id));

    lectureService.createLecture(laterDto);
    lectureService.createLecture(earlierDto);
    lectureService.createLecture(otherGroupDto);

    // Пагинация с сортировкой строится контроллером; здесь воспроизводим тот же Pageable,
    // чтобы проверить, что сервис/репозиторий её действительно применяют.
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "scheduledTime"));

    // When
    Page<LectureDto> result = lectureService.getMyLectures(studentId, pageable);

    // Then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().get(0).scheduledTime()).isEqualTo(earlierDto.scheduledTime());
    assertThat(result.getContent().get(1).scheduledTime()).isEqualTo(laterDto.scheduledTime());
  }

  @Test
  void shouldReturnEmptyPageWhenStudentHasNoGroup() {
    // Given
    UUID studentId = createStudent(null);
    lectureService.createLecture(createDto(Set.of(group1Id)));

    // When
    Page<LectureDto> result = lectureService.getMyLectures(studentId, PageRequest.of(0, 10));

    // Then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldReturnEmptyPageWhenStudentHasNoUniversity() {
    // Given: студент зарегистрировался, не выбрав университет
    UUID studentId = createStudent(null);
    Student student = studentRepository.findById(studentId).orElseThrow();
    student.setUniversity(null);
    studentRepository.save(student);
    lectureService.createLecture(createDto(Set.of(group1Id)));

    // When
    Page<LectureDto> result = lectureService.getMyLectures(studentId, PageRequest.of(0, 10));

    // Then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldThrowExceptionWhenGettingMyLecturesForNonExistentStudent() {
    assertThatThrownBy(() -> lectureService.getMyLectures(UUID.randomUUID(), PageRequest.of(0, 10)))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldUpdateLecture() {
    // Given
    LectureDto original = lectureService.createLecture(createDto(Set.of(group1Id)));

    LectureDto updateDto = original.toBuilder()
        .title("Updated title")
        .groupIds(Set.of(group1Id, group2Id))
        .build();

    // When
    LectureDto updated = lectureService.updateLecture(original.id(), updateDto);

    // Then
    assertThat(updated.title()).isEqualTo("Updated title");
    assertThat(updated.groupIds()).containsExactlyInAnyOrder(group1Id, group2Id);
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentLecture() {
    LectureDto dto = createDto(Set.of(group1Id));

    assertThatThrownBy(() -> lectureService.updateLecture(UUID.randomUUID(), dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldDeleteLectureById() {
    // Given
    LectureDto lecture = lectureService.createLecture(createDto(Set.of(group1Id)));

    // When
    lectureService.deleteLecture(lecture.id());

    // Then
    assertThat(lectureRepository.findById(lecture.id())).isEmpty();
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentLecture() {
    assertThatThrownBy(() -> lectureService.deleteLecture(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldGenerateLectureFromPairCopyingGroupsAndSchedule() {
    // Given: первый понедельник семестра приходится на нечётную неделю — как и пара
    GenerateLectureRequest request = new GenerateLectureRequest(pairId, FIRST_MONDAY_ODD_WEEK);

    // When
    LectureDto generated = lectureService.generateFromPair(request, null);

    // Then
    assertThat(generated.sourcePairId()).isEqualTo(pairId);
    assertThat(generated.courseId()).isEqualTo(courseId);
    assertThat(generated.groupIds()).containsExactlyInAnyOrder(group1Id, group2Id);
    assertThat(generated.scheduledTime()).isEqualTo(LocalDateTime.of(2026, 9, 7, 8, 0));
    assertThat(generated.durationMinutes()).isEqualTo(90);
  }

  @Test
  void shouldThrowExceptionWhenGeneratingWithWrongDayOfWeek() {
    // Given: вторник, а пара привязана к понедельнику
    GenerateLectureRequest request = new GenerateLectureRequest(pairId, FIRST_MONDAY_ODD_WEEK.plusDays(1));

    assertThatThrownBy(() -> lectureService.generateFromPair(request, null))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowExceptionWhenGeneratingWithWrongWeekParity() {
    // Given: второй понедельник семестра — чётная неделя, а пара рассчитана на нечётную
    GenerateLectureRequest request = new GenerateLectureRequest(pairId, SECOND_MONDAY_EVEN_WEEK);

    assertThatThrownBy(() -> lectureService.generateFromPair(request, null))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowExceptionWhenGeneratingDuplicateForSameDate() {
    // Given
    lectureService.generateFromPair(new GenerateLectureRequest(pairId, FIRST_MONDAY_ODD_WEEK), null);

    // When & Then
    assertThatThrownBy(
        () -> lectureService.generateFromPair(new GenerateLectureRequest(pairId, FIRST_MONDAY_ODD_WEEK), null))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowExceptionWhenGeneratingFromNonExistentPair() {
    GenerateLectureRequest request = new GenerateLectureRequest(UUID.randomUUID(), FIRST_MONDAY_ODD_WEEK);

    assertThatThrownBy(() -> lectureService.generateFromPair(request, null))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldGenerateSemesterLecturesOnlyForMatchingParityDates() {
    // Given: фикстурная пара — WeekParity.ODD, диапазон семестра — 3 понедельника (нечёт/чёт/нечёт)

    // When
    List<LectureDto> generated = lectureService.generateSemesterLectures(cycleId, null);

    // Then: сгенерированы только нечётные понедельники, чётный (14 сентября) пропущен
    assertThat(generated).hasSize(2);
    assertThat(generated)
        .extracting(LectureDto::scheduledTime)
        .containsExactlyInAnyOrder(
            LocalDateTime.of(FIRST_MONDAY_ODD_WEEK, LocalTime.of(8, 0)),
            LocalDateTime.of(THIRD_MONDAY_ODD_WEEK, LocalTime.of(8, 0)));
  }

  @Test
  void shouldSkipAlreadyGeneratedLecturesOnRepeatedCall() {
    // Given
    lectureService.generateSemesterLectures(cycleId, null);

    // When: повторный вызов — все даты этого цикла уже сгенерированы
    List<LectureDto> secondCall = lectureService.generateSemesterLectures(cycleId, null);

    // Then
    assertThat(secondCall).isEmpty();
  }

  @Test
  void shouldGenerateSemesterLecturesForBothParityPair() {
    // Given: вторая пара того же цикла, каждую неделю независимо от чётности
    Pair everyWeekPair = new Pair();
    everyWeekPair.setWeekScheduleCycle(weekScheduleCycleRepository.getReferenceById(cycleId));
    everyWeekPair.setDayOfWeek(DayOfWeek.MONDAY);
    everyWeekPair.setWeekParity(WeekParity.BOTH);
    everyWeekPair.setPairNumber(2);
    everyWeekPair.setStartTime(LocalTime.of(9, 40));
    everyWeekPair.setEndTime(LocalTime.of(11, 10));
    everyWeekPair.setCourse(courseRepository.getReferenceById(courseId));
    everyWeekPair.setGroups(Set.of(groupRepository.findById(group1Id).orElseThrow()));
    pairRepository.save(everyWeekPair);

    // When
    List<LectureDto> generated = lectureService.generateSemesterLectures(cycleId, null);

    // Then: фикстурная ODD-пара даёт 2 лекции (07, 21 сентября) + BOTH-пара даёт 3 (07, 14, 21) = 5
    assertThat(generated).hasSize(5);
  }

  @Test
  void shouldThrowExceptionWhenGeneratingForNonExistentWeekScheduleCycle() {
    assertThatThrownBy(() -> lectureService.generateSemesterLectures(UUID.randomUUID(), null))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  // === Чётность по календарным неделям, семестр не с понедельника (issue #106) ===

  @Test
  void shouldGenerateSemesterLecturesByCalendarWeeksWhenSemesterStartsOnWednesday() {
    // Given: семестр со среды 2026-09-02 — неделя 1 (нечётная) — 31 авг.–6 сент., пн–вт которой в семестр
    // не входят; неделя 2 (чётная) — 7-13 сент., неделя 3 (нечётная) — 14-20 сент.
    moveSemesterStart(LocalDate.of(2026, 9, 2));
    createPair(DayOfWeek.WEDNESDAY, WeekParity.ODD, 2);
    createPair(DayOfWeek.SUNDAY, WeekParity.ODD, 3);

    // When
    List<LectureDto> generated = lectureService.generateSemesterLectures(cycleId, null);

    // Then: ODD-пара по понедельникам — только 14 сент. (07 и 21 сент. — чётные недели); по средам —
    // 02 и 16 сент.; по воскресеньям — 06 и 20 сент.
    assertThat(generated)
        .extracting(lecture -> lecture.scheduledTime().toLocalDate())
        .containsExactlyInAnyOrder(
            LocalDate.of(2026, 9, 14),
            LocalDate.of(2026, 9, 2),
            LocalDate.of(2026, 9, 16),
            LocalDate.of(2026, 9, 6),
            LocalDate.of(2026, 9, 20));
  }

  @Test
  void shouldSwitchWeekParityBetweenSundayAndMonday() {
    // Given: семестр со среды; воскресенье 06 сент. — ещё первая (нечётная) неделя, понедельник
    // 07 сент. — уже вторая (чётная)
    moveSemesterStart(LocalDate.of(2026, 9, 2));
    UUID sundayPairId = createPair(DayOfWeek.SUNDAY, WeekParity.ODD, 2);

    // When & Then
    assertThat(lectureService.generateFromPair(
        new GenerateLectureRequest(sundayPairId, LocalDate.of(2026, 9, 6)), null).sourcePairId())
        .isEqualTo(sundayPairId);
    assertThatThrownBy(() -> lectureService.generateFromPair(
        new GenerateLectureRequest(pairId, LocalDate.of(2026, 9, 7)), null))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("EVEN");
  }

  @Test
  void shouldRejectGeneratingFromPairForDateOutsideSemester() {
    // Given: семестр со среды — понедельник 31 авг. той же недели в семестр не входит
    moveSemesterStart(LocalDate.of(2026, 9, 2));

    // When & Then
    assertThatThrownBy(() -> lectureService.generateFromPair(
        new GenerateLectureRequest(pairId, LocalDate.of(2026, 8, 31)), null))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("вне семестра");
    assertThatThrownBy(() -> lectureService.generateFromPair(
        new GenerateLectureRequest(pairId, SEMESTER_END.plusWeeks(2)), null))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("вне семестра");
  }

  // === STUDENT-ограничения генерации: своя группа (issue #61) ===

  @Test
  void shouldAllowStudentToGenerateFromPairForOwnGroup() {
    // Given: фикстурная pairId — поток на group1Id и group2Id
    UUID studentId = createStudent(group1Id);
    GenerateLectureRequest request = new GenerateLectureRequest(pairId, FIRST_MONDAY_ODD_WEEK);

    // When
    LectureDto generated = lectureService.generateFromPair(request, studentId);

    // Then
    assertThat(generated.sourcePairId()).isEqualTo(pairId);
  }

  @Test
  void shouldRejectStudentGeneratingFromPairForForeignGroup() {
    // Given: студент группы, не участвующей в этой Pair
    UUID group3Id = createGroup("Чужая группа");
    UUID studentId = createStudent(group3Id);
    GenerateLectureRequest request = new GenerateLectureRequest(pairId, FIRST_MONDAY_ODD_WEEK);

    // When & Then
    assertThatThrownBy(() -> lectureService.generateFromPair(request, studentId))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("своей группы");
  }

  @Test
  void shouldRejectStudentWithoutGroupGeneratingFromPair() {
    // Given
    UUID studentId = createStudent(null);
    GenerateLectureRequest request = new GenerateLectureRequest(pairId, FIRST_MONDAY_ODD_WEEK);

    // When & Then
    assertThatThrownBy(() -> lectureService.generateFromPair(request, studentId))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("не привязан к группе");
  }

  @Test
  void shouldThrowExceptionWhenCallerStudentDoesNotExist() {
    GenerateLectureRequest request = new GenerateLectureRequest(pairId, FIRST_MONDAY_ODD_WEEK);

    assertThatThrownBy(() -> lectureService.generateFromPair(request, UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldGenerateSemesterLecturesOnlyForStudentsOwnGroupPairs() {
    // Given: вторая пара того же цикла принадлежит только чужой (для студента) группе
    UUID group3Id = createGroup("Чужая группа");
    Pair foreignPair = new Pair();
    foreignPair.setWeekScheduleCycle(weekScheduleCycleRepository.getReferenceById(cycleId));
    foreignPair.setDayOfWeek(DayOfWeek.MONDAY);
    foreignPair.setWeekParity(WeekParity.ODD);
    foreignPair.setPairNumber(2);
    foreignPair.setStartTime(LocalTime.of(9, 40));
    foreignPair.setEndTime(LocalTime.of(11, 10));
    foreignPair.setCourse(courseRepository.getReferenceById(courseId));
    foreignPair.setGroups(Set.of(groupRepository.findById(group3Id).orElseThrow()));
    pairRepository.save(foreignPair);

    UUID studentId = createStudent(group1Id);

    // When: фикстурная pairId (group1+group2) даёт 2 лекции (07, 21 сентября); foreignPair (group3)
    // должна быть пропущена молча, а не дать ошибку
    List<LectureDto> generated = lectureService.generateSemesterLectures(cycleId, studentId);

    // Then
    assertThat(generated).hasSize(2);
    assertThat(lectureRepository.findAll())
        .allSatisfy(lecture -> assertThat(lecture.getSourcePair().getId()).isEqualTo(pairId));
  }

  // === Лекции следуют за изменением и удалением пары (issue #99) ===

  @Test
  void shouldRecreateGeneratedLecturesWhenPairIsMovedToAnotherDay() {
    // Given: из ODD-пары по понедельникам сгенерированы 2 лекции (07 и 21 сентября)
    lectureService.generateSemesterLectures(cycleId, null);
    PairDto pair = pairService.getPairById(pairId);

    // When: пара перенесена на вторник, каждую неделю, в другую аудиторию
    pairService.updatePair(pairId, pair.toBuilder()
        .dayOfWeek(DayOfWeek.TUESDAY)
        .weekParity(WeekParity.BOTH)
        .room("202")
        .build(), null);

    // Then: понедельников не осталось, лекции стоят по вторникам семестра (08 и 15 сентября)
    List<LectureDto> lectures = findLecturesOfPair(pairId);
    assertThat(lectures)
        .extracting(LectureDto::scheduledTime)
        .containsExactlyInAnyOrder(
            LocalDateTime.of(2026, 9, 8, 8, 0),
            LocalDateTime.of(2026, 9, 15, 8, 0));
    assertThat(lectures).extracting(LectureDto::room).containsOnly("202");
    assertThat(lectureRepository.count()).isEqualTo(2);

    // Повторная генерация ничего не добавляет
    assertThat(lectureService.generateSemesterLectures(cycleId, null)).isEmpty();
  }

  @Test
  void shouldNotLeaveLecturesAtOldTimeWhenPairTimeChanges() {
    // Given
    lectureService.generateSemesterLectures(cycleId, null);
    PairDto pair = pairService.getPairById(pairId);

    // When: тот же день, но другая пара по счёту и группа — уже не поток
    pairService.updatePair(pairId, pair.toBuilder()
        .pairNumber(2)
        .startTime(LocalTime.of(9, 40))
        .endTime(LocalTime.of(11, 40))
        .groupIds(Set.of(group1Id))
        .build(), null);

    // Then: те же даты, новое время, длительность и состав групп
    List<LectureDto> lectures = findLecturesOfPair(pairId);
    assertThat(lectures)
        .extracting(LectureDto::scheduledTime)
        .containsExactlyInAnyOrder(
            LocalDateTime.of(FIRST_MONDAY_ODD_WEEK, LocalTime.of(9, 40)),
            LocalDateTime.of(THIRD_MONDAY_ODD_WEEK, LocalTime.of(9, 40)));
    assertThat(lectures).extracting(LectureDto::durationMinutes).containsOnly(120);
    assertThat(lectures).allSatisfy(lecture -> assertThat(lecture.groupIds()).containsExactly(group1Id));
    assertThat(lectureService.generateSemesterLectures(cycleId, null)).isEmpty();
  }

  @Test
  void shouldNotGenerateLecturesOnPairUpdateWhenNoneWereGenerated() {
    // Given: лекции из пары ещё не генерировались
    PairDto pair = pairService.getPairById(pairId);

    // When
    pairService.updatePair(pairId, pair.toBuilder().dayOfWeek(DayOfWeek.TUESDAY).build(), null);

    // Then
    assertThat(lectureRepository.count()).isZero();
  }

  @Test
  void shouldDeleteGeneratedLecturesWhenPairIsDeleted() {
    // Given: 2 лекции из пары и одна, созданная вручную
    lectureService.generateSemesterLectures(cycleId, null);
    LectureDto manual = lectureService.createLecture(createDto(Set.of(group1Id)));

    // When
    pairService.deletePair(pairId, null);

    // Then: лекции пары удалены вместе с ней, ручная осталась
    assertThat(lectureRepository.findAll()).extracting(Lecture::getId).containsExactly(manual.id());
  }

  @Test
  void shouldKeepLecturesWithAttendanceWhenPairIsUpdatedAndDeleted() {
    // Given: по первой из двух лекций пары уже отмечена посещаемость
    lectureService.generateSemesterLectures(cycleId, null);
    LectureDto attended = findLecturesOfPair(pairId).stream()
        .filter(lecture -> lecture.scheduledTime().toLocalDate().equals(FIRST_MONDAY_ODD_WEEK))
        .findFirst()
        .orElseThrow();
    markAttendance(attended.id());
    PairDto pair = pairService.getPairById(pairId);

    // When: пара перенесена на вторник
    pairService.updatePair(pairId, pair.toBuilder().dayOfWeek(DayOfWeek.TUESDAY).build(), null);

    // Then: отмеченная лекция осталась как была, остальные — по нечётным вторникам (08 сентября)
    assertThat(findLecturesOfPair(pairId))
        .extracting(LectureDto::scheduledTime)
        .containsExactlyInAnyOrder(
            attended.scheduledTime(),
            LocalDateTime.of(2026, 9, 8, 8, 0));
    assertThat(lectureService.getLectureById(attended.id()).scheduledTime()).isEqualTo(attended.scheduledTime());

    // When: пара удалена
    pairService.deletePair(pairId, null);

    // Then: осталась только отмеченная лекция, уже без ссылки на пару
    assertThat(lectureRepository.findAll()).extracting(Lecture::getId).containsExactly(attended.id());
    assertThat(lectureService.getLectureById(attended.id()).sourcePairId()).isNull();
    assertThat(lectureAttendanceRepository.count()).isEqualTo(1);
  }

  @Test
  void shouldKeepLectureWithStudentOwnMarkWhenPairIsUpdated() {
    // Given: по первой лекции пары студент сам отметил, что был (issue #117)
    lectureService.generateSemesterLectures(cycleId, null);
    LectureDto marked = findLecturesOfPair(pairId).stream()
        .filter(lecture -> lecture.scheduledTime().toLocalDate().equals(FIRST_MONDAY_ODD_WEEK))
        .findFirst()
        .orElseThrow();
    LectureSelfAttendance mark = new LectureSelfAttendance();
    mark.setStudentId(createStudent(group1Id));
    mark.setLectureId(marked.id());
    mark.setAttended(true);
    lectureSelfAttendanceRepository.save(mark);
    PairDto pair = pairService.getPairById(pairId);

    // When: пара перенесена на вторник
    pairService.updatePair(pairId, pair.toBuilder().dayOfWeek(DayOfWeek.TUESDAY).build(), null);

    // Then: лекция с личной отметкой осталась как была — вместе с отметкой
    assertThat(findLecturesOfPair(pairId))
        .extracting(LectureDto::scheduledTime)
        .containsExactlyInAnyOrder(
            marked.scheduledTime(),
            LocalDateTime.of(2026, 9, 8, 8, 0));
    assertThat(lectureSelfAttendanceRepository.count()).isEqualTo(1);
  }

  @Test
  void shouldRecordLectureCountsOfPairUpdateAndDeleteInAuditLog() {
    // Given: 2 лекции пары (07 и 21 сентября), по первой отмечена посещаемость
    lectureService.generateSemesterLectures(cycleId, null);
    findLecturesOfPair(pairId).stream()
        .filter(lecture -> lecture.scheduledTime().toLocalDate().equals(FIRST_MONDAY_ODD_WEEK))
        .findFirst()
        .ifPresent(lecture -> markAttendance(lecture.id()));
    PairDto pair = pairService.getPairById(pairId);

    // When: пара перенесена на вторник, затем удалена
    pairService.updatePair(pairId, pair.toBuilder().dayOfWeek(DayOfWeek.TUESDAY).build(), null);
    AuditEvent updated = findSingleAuditEvent(AuditAction.UPDATE);
    pairService.deletePair(pairId, null);
    AuditEvent deleted = findSingleAuditEvent(AuditAction.DELETE);

    // Then: при переносе удалена лекция 21 сентября и создана 08 сентября; при удалении — удалена она
    assertThat(updated.getEntityType()).isEqualTo(AuditEntityType.PAIR);
    assertThat(updated.getEntityId()).isEqualTo(pairId);
    assertThat(updated.getDetails())
        .containsEntry("lecturesDeleted", 1)
        .containsEntry("lecturesCreated", 1);
    assertThat(updated.getDetails().get("before")).asInstanceOf(InstanceOfAssertFactories.MAP)
        .containsEntry("dayOfWeek", "MONDAY");
    assertThat(updated.getDetails().get("after")).asInstanceOf(InstanceOfAssertFactories.MAP)
        .containsEntry("dayOfWeek", "TUESDAY");

    assertThat(deleted.getEntityId()).isEqualTo(pairId);
    assertThat(deleted.getDetails()).containsEntry("lecturesDeleted", 1);
  }

  private AuditEvent findSingleAuditEvent(AuditAction action) {
    List<AuditEvent> events = auditEventRepository.findAll().stream()
        .filter(event -> event.getAction() == action)
        .toList();
    assertThat(events).hasSize(1);
    return events.get(0);
  }

  @Test
  void shouldNotCreateSecondLectureOnDateOfLectureWithAttendance() {
    // Given: по лекции 07 сентября отмечена посещаемость
    lectureService.generateSemesterLectures(cycleId, null);
    LectureDto attended = findLecturesOfPair(pairId).stream()
        .filter(lecture -> lecture.scheduledTime().toLocalDate().equals(FIRST_MONDAY_ODD_WEEK))
        .findFirst()
        .orElseThrow();
    markAttendance(attended.id());
    PairDto pair = pairService.getPairById(pairId);

    // When: у пары меняется только время
    pairService.updatePair(pairId, pair.toBuilder()
        .startTime(LocalTime.of(9, 40))
        .endTime(LocalTime.of(11, 10))
        .build(), null);

    // Then: 07 сентября — прежняя лекция в прежнее время, 21 сентября — пересоздана на новое
    assertThat(findLecturesOfPair(pairId))
        .extracting(LectureDto::scheduledTime)
        .containsExactlyInAnyOrder(
            LocalDateTime.of(FIRST_MONDAY_ODD_WEEK, LocalTime.of(8, 0)),
            LocalDateTime.of(THIRD_MONDAY_ODD_WEEK, LocalTime.of(9, 40)));
    assertThat(lectureService.generateSemesterLectures(cycleId, null)).isEmpty();
    assertThatThrownBy(() -> lectureService.generateFromPair(
        new GenerateLectureRequest(pairId, FIRST_MONDAY_ODD_WEEK), null))
        .isInstanceOf(ValidationException.class);
  }

  // === Преподаватель текстом переносится из пары в занятия (issue #104) ===

  @Test
  void shouldCopyTeacherNameFromPairToGeneratedLectures() {
    PairDto pair = pairService.getPairById(pairId);
    pairService.updatePair(pairId, pair.toBuilder().teacherName("Иванов И. И.").build(), null);

    List<LectureDto> generated = lectureService.generateSemesterLectures(cycleId, null);

    assertThat(generated).isNotEmpty();
    assertThat(generated).extracting(LectureDto::teacherName).containsOnly("Иванов И. И.");
  }

  @Test
  void shouldUpdateTeacherNameOfGeneratedLecturesWhenPairChanges() {
    lectureService.generateSemesterLectures(cycleId, null);
    PairDto pair = pairService.getPairById(pairId);

    pairService.updatePair(pairId, pair.toBuilder().teacherName("Сидорова А. В.").build(), null);

    assertThat(findLecturesOfPair(pairId)).isNotEmpty();
    assertThat(findLecturesOfPair(pairId)).extracting(LectureDto::teacherName).containsOnly("Сидорова А. В.");
  }

  // === Вспомогательные методы ===

  private void moveSemesterStart(LocalDate startDate) {
    Semester semester = semesterRepository.findById(semesterId).orElseThrow();
    semester.setStartDate(startDate);
    semesterRepository.save(semester);
  }

  private UUID createPair(DayOfWeek dayOfWeek, WeekParity weekParity, int pairNumber) {
    Pair pair = new Pair();
    pair.setWeekScheduleCycle(weekScheduleCycleRepository.getReferenceById(cycleId));
    pair.setDayOfWeek(dayOfWeek);
    pair.setWeekParity(weekParity);
    pair.setPairNumber(pairNumber);
    pair.setStartTime(LocalTime.of(8, 0));
    pair.setEndTime(LocalTime.of(9, 30));
    pair.setCourse(courseRepository.getReferenceById(courseId));
    pair.setGroups(Set.of(groupRepository.findById(group1Id).orElseThrow()));
    return pairRepository.save(pair).getId();
  }

  private List<LectureDto> findLecturesOfPair(UUID sourcePairId) {
    return lectureService.getLectures(PageRequest.of(0, 100)).getContent().stream()
        .filter(lecture -> sourcePairId.equals(lecture.sourcePairId()))
        .toList();
  }

  private void markAttendance(UUID lectureId) {
    LectureAttendance attendance = new LectureAttendance();
    attendance.setStudentId(createStudent(group1Id));
    attendance.setLectureId(lectureId);
    lectureAttendanceRepository.save(attendance);
  }

  private UUID createGroup(String name) {
    Group group = new Group();
    group.setSemester(semesterRepository.getReferenceById(semesterId));
    group.setName(name);
    return groupRepository.save(group).getId();
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

  private LectureDto createDto(Set<UUID> groupIds) {
    return LectureDto.builder()
        .title("Algorithms and Data Structures")
        .scheduledTime(LocalDateTime.of(2026, 9, 1, 8, 0))
        .durationMinutes(90)
        .courseId(courseId)
        .groupIds(groupIds)
        .build();
  }
}
