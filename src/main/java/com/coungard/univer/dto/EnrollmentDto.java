package com.coungard.univer.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record EnrollmentDto(
    UUID studentId,
    UUID courseId,
    LocalDateTime enrolledAt,
    String status
) {}