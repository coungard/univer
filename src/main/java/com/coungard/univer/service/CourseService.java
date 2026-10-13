package com.coungard.univer.service;

import com.coungard.univer.dto.CourseDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {

  /**
   * Создать новый курс. Кафедра, факультет и преподаватель опциональны. Если указан факультет,
   * название должно быть свободно среди предметов этого факультета, а указанная кафедра —
   * относиться к нему.
   *
   * @param courseDto данные курса
   * @return созданный CourseDto
   * @throws com.coungard.univer.exception.ConflictException предмет с таким названием на этом
   *     факультете уже есть — с ID существующего предмета
   */
  CourseDto createCourse(CourseDto courseDto);

  /**
   * Создать предмет студентом: привязывается к факультету напрямую, без кафедры и преподавателя.
   *
   * @param facultyId идентификатор факультета
   * @param title название предмета
   * @param createdByStudentId студент, создающий предмет
   * @return созданный CourseDto
   * @throws com.coungard.univer.exception.ConflictException предмет с таким названием на этом
   *     факультете уже есть — с ID существующего предмета
   */
  CourseDto createCourseByStudent(UUID facultyId, String title, UUID createdByStudentId);

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
   * Получить страницу предметов факультета, отсортированных по названию: привязанные к факультету
   * напрямую (в том числе созданные студентами) и привязанные через его кафедры.
   *
   * @param facultyId идентификатор факультета
   * @param pageable параметры пагинации
   * @return страница CourseDto; пустая, если факультета нет или предметов у него нет
   */
  Page<CourseDto> getCoursesByFaculty(UUID facultyId, Pageable pageable);

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
