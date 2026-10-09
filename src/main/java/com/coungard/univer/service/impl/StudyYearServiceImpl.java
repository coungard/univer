package com.coungard.univer.service.impl;

import com.coungard.univer.dto.StudyYearDto;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.mapper.StudyYearMapper;
import com.coungard.univer.repository.FacultyRepository;
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
  private final FacultyRepository facultyRepository;
  private final StudyYearMapper studyYearMapper;

  @Override
  @Transactional
  public StudyYearDto createStudyYear(StudyYearDto studyYearDto) {
    validateFacultyExists(studyYearDto.facultyId());

    validateUnique(studyYearDto.facultyId(), studyYearDto.yearNumber());

    StudyYear studyYear = studyYearMapper.toEntity(studyYearDto);

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
  public Page<StudyYearDto> getStudyYearsByFaculty(UUID facultyId, Pageable pageable) {
    return studyYearRepository.findByFacultyId(facultyId, pageable).map(studyYearMapper::toDto);
  }

  @Override
  @Transactional
  public StudyYearDto updateStudyYear(UUID id, StudyYearDto studyYearDto) {
    StudyYear existing = studyYearRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Курс обучения не найден с ID: " + id));

    validateFacultyExists(studyYearDto.facultyId());

    boolean changed = !existing.getYearNumber().equals(studyYearDto.yearNumber())
        || !existing.getFacultyId().equals(studyYearDto.facultyId());
    if (changed) {
      validateUnique(studyYearDto.facultyId(), studyYearDto.yearNumber());
    }

    existing.setFacultyId(studyYearDto.facultyId());
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

  private void validateFacultyExists(UUID facultyId) {
    if (!facultyRepository.existsById(facultyId)) {
      throw new ResourceNotFoundException("Факультет не найден с ID: " + facultyId);
    }
  }

  private void validateUnique(UUID facultyId, Integer yearNumber) {
    if (studyYearRepository.existsByFacultyIdAndYearNumber(facultyId, yearNumber)) {
      throw new ValidationException(
          "Курс " + yearNumber + " уже существует на факультете с ID: " + facultyId);
    }
  }
}
