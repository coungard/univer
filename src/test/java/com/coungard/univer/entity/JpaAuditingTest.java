package com.coungard.univer.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.coungard.univer.TestRegions;
import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.UniversityDto;
import com.coungard.univer.repository.RegionRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.service.UniversityService;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Кто и когда создал и изменил сущность — {@link AuditableEntity} (issue #112).
 */
@SpringBootTest(classes = UniverApplication.class)
@Testcontainers
class JpaAuditingTest {

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
  private UniversityService universityService;

  @Autowired
  private UniversityRepository universityRepository;

  @Autowired
  private RegionRepository regionRepository;

  private UUID regionId;

  @BeforeEach
  void setUp() {
    universityRepository.deleteAll();
    regionId = TestRegions.create(regionRepository).getId();
  }

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldKeepCreatorAndTrackLastEditor() {
    // Given: университет создаёт один администратор
    UUID creatorId = UUID.randomUUID();
    authenticateAs(creatorId);
    UUID id = universityService.createUniversity(universityDto("МГУ")).id();
    University created = universityRepository.findById(id).orElseThrow();

    // When: изменяет другой
    UUID editorId = UUID.randomUUID();
    authenticateAs(editorId);
    universityService.updateUniversity(id, universityDto("МГУ им. Ломоносова"));
    University updated = universityRepository.findById(id).orElseThrow();

    // Then: при создании автор и редактор совпадают, после изменения меняется только редактор
    assertThat(created.getCreatedBy()).isEqualTo(creatorId);
    assertThat(created.getUpdatedBy()).isEqualTo(creatorId);
    assertThat(created.getCreatedAt()).isNotNull();

    assertThat(updated.getCreatedBy()).isEqualTo(creatorId);
    assertThat(updated.getCreatedAt()).isEqualTo(created.getCreatedAt());
    assertThat(updated.getUpdatedBy()).isEqualTo(editorId);
    assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(created.getUpdatedAt());
  }

  @Test
  void shouldLeaveAuthorEmptyWithoutAuthentication() {
    // When: вне запроса (как регистрация, seed-данные)
    UUID id = universityService.createUniversity(universityDto("СПбГУ")).id();

    // Then: время есть, автора нет
    University created = universityRepository.findById(id).orElseThrow();
    assertThat(created.getCreatedAt()).isNotNull();
    assertThat(created.getUpdatedAt()).isNotNull();
    assertThat(created.getCreatedBy()).isNull();
    assertThat(created.getUpdatedBy()).isNull();
  }

  private void authenticateAs(UUID userId) {
    SecurityContextHolder.getContext().setAuthentication(
        new TestingAuthenticationToken(userId.toString(), null, "ROLE_ADMIN"));
  }

  private UniversityDto universityDto(String name) {
    return UniversityDto.builder()
        .name(name)
        .regionId(regionId)
        .build();
  }
}
