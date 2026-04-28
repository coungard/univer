package com.coungard.univer.dto.mapper;

import com.coungard.univer.dto.InstructorDto;
import com.coungard.univer.entity.Instructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DepartmentMapper.class, CommonMappers.class})
public interface InstructorMapper {

    @Mapping(source = "department.id", target = "departmentId")
    InstructorDto toDto(Instructor entity);

    @Mapping(target = "department", source = "departmentId", qualifiedByName = "mapToDepartment")
    Instructor toEntity(InstructorDto dto);
}
