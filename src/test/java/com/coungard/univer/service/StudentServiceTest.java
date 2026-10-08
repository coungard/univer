package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coungard.univer.TestRegions;
import com.coungard.univer.UniverApplication;
import com.coungard.univer.dto.EducationForm;
import com.coungard.univer.dto.SemesterType;
import com.coungard.univer.dto.StudentDto;
import com.coungard.univer.dto.registration.RegisterData;
import com.coungard.univer.dto.registration.RegisterStudentRequest;
import com.coungard.univer.dto.request.UpdateStudentProfileRequest;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Program;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ConflictException;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.GroupRepository;
import com.coungard.univer.repository.ProgramRepository;
import com.coungard.univer.repository.SemesterRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.StudyYearRepository;
import com.coungard.univer.repository.RegionRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.security.KeycloakAdminService;
import com.coungard.univer.security.Role;
import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
class StudentServiceTest {

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
  private StudentService studentService;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private UniversityRepository universityRepository;

  @Autowired
  private RegionRepository regionRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private StudyYearRepository studyYearRepository;

  @Autowired
  private SemesterRepository semesterRepository;

  @Autowired
  private GroupRepository groupRepository;

  @MockBean
  private KeycloakAdminService keycloakAdminService;

  private UUID universityId;

  @BeforeEach
  void setUp() {
    studentRepository.deleteAll();
    groupRepository.deleteAll();
    semesterRepository.deleteAll();
    studyYearRepository.deleteAll();
    programRepository.deleteAll();
    facultyRepository.deleteAll();
    universityRepository.deleteAll();

    University university = new University();
    university.setName("Test University");
    university.setRegion(TestRegions.create(regionRepository));
    universityId = universityRepository.save(university).getId();
  }

  @Test
  void shouldRegisterNewStudent() {
    // Given
    String mockKeycloakId = UUID.randomUUID().toString();
    RegisterStudentRequest registerDto = new RegisterStudentRequest(
        "ivan",
        "Иван",
        "Иванов",
        "Иванович",
        "ivan@example.com",
        "password123",
        LocalDate.now().minusYears(1),
        LocalDate.now().minusYears(20),
        universityId
    );

    // When: Мокаем ответ Keycloak
    when(keycloakAdminService.createUser(any(RegisterData.class))).thenReturn(mockKeycloakId);

    StudentDto registered = studentService.registerStudent(registerDto);

    // Then
    assertThat(registered.firstname()).isEqualTo("Иван");
    assertThat(registered.email()).isEqualTo("ivan@example.com");
    assertThat(registered.universityId()).isEqualTo(universityId);

    assertThat(studentRepository.findById(UUID.fromString(mockKeycloakId))).isPresent();

    verify(keycloakAdminService).createUser(any(RegisterData.class));
    verify(keycloakAdminService).assignRole(eq(mockKeycloakId), eq(Role.ROLE_STUDENT));
  }

  @Test
  void shouldBuildFullnameFromLastnameAndFirstnameWhenNotProvided() {
    String mockKeycloakId = UUID.randomUUID().toString();
    RegisterStudentRequest registerDto = new RegisterStudentRequest(
        "ivan",
        "Иван",
        "Иванов",
        null,
        "ivan@example.com",
        "password123",
        null,
        LocalDate.now().minusYears(20),
        universityId
    );
    when(keycloakAdminService.createUser(any(RegisterData.class))).thenReturn(mockKeycloakId);

    StudentDto registered = studentService.registerStudent(registerDto);

    assertThat(registered.fullname()).isEqualTo("Иванов Иван");
  }

  @Test
  void shouldRegisterStudentWithoutUniversity() {
    String mockKeycloakId = UUID.randomUUID().toString();
    RegisterStudentRequest registerDto = new RegisterStudentRequest(
        "ivan",
        "Иван",
        "Иванов",
        null,
        "ivan@example.com",
        "password123",
        null,
        LocalDate.now().minusYears(20),
        null
    );
    when(keycloakAdminService.createUser(any(RegisterData.class))).thenReturn(mockKeycloakId);

    StudentDto registered = studentService.registerStudent(registerDto);

    assertThat(registered.universityId()).isNull();
    assertThat(registered.groupId()).isNull();
    assertThat(studentService.getStudentById(registered.id()).universityId()).isNull();
    assertThat(studentService.getStudents(PageRequest.of(0, 10)).getContent()).hasSize(1);
  }

