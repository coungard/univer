package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.AddressDto;
import com.coungard.univer.dto.UniversityDto;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.repository.UniversityRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class UniversityServiceTest {

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

  @BeforeEach
  void setUp() {
    universityRepository.deleteAll();
  }

  @Test
  @DisplayName("Создание и получение университета по ID")
  void shouldCreateAndRetrieveUniversity() {
    // Given
    AddressDto addressDto = AddressDto.builder()
        .address("USA")
        .email("usa.gmail.com")
        .website("usa.edu")
        .region("Texas")
        .country("USA")
        .city("Cambridge")
        .street("MIT Street, 1")
        .build();

    UniversityDto dto = UniversityDto.builder()
        .name("MIT")
        .description("Massachusetts Institute of Technology")
        .address(addressDto)
        .build();

    // When
    UniversityDto saved = universityService.createUniversity(dto);
    UniversityDto found = universityService.getUniversityById(saved.id());

    // Then
    assertThat(found).isNotNull();
    assertThat(found.name()).isEqualTo("MIT");
    assertThat(found.description()).isEqualTo("Massachusetts Institute of Technology");
    assertThat(found.address()).isNotNull();
    assertThat(found.address().country()).isEqualTo("USA");
    assertThat(found.address().city()).isEqualTo("Cambridge");
    assertThat(found.createdAt()).isNotNull();
  }

  @Test
  @DisplayName("Получаем все университеты")
  void shouldGetAllUniversities() {
    // Given
    UniversityDto harvardDto = UniversityDto.builder()
        .name("Harvard")
        .description("Harvard University")
        .address(AddressDto.builder()
            .country("USA")
            .address("USA")
            .email("usa.gmail.com")
            .website("usa.edu")
            .region("Texas")
            .city("Cambridge")
            .street("Harvard Yard")
            .build())
        .build();

    UniversityDto oxfordDto = UniversityDto.builder()
        .name("Oxford")
        .description("University of Oxford")
        .address(AddressDto.builder()
            .country("UK")
            .address("USA")
            .email("usa.gmail.com")
            .website("usa.edu")
            .region("Texas")
            .city("Oxford")
            .street("High Street")
            .build())
        .build();

    universityService.createUniversity(harvardDto);
    universityService.createUniversity(oxfordDto);

    // When
    Pageable pageable = PageRequest.of(0, 10);
    Page<UniversityDto> all = universityService.getUniversities(pageable);

    // Then
    assertThat(all.getContent()).hasSize(2);
    assertThat(all.getContent())
        .extracting(UniversityDto::name)
        .containsExactlyInAnyOrder("Harvard", "Oxford");
  }

  @Test
  @DisplayName("Исключение при попытке получить несуществующий университет по ID")
  void shouldThrowExceptionWhenUniversityNotFound() {
    UUID randomId = UUID.randomUUID();
    assertThatThrownBy(() -> universityService.getUniversityById(randomId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("University not found with id:");
  }
}