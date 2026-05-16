package com.coungard.univer.mapper;

import com.coungard.univer.dto.StudentDto;
import com.coungard.univer.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

  public StudentDto toDto(Student student) {
    if (student == null) {
      return null;
    }
    return StudentDto.builder()
        .id(student.getId())
        .username(student.getPerson().getUsername())
        .email(student.getPerson().getEmail())
        .firstname(student.getPerson().getFirstname())
        .lastname(student.getPerson().getLastname())
        .fullname(student.getPerson().getFullname())
        .universityId(student.getUniversity().getId())
        .createdAt(student.getCreatedAt())
        .updatedAt(student.getUpdatedAt())
        .build();
  }
}