package com.coungard.univer.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;

@Builder
public record WeekScheduleCycleDto(
    UUID id,

    @NotNull(message = "ID семестра обязателен")
    UUID semesterId
) {
}
