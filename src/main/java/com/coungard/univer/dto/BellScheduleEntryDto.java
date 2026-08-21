package com.coungard.univer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.UUID;
import lombok.Builder;

@Builder(toBuilder = true)
public record BellScheduleEntryDto(
    UUID id,

    // null = дефолт для всех университетов без собственной записи на этот pairNumber
    UUID universityId,

    @NotNull(message = "Номер пары обязателен")
    @Min(value = 1, message = "Номер пары не может быть меньше 1")
    Integer pairNumber,

    @NotNull(message = "Время начала пары обязательно")
    LocalTime startTime,

    @NotNull(message = "Время окончания пары обязательно")
    LocalTime endTime
) {
}
