package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.BellScheduleEntryDto;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.repository.BellScheduleEntryRepository;
import com.coungard.univer.repository.UniversityRepository;
import java.time.LocalTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = UniverApplication.class)
@Testcontainers
class BellScheduleEntryServiceTest {

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
  private BellScheduleEntryService bellScheduleEntryService;

  @Autowired
  private BellScheduleEntryRepository bellScheduleEntryRepository;

  @Autowired
  private UniversityRepository universityRepository;

  private UUID universityId;

  @BeforeEach
  void setUp() {
    bellScheduleEntryRepository.deleteAll();
    universityRepository.deleteAll();

    University university = new University();
    university.setName("Test University");
    universityId = universityRepository.save(university).getId();
  }

  @Test
  void shouldCreateAndRetrieveEntry() {
    // Given
    BellScheduleEntryDto dto = createDto(universityId, 1, LocalTime.of(9, 0), LocalTime.of(10, 30));

    // When
    BellScheduleEntryDto created = bellScheduleEntryService.createEntry(dto);
    BellScheduleEntryDto found = bellScheduleEntryService.getEntryById(created.id());

    // Then
    assertThat(found).isNotNull();
    assertThat(found.universityId()).isEqualTo(universityId);
    assertThat(found.pairNumber()).isEqualTo(1);
    assertThat(found.startTime()).isEqualTo(LocalTime.of(9, 0));
    assertThat(found.endTime()).isEqualTo(LocalTime.of(10, 30));
  }

  @Test
  void shouldCreateDefaultEntryWithoutUniversity() {
    // Given
    BellScheduleEntryDto dto = createDto(null, 1, LocalTime.of(9, 0), LocalTime.of(10, 30));

    // When
    BellScheduleEntryDto created = bellScheduleEntryService.createEntry(dto);

    // Then
    assertThat(created.universityId()).isNull();
  }

  @Test
  void shouldThrowExceptionWhenCreatingDuplicateUniversityPairNumber() {
    // Given
    bellScheduleEntryService.createEntry(createDto(universityId, 1, LocalTime.of(9, 0), LocalTime.of(10, 30)));
    BellScheduleEntryDto duplicate = createDto(universityId, 1, LocalTime.of(11, 0), LocalTime.of(12, 30));

    assertThatThrownBy(() -> bellScheduleEntryService.createEntry(duplicate))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowExceptionWhenCreatingDuplicateDefaultPairNumber() {
    // Given
    bellScheduleEntryService.createEntry(createDto(null, 1, LocalTime.of(9, 0), LocalTime.of(10, 30)));
    BellScheduleEntryDto duplicate = createDto(null, 1, LocalTime.of(11, 0), LocalTime.of(12, 30));

    assertThatThrownBy(() -> bellScheduleEntryService.createEntry(duplicate))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldGetEntriesByUniversity() {
    // Given
    bellScheduleEntryService.createEntry(createDto(universityId, 1, LocalTime.of(9, 0), LocalTime.of(10, 30)));
    bellScheduleEntryService.createEntry(createDto(universityId, 2, LocalTime.of(10, 40), LocalTime.of(12, 10)));
    bellScheduleEntryService.createEntry(createDto(null, 1, LocalTime.of(8, 0), LocalTime.of(9, 30)));

    // When
    Pageable pageable = PageRequest.of(0, 10);
    Page<BellScheduleEntryDto> entries = bellScheduleEntryService.getEntriesByUniversity(universityId, pageable);

    // Then
    assertThat(entries.getContent()).hasSize(2);
  }

  @Test
  void shouldUpdateEntry() {
    // Given
    BellScheduleEntryDto original = bellScheduleEntryService.createEntry(
        createDto(universityId, 1, LocalTime.of(9, 0), LocalTime.of(10, 30)));

    BellScheduleEntryDto updateDto = original.toBuilder()
        .startTime(LocalTime.of(9, 10))
        .endTime(LocalTime.of(10, 40))
        .build();

    // When
    BellScheduleEntryDto updated = bellScheduleEntryService.updateEntry(original.id(), updateDto);

    // Then
    assertThat(updated.startTime()).isEqualTo(LocalTime.of(9, 10));
    assertThat(updated.endTime()).isEqualTo(LocalTime.of(10, 40));
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentEntry() {
    BellScheduleEntryDto dto = createDto(universityId, 1, LocalTime.of(9, 0), LocalTime.of(10, 30));

    assertThatThrownBy(() -> bellScheduleEntryService.updateEntry(UUID.randomUUID(), dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldDeleteEntryById() {
    // Given
    BellScheduleEntryDto entry = bellScheduleEntryService.createEntry(
        createDto(universityId, 1, LocalTime.of(9, 0), LocalTime.of(10, 30)));

    // When
    bellScheduleEntryService.deleteEntry(entry.id());

    // Then
    assertThat(bellScheduleEntryRepository.findById(entry.id())).isEmpty();
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentEntry() {
    assertThatThrownBy(() -> bellScheduleEntryService.deleteEntry(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenEntryNotFound() {
    assertThatThrownBy(() -> bellScheduleEntryService.getEntryById(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  // === Вспомогательные методы ===

  private BellScheduleEntryDto createDto(UUID universityId, int pairNumber, LocalTime startTime, LocalTime endTime) {
    return BellScheduleEntryDto.builder()
        .universityId(universityId)
        .pairNumber(pairNumber)
        .startTime(startTime)
        .endTime(endTime)
        .build();
  }
}
