package com.coungard.univer.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record UniversityDto(
        UUID id,
        String name,
        String description,
        LocalDateTime createdAt,
        AddressDto address,
        List<FacultyDto> faculties
) {
    public UniversityDto {
        faculties = faculties == null ? List.of() : faculties;
    }
}