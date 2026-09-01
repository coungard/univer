package com.coungard.univer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record UniversityDto(
        UUID id,
        String name,
        String description,

        @Schema(description = "ФИО ректора", example = "Месхи Бесарион Чохоевич")
        String rector,

        @Min(value = 1000, message = "Год основания указан некорректно")
        @Schema(description = "Год основания", example = "1930")
        Integer foundingYear,

        @PositiveOrZero(message = "Число студентов не может быть отрицательным")
        @Schema(description = "Число студентов", example = "20000")
        Integer studentCount,

        Instant createdAt,
        Instant updatedAt,
        AddressDto address,
        List<FacultyDto> faculties
) {
    public UniversityDto {
        faculties = faculties == null ? List.of() : faculties;
    }
}
