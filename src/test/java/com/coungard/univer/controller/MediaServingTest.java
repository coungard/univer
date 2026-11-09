package com.coungard.univer.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.service.StorageService;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Раздача медиа-файлов приложением при {@code media.serve-from-app} — без токена и без листинга (issue #136). */
@SpringBootTest(classes = UniverApplication.class)
@AutoConfigureMockMvc
@Testcontainers
class MediaServingTest {

  private static final String KEY = "avatars/0b6f7c1e-2d4a-4f3b-9c8e-1a2b3c4d5e6f/512.jpg";

  @TempDir
  static Path mediaDir;

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
    registry.add("media.storage-dir", () -> mediaDir.resolve("media").toString());
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private StorageService storageService;

  @MockBean
  private JwtDecoder jwtDecoder;

  @Test
  void shouldServeStoredObjectWithoutToken() throws Exception {
    storageService.put(KEY, "jpeg-bytes".getBytes(StandardCharsets.UTF_8), "image/jpeg");

    mockMvc.perform(get("/media/" + KEY))
        .andExpect(status().isOk())
        .andExpect(content().string("jpeg-bytes"))
        .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "max-age=31536000, public"));
  }

  @Test
  void shouldReturnNotFoundForMissingObjectAndDirectories() throws Exception {
    storageService.put(KEY, "jpeg-bytes".getBytes(StandardCharsets.UTF_8), "image/jpeg");

    mockMvc.perform(get("/media/avatars/missing/512.jpg")).andExpect(status().isNotFound());
    mockMvc.perform(get("/media/avatars/")).andExpect(status().isNotFound());
    mockMvc.perform(get("/media/")).andExpect(status().isNotFound());
  }

  @Test
  void shouldNotServeFilesOutsideMediaDirectory() throws Exception {
    Files.writeString(mediaDir.resolve("secret.txt"), "secret");

    mockMvc.perform(get("/media/../secret.txt")).andExpect(status().is4xxClientError());
    mockMvc.perform(get("/media/%2e%2e/secret.txt")).andExpect(status().is4xxClientError());
  }

  @Test
  void shouldBuildPublicUrlFromConfig() {
    assertThat(storageService.publicUrl(KEY))
        .isEqualTo("http://localhost:8023/media/" + KEY);
  }
}
