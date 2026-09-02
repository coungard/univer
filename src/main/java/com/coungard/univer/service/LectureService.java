package com.coungard.univer.service;

import com.coungard.univer.dto.LectureDto;
import com.coungard.univer.dto.request.GenerateLectureRequest;
import java.util.List;
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
   * @param request         ID пары и дата
   * @param callerStudentId {@code null}, если вызывает ADMIN/TEACHER (без ограничений); иначе ID
   *                        вызывающего STUDENT — целевой Pair должен принадлежать его группе
   * @return созданный LectureDto
   */
  LectureDto generateFromPair(GenerateLectureRequest request, UUID callerStudentId);

  /**
   * Сгенерировать лекции на весь семестр из Pair-шаблонов данного WeekScheduleCycle. Для каждой
   * Pair перебираются все даты её дня недели в границах [Semester.startDate, Semester.endDate],
   * подходящие по чётности недели; уже сгенерированные ранее пара+дата пропускаются без ошибки —
   * операция идемпотентна, безопасно вызывать повторно (например, после добавления новых Pair).
   *
   * @param weekScheduleCycleId идентификатор циклического расписания (= семестра)
   * @param callerStudentId     {@code null}, если вызывает ADMIN/TEACHER — генерируются лекции по
   *                            всем Pair цикла, как раньше; иначе ID вызывающего STUDENT —
   *                            генерируются лекции только по Pair его группы, остальные Pair цикла
   *                            пропускаются молча (не ошибка — это ожидаемое сужение выборки)
   * @return список только вновь созданных LectureDto (уже существовавшие в выборку не попадают)
   */
  List<LectureDto> generateSemesterLectures(UUID weekScheduleCycleId, UUID callerStudentId);

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
   * Получить страницу расписания текущего студента — лекции группы, к которой он привязан.
   * Идентификатор студента совпадает с Keycloak user ID (см. флоу регистрации). Если студент ещё
   * не привязан к группе, возвращается пустая страница, а не ошибка.
   *
   * @param studentId идентификатор студента (= Keycloak subject из JWT)
   * @param pageable параметры пагинации и сортировки
   * @return страница LectureDto
   */
  Page<LectureDto> getMyLectures(UUID studentId, Pageable pageable);

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
