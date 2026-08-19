package com.coungard.univer.dto;

import lombok.Builder;

/**
 * Статистика посещаемости — либо по конкретной лекции (сколько студентов отметились), либо по
 * студенту в рамках курса (сколько лекций курса он посетил из отмеченных).
 */
@Builder
public record AttendanceStatsDto(
    long totalMarked,
    long attendedCount,
    double attendanceRate
) {
}
