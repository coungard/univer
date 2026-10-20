package com.coungard.univer.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder(toBuilder = true)
public record LectureAttendanceDto(

    @NotNull(message = "ID студента обязателен")
    UUID studentId,

    @NotNull(message = "ID лекции обязателен")
    UUID lectureId,

    boolean attended,

    // Только в ответе (AuditableEntity, issue #119): во входящих запросах игнорируются.
    Instant createdAt,
    Instant updatedAt,
    UUID createdBy,
    UUID updatedBy
) {
}
