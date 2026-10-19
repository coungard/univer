package com.coungard.univer.service.impl;

import com.coungard.univer.dto.AuditAction;
import com.coungard.univer.dto.AuditEntityType;
import com.coungard.univer.dto.SemesterDto;
import com.coungard.univer.dto.SemesterWeekDto;
import com.coungard.univer.dto.WeekParity;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.mapper.SemesterMapper;
import com.coungard.univer.repository.SemesterRepository;
import com.coungard.univer.repository.StudyYearRepository;
import com.coungard.univer.service.AuditService;
import com.coungard.univer.service.SemesterService;
import java.time.LocalDate;
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
  private final AuditService auditService;

  @Override
  @Transactional
  public SemesterDto createSemester(SemesterDto semesterDto) {
    validateDateOrder(semesterDto);

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
  public SemesterWeekDto getSemesterWeek(UUID id, LocalDate date) {
    Semester semester = semesterRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Семестр не найден с ID: " + id));

    LocalDate day = date != null ? date : LocalDate.now();
    LocalDate weekStart = WeekParity.weekStart(day);
    LocalDate weekEnd = weekStart.plusDays(6);
    // Неделя, а не сама дата: понедельник первой недели семестра, начавшегося в среду, — тоже неделя 1
    boolean inSemester = !weekStart.isAfter(semester.getEndDate()) && !weekEnd.isBefore(semester.getStartDate());

    return SemesterWeekDto.builder()
        .semesterId(id)
        .date(day)
        .weekStart(weekStart)
        .weekEnd(weekEnd)
        .inSemester(inSemester)
        .weekNumber(inSemester ? WeekParity.weekNumber(semester.getStartDate(), day) : null)
        .weekParity(inSemester ? WeekParity.of(semester.getStartDate(), day) : null)
        .build();
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
    validateDateOrder(semesterDto);

    Semester existing = semesterRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Семестр не найден с ID: " + id));

    StudyYear studyYear = studyYearRepository.findById(semesterDto.studyYearId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Курс обучения не найден с ID: " + semesterDto.studyYearId()));

    existing.setStudyYear(studyYear);
    existing.setType(semesterDto.type());
    existing.setStartDate(semesterDto.startDate());
    existing.setEndDate(semesterDto.endDate());

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
    auditService.record(AuditAction.DELETE, AuditEntityType.SEMESTER, id);
  }

  private void validateDateOrder(SemesterDto semesterDto) {
    if (!semesterDto.endDate().isAfter(semesterDto.startDate())) {
      throw new ValidationException("Дата окончания семестра должна быть позже даты начала");
    }
  }
}
