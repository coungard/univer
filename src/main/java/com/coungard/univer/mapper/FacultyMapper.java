package com.coungard.univer.mapper;

import com.coungard.univer.dto.DepartmentDto;
import com.coungard.univer.dto.FacultyDto;
import com.coungard.univer.entity.Department;
import com.coungard.univer.entity.Faculty;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FacultyMapper {

    @Mapping(source = "university.id", target = "universityId")
    FacultyDto toDto(Faculty faculty);

    // Faculty собирается через Lombok-builder, в котором нет полей AuditableEntity, — игнорировать их
    // здесь не нужно (и нельзя).
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "university.id", source = "universityId")
    @Mapping(target = "createdByStudentId", ignore = true)
    Faculty toEntity(FacultyDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "university", ignore = true)
    @Mapping(target = "createdByStudentId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(FacultyDto dto, @MappingTarget Faculty faculty);

    // Поля аудита в DTO только для ответа (issue #119): из запроса в кафедру не переносятся.
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Department toDepartment(DepartmentDto dto);
}
