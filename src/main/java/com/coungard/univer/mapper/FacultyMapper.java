package com.coungard.univer.mapper;

import com.coungard.univer.dto.FacultyDto;
import com.coungard.univer.entity.Faculty;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FacultyMapper {

    @Mapping(source = "university.id", target = "universityId")
    FacultyDto toDto(Faculty faculty);

    // Кафедры — только в ответе: создаются и меняются через /departments (issue #121). Иначе пустой по
    // умолчанию FacultyDto.departments заменял бы ими все кафедры факультета (orphanRemoval).
    // Faculty собирается через Lombok-builder, в котором нет полей AuditableEntity, — игнорировать их
    // здесь не нужно (и нельзя).
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "university.id", source = "universityId")
    @Mapping(target = "createdByStudentId", ignore = true)
    @Mapping(target = "departments", ignore = true)
    Faculty toEntity(FacultyDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "university", ignore = true)
    @Mapping(target = "createdByStudentId", ignore = true)
    @Mapping(target = "departments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(FacultyDto dto, @MappingTarget Faculty faculty);
}
