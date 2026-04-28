package com.coungard.univer.dto;

import java.util.UUID;

public record DepartmentDto(
    UUID id,
    String name,
    UUID universityId
) {}