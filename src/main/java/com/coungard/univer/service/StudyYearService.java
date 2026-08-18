package com.coungard.univer.service;

import com.coungard.univer.dto.StudyYearDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudyYearService {

  /**
   * Создать новый курс обучения (год обучения в рамках программы). Номер курса не может превышать
   * длительность программы ({@code Program.durationOfStudy.years}).
   *
   * @param studyYearDto данные курса обучения
   * @return созданный StudyYearDto
   */
  StudyYearDto createStudyYear(StudyYearDto studyYearDto);

  /**
   * Получить курс обучения по ID.
   *
   * @param id идентификатор курса обучения
   * @return StudyYearDto
   */
  StudyYearDto getStudyYearById(UUID id);

  /**
   * Получить страницу курсов обучения с пагинацией.
   *
   * @param pageable параметры пагинации и сортировки
   * @return страница StudyYearDto
   */
  Page<StudyYearDto> getStudyYears(Pageable pageable);

  /**
   * Получить страницу курсов обучения по ID программы.
   *
   * @param programId идентификатор программы
   * @param pageable параметры пагинации и сортировки
   * @return страница StudyYearDto
   */
  Page<StudyYearDto> getStudyYearsByProgram(UUID programId, Pageable pageable);

  /**
   * Обновить курс обучения.
   *
   * @param id идентификатор курса обучения
   * @param studyYearDto новые данные
   * @return обновлённый StudyYearDto
   */
  StudyYearDto updateStudyYear(UUID id, StudyYearDto studyYearDto);

  /**
   * Удалить курс обучения по ID.
   *
   * @param id идентификатор курса обучения
   */
  void deleteStudyYear(UUID id);
}
