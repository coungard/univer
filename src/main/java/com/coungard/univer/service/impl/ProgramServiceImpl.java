package com.coungard.univer.service.impl;

import com.coungard.univer.dto.ProgramDto;
import com.coungard.univer.dto.request.CreateProgramRequest;
import com.coungard.univer.entity.Program;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.mapper.ProgramMapper;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.ProgramRepository;
import com.coungard.univer.service.ProgramService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProgramServiceImpl implements ProgramService {

  private final ProgramRepository programRepository;
  private final FacultyRepository facultyRepository;
  private final ProgramMapper programMapper;

  @Override
  public ProgramDto createProgram(CreateProgramRequest programDto) {
    if (!facultyRepository.existsById(programDto.facultyId())) {
      throw new ResourceNotFoundException("Факультет с ID " + programDto.facultyId() + " не найден");
    }

    Program program = programMapper.toEntity(programDto);
    Program saved = programRepository.save(program);
    return programMapper.toDto(saved);
  }

  @Override
  public ProgramDto getProgramById(UUID id) {
    Program program = programRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Программа с ID " + id + " не найдена"));
    return programMapper.toDto(program);
  }

  @Override
  public Page<ProgramDto> getProgramsByFaculty(UUID facultyId, Pageable pageable) {
    return programRepository.findByFacultyId(facultyId, pageable)
        .map(programMapper::toDto);
  }

  @Override
  public Page<ProgramDto> getPrograms(Pageable pageable) {
    return programRepository.findAll(pageable).map(programMapper::toDto);
  }

  @Override
  public ProgramDto updateProgram(UUID id, CreateProgramRequest programDto) {
    Program program = programRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Программа с ID " + id + " не найдена"));

    if (!facultyRepository.existsById(programDto.facultyId())) {
      throw new ResourceNotFoundException("Факультет с ID " + programDto.facultyId() + " не найден");
    }

    program.setFacultyId(programDto.facultyId());
    program.setCode(programDto.code());
    program.setName(programDto.name());
    program.setProfession(programDto.profession());
    program.setDirection(programDto.direction());
    program.setEducationLevel(programDto.educationLevel());
    program.setEducationForm(programDto.educationForm());
    program.setDurationOfStudy(programMapper.studyDurationToDuration(programDto.durationOfStudy()));
    program.setQualification(programDto.qualification());

    Program updated = programRepository.save(program);
    return programMapper.toDto(updated);
  }

  @Override
  public void deleteProgram(UUID id) {
    if (!programRepository.existsById(id)) {
      throw new ResourceNotFoundException("Программа с ID " + id + " не найдена");
    }
    programRepository.deleteById(id);
  }
}