package com.coungard.univer.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class WeekParityTest {

  // 2026-09-07 — понедельник
  private static final LocalDate MONDAY = LocalDate.of(2026, 9, 7);

  @Test
  void shouldCountWeeksFromStartWhenSemesterStartsOnMonday() {
    assertThat(WeekParity.of(MONDAY, MONDAY)).isEqualTo(WeekParity.ODD);
    assertThat(WeekParity.of(MONDAY, MONDAY.plusDays(6))).isEqualTo(WeekParity.ODD);
    assertThat(WeekParity.of(MONDAY, MONDAY.plusDays(7))).isEqualTo(WeekParity.EVEN);
    assertThat(WeekParity.of(MONDAY, MONDAY.plusDays(14))).isEqualTo(WeekParity.ODD);
  }

  @Test
  void shouldTreatRestOfStartWeekAsFirstWeekWhenSemesterStartsOnWednesday() {
    LocalDate wednesday = MONDAY.plusDays(2);

    assertThat(WeekParity.of(wednesday, wednesday)).isEqualTo(WeekParity.ODD);
    // Воскресенье той же календарной недели — ещё первая неделя, понедельник — уже вторая
    assertThat(WeekParity.of(wednesday, MONDAY.plusDays(6))).isEqualTo(WeekParity.ODD);
    assertThat(WeekParity.of(wednesday, MONDAY.plusDays(7))).isEqualTo(WeekParity.EVEN);
    assertThat(WeekParity.of(wednesday, MONDAY.plusDays(8))).isEqualTo(WeekParity.EVEN);
    assertThat(WeekParity.of(wednesday, MONDAY.plusDays(14))).isEqualTo(WeekParity.ODD);
  }

  @Test
  void shouldMakeFirstWeekOneDayLongWhenSemesterStartsOnSunday() {
    LocalDate sunday = MONDAY.plusDays(6);

    assertThat(WeekParity.of(sunday, sunday)).isEqualTo(WeekParity.ODD);
    assertThat(WeekParity.of(sunday, sunday.plusDays(1))).isEqualTo(WeekParity.EVEN);
  }
}
