package com.coungard.univer.controller;

import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coungard.univer.TestRegions;
import com.coungard.univer.UniverApplication;
import com.coungard.univer.entity.University;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.RegionRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.security.KeycloakAdminService;
import com.jayway.jsonpath.JsonPath;
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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Поля аудита в ответах API (issue #119): автор и последний редактор видны всем, пришедшие в теле
 * запроса значения игнорируются, а ответ на изменение уже содержит нового редактора.
 *
 * <p>Токен не настоящий: {@link JwtDecoder} подменён и по строке токена отдаёт JWT с нужными
 * {@code sub} и ролями.
 */
@SpringBootTest(classes = UniverApplication.class)
@AutoConfigureMockMvc
@Testcontainers
class AuditFieldsControllerTest {

  private static final String CREATOR_TOKEN = "creator-token";
  private static final String EDITOR_TOKEN = "editor-token";
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
  private CourseRepository courseRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private UniversityRepository universityRepository;

  @Autowired
  private RegionRepository regionRepository;

  @MockBean
  private JwtDecoder jwtDecoder;

  @MockBean
  private KeycloakAdminService keycloakAdminService;

  private final UUID creatorId = UUID.randomUUID();
  private final UUID editorId = UUID.randomUUID();
  private UUID universityId;

  @BeforeEach
  void setUp() {
    courseRepository.deleteAll();
    facultyRepository.deleteAll();
    universityRepository.deleteAll();

    University university = new University();
    university.setName("Test University");
    university.setRegion(TestRegions.create(regionRepository));
    universityId = universityRepository.save(university).getId();

    when(jwtDecoder.decode(CREATOR_TOKEN)).thenReturn(jwt(creatorId, "ROLE_ADMIN"));
    when(jwtDecoder.decode(EDITOR_TOKEN)).thenReturn(jwt(editorId, "ROLE_ADMIN"));
    when(jwtDecoder.decode(STUDENT_TOKEN)).thenReturn(jwt(UUID.randomUUID(), "ROLE_STUDENT"));
  }

  @Test
  void shouldReturnAuthorAndEditorOfFaculty() throws Exception {
    // Given: факультет создаёт один администратор
    String created = mockMvc.perform(post("/api/v1/faculties")
            .header(HttpHeaders.AUTHORIZATION, bearer(CREATOR_TOKEN))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\": \"ФКТиЭ\", \"universityId\": \"" + universityId + "\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.createdBy").value(creatorId.toString()))
        .andExpect(jsonPath("$.updatedBy").value(creatorId.toString()))
        .andExpect(jsonPath("$.createdAt").value(isA(String.class)))
        .andExpect(jsonPath("$.updatedAt").value(isA(String.class)))
        .andReturn().getResponse().getContentAsString();
    String id = JsonPath.read(created, "$.id");
    String createdAt = JsonPath.read(created, "$.createdAt");

    // When: другой изменяет его и подсовывает свои значения полей аудита
    mockMvc.perform(put("/api/v1/faculties/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer(EDITOR_TOKEN))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\": \"ФКТиЭ ДГТУ\", \"universityId\": \"" + universityId + "\", "
                + "\"createdAt\": \"2000-01-01T00:00:00Z\", \"updatedAt\": \"2000-01-01T00:00:00Z\", "
                + "\"createdBy\": \"" + UUID.randomUUID() + "\", "
                + "\"updatedBy\": \"" + UUID.randomUUID() + "\"}"))
        // Then: в ответе уже новый редактор, автор и время создания прежние
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.createdBy").value(creatorId.toString()))
        .andExpect(jsonPath("$.createdAt").value(createdAt))
        .andExpect(jsonPath("$.updatedBy").value(editorId.toString()))
        .andExpect(jsonPath("$.updatedAt").value(not("2000-01-01T00:00:00Z")));

    // И это видит не только ADMIN
    mockMvc.perform(get("/api/v1/faculties/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer(STUDENT_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.createdBy").value(creatorId.toString()))
        .andExpect(jsonPath("$.updatedBy").value(editorId.toString()))
        .andExpect(jsonPath("$.createdAt").value(createdAt));
  }

  @Test
  void shouldReturnNewEditorRightInUpdateResponse() throws Exception {
    // Given
    String created = mockMvc.perform(post("/api/v1/courses")
            .header(HttpHeaders.AUTHORIZATION, bearer(CREATOR_TOKEN))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"Физика\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.createdBy").value(creatorId.toString()))
        .andReturn().getResponse().getContentAsString();
    String id = JsonPath.read(created, "$.id");

    // When / Then: updatedBy проставляется на flush — ответ не должен показывать прежнего редактора
    mockMvc.perform(put("/api/v1/courses/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer(EDITOR_TOKEN))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"Общая физика\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Общая физика"))
        .andExpect(jsonPath("$.createdBy").value(creatorId.toString()))
        .andExpect(jsonPath("$.updatedBy").value(editorId.toString()));
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