  @Test
  void shouldDeleteKeycloakUserWhenStudentCannotBeSaved() {
    String mockKeycloakId = UUID.randomUUID().toString();
    RegisterStudentRequest registerDto = new RegisterStudentRequest(
        "ivan",
        "И".repeat(300), // не помещается в колонку — БД отклоняет INSERT
        "Иванов",
        "Иванов Иван",
        "ivan@example.com",
        "password123",
        null,
        LocalDate.now().minusYears(20),
        universityId
    );
    when(keycloakAdminService.createUser(any(RegisterData.class))).thenReturn(mockKeycloakId);

    assertThatThrownBy(() -> studentService.registerStudent(registerDto))
        .isInstanceOf(RuntimeException.class);

    verify(keycloakAdminService).deleteUser(mockKeycloakId);
    assertThat(studentRepository.findById(UUID.fromString(mockKeycloakId))).isEmpty();
  }

  @Test
  void shouldReportTakenEmailAndUsernameAsConflictWithField() {
    createTestStudent("ivan", "Иван", "Иванов", LocalDate.of(2023, 9, 1));

    assertThatThrownBy(() -> studentService.registerStudent(registerRequest("petr", "иван.иванов@test.com")))
        .isInstanceOfSatisfying(ConflictException.class, ex -> assertThat(ex.getField()).isEqualTo("email"));
    assertThatThrownBy(() -> studentService.registerStudent(registerRequest("ivan", "petr@example.com")))
        .isInstanceOfSatisfying(ConflictException.class, ex -> assertThat(ex.getField()).isEqualTo("username"));
  }

  @Test
  void shouldPassThroughConflictReportedByKeycloak() {
    // Логин свободен среди студентов, но занят в Keycloak (например, преподавателем)
    when(keycloakAdminService.createUser(any(RegisterData.class)))
        .thenThrow(new ConflictException("username", "Пользователь с таким логином уже существует: petr"));

    assertThatThrownBy(() -> studentService.registerStudent(registerRequest("petr", "petr@example.com")))
        .isInstanceOfSatisfying(ConflictException.class, ex -> assertThat(ex.getField()).isEqualTo("username"));
  }

  private RegisterStudentRequest registerRequest(String username, String email) {
    return new RegisterStudentRequest(
        username,
        "Пётр",
        "Петров",
        null,
        email,
        "password123",
        null,
        LocalDate.now().minusYears(20),
        universityId
    );
  }

  @Test
  void shouldGetStudentsWithPaginationAndFiltering() {
    // Given
    createTestStudent("anna", "Анна", "Смирнова", LocalDate.of(2023, 9, 1));
    createTestStudent("ivan", "Иван", "Иванов", LocalDate.of(2023, 9, 1));

    Pageable pageable = PageRequest.of(0, 10);
    // When: Поиск по имени "Иван"
    Page<StudentDto> result = studentService.getStudents(pageable);

    // Then
    assertThat(result.getContent()).hasSize(2);
  }

  @Test
  void shouldUpdateStudent() {
    // Given
    StudentDto original = createTestStudent("ivan", "Иван", "Иванов", LocalDate.now());

    StudentDto updateDto = StudentDto.builder()
        .id(original.id())
        .firstname("Петр")
        .lastname("Петров")
        .email("petr@example.com")
        .enrollmentDate(LocalDate.now().minusDays(1))
        .universityId(universityId)
        .build();

    // When
    StudentDto updated = studentService.updateStudent(original.id(), updateDto);

    // Then
    assertThat(updated.firstname()).isEqualTo("Петр");
    assertThat(updated.lastname()).isEqualTo("Петров");
    assertThat(updated.email()).isEqualTo("petr@example.com");
  }

