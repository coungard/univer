package com.coungard.univer.dto;

import java.time.Instant;
import java.util.UUID;

public record TeacherDto(
    UUID id,
    String firstname,
    String lastname,
    String fullname,
    Instant createdAt,
    Instant updatedAt,
    String email,
    UUID departmentId
) {}