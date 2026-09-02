package com.coungard.univer.service.impl;

import com.coungard.univer.dto.PairDto;
import com.coungard.univer.dto.WeekScheduleCycleStatus;
import com.coungard.univer.entity.BellScheduleEntry;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Pair;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.Teacher;
import com.coungard.univer.entity.WeekScheduleCycle;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.mapper.PairMapper;
import com.coungard.univer.repository.BellScheduleEntryRepository;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.GroupRepository;
import com.coungard.univer.repository.PairRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.TeacherRepository;
import com.coungard.univer.repository.WeekScheduleCycleRepository;
import com.coungard.univer.service.PairService;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Optional;
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
  private final BellScheduleEntryRepository bellScheduleEntryRepository;
  private final StudentRepository studentRepository;
  private final PairMapper pairMapper;

  @Override
  @Transactional
  public PairDto createPair(PairDto pairDto, UUID callerStudentId) {
    WeekScheduleCycle cycle = weekScheduleCycleRepository.findById(pairDto.weekScheduleCycleId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Циклическое расписание не найдено с ID: " + pairDto.weekScheduleCycleId()));

    Course course = courseRepository.findById(pairDto.courseId())
        .orElseThrow(() -> new ResourceNotFoundException("Учебный курс не найден с ID: " + pairDto.courseId()));

    ResolvedSchedule schedule = resolveSchedule(pairDto, course);
    Set<Group> groups = resolveGroups(pairDto.groupIds());
    checkStudentCanEdit(callerStudentId, cycle, groups);

    Pair pair = pairMapper.toEntity(pairDto);
    pair.setWeekScheduleCycle(cycle);
    pair.setCourse(course);
    pair.setTeacher(resolveTeacher(pairDto.teacherId()));
    pair.setGroups(groups);
    pair.setStartTime(schedule.startTime());
    pair.setEndTime(schedule.endTime());

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
  public PairDto updatePair(UUID id, PairDto pairDto, UUID callerStudentId) {
    Pair existing = pairRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Пара не найдена с ID: " + id));
    // Проверяем и текущее состояние (нельзя тронуть чужую/согласованную пару)...
    checkStudentCanEdit(callerStudentId, existing.getWeekScheduleCycle(), existing.getGroups());

    WeekScheduleCycle cycle = weekScheduleCycleRepository.findById(pairDto.weekScheduleCycleId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Циклическое расписание не найдено с ID: " + pairDto.weekScheduleCycleId()));

    Course course = courseRepository.findById(pairDto.courseId())
        .orElseThrow(() -> new ResourceNotFoundException("Учебный курс не найден с ID: " + pairDto.courseId()));

    ResolvedSchedule schedule = resolveSchedule(pairDto, course);
    Set<Group> groups = resolveGroups(pairDto.groupIds());
    // ...и новое (нельзя перенести пару в чужой цикл/группу или в уже согласованный цикл).
    checkStudentCanEdit(callerStudentId, cycle, groups);

    existing.setWeekScheduleCycle(cycle);
    existing.setCourse(course);
    existing.setTeacher(resolveTeacher(pairDto.teacherId()));
    existing.setDayOfWeek(pairDto.dayOfWeek());
    existing.setWeekParity(pairDto.weekParity());
    existing.setPairNumber(pairDto.pairNumber());
    existing.setStartTime(schedule.startTime());
    existing.setEndTime(schedule.endTime());
    existing.setRoom(pairDto.room());
    existing.setGroups(groups);

    Pair updated = pairRepository.save(existing);
    return pairMapper.toDto(updated);
  }

  @Override
  @Transactional
  public void deletePair(UUID id, UUID callerStudentId) {
    Pair existing = pairRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Пара не найдена с ID: " + id));
    checkStudentCanEdit(callerStudentId, existing.getWeekScheduleCycle(), existing.getGroups());

    pairRepository.delete(existing);
  }

  /**
   * Проверка права STUDENT редактировать Pair. {@code callerStudentId == null} означает вызов от
   * ADMIN — без ограничений. Иначе проверяются по порядку два независимых условия, каждое со своим
   * сообщением, чтобы клиент мог их различить: (1) пара принадлежит группе вызывающего студента и
   * только ей, (2) цикл расписания в статусе DRAFT — как только он AGREED, редактировать может
   * только ADMIN, даже свою группу.
   */
  private void checkStudentCanEdit(UUID callerStudentId, WeekScheduleCycle cycle, Set<Group> groups) {
    if (callerStudentId == null) {
      return;
    }

    Student student = studentRepository.findById(callerStudentId)
        .orElseThrow(() -> new ResourceNotFoundException("Студент не найден с ID: " + callerStudentId));

    // Строго "ровно своя группа": студент не может ни писать чужую группу, ни превращать пару в
    // поток на несколько групп (это затронуло бы группу, не давшую на это согласия).
    boolean exactlyOwnGroup = student.getGroup() != null
        && groups.size() == 1
        && groups.stream().allMatch(g -> g.getId().equals(student.getGroup().getId()));
    if (!exactlyOwnGroup) {
      throw new ValidationException("Студент может редактировать пары только своей группы");
    }
    if (cycle.getStatus() != WeekScheduleCycleStatus.DRAFT) {
      throw new ValidationException(
          "Циклическое расписание уже согласовано — редактирование доступно только администратору");
    }
  }

  /**
   * Разрешает день недели и время пары. Если {@code startTime}/{@code endTime} явно заданы в DTO —
   * используются как есть (явный override, например перенос конкретного занятия). Если хотя бы
   * одно не задано — подставляются из справочника звонкового расписания {@link BellScheduleEntry}
   * по университету курса и номеру пары, с fallback на системную запись по умолчанию
   * ({@code university == null}). Справочник — только источник для заполнения, не жёсткая связь:
   * ничего не форсирует совпадение, если время задано явно.
   */
  private ResolvedSchedule resolveSchedule(PairDto pairDto, Course course) {
    if (!WEEKDAYS.contains(pairDto.dayOfWeek())) {
      throw new ValidationException(
          "День недели должен быть с понедельника по пятницу, получено: " + pairDto.dayOfWeek());
    }

    LocalTime startTime = pairDto.startTime();
    LocalTime endTime = pairDto.endTime();
    if (startTime == null || endTime == null) {
      BellScheduleEntry entry = resolveBellScheduleEntry(course, pairDto.pairNumber());
      startTime = entry.getStartTime();
      endTime = entry.getEndTime();
    }

    if (!endTime.isAfter(startTime)) {
      throw new ValidationException("Время окончания пары должно быть позже времени начала");
    }
    return new ResolvedSchedule(startTime, endTime);
  }

  private BellScheduleEntry resolveBellScheduleEntry(Course course, Integer pairNumber) {
    UUID universityId = course.getDepartment() != null
        ? course.getDepartment().getFaculty().getUniversity().getId()
        : null;

    Optional<BellScheduleEntry> entry = universityId != null
        ? bellScheduleEntryRepository.findByUniversityIdAndPairNumber(universityId, pairNumber)
        : Optional.empty();

    return entry
        .or(() -> bellScheduleEntryRepository.findByUniversityIsNullAndPairNumber(pairNumber))
        .orElseThrow(() -> new ValidationException(
            "Время пары не указано и не найдено в справочнике звонкового расписания для номера пары "
                + pairNumber));
  }

  private record ResolvedSchedule(LocalTime startTime, LocalTime endTime) {
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
