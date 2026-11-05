package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.AuditAction;
import com.coungard.univer.dto.AuditEntityType;
import com.coungard.univer.dto.FeedbackAdminDto;
import com.coungard.univer.dto.FeedbackDto;
import com.coungard.univer.dto.FeedbackPlatform;
import com.coungard.univer.dto.FeedbackStatus;
import com.coungard.univer.dto.FeedbackType;
import com.coungard.univer.dto.request.SubmitFeedbackRequest;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.TooManyRequestsException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.repository.AuditEventRepository;
import com.coungard.univer.repository.FeedbackRepository;
import com.coungard.univer.security.KeycloakAdminService;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
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
class FeedbackServiceTest {

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

  private static final Pageable NEWEST_FIRST = PageRequest.of(0, 20, Sort.by("createdAt").descending());

  @Autowired
  private FeedbackService feedbackService;

  @Autowired
  private FeedbackRepository feedbackRepository;

  @Autowired
  private AuditEventRepository auditEventRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @MockBean
  private KeycloakAdminService keycloakAdminService;

  private UUID userId;

  @BeforeEach
  void setUp() {
    auditEventRepository.deleteAll();
    feedbackRepository.deleteAll();
    userId = UUID.randomUUID();
  }

  @Test
  void shouldSaveFeedbackAsNewWithAuthorAndDevice() {
    FeedbackDto created = submit(userId, FeedbackType.IMPROVEMENT);

    assertThat(created.id()).isNotNull();
    assertThat(created.status()).isEqualTo(FeedbackStatus.NEW);
    assertThat(created.createdAt()).isNotNull();

    FeedbackAdminDto saved = feedbackService.getFeedback(null, null, NEWEST_FIRST).getContent().get(0);
    assertThat(saved.userId()).isEqualTo(userId);
    assertThat(saved.role()).isEqualTo("STUDENT");
    assertThat(saved.username()).isEqualTo("ivan");
    assertThat(saved.type()).isEqualTo(FeedbackType.IMPROVEMENT);
    assertThat(saved.platform()).isEqualTo(FeedbackPlatform.IOS);
    assertThat(saved.appVersion()).isEqualTo("0.0.1");
    assertThat(saved.osVersion()).isEqualTo("18.1");
    // Оставить отзыв — не значимое изменение данных, в журнал аудита не пишется
    assertThat(auditEventRepository.count()).isZero();
  }

  @Test
  void shouldLimitFeedbackPerUserPerHour() {
    for (int i = 0; i < FeedbackService.RATE_LIMIT; i++) {
      submit(userId, FeedbackType.BUG);
    }
    // Самый старый отзыв в окне — 40 минут назад: место освободится примерно через 20 минут.
    // Остальные отзывы (при лимите больше одного) — только что, они в окне в любом случае
    setCreatedAt(feedbackRepository.findAll().get(0).getId(), Instant.now().minus(Duration.ofMinutes(40)));

    assertThatThrownBy(() -> submit(userId, FeedbackType.BUG))
        .isInstanceOfSatisfying(TooManyRequestsException.class, ex -> assertThat(ex.getRetryAfter())
            .isBetween(Duration.ofMinutes(19), Duration.ofMinutes(20)));

    // У другого пользователя лимит свой
    submit(UUID.randomUUID(), FeedbackType.BUG);
    assertThat(feedbackRepository.count()).isEqualTo(FeedbackService.RATE_LIMIT + 1);
  }

  @Test
  void shouldNotCountFeedbackOlderThanHour() {
    for (int i = 0; i < FeedbackService.RATE_LIMIT; i++) {
      submit(userId, FeedbackType.BUG);
    }
    jdbcTemplate.update("UPDATE feedback SET created_at = ? WHERE user_id = ?",
        Timestamp.from(Instant.now().minus(Duration.ofMinutes(61))), userId);

    submit(userId, FeedbackType.BUG);

    assertThat(feedbackRepository.count()).isEqualTo(FeedbackService.RATE_LIMIT + 1);
  }

