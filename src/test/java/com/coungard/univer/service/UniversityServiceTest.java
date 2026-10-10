package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.AddressDto;
import com.coungard.univer.dto.UniversityDto;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Region;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.RegionRepository;
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

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private RegionRepository regionRepository;

  private UUID regionId;

  @BeforeEach
  void setUp() {
    universityRepository.deleteAll();
    regionRepository.deleteAll();
    regionId = createRegion("01", "Test Region").getId();
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
        .regionId(regionId)
        .description("Massachusetts Institute of Technology")
        .rector("Sally Kornbluth")
        .foundingYear(1861)
        .studentCount(11858)
        .address(addressDto)
        .build();

    // When
    UniversityDto saved = universityService.createUniversity(dto);
    UniversityDto found = universityService.getUniversityById(saved.id());

    // Then
    assertThat(found).isNotNull();
    assertThat(found.name()).isEqualTo("MIT");
    assertThat(found.description()).isEqualTo("Massachusetts Institute of Technology");
    assertThat(found.rector()).isEqualTo("Sally Kornbluth");
    assertThat(found.foundingYear()).isEqualTo(1861);
    assertThat(found.studentCount()).isEqualTo(11858);
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
        .regionId(regionId)
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
        .regionId(regionId)
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
    Page<UniversityDto> all = universityService.getUniversities(null, null, pageable);

    // Then
    assertThat(all.getContent()).hasSize(2);
    assertThat(all.getContent())
        .extracting(UniversityDto::name)
        .containsExactlyInAnyOrder("Harvard", "Oxford");
  }

  @Test
  @DisplayName("Поиск университетов по подстроке в названии, без учёта регистра")
  void shouldSearchUniversitiesByNameSubstring() {
    // Given
    UniversityDto harvardDto = UniversityDto.builder()
        .name("Harvard University")
        .regionId(regionId)
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
        .name("Oxford University")
        .regionId(regionId)
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

    UniversityDto mitDto = UniversityDto.builder()
        .name("MIT")
        .regionId(regionId)
        .description("Massachusetts Institute of Technology")
        .address(AddressDto.builder()
            .country("USA")
            .address("USA")
            .email("usa.gmail.com")
            .website("usa.edu")
            .region("Massachusetts")
            .city("Cambridge")
            .street("MIT Street, 1")
            .build())
        .build();

    universityService.createUniversity(harvardDto);
    universityService.createUniversity(oxfordDto);
    universityService.createUniversity(mitDto);

    // When
    Pageable pageable = PageRequest.of(0, 10);
    Page<UniversityDto> found = universityService.getUniversities("UniVer", null, pageable);

    // Then
    assertThat(found.getContent()).hasSize(2);
    assertThat(found.getContent())
        .extracting(UniversityDto::name)
        .containsExactlyInAnyOrder("Harvard University", "Oxford University");
  }

  @Test
  @DisplayName("Университет привязывается к региону при создании и обновлении")
  void shouldLinkUniversityToRegion() {
    Region dagestan = createRegion("05", "Республика Дагестан");
    Region moscow = createRegion("77", "Москва");

    UniversityDto saved = universityService.createUniversity(UniversityDto.builder()
        .name("ДГТУ")
        .regionId(dagestan.getId())
        .build());
    assertThat(universityService.getUniversityById(saved.id()).regionId()).isEqualTo(dagestan.getId());

    UniversityDto updated = universityService.updateUniversity(saved.id(), UniversityDto.builder()
        .name("ДГТУ")
        .regionId(moscow.getId())
        .build());
    assertThat(updated.regionId()).isEqualTo(moscow.getId());
  }

  @Test
  @DisplayName("Фильтр университетов по региону, в том числе вместе с поиском по названию")
  void shouldFilterUniversitiesByRegion() {
    Region dagestan = createRegion("05", "Республика Дагестан");
    Region moscow = createRegion("77", "Москва");

    universityService.createUniversity(UniversityDto.builder().name("ДГТУ").regionId(dagestan.getId()).build());
    universityService.createUniversity(UniversityDto.builder().name("ДГУ").regionId(dagestan.getId()).build());
    universityService.createUniversity(UniversityDto.builder().name("МГУ").regionId(moscow.getId()).build());

    Pageable pageable = PageRequest.of(0, 10);

    assertThat(universityService.getUniversities(null, dagestan.getId(), pageable).getContent())
        .extracting(UniversityDto::name)
        .containsExactlyInAnyOrder("ДГТУ", "ДГУ");
    assertThat(universityService.getUniversities("дгт", dagestan.getId(), pageable).getContent())
        .extracting(UniversityDto::name)
        .containsExactly("ДГТУ");
    assertThat(universityService.getUniversities("дгт", moscow.getId(), pageable).getContent()).isEmpty();
  }

  @Test
  @DisplayName("Число факультетов приходит в списке — с поиском, с регионом и без параметров — и по ID")
  void shouldReturnFacultyCount() {
    Region dagestan = createRegion("05", "Республика Дагестан");

    UUID withFaculties = universityService.createUniversity(
        UniversityDto.builder().name("ДГТУ").regionId(dagestan.getId()).build()).id();
    UUID withoutFaculties = universityService.createUniversity(
        UniversityDto.builder().name("ДГУ").regionId(dagestan.getId()).build()).id();
    createFaculty(withFaculties, "Факультет информатики");
    createFaculty(withFaculties, "Факультет права");

    Pageable pageable = PageRequest.of(0, 10);

    assertThat(universityService.getUniversities(null, null, pageable).getContent())
        .extracting(UniversityDto::id, UniversityDto::facultyCount)
        .containsExactlyInAnyOrder(tuple(withFaculties, 2L), tuple(withoutFaculties, 0L));
    assertThat(universityService.getUniversities(null, dagestan.getId(), pageable).getContent())
        .extracting(UniversityDto::id, UniversityDto::facultyCount)
        .containsExactlyInAnyOrder(tuple(withFaculties, 2L), tuple(withoutFaculties, 0L));
    assertThat(universityService.getUniversities("дгт", null, pageable).getContent())
        .extracting(UniversityDto::id, UniversityDto::facultyCount)
        .containsExactly(tuple(withFaculties, 2L));

    assertThat(universityService.getUniversityById(withFaculties).facultyCount()).isEqualTo(2L);
    assertThat(universityService.getUniversityById(withoutFaculties).facultyCount()).isZero();
  }

  @Test
  @DisplayName("Число факультетов из тела запроса игнорируется при создании и обновлении")
  void shouldIgnoreFacultyCountFromRequest() {
    UniversityDto saved = universityService.createUniversity(UniversityDto.builder()
        .name("ДГТУ")
        .regionId(regionId)
        .facultyCount(99)
        .build());
    assertThat(saved.facultyCount()).isZero();

    createFaculty(saved.id(), "Факультет информатики");

    UniversityDto updated = universityService.updateUniversity(saved.id(), UniversityDto.builder()
        .name("ДГТУ")
        .regionId(regionId)
        .facultyCount(99)
        .build());
    assertThat(updated.facultyCount()).isEqualTo(1L);
    assertThat(universityService.getUniversityById(saved.id()).facultyCount()).isEqualTo(1L);
  }

  private void createFaculty(UUID universityId, String name) {
    Faculty faculty = new Faculty();
    faculty.setName(name);
    faculty.setUniversity(universityRepository.getReferenceById(universityId));
    facultyRepository.save(faculty);
  }

  @Test
  @DisplayName("Исключение при создании университета с несуществующим регионом")
  void shouldThrowExceptionWhenRegionNotFound() {
    UniversityDto dto = UniversityDto.builder()
        .name("ДГТУ")
        .regionId(UUID.randomUUID())
        .build();

    assertThatThrownBy(() -> universityService.createUniversity(dto))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Region not found with id:");
  }

  @Test
  @DisplayName("Исключение при создании университета без региона")
  void shouldThrowExceptionWhenRegionIsMissing() {
    UniversityDto dto = UniversityDto.builder()
        .name("ДГТУ")
        .build();

    assertThatThrownBy(() -> universityService.createUniversity(dto))
        .isInstanceOf(ValidationException.class);
  }

  private Region createRegion(String code, String name) {
    Region region = new Region();
    region.setCode(code);
    region.setName(name);
    return regionRepository.save(region);
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