package com.coungard.univer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;

@Builder
public record StudyYearDto(
    UUID id,

    @NotNull(message = "ID программы обязателен")
    UUID programId,

    @NotNull(message = "Номер курса обязателен")
    @Min(value = 1, message = "Номер курса не может быть меньше 1")
    Integer yearNumber
) {
}
