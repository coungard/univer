package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.TestRegions;
import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.GroupDto;
import com.coungard.univer.dto.SemesterType;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ConflictException;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.GroupRepository;
import com.coungard.univer.repository.SemesterRepository;
import com.coungard.univer.repository.StudyYearRepository;
import com.coungard.univer.repository.RegionRepository;
import com.coungard.univer.repository.UniversityRepository;
import java.time.LocalDate;
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
class GroupServiceTest {

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
  private GroupService groupService;

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private SemesterRepository semesterRepository;

  @Autowired
  private StudyYearRepository studyYearRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private UniversityRepository universityRepository;

  @Autowired
  private RegionRepository regionRepository;

  private UUID universityId;

  private UUID semesterId;

  @BeforeEach
  void setUp() {
    groupRepository.deleteAll();
    semesterRepository.deleteAll();
    studyYearRepository.deleteAll();
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

    StudyYear studyYear = new StudyYear();
    studyYear.setFacultyId(facultyId);
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
  void shouldCreateAndRetrieveGroup() {
    // Given
    GroupDto dto = GroupDto.builder().semesterId(semesterId).name("У532 КСиТ").build();

    // When
    GroupDto created = groupService.createGroup(dto);
    GroupDto found = groupService.getGroupById(created.id());

    // Then
    assertThat(found).isNotNull();
    assertThat(found.semesterId()).isEqualTo(semesterId);
    assertThat(found.name()).isEqualTo("У532 КСиТ");
  }

  @Test
  void shouldStoreFullNameAndTrimName() {
    GroupDto created = groupService.createGroup(GroupDto.builder()
        .semesterId(semesterId)
        .name(" У530 ")
        .fullName("Разработка программных и информационных систем")
        .build());

    assertThat(created.name()).isEqualTo("У530");
    assertThat(created.fullName()).isEqualTo("Разработка программных и информационных систем");

    GroupDto updated = groupService.updateGroup(created.id(),
        GroupDto.builder().semesterId(semesterId).name("У530").fullName("  ").build());
    assertThat(updated.fullName()).isNull();
  }

  @Test
  void shouldRejectDuplicateNameInSameSemester() {
    GroupDto first = groupService.createGroup(GroupDto.builder().semesterId(semesterId).name("У532 КСиТ").build());
    GroupDto second = groupService.createGroup(GroupDto.builder().semesterId(semesterId).name("У533 КСиТ").build());

    assertThatThrownBy(
        () -> groupService.createGroup(GroupDto.builder().semesterId(semesterId).name("у532 ксит ").build()))
        .isInstanceOfSatisfying(ConflictException.class, ex -> {
          assertThat(ex.getField()).isEqualTo("name");
          assertThat(ex.getExistingId()).isEqualTo(first.id());
        });
    assertThatThrownBy(() -> groupService.updateGroup(second.id(),
        GroupDto.builder().semesterId(semesterId).name("У532 КСИТ").build()))
        .isInstanceOf(ConflictException.class);

    // Сама себе группа не мешает: то же название при обновлении допустимо
    GroupDto renamed = groupService.updateGroup(first.id(),
        GroupDto.builder().semesterId(semesterId).name("у532 КСиТ").build());
    assertThat(renamed.name()).isEqualTo("у532 КСиТ");
  }

  @Test
  void shouldThrowExceptionWhenCreatingGroupWithNonExistentSemester() {
    GroupDto dto = GroupDto.builder().semesterId(UUID.randomUUID()).name("У532 КСиТ").build();

    assertThatThrownBy(() -> groupService.createGroup(dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenGroupNotFound() {
    UUID randomId = UUID.randomUUID();
    assertThatThrownBy(() -> groupService.getGroupById(randomId))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldGetGroups() {
    // Given
    groupService.createGroup(GroupDto.builder().semesterId(semesterId).name("У532 КСиТ").build());
    groupService.createGroup(GroupDto.builder().semesterId(semesterId).name("У533 КСиТ").build());

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<GroupDto> result = groupService.getGroups(pageable);

    // Then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent())
        .extracting(GroupDto::name)
        .containsExactlyInAnyOrder("У532 КСиТ", "У533 КСиТ");
  }

  @Test
  void shouldGetGroupsBySemester() {
    // Given
    groupService.createGroup(GroupDto.builder().semesterId(semesterId).name("У532 КСиТ").build());

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<GroupDto> result = groupService.getGroupsBySemester(semesterId, pageable);

    // Then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).name()).isEqualTo("У532 КСиТ");
  }

  @Test
  void shouldUpdateGroup() {
    // Given
    GroupDto original = groupService.createGroup(
        GroupDto.builder().semesterId(semesterId).name("У532 КСиТ").build());

    GroupDto updateDto = GroupDto.builder().semesterId(semesterId).name("У532 КСиТ (переим.)").build();

    // When
    GroupDto updated = groupService.updateGroup(original.id(), updateDto);

    // Then
    assertThat(updated.name()).isEqualTo("У532 КСиТ (переим.)");
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentGroup() {
    GroupDto dto = GroupDto.builder().semesterId(semesterId).name("У532 КСиТ").build();

    assertThatThrownBy(() -> groupService.updateGroup(UUID.randomUUID(), dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldDeleteGroupById() {
    // Given
    GroupDto group = groupService.createGroup(
        GroupDto.builder().semesterId(semesterId).name("У532 КСиТ").build());

    // When
    groupService.deleteGroup(group.id());

    // Then
    assertThat(groupRepository.findById(group.id())).isEmpty();
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentGroup() {
    assertThatThrownBy(() -> groupService.deleteGroup(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  // === GET /groups?facultyId=&yearNumber= ===

  @Test
  void shouldGetGroupsOfFacultyAndYear() {
    // Given: две группы факультета на 2 курсе, плюс 3 курс и чужой факультет
    UUID facultyId = createFaculty();
    UUID secondYearSemesterId = currentSemester(createStudyYear(facultyId, 2));
    createGroup(secondYearSemesterId, "А-21");
    createGroup(secondYearSemesterId, "Б-21");
    createGroup(currentSemester(createStudyYear(facultyId, 3)), "А-31");
    createGroup(currentSemester(createStudyYear(createFaculty(), 2)), "Чужая-21");

    // When
    Page<GroupDto> result = groupService.getGroups(facultyId, 2, PageRequest.of(0, 10));

    // Then
    assertThat(result.getContent()).extracting(GroupDto::name).containsExactly("А-21", "Б-21");
    assertThat(result.getTotalElements()).isEqualTo(2);
  }

  @Test
  void shouldFilterByFacultyOrYearAlone() {
    UUID facultyId = createFaculty();
    createGroup(currentSemester(createStudyYear(facultyId, 1)), "А-11");
    createGroup(currentSemester(createStudyYear(facultyId, 2)), "А-21");
    createGroup(currentSemester(createStudyYear(createFaculty(), 2)), "Чужая-21");

    assertThat(groupService.getGroups(facultyId, null, PageRequest.of(0, 10)).getContent())
        .extracting(GroupDto::name).containsExactly("А-11", "А-21");
    assertThat(groupService.getGroups(null, 2, PageRequest.of(0, 10)).getContent())
        .extracting(GroupDto::name).containsExactly("А-21", "Чужая-21");
  }

  @Test
  void shouldReturnAllGroupsWhenNoFilterGiven() {
    // Без фильтров правило актуального семестра не применяется — прежнее поведение
    UUID studyYearId = createStudyYear(createFaculty(), 2);
    LocalDate today = LocalDate.now();
    createGroup(createSemester(studyYearId, today.minusYears(1), today.minusMonths(8)), "Прошлая");
    createGroup(currentSemester(studyYearId), "Текущая");

    Page<GroupDto> result = groupService.getGroups(null, null, PageRequest.of(0, 10));

    assertThat(result.getContent()).extracting(GroupDto::name).containsExactlyInAnyOrder("Прошлая", "Текущая");
  }

  @Test
  void shouldReturnOnlyGroupsOfSemesterRunningToday() {
    UUID facultyId = createFaculty();
    UUID studyYearId = createStudyYear(facultyId, 2);
    LocalDate today = LocalDate.now();
    createGroup(createSemester(studyYearId, today.minusMonths(8), today.minusMonths(4)), "Прошлая");
    // Границы семестра включаются: сегодня — его первый день
    createGroup(createSemester(studyYearId, today, today.plusMonths(3)), "Текущая");
    createGroup(createSemester(studyYearId, today.plusMonths(5), today.plusMonths(9)), "Будущая");

    Page<GroupDto> result = groupService.getGroups(facultyId, 2, PageRequest.of(0, 10));

    assertThat(result.getContent()).extracting(GroupDto::name).containsExactly("Текущая");
  }

  @Test
  void shouldReturnGroupsOfNearestUpcomingSemesterBetweenSemesters() {
    // Каникулы: прошлый семестр закончился, следующие ещё не начались — берём ближайший из них
    UUID facultyId = createFaculty();
    UUID studyYearId = createStudyYear(facultyId, 2);
    LocalDate today = LocalDate.now();
    createGroup(createSemester(studyYearId, today.minusMonths(5), today.minusDays(10)), "Прошлая");
    createGroup(createSemester(studyYearId, today.plusDays(20), today.plusMonths(4)), "Ближайшая");
    createGroup(createSemester(studyYearId, today.plusMonths(6), today.plusMonths(10)), "Дальняя");

    Page<GroupDto> result = groupService.getGroups(facultyId, 2, PageRequest.of(0, 10));

    assertThat(result.getContent()).extracting(GroupDto::name).containsExactly("Ближайшая");
  }

  @Test
  void shouldFallBackToLatestFinishedSemesterWhenNoUpcomingOne() {
    // Следующий семестр ещё не заведён в справочнике — показываем последний закончившийся
    UUID facultyId = createFaculty();
    UUID studyYearId = createStudyYear(facultyId, 2);
    LocalDate today = LocalDate.now();
    createGroup(createSemester(studyYearId, today.minusMonths(12), today.minusMonths(8)), "Давняя");
    createGroup(createSemester(studyYearId, today.minusMonths(5), today.minusDays(10)), "Последняя");

    Page<GroupDto> result = groupService.getGroups(facultyId, 2, PageRequest.of(0, 10));

    assertThat(result.getContent()).extracting(GroupDto::name).containsExactly("Последняя");
  }

  @Test
  void shouldChooseSemesterIndependentlyForEachStudyYear() {
    // У одного курса семестр идёт, у другого — каникулы: каждый даёт свой актуальный семестр
    UUID facultyId = createFaculty();
    UUID runningYearId = createStudyYear(facultyId, 2);
    UUID vacationYearId = createStudyYear(facultyId, 3);
    LocalDate today = LocalDate.now();
    createGroup(currentSemester(runningYearId), "Идёт");
    createGroup(createSemester(runningYearId, today.plusMonths(5), today.plusMonths(9)), "Идёт-следующий");
    createGroup(createSemester(vacationYearId, today.plusDays(20), today.plusMonths(4)), "Каникулы-следующий");

    Page<GroupDto> result = groupService.getGroups(facultyId, null, PageRequest.of(0, 10));

    assertThat(result.getContent()).extracting(GroupDto::name).containsExactly("Идёт", "Каникулы-следующий");
  }

  @Test
  void shouldReturnEmptyPageWhenNoGroupsMatch() {
    UUID facultyId = createFaculty();
    createGroup(currentSemester(createStudyYear(facultyId, 2)), "А-21");

    assertThat(groupService.getGroups(facultyId, 4, PageRequest.of(0, 10)).getContent()).isEmpty();
    assertThat(groupService.getGroups(UUID.randomUUID(), 2, PageRequest.of(0, 10)).getContent()).isEmpty();
  }

  @Test
  void shouldPaginateFilteredGroups() {
    UUID facultyId = createFaculty();
    UUID currentSemesterId = currentSemester(createStudyYear(facultyId, 2));
    createGroup(currentSemesterId, "А-21");
    createGroup(currentSemesterId, "Б-21");
    createGroup(currentSemesterId, "В-21");

    Page<GroupDto> secondPage = groupService.getGroups(facultyId, 2, PageRequest.of(1, 2));

    assertThat(secondPage.getTotalElements()).isEqualTo(3);
    assertThat(secondPage.getContent()).extracting(GroupDto::name).containsExactly("В-21");
  }

  // === Вспомогательные методы ===

  private UUID createFaculty() {
    Faculty faculty = Faculty.builder()
        .name("Faculty " + UUID.randomUUID())
        .university(universityRepository.getReferenceById(universityId))
        .build();
    return facultyRepository.save(faculty).getId();
  }

  /** Учебный год факультета с заданным номером курса. */
  private UUID createStudyYear(UUID facultyId, int yearNumber) {
    StudyYear studyYear = new StudyYear();
    studyYear.setFacultyId(facultyId);
    studyYear.setYearNumber(yearNumber);
    return studyYearRepository.save(studyYear).getId();
  }

  private UUID createSemester(UUID studyYearId, LocalDate startDate, LocalDate endDate) {
    Semester semester = new Semester();
    semester.setStudyYear(studyYearRepository.getReferenceById(studyYearId));
    semester.setType(SemesterType.AUTUMN);
    semester.setStartDate(startDate);
    semester.setEndDate(endDate);
    return semesterRepository.save(semester).getId();
  }

  private UUID currentSemester(UUID studyYearId) {
    LocalDate today = LocalDate.now();
    return createSemester(studyYearId, today.minusMonths(1), today.plusMonths(3));
  }

  private void createGroup(UUID semesterId, String name) {
    groupService.createGroup(GroupDto.builder().semesterId(semesterId).name(name).build());
  }
}
