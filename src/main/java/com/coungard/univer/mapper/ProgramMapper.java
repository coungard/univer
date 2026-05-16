package com.coungard.univer.mapper;

import com.coungard.univer.dto.ProgramDto;
import com.coungard.univer.dto.request.CreateProgramRequest;
import com.coungard.univer.entity.Program;
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
        program.getDurationOfStudy(),
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
    program.setDurationOfStudy(dto.durationOfStudy());
    program.setQualification(dto.qualification());
    return program;
  }
}