package com.coungard.univer.dto.mapper;

import com.coungard.univer.dto.TeacherDto;
import com.coungard.univer.dto.registration.RegisterTeacherRequest;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Teacher;
import org.springframework.stereotype.Component;

@Component
public class TeacherMapper {

  public TeacherDto toDto(Teacher teacher) {
    if (teacher == null) {
      return null;
    }
    return TeacherDto.builder()
        .id(teacher.getId())
        .username(teacher.getPerson().getUsername())
        .email(teacher.getPerson().getEmail())
        .firstname(teacher.getPerson().getFirstname())
        .lastname(teacher.getPerson().getLastname())
        .fullname(teacher.getPerson().getFullname())
        .departmentId(teacher.getDepartment().getId())
        .position(teacher.getPosition())
        .createdAt(teacher.getCreatedAt())
        .updatedAt(teacher.getUpdatedAt())
        .build();
  }

  public Teacher fromRegisterToEntity(RegisterTeacherRequest request) {

    Person person = new Person();
    person.setUsername(request.getUsername().toLowerCase());
    person.setFirstname(request.getFirstname());
    person.setLastname(request.getLastname());
    person.setFullname(request.getFullname());
    person.setEmail(request.getEmail());

    Teacher teacher = new Teacher();
    teacher.setPosition(request.getPosition());
    teacher.setPerson(person);

    return teacher;
  }
}