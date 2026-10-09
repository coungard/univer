package com.coungard.univer.service.impl;

import com.coungard.univer.dto.GroupDto;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.exception.ConflictException;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.mapper.GroupMapper;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.GroupRepository;
import com.coungard.univer.repository.SemesterRepository;
import com.coungard.univer.repository.StudyYearRepository;
import com.coungard.univer.service.GroupService;
import com.coungard.univer.service.SemesterPeriod;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

  private final GroupRepository groupRepository;
  private final SemesterRepository semesterRepository;
  private final StudyYearRepository studyYearRepository;
  private final FacultyRepository facultyRepository;
  private final GroupMapper groupMapper;

  @Override
  @Transactional
  public GroupDto createGroup(GroupDto groupDto) {
    Semester semester = semesterRepository.findById(groupDto.semesterId())
        .orElseThrow(() -> new ResourceNotFoundException("Семестр не найден с ID: " + groupDto.semesterId()));

    Group saved = saveNewGroup(semester, groupDto.name(), groupDto.fullName());
    return groupMapper.toDto(saved);
  }

  @Override
  @Transactional
  public GroupDto createGroupInCurrentSemester(UUID facultyId, Integer yearNumber, String name, String fullName) {
    if (!facultyRepository.existsById(facultyId)) {
      throw new ResourceNotFoundException("Факультет не найден с ID: " + facultyId);
    }

    StudyYear studyYear = studyYearRepository.findByFacultyIdAndYearNumber(facultyId, yearNumber)
        .orElseGet(() -> {
          StudyYear created = new StudyYear();
          created.setFacultyId(facultyId);
          created.setYearNumber(yearNumber);
          return studyYearRepository.save(created);
        });

    LocalDate today = LocalDate.now();
    Semester semester = findCurrentSemester(studyYear, today)
        .orElseGet(() -> {
          SemesterPeriod period = SemesterPeriod.at(today);
          Semester created = new Semester();
          created.setStudyYear(studyYear);
          created.setType(period.type());
          created.setStartDate(period.startDate());
          created.setEndDate(period.endDate());
          return semesterRepository.save(created);
        });

    Group saved = saveNewGroup(semester, name, fullName);
    return groupMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public GroupDto getGroupById(UUID id) {
    Group group = groupRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Группа не найдена с ID: " + id));
    return groupMapper.toDto(group);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<GroupDto> getGroups(Pageable pageable) {
    return groupRepository.findAll(pageable).map(groupMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<GroupDto> getGroups(UUID facultyId, Integer yearNumber, Pageable pageable) {
    if (facultyId == null && yearNumber == null) {
      return getGroups(pageable);
    }
    return groupRepository.findCurrentSemesterGroups(facultyId, yearNumber, LocalDate.now(), pageable)
        .map(groupMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<GroupDto> getGroupsBySemester(UUID semesterId, Pageable pageable) {
    return groupRepository.findBySemesterId(semesterId, pageable).map(groupMapper::toDto);
  }

  @Override
  @Transactional
  public GroupDto updateGroup(UUID id, GroupDto groupDto) {
    Group existing = groupRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Группа не найдена с ID: " + id));

    Semester semester = semesterRepository.findById(groupDto.semesterId())
        .orElseThrow(() -> new ResourceNotFoundException("Семестр не найден с ID: " + groupDto.semesterId()));

    String name = groupDto.name().strip();
    validateNameIsFree(semester, name, id);

    existing.setSemester(semester);
    existing.setName(name);
    existing.setFullName(normalizeFullName(groupDto.fullName()));

    Group updated = groupRepository.save(existing);
    return groupMapper.toDto(updated);
  }

  @Override
  @Transactional
  public void deleteGroup(UUID id) {
    if (!groupRepository.existsById(id)) {
      throw new ResourceNotFoundException("Группа не найдена с ID: " + id);
    }
    groupRepository.deleteById(id);
  }

  private Group saveNewGroup(Semester semester, String rawName, String fullName) {
    String name = rawName.strip();
    validateNameIsFree(semester, name, null);

    Group group = new Group();
    group.setSemester(semester);
    group.setName(name);
    group.setFullName(normalizeFullName(fullName));
    return groupRepository.save(group);
  }

  /**
   * Название группы уникально в пределах семестра без учёта регистра и крайних пробелов.
   *
   * @param selfId ID самой обновляемой группы — она себе не мешает; {@code null} при создании
   */
  private void validateNameIsFree(Semester semester, String name, UUID selfId) {
    groupRepository.findBySemesterIdAndNameIgnoreCase(semester.getId(), name).stream()
        .filter(group -> !group.getId().equals(selfId))
        .findFirst()
        .ifPresent(group -> {
          throw new ConflictException("name",
              "Группа с таким названием в этом семестре уже есть: " + group.getName(), group.getId());
        });
  }

  private String normalizeFullName(String fullName) {
    return StringUtils.hasText(fullName) ? fullName.strip() : null;
  }

  /**
   * Актуальный на дату {@code today} семестр учебного года — по тому же правилу, что и в
   * {@link GroupRepository#findCurrentSemesterGroups}: идущий сейчас, иначе ближайший будущий, иначе
   * последний закончившийся. Пусто, только если семестров у учебного года нет вовсе.
   */
  private Optional<Semester> findCurrentSemester(StudyYear studyYear, LocalDate today) {
    List<Semester> semesters = semesterRepository.findAllByStudyYearId(studyYear.getId());

    Optional<Semester> running = semesters.stream()
        .filter(s -> !s.getStartDate().isAfter(today) && !s.getEndDate().isBefore(today))
        .min(Comparator.comparing(Semester::getStartDate).thenComparing(Semester::getId));
    if (running.isPresent()) {
      return running;
    }

    Optional<Semester> upcoming = semesters.stream()
        .filter(s -> s.getStartDate().isAfter(today))
        .min(Comparator.comparing(Semester::getStartDate).thenComparing(Semester::getId));
    if (upcoming.isPresent()) {
      return upcoming;
    }

    return semesters.stream()
        .max(Comparator.comparing(Semester::getEndDate).thenComparing(Semester::getId));
  }
}