  @Test
  void shouldFilterByTypeAndStatusNewestFirst() {
    UUID oldBug = submit(UUID.randomUUID(), FeedbackType.BUG).id();
    UUID idea = submit(UUID.randomUUID(), FeedbackType.IDEA).id();
    UUID newBug = submit(UUID.randomUUID(), FeedbackType.BUG).id();
    setCreatedAt(oldBug, Instant.now().minusSeconds(300));
    setCreatedAt(idea, Instant.now().minusSeconds(200));
    setCreatedAt(newBug, Instant.now().minusSeconds(100));
    feedbackService.updateStatus(oldBug, FeedbackStatus.IN_PROGRESS);

    assertThat(ids(feedbackService.getFeedback(null, null, NEWEST_FIRST))).containsExactly(newBug, idea, oldBug);
    assertThat(ids(feedbackService.getFeedback(FeedbackType.BUG, null, NEWEST_FIRST))).containsExactly(newBug, oldBug);
    assertThat(ids(feedbackService.getFeedback(null, FeedbackStatus.IN_PROGRESS, NEWEST_FIRST)))
        .containsExactly(oldBug);
    assertThat(ids(feedbackService.getFeedback(FeedbackType.BUG, FeedbackStatus.NEW, NEWEST_FIRST)))
        .containsExactly(newBug);
    assertThat(ids(feedbackService.getFeedback(FeedbackType.IDEA, FeedbackStatus.DONE, NEWEST_FIRST))).isEmpty();
  }

  @Test
  void shouldChangeStatusAndRecordAudit() {
    UUID id = submit(UUID.randomUUID(), FeedbackType.BUG).id();

    FeedbackAdminDto updated = feedbackService.updateStatus(id, FeedbackStatus.IN_PROGRESS);

    assertThat(updated.status()).isEqualTo(FeedbackStatus.IN_PROGRESS);
    assertThat(updated.updatedAt()).isNotNull();
    assertThat(auditEventRepository.findAll()).singleElement().satisfies(event -> {
      assertThat(event.getAction()).isEqualTo(AuditAction.STATUS_CHANGE);
      assertThat(event.getEntityType()).isEqualTo(AuditEntityType.FEEDBACK);
      assertThat(event.getEntityId()).isEqualTo(id);
      assertThat(event.getDetails()).containsEntry("from", "NEW").containsEntry("to", "IN_PROGRESS");
    });
  }

  @Test
  void shouldIgnoreSameStatus() {
    UUID id = submit(UUID.randomUUID(), FeedbackType.BUG).id();

    FeedbackAdminDto result = feedbackService.updateStatus(id, FeedbackStatus.NEW);

    assertThat(result.status()).isEqualTo(FeedbackStatus.NEW);
    assertThat(auditEventRepository.count()).isZero();
  }

  @Test
  void shouldAllowOnlyForwardTransitions() {
    // Из NEW — в любой следующий, в том числе сразу в окончательный
    UUID done = submit(UUID.randomUUID(), FeedbackType.BUG).id();
    assertThat(feedbackService.updateStatus(done, FeedbackStatus.DONE).status()).isEqualTo(FeedbackStatus.DONE);

    UUID rejected = submit(UUID.randomUUID(), FeedbackType.BUG).id();
    feedbackService.updateStatus(rejected, FeedbackStatus.IN_PROGRESS);
    assertThat(feedbackService.updateStatus(rejected, FeedbackStatus.REJECTED).status())
        .isEqualTo(FeedbackStatus.REJECTED);

    UUID inProgress = submit(UUID.randomUUID(), FeedbackType.BUG).id();
    feedbackService.updateStatus(inProgress, FeedbackStatus.IN_PROGRESS);

    assertThatThrownBy(() -> feedbackService.updateStatus(inProgress, FeedbackStatus.NEW))
        .isInstanceOf(ValidationException.class);
    assertThatThrownBy(() -> feedbackService.updateStatus(done, FeedbackStatus.IN_PROGRESS))
        .isInstanceOf(ValidationException.class);
    assertThatThrownBy(() -> feedbackService.updateStatus(rejected, FeedbackStatus.DONE))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowNotFoundForUnknownFeedback() {
    assertThatThrownBy(() -> feedbackService.updateStatus(UUID.randomUUID(), FeedbackStatus.DONE))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  // === Вспомогательные методы ===

  private FeedbackDto submit(UUID author, FeedbackType type) {
    return feedbackService.submit(author, "STUDENT", "ivan",
        new SubmitFeedbackRequest(type, "Текст отзыва", "0.0.1", FeedbackPlatform.IOS, "18.1"));
  }

  private void setCreatedAt(UUID id, Instant createdAt) {
    jdbcTemplate.update("UPDATE feedback SET created_at = ? WHERE id = ?", Timestamp.from(createdAt), id);
  }

  private static java.util.List<UUID> ids(Page<FeedbackAdminDto> page) {
    return page.getContent().stream().map(FeedbackAdminDto::id).toList();
  }
}
