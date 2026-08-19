package com.coungard.univer.service;

import com.coungard.univer.dto.EnrollmentDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnrollmentService {

  /**
   * Зачислить студента на курс. Студент не может быть зачислен на один и тот же курс дважды.
   *
   * @param enrollmentDto ID студента, ID курса, опционально статус (по умолчанию {@code ACTIVE})
   * @return созданный EnrollmentDto
   */
  EnrollmentDto enroll(EnrollmentDto enrollmentDto);

  /**
   * Зачислить на курс сразу всю группу — по одной записи Enrollment на каждого студента группы,
   * ещё не зачисленного на этот курс. Уже зачисленные студенты молча пропускаются, а не приводят
   * к ошибке всей операции.
   *
   * @param groupId  ID группы
   * @param courseId ID курса
   * @return список вновь созданных EnrollmentDto (без уже существовавших ранее)
   */
  List<EnrollmentDto> enrollGroup(UUID groupId, UUID courseId);

  /**
   * Получить запись о зачислении по студенту и курсу.
   *
   * @param studentId ID студента
   * @param courseId  ID курса
   * @return EnrollmentDto
   */
  EnrollmentDto getEnrollment(UUID studentId, UUID courseId);

  /**
   * Получить страницу зачислений по ID студента.
   *
   * @param studentId ID студента
   * @param pageable  параметры пагинации и сортировки
   * @return страница EnrollmentDto
   */
  Page<EnrollmentDto> getEnrollmentsByStudent(UUID studentId, Pageable pageable);

  /**
   * Получить страницу зачислений по ID курса.
   *
   * @param courseId ID курса
   * @param pageable параметры пагинации и сортировки
   * @return страница EnrollmentDto
   */
  Page<EnrollmentDto> getEnrollmentsByCourse(UUID courseId, Pageable pageable);

  /**
   * Изменить статус зачисления (например, отметить завершение курса или отчисление).
   *
   * @param studentId     ID студента
   * @param courseId      ID курса
   * @param enrollmentDto новые данные, используется только {@code status}
   * @return обновлённый EnrollmentDto
   */
  EnrollmentDto updateStatus(UUID studentId, UUID courseId, EnrollmentDto enrollmentDto);

  /**
   * Отчислить студента с курса — удалить запись зачисления.
   *
   * @param studentId ID студента
   * @param courseId  ID курса
   */
  void unenroll(UUID studentId, UUID courseId);
}
