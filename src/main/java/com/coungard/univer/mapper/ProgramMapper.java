package com.coungard.univer.mapper;

import com.coungard.univer.dto.ProgramDto;
import com.coungard.univer.dto.StudyDuration;
import com.coungard.univer.dto.request.CreateProgramRequest;
import com.coungard.univer.entity.Program;
import java.time.Period;
import org.springframework.stereotype.Component;

@Component
public class ProgramMapper {

  public ProgramDto toDto(Program program) {
    if (program == null) {
      return null;
    }
    return new ProgramDto(
        program.getId(),
        program.getFacultyId(),
        program.getCode(),
        program.getName(),
        program.getProfession(),
        program.getDirection(),
        program.getEducationLevel(),
        program.getEducationForm(),
        this.durationToStudyDuration(program.getDurationOfStudy()),
        program.getQualification()
    );
  }

  public Program toEntity(CreateProgramRequest dto) {
    if (dto == null) {
      return null;
    }
    Program program = new Program();
    program.setFacultyId(dto.facultyId());
    program.setCode(dto.code());
    program.setName(dto.name());
    program.setProfession(dto.profession());
    program.setDirection(dto.direction());
    program.setEducationLevel(dto.educationLevel());
    program.setEducationForm(dto.educationForm());
    program.setDurationOfStudy(this.studyDurationToDuration(dto.durationOfStudy()));
    program.setQualification(dto.qualification());
    return program;
  }

  /**
   * Конвертация Duration в StudyDuration
   */
  private StudyDuration durationToStudyDuration(Period period) {
    if (period == null) {
      return null;
    }
    return new StudyDuration(period.getYears(), period.getMonths(), period.getDays());
  }

  /**
   * Конвертация StudyDuration в Duration
   */
  public Period studyDurationToDuration(StudyDuration sd) {
    if (sd == null) {
      return null;
    }

    return Period.ofYears(sd.getYears()).plusMonths(sd.getMonths()).plusDays(sd.getDays());
  }
}