package com.coungard.univer.mapper;

import com.coungard.univer.dto.SemesterDto;
import com.coungard.univer.entity.Semester;
import org.springframework.stereotype.Component;

@Component
public class SemesterMapper {

  public SemesterDto toDto(Semester semester) {
    if (semester == null) {
      return null;
    }
    return SemesterDto.builder()
        .id(semester.getId())
        .studyYearId(semester.getStudyYear().getId())
        .type(semester.getType())
        .startDate(semester.getStartDate())
        .endDate(semester.getEndDate())
        .build();
  }

  public Semester toEntity(SemesterDto dto) {
    if (dto == null) {
      return null;
    }
    Semester semester = new Semester();
    semester.setType(dto.type());
    semester.setStartDate(dto.startDate());
    semester.setEndDate(dto.endDate());
    return semester;
  }
}
