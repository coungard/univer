package com.coungard.univer.service.impl;

import com.coungard.univer.dto.StudentDto;
import com.coungard.univer.mapper.StudentMapper;
import com.coungard.univer.dto.registration.RegisterData;
import com.coungard.univer.dto.registration.RegisterStudentRequest;
import com.coungard.univer.dto.request.UpdateStudentProfileRequest;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ConflictException;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.GroupRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.security.KeycloakAdminService;
import com.coungard.univer.security.Role;
import com.coungard.univer.service.StudentService;
import com.coungard.univer.validation.StudentValidator;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

  private final StudentRepository studentRepository;
  private final UniversityRepository universityRepository;
  private final FacultyRepository facultyRepository;
  private final GroupRepository groupRepository;
  private final StudentMapper studentMapper;
  private final StudentValidator studentValidator;

  private final KeycloakAdminService keycloakAdminService;

  @Override
  @Transactional(readOnly = true)
  public Page<StudentDto> getStudents(Pageable pageable) {

    return studentRepository.findAll(pageable).map(studentMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public StudentDto getStudentById(UUID id) {
    Student student = studentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    return studentMapper.toDto(student);
  }

  @Override
  @Transactional
  public StudentDto registerStudent(RegisterStudentRequest registerStudentRequest) {

    String keycloakUserId = null;
    studentValidator.validateRegisterData(registerStudentRequest);
    try {
      // Университет необязателен: в многошаговой регистрации аккаунт создаётся до выбора вуза
      University university = registerStudentRequest.universityId() == null
          ? null
          : universityRepository.findById(registerStudentRequest.universityId())
              .orElseThrow(() -> new ResourceNotFoundException("University not found"));

      // 2. Создаём пользователя в Keycloak
      keycloakUserId = keycloakAdminService.createUser(RegisterData.builder()
          .username(registerStudentRequest.username())
          .email(registerStudentRequest.email())
          .password(registerStudentRequest.password())
          .firstname(registerStudentRequest.firstname())
          .lastname(registerStudentRequest.lastname())
          .build());

      // 3. Назначаем роль STUDENT
      keycloakAdminService.assignRole(keycloakUserId, Role.ROLE_STUDENT);

      Student student = new Student();
      student.setId(UUID.fromString(keycloakUserId)); // Используем Keycloak ID как ID студента
      student.setEnrollmentDate(registerStudentRequest.enrollmentDate());
      student.setUniversity(university);

      Person person = new Person();
      person.setUsername(registerStudentRequest.username().toLowerCase());
      person.setFirstname(registerStudentRequest.firstname());
      person.setLastname(registerStudentRequest.lastname());
      // fullname необязателен в запросе, но обязателен в БД (persons.fullname NOT NULL)
      person.setFullname(StringUtils.hasText(registerStudentRequest.fullname())
          ? registerStudentRequest.fullname()
          : registerStudentRequest.lastname() + " " + registerStudentRequest.firstname());
      person.setEmail(registerStudentRequest.email());
      person.setBirthday(registerStudentRequest.birthday());

      student.setPerson(person);

      // saveAndFlush, а не save: у студента заранее заданный ID, поэтому INSERT иначе откладывается до
      // коммита транзакции — уже за пределами этого try, и откат пользователя в Keycloak не срабатывает
      Student saved = studentRepository.saveAndFlush(student);
      return studentMapper.toDto(saved);
    } catch (ConflictException ex) {
      // Логин или email занят в Keycloak: пользователь не создан, откатывать нечего — отдаём 409 как есть
      throw ex;
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      // Откат: если Keycloak-пользователь был создан, но БД упала
      if (keycloakUserId != null) {
        try {
          keycloakAdminService.deleteUser(keycloakUserId);
          log.info("Пользователь в Keycloak удалён после сбоя в БД: " + keycloakUserId);
        } catch (Exception cleanupEx) {
          log.warn("Не удалось удалить пользователя в Keycloak: " + keycloakUserId);
          log.error(cleanupEx.getMessage(), cleanupEx);
        }
      }
      throw new RuntimeException("Ошибка при регистрации студента: " + ex.getMessage());
    }
  }

  @Override
  @Transactional
  public StudentDto updateStudent(UUID id, StudentDto studentDto) {
    Student existing = studentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

    existing.getPerson().setFirstname(studentDto.firstname());
    existing.getPerson().setLastname(studentDto.lastname());
    existing.getPerson().setFullname(studentDto.fullname());
    existing.getPerson().setEmail(studentDto.email());
    existing.getPerson().setBirthday(studentDto.birthday());
    existing.setEnrollmentDate(studentDto.enrollmentDate());

    if (studentDto.universityId() == null) {
      existing.setUniversity(null);
    } else {
      University university = universityRepository.findById(studentDto.universityId())
          .orElseThrow(
              () -> new ResourceNotFoundException("University not found with id: " + studentDto.universityId()));
      existing.setUniversity(university);
    }

    if (studentDto.groupId() == null) {
      existing.setGroup(null);
    } else {
      Group group = groupRepository.findById(studentDto.groupId())
          .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + studentDto.groupId()));
      existing.setGroup(group);
    }

    Student updated = studentRepository.save(existing);
    return studentMapper.toDto(updated);
  }

  @Override
  @Transactional
  public StudentDto updateMyProfile(UUID id, UpdateStudentProfileRequest request) {
    Student student = studentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Студент не найден с ID: " + id));

    // Строго сверху вниз по цепочке: каждый шаг сбрасывает всё, что ниже, а следующие шаги того же
    // запроса заполняют это заново
    if (request.isUniversityIdSet()) {
      changeUniversity(student, request.getUniversityId());
    }
    if (request.isFacultyIdSet()) {
      changeFaculty(student, request.getFacultyId());
    }
    if (request.isYearNumberSet()) {
      changeYearNumber(student, request.getYearNumber());
    }
    if (request.isGroupIdSet()) {
      changeGroup(student, request.getGroupId());
    }

    Student updated = studentRepository.save(student);
    return studentMapper.toDto(updated);
  }

  private void changeUniversity(Student student, UUID universityId) {
    UUID currentId = student.getUniversity() != null ? student.getUniversity().getId() : null;
    if (Objects.equals(currentId, universityId)) {
      return;
    }
    University university = universityId == null
        ? null
        : universityRepository.findById(universityId)
            .orElseThrow(() -> new ResourceNotFoundException("Университет не найден с ID: " + universityId));

    student.setUniversity(university);
    student.setFaculty(null);
    student.setYearNumber(null);
    student.setGroup(null);
  }

  private void changeFaculty(Student student, UUID facultyId) {
    UUID currentId = student.getFaculty() != null ? student.getFaculty().getId() : null;
    if (Objects.equals(currentId, facultyId)) {
      return;
    }
    Faculty faculty = facultyId == null
        ? null
        : facultyRepository.findById(facultyId)
            .orElseThrow(() -> new ResourceNotFoundException("Факультет не найден с ID: " + facultyId));
    if (faculty != null) {
      alignUniversity(student, faculty, "Факультет не относится к университету студента");
    }

    student.setFaculty(faculty);
    student.setYearNumber(null);
    student.setGroup(null);
  }

  private void changeYearNumber(Student student, Integer yearNumber) {
    if (Objects.equals(student.getYearNumber(), yearNumber)) {
      return;
    }
    if (yearNumber != null && student.getFaculty() == null) {
      throw new ValidationException("Нельзя выбрать курс, пока не выбран факультет");
    }

    student.setYearNumber(yearNumber);
    student.setGroup(null);
  }

  private void changeGroup(Student student, UUID groupId) {
    if (groupId == null) {
      student.setGroup(null);
      return;
    }
    Group group = groupRepository.findById(groupId)
        .orElseThrow(() -> new ResourceNotFoundException("Группа не найдена с ID: " + groupId));

    // Группа привязана к факультету только по цепочке: семестр → учебный год → факультет
    StudyYear studyYear = group.getSemester().getStudyYear();
    UUID groupFacultyId = studyYear.getFacultyId();

    if (student.getFaculty() == null) {
      Faculty faculty = facultyRepository.findById(groupFacultyId)
          .orElseThrow(() -> new ResourceNotFoundException("Факультет не найден с ID: " + groupFacultyId));
      alignUniversity(student, faculty, "Группа не относится к университету студента");
      student.setFaculty(faculty);
    } else if (!student.getFaculty().getId().equals(groupFacultyId)) {
      throw new ValidationException("Группа не относится к факультету студента");
    }

    if (student.getYearNumber() == null) {
      student.setYearNumber(studyYear.getYearNumber());
    } else if (!student.getYearNumber().equals(studyYear.getYearNumber())) {
      throw new ValidationException("Группа относится к " + studyYear.getYearNumber()
          + " курсу, а у студента выбран " + student.getYearNumber());
    }

    student.setGroup(group);
  }

  /**
   * Согласовать университет студента с факультетом: если университет ещё не выбран — проставить
   * университет факультета, иначе они обязаны совпадать.
   */
  private void alignUniversity(Student student, Faculty faculty, String mismatchMessage) {
    if (student.getUniversity() == null) {
      student.setUniversity(faculty.getUniversity());
    } else if (!student.getUniversity().getId().equals(faculty.getUniversity().getId())) {
      throw new ValidationException(mismatchMessage);
    }
  }

  @Override
  @Transactional
  public void deleteStudentById(UUID id) {
    if (!studentRepository.existsById(id)) {
      throw new ResourceNotFoundException("Student not found with id: " + id);
    }
    studentRepository.deleteById(id);
    keycloakAdminService.deleteUser(id.toString());
  }
}