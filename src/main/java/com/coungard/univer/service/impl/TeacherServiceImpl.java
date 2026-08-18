package com.coungard.univer.service.impl;

import com.coungard.univer.dto.TeacherDto;
import com.coungard.univer.dto.registration.RegisterData;
import com.coungard.univer.dto.registration.RegisterTeacherRequest;
import com.coungard.univer.entity.Department;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Teacher;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.mapper.TeacherMapper;
import com.coungard.univer.repository.DepartmentRepository;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.TeacherRepository;
import com.coungard.univer.security.KeycloakAdminService;
import com.coungard.univer.security.Role;
import com.coungard.univer.service.TeacherService;
import com.coungard.univer.validation.TeacherValidator;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

  private final TeacherRepository teacherRepository;
  private final FacultyRepository facultyRepository;
  private final DepartmentRepository departmentRepository;
  private final KeycloakAdminService keycloakAdminService;
  private final TeacherValidator teacherValidator;
  private final TeacherMapper teacherMapper;

  @Override
  public TeacherDto getTeacherById(UUID id) {
    Teacher teacher = teacherRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Преподаватель не найден с ID: " + id));
    return teacherMapper.toDto(teacher);
  }

  @Override
  public Page<TeacherDto> getTeachers(Pageable pageable) {
    Page<Teacher> teachers = teacherRepository.findAll(pageable);
    return teachers.map(teacherMapper::toDto);
  }

  @Override
  public TeacherDto registerTeacher(RegisterTeacherRequest request) {
    teacherValidator.validateRegisterTeacher(request);

    Department department = departmentRepository.findById(request.getDepartmentId())
        .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
    Faculty faculty = department.getFaculty();

    String keycloakId = keycloakAdminService.createUser(RegisterData.builder()
        .username(request.getUsername())
        .email(request.getEmail())
        .password(request.getPassword())
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .build());
    keycloakAdminService.assignRole(keycloakId, Role.ROLE_TEACHER);

    Teacher teacher = teacherMapper.fromRegisterToEntity(request);
    teacher.setFaculty(faculty);
    teacher.setRegistered(true); // Устанавливаем флаг зарегистрированности
    teacher.setId(UUID.fromString(keycloakId)); // Используем Keycloak ID как ID студента

    Teacher saved = teacherRepository.save(teacher);
    return teacherMapper.toDto(saved);
  }

  @Override
  // TODO Данный метод нужен для создания студентов без регистрации в системе для привязки к лекциям
  public TeacherDto createTeacher(TeacherDto teacherDto) {
    if (teacherRepository.existsByPersonEmail(teacherDto.email())) {
      throw new ValidationException("Преподаватель с таким email уже существует: " + teacherDto.email());
    }
    Faculty faculty = facultyRepository.findById(teacherDto.facultyId())
        .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));

    Teacher teacher = new Teacher();
    teacher.setFaculty(faculty);
    teacher.setPosition(teacherDto.position());
    teacher.setRegistered(false);

    Person person = new Person();

    person.setUsername(teacherDto.username());
    person.setFirstname(teacherDto.firstname());
    person.setLastname(teacherDto.lastname());
    person.setFullname(teacherDto.fullname());
    person.setEmail(teacherDto.email());
    person.setPhone(teacherDto.phone());

    teacher.setPerson(person);

    Teacher saved = teacherRepository.save(teacher);
    return teacherMapper.toDto(saved);
  }

  @Override
  public TeacherDto updateTeacher(UUID id, TeacherDto teacherDto) {
    Teacher teacher = teacherRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Преподаватель не найден с ID: " + id));

    Faculty faculty = facultyRepository.findById(teacherDto.facultyId())
        .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));

    teacher.getPerson().setFirstname(teacherDto.firstname());
    teacher.getPerson().setLastname(teacherDto.lastname());
    teacher.getPerson().setFullname(teacherDto.fullname());
    teacher.getPerson().setEmail(teacherDto.email());
    teacher.setFaculty(faculty);

    Teacher updated = teacherRepository.save(teacher);
    return teacherMapper.toDto(updated);
  }

  @Override
  public void deleteTeacherById(UUID id) {
    if (!teacherRepository.existsById(id)) {
      throw new ResourceNotFoundException("Преподаватель не найден с ID: " + id);
    }
    teacherRepository.deleteById(id);
    keycloakAdminService.deleteUser(id.toString());
  }
}