package com.coungard.univer.mapper;

import com.coungard.univer.dto.FacultyDto;
import com.coungard.univer.entity.Faculty;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FacultyMapper {

    @Mapping(source = "university.id", target = "universityId")
    FacultyDto toDto(Faculty faculty);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "university.id", source = "universityId")
    Faculty toEntity(FacultyDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "university", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(FacultyDto dto, @MappingTarget Faculty faculty);
}