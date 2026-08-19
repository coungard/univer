package com.coungard.univer.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder(toBuilder = true)
public record EnrollmentDto(

    @NotNull(message = "ID студента обязателен")
    UUID studentId,

    @NotNull(message = "ID курса обязателен")
    UUID courseId,

    LocalDateTime enrolledAt,

    EnrollmentStatus status
) {
}
