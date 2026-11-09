package com.coungard.univer.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.exception.AccountDeletionException;
import com.coungard.univer.exception.ConflictException;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.TooManyRequestsException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.filter.RequestIdFilter;
import com.coungard.univer.service.RegionService;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Сквозной {@code X-Request-Id} и обработка ошибок через настоящую цепочку фильтров и Spring Security
 * (issue #110). {@link JwtDecoder} подменён, как в {@code UniversityRequestControllerTest}.
 */
@SpringBootTest(classes = UniverApplication.class)
@AutoConfigureMockMvc
@Testcontainers
class RequestIdAndErrorHandlingTest {

  private static final String STUDENT_TOKEN = "student-token";

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

  @MockBean
  private RegionService regionService;

  @MockBean
  private JwtDecoder jwtDecoder;

  @Test
  void shouldReturnGeneratedRequestIdInResponse() throws Exception {
    when(regionService.getRegions()).thenReturn(List.of());

    mockMvc.perform(get("/api/v1/regions"))
        .andExpect(status().isOk())
        .andExpect(header().exists(RequestIdFilter.HEADER));
  }

  @Test
  void shouldReturnRequestIdSentByClient() throws Exception {
    when(regionService.getRegions()).thenReturn(List.of());

    mockMvc.perform(get("/api/v1/regions").header(RequestIdFilter.HEADER, "client-req-1"))
        .andExpect(status().isOk())
        .andExpect(header().string(RequestIdFilter.HEADER, "client-req-1"));
  }

  @Test
  void shouldReturn500WithRequestIdAndWithoutDetailsOnUnexpectedException() throws Exception {
    when(regionService.getRegions()).thenThrow(new IllegalStateException("секретная деталь из БД"));

    mockMvc.perform(get("/api/v1/regions").header(RequestIdFilter.HEADER, "client-req-500"))
        .andExpect(status().isInternalServerError())
        .andExpect(header().string(RequestIdFilter.HEADER, "client-req-500"))
        .andExpect(jsonPath("$.requestId").value("client-req-500"))
        .andExpect(jsonPath("$.message").value("Внутренняя ошибка сервера"))
        .andExpect(jsonPath("$.path").value("/api/v1/regions"))
        .andExpect(content().string(not(containsString("секретная деталь"))));
  }

  @Test
  void shouldKeepSpringStatusForStandardMvcErrors() throws Exception {
    // Не UUID в пути — 400, как и до общего обработчика, а не 500
    mockMvc.perform(get("/api/v1/universities/not-a-uuid"))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists(RequestIdFilter.HEADER));
  }

  @Test
  void shouldReturn401WithRequestIdWithoutToken() throws Exception {
    mockMvc.perform(get("/api/v1/semesters"))
        .andExpect(status().isUnauthorized())
        .andExpect(header().exists(RequestIdFilter.HEADER));
  }

  @Test
  void shouldReturn403FromMethodSecurityInsteadOf500() throws Exception {
    when(jwtDecoder.decode(STUDENT_TOKEN)).thenReturn(jwt(UUID.randomUUID(), "ROLE_STUDENT"));

    mockMvc.perform(delete("/api/v1/semesters/" + UUID.randomUUID())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + STUDENT_TOKEN))
        .andExpect(status().isForbidden())
        .andExpect(header().exists(RequestIdFilter.HEADER));
  }

  /**
   * Тело доменных ошибок (issue #144): {@code error} — стандартная причина для статуса (у 422 раньше был
   * «Not Found»), {@code path} — реальный путь (раньше «/»), {@code requestId} — как в заголовке.
   */
  @Test
  void shouldReturnUniformBodyForDomainErrors() throws Exception {
    record Case(RuntimeException exception, int status, String error) {
    }
    List<Case> cases = List.of(
        new Case(new ResourceNotFoundException("нет такого"), 404, "Not Found"),
        new Case(new ValidationException("не согласовано"), 422, "Unprocessable Entity"),
        new Case(new ConflictException("name", "занято", UUID.fromString("00000000-0000-0000-0000-000000000001")),
            409, "Conflict"),
        new Case(new TooManyRequestsException("часто", Duration.ofSeconds(90)), 429, "Too Many Requests"),
        new Case(new AccountDeletionException("Keycloak недоступен", new RuntimeException()), 500,
            "Internal Server Error"));

    for (Case c : cases) {
      doThrow(c.exception()).when(regionService).getRegions();

      mockMvc.perform(get("/api/v1/regions").header(RequestIdFilter.HEADER, "req-" + c.status()))
          .andExpect(status().is(c.status()))
          .andExpect(jsonPath("$.status").value(c.status()))
          .andExpect(jsonPath("$.error").value(c.error()))
          .andExpect(jsonPath("$.message").value(c.exception().getMessage()))
          .andExpect(jsonPath("$.requestId").value("req-" + c.status()))
          .andExpect(jsonPath("$.path").value("/api/v1/regions"))
          .andExpect(jsonPath("$.timestamp").exists());
    }

    // Свои поля 409 и 429 остаются на месте
    doThrow(cases.get(2).exception()).when(regionService).getRegions();
    mockMvc.perform(get("/api/v1/regions"))
        .andExpect(jsonPath("$.field").value("name"))
        .andExpect(jsonPath("$.id").value("00000000-0000-0000-0000-000000000001"));
    doThrow(cases.get(3).exception()).when(regionService).getRegions();
    mockMvc.perform(get("/api/v1/regions"))
        .andExpect(jsonPath("$.retryAfterSeconds").value(90))
        .andExpect(header().string(HttpHeaders.RETRY_AFTER, "90"));
  }

  private static Jwt jwt(UUID subject, String role) {
    return Jwt.withTokenValue("token")
        .header("alg", "none")
        .subject(subject.toString())
        .claim("realm_access", Map.of("roles", List.of(role)))
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(300))
        .build();
  }
}
