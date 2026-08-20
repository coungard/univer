package com.coungard.univer.service.impl;

import com.coungard.univer.dto.PairDto;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Pair;
import com.coungard.univer.entity.Teacher;
import com.coungard.univer.entity.WeekScheduleCycle;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.mapper.PairMapper;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.GroupRepository;
import com.coungard.univer.repository.PairRepository;
import com.coungard.univer.repository.TeacherRepository;
import com.coungard.univer.repository.WeekScheduleCycleRepository;
import com.coungard.univer.service.PairService;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PairServiceImpl implements PairService {

  private static final Set<DayOfWeek> WEEKDAYS = Set.of(
      DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);

  private final PairRepository pairRepository;
  private final WeekScheduleCycleRepository weekScheduleCycleRepository;
  private final CourseRepository courseRepository;
  private final TeacherRepository teacherRepository;
  private final GroupRepository groupRepository;
  private final PairMapper pairMapper;

  @Override
  @Transactional
  public PairDto createPair(PairDto pairDto) {
    WeekScheduleCycle cycle = weekScheduleCycleRepository.findById(pairDto.weekScheduleCycleId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Циклическое расписание не найдено с ID: " + pairDto.weekScheduleCycleId()));

    Course course = courseRepository.findById(pairDto.courseId())
        .orElseThrow(() -> new ResourceNotFoundException("Учебный курс не найден с ID: " + pairDto.courseId()));

    validateSchedule(pairDto);

    Pair pair = pairMapper.toEntity(pairDto);
    pair.setWeekScheduleCycle(cycle);
    pair.setCourse(course);
    pair.setTeacher(resolveTeacher(pairDto.teacherId()));
    pair.setGroups(resolveGroups(pairDto.groupIds()));

    Pair saved = pairRepository.save(pair);
    return pairMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public PairDto getPairById(UUID id) {
    Pair pair = pairRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Пара не найдена с ID: " + id));
    return pairMapper.toDto(pair);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PairDto> getPairs(Pageable pageable) {
    return pairRepository.findAll(pageable).map(pairMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PairDto> getPairsByWeekScheduleCycle(UUID weekScheduleCycleId, Pageable pageable) {
    return pairRepository.findByWeekScheduleCycleId(weekScheduleCycleId, pageable).map(pairMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PairDto> getPairsByGroup(UUID groupId, Pageable pageable) {
    return pairRepository.findByGroupsId(groupId, pageable).map(pairMapper::toDto);
  }

  @Override
  @Transactional
  public PairDto updatePair(UUID id, PairDto pairDto) {
    Pair existing = pairRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Пара не найдена с ID: " + id));

    WeekScheduleCycle cycle = weekScheduleCycleRepository.findById(pairDto.weekScheduleCycleId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Циклическое расписание не найдено с ID: " + pairDto.weekScheduleCycleId()));

    Course course = courseRepository.findById(pairDto.courseId())
        .orElseThrow(() -> new ResourceNotFoundException("Учебный курс не найден с ID: " + pairDto.courseId()));

    validateSchedule(pairDto);

    existing.setWeekScheduleCycle(cycle);
    existing.setCourse(course);
    existing.setTeacher(resolveTeacher(pairDto.teacherId()));
    existing.setDayOfWeek(pairDto.dayOfWeek());
    existing.setWeekParity(pairDto.weekParity());
    existing.setPairNumber(pairDto.pairNumber());
    existing.setStartTime(pairDto.startTime());
    existing.setEndTime(pairDto.endTime());
    existing.setRoom(pairDto.room());
    existing.setGroups(resolveGroups(pairDto.groupIds()));

    Pair updated = pairRepository.save(existing);
    return pairMapper.toDto(updated);
  }

  @Override
  @Transactional
  public void deletePair(UUID id) {
    if (!pairRepository.existsById(id)) {
      throw new ResourceNotFoundException("Пара не найдена с ID: " + id);
    }
    pairRepository.deleteById(id);
  }

  private void validateSchedule(PairDto pairDto) {
    if (!WEEKDAYS.contains(pairDto.dayOfWeek())) {
      throw new ValidationException(
          "День недели должен быть с понедельника по пятницу, получено: " + pairDto.dayOfWeek());
    }
    if (!pairDto.endTime().isAfter(pairDto.startTime())) {
      throw new ValidationException("Время окончания пары должно быть позже времени начала");
    }
  }

  private Teacher resolveTeacher(UUID teacherId) {
    if (teacherId == null) {
      return null;
    }
    return teacherRepository.findById(teacherId)
        .orElseThrow(() -> new ResourceNotFoundException("Преподаватель не найден с ID: " + teacherId));
  }

  private Set<Group> resolveGroups(Set<UUID> groupIds) {
    Set<Group> groups = new HashSet<>();
    for (UUID groupId : groupIds) {
      Group group = groupRepository.findById(groupId)
          .orElseThrow(() -> new ResourceNotFoundException("Группа не найдена с ID: " + groupId));
      groups.add(group);
    }
    return groups;
  }
}
