package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.TestRegions;
import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.StudentDto;
import com.coungard.univer.dto.UniversityRequestDto;
import com.coungard.univer.dto.UniversityRequestStatus;
import com.coungard.univer.dto.request.SubmitUniversityRequest;
import com.coungard.univer.dto.request.UpdateStudentProfileRequest;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Region;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.RegionRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.repository.UniversityRequestRepository;
import com.coungard.univer.security.KeycloakAdminService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = UniverApplication.class)
@Testcontainers
class UniversityRequestServiceTest {

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
  private UniversityRequestService universityRequestService;

  @Autowired
  private StudentService studentService;

  @Autowired
  private UniversityRequestRepository universityRequestRepository;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private UniversityRepository universityRepository;

  @Autowired
  private RegionRepository regionRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @MockBean
  private KeycloakAdminService keycloakAdminService;

  private static final Pageable OLDEST_FIRST = PageRequest.of(0, 10, Sort.by("createdAt").ascending());

  private Region region;
  private UUID universityId;
  private UUID studentId;

  @BeforeEach
  void setUp() {
    universityRequestRepository.deleteAll();
    studentRepository.deleteAll();
    facultyRepository.deleteAll();
    universityRepository.deleteAll();

    region = TestRegions.create(regionRepository);
    universityId = createUniversity("Test University").getId();
    studentId = createStudent("ivan");
  }

  @Test
  void shouldSubmitRequestWithoutChangingProfile() {
    UniversityRequestDto created = universityRequestService.submitMyRequest(studentId,
        new SubmitUniversityRequest("  Ненецкий аграрно-экономический техникум ", region.getId()));

    assertThat(created.id()).isNotNull();
    assertThat(created.name()).isEqualTo("Ненецкий аграрно-экономический техникум");
    assertThat(created.regionId()).isEqualTo(region.getId());
    assertThat(created.regionName()).isEqualTo(region.getName());
    assertThat(created.status()).isEqualTo(UniversityRequestStatus.PENDING);
    assertThat(created.universityId()).isNull();
    assertThat(created.studentId()).isEqualTo(studentId);
    assertThat(created.studentUsername()).isEqualTo("ivan");
    assertThat(created.createdAt()).isNotNull();

    assertThat(studentService.getStudentById(studentId).universityId()).isNull();
    assertThat(universityRequestService.getMyRequest(studentId).id()).isEqualTo(created.id());
  }

  @Test
  void shouldSubmitRequestWithoutRegion() {
    UniversityRequestDto created = universityRequestService.submitMyRequest(studentId,
        new SubmitUniversityRequest("Техникум", null));

    assertThat(created.regionId()).isNull();
    assertThat(created.regionName()).isNull();
  }

  @Test
  void shouldUpdatePendingRequestInsteadOfCreatingSecondOne() {
    UniversityRequestDto first = universityRequestService.submitMyRequest(studentId,
        new SubmitUniversityRequest("Техникум", region.getId()));

    UniversityRequestDto second = universityRequestService.submitMyRequest(studentId,
        new SubmitUniversityRequest("Аграрный техникум", null));

    assertThat(second.id()).isEqualTo(first.id());
    assertThat(second.name()).isEqualTo("Аграрный техникум");
    assertThat(second.regionId()).isNull();
    assertThat(universityRequestRepository.count()).isEqualTo(1);
  }

  @Test
  void shouldRejectSubmitWithUnknownRegionOrStudent() {
    assertThatThrownBy(() -> universityRequestService.submitMyRequest(studentId,
        new SubmitUniversityRequest("Техникум", UUID.randomUUID())))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Регион не найден");
    assertThatThrownBy(() -> universityRequestService.submitMyRequest(UUID.randomUUID(),
        new SubmitUniversityRequest("Техникум", null)))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Студент не найден");
    assertThat(universityRequestRepository.count()).isZero();
  }

