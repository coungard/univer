package com.coungard.univer.service.impl;

import com.coungard.univer.dto.AuditAction;
import com.coungard.univer.dto.AuditEntityType;
import com.coungard.univer.dto.PairDto;
import com.coungard.univer.dto.WeekParity;
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
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.GroupRepository;
import com.coungard.univer.repository.PairRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.TeacherRepository;
import com.coungard.univer.repository.WeekScheduleCycleRepository;
import com.coungard.univer.service.AuditService;
import com.coungard.univer.service.LectureService;
import com.coungard.univer.service.PairLecturesChange;
import com.coungard.univer.service.PairService;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

  private final PairRepository pairRepository;
  private final WeekScheduleCycleRepository weekScheduleCycleRepository;
  private final CourseRepository courseRepository;
  private final TeacherRepository teacherRepository;
  private final GroupRepository groupRepository;
  private final FacultyRepository facultyRepository;
  private final BellScheduleEntryRepository bellScheduleEntryRepository;
  private final StudentRepository studentRepository;
  private final LectureService lectureService;
  private final PairMapper pairMapper;
  private final AuditService auditService;

  @Override
  @Transactional
  public PairDto createPair(PairDto pairDto, UUID callerStudentId) {
    WeekScheduleCycle cycle = weekScheduleCycleRepository.findById(pairDto.weekScheduleCycleId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Циклическое расписание не найдено с ID: " + pairDto.weekScheduleCycleId()));

    Course course = courseRepository.findById(pairDto.courseId())
        .orElseThrow(() -> new ResourceNotFoundException("Учебный курс не найден с ID: " + pairDto.courseId()));

    Set<Group> groups = resolveGroups(pairDto.groupIds());
    ResolvedSchedule schedule = resolveSchedule(pairDto, groups);
    checkStudentCanEdit(callerStudentId, cycle, groups);
    checkForConflicts(cycle, pairDto, schedule, null);

    Pair pair = pairMapper.toEntity(pairDto);
    pair.setWeekScheduleCycle(cycle);
    pair.setCourse(course);
    pair.setTeacher(resolveTeacher(pairDto.teacherId()));
    pair.setTeacherName(resolveTeacherName(pairDto.teacherId(), pairDto.teacherName()));
    pair.setGroups(groups);
    pair.setStartTime(schedule.startTime());
    pair.setEndTime(schedule.endTime());

    Pair saved = pairRepository.save(pair);
    auditService.record(AuditAction.CREATE, AuditEntityType.PAIR, saved.getId(), snapshot(saved));
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
    Map<String, Object> before = snapshot(existing);

    WeekScheduleCycle cycle = weekScheduleCycleRepository.findById(pairDto.weekScheduleCycleId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Циклическое расписание не найдено с ID: " + pairDto.weekScheduleCycleId()));

    Course course = courseRepository.findById(pairDto.courseId())
        .orElseThrow(() -> new ResourceNotFoundException("Учебный курс не найден с ID: " + pairDto.courseId()));

    Set<Group> groups = resolveGroups(pairDto.groupIds());
    ResolvedSchedule schedule = resolveSchedule(pairDto, groups);
    // ...и новое (нельзя перенести пару в чужой цикл/группу или в уже согласованный цикл).
    checkStudentCanEdit(callerStudentId, cycle, groups);
    checkForConflicts(cycle, pairDto, schedule, id);

    existing.setWeekScheduleCycle(cycle);
    existing.setCourse(course);
    existing.setTeacher(resolveTeacher(pairDto.teacherId()));
    existing.setTeacherName(resolveTeacherName(pairDto.teacherId(), pairDto.teacherName()));
    existing.setDayOfWeek(pairDto.dayOfWeek());
    existing.setWeekParity(pairDto.weekParity());
    existing.setPairNumber(pairDto.pairNumber());
    existing.setStartTime(schedule.startTime());
    existing.setEndTime(schedule.endTime());
    existing.setRoom(pairDto.room());
    existing.setGroups(groups);

    Pair updated = pairRepository.save(existing);
    // Лекции, уже сгенерированные из пары, иначе остались бы на прежнем дне и времени (issue #99)
    PairLecturesChange lectures = lectureService.syncLecturesWithPair(updated.getId());
    auditService.record(AuditAction.UPDATE, AuditEntityType.PAIR, id, AuditService.details(
        "before", before,
        "after", snapshot(updated),
        "lecturesDeleted", lectures.deleted(),
        "lecturesCreated", lectures.created()));
    return pairMapper.toDto(updated);
  }

  @Override
  @Transactional
  public void deletePair(UUID id, UUID callerStudentId) {
    Pair existing = pairRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Пара не найдена с ID: " + id));
    checkStudentCanEdit(callerStudentId, existing.getWeekScheduleCycle(), existing.getGroups());
    Map<String, Object> before = snapshot(existing);

    // Иначе в расписании группы остались бы занятия по паре, которой уже нет (issue #99)
    PairLecturesChange lectures = lectureService.deleteLecturesOfPair(id);
    pairRepository.delete(existing);
    auditService.record(AuditAction.DELETE, AuditEntityType.PAIR, id, AuditService.details(
        "before", before,
        "lecturesDeleted", lectures.deleted()));
  }

  /**
   * Состояние пары для журнала аудита: всё, что влияет на расписание, без имени преподавателя
   * текстом — это персональные данные.
   */
  private Map<String, Object> snapshot(Pair pair) {
    return AuditService.details(
        "weekScheduleCycleId", pair.getWeekScheduleCycle().getId(),
        "courseId", pair.getCourse().getId(),
        "teacherId", pair.getTeacher() != null ? pair.getTeacher().getId() : null,
        "groupIds", pair.getGroups().stream().map(Group::getId).map(UUID::toString).sorted().toList(),
        "dayOfWeek", pair.getDayOfWeek(),
        "weekParity", pair.getWeekParity(),
        "pairNumber", pair.getPairNumber(),
        "startTime", String.valueOf(pair.getStartTime()),
        "endTime", String.valueOf(pair.getEndTime()),
        "room", pair.getRoom());
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
   * Проверка конфликтов преподаватель/аудитория между Pair одного цикла (issue #62): два Pair
   * конфликтуют, если совпадает день недели, пересекается чётность недели (с учётом BOTH),
   * пересекаются фактические интервалы времени, и при этом совпадает преподаватель или аудитория.
   * Действует для всех ролей (не только STUDENT) — сегодняшний ADMIN тоже может создать конфликт по
   * недосмотру, отдельного пути в обход валидации быть не должно. Защита от гонки при параллельных
   * сохранениях сознательно не реализована — см. решение в issue #62 (низкий риск для MVP).
   *
   * @param excludePairId при update — ID редактируемой пары, чтобы не конфликтовать самой с собой;
   *                      {@code null} при create
   */
  private void checkForConflicts(
      WeekScheduleCycle cycle, PairDto pairDto, ResolvedSchedule schedule, UUID excludePairId) {

    UUID teacherId = pairDto.teacherId();
    String room = pairDto.room();

    List<Pair> pairsInCycle = pairRepository.findByWeekScheduleCycleId(cycle.getId());
    for (Pair other : pairsInCycle) {
      if (excludePairId != null && other.getId().equals(excludePairId)) {
        continue;
      }
      if (other.getDayOfWeek() != pairDto.dayOfWeek()
          || !weekParityOverlaps(other.getWeekParity(), pairDto.weekParity())
          || !timeOverlaps(other.getStartTime(), other.getEndTime(), schedule.startTime(), schedule.endTime())) {
        continue;
      }

      boolean teacherConflict = teacherId != null
          && other.getTeacher() != null
          && teacherId.equals(other.getTeacher().getId());
      if (teacherConflict) {
        throw new ValidationException(
            "Конфликт расписания: преподаватель уже занят в это время парой " + other.getId()
                + " (" + other.getDayOfWeek() + " " + other.getStartTime() + "-" + other.getEndTime() + ")");
      }

      boolean roomConflict = room != null && !room.isBlank() && room.equalsIgnoreCase(other.getRoom());
      if (roomConflict) {
        throw new ValidationException(
            "Конфликт расписания: аудитория \"" + other.getRoom() + "\" уже занята в это время парой "
                + other.getId() + " (" + other.getDayOfWeek() + " " + other.getStartTime() + "-"
                + other.getEndTime() + ")");
      }
    }
  }

  private boolean weekParityOverlaps(WeekParity a, WeekParity b) {
    return a == WeekParity.BOTH || b == WeekParity.BOTH || a == b;
  }

  private boolean timeOverlaps(LocalTime startA, LocalTime endA, LocalTime startB, LocalTime endB) {
    return startA.isBefore(endB) && startB.isBefore(endA);
  }

  /**
   * Разрешает день недели и время пары. Если {@code startTime}/{@code endTime} явно заданы в DTO —
   * используются как есть (явный override, например перенос конкретного занятия). Если хотя бы
   * одно не задано — подставляются из справочника звонкового расписания {@link BellScheduleEntry}
   * по университету группы пары и номеру пары, с fallback на системную запись по умолчанию
   * ({@code university == null}). Справочник — только источник для заполнения, не жёсткая связь:
   * ничего не форсирует совпадение, если время задано явно.
   */
  private ResolvedSchedule resolveSchedule(PairDto pairDto, Set<Group> groups) {
    LocalTime startTime = pairDto.startTime();
    LocalTime endTime = pairDto.endTime();
    if (startTime == null || endTime == null) {
      BellScheduleEntry entry = resolveBellScheduleEntry(groups, pairDto.pairNumber());
      startTime = entry.getStartTime();
      endTime = entry.getEndTime();
    }

    if (!endTime.isAfter(startTime)) {
      throw new ValidationException("Время окончания пары должно быть позже времени начала");
    }
    return new ResolvedSchedule(startTime, endTime);
  }

  private BellScheduleEntry resolveBellScheduleEntry(Set<Group> groups, Integer pairNumber) {
    UUID universityId = resolveUniversityId(groups);

    Optional<BellScheduleEntry> entry = universityId != null
        ? bellScheduleEntryRepository.findByUniversityIdAndPairNumber(universityId, pairNumber)
        : Optional.empty();

    return entry
        .or(() -> bellScheduleEntryRepository.findByUniversityIsNullAndPairNumber(pairNumber))
        .orElseThrow(() -> new ValidationException(
            "Время пары не указано и не найдено в справочнике звонкового расписания для номера пары "
                + pairNumber));
  }

  /**
   * Университет пары — по её группе (группа → семестр → учебный год → факультет), а не по кафедре
   * предмета: у предмета кафедры может не быть (issue #98). Поток идёт группам одного университета,
   * поэтому достаточно любой группы; берётся с наименьшим ID, чтобы результат не зависел от порядка
   * обхода множества.
   */
  private UUID resolveUniversityId(Set<Group> groups) {
    return groups.stream()
        .min(Comparator.comparing(Group::getId))
        .flatMap(group -> facultyRepository.findById(group.getSemester().getStudyYear().getFacultyId()))
        .map(faculty -> faculty.getUniversity().getId())
        .orElse(null);
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

  /**
   * Имя преподавателя текстом хранится, только если он не выбран из зарегистрированных: при заданном
   * {@code teacherId} имя берётся из {@link Teacher} (issue #104). Пустая строка — то же, что не задано.
   */
  private String resolveTeacherName(UUID teacherId, String teacherName) {
    if (teacherId != null || teacherName == null || teacherName.isBlank()) {
      return null;
    }
    return teacherName.strip();
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
