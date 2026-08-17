package com.coungard.univer.service;

import com.coungard.univer.dto.FacultyDto;
import java.util.List;
import java.util.UUID;

public interface FacultyService {

  /**
   * Создать новый факультет
   */
  FacultyDto createFaculty(FacultyDto facultyDto);

  /**
   * Получить факультеты по ID университета
   */
  List<FacultyDto> getFacultiesByUniversity(UUID universityId);

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
