package com.coungard.univer.dto.mapper;

import com.coungard.univer.dto.EnrollmentDto;
import com.coungard.univer.entity.Enrollment;
import com.coungard.univer.entity.EnrollmentId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {StudentMapper.class, CourseMapper.class, CommonMappers.class})
public interface EnrollmentMapper {

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "course.id", target = "courseId")
    EnrollmentDto toDto(Enrollment entity);

    @Mapping(target = "student", source = "studentId", qualifiedByName="mapToStudent")
    @Mapping(target = "course", source = "courseId", qualifiedByName ="mapToCourse")
    Enrollment toEntity(EnrollmentDto dto);

    // Вспомогательный метод для создания ID
    default EnrollmentId toId(EnrollmentDto dto) {
        return new EnrollmentId(dto.studentId(), dto.courseId());
    }
}