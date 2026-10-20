package com.coungard.univer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DepartmentDto(

        UUID id,
        @NotBlank(message = "Название кафедры обязательно")
        String name,
        String description,
        UUID facultyId,

        // Только в ответе (AuditableEntity, issue #119): во входящих запросах игнорируются.
        Instant createdAt,
        Instant updatedAt,
        UUID createdBy,
        UUID updatedBy
) {
}