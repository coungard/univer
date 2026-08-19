package com.coungard.univer.service;

import com.coungard.univer.dto.AttendanceStatsDto;
import com.coungard.univer.dto.LectureAttendanceDto;
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
}
