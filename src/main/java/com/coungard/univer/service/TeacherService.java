package com.coungard.univer.service;

import com.coungard.univer.dto.request.UpdateTeacherProfileRequest;
import com.coungard.univer.dto.registration.RegisterTeacherRequest;
import com.coungard.univer.dto.TeacherDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeacherService {

  TeacherDto getTeacherById(UUID id);

  /**
   * Преподаватели постранично, с необязательным отбором: по факультету или, если он не задан, по
   * университету. Без обоих параметров — все преподаватели.
   */
  Page<TeacherDto> getTeachers(UUID facultyId, UUID universityId, Pageable pageable);

  TeacherDto registerTeacher(RegisterTeacherRequest registerDto);

  TeacherDto createTeacher(TeacherDto teacherDto);

  TeacherDto updateTeacher(UUID id, TeacherDto teacherDto);

  /**
   * Частичное обновление своего профиля преподавателем (issue #139): непереданное поле не меняется. Пол
   * обязателен — явный {@code null} даёт {@link com.coungard.univer.exception.ValidationException}.
   *
   * @param id subject JWT = id преподавателя
   */
  TeacherDto updateMyProfile(UUID id, UpdateTeacherProfileRequest request);

  void deleteTeacherById(UUID id);
}