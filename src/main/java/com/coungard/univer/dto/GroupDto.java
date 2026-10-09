package com.coungard.univer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Builder;

@Builder
public record GroupDto(
    UUID id,

    @NotNull(message = "ID семестра обязателен")
    UUID semesterId,

    @NotBlank(message = "Название группы обязательно")
    @Size(max = 64, message = "Название группы не длиннее 64 символов")
    String name,

    @Size(max = 255, message = "Расшифровка названия группы не длиннее 255 символов")
    String fullName,

    UUID createdByStudentId
) {
}
