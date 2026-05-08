package com.coungard.univer.service.impl;

import com.coungard.univer.dto.StudentDto;
import com.coungard.univer.dto.mapper.StudentMapper;
import com.coungard.univer.dto.registration.RegisterData;
import com.coungard.univer.dto.registration.RegisterStudentRequest;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.security.KeycloakAdminService;
import com.coungard.univer.security.Role;
import com.coungard.univer.service.StudentService;
import com.coungard.univer.validation.StudentValidator;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

  private final StudentRepository studentRepository;
  private final UniversityRepository universityRepository;
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
      University university = universityRepository.findById(registerStudentRequest.universityId())
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
      person.setFullname(registerStudentRequest.fullname());
      person.setEmail(registerStudentRequest.email());

      student.setPerson(person);

      Student saved = studentRepository.save(student);
      return studentMapper.toDto(saved);
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
    existing.setEnrollmentDate(studentDto.enrollmentDate());

    University university = universityRepository.findById(studentDto.universityId())
        .orElseThrow(() -> new ResourceNotFoundException("University not found with id: " + studentDto.universityId()));
    existing.setUniversity(university);

    Student updated = studentRepository.save(existing);
    return studentMapper.toDto(updated);
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