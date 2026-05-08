package com.coungard.univer.service;

import com.coungard.univer.dto.registration.RegisterTeacherRequest;
import com.coungard.univer.dto.TeacherDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeacherService {

  TeacherDto getTeacherById(UUID id);

  Page<TeacherDto> getTeachers(Pageable pageable);

  TeacherDto registerTeacher(RegisterTeacherRequest registerDto);

  TeacherDto createTeacher(TeacherDto teacherDto);

  TeacherDto updateTeacher(UUID id, TeacherDto teacherDto);

  void deleteTeacherById(UUID id);
}