package com.coungard.univer.mapper;

import com.coungard.univer.dto.StudyYearDto;
import com.coungard.univer.entity.StudyYear;
import org.springframework.stereotype.Component;

@Component
public class StudyYearMapper {

  public StudyYearDto toDto(StudyYear studyYear) {
    if (studyYear == null) {
      return null;
    }
    return StudyYearDto.builder()
        .id(studyYear.getId())
        .programId(studyYear.getProgram().getId())
        .yearNumber(studyYear.getYearNumber())
        .build();
  }

  public StudyYear toEntity(StudyYearDto dto) {
    if (dto == null) {
      return null;
    }
    StudyYear studyYear = new StudyYear();
    studyYear.setYearNumber(dto.yearNumber());
    return studyYear;
  }
}
