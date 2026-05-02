package com.coungard.univer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DepartmentDto(

        UUID id,
        @NotBlank(message = "Название кафедры обязательно")
        String name,
        String description,
        UUID facultyId
) {
}