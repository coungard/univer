package com.coungard.univer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CourseDto(
        UUID id,

        @NotBlank(message = "Название курса обязательно")
        String title,

        String description,

        UUID departmentId,

        UUID teacherId
) {
}
