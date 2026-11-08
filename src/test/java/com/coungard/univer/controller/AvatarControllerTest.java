package com.coungard.univer.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.AuditAction;
import com.coungard.univer.dto.AuditEntityType;
import com.coungard.univer.dto.MediaFileStatus;
import com.coungard.univer.dto.MediaPurpose;
import com.coungard.univer.entity.AuditEvent;
import com.coungard.univer.entity.MediaFile;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.Teacher;
import com.coungard.univer.repository.AuditEventRepository;
import com.coungard.univer.repository.MediaFileRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.TeacherRepository;
import com.coungard.univer.security.KeycloakAdminService;
import com.coungard.univer.service.AvatarKeys;
import com.coungard.univer.service.MediaSweeper;
import com.coungard.univer.service.StorageService;
import com.coungard.univer.service.TestImages;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
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
 * Фото профиля (issue #137): загрузка, замена, удаление, ссылки в профиле, удаление вместе с аккаунтом и
 * уборка файлов-сирот. Хранилище — настоящее, во временном каталоге; JWT и Keycloak подменены, как в
 * {@code UserControllerTest}.
 */
@SpringBootTest(classes = UniverApplication.class)
@AutoConfigureMockMvc
@Testcontainers
class AvatarControllerTest {

  private static final String STUDENT_TOKEN = "student-token";
  private static final String TEACHER_TOKEN = "teacher-token";
  private static final String ADMIN_TOKEN = "admin-token";
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
  private MediaSweeper mediaSweeper;

  @Autowired
  private MediaFileRepository mediaFileRepository;

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

  @BeforeEach
  void setUp() throws IOException {
    jdbcTemplate.update("UPDATE persons SET avatar_file_id = NULL");
    auditEventRepository.deleteAll();
    studentRepository.deleteAll();
    teacherRepository.deleteAll();
    mediaFileRepository.deleteAll();
    try (var files = Files.walk(MEDIA_DIR)) {
      files.sorted(java.util.Comparator.reverseOrder()).filter(p -> !p.equals(MEDIA_DIR)).forEach(p -> p.toFile().delete());
    }

    Student student = new Student();
    student.setPerson(person("ivan"));
    studentId = studentRepository.save(student).getId();

    Teacher teacher = new Teacher();
    teacher.setPerson(person("teacher"));
    teacher.setRegistered(true);
    teacherId = teacherRepository.save(teacher).getId();

    when(jwtDecoder.decode(STUDENT_TOKEN)).thenReturn(jwt(studentId, "ROLE_STUDENT"));
    when(jwtDecoder.decode(TEACHER_TOKEN)).thenReturn(jwt(teacherId, "ROLE_TEACHER"));
    when(jwtDecoder.decode(ADMIN_TOKEN)).thenReturn(jwt(UUID.randomUUID(), "ROLE_ADMIN"));
    when(keycloakAdminService.deleteUserIfExists(anyString())).thenReturn(true);
  }

  @Test
  void shouldUploadAvatarAndShowItInProfile() throws Exception {
    JsonNode avatar = upload(STUDENT_TOKEN, TestImages.jpeg(800, 600), "photo.jpg")
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString().transform(this::json);

    UUID fileId = UUID.fromString(avatar.get("fileId").asText());
    assertThat(avatar.get("avatarUrl").asText()).isEqualTo(PUBLIC_BASE_URL + "/" + AvatarKeys.full(fileId));
    assertThat(avatar.get("avatarThumbnailUrl").asText())
        .isEqualTo(PUBLIC_BASE_URL + "/" + AvatarKeys.thumbnail(fileId));
    assertThat(AvatarKeys.all(fileId)).allMatch(storageService::exists);

    MediaFile file = mediaFileRepository.findById(fileId).orElseThrow();
    assertThat(file.getPurpose()).isEqualTo(MediaPurpose.AVATAR);
    assertThat(file.getStatus()).isEqualTo(MediaFileStatus.READY);
    assertThat(file.getOwnerId()).isEqualTo(studentId);
    assertThat(file.getOriginalName()).isEqualTo("photo.jpg");
    assertThat(file.getContentType()).isEqualTo("image/jpeg");
    assertThat(file.getCreatedBy()).isEqualTo(studentId);

    mockMvc.perform(get("/api/v1/students/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + STUDENT_TOKEN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.avatarUrl").value(avatar.get("avatarUrl").asText()))
        .andExpect(jsonPath("$.avatarThumbnailUrl").value(avatar.get("avatarThumbnailUrl").asText()));

    AuditEvent event = auditEventRepository.findAll().get(0);
    assertThat(event.getAction()).isEqualTo(AuditAction.UPDATE);
    assertThat(event.getEntityType()).isEqualTo(AuditEntityType.STUDENT);
    assertThat(event.getDetails()).containsEntry("avatarFileId", fileId.toString());
  }

  @Test
  void shouldServeUploadedAvatarWithoutToken() throws Exception {
    UUID fileId = uploadedFileId(STUDENT_TOKEN);

    mockMvc.perform(get("/media/" + AvatarKeys.thumbnail(fileId)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_JPEG));
  }

  @Test
  void shouldReplaceAvatarAndDeletePreviousFiles() throws Exception {
    UUID first = uploadedFileId(STUDENT_TOKEN);
    UUID second = uploadedFileId(STUDENT_TOKEN);

    assertThat(second).isNotEqualTo(first);
    assertThat(AvatarKeys.all(first)).noneMatch(storageService::exists);
    assertThat(AvatarKeys.all(second)).allMatch(storageService::exists);
    assertThat(mediaFileRepository.findAll()).extracting(MediaFile::getId).containsExactly(second);
    assertThat(MEDIA_DIR.resolve("avatars").resolve(first.toString())).doesNotExist();
  }

  @Test
  void shouldUploadTeacherAvatar() throws Exception {
    UUID fileId = uploadedFileId(TEACHER_TOKEN);

    mockMvc.perform(get("/api/v1/teachers/" + teacherId).header(HttpHeaders.AUTHORIZATION, "Bearer " + TEACHER_TOKEN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.avatarUrl").value(PUBLIC_BASE_URL + "/" + AvatarKeys.full(fileId)));
    assertThat(auditEventRepository.findAll()).extracting(AuditEvent::getEntityType)
        .containsExactly(AuditEntityType.TEACHER);
  }

  @Test
  void shouldDeleteAvatar() throws Exception {
    UUID fileId = uploadedFileId(STUDENT_TOKEN);

    mockMvc.perform(delete(AVATAR_URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + STUDENT_TOKEN))
        .andExpect(status().isNoContent());

    assertThat(AvatarKeys.all(fileId)).noneMatch(storageService::exists);
    assertThat(mediaFileRepository.count()).isZero();
    mockMvc.perform(get("/api/v1/students/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + STUDENT_TOKEN))
        .andExpect(jsonPath("$.avatarUrl").doesNotExist())
        .andExpect(jsonPath("$.avatarThumbnailUrl").doesNotExist());

    // Повторно — фото уже нет, тоже 204
    mockMvc.perform(delete(AVATAR_URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + STUDENT_TOKEN))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldDeleteAvatarWithAccount() throws Exception {
    UUID studentFile = uploadedFileId(STUDENT_TOKEN);
    UUID teacherFile = uploadedFileId(TEACHER_TOKEN);

    for (String token : List.of(STUDENT_TOKEN, TEACHER_TOKEN)) {
      mockMvc.perform(delete("/api/v1/users/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
          .andExpect(status().isNoContent());
    }

    assertThat(AvatarKeys.all(studentFile)).noneMatch(storageService::exists);
    assertThat(AvatarKeys.all(teacherFile)).noneMatch(storageService::exists);
    assertThat(mediaFileRepository.count()).isZero();
    assertThat(auditEventRepository.findAll())
        .filteredOn(event -> event.getAction() == AuditAction.DELETE)
        .allSatisfy(event -> assertThat(event.getDetails()).containsEntry("avatarDeleted", true))
        .hasSize(2);
  }

  @Test
  void shouldRejectInvalidFiles() throws Exception {
    upload(STUDENT_TOKEN, "не картинка".getBytes(StandardCharsets.UTF_8), "photo.jpg")
        .andExpect(status().isUnsupportedMediaType())
        .andExpect(jsonPath("$.message").value("Файл не является изображением JPEG, PNG или WebP"));
    upload(STUDENT_TOKEN, new byte[5 * 1024 * 1024 + 1], "big.jpg")
        .andExpect(status().isPayloadTooLarge());
    upload(STUDENT_TOKEN, new byte[0], "empty.jpg")
        .andExpect(status().isUnprocessableEntity());

    assertThat(mediaFileRepository.count()).isZero();
    assertThat(storageService.list("")).isEmpty();
  }

  @Test
  void shouldCheckAccess() throws Exception {
    mockMvc.perform(multipart(HttpMethod.PUT, AVATAR_URL).file(file(TestImages.jpeg(10, 10), "a.jpg")))
        .andExpect(status().isUnauthorized());
    upload(ADMIN_TOKEN, TestImages.jpeg(10, 10), "a.jpg").andExpect(status().isForbidden());
    mockMvc.perform(delete(AVATAR_URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_TOKEN))
        .andExpect(status().isForbidden());

    // Роль есть, а профиля нет (например, удалён)
    when(jwtDecoder.decode(STUDENT_TOKEN)).thenReturn(jwt(UUID.randomUUID(), "ROLE_STUDENT"));
    upload(STUDENT_TOKEN, TestImages.jpeg(10, 10), "a.jpg").andExpect(status().isNotFound());
  }

  @Test
  void sweeperShouldDeleteOnlyOldOrphans() throws Exception {
    UUID kept = uploadedFileId(STUDENT_TOKEN);
    UUID oldOrphan = UUID.randomUUID();
    UUID freshOrphan = UUID.randomUUID();
    for (UUID id : List.of(oldOrphan, freshOrphan)) {
      for (String key : AvatarKeys.all(id)) {
        storageService.put(key, new byte[] {1}, "image/jpeg");
      }
    }
    Instant twoHoursAgo = Instant.now().minus(Duration.ofHours(2));
    for (UUID id : List.of(kept, oldOrphan)) {
      for (String key : AvatarKeys.all(id)) {
        Files.setLastModifiedTime(MEDIA_DIR.resolve(key), FileTime.from(twoHoursAgo));
      }
    }

    int deleted = mediaSweeper.deleteOrphanAvatarFiles(Instant.now());

    assertThat(deleted).isEqualTo(2);
    assertThat(AvatarKeys.all(oldOrphan)).noneMatch(storageService::exists);
    assertThat(AvatarKeys.all(freshOrphan)).allMatch(storageService::exists);
    assertThat(AvatarKeys.all(kept)).allMatch(storageService::exists);
  }

  @Test
  void sweeperShouldDeleteStalePendingUploads() {
    MediaFile pending = new MediaFile();
    pending.setPurpose(MediaPurpose.AVATAR);
    pending.setStorageKey("avatars/" + UUID.randomUUID() + "/512.jpg");
    pending.setContentType("image/jpeg");
    pending.setOwnerId(studentId);
    pending.setStatus(MediaFileStatus.PENDING);
    pending = mediaFileRepository.save(pending);
    storageService.put(pending.getStorageKey(), new byte[] {1}, "image/jpeg");

    assertThat(mediaSweeper.deleteStalePending(Instant.now())).isZero();
    assertThat(mediaSweeper.deleteStalePending(Instant.now().plus(Duration.ofDays(2)))).isEqualTo(1);

    assertThat(mediaFileRepository.count()).isZero();
    assertThat(storageService.exists(pending.getStorageKey())).isFalse();
  }

  private UUID uploadedFileId(String token) throws Exception {
    String body = upload(token, TestImages.jpeg(300, 300), "photo.jpg")
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return UUID.fromString(json(body).get("fileId").asText());
  }

  private ResultActions upload(String token, byte[] content, String filename) throws Exception {
    return mockMvc.perform(multipart(HttpMethod.PUT, AVATAR_URL)
        .file(file(content, filename))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
  }

  private static MockMultipartFile file(byte[] content, String filename) {
    return new MockMultipartFile("file", filename, MediaType.IMAGE_JPEG_VALUE, content);
  }

  private JsonNode json(String body) {
    try {
      return objectMapper.readTree(body);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
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

  private static Path createTempDir() {
    try {
      Path dir = Files.createTempDirectory("univer-avatar-test");
      dir.toFile().deleteOnExit();
      return dir;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
