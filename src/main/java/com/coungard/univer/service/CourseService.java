package com.coungard.univer.service;

import com.coungard.univer.dto.CourseDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {

  /**
   * Создать новый курс. Обязательна привязка к кафедре, преподаватель опционален.
   *
   * @param courseDto данные курса
   * @return созданный CourseDto
   */
  CourseDto createCourse(CourseDto courseDto);

  /**
   * Получить курс по ID.
   *
   * @param id идентификатор курса
   * @return CourseDto
   */
  CourseDto getCourseById(UUID id);

  /**
   * Получить страницу курсов с пагинацией.
   *
   * @param pageable параметры пагинации и сортировки
   * @return страница CourseDto
   */
  Page<CourseDto> getCourses(Pageable pageable);

  /**
   * Получить страницу курсов по ID кафедры.
   *
   * @param departmentId идентификатор кафедры
   * @param pageable параметры пагинации и сортировки
   * @return страница CourseDto
   */
  Page<CourseDto> getCoursesByDepartment(UUID departmentId, Pageable pageable);

  /**
   * Обновить курс.
   *
   * @param id идентификатор курса
   * @param courseDto новые данные
   * @return обновлённый CourseDto
   */
  CourseDto updateCourse(UUID id, CourseDto courseDto);

  /**
   * Удалить курс по ID.
   *
   * @param id идентификатор курса
   */
  void deleteCourse(UUID id);
}
