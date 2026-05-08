package com.coungard.univer.validation;

import com.coungard.univer.dto.registration.RegisterTeacherRequest;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.repository.DepartmentRepository;
import com.coungard.univer.repository.TeacherRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherValidator {

  private final DepartmentRepository departmentRepository;
  private final TeacherRepository teacherRepository;

  /**
   * Валидация данных при регистрации преподавателя
   */
  public void validateRegisterTeacher(RegisterTeacherRequest registerDto) {

    UUID departmentId = registerDto.getDepartmentId();
    if (!departmentRepository.existsById(departmentId)) {
      throw new ResourceNotFoundException("Кафедра с ID " + departmentId + " не найдена");
    }

    if (teacherRepository.existsByPersonEmail(registerDto.getEmail())) {
      throw new ValidationException("Преподаватель с таким email уже существует: " + registerDto.getEmail());
    }

    if (teacherRepository.existsByPersonUsername(registerDto.getUsername())) {
      throw new ValidationException("Преподаватель с таким логином уже существует: " + registerDto.getUsername());
    }
  }
}