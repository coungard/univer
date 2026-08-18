package com.coungard.univer.service;

import com.coungard.univer.dto.UniversityDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UniversityService {

  /**
   * Получить страницу университетов с пагинацией и сортировкой.
   *
   * @param pageable параметры пагинации и сортировки
   * @return страница UniversityDto
   */
  Page<UniversityDto> getUniversities(Pageable pageable);

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
