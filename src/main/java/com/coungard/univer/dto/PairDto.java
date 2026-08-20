package com.coungard.univer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;

@Builder(toBuilder = true)
public record PairDto(
    UUID id,

    @NotNull(message = "ID циклического расписания обязателен")
    UUID weekScheduleCycleId,

    @NotNull(message = "День недели обязателен")
    DayOfWeek dayOfWeek,

    @NotNull(message = "Чётность недели обязательна")
    WeekParity weekParity,

    @NotNull(message = "Номер пары обязателен")
    @Min(value = 1, message = "Номер пары не может быть меньше 1")
    Integer pairNumber,

    @NotNull(message = "Время начала пары обязательно")
    LocalTime startTime,

    @NotNull(message = "Время окончания пары обязательно")
    LocalTime endTime,

    @NotNull(message = "ID учебного курса обязателен")
    UUID courseId,

    UUID teacherId,

    String room,

    @NotEmpty(message = "Пара должна быть привязана хотя бы к одной группе")
    Set<UUID> groupIds
) {
}
