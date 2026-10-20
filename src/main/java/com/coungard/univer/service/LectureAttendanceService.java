package com.coungard.univer.service;

import com.coungard.univer.dto.AttendanceStatsDto;
import com.coungard.univer.dto.AttendanceSummaryDto;
import com.coungard.univer.dto.LectureAttendanceDto;
import com.coungard.univer.dto.MyAttendanceDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LectureAttendanceService {

  /**
   * Отметить посещение лекции студентом (создать запись или обновить {@code attended}, если она
   * уже есть). Студент должен быть активно ({@code ACTIVE}) зачислен на курс этой лекции —
   * иначе бросается {@link com.coungard.univer.exception.ValidationException}.
   *
   * @param attendanceDto ID студента, ID лекции, признак присутствия
   * @return сохранённый LectureAttendanceDto
   */
  LectureAttendanceDto markAttendance(LectureAttendanceDto attendanceDto);

  /**
   * Получить страницу отметок посещаемости по ID студента.
   *
   * @param studentId ID студента
   * @param pageable  параметры пагинации и сортировки
   * @return страница LectureAttendanceDto
   */
  Page<LectureAttendanceDto> getAttendanceByStudent(UUID studentId, Pageable pageable);

  /**
   * Получить страницу отметок посещаемости по ID лекции.
   *
   * @param lectureId ID лекции
   * @param pageable  параметры пагинации и сортировки
   * @return страница LectureAttendanceDto
   */
  Page<LectureAttendanceDto> getAttendanceByLecture(UUID lectureId, Pageable pageable);

  /**
   * Статистика посещаемости конкретной лекции: сколько студентов отмечено всего и сколько из них
   * присутствовало.
   *
   * @param lectureId ID лекции
   * @return AttendanceStatsDto
   */
  AttendanceStatsDto getLectureStats(UUID lectureId);

  /**
   * Статистика посещаемости конкретного студента в рамках курса: сколько отмеченных лекций курса
   * и сколько из них он посетил.
   *
   * @param studentId ID студента
   * @param courseId  ID курса
   * @return AttendanceStatsDto
   */
  AttendanceStatsDto getStudentCourseStats(UUID studentId, UUID courseId);

  /**
   * Личная отметка студента о посещении лекции (issue #117): создать или изменить. Лекция должна быть
   * в расписании группы студента и уже начаться — иначе
   * {@link com.coungard.univer.exception.ValidationException}. Зачисление на курс не требуется.
   *
   * @param studentId ID студента (subject JWT)
   * @param lectureId ID лекции
   * @param attended  был ли студент на лекции
   * @return итоговая отметка лекции: отметка преподавателя, если она есть, иначе только что поставленная
   */
  MyAttendanceDto markMyAttendance(UUID studentId, UUID lectureId, boolean attended);

  /**
   * Снять личную отметку студента. Отметку преподавателя не трогает; если личной отметки нет — ничего
   * не делает.
   *
   * @param studentId ID студента (subject JWT)
   * @param lectureId ID лекции
   */
  void unmarkMyAttendance(UUID studentId, UUID lectureId);

  /**
   * Итоговые отметки студента: по одной на каждую лекцию, где есть отметка преподавателя или личная.
   * Отметка преподавателя главнее.
   *
   * @param studentId ID студента (subject JWT)
   * @param pageable  параметры пагинации и сортировки (по полям Lecture)
   * @return страница MyAttendanceDto
   */
  Page<MyAttendanceDto> getMyAttendance(UUID studentId, Pageable pageable);

  /**
   * Сводка посещаемости студентом курса: начавшиеся лекции курса у группы студента в семестре этой
   * группы и сколько из них посещено, пропущено и не отмечено (по итоговым отметкам).
   *
   * @param studentId ID студента (subject JWT)
   * @param courseId  ID курса
   * @return AttendanceSummaryDto; у студента без группы — все нули
   */
  AttendanceSummaryDto getMyCourseSummary(UUID studentId, UUID courseId);
}
