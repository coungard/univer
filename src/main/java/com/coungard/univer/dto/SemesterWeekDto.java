package com.coungard.univer.dto;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;

/**
 * Календарная неделя (пн–вс) семестра, на которую приходится {@code date} (issue #107). Номер и
 * чётность считает сервер по правилу {@link WeekParity#of}, чтобы клиенты его не дублировали.
 *
 * @param inSemester неделя пересекается с семестром ({@code weekStart ≤ endDate} и {@code weekEnd ≥
 *     startDate}); иначе {@code weekNumber} и {@code weekParity} — {@code null}
 */
@Builder
public record SemesterWeekDto(
    UUID semesterId,
    LocalDate date,
    LocalDate weekStart,
    LocalDate weekEnd,
    boolean inSemester,
    Integer weekNumber,
    WeekParity weekParity
) {
}
