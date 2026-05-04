package com.coungard.univer.dto;

import java.util.UUID;

public record CourseDto(
        UUID id,
        String title,
        String description,
        UUID departmentId
) {
}