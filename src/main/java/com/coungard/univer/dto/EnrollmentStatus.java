package com.coungard.univer.dto;

/**
 * Статус зачисления студента на курс ({@code Enrollment}).
 */
public enum EnrollmentStatus {

  /** Студент зачислен и активно изучает курс. */
  ACTIVE,

  /** Студент успешно завершил курс. */
  COMPLETED,

  /** Студент отчислен с курса до его завершения. */
  DROPPED
}
