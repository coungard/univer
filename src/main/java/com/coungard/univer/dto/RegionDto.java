package com.coungard.univer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(description = "Субъект РФ")
public record RegionDto(
        UUID id,

        @Schema(description = "Двузначный код субъекта РФ", example = "05")
        String code,

        @Schema(description = "Официальное название", example = "Республика Дагестан")
        String name
) {
}
