package com.coungard.univer.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record WeekScheduleCycleDto(
    UUID id,

    @NotNull(message = "ID семестра обязателен")
    UUID semesterId,

    // Read-only: при создании сервис всегда форсирует DRAFT независимо от присланного значения;
    // менять статус — через PUT /week-schedule-cycles/{id}/status.
    WeekScheduleCycleStatus status,

    // Только в ответе (AuditableEntity, issue #119): во входящих запросах игнорируются.
    Instant createdAt,
    Instant updatedAt,
    UUID createdBy,
    UUID updatedBy
) {
}
