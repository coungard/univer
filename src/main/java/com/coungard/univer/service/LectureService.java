package com.coungard.univer.service;

import com.coungard.univer.dto.LectureDto;
import com.coungard.univer.dto.request.GenerateLectureRequest;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LectureService {

  /**
   * Создать лекцию вручную. Обязательна привязка к учебному курсу и хотя бы одной группе (может
   * быть несколько — поточная лекция). Преподаватель опционален.
   *
   * @param lectureDto данные лекции
   * @return созданный LectureDto
   */
  LectureDto createLecture(LectureDto lectureDto);

  /**
   * Сгенерировать лекцию на конкретную дату из шаблона {@code Pair} циклического расписания.
   * Курс, преподаватель и связанные группы копируются из шаблона; дата должна соответствовать дню
   * недели и чётности недели пары (считая от {@code Semester.startDate}).
   *
   * @param request ID пары и дата
   * @return созданный LectureDto
   */
  LectureDto generateFromPair(GenerateLectureRequest request);

  /**
   * Получить лекцию по ID.
   *
   * @param id идентификатор лекции
   * @return LectureDto
   */
  LectureDto getLectureById(UUID id);

  /**
   * Получить страницу лекций с пагинацией.
   *
   * @param pageable параметры пагинации и сортировки
   * @return страница LectureDto
   */
  Page<LectureDto> getLectures(Pageable pageable);

  /**
   * Получить страницу лекций по ID учебного курса.
   *
   * @param courseId идентификатор учебного курса
   * @param pageable параметры пагинации и сортировки
   * @return страница LectureDto
   */
  Page<LectureDto> getLecturesByCourse(UUID courseId, Pageable pageable);

  /**
   * Получить страницу лекций по ID группы.
   *
   * @param groupId идентификатор группы
   * @param pageable параметры пагинации и сортировки
   * @return страница LectureDto
   */
  Page<LectureDto> getLecturesByGroup(UUID groupId, Pageable pageable);

  /**
   * Обновить лекцию.
   *
   * @param id идентификатор лекции
   * @param lectureDto новые данные
   * @return обновлённый LectureDto
   */
  LectureDto updateLecture(UUID id, LectureDto lectureDto);

  /**
   * Удалить лекцию по ID.
   *
   * @param id идентификатор лекции
   */
  void deleteLecture(UUID id);
}
