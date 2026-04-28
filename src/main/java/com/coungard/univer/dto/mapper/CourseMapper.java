package com.coungard.univer.dto.mapper;

import com.coungard.univer.dto.CourseDto;
import com.coungard.univer.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {DepartmentMapper.class, InstructorMapper.class, CommonMappers.class})
public interface CourseMapper {

    CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "instructor.id", target = "instructorId")
    CourseDto toDto(Course entity);

    @Mapping(target = "department", source = "departmentId", qualifiedByName = "mapToDepartment")
    @Mapping(target = "instructor", source = "instructorId", qualifiedByName = "mapToInstructor")
    Course toEntity(CourseDto dto);
}