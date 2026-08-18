package com.coungard.univer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CourseDto(
        UUID id,

        @NotBlank(message = "Название курса обязательно")
        String title,

        String description,

        @NotNull(message = "ID кафедры обязателен")
        UUID departmentId,

        UUID teacherId
) {
}
