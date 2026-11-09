package com.coungard.univer.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.Gender;
import com.coungard.univer.dto.registration.RegisterData;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.Teacher;
import com.coungard.univer.repository.AuditEventRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.TeacherRepository;
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
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Пол студента и преподавателя (issue #139): регистрация с полем и без, неизвестное значение, чтение в
 * профиле, {@code PATCH /students/me} и {@code PATCH /teachers/me}. JWT и Keycloak подменены, как в
 * {@code UserControllerTest}.
 */
@SpringBootTest(classes = UniverApplication.class)
@AutoConfigureMockMvc
@Testcontainers
class GenderControllerTest {

  private static final String STUDENT_TOKEN = "student-token";
  private static final String TEACHER_TOKEN = "teacher-token";

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
  private StudentRepository studentRepository;

  @Autowired
  private TeacherRepository teacherRepository;

  @MockBean
  private JwtDecoder jwtDecoder;

  @MockBean
  private KeycloakAdminService keycloakAdminService;

  private UUID studentId;
  private UUID teacherId;
  private UUID otherTeacherId;

  @BeforeEach
  void setUp() {
    auditEventRepository.deleteAll();
    studentRepository.deleteAll();
    teacherRepository.deleteAll();

    Student student = new Student();
    student.setPerson(person("ivan"));
    studentId = studentRepository.save(student).getId();

    teacherId = createTeacher("teacher");
    otherTeacherId = createTeacher("other");

    when(jwtDecoder.decode(STUDENT_TOKEN)).thenReturn(jwt(studentId, "ROLE_STUDENT"));
    when(jwtDecoder.decode(TEACHER_TOKEN)).thenReturn(jwt(teacherId, "ROLE_TEACHER"));
    when(keycloakAdminService.createUser(any(RegisterData.class)))
        .thenAnswer(invocation -> UUID.randomUUID().toString());
  }

  @Test
  void shouldRegisterStudentOnlyWithGender() throws Exception {
    registerStudent("anna", "\"gender\": \"FEMALE\"")
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.gender").value("FEMALE"));
    registerStudent("old", null)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.gender").value("Пол обязателен"));
    registerStudent("nulled", "\"gender\": null")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.gender").value("Пол обязателен"));

    // Студент из setUp зарегистрирован «до» поля — у него пола нет
    assertThat(studentRepository.findAll()).extracting(s -> s.getPerson().getGender())
        .containsExactlyInAnyOrder(null, Gender.FEMALE);
  }

  @Test
  void shouldRegisterTeacherOnlyWithGender() throws Exception {
    registerTeacher("petrov", "\"gender\": \"MALE\"")
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.gender").value("MALE"));
    registerTeacher("sidorov", null)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.gender").value("Пол обязателен"));
  }

  @Test
  void shouldRejectUnknownGenderAtRegistration() throws Exception {
    registerStudent("anna", "\"gender\": \"OTHER\"")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.gender").value("Недопустимое значение. Допустимые: MALE, FEMALE"));
    registerTeacher("petrov", "\"gender\": \"female\"")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.gender").value("Недопустимое значение. Допустимые: MALE, FEMALE"));
  }

  @Test
  void studentShouldSetGenderAfterRegistration() throws Exception {
    mockMvc.perform(get("/api/v1/students/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + STUDENT_TOKEN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.gender").doesNotExist());

    patchMe("/api/v1/students/me", STUDENT_TOKEN, "{\"gender\": \"FEMALE\"}")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.gender").value("FEMALE"));
    // Поле не передано — не меняется
    patchMe("/api/v1/students/me", STUDENT_TOKEN, "{}")
        .andExpect(jsonPath("$.gender").value("FEMALE"));
    patchMe("/api/v1/students/me", STUDENT_TOKEN, "{\"gender\": \"UNKNOWN\"}")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.gender").exists());
    // Пол обязателен: явный null не очищает его
    patchMe("/api/v1/students/me", STUDENT_TOKEN, "{\"gender\": null}")
        .andExpect(status().isUnprocessableEntity());
    patchMe("/api/v1/students/me", STUDENT_TOKEN, "{\"gender\": \"MALE\"}")
        .andExpect(jsonPath("$.gender").value("MALE"));
  }

  @Test
  void teacherShouldSetOnlyOwnGender() throws Exception {
    patchMe("/api/v1/teachers/me", TEACHER_TOKEN, "{\"gender\": \"MALE\"}")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(teacherId.toString()))
        .andExpect(jsonPath("$.gender").value("MALE"))
        .andExpect(jsonPath("$.updatedBy").value(teacherId.toString()));

    mockMvc.perform(get("/api/v1/teachers/" + teacherId).header(HttpHeaders.AUTHORIZATION, "Bearer " + TEACHER_TOKEN))
        .andExpect(jsonPath("$.gender").value("MALE"));
    mockMvc.perform(get("/api/v1/teachers/" + otherTeacherId).header(HttpHeaders.AUTHORIZATION, "Bearer " + TEACHER_TOKEN))
        .andExpect(jsonPath("$.gender").doesNotExist());

    patchMe("/api/v1/teachers/me", TEACHER_TOKEN, "{}")
        .andExpect(jsonPath("$.gender").value("MALE"));
    patchMe("/api/v1/teachers/me", TEACHER_TOKEN, "{\"gender\": null}")
        .andExpect(status().isUnprocessableEntity());
    patchMe("/api/v1/teachers/me", TEACHER_TOKEN, "{}")
        .andExpect(jsonPath("$.gender").value("MALE"));
    patchMe("/api/v1/teachers/me", TEACHER_TOKEN, "{\"gender\": \"X\"}")
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldCheckAccessToTeacherProfile() throws Exception {
    patchMe("/api/v1/teachers/me", STUDENT_TOKEN, "{\"gender\": \"MALE\"}").andExpect(status().isForbidden());
    patchMe("/api/v1/students/me", TEACHER_TOKEN, "{\"gender\": \"MALE\"}").andExpect(status().isForbidden());
    mockMvc.perform(patch("/api/v1/teachers/me").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isUnauthorized());

    // Роль есть, а профиля нет
    when(jwtDecoder.decode(TEACHER_TOKEN)).thenReturn(jwt(UUID.randomUUID(), "ROLE_TEACHER"));
    patchMe("/api/v1/teachers/me", TEACHER_TOKEN, "{\"gender\": \"MALE\"}").andExpect(status().isNotFound());
  }

  private ResultActions registerStudent(String username, String genderJson) throws Exception {
    String body = """
        {"username": "%s", "firstname": "Анна", "lastname": "Иванова", "email": "%s@test.com",
         "password": "secret123", "birthday": "2004-05-01"%s}
        """.formatted(username, username, genderJson == null ? "" : ", " + genderJson);
    return mockMvc.perform(post("/api/v1/students/register").contentType(MediaType.APPLICATION_JSON).content(body));
  }

  private ResultActions registerTeacher(String username, String genderJson) throws Exception {
    String body = """
        {"username": "%s", "firstname": "Пётр", "lastname": "Петров", "email": "%s@test.com",
         "password": "secret123", "birthday": "1980-05-01", "position": "Доцент"%s}
        """.formatted(username, username, genderJson == null ? "" : ", " + genderJson);
    return mockMvc.perform(post("/api/v1/teachers/register").contentType(MediaType.APPLICATION_JSON).content(body));
  }

  private ResultActions patchMe(String url, String token, String body) throws Exception {
    return mockMvc.perform(patch(url)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body));
  }

  private UUID createTeacher(String username) {
    Teacher teacher = new Teacher();
    teacher.setPerson(person(username));
    teacher.setRegistered(true);
    return teacherRepository.save(teacher).getId();
  }

  private static Person person(String username) {
    Person person = new Person();
    person.setUsername(username);
    person.setFirstname(username);
    person.setLastname(username);
    person.setFullname(username);
    person.setEmail(username + "@test.com");
    return person;
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
