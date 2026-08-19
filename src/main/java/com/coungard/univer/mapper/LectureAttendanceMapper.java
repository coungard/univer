package com.coungard.univer.mapper;

import com.coungard.univer.dto.LectureAttendanceDto;
import com.coungard.univer.entity.LectureAttendance;
import org.springframework.stereotype.Component;

@Component
public class LectureAttendanceMapper {

  public LectureAttendanceDto toDto(LectureAttendance attendance) {
    if (attendance == null) {
      return null;
    }
    return LectureAttendanceDto.builder()
        .studentId(attendance.getStudent().getId())
        .lectureId(attendance.getLecture().getId())
        .attended(attendance.isAttended())
        .build();
  }

  public LectureAttendance toEntity(LectureAttendanceDto dto) {
    if (dto == null) {
      return null;
    }
    LectureAttendance attendance = new LectureAttendance();
    attendance.setAttended(dto.attended());
    return attendance;
  }
}
