package com.coungard.univer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;

@Builder(toBuilder = true)
public record LectureDto(
    UUID id,

    @NotBlank(message = "Название лекции обязательно")
    String title,

    String content,

    @NotNull(message = "Время проведения лекции обязательно")
    LocalDateTime scheduledTime,

    Integer durationMinutes,

    @NotNull(message = "ID учебного курса обязателен")
    UUID courseId,

    UUID teacherId,

    // Преподаватель текстом, если его нет среди зарегистрированных (issue #104). В ответе при заданном
    // teacherId — полное имя этого Teacher; на входе при заданном teacherId игнорируется.
    @Size(max = 100, message = "Имя преподавателя не может быть длиннее 100 символов")
    String teacherName,

    String room,

    /** Заполняется автоматически при генерации из шаблона Pair — не для ручного заполнения. */
    UUID sourcePairId,

    @NotEmpty(message = "Лекция должна быть привязана хотя бы к одной группе")
    Set<UUID> groupIds,

    // Только в ответе (AuditableEntity, issue #119): во входящих запросах игнорируются.
    Instant createdAt,
    Instant updatedAt,
    UUID createdBy,
    UUID updatedBy
) {
}
