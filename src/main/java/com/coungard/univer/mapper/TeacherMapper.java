package com.coungard.univer.mapper;

import com.coungard.univer.dto.TeacherDto;
import com.coungard.univer.dto.registration.RegisterTeacherRequest;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Teacher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
        .phone(teacher.getPerson().getPhone())
        .birthday(teacher.getPerson().getBirthday())
        .facultyId(teacher.getFaculty() != null ? teacher.getFaculty().getId() : null)
        .position(teacher.getPosition())
        .registered(teacher.isRegistered())
        .createdAt(teacher.getCreatedAt())
        .updatedAt(teacher.getUpdatedAt())
        .build();
  }

  public Teacher fromRegisterToEntity(RegisterTeacherRequest request) {

    Person person = new Person();
    person.setUsername(request.getUsername().toLowerCase());
    person.setFirstname(request.getFirstname());
    person.setLastname(request.getLastname());
    // fullname необязателен в запросе, но обязателен в БД (persons.fullname NOT NULL)
    person.setFullname(StringUtils.hasText(request.getFullname())
        ? request.getFullname()
        : request.getLastname() + " " + request.getFirstname());
    person.setEmail(request.getEmail());
    person.setBirthday(request.getBirthday());

    Teacher teacher = new Teacher();
    teacher.setPosition(request.getPosition());
    teacher.setPerson(person);

    return teacher;
  }
}