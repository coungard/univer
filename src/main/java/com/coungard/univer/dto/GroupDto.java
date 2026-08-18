package com.coungard.univer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;

@Builder
public record GroupDto(
    UUID id,

    @NotNull(message = "ID семестра обязателен")
    UUID semesterId,

    @NotBlank(message = "Название группы обязательно")
    String name
) {
}