  @Test
  void shouldRejectSubmitWhenUniversityAlreadyChosen() {
    studentService.updateMyProfile(studentId, universityProfile(universityId));

    assertThatThrownBy(() -> universityRequestService.submitMyRequest(studentId,
        new SubmitUniversityRequest("Техникум", null)))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowNotFoundWhenStudentHasNoRequests() {
    assertThatThrownBy(() -> universityRequestService.getMyRequest(studentId))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldCompleteRequestAndSetUniversityInAuthorProfile() {
    UniversityRequestDto request = universityRequestService.submitMyRequest(studentId,
        new SubmitUniversityRequest("Техникум", region.getId()));

    UniversityRequestDto completed = universityRequestService.completeRequest(request.id(), universityId);

    assertThat(completed.status()).isEqualTo(UniversityRequestStatus.COMPLETED);
    assertThat(completed.universityId()).isEqualTo(universityId);
    assertThat(studentService.getStudentById(studentId).universityId()).isEqualTo(universityId);

    UniversityRequestDto mine = universityRequestService.getMyRequest(studentId);
    assertThat(mine.status()).isEqualTo(UniversityRequestStatus.COMPLETED);
    assertThat(mine.universityId()).isEqualTo(universityId);
  }

  @Test
  void shouldRejectRequestWithoutChangingProfile() {
    UniversityRequestDto request = universityRequestService.submitMyRequest(studentId,
        new SubmitUniversityRequest("asdf", null));

    UniversityRequestDto rejected = universityRequestService.rejectRequest(
        request.id(), "  Это не университет ");

    assertThat(rejected.status()).isEqualTo(UniversityRequestStatus.REJECTED);
    assertThat(rejected.universityId()).isNull();
    assertThat(rejected.comment()).isEqualTo("Это не университет");
    assertThat(studentService.getStudentById(studentId).universityId()).isNull();
    assertThat(universityRequestService.getMyRequest(studentId).comment()).isEqualTo("Это не университет");
  }

  @Test
  void shouldRejectRequestWithoutComment() {
    UUID first = universityRequestService.submitMyRequest(studentId,
        new SubmitUniversityRequest("asdf", null)).id();
    UUID second = universityRequestService.submitMyRequest(createStudent("petr"),
        new SubmitUniversityRequest("asdf", null)).id();

    assertThat(universityRequestService.rejectRequest(first, null).comment()).isNull();
    assertThat(universityRequestService.rejectRequest(second, "   ").comment()).isNull();
  }

  @Test
  void shouldKeepSingleRequestWhenSubmittedConcurrently() throws Exception {
    // Схему в тестах создаёт Hibernate, а не Flyway — частичный уникальный индекс из V39 заводим сами
    jdbcTemplate.execute("CREATE UNIQUE INDEX IF NOT EXISTS uq_university_request_pending_student "
        + "ON university_requests(student_id) WHERE status = 'PENDING'");

    int threads = 8;
    ExecutorService executor = Executors.newFixedThreadPool(threads);
    CountDownLatch start = new CountDownLatch(1);
    try {
      List<Future<UniversityRequestDto>> results = new ArrayList<>();
      for (int i = 0; i < threads; i++) {
        results.add(executor.submit(() -> {
          start.await();
          return universityRequestService.submitMyRequest(studentId,
              new SubmitUniversityRequest("Техникум", region.getId()));
        }));
      }
      start.countDown();

      // Никто не падает с ошибкой, и все получают одну и ту же заявку
      Set<UUID> ids = new HashSet<>();
      for (Future<UniversityRequestDto> result : results) {
        ids.add(result.get(30, TimeUnit.SECONDS).id());
      }
      assertThat(ids).hasSize(1);
      assertThat(universityRequestRepository.count()).isEqualTo(1);
    } finally {
      executor.shutdownNow();
    }
  }

  @Test
  void shouldNotProcessRequestTwice() {
    UUID completedId = universityRequestService.submitMyRequest(studentId,
        new SubmitUniversityRequest("Техникум", null)).id();
    universityRequestService.completeRequest(completedId, universityId);

    UUID rejectedId = universityRequestService.submitMyRequest(createStudent("petr"),
        new SubmitUniversityRequest("Техникум", null)).id();
    universityRequestService.rejectRequest(rejectedId, null);

    assertThatThrownBy(() -> universityRequestService.rejectRequest(completedId, null))
        .isInstanceOf(ValidationException.class);
    assertThatThrownBy(() -> universityRequestService.completeRequest(rejectedId, universityId))
        .isInstanceOf(ValidationException.class);
    assertThat(universityRequestService.getMyRequest(studentId).status())
        .isEqualTo(UniversityRequestStatus.COMPLETED);
  }

  @Test
  void shouldThrowNotFoundForUnknownRequestOrUniversity() {
    UUID requestId = universityRequestService.submitMyRequest(studentId,
        new SubmitUniversityRequest("Техникум", null)).id();

    assertThatThrownBy(() -> universityRequestService.completeRequest(UUID.randomUUID(), universityId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Заявка не найдена");
    assertThatThrownBy(() -> universityRequestService.rejectRequest(UUID.randomUUID(), null))
        .isInstanceOf(ResourceNotFoundException.class);
    assertThatThrownBy(() -> universityRequestService.completeRequest(requestId, UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Университет не найден");
    assertThat(universityRequestService.getMyRequest(studentId).status())
        .isEqualTo(UniversityRequestStatus.PENDING);
  }

  @Test
  void shouldCreateNewRequestAfterPreviousOneIsRejected() {
    UUID rejectedId = universityRequestService.submitMyRequest(studentId,
        new SubmitUniversityRequest("asdf", null)).id();
    universityRequestService.rejectRequest(rejectedId, null);

    UniversityRequestDto next = universityRequestService.submitMyRequest(studentId,
        new SubmitUniversityRequest("Техникум", region.getId()));

    assertThat(next.id()).isNotEqualTo(rejectedId);
    assertThat(next.status()).isEqualTo(UniversityRequestStatus.PENDING);
    assertThat(universityRequestRepository.count()).isEqualTo(2);
    // Свою заявку студент видит последнюю, а отклонённая остаётся в истории
    assertThat(universityRequestService.getMyRequest(studentId).id()).isEqualTo(next.id());
    assertThat(universityRequestRepository.findById(rejectedId).orElseThrow().getStatus())
        .isEqualTo(UniversityRequestStatus.REJECTED);
  }

  @Test
  void shouldCreateNewRequestAfterPreviousOneIsCompletedAndUniversityCleared() {
    UUID completedId = universityRequestService.submitMyRequest(studentId,
        new SubmitUniversityRequest("Техникум", null)).id();
    universityRequestService.completeRequest(completedId, universityId);

    // Администратор закрыл заявку не тем университетом: студент снимает его и подаёт заявку заново
    studentService.updateMyProfile(studentId, universityProfile(null));
    UniversityRequestDto next = universityRequestService.submitMyRequest(studentId,
        new SubmitUniversityRequest("Аграрный техникум", null));

    assertThat(next.id()).isNotEqualTo(completedId);
    assertThat(next.status()).isEqualTo(UniversityRequestStatus.PENDING);
  }

  @Test
  void shouldCompletePendingRequestWhenStudentChoosesUniversityHimself() {
    UUID requestId = universityRequestService.submitMyRequest(studentId,
        new SubmitUniversityRequest("Техникум", null)).id();

    studentService.updateMyProfile(studentId, universityProfile(universityId));

    UniversityRequestDto closed = universityRequestService.getMyRequest(studentId);
    assertThat(closed.id()).isEqualTo(requestId);
    assertThat(closed.status()).isEqualTo(UniversityRequestStatus.COMPLETED);
    assertThat(closed.universityId()).isEqualTo(universityId);
    assertThatThrownBy(() -> universityRequestService.rejectRequest(requestId, null))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldCompletePendingRequestWhenUniversityComesFromChosenFaculty() {
    universityRequestService.submitMyRequest(studentId, new SubmitUniversityRequest("Техникум", null));
    UUID facultyId = facultyRepository.save(Faculty.builder()
        .name("Faculty of Computer Science")
        .university(universityRepository.getReferenceById(universityId))
        .build()).getId();

    UpdateStudentProfileRequest profile = new UpdateStudentProfileRequest();
    profile.setFacultyId(facultyId);
    studentService.updateMyProfile(studentId, profile);

    UniversityRequestDto closed = universityRequestService.getMyRequest(studentId);
    assertThat(closed.status()).isEqualTo(UniversityRequestStatus.COMPLETED);
    assertThat(closed.universityId()).isEqualTo(universityId);
  }

  @Test
  void shouldCompletePendingRequestWhenAdminAssignsUniversity() {
    universityRequestService.submitMyRequest(studentId, new SubmitUniversityRequest("Техникум", null));
    StudentDto student = studentService.getStudentById(studentId);

    studentService.updateStudent(studentId, StudentDto.builder()
        .username(student.username())
        .firstname(student.firstname())
        .lastname(student.lastname())
        .fullname(student.fullname())
        .email(student.email())
        .universityId(universityId)
        .build());

    assertThat(universityRequestService.getMyRequest(studentId).status())
        .isEqualTo(UniversityRequestStatus.COMPLETED);
  }

  @Test
  void shouldKeepRequestPendingWhileProfileChangesWithoutUniversity() {
    universityRequestService.submitMyRequest(studentId, new SubmitUniversityRequest("Техникум", null));

    studentService.updateMyProfile(studentId, universityProfile(null));

    assertThat(universityRequestService.getMyRequest(studentId).status())
        .isEqualTo(UniversityRequestStatus.PENDING);
  }

  @Test
  void shouldListRequestsOldestFirstWithStatusFilter() {
    UUID first = universityRequestService.submitMyRequest(studentId,
        new SubmitUniversityRequest("Первый техникум", region.getId())).id();
    UUID second = universityRequestService.submitMyRequest(createStudent("petr"),
        new SubmitUniversityRequest("Второй техникум", null)).id();
    UUID third = universityRequestService.submitMyRequest(createStudent("anna"),
        new SubmitUniversityRequest("asdf", null)).id();
    universityRequestService.completeRequest(first, universityId);
    universityRequestService.rejectRequest(third, null);

    Page<UniversityRequestDto> all = universityRequestService.getRequests(null, OLDEST_FIRST);
    assertThat(all.getContent()).extracting(UniversityRequestDto::id).containsExactly(first, second, third);

    Page<UniversityRequestDto> pending = universityRequestService.getRequests(
        UniversityRequestStatus.PENDING, OLDEST_FIRST);
    assertThat(pending.getContent()).extracting(UniversityRequestDto::id).containsExactly(second);
    assertThat(pending.getContent().get(0).studentUsername()).isEqualTo("petr");

    assertThat(universityRequestService.getRequests(UniversityRequestStatus.COMPLETED, OLDEST_FIRST)
        .getContent()).extracting(UniversityRequestDto::id).containsExactly(first);
    assertThat(universityRequestService.getRequests(UniversityRequestStatus.REJECTED, OLDEST_FIRST)
        .getContent()).extracting(UniversityRequestDto::id).containsExactly(third);
  }

  // === Вспомогательные методы ===

  private static UpdateStudentProfileRequest universityProfile(UUID universityId) {
    UpdateStudentProfileRequest profile = new UpdateStudentProfileRequest();
    profile.setUniversityId(universityId);
    return profile;
  }

  private University createUniversity(String name) {
    University university = new University();
    university.setName(name);
    university.setRegion(region);
    return universityRepository.save(university);
  }

  private UUID createStudent(String username) {
    Person person = new Person();
    person.setUsername(username);
    person.setFirstname("Иван");
    person.setLastname("Иванов");
    person.setFullname("Иванов Иван");
    person.setEmail(username + "@test.com");

    Student student = new Student();
    student.setPerson(person);
    return studentRepository.save(student).getId();
  }
}
