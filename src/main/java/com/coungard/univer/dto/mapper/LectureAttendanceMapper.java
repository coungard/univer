package com.coungard.univer.dto.mapper;

import com.coungard.univer.dto.LectureAttendanceDto;
import com.coungard.univer.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {StudentMapper.class, LectureMapper.class, CommonMappers.class})
public interface LectureAttendanceMapper {

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "lecture.id", target = "lectureId")
    LectureAttendanceDto toDto(LectureAttendance entity);

    @Mapping(target = "student", source = "studentId", qualifiedByName = "mapToStudent")
    @Mapping(target = "lecture", source = "lectureId", qualifiedByName = "mapToLecture")
    LectureAttendance toEntity(LectureAttendanceDto dto);

    default LectureAttendanceId toId(LectureAttendanceDto dto) {
        return new LectureAttendanceId(dto.studentId(), dto.lectureId());
    }
}