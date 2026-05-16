package com.coungard.univer.dto;

import java.util.UUID;

public record ProgramDto(
    UUID id,
    UUID facultyId,
    String code,
    String name,
    String profession,
    String direction,
    String educationLevel,
    EducationForm educationForm,
    Integer durationOfStudy,
    String qualification
) {

}