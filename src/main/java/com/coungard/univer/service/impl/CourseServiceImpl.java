package com.coungard.univer.service.impl;

import com.coungard.univer.dto.CourseDto;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Department;
import com.coungard.univer.entity.Teacher;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.mapper.CourseMapper;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.DepartmentRepository;
import com.coungard.univer.repository.TeacherRepository;
import com.coungard.univer.service.CourseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

  private final CourseRepository courseRepository;
  private final DepartmentRepository departmentRepository;
  private final TeacherRepository teacherRepository;
  private final CourseMapper courseMapper;

  @Override
  @Transactional
  public CourseDto createCourse(CourseDto courseDto) {
    Department department = departmentRepository.findById(courseDto.departmentId())
        .orElseThrow(() -> new ResourceNotFoundException("Кафедра не найдена с ID: " + courseDto.departmentId()));

    Course course = courseMapper.toEntity(courseDto);
    course.setDepartment(department);
    course.setTeacher(resolveTeacher(courseDto.teacherId()));

    Course saved = courseRepository.save(course);
    return courseMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public CourseDto getCourseById(UUID id) {
    Course course = courseRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Курс не найден с ID: " + id));
    return courseMapper.toDto(course);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CourseDto> getCourses(Pageable pageable) {
    return courseRepository.findAll(pageable).map(courseMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CourseDto> getCoursesByDepartment(UUID departmentId, Pageable pageable) {
    return courseRepository.findByDepartmentId(departmentId, pageable).map(courseMapper::toDto);
  }

  @Override
  @Transactional
  public CourseDto updateCourse(UUID id, CourseDto courseDto) {
    Course existing = courseRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Курс не найден с ID: " + id));

    Department department = departmentRepository.findById(courseDto.departmentId())
        .orElseThrow(() -> new ResourceNotFoundException("Кафедра не найдена с ID: " + courseDto.departmentId()));

    existing.setTitle(courseDto.title());
    existing.setDescription(courseDto.description());
    existing.setDepartment(department);
    existing.setTeacher(resolveTeacher(courseDto.teacherId()));

    Course updated = courseRepository.save(existing);
    return courseMapper.toDto(updated);
  }

  @Override
  @Transactional
  public void deleteCourse(UUID id) {
    if (!courseRepository.existsById(id)) {
      throw new ResourceNotFoundException("Курс не найден с ID: " + id);
    }
    courseRepository.deleteById(id);
  }

  private Teacher resolveTeacher(UUID teacherId) {
    if (teacherId == null) {
      return null;
    }
    return teacherRepository.findById(teacherId)
        .orElseThrow(() -> new ResourceNotFoundException("Преподаватель не найден с ID: " + teacherId));
  }
}
