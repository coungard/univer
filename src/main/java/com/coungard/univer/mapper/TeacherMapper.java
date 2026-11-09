package com.coungard.univer.mapper;

import com.coungard.univer.dto.TeacherDto;
import com.coungard.univer.dto.registration.RegisterTeacherRequest;
import com.coungard.univer.entity.AuditableEntity;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Teacher;
import com.coungard.univer.service.AvatarUrls;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class TeacherMapper {

  private final AvatarUrls avatarUrls;

  public TeacherDto toDto(Teacher teacher) {
    if (teacher == null) {
      return null;
    }
    // Правка имени, email или телефона меняет только Person
    AuditableEntity lastModified = AuditableEntity.lastModified(teacher, teacher.getPerson());
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
        .gender(teacher.getPerson().getGender())
        .createdAt(teacher.getCreatedAt())
        .updatedAt(lastModified.getUpdatedAt())
        .createdBy(teacher.getCreatedBy())
        .updatedBy(lastModified.getUpdatedBy())
        .avatarUrl(avatarUrls.full(teacher.getPerson().getAvatarFile()))
        .avatarThumbnailUrl(avatarUrls.thumbnail(teacher.getPerson().getAvatarFile()))
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
    person.setGender(request.getGender());

    Teacher teacher = new Teacher();
    teacher.setPosition(request.getPosition());
    teacher.setPerson(person);

    return teacher;
  }
}