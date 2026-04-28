package com.coungard.univer.dto.mapper;

import com.coungard.univer.dto.StudentDto;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.University;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {UniversityMapper.class, CommonMappers.class})
public interface StudentMapper {

    @Mapping(source = "university.id", target = "universityId")
    StudentDto toDto(Student entity);

    @Mapping(target = "university", source = "universityId", qualifiedByName = "mapToUniversity")
    Student toEntity(StudentDto dto);
}