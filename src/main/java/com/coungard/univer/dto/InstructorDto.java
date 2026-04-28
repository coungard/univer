package com.coungard.univer.dto;

import java.util.UUID;

public record InstructorDto(
    UUID id,
    String firstName,
    String lastName,
    String email,
    UUID departmentId
) {}