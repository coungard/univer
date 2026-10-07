package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.RegionDto;
import com.coungard.univer.entity.Region;
import com.coungard.univer.repository.RegionRepository;
import com.coungard.univer.repository.UniversityRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = UniverApplication.class)
@Testcontainers
class RegionServiceTest {

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
  private RegionService regionService;

  @Autowired
  private RegionRepository regionRepository;

  @Autowired
  private UniversityRepository universityRepository;

  @BeforeEach
  void setUp() {
    universityRepository.deleteAll();
    regionRepository.deleteAll();
  }

  @Test
  @DisplayName("Регионы возвращаются отсортированными по названию")
  void shouldGetRegionsSortedByName() {
    createRegion("77", "Москва");
    createRegion("05", "Республика Дагестан");
    createRegion("22", "Алтайский край");

    List<RegionDto> regions = regionService.getRegions();

    assertThat(regions)
        .extracting(RegionDto::name)
        .containsExactly("Алтайский край", "Москва", "Республика Дагестан");
    assertThat(regions.get(0).code()).isEqualTo("22");
    assertThat(regions.get(0).id()).isNotNull();
  }

  private void createRegion(String code, String name) {
    Region region = new Region();
    region.setCode(code);
    region.setName(name);
    regionRepository.save(region);
  }
}
