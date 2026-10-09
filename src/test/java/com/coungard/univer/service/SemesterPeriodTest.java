package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.coungard.univer.dto.SemesterType;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SemesterPeriodTest {

  @Test
  void shouldReturnAutumnSemesterFromSeptemberToJanuary() {
    SemesterPeriod expected = new SemesterPeriod(
        SemesterType.AUTUMN, LocalDate.of(2026, 9, 1), LocalDate.of(2027, 1, 31));

    assertThat(SemesterPeriod.at(LocalDate.of(2026, 9, 1))).isEqualTo(expected);
    assertThat(SemesterPeriod.at(LocalDate.of(2026, 12, 31))).isEqualTo(expected);
    // Январь — ещё осенний семестр, начавшийся в прошлом календарном году
    assertThat(SemesterPeriod.at(LocalDate.of(2027, 1, 31))).isEqualTo(expected);
  }

  @Test
  void shouldReturnSpringSemesterFromFebruaryToJune() {
    SemesterPeriod expected = new SemesterPeriod(
        SemesterType.SPRING, LocalDate.of(2027, 2, 1), LocalDate.of(2027, 6, 30));

    assertThat(SemesterPeriod.at(LocalDate.of(2027, 2, 1))).isEqualTo(expected);
    assertThat(SemesterPeriod.at(LocalDate.of(2027, 6, 30))).isEqualTo(expected);
  }

  @Test
  void shouldReturnUpcomingAutumnSemesterDuringSummerBreak() {
    SemesterPeriod expected = new SemesterPeriod(
        SemesterType.AUTUMN, LocalDate.of(2027, 9, 1), LocalDate.of(2028, 1, 31));

    assertThat(SemesterPeriod.at(LocalDate.of(2027, 7, 1))).isEqualTo(expected);
    assertThat(SemesterPeriod.at(LocalDate.of(2027, 8, 31))).isEqualTo(expected);
  }
}
