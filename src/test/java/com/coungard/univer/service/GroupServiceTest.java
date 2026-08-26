package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.EducationForm;
import com.coungard.univer.dto.GroupDto;
import com.coungard.univer.dto.SemesterType;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Program;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.GroupRepository;
import com.coungard.univer.repository.ProgramRepository;
import com.coungard.univer.repository.SemesterRepository;
import com.coungard.univer.repository.StudyYearRepository;
import com.coungard.univer.repository.UniversityRepository;
import java.time.LocalDate;
import java.time.Period;
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
class GroupServiceTest {

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
  private GroupService groupService;

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private SemesterRepository semesterRepository;

  @Autowired
  private StudyYearRepository studyYearRepository;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private UniversityRepository universityRepository;

  private UUID semesterId;

  @BeforeEach
  void setUp() {
    groupRepository.deleteAll();
    semesterRepository.deleteAll();
    studyYearRepository.deleteAll();
    programRepository.deleteAll();
    facultyRepository.deleteAll();
    universityRepository.deleteAll();

    University university = new University();
    university.setName("Test University");
    UUID universityId = universityRepository.save(university).getId();

    Faculty faculty = Faculty.builder()
        .name("Faculty of Computer Science")
        .university(universityRepository.getReferenceById(universityId))
        .build();
    UUID facultyId = facultyRepository.save(faculty).getId();

    Program program = new Program();
    program.setFacultyId(facultyId);
    program.setCode("09.03.04");
    program.setName("Software Engineering");
    program.setEducationLevel("Bachelor");
    program.setEducationForm(EducationForm.FULL_TIME);
    program.setDurationOfStudy(Period.ofYears(4));
    UUID programId = programRepository.save(program).getId();

    StudyYear studyYear = new StudyYear();
    studyYear.setProgram(programRepository.getReferenceById(programId));
    studyYear.setYearNumber(5);
    UUID studyYearId = studyYearRepository.save(studyYear).getId();

    Semester semester = new Semester();
    semester.setStudyYear(studyYearRepository.getReferenceById(studyYearId));
    semester.setType(SemesterType.AUTUMN);
    semester.setStartDate(LocalDate.of(2026, 9, 1));
    semester.setEndDate(LocalDate.of(2026, 12, 20));
    semesterId = semesterRepository.save(semester).getId();
  }

  @Test
  void shouldCreateAndRetrieveGroup() {
    // Given
    GroupDto dto = GroupDto.builder().semesterId(semesterId).name("У532 КСиТ").build();

    // When
    GroupDto created = groupService.createGroup(dto);
    GroupDto found = groupService.getGroupById(created.id());

    // Then
    assertThat(found).isNotNull();
    assertThat(found.semesterId()).isEqualTo(semesterId);
    assertThat(found.name()).isEqualTo("У532 КСиТ");
  }

  @Test
  void shouldThrowExceptionWhenCreatingGroupWithNonExistentSemester() {
    GroupDto dto = GroupDto.builder().semesterId(UUID.randomUUID()).name("У532 КСиТ").build();

    assertThatThrownBy(() -> groupService.createGroup(dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldThrowExceptionWhenGroupNotFound() {
    UUID randomId = UUID.randomUUID();
    assertThatThrownBy(() -> groupService.getGroupById(randomId))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldGetGroups() {
    // Given
    groupService.createGroup(GroupDto.builder().semesterId(semesterId).name("У532 КСиТ").build());
    groupService.createGroup(GroupDto.builder().semesterId(semesterId).name("У533 КСиТ").build());

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<GroupDto> result = groupService.getGroups(pageable);

    // Then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent())
        .extracting(GroupDto::name)
        .containsExactlyInAnyOrder("У532 КСиТ", "У533 КСиТ");
  }

  @Test
  void shouldGetGroupsBySemester() {
    // Given
    groupService.createGroup(GroupDto.builder().semesterId(semesterId).name("У532 КСиТ").build());

    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<GroupDto> result = groupService.getGroupsBySemester(semesterId, pageable);

    // Then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).name()).isEqualTo("У532 КСиТ");
  }

  @Test
  void shouldUpdateGroup() {
    // Given
    GroupDto original = groupService.createGroup(
        GroupDto.builder().semesterId(semesterId).name("У532 КСиТ").build());

    GroupDto updateDto = GroupDto.builder().semesterId(semesterId).name("У532 КСиТ (переим.)").build();

    // When
    GroupDto updated = groupService.updateGroup(original.id(), updateDto);

    // Then
    assertThat(updated.name()).isEqualTo("У532 КСиТ (переим.)");
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentGroup() {
    GroupDto dto = GroupDto.builder().semesterId(semesterId).name("У532 КСиТ").build();

    assertThatThrownBy(() -> groupService.updateGroup(UUID.randomUUID(), dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldDeleteGroupById() {
    // Given
    GroupDto group = groupService.createGroup(
        GroupDto.builder().semesterId(semesterId).name("У532 КСиТ").build());

    // When
    groupService.deleteGroup(group.id());

    // Then
    assertThat(groupRepository.findById(group.id())).isEmpty();
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentGroup() {
    assertThatThrownBy(() -> groupService.deleteGroup(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class);
  }
}
