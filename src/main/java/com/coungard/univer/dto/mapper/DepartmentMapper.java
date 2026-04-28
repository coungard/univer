package com.coungard.univer.dto.mapper;

import com.coungard.univer.dto.DepartmentDto;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Department;
import com.coungard.univer.entity.University;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {UniversityMapper.class, CommonMappers.class})
public interface DepartmentMapper {

    @Mapping(source = "university.id", target = "universityId")
    DepartmentDto toDto(Department entity);

    @Mapping(target = "university", source = "universityId", qualifiedByName="mapToUniversity")
    Department toEntity(DepartmentDto dto);
}