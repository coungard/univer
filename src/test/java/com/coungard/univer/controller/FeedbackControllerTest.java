package com.coungard.univer.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.entity.Feedback;
import com.coungard.univer.repository.AuditEventRepository;
import com.coungard.univer.repository.FeedbackRepository;
import com.coungard.univer.security.KeycloakAdminService;
import com.coungard.univer.service.FeedbackService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Отзывы через HTTP: права на пути, валидация тела, лимит частоты и сериализация ответа. Правила статусов и
 * окна лимита подробно покрыты в {@code FeedbackServiceTest}.
 *
 * <p>Токен не настоящий: {@link JwtDecoder} подменён и по строке токена отдаёт JWT с нужными {@code sub},
 * {@code preferred_username} и ролями, дальше работает настоящая цепочка Spring Security с
 * {@code KeycloakRoleConverter}.
 */
@SpringBootTest(classes = UniverApplication.class)
@AutoConfigureMockMvc
@Testcontainers
class FeedbackControllerTest {

  private static final String STUDENT_TOKEN = "student-token";
  private static final String OTHER_STUDENT_TOKEN = "other-student-token";
  private static final String TEACHER_TOKEN = "teacher-token";
  private static final String ADMIN_TOKEN = "admin-token";
  private static final String APPLICANT_TOKEN = "applicant-token";

  private static final UUID STUDENT_ID = UUID.randomUUID();
  private static final UUID TEACHER_ID = UUID.randomUUID();
  private static final UUID ADMIN_ID = UUID.randomUUID();

  private static final String VALID_BODY = """
      {"type": "BUG", "message": "Английский в четверг показывается два раза",
       "appVersion": "0.0.1", "platform": "android", "osVersion": "15"}""";

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
  private MockMvc mockMvc;

  @Autowired
  private FeedbackRepository feedbackRepository;

  @Autowired
  private AuditEventRepository auditEventRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @MockBean
  private JwtDecoder jwtDecoder;

  @MockBean
  private KeycloakAdminService keycloakAdminService;

  @BeforeEach
  void setUp() {
    auditEventRepository.deleteAll();
    feedbackRepository.deleteAll();

    when(jwtDecoder.decode(STUDENT_TOKEN)).thenReturn(jwt(STUDENT_ID, "ivan", "ROLE_STUDENT"));
    when(jwtDecoder.decode(OTHER_STUDENT_TOKEN)).thenReturn(jwt(UUID.randomUUID(), "petr", "ROLE_STUDENT"));
    when(jwtDecoder.decode(TEACHER_TOKEN)).thenReturn(jwt(TEACHER_ID, "teacher", "ROLE_TEACHER"));
    when(jwtDecoder.decode(ADMIN_TOKEN)).thenReturn(jwt(ADMIN_ID, "super", "ROLE_ADMIN"));
    when(jwtDecoder.decode(APPLICANT_TOKEN)).thenReturn(jwt(UUID.randomUUID(), "guest", "ROLE_APPLICANT"));
  }

  @Test
  void shouldSubmitFeedbackWithAuthorFromToken() throws Exception {
    submit(STUDENT_TOKEN, """
        {"type": "BUG", "message": "  Английский в четверг показывается два раза  ",
         "appVersion": " 0.0.1 ", "platform": "android", "osVersion": "15"}""")
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isString())
        .andExpect(jsonPath("$.type").value("BUG"))
        .andExpect(jsonPath("$.message").value("Английский в четверг показывается два раза"))
        .andExpect(jsonPath("$.status").value("NEW"))
        .andExpect(jsonPath("$.createdAt").value(isA(String.class)))
        // Автор и устройство в ответ автору не уходят
        .andExpect(jsonPath("$.userId").doesNotExist())
        .andExpect(jsonPath("$.appVersion").doesNotExist());

    submit(TEACHER_TOKEN, VALID_BODY.replace("BUG", "IDEA"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value("IDEA"));

    List<Feedback> saved = feedbackRepository.findAll();
    assertThat(saved).hasSize(2);
    assertThat(saved).filteredOn(feedback -> feedback.getUserId().equals(STUDENT_ID)).singleElement()
        .satisfies(feedback -> {
          assertThat(feedback.getRole()).isEqualTo("STUDENT");
          assertThat(feedback.getUsername()).isEqualTo("ivan");
          assertThat(feedback.getAppVersion()).isEqualTo("0.0.1");
        });
    assertThat(saved).filteredOn(feedback -> feedback.getUserId().equals(TEACHER_ID)).singleElement()
        .satisfies(feedback -> assertThat(feedback.getRole()).isEqualTo("TEACHER"));
  }

