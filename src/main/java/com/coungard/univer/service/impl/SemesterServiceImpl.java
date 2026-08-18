package com.coungard.univer.service.impl;

import com.coungard.univer.dto.SemesterDto;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.mapper.SemesterMapper;
import com.coungard.univer.repository.SemesterRepository;
import com.coungard.univer.repository.StudyYearRepository;
import com.coungard.univer.service.SemesterService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SemesterServiceImpl implements SemesterService {

  private final SemesterRepository semesterRepository;
  private final StudyYearRepository studyYearRepository;
  private final SemesterMapper semesterMapper;

  @Override
  @Transactional
  public SemesterDto createSemester(SemesterDto semesterDto) {
    StudyYear studyYear = studyYearRepository.findById(semesterDto.studyYearId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Курс обучения не найден с ID: " + semesterDto.studyYearId()));

    Semester semester = semesterMapper.toEntity(semesterDto);
    semester.setStudyYear(studyYear);

    Semester saved = semesterRepository.save(semester);
    return semesterMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public SemesterDto getSemesterById(UUID id) {
    Semester semester = semesterRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Семестр не найден с ID: " + id));
    return semesterMapper.toDto(semester);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<SemesterDto> getSemesters(Pageable pageable) {
    return semesterRepository.findAll(pageable).map(semesterMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<SemesterDto> getSemestersByStudyYear(UUID studyYearId, Pageable pageable) {
    return semesterRepository.findByStudyYearId(studyYearId, pageable).map(semesterMapper::toDto);
  }

  @Override
  @Transactional
  public SemesterDto updateSemester(UUID id, SemesterDto semesterDto) {
    Semester existing = semesterRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Семестр не найден с ID: " + id));

    StudyYear studyYear = studyYearRepository.findById(semesterDto.studyYearId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Курс обучения не найден с ID: " + semesterDto.studyYearId()));

    existing.setStudyYear(studyYear);
    existing.setType(semesterDto.type());
    existing.setStartDate(semesterDto.startDate());

    Semester updated = semesterRepository.save(existing);
    return semesterMapper.toDto(updated);
  }

  @Override
  @Transactional
  public void deleteSemester(UUID id) {
    if (!semesterRepository.existsById(id)) {
      throw new ResourceNotFoundException("Семестр не найден с ID: " + id);
    }
    semesterRepository.deleteById(id);
  }
}
