package com.coungard.univer.service;

import com.coungard.univer.dto.SemesterDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SemesterService {

  /**
   * Создать новый семестр. Обязательна привязка к курсу обучения ({@code StudyYear}).
   *
   * @param semesterDto данные семестра
   * @return созданный SemesterDto
   */
  SemesterDto createSemester(SemesterDto semesterDto);

  /**
   * Получить семестр по ID.
   *
   * @param id идентификатор семестра
   * @return SemesterDto
   */
  SemesterDto getSemesterById(UUID id);

  /**
   * Получить страницу семестров с пагинацией.
   *
   * @param pageable параметры пагинации и сортировки
   * @return страница SemesterDto
   */
  Page<SemesterDto> getSemesters(Pageable pageable);

  /**
   * Получить страницу семестров по ID курса обучения.
   *
   * @param studyYearId идентификатор курса обучения
   * @param pageable параметры пагинации и сортировки
   * @return страница SemesterDto
   */
  Page<SemesterDto> getSemestersByStudyYear(UUID studyYearId, Pageable pageable);

  /**
   * Обновить семестр.
   *
   * @param id идентификатор семестра
   * @param semesterDto новые данные
   * @return обновлённый SemesterDto
   */
  SemesterDto updateSemester(UUID id, SemesterDto semesterDto);

  /**
   * Удалить семестр по ID.
   *
   * @param id идентификатор семестра
   */
  void deleteSemester(UUID id);
}