  @Test
  void shouldValidateFeedbackWithFieldMap() throws Exception {
    submit(STUDENT_TOKEN, VALID_BODY.replace("Английский в четверг показывается два раза", "   "))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").isString());

    submit(STUDENT_TOKEN, VALID_BODY.replace("Английский в четверг показывается два раза", "a".repeat(2001)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").isString());

    // Ограничение длины — без крайних пробелов
    submit(STUDENT_TOKEN, VALID_BODY.replace("Английский в четверг показывается два раза",
        "  " + "a".repeat(2000) + "  "))
        .andExpect(status().isCreated());

    submit(STUDENT_TOKEN, VALID_BODY.replace("\"BUG\"", "\"COMPLAINT\""))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.type").value("Недопустимое значение. Допустимые: BUG, IMPROVEMENT, IDEA"));

    submit(STUDENT_TOKEN, VALID_BODY.replace("\"android\"", "\"windows\""))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.platform").value("Недопустимое значение. Допустимые: ios, android"));

    submit(STUDENT_TOKEN, "{}")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.type").isString())
        .andExpect(jsonPath("$.message").isString())
        .andExpect(jsonPath("$.appVersion").isString())
        .andExpect(jsonPath("$.platform").isString())
        .andExpect(jsonPath("$.osVersion").isString());

    submit(STUDENT_TOKEN, VALID_BODY.replace("\"0.0.1\"", "\"" + "1".repeat(33) + "\""))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.appVersion").isString());

    // Битый JSON — по-прежнему стандартный 400 Spring
    submit(STUDENT_TOKEN, "{\"type\": ")
        .andExpect(status().isBadRequest());

    assertThat(feedbackRepository.count()).isEqualTo(1);
  }

  @Test
  void shouldAllowOnlyStudentsTeachersAndAdminsToSubmit() throws Exception {
    mockMvc.perform(post("/api/v1/feedback")
            .contentType(MediaType.APPLICATION_JSON)
            .content(VALID_BODY))
        .andExpect(status().isUnauthorized());

    submit(APPLICANT_TOKEN, VALID_BODY)
        .andExpect(status().isForbidden());

    submit(ADMIN_TOKEN, VALID_BODY)
        .andExpect(status().isCreated());
  }

  @Test
  void shouldRejectFeedbackOverHourlyLimit() throws Exception {
    for (int i = 0; i < FeedbackService.RATE_LIMIT; i++) {
      submit(STUDENT_TOKEN, VALID_BODY).andExpect(status().isCreated());
    }

    // Отзыв только что оставлен — место освободится почти через час
    submit(STUDENT_TOKEN, VALID_BODY)
        .andExpect(status().isTooManyRequests())
        .andExpect(header().string(HttpHeaders.RETRY_AFTER, matchesPattern("3[56]\\d\\d")))
        .andExpect(jsonPath("$.status").value(429))
        .andExpect(jsonPath("$.message").isString())
        .andExpect(jsonPath("$.retryAfterSeconds").isNumber());

    // Лимит у каждого пользователя свой
    submit(OTHER_STUDENT_TOKEN, VALID_BODY)
        .andExpect(status().isCreated());

    assertThat(feedbackRepository.count()).isEqualTo(FeedbackService.RATE_LIMIT + 1);
  }

  @Test
  void shouldRestrictListAndStatusChangeToAdmin() throws Exception {
    submit(STUDENT_TOKEN, VALID_BODY).andExpect(status().isCreated());
    UUID id = feedbackRepository.findAll().get(0).getId();

    mockMvc.perform(get("/api/v1/feedback"))
        .andExpect(status().isUnauthorized());

    for (String token : List.of(STUDENT_TOKEN, TEACHER_TOKEN)) {
      mockMvc.perform(get("/api/v1/feedback")
              .header(HttpHeaders.AUTHORIZATION, bearer(token)))
          .andExpect(status().isForbidden());
      mockMvc.perform(patch("/api/v1/feedback/{id}", id)
              .header(HttpHeaders.AUTHORIZATION, bearer(token))
              .contentType(MediaType.APPLICATION_JSON)
              .content("{\"status\": \"DONE\"}"))
          .andExpect(status().isForbidden());
    }
  }

  @Test
  void shouldListFeedbackNewestFirstWithFilters() throws Exception {
    UUID oldest = submitAndGetId(STUDENT_TOKEN, VALID_BODY);
    UUID middle = submitAndGetId(TEACHER_TOKEN, VALID_BODY.replace("BUG", "IDEA").replace("android", "ios"));
    // Разные авторы: каждый может оставить только один отзыв в час
    UUID newest = submitAndGetId(OTHER_STUDENT_TOKEN, VALID_BODY);
    setCreatedAt(oldest, Instant.now().minusSeconds(300));
    setCreatedAt(middle, Instant.now().minusSeconds(200));
    setCreatedAt(newest, Instant.now().minusSeconds(100));

    mockMvc.perform(get("/api/v1/feedback")
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(3))
        .andExpect(jsonPath("$.content[0].id").value(newest.toString()))
        .andExpect(jsonPath("$.content[1].id").value(middle.toString()))
        .andExpect(jsonPath("$.content[2].id").value(oldest.toString()))
        .andExpect(jsonPath("$.content[1].userId").value(TEACHER_ID.toString()))
        .andExpect(jsonPath("$.content[1].role").value("TEACHER"))
        .andExpect(jsonPath("$.content[1].username").value("teacher"))
        .andExpect(jsonPath("$.content[1].platform").value("ios"))
        .andExpect(jsonPath("$.content[1].appVersion").value("0.0.1"))
        .andExpect(jsonPath("$.content[1].osVersion").value("15"));

    mockMvc.perform(get("/api/v1/feedback").param("type", "BUG")
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.content[0].id").value(newest.toString()))
        .andExpect(jsonPath("$.content[1].id").value(oldest.toString()));

    changeStatus(oldest, "DONE").andExpect(status().isOk());

    mockMvc.perform(get("/api/v1/feedback").param("type", "BUG").param("status", "NEW")
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].id").value(newest.toString()));

    mockMvc.perform(get("/api/v1/feedback").param("status", "UNKNOWN")
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldChangeStatus() throws Exception {
    UUID id = submitAndGetId(STUDENT_TOKEN, VALID_BODY);

    changeStatus(id, "IN_PROGRESS")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
        .andExpect(jsonPath("$.updatedAt").value(isA(String.class)))
        .andExpect(jsonPath("$.updatedBy").value(ADMIN_ID.toString()))
        .andExpect(jsonPath("$.userId").value(STUDENT_ID.toString()));

    changeStatus(id, "REJECTED")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("REJECTED"));

    // Окончательный статус не меняется
    changeStatus(id, "NEW")
        .andExpect(status().isUnprocessableEntity());

    changeStatus(UUID.randomUUID(), "DONE")
        .andExpect(status().isNotFound());

    changeStatus(id, "ARCHIVED")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").isString());

    mockMvc.perform(patch("/api/v1/feedback/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").isString());
  }

  // === Вспомогательные методы ===

  private ResultActions submit(String token, String body) throws Exception {
    return mockMvc.perform(post("/api/v1/feedback")
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content(body));
  }

  private UUID submitAndGetId(String token, String body) throws Exception {
    String response = submit(token, body)
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();
    return UUID.fromString(response.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1"));
  }

  private ResultActions changeStatus(UUID id, String status) throws Exception {
    return mockMvc.perform(patch("/api/v1/feedback/{id}", id)
        .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN))
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"status\": \"" + status + "\"}"));
  }

  private void setCreatedAt(UUID id, Instant createdAt) {
    jdbcTemplate.update("UPDATE feedback SET created_at = ? WHERE id = ?",
        java.sql.Timestamp.from(createdAt), id);
  }

  private static String bearer(String token) {
    return "Bearer " + token;
  }

  private static Jwt jwt(UUID subject, String username, String role) {
    return Jwt.withTokenValue("token")
        .header("alg", "none")
        .subject(subject.toString())
        .claim("preferred_username", username)
        .claim("realm_access", Map.of("roles", List.of(role)))
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(300))
        .build();
  }
}
