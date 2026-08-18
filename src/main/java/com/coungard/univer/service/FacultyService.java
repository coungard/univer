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
