package com.coungard.univer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    /** Заполняется автоматически при генерации из шаблона Pair — не для ручного заполнения. */
    UUID sourcePairId,

    @NotEmpty(message = "Лекция должна быть привязана хотя бы к одной группе")
    Set<UUID> groupIds
) {
}
