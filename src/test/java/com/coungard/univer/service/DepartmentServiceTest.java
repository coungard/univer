package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.DepartmentDto;
import com.coungard.univer.entity.Department;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.repository.DepartmentRepository;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.UniversityRepository;
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
class DepartmentServiceTest {

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
  private DepartmentService departmentService;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private UniversityRepository universityRepository;

  private UUID facultyId;

  @BeforeEach
  void setUp() {
    departmentRepository.deleteAll();
    facultyRepository.deleteAll();
    universityRepository.deleteAll();

    University university = new University();
    university.setName("Test University");
    UUID universityId = universityRepository.save(university).getId();

    facultyId = createTestFaculty("Faculty of Computer Science", universityId).getId();
  }

  @Test
  void shouldCreateAndRetrieveDepartment() {
    // Given
    DepartmentDto dto = DepartmentDto.builder()
        .name("Department of Algorithms")
        .description("Algorithms and data structures")
        .facultyId(facultyId)
        .build();

    // When
    DepartmentDto created = departmentService.createDepartment(dto);
    DepartmentDto found = departmentService.getDepartmentById(created.id());

    // Then
    assertThat(found).isNotNull();
    assertThat(found.name()).isEqualTo("Department of Algorithms");
    assertThat(found.description()).isEqualTo("Algorithms and data structures");
    assertThat(found.facultyId()).isEqualTo(facultyId);
  }

  @Test
  void shouldThrowExceptionWhenCreatingDepartmentWithNonExistentFaculty() {
    DepartmentDto dto = DepartmentDto.builder()
        .name("Department of Physics")
        .facultyId(UUID.randomUUID())
        .build();

    assertThatThrownBy(() -> departmentService.createDepartment(dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenCreatingDuplicateDepartmentInSameFaculty() {
    // Given
    createTestDepartment("Department of Networks", facultyId);

    DepartmentDto duplicate = DepartmentDto.builder()
        .name("Department of Networks")
        .facultyId(facultyId)
        .build();

    // When & Then
    assertThatThrownBy(() -> departmentService.createDepartment(duplicate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldGetDepartmentsByFaculty() {
    // Given
    createTestDepartment("Department of Mathematics", facultyId);
    createTestDepartment("Department of Physics", facultyId);

    // When
    Pageable pageable = PageRequest.of(0, 10);
    Page<DepartmentDto> departments = departmentService.getDepartmentsByFaculty(facultyId, pageable);

    // Then
    assertThat(departments.getContent()).hasSize(2);
    assertThat(departments.getContent())
        .extracting(DepartmentDto::name)
        .containsExactlyInAnyOrder("Department of Mathematics", "Department of Physics");
  }

  @Test
  void shouldGetDepartmentsByUniversity() {
    // Given
    University otherUniversity = new University();
    otherUniversity.setName("Other University");
    UUID otherUniversityId = universityRepository.save(otherUniversity).getId();
    UUID otherFacultyId = createTestFaculty("Faculty of Law", otherUniversityId).getId();

    createTestDepartment("Department of Chemistry", facultyId);
    createTestDepartment("Department of Civil Law", otherFacultyId);

    University myUniversity = facultyRepository.findById(facultyId).orElseThrow().getUniversity();

    // When
    Pageable pageable = PageRequest.of(0, 10);
    Page<DepartmentDto> departments = departmentService.getDepartmentsByUniversity(myUniversity.getId(), pageable);

    // Then
    assertThat(departments.getContent()).hasSize(1);
    assertThat(departments.getContent().get(0).name()).isEqualTo("Department of Chemistry");
  }

  @Test
  void shouldUpdateDepartment() {
    // Given
    DepartmentDto original = createDepartmentViaService("Department of Biology");

    DepartmentDto updateDto = DepartmentDto.builder()
        .name("Department of Applied Biology")
        .description("Renamed department")
        .facultyId(facultyId)
        .build();

    // When
    DepartmentDto updated = departmentService.updateDepartment(original.id(), updateDto);

    // Then
    assertThat(updated.name()).isEqualTo("Department of Applied Biology");
    assertThat(updated.description()).isEqualTo("Renamed department");
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentDepartment() {
    DepartmentDto dto = DepartmentDto.builder()
        .name("Department of History")
        .facultyId(facultyId)
        .build();

    assertThatThrownBy(() -> departmentService.updateDepartment(UUID.randomUUID(), dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenRenamingToExistingDepartmentNameInSameFaculty() {
    // Given
    createTestDepartment("Department of Geology", facultyId);
    DepartmentDto toRename = createDepartmentViaService("Department of Astronomy");

    DepartmentDto updateDto = DepartmentDto.builder()
        .name("Department of Geology")
        .facultyId(facultyId)
        .build();

    // When & Then
    assertThatThrownBy(() -> departmentService.updateDepartment(toRename.id(), updateDto))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldDeleteDepartmentById() {
    // Given
    DepartmentDto department = createDepartmentViaService("Department of Economics");

    // When
    departmentService.deleteDepartment(department.id());

    // Then
    assertThat(departmentRepository.findById(department.id())).isEmpty();
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentDepartment() {
    assertThatThrownBy(() -> departmentService.deleteDepartment(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  // === Вспомогательные методы ===

  private DepartmentDto createDepartmentViaService(String name) {
    return departmentService.createDepartment(DepartmentDto.builder()
        .name(name)
        .facultyId(facultyId)
        .build());
  }

  private Department createTestDepartment(String name, UUID facultyId) {
    Faculty faculty = facultyRepository.getReferenceById(facultyId);

    Department department = new Department();
    department.setName(name);
    department.setFaculty(faculty);

    return departmentRepository.save(department);
  }

  private Faculty createTestFaculty(String name, UUID universityId) {
    University university = universityRepository.getReferenceById(universityId);

    Faculty faculty = Faculty.builder()
        .name(name)
        .university(university)
        .build();

    return facultyRepository.save(faculty);
  }
}
