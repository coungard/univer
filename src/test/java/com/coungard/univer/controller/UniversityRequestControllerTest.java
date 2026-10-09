package com.coungard.univer.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coungard.univer.TestRegions;
import com.coungard.univer.UniverApplication;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Region;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.University;
import com.coungard.univer.repository.RegionRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.repository.UniversityRequestRepository;
import com.coungard.univer.security.KeycloakAdminService;
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
 * Заявки на добавление университета через HTTP: права на пути, привязка параметров и сериализация
 * ответа. Бизнес-правила покрыты в {@code UniversityRequestServiceTest}.
 *
 * <p>Токен не настоящий: {@link JwtDecoder} подменён и по строке токена отдаёт JWT с нужными
 * {@code sub} и ролями, дальше работает настоящая цепочка Spring Security с
 * {@code KeycloakRoleConverter}.
 */
@SpringBootTest(classes = UniverApplication.class)
@AutoConfigureMockMvc
@Testcontainers
class UniversityRequestControllerTest {

  private static final String STUDENT_TOKEN = "student-token";
  private static final String OTHER_STUDENT_TOKEN = "other-student-token";
  private static final String ADMIN_TOKEN = "admin-token";

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
  private UniversityRequestRepository universityRequestRepository;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private UniversityRepository universityRepository;

  @Autowired
  private RegionRepository regionRepository;

  @MockBean
  private JwtDecoder jwtDecoder;

  @MockBean
  private KeycloakAdminService keycloakAdminService;

  private Region region;
  private UUID universityId;

  @BeforeEach
  void setUp() {
    universityRequestRepository.deleteAll();
    studentRepository.deleteAll();
    universityRepository.deleteAll();

    region = TestRegions.create(regionRepository);
    University university = new University();
    university.setName("Test University");
    university.setRegion(region);
    universityId = universityRepository.save(university).getId();

    when(jwtDecoder.decode(STUDENT_TOKEN)).thenReturn(jwt(createStudent("ivan"), "ROLE_STUDENT"));
    when(jwtDecoder.decode(OTHER_STUDENT_TOKEN)).thenReturn(jwt(createStudent("petr"), "ROLE_STUDENT"));
    when(jwtDecoder.decode(ADMIN_TOKEN)).thenReturn(jwt(UUID.randomUUID(), "ROLE_ADMIN"));
  }

  @Test
  void shouldSubmitAndReadOwnRequest() throws Exception {
    mockMvc.perform(put("/api/v1/students/me/university-request")
            .header(HttpHeaders.AUTHORIZATION, bearer(STUDENT_TOKEN))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\": \" Аграрный техникум \", \"regionId\": \"" + region.getId() + "\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isString())
        .andExpect(jsonPath("$.name").value("Аграрный техникум"))
        .andExpect(jsonPath("$.regionId").value(region.getId().toString()))
        .andExpect(jsonPath("$.regionName").value(region.getName()))
        .andExpect(jsonPath("$.status").value("PENDING"))
        .andExpect(jsonPath("$.universityId").value(nullValue()))
        .andExpect(jsonPath("$.studentUsername").value("ivan"))
        // Даты уходят строкой ISO-8601, а не числом
        .andExpect(jsonPath("$.createdAt").value(isA(String.class)))
        .andExpect(jsonPath("$.updatedAt").value(isA(String.class)));

    mockMvc.perform(get("/api/v1/students/me/university-request")
            .header(HttpHeaders.AUTHORIZATION, bearer(STUDENT_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Аграрный техникум"))
        .andExpect(jsonPath("$.status").value("PENDING"));

    // Чужую заявку второй студент не видит
    mockMvc.perform(get("/api/v1/students/me/university-request")
            .header(HttpHeaders.AUTHORIZATION, bearer(OTHER_STUDENT_TOKEN)))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldValidateSubmittedRequest() throws Exception {
    mockMvc.perform(put("/api/v1/students/me/university-request")
            .header(HttpHeaders.AUTHORIZATION, bearer(STUDENT_TOKEN))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\": \"  \"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.name").isString());

    mockMvc.perform(put("/api/v1/students/me/university-request")
            .header(HttpHeaders.AUTHORIZATION, bearer(STUDENT_TOKEN))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\": \"Техникум\", \"regionId\": \"" + UUID.randomUUID() + "\"}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldRequireStudentRoleForOwnRequest() throws Exception {
    mockMvc.perform(get("/api/v1/students/me/university-request"))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(put("/api/v1/students/me/university-request")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\": \"Техникум\"}"))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/api/v1/students/me/university-request")
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
        .andExpect(status().isForbidden());
    mockMvc.perform(put("/api/v1/students/me/university-request")
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\": \"Техникум\"}"))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldRequireAdminRoleForProcessingRequests() throws Exception {
    String requestId = submitRequest(STUDENT_TOKEN, "Техникум");

    mockMvc.perform(get("/api/v1/university-requests"))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(get("/api/v1/university-requests")
            .header(HttpHeaders.AUTHORIZATION, bearer(STUDENT_TOKEN)))
        .andExpect(status().isForbidden());
    mockMvc.perform(post("/api/v1/university-requests/{id}/complete", requestId)
            .header(HttpHeaders.AUTHORIZATION, bearer(STUDENT_TOKEN))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"universityId\": \"" + universityId + "\"}"))
        .andExpect(status().isForbidden());
    mockMvc.perform(post("/api/v1/university-requests/{id}/reject", requestId)
            .header(HttpHeaders.AUTHORIZATION, bearer(STUDENT_TOKEN)))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldListRequestsWithStatusFilter() throws Exception {
    String pendingId = submitRequest(STUDENT_TOKEN, "Техникум");
    String rejectedId = submitRequest(OTHER_STUDENT_TOKEN, "asdf");
    mockMvc.perform(post("/api/v1/university-requests/{id}/reject", rejectedId)
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/v1/university-requests")
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.content[0].id").value(pendingId))
        .andExpect(jsonPath("$.content[1].id").value(rejectedId))
        .andExpect(jsonPath("$.totalElements").value(2));

    mockMvc.perform(get("/api/v1/university-requests").param("status", "PENDING")
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].id").value(pendingId))
        .andExpect(jsonPath("$.content[0].studentUsername").value("ivan"));

    mockMvc.perform(get("/api/v1/university-requests").param("status", "REJECTED").param("size", "1")
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(rejectedId));

    mockMvc.perform(get("/api/v1/university-requests").param("status", "UNKNOWN")
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldCompleteRequestAndShowUniversityInStudentProfile() throws Exception {
    String requestId = submitRequest(STUDENT_TOKEN, "Техникум");

    mockMvc.perform(post("/api/v1/university-requests/{id}/complete", requestId)
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/api/v1/university-requests/{id}/complete", requestId)
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"universityId\": \"" + universityId + "\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("COMPLETED"))
        .andExpect(jsonPath("$.universityId").value(universityId.toString()));

    mockMvc.perform(get("/api/v1/students/me")
            .header(HttpHeaders.AUTHORIZATION, bearer(STUDENT_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.universityId").value(universityId.toString()));

    // Повторно обработать закрытую заявку нельзя
    mockMvc.perform(post("/api/v1/university-requests/{id}/reject", requestId)
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void shouldRejectRequestWithOptionalComment() throws Exception {
    String withComment = submitRequest(STUDENT_TOKEN, "asdf");
    String withoutBody = submitRequest(OTHER_STUDENT_TOKEN, "qwerty");

    mockMvc.perform(post("/api/v1/university-requests/{id}/reject", withComment)
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"comment\": \"Укажите полное название\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("REJECTED"))
        .andExpect(jsonPath("$.comment").value("Укажите полное название"));

    // Причину видит и сам студент
    mockMvc.perform(get("/api/v1/students/me/university-request")
            .header(HttpHeaders.AUTHORIZATION, bearer(STUDENT_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("REJECTED"))
        .andExpect(jsonPath("$.comment").value("Укажите полное название"));

    mockMvc.perform(post("/api/v1/university-requests/{id}/reject", withoutBody)
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("REJECTED"))
        .andExpect(jsonPath("$.comment").value(nullValue()));

    mockMvc.perform(post("/api/v1/university-requests/{id}/reject", UUID.randomUUID())
            .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
        .andExpect(status().isNotFound());
  }

  // === Вспомогательные методы ===

  private String submitRequest(String token, String name) throws Exception {
    mockMvc.perform(put("/api/v1/students/me/university-request")
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\": \"" + name + "\"}"))
        .andExpect(status().isOk());
    return universityRequestRepository.findAll().stream()
        .filter(request -> request.getName().equals(name))
        .findFirst()
        .orElseThrow()
        .getId()
        .toString();
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