  @Test
  void shouldAssignGroupToStudent() {
    // Given
    StudentDto original = createTestStudent("ivan", "Иван", "Иванов", LocalDate.now());
    Group group = createTestGroup("У532 КСиТ");

    StudentDto updateDto = StudentDto.builder()
        .id(original.id())
        .firstname(original.firstname())
        .lastname(original.lastname())
        .email(original.email())
        .enrollmentDate(original.enrollmentDate())
        .universityId(universityId)
        .groupId(group.getId())
        .build();

    // When
    StudentDto updated = studentService.updateStudent(original.id(), updateDto);

    // Then
    assertThat(updated.groupId()).isEqualTo(group.getId());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingStudentWithNonExistentGroup() {
    // Given
    StudentDto original = createTestStudent("ivan", "Иван", "Иванов", LocalDate.now());

    StudentDto updateDto = StudentDto.builder()
        .id(original.id())
        .firstname(original.firstname())
        .lastname(original.lastname())
        .email(original.email())
        .enrollmentDate(original.enrollmentDate())
        .universityId(universityId)
        .groupId(UUID.randomUUID())
        .build();

    // When & Then
    assertThatThrownBy(() -> studentService.updateStudent(original.id(), updateDto))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Group not found");
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentStudent() {
    StudentDto dto = StudentDto.builder()
        .id(UUID.randomUUID())
        .username("petr")
        .firstname("Петр")
        .lastname("Петров")
        .email("petr@example.com")
        .enrollmentDate(LocalDate.now().minusDays(1))
        .universityId(universityId)
        .build();

    assertThatThrownBy(() -> studentService.updateStudent(dto.id(), dto))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Student not found");
  }

  // === PATCH /students/me ===

  @Test
  void shouldFillProfileStepByStepFromUniversityToGroup() {
    UUID studentId = createTestStudent("ivan", "Иван", "Иванов", null, null).id();
    UUID facultyId = createTestFaculty(universityId);
    Group group = createTestGroup("У232 КСиТ", facultyId, 2);

    // Остановиться можно на любом шаге — профиль остаётся валидным
    StudentDto withUniversity = studentService.updateMyProfile(studentId, profile().university(universityId).build());
    assertThat(withUniversity.universityId()).isEqualTo(universityId);
    assertThat(withUniversity.facultyId()).isNull();

    StudentDto withFaculty = studentService.updateMyProfile(studentId, profile().faculty(facultyId).build());
    assertThat(withFaculty.facultyId()).isEqualTo(facultyId);
    assertThat(withFaculty.yearNumber()).isNull();

    StudentDto withYear = studentService.updateMyProfile(studentId, profile().year(2).build());
    assertThat(withYear.yearNumber()).isEqualTo(2);
    assertThat(withYear.groupId()).isNull();

    StudentDto withGroup = studentService.updateMyProfile(studentId, profile().group(group.getId()).build());
    assertThat(withGroup.universityId()).isEqualTo(universityId);
    assertThat(withGroup.facultyId()).isEqualTo(facultyId);
    assertThat(withGroup.yearNumber()).isEqualTo(2);
    assertThat(withGroup.groupId()).isEqualTo(group.getId());
  }

  @Test
  void shouldFillUniversityFacultyAndYearFromGroup() {
    UUID studentId = createTestStudent("ivan", "Иван", "Иванов", null, null).id();
    UUID facultyId = createTestFaculty(universityId);
    Group group = createTestGroup("У332 КСиТ", facultyId, 3);

    StudentDto updated = studentService.updateMyProfile(studentId, profile().group(group.getId()).build());

    assertThat(updated.universityId()).isEqualTo(universityId);
    assertThat(updated.facultyId()).isEqualTo(facultyId);
    assertThat(updated.yearNumber()).isEqualTo(3);
    assertThat(updated.groupId()).isEqualTo(group.getId());
  }

  @Test
  void shouldNotChangeFieldsMissingFromRequest() {
    UUID studentId = createStudentInGroup(2).id();
    StudentDto before = studentService.getStudentById(studentId);

    StudentDto updated = studentService.updateMyProfile(studentId, profile().build());

    assertThat(updated.universityId()).isEqualTo(before.universityId());
    assertThat(updated.facultyId()).isEqualTo(before.facultyId());
    assertThat(updated.yearNumber()).isEqualTo(2);
    assertThat(updated.groupId()).isEqualTo(before.groupId());
  }

  @Test
  void shouldKeepLowerFieldsWhenSameValueIsSentAgain() {
    StudentDto student = createStudentInGroup(2);

    StudentDto updated = studentService.updateMyProfile(student.id(),
        profile().university(student.universityId()).faculty(student.facultyId()).year(2).build());

    assertThat(updated.groupId()).isEqualTo(student.groupId());
  }

  @Test
  void shouldResetFacultyYearAndGroupWhenUniversityChanges() {
    UUID studentId = createStudentInGroup(2).id();
    UUID otherUniversityId = createOtherUniversity().getId();

    StudentDto updated = studentService.updateMyProfile(studentId, profile().university(otherUniversityId).build());

    assertThat(updated.universityId()).isEqualTo(otherUniversityId);
    assertThat(updated.facultyId()).isNull();
    assertThat(updated.yearNumber()).isNull();
    assertThat(updated.groupId()).isNull();
  }

  @Test
  void shouldResetYearAndGroupWhenFacultyChanges() {
    StudentDto student = createStudentInGroup(2);
    UUID otherFacultyId = createTestFaculty(universityId);

    StudentDto updated = studentService.updateMyProfile(student.id(), profile().faculty(otherFacultyId).build());

    assertThat(updated.universityId()).isEqualTo(universityId);
    assertThat(updated.facultyId()).isEqualTo(otherFacultyId);
    assertThat(updated.yearNumber()).isNull();
    assertThat(updated.groupId()).isNull();
  }

  @Test
  void shouldResetGroupWhenYearChanges() {
    StudentDto student = createStudentInGroup(2);

    StudentDto updated = studentService.updateMyProfile(student.id(), profile().year(3).build());

    assertThat(updated.facultyId()).isEqualTo(student.facultyId());
    assertThat(updated.yearNumber()).isEqualTo(3);
    assertThat(updated.groupId()).isNull();
  }

  @Test
  void shouldClearFieldAndEverythingBelowOnExplicitNull() {
    StudentDto student = createStudentInGroup(2);

    StudentDto withoutGroup = studentService.updateMyProfile(student.id(), profile().group(null).build());
    assertThat(withoutGroup.groupId()).isNull();
    assertThat(withoutGroup.yearNumber()).isEqualTo(2);

    StudentDto withoutUniversity = studentService.updateMyProfile(student.id(), profile().university(null).build());
    assertThat(withoutUniversity.universityId()).isNull();
    assertThat(withoutUniversity.facultyId()).isNull();
    assertThat(withoutUniversity.yearNumber()).isNull();
  }

  @Test
  void shouldMoveToAnotherUniversityInSingleRequest() {
    UUID studentId = createStudentInGroup(2).id();
    UUID otherUniversityId = createOtherUniversity().getId();
    UUID otherFacultyId = createTestFaculty(otherUniversityId);
    Group otherGroup = createTestGroup("Б101", otherFacultyId, 1);

    StudentDto updated = studentService.updateMyProfile(studentId, profile()
        .university(otherUniversityId)
        .faculty(otherFacultyId)
        .year(1)
        .group(otherGroup.getId())
        .build());

    assertThat(updated.universityId()).isEqualTo(otherUniversityId);
    assertThat(updated.facultyId()).isEqualTo(otherFacultyId);
    assertThat(updated.yearNumber()).isEqualTo(1);
    assertThat(updated.groupId()).isEqualTo(otherGroup.getId());
  }

  @Test
  void shouldRejectFacultyOfAnotherUniversity() {
    UUID studentId = createTestStudent("ivan", "Иван", "Иванов", null).id();
    UUID foreignFacultyId = createTestFaculty(createOtherUniversity().getId());

    assertThatThrownBy(() -> studentService.updateMyProfile(studentId, profile().faculty(foreignFacultyId).build()))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("Факультет не относится к университету");
  }

  @Test
  void shouldRejectYearWithoutFaculty() {
    UUID studentId = createTestStudent("ivan", "Иван", "Иванов", null).id();

    assertThatThrownBy(() -> studentService.updateMyProfile(studentId, profile().year(1).build()))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("не выбран факультет");
  }

  @Test
  void shouldRejectGroupOfAnotherFacultyOrYear() {
    StudentDto student = createStudentInGroup(2);
    Group sameFacultyOtherYear = createTestGroup("У332 КСиТ", student.facultyId(), 3);
    Group otherFaculty = createTestGroup("Э201", createTestFaculty(universityId), 2);

    assertThatThrownBy(
        () -> studentService.updateMyProfile(student.id(), profile().group(sameFacultyOtherYear.getId()).build()))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("курсу");
    assertThatThrownBy(
        () -> studentService.updateMyProfile(student.id(), profile().group(otherFaculty.getId()).build()))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("Группа не относится к факультету");

    // Отклонённый запрос ничего не меняет
    assertThat(studentService.getStudentById(student.id()).groupId()).isEqualTo(student.groupId());
  }

  @Test
  void shouldThrowNotFoundForUnknownIdsInProfile() {
    UUID studentId = createTestStudent("ivan", "Иван", "Иванов", null).id();

    assertThatThrownBy(
        () -> studentService.updateMyProfile(studentId, profile().university(UUID.randomUUID()).build()))
        .isInstanceOf(ResourceNotFoundException.class);
    assertThatThrownBy(() -> studentService.updateMyProfile(studentId, profile().faculty(UUID.randomUUID()).build()))
        .isInstanceOf(ResourceNotFoundException.class);
    assertThatThrownBy(() -> studentService.updateMyProfile(studentId, profile().group(UUID.randomUUID()).build()))
        .isInstanceOf(ResourceNotFoundException.class);
    assertThatThrownBy(() -> studentService.updateMyProfile(UUID.randomUUID(), profile().build()))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  /** Студент с полностью заполненным профилем: университет, факультет, курс и группа этого курса. */
  private StudentDto createStudentInGroup(int yearNumber) {
    UUID studentId = createTestStudent("ivan", "Иван", "Иванов", null).id();
    Group group = createTestGroup("У532 КСиТ", createTestFaculty(universityId), yearNumber);
    return studentService.updateMyProfile(studentId, profile().group(group.getId()).build());
  }

  private static ProfileRequestBuilder profile() {
    return new ProfileRequestBuilder();
  }

  /** Собирает тело PATCH: вызванный метод — поле «передано» (в том числе с null), невызванный — нет. */
  private static class ProfileRequestBuilder {

    private final UpdateStudentProfileRequest request = new UpdateStudentProfileRequest();

    ProfileRequestBuilder university(UUID universityId) {
      request.setUniversityId(universityId);
      return this;
    }

    ProfileRequestBuilder faculty(UUID facultyId) {
      request.setFacultyId(facultyId);
      return this;
    }

    ProfileRequestBuilder year(Integer yearNumber) {
      request.setYearNumber(yearNumber);
      return this;
    }

    ProfileRequestBuilder group(UUID groupId) {
      request.setGroupId(groupId);
      return this;
    }

    UpdateStudentProfileRequest build() {
      return request;
    }
  }

  @Test
  void shouldDeleteStudentById() {
    // Given
    StudentDto student = createTestStudent("ivan", "Иван", "Иванов", LocalDate.now());

    // When
    studentService.deleteStudentById(student.id());

    // Then
    assertThat(studentRepository.findById(student.id())).isEmpty();
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentStudent() {
    // When & Then
    assertThatThrownBy(() -> studentService.deleteStudentById(UUID.randomUUID()))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Student not found");
  }

  // === Вспомогательные методы ===

  private StudentDto createTestStudent(String username, String firstName, String lastName, LocalDate enrollmentDate) {
    return createTestStudent(username, firstName, lastName, enrollmentDate, universityId);
  }

  private StudentDto createTestStudent(String username, String firstName, String lastName, LocalDate enrollmentDate,
      UUID universityId) {
    Student student = new Student();

    Person person = new Person();
    person.setUsername(username);
    person.setFirstname(firstName);
    person.setLastname(lastName);
    person.setEmail((firstName + "." + lastName + "@test.com").toLowerCase());

    student.setPerson(person);
    student.setEnrollmentDate(enrollmentDate);
    if (universityId != null) {
      University uni = new University();
      uni.setId(universityId);
      student.setUniversity(uni);
    }

    Student saved = studentRepository.save(student);

    return StudentDto.builder()
        .id(saved.getId())
        .username(saved.getPerson().getUsername())
        .firstname(saved.getPerson().getFirstname())
        .lastname(saved.getPerson().getLastname())
        .email(saved.getPerson().getEmail())
        .enrollmentDate(saved.getEnrollmentDate())
        .universityId(universityId)
        .build();
  }

  private University createOtherUniversity() {
    University university = new University();
    university.setName("Other University");
    university.setRegion(TestRegions.create(regionRepository));
    return universityRepository.save(university);
  }

  private Group createTestGroup(String name) {
    return createTestGroup(name, createTestFaculty(universityId), 5);
  }

  private UUID createTestFaculty(UUID universityId) {
    Faculty faculty = Faculty.builder()
        .name("Faculty of Computer Science")
        .university(universityRepository.getReferenceById(universityId))
        .build();
    return facultyRepository.save(faculty).getId();
  }

  private Group createTestGroup(String name, UUID facultyId, int yearNumber) {
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
    studyYear.setYearNumber(yearNumber);
    UUID studyYearId = studyYearRepository.save(studyYear).getId();

    Semester semester = new Semester();
    semester.setStudyYear(studyYearRepository.getReferenceById(studyYearId));
    semester.setType(SemesterType.AUTUMN);
    semester.setStartDate(LocalDate.of(2026, 9, 1));
    semester.setEndDate(LocalDate.of(2026, 12, 20));
    UUID semesterId = semesterRepository.save(semester).getId();

    Group group = new Group();
    group.setSemester(semesterRepository.getReferenceById(semesterId));
    group.setName(name);
    return groupRepository.save(group);
  }
}
