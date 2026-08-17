package com.coungard.univer.service;

import com.coungard.univer.dto.UniversityDto;
import java.util.List;
import java.util.UUID;

public interface UniversityService {

  /**
   * Получить все университеты
   */
  List<UniversityDto> getAllUniversities();

  /**
   * Получить университет по ID
   */
  UniversityDto getUniversityById(UUID id);

  /**
   * Создать новый университет
   */
  UniversityDto createUniversity(UniversityDto universityDto);

  /**
   * Обновить университет
   */
  UniversityDto updateUniversity(UUID id, UniversityDto universityDto);

  /**
   * Удалить университет по ID
   */
  void deleteUniversityById(UUID id);
}
