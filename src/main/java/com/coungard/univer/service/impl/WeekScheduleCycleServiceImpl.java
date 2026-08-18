package com.coungard.univer.service.impl;

import com.coungard.univer.dto.WeekScheduleCycleDto;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.WeekScheduleCycle;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.mapper.WeekScheduleCycleMapper;
import com.coungard.univer.repository.SemesterRepository;
import com.coungard.univer.repository.WeekScheduleCycleRepository;
import com.coungard.univer.service.WeekScheduleCycleService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WeekScheduleCycleServiceImpl implements WeekScheduleCycleService {

  private final WeekScheduleCycleRepository weekScheduleCycleRepository;
  private final SemesterRepository semesterRepository;
  private final WeekScheduleCycleMapper weekScheduleCycleMapper;

  @Override
  @Transactional
  public WeekScheduleCycleDto createWeekScheduleCycle(WeekScheduleCycleDto cycleDto) {
    Semester semester = semesterRepository.findById(cycleDto.semesterId())
        .orElseThrow(() -> new ResourceNotFoundException("Семестр не найден с ID: " + cycleDto.semesterId()));

    if (weekScheduleCycleRepository.existsBySemesterId(cycleDto.semesterId())) {
      throw new ValidationException(
          "Циклическое расписание для семестра с ID: " + cycleDto.semesterId() + " уже существует");
    }

    WeekScheduleCycle cycle = weekScheduleCycleMapper.toEntity(cycleDto);
    cycle.setSemester(semester);

    WeekScheduleCycle saved = weekScheduleCycleRepository.save(cycle);
    return weekScheduleCycleMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public WeekScheduleCycleDto getWeekScheduleCycleById(UUID id) {
    WeekScheduleCycle cycle = weekScheduleCycleRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Циклическое расписание не найдено с ID: " + id));
    return weekScheduleCycleMapper.toDto(cycle);
  }

  @Override
  @Transactional(readOnly = true)
  public WeekScheduleCycleDto getWeekScheduleCycleBySemester(UUID semesterId) {
    WeekScheduleCycle cycle = weekScheduleCycleRepository.findBySemesterId(semesterId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Циклическое расписание не найдено для семестра с ID: " + semesterId));
    return weekScheduleCycleMapper.toDto(cycle);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<WeekScheduleCycleDto> getWeekScheduleCycles(Pageable pageable) {
    return weekScheduleCycleRepository.findAll(pageable).map(weekScheduleCycleMapper::toDto);
  }

  @Override
  @Transactional
  public void deleteWeekScheduleCycle(UUID id) {
    if (!weekScheduleCycleRepository.existsById(id)) {
      throw new ResourceNotFoundException("Циклическое расписание не найдено с ID: " + id);
    }
    weekScheduleCycleRepository.deleteById(id);
  }
}
