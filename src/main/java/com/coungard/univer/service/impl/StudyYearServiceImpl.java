package com.coungard.univer.service.impl;

import com.coungard.univer.dto.StudyYearDto;
import com.coungard.univer.entity.Program;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.mapper.StudyYearMapper;
import com.coungard.univer.repository.ProgramRepository;
import com.coungard.univer.repository.StudyYearRepository;
import com.coungard.univer.service.StudyYearService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyYearServiceImpl implements StudyYearService {

  private final StudyYearRepository studyYearRepository;
  private final ProgramRepository programRepository;
  private final StudyYearMapper studyYearMapper;

  @Override
  @Transactional
  public StudyYearDto createStudyYear(StudyYearDto studyYearDto) {
    Program program = programRepository.findById(studyYearDto.programId())
        .orElseThrow(() -> new ResourceNotFoundException("Программа не найдена с ID: " + studyYearDto.programId()));

    validateYearNumber(program, studyYearDto.yearNumber());
    validateUnique(studyYearDto.programId(), studyYearDto.yearNumber());

    StudyYear studyYear = studyYearMapper.toEntity(studyYearDto);
    studyYear.setProgram(program);

    StudyYear saved = studyYearRepository.save(studyYear);
    return studyYearMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public StudyYearDto getStudyYearById(UUID id) {
    StudyYear studyYear = studyYearRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Курс обучения не найден с ID: " + id));
    return studyYearMapper.toDto(studyYear);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<StudyYearDto> getStudyYears(Pageable pageable) {
    return studyYearRepository.findAll(pageable).map(studyYearMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<StudyYearDto> getStudyYearsByProgram(UUID programId, Pageable pageable) {
    return studyYearRepository.findByProgramId(programId, pageable).map(studyYearMapper::toDto);
  }

  @Override
  @Transactional
  public StudyYearDto updateStudyYear(UUID id, StudyYearDto studyYearDto) {
    StudyYear existing = studyYearRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Курс обучения не найден с ID: " + id));

    Program program = programRepository.findById(studyYearDto.programId())
        .orElseThrow(() -> new ResourceNotFoundException("Программа не найдена с ID: " + studyYearDto.programId()));

    validateYearNumber(program, studyYearDto.yearNumber());

    boolean changed = !existing.getYearNumber().equals(studyYearDto.yearNumber())
        || !existing.getProgram().getId().equals(studyYearDto.programId());
    if (changed) {
      validateUnique(studyYearDto.programId(), studyYearDto.yearNumber());
    }

    existing.setProgram(program);
    existing.setYearNumber(studyYearDto.yearNumber());

    StudyYear updated = studyYearRepository.save(existing);
    return studyYearMapper.toDto(updated);
  }

  @Override
  @Transactional
  public void deleteStudyYear(UUID id) {
    if (!studyYearRepository.existsById(id)) {
      throw new ResourceNotFoundException("Курс обучения не найден с ID: " + id);
    }
    studyYearRepository.deleteById(id);
  }

  private void validateYearNumber(Program program, Integer yearNumber) {
    if (program.getDurationOfStudy() == null) {
      return;
    }
    int maxYears = program.getDurationOfStudy().getYears();
    if (maxYears > 0 && yearNumber > maxYears) {
      throw new ValidationException(
          "Курс " + yearNumber + " превышает длительность программы (" + maxYears + " лет)");
    }
  }

  private void validateUnique(UUID programId, Integer yearNumber) {
    if (studyYearRepository.existsByProgramIdAndYearNumber(programId, yearNumber)) {
      throw new ValidationException(
          "Курс " + yearNumber + " уже существует для программы с ID: " + programId);
    }
  }
}
