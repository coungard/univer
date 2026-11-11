package com.coungard.univer.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coungard.univer.TestRegions;
import com.coungard.univer.UniverApplication;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Department;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.University;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.DepartmentRepository;
import com.coungard.univer.repository.EnrollmentRepository;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.MediaFileRepository;
import com.coungard.univer.repository.RegionRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.security.KeycloakAdminService;
import com.coungard.univer.service.AvatarKeys;
import com.coungard.univer.service.StorageService;
import com.coungard.univer.service.TestImages;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Ростер преподавателя (issue #148): зачисления курса отдают ФИО и аватар студента, при этом профиль
 * студента преподавателю по-прежнему закрыт. Хранилище медиа — настоящее, во временном каталоге; JWT и
 * Keycloak подменены, как в {@code AvatarControllerTest}.
 */
@SpringBootTest(classes = UniverApplication.class)
@AutoConfigureMockMvc
@Testcontainers
class EnrollmentControllerTest {

  private static final String STUDENT_TOKEN = "student-token";
  private static final String TEACHER_TOKEN = "teacher-token";
  private static final String ENROLLMENTS_URL = "/api/v1/enrollments";
  private static final String AVATAR_URL = "/api/v1/users/me/avatar";
  private static final String PUBLIC_BASE_URL = "https://api.example.ru/media";

  private static final Path MEDIA_DIR = createTempDir();

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
    registry.add("media.storage-dir", MEDIA_DIR::toString);
    registry.add("media.public-base-url", () -> PUBLIC_BASE_URL);
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private StorageService storageService;

  @Autowired
  private EnrollmentRepository enrollmentRepository;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private UniversityRepository universityRepository;

  @Autowired
  private RegionRepository regionRepository;

  @Autowired
  private MediaFileRepository mediaFileRepository;

  @MockBean
  private JwtDecoder jwtDecoder;

  @MockBean
  private KeycloakAdminService keycloakAdminService;

  private UUID universityId;
  private UUID courseId;
  private UUID studentWithAvatarId;
  private UUID studentWithoutAvatarId;

  @BeforeEach
  void setUp() throws IOException {
    enrollmentRepository.deleteAll();
    jdbcTemplate.update("UPDATE persons SET avatar_file_id = NULL");
    studentRepository.deleteAll();
    courseRepository.deleteAll();
    departmentRepository.deleteAll();
    facultyRepository.deleteAll();
    universityRepository.deleteAll();
    mediaFileRepository.deleteAll();
    wipeMediaDir();

    University university = new University();
    university.setName("Test University");
    university.setRegion(TestRegions.create(regionRepository));
    universityId = universityRepository.save(university).getId();

    Faculty faculty = Faculty.builder()
        .name("Faculty of Computer Science")
        .university(universityRepository.getReferenceById(universityId))
        .build();
    UUID facultyId = facultyRepository.save(faculty).getId();

    Department department = new Department();
    department.setName("Department of Algorithms");
    department.setFaculty(facultyRepository.getReferenceById(facultyId));
    UUID departmentId = departmentRepository.save(department).getId();

    Course course = new Course();
    course.setTitle("Algorithms and Data Structures");
    course.setDepartment(departmentRepository.getReferenceById(departmentId));
    courseId = courseRepository.save(course).getId();

    studentWithAvatarId = createStudent("ivan", "Ivan", "Ivanov");
    studentWithoutAvatarId = createStudent("petr", "Petr", "Petrov");

    when(jwtDecoder.decode(STUDENT_TOKEN)).thenReturn(jwt(studentWithAvatarId, "ROLE_STUDENT"));
    when(jwtDecoder.decode(TEACHER_TOKEN)).thenReturn(jwt(UUID.randomUUID(), "ROLE_TEACHER"));
    when(keycloakAdminService.deleteUserIfExists(anyString())).thenReturn(true);
  }

  @Test
  void shouldReturnStudentFullnameAndAvatarInCourseEnrollments() throws Exception {
    UUID fileId = uploadedFileId();
    enroll(studentWithAvatarId);
    enroll(studentWithoutAvatarId);

    String body = mockMvc.perform(get(ENROLLMENTS_URL + "/course/" + courseId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEACHER_TOKEN))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    Map<UUID, JsonNode> byStudentId = new HashMap<>();
    json(body).get("content").forEach(node -> byStudentId.put(UUID.fromString(node.get("studentId").asText()), node));
    assertThat(byStudentId).hasSize(2);

    JsonNode withAvatar = byStudentId.get(studentWithAvatarId);
    assertThat(withAvatar.get("studentFullname").asText()).isEqualTo("Ivan Ivanov");
    assertThat(withAvatar.get("studentAvatarUrl").asText())
        .isEqualTo(PUBLIC_BASE_URL + "/" + AvatarKeys.full(fileId));
    assertThat(withAvatar.get("studentAvatarThumbnailUrl").asText())
        .isEqualTo(PUBLIC_BASE_URL + "/" + AvatarKeys.thumbnail(fileId));
    assertThat(AvatarKeys.all(fileId)).allMatch(storageService::exists);

    mockMvc.perform(get("/media/" + AvatarKeys.full(fileId)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_JPEG));

    JsonNode withoutAvatar = byStudentId.get(studentWithoutAvatarId);
    assertThat(withoutAvatar.get("studentFullname").asText()).isEqualTo("Petr Petrov");
    assertThat(withoutAvatar.get("studentAvatarUrl").isNull()).isTrue();
    assertThat(withoutAvatar.get("studentAvatarThumbnailUrl").isNull()).isTrue();
  }

  @Test
  void shouldEnrollStudentWithoutStudentFieldsInRequest() throws Exception {
    String request = objectMapper.writeValueAsString(Map.of(
        "studentId", studentWithoutAvatarId.toString(),
        "courseId", courseId.toString()));

    String body = mockMvc.perform(post(ENROLLMENTS_URL)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEACHER_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    JsonNode enrollment = json(body);
    assertThat(enrollment.get("studentFullname").asText()).isEqualTo("Petr Petrov");
    assertThat(enrollment.get("studentAvatarUrl").isNull()).isTrue();
    assertThat(enrollment.get("studentAvatarThumbnailUrl").isNull()).isTrue();
    assertThat(enrollmentRepository.existsByStudentIdAndCourseId(studentWithoutAvatarId, courseId)).isTrue();
  }

  @Test
  void shouldForbidTeacherFromReadingStudentProfile() throws Exception {
    mockMvc.perform(get("/api/v1/students/" + studentWithAvatarId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEACHER_TOKEN))
        .andExpect(status().isForbidden());
  }

  // === Вспомогательные методы ===

  private void enroll(UUID studentId) throws Exception {
    String request = objectMapper.writeValueAsString(Map.of(
        "studentId", studentId.toString(),
        "courseId", courseId.toString()));

    mockMvc.perform(post(ENROLLMENTS_URL)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEACHER_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isCreated());
  }

  private UUID uploadedFileId() throws Exception {
    String body = mockMvc.perform(multipart(HttpMethod.PUT, AVATAR_URL)
            .file(new MockMultipartFile("file", "photo.jpg", MediaType.IMAGE_JPEG_VALUE,
                TestImages.jpeg(300, 300)))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + STUDENT_TOKEN))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return UUID.fromString(json(body).get("fileId").asText());
  }

  private UUID createStudent(String username, String firstName, String lastName) {
    Student student = new Student();

    Person person = new Person();
    person.setUsername(username);
    person.setFirstname(firstName);
    person.setLastname(lastName);
    person.setFullname(firstName + " " + lastName);
    person.setEmail(username + "@test.com");

    student.setPerson(person);
    student.setEnrollmentDate(LocalDate.of(2026, 9, 1));
    University university = new University();
    university.setId(universityId);
    student.setUniversity(university);

    return studentRepository.save(student).getId();
  }

  private void wipeMediaDir() throws IOException {
    try (var files = Files.walk(MEDIA_DIR)) {
      files.sorted(Comparator.reverseOrder())
          .filter(path -> !path.equals(MEDIA_DIR))
          .forEach(path -> path.toFile().delete());
    }
  }

  private JsonNode json(String body) {
    try {
      return objectMapper.readTree(body);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
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

  private static Path createTempDir() {
    try {
      Path dir = Files.createTempDirectory("univer-enrollment-test");
      dir.toFile().deleteOnExit();
      return dir;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
