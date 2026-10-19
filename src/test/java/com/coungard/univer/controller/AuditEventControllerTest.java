package com.coungard.univer.controller;

import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coungard.univer.TestRegions;
import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.AuditAction;
import com.coungard.univer.dto.AuditEntityType;
import com.coungard.univer.entity.AuditEvent;
import com.coungard.univer.entity.University;
import com.coungard.univer.filter.RequestIdFilter;
import com.coungard.univer.repository.AuditEventRepository;
import com.coungard.univer.repository.RegionRepository;
import com.coungard.univer.repository.UniversityRepository;
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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Журнал аудита через настоящую цепочку фильтров и Spring Security (issue #111): автор и request id
 * берутся из запроса, читать журнал может только ADMIN.
 */
@SpringBootTest(classes = UniverApplication.class)
@AutoConfigureMockMvc
@Testcontainers
class AuditEventControllerTest {

  private static final String ADMIN_TOKEN = "admin-token";
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

  @Autowired
  private AuditEventRepository auditEventRepository;

  @Autowired
  private UniversityRepository universityRepository;

  @Autowired
  private RegionRepository regionRepository;

  @MockBean
  private JwtDecoder jwtDecoder;

  private UUID adminId;

  @BeforeEach
  void setUp() {
    auditEventRepository.deleteAll();
    universityRepository.deleteAll();

    adminId = UUID.randomUUID();
    when(jwtDecoder.decode(ADMIN_TOKEN)).thenReturn(jwt(adminId, "ROLE_ADMIN"));
    when(jwtDecoder.decode(STUDENT_TOKEN)).thenReturn(jwt(UUID.randomUUID(), "ROLE_STUDENT"));
  }

  @Test
  void shouldRecordActorAndRequestIdOfHttpRequest() throws Exception {
    // Given
    University university = new University();
    university.setName("Test University");
    university.setRegion(TestRegions.create(regionRepository));
    UUID universityId = universityRepository.save(university).getId();

    // When: администратор удаляет университет
    mockMvc.perform(delete("/api/v1/universities/" + universityId)
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN))
            .header(RequestIdFilter.HEADER, "req-delete-1"))
        .andExpect(status().is2xxSuccessful());

    // Then
    mockMvc.perform(get("/api/v1/audit-events")
            .param("entityType", "UNIVERSITY")
            .param("entityId", universityId.toString())
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.content[0].action").value("DELETE"))
        .andExpect(jsonPath("$.content[0].entityType").value("UNIVERSITY"))
        .andExpect(jsonPath("$.content[0].entityId").value(universityId.toString()))
        .andExpect(jsonPath("$.content[0].actorId").value(adminId.toString()))
        .andExpect(jsonPath("$.content[0].actorRole").value("ADMIN"))
        .andExpect(jsonPath("$.content[0].requestId").value("req-delete-1"))
        .andExpect(jsonPath("$.content[0].occurredAt").isString());
  }

  @Test
  void shouldFilterEventsAndReturnNewestFirst() throws Exception {
    // Given
    UUID pairId = UUID.randomUUID();
    UUID studentId = UUID.randomUUID();
    save(AuditAction.CREATE, pairId, studentId, "2026-10-01T10:00:00Z");
    save(AuditAction.UPDATE, pairId, studentId, "2026-10-05T10:00:00Z");
    save(AuditAction.DELETE, pairId, adminId, "2026-10-10T10:00:00Z");
    save(AuditAction.CREATE, UUID.randomUUID(), studentId, "2026-10-06T10:00:00Z");

    // When & Then: все события пары — сначала новые
    mockMvc.perform(get("/api/v1/audit-events")
            .param("entityType", "PAIR")
            .param("entityId", pairId.toString())
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[*].action", contains("DELETE", "UPDATE", "CREATE")))
        .andExpect(jsonPath("$.content[0].details.lecturesDeleted").value(3));

    // Действия студента за период: from включительно, to не включительно
    mockMvc.perform(get("/api/v1/audit-events")
            .param("actorId", studentId.toString())
            .param("from", "2026-10-05T10:00:00Z")
            .param("to", "2026-10-06T10:00:00Z")
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.content[0].action").value("UPDATE"));
  }

  @Test
  void shouldForbidAuditLogToStudent() throws Exception {
    mockMvc.perform(get("/api/v1/audit-events").header(HttpHeaders.AUTHORIZATION, bearer(STUDENT_TOKEN)))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldRequireAuthenticationForAuditLog() throws Exception {
    mockMvc.perform(get("/api/v1/audit-events"))
        .andExpect(status().isUnauthorized());
  }

  private void save(AuditAction action, UUID entityId, UUID actorId, String occurredAt) {
    AuditEvent event = new AuditEvent();
    event.setAction(action);
    event.setEntityType(AuditEntityType.PAIR);
    event.setEntityId(entityId);
    event.setActorId(actorId);
    event.setOccurredAt(Instant.parse(occurredAt));
    event.setDetails(Map.of("lecturesDeleted", 3));
    auditEventRepository.save(event);
  }

  private static String bearer(String token) {
    return "Bearer " + token;
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
