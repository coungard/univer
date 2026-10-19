package com.coungard.univer.dto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * Чётность недели циклического расписания ({@code WeekScheduleCycle}). Недели семестра — календарные
 * (пн–вс): неделя, в которую попадает {@code Semester.startDate}, — первая, нечётная, даже если семестр
 * начался не в понедельник; дальше недели меняются по понедельникам — см. TARGET.md, раздел
 * «Циклическое расписание: чётность недели».
 */
public enum WeekParity {

  /** Нечётная неделя семестра (1, 3, 5, 7...). */
  ODD,

  /** Чётная неделя семестра (2, 4, 6, 8...). */
  EVEN,

  /** Каждую неделю, независимо от чётности. */
  BOTH;

  /**
   * Чётность недели семестра, на которую приходится {@code date} (issue #106). Границы семестра не
   * проверяются: это задача вызывающего.
   */
  public static WeekParity of(LocalDate semesterStart, LocalDate date) {
    return Math.floorMod(weekNumber(semesterStart, date), 2) == 1 ? ODD : EVEN;
  }

  /**
   * Номер недели семестра, на которую приходится {@code date}: 1 — календарная неделя {@code
   * semesterStart}. Для дат раньше этой недели — 0 и меньше; границы семестра не проверяются.
   */
  public static int weekNumber(LocalDate semesterStart, LocalDate date) {
    return (int) Math.floorDiv(ChronoUnit.DAYS.between(weekStart(semesterStart), date), 7) + 1;
  }

  /** Понедельник календарной недели, на которую приходится {@code date}. */
  public static LocalDate weekStart(LocalDate date) {
    return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
  }
}
