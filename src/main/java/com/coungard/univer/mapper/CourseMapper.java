package com.coungard.univer.mapper;

import com.coungard.univer.dto.CourseDto;
import com.coungard.univer.entity.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

  public CourseDto toDto(Course course) {
    if (course == null) {
      return null;
    }
    return CourseDto.builder()
        .id(course.getId())
        .title(course.getTitle())
        .description(course.getDescription())
        .departmentId(course.getDepartment().getId())
        .teacherId(course.getTeacher() != null ? course.getTeacher().getId() : null)
        .build();
  }

  public Course toEntity(CourseDto dto) {
    if (dto == null) {
      return null;
    }
    Course course = new Course();
    course.setTitle(dto.title());
    course.setDescription(dto.description());
    return course;
  }
}
