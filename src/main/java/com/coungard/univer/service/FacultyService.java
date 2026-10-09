package com.coungard.univer.service;

import com.coungard.univer.dto.FacultyDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FacultyService {

  /**
   * Создать новый факультет
   */
  FacultyDto createFaculty(FacultyDto facultyDto);

  /**
   * Создать факультет самим студентом — только с названием, без описания и кафедр.
   *
   * @param universityId идентификатор университета
   * @param name название факультета
   * @param createdByStudentId студент, создающий факультет
   * @return созданный FacultyDto
   * @throws com.coungard.univer.exception.ConflictException факультет с таким названием в этом
   *     университете уже есть — с ID существующего факультета
   */
  FacultyDto createFacultyByStudent(UUID universityId, String name, UUID createdByStudentId);

  /**
   * Изменить название факультета, не трогая остальные поля.
   *
   * @param id идентификатор факультета
   * @param name новое название
   * @return обновлённый FacultyDto
   * @throws com.coungard.univer.exception.ConflictException другой факультет с таким названием в этом
   *     университете уже есть — с ID существующего факультета
   */
  FacultyDto renameFaculty(UUID id, String name);

  /**
   * Получить страницу факультетов по ID университета
   */
  Page<FacultyDto> getFacultiesByUniversity(UUID universityId, Pageable pageable);

  /**
   * Получить факультет по ID
   */
  FacultyDto getFacultyById(UUID id);

  /**
   * Обновить факультет
   */
  FacultyDto updateFaculty(UUID id, FacultyDto facultyDto);

  /**
   * Удалить факультет по ID
   */
  void deleteFaculty(UUID id);
}
