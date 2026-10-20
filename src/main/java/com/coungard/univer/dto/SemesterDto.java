package com.coungard.univer.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;

@Builder
public record SemesterDto(
    UUID id,

    @NotNull(message = "ID курса обучения обязателен")
    UUID studyYearId,

    @NotNull(message = "Тип семестра обязателен")
    SemesterType type,

    @NotNull(message = "Дата начала семестра обязательна")
    LocalDate startDate,

    @NotNull(message = "Дата окончания семестра обязательна")
    LocalDate endDate,

    // Только в ответе (AuditableEntity, issue #119): во входящих запросах игнорируются.
    Instant createdAt,
    Instant updatedAt,
    UUID createdBy,
    UUID updatedBy
) {
}
