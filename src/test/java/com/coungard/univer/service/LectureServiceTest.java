package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.EducationForm;
import com.coungard.univer.dto.LectureDto;
import com.coungard.univer.dto.SemesterType;
import com.coungard.univer.dto.WeekParity;
import com.coungard.univer.dto.request.GenerateLectureRequest;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Department;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Pair;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Program;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.entity.University;
import com.coungard.univer.entity.WeekScheduleCycle;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.DepartmentRepository;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.GroupRepository;
import com.coungard.univer.repository.LectureRepository;
import com.coungard.univer.repository.PairRepository;
import com.coungard.univer.repository.ProgramRepository;
import com.coungard.univer.repository.SemesterRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.StudyYearRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.repository.WeekScheduleCycleRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
  private ProgramRepository programRepository;

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private UniversityRepository universityRepository;

  @Autowired
  private StudentRepository studentRepository;

  // Семестр начинается во вторник 2026-09-01. Неделя считается 7-дневными блоками от этой даты:
  // 1-7 сент. - неделя 1 (нечётная), 8-14 сент. - неделя 2 (чётная). Первый понедельник семестра —
  // 2026-09-07 (нечётная неделя), второй — 2026-09-14 (чётная), третий — 2026-09-21 (снова нечётная).
  // Семестр заканчивается 2026-09-21 — ровно три понедельника в диапазоне для тестов массовой генерации.
  private static final LocalDate SEMESTER_START = LocalDate.of(2026, 9, 1);
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
    studentRepository.deleteAll();
    lectureRepository.deleteAll();
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

  // === Вспомогательные методы ===

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
