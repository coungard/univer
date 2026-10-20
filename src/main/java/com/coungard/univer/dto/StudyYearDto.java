package com.coungard.univer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record StudyYearDto(
    UUID id,

    @NotNull(message = "ID факультета обязателен")
    UUID facultyId,

    @NotNull(message = "Номер курса обязателен")
    @Min(value = 1, message = "Номер курса не может быть меньше 1")
    Integer yearNumber,

    // Только в ответе (AuditableEntity, issue #119): во входящих запросах игнорируются.
    Instant createdAt,
    Instant updatedAt,
    UUID createdBy,
    UUID updatedBy
) {
}
