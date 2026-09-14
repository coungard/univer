package com.coungard.univer.service;

import com.coungard.univer.dto.UniversityDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UniversityService {

  /**
   * Получить страницу университетов с пагинацией, сортировкой и опциональным поиском по названию.
   *
   * @param search  регистронезависимая подстрока для поиска по названию университета;
   *                {@code null} или пустая строка — фильтрация не применяется
   * @param pageable параметры пагинации и сортировки
   * @return страница UniversityDto
   */
  Page<UniversityDto> getUniversities(String search, Pageable pageable);

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
