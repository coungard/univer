package com.coungard.univer.dto.mapper;

import com.coungard.univer.dto.LectureDto;
import com.coungard.univer.entity.Lecture;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CourseMapper.class, InstructorMapper.class, CommonMappers.class})
public interface LectureMapper {

    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "instructor.id", target = "instructorId")
    LectureDto toDto(Lecture entity);

    @Mapping(target = "course", source = "courseId", qualifiedByName = "mapToCourse")
    @Mapping(target = "instructor", source = "instructorId", qualifiedByName = "mapToInstructor")
    @Mapping(target = "content", ignore = true)
    Lecture toEntity(LectureDto dto);
}