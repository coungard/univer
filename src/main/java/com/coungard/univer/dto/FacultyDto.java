package com.coungard.univer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FacultyDto(

        UUID id,
        String name,
        String description,
        List<DepartmentDto> departments,
        UUID universityId
) {
    public FacultyDto {
        departments = departments == null ? List.of() : departments;
    }
}
