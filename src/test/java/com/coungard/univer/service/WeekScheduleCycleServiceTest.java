package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.TestRegions;
import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.AuditAction;
import com.coungard.univer.dto.AuditEntityType;
import com.coungard.univer.dto.SemesterType;
import com.coungard.univer.dto.WeekScheduleCycleDto;
import com.coungard.univer.dto.WeekScheduleCycleStatus;
import com.coungard.univer.entity.AuditEvent;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.entity.University;
import com.coungard.univer.entity.WeekScheduleCycle;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.filter.RequestIdFilter;
import com.coungard.univer.repository.AuditEventRepository;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.SemesterRepository;
import com.coungard.univer.repository.StudyYearRepository;
import com.coungard.univer.repository.RegionRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.repository.WeekScheduleCycleRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
  private FacultyRepository facultyRepository;

  @Autowired
  private UniversityRepository universityRepository;

  @Autowired
  private RegionRepository regionRepository;

  @Autowired
  private AuditEventRepository auditEventRepository;

  private UUID semesterId;

  @AfterEach
  void clearRequestContext() {
    SecurityContextHolder.clearContext();
    MDC.remove(RequestIdFilter.MDC_KEY);
  }

  @BeforeEach
  void setUp() {
    auditEventRepository.deleteAll();
    weekScheduleCycleRepository.deleteAll();
    semesterRepository.deleteAll();
    studyYearRepository.deleteAll();
    facultyRepository.deleteAll();
    universityRepository.deleteAll();

    University university = new University();
    university.setName("Test University");
    university.setRegion(TestRegions.create(regionRepository));
    UUID universityId = universityRepository.save(university).getId();

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

  @Test
  void shouldAlwaysCreateCycleInDraftStatus() {
    // Given: клиент присылает статус, отличный от DRAFT — должен быть проигнорирован
    WeekScheduleCycleDto dto = WeekScheduleCycleDto.builder()
        .semesterId(semesterId)
        .status(WeekScheduleCycleStatus.AGREED)
        .build();

    // When
    WeekScheduleCycleDto created = weekScheduleCycleService.createWeekScheduleCycle(dto);

    // Then
    assertThat(created.status()).isEqualTo(WeekScheduleCycleStatus.DRAFT);
  }

  @Test
  void shouldUpdateStatusFromDraftToAgreedAndBack() {
    // Given
    WeekScheduleCycleDto created = weekScheduleCycleService.createWeekScheduleCycle(
        WeekScheduleCycleDto.builder().semesterId(semesterId).build());
    assertThat(created.status()).isEqualTo(WeekScheduleCycleStatus.DRAFT);

    // When
    WeekScheduleCycleDto agreed = weekScheduleCycleService.updateStatus(
        created.id(), WeekScheduleCycleStatus.AGREED);

    // Then
    assertThat(agreed.status()).isEqualTo(WeekScheduleCycleStatus.AGREED);
    assertThat(weekScheduleCycleService.getWeekScheduleCycleById(created.id()).status())
        .isEqualTo(WeekScheduleCycleStatus.AGREED);

    // When: откат обратно в DRAFT для исправлений
    WeekScheduleCycleDto backToDraft = weekScheduleCycleService.updateStatus(
        created.id(), WeekScheduleCycleStatus.DRAFT);

    // Then
    assertThat(backToDraft.status()).isEqualTo(WeekScheduleCycleStatus.DRAFT);
  }

  @Test
  void shouldRecordStatusChangeWithActorAndRequestIdInAuditLog() {
    // Given: действует администратор, у которого есть и роль студента; запрос с известным request id
    UUID adminId = UUID.randomUUID();
    SecurityContextHolder.getContext().setAuthentication(
        new TestingAuthenticationToken(adminId.toString(), null, "ROLE_STUDENT", "ROLE_ADMIN"));
    MDC.put(RequestIdFilter.MDC_KEY, "req-cycle-1");
    WeekScheduleCycleDto cycle = weekScheduleCycleService.createWeekScheduleCycle(
        WeekScheduleCycleDto.builder().semesterId(semesterId).build());

    // When: перевод в AGREED и повторный перевод в тот же статус
    weekScheduleCycleService.updateStatus(cycle.id(), WeekScheduleCycleStatus.AGREED);
    weekScheduleCycleService.updateStatus(cycle.id(), WeekScheduleCycleStatus.AGREED);

    // Then: одно событие — повтор статус не меняет; роль — старшая из ролей автора
    List<AuditEvent> events = auditEventRepository.findAll();
    assertThat(events).hasSize(1);
    AuditEvent event = events.get(0);
    assertThat(event.getAction()).isEqualTo(AuditAction.STATUS_CHANGE);
    assertThat(event.getEntityType()).isEqualTo(AuditEntityType.WEEK_SCHEDULE_CYCLE);
    assertThat(event.getEntityId()).isEqualTo(cycle.id());
    assertThat(event.getActorId()).isEqualTo(adminId);
    assertThat(event.getActorRole()).isEqualTo("ADMIN");
    assertThat(event.getRequestId()).isEqualTo("req-cycle-1");
    assertThat(event.getOccurredAt()).isNotNull();
    assertThat(event.getDetails()).containsEntry("from", "DRAFT").containsEntry("to", "AGREED");
  }

  @Test
  void shouldRecordCycleDeletionWithoutActorOutsideRequest() {
    // Given
    WeekScheduleCycleDto cycle = weekScheduleCycleService.createWeekScheduleCycle(
        WeekScheduleCycleDto.builder().semesterId(semesterId).build());

    // When
    weekScheduleCycleService.deleteWeekScheduleCycle(cycle.id());

    // Then
    assertThat(auditEventRepository.findAll()).singleElement().satisfies(event -> {
      assertThat(event.getAction()).isEqualTo(AuditAction.DELETE);
      assertThat(event.getEntityId()).isEqualTo(cycle.id());
      assertThat(event.getActorId()).isNull();
      assertThat(event.getActorRole()).isNull();
      assertThat(event.getRequestId()).isNull();
      assertThat(event.getDetails()).isNull();
    });
  }

  @Test
  void shouldThrowExceptionWhenUpdatingStatusOfNonExistentCycle() {
    assertThatThrownBy(
        () -> weekScheduleCycleService.updateStatus(UUID.randomUUID(), WeekScheduleCycleStatus.AGREED))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  // === getOrCreateDraftCycle (issue #96) ===

  @Test
  void shouldCreateDraftCycleWhenSemesterHasNone() {
    // When
    WeekScheduleCycleDto created = weekScheduleCycleService.getOrCreateDraftCycle(semesterId);

    // Then
    assertThat(created.id()).isNotNull();
    assertThat(created.semesterId()).isEqualTo(semesterId);
    assertThat(created.status()).isEqualTo(WeekScheduleCycleStatus.DRAFT);
    assertThat(weekScheduleCycleService.getWeekScheduleCycleBySemester(semesterId).id()).isEqualTo(created.id());
  }

  @Test
  void shouldStampAuthorOfDraftCycleCreatedByNativeInsert() {
    // Given: цикл заводит студент — через нативный INSERT, мимо JPA auditing (issue #112)
    UUID studentId = UUID.randomUUID();
    SecurityContextHolder.getContext().setAuthentication(
        new TestingAuthenticationToken(studentId.toString(), null, "ROLE_STUDENT"));

    // When
    UUID cycleId = weekScheduleCycleService.getOrCreateDraftCycle(semesterId).id();

    // Then
    WeekScheduleCycle cycle = weekScheduleCycleRepository.findById(cycleId).orElseThrow();
    assertThat(cycle.getCreatedBy()).isEqualTo(studentId);
    assertThat(cycle.getUpdatedBy()).isEqualTo(studentId);
    assertThat(cycle.getCreatedAt()).isNotNull();
    assertThat(cycle.getUpdatedAt()).isNotNull();
  }

  @Test
  void shouldCreateDraftCycleWithoutAuthorOutsideRequest() {
    UUID cycleId = weekScheduleCycleService.getOrCreateDraftCycle(semesterId).id();

    WeekScheduleCycle cycle = weekScheduleCycleRepository.findById(cycleId).orElseThrow();
    assertThat(cycle.getCreatedBy()).isNull();
    assertThat(cycle.getCreatedAt()).isNotNull();
  }

  @Test
  void shouldReturnExistingCycleAsIsInsteadOfCreatingSecond() {
    // Given: цикл уже заведён и согласован администратором
    WeekScheduleCycleDto existing = weekScheduleCycleService.createWeekScheduleCycle(
        WeekScheduleCycleDto.builder().semesterId(semesterId).build());
    weekScheduleCycleService.updateStatus(existing.id(), WeekScheduleCycleStatus.AGREED);

    // When
    WeekScheduleCycleDto found = weekScheduleCycleService.getOrCreateDraftCycle(semesterId);

    // Then: тот же цикл, статус не сброшен в DRAFT
    assertThat(found.id()).isEqualTo(existing.id());
    assertThat(found.status()).isEqualTo(WeekScheduleCycleStatus.AGREED);
    assertThat(weekScheduleCycleRepository.count()).isEqualTo(1);
  }

  @Test
  void shouldCreateSingleCycleOnConcurrentRequests() throws Exception {
    // Given
    int threads = 8;
    ExecutorService executor = Executors.newFixedThreadPool(threads);
    CountDownLatch start = new CountDownLatch(1);

    // When: все потоки запрашивают цикл одного семестра одновременно
    List<Future<WeekScheduleCycleDto>> futures = new ArrayList<>();
    for (int i = 0; i < threads; i++) {
      futures.add(executor.submit(() -> {
        start.await();
        return weekScheduleCycleService.getOrCreateDraftCycle(semesterId);
      }));
    }
    start.countDown();

    // Then: ни один запрос не упал, все получили один и тот же цикл
    List<UUID> cycleIds = new ArrayList<>();
    for (Future<WeekScheduleCycleDto> future : futures) {
      cycleIds.add(future.get(30, TimeUnit.SECONDS).id());
    }
    executor.shutdown();

    assertThat(cycleIds).doesNotContainNull();
    assertThat(cycleIds).containsOnly(cycleIds.get(0));
    assertThat(weekScheduleCycleRepository.count()).isEqualTo(1);
  }

  @Test
  void shouldThrowExceptionWhenGettingOrCreatingCycleForNonExistentSemester() {
    assertThatThrownBy(() -> weekScheduleCycleService.getOrCreateDraftCycle(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class);
  }
}
