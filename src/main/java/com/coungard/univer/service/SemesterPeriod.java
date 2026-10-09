package com.coungard.univer.service;

import com.coungard.univer.dto.SemesterType;
import java.time.LocalDate;
import java.time.Month;

/**
 * Календарное полугодие — тип и даты семестра, который создаётся автоматически, когда у учебного года
 * ещё нет ни одного (см. {@link GroupService#createGroupInCurrentSemester}). Точных дат вуза сервер не
 * знает, поэтому берёт границы по календарю: осенний — 1 сентября – 31 января, весенний — 1 февраля –
 * 30 июня.
 */
public record SemesterPeriod(SemesterType type, LocalDate startDate, LocalDate endDate) {

  /**
   * Полугодие, идущее на дату {@code today}; на летних каникулах (июль–август) — ближайшее осеннее.
   */
  public static SemesterPeriod at(LocalDate today) {
    int year = today.getYear();
    Month month = today.getMonth();
    if (month == Month.JANUARY) {
      return autumn(year - 1);
    }
    if (month.getValue() <= Month.JUNE.getValue()) {
      return new SemesterPeriod(SemesterType.SPRING, LocalDate.of(year, 2, 1), LocalDate.of(year, 6, 30));
    }
    return autumn(year);
  }

  private static SemesterPeriod autumn(int startYear) {
    return new SemesterPeriod(SemesterType.AUTUMN, LocalDate.of(startYear, 9, 1),
        LocalDate.of(startYear + 1, 1, 31));
  }
}
