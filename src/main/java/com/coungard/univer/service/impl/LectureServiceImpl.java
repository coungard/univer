package com.coungard.univer.service.impl;

import com.coungard.univer.dto.AuditAction;
import com.coungard.univer.dto.AuditEntityType;
import com.coungard.univer.dto.LectureDto;
import com.coungard.univer.dto.WeekParity;
import com.coungard.univer.dto.request.GenerateLectureRequest;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Lecture;
import com.coungard.univer.entity.Pair;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.Teacher;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.mapper.LectureMapper;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.GroupRepository;
import com.coungard.univer.repository.LectureRepository;
import com.coungard.univer.repository.PairRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.TeacherRepository;
import com.coungard.univer.repository.WeekScheduleCycleRepository;
import com.coungard.univer.service.AuditService;
import com.coungard.univer.service.LectureService;
import com.coungard.univer.service.PairLecturesChange;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {

  private final LectureRepository lectureRepository;
  private final CourseRepository courseRepository;
  private final TeacherRepository teacherRepository;
  private final GroupRepository groupRepository;
  private final PairRepository pairRepository;
  private final StudentRepository studentRepository;
  private final WeekScheduleCycleRepository weekScheduleCycleRepository;
  private final LectureMapper lectureMapper;
  private final AuditService auditService;

  @Override
  @Transactional
  public LectureDto createLecture(LectureDto lectureDto) {
    Course course = courseRepository.findById(lectureDto.courseId())
        .orElseThrow(() -> new ResourceNotFoundException("Учебный курс не найден с ID: " + lectureDto.courseId()));

    Lecture lecture = lectureMapper.toEntity(lectureDto);
    lecture.setCourse(course);
    lecture.setTeacher(resolveTeacher(lectureDto.teacherId()));
    lecture.setTeacherName(resolveTeacherName(lectureDto.teacherId(), lectureDto.teacherName()));
    lecture.setGroups(resolveGroups(lectureDto.groupIds()));

    Lecture saved = lectureRepository.save(lecture);
    return lectureMapper.toDto(saved);
  }

  @Override
  @Transactional
  public LectureDto generateFromPair(GenerateLectureRequest request, UUID callerStudentId) {
    Pair pair = pairRepository.findById(request.pairId())
        .orElseThrow(() -> new ResourceNotFoundException("Пара не найдена с ID: " + request.pairId()));
    checkStudentCanGenerate(callerStudentId, pair.getGroups());

    LocalDate date = request.date();
    if (date.getDayOfWeek() != pair.getDayOfWeek()) {
      throw new ValidationException(
          "Дата " + date + " (" + date.getDayOfWeek() + ") не совпадает с днём недели пары: "
              + pair.getDayOfWeek());
    }

    Semester semester = pair.getWeekScheduleCycle().getSemester();
    if (date.isBefore(semester.getStartDate()) || date.isAfter(semester.getEndDate())) {
      throw new ValidationException(
          "Дата " + date + " вне семестра: " + semester.getStartDate() + " — " + semester.getEndDate());
    }

    WeekParity actualParity = WeekParity.of(semester.getStartDate(), date);
    if (pair.getWeekParity() != WeekParity.BOTH && pair.getWeekParity() != actualParity) {
      throw new ValidationException(
          "Дата " + date + " приходится на " + actualParity
              + " неделю семестра, а пара рассчитана на " + pair.getWeekParity());
    }

    if (findGeneratedDates(pair.getId()).contains(date)) {
      throw new ValidationException(
          "Лекция из пары " + pair.getId() + " на дату " + date + " уже сгенерирована");
    }

    Lecture saved = lectureRepository.save(buildLectureFromPair(pair, date));
    return lectureMapper.toDto(saved);
  }

  @Override
  @Transactional
  public List<LectureDto> generateSemesterLectures(UUID weekScheduleCycleId, UUID callerStudentId) {
    if (!weekScheduleCycleRepository.existsById(weekScheduleCycleId)) {
      throw new ResourceNotFoundException("Циклическое расписание не найдено с ID: " + weekScheduleCycleId);
    }
    // Для ADMIN/TEACHER — null, генерируются лекции по всем Pair цикла, как раньше. Для STUDENT —
    // группа вызывающего, дальше отфильтровываем ею список Pair.
    UUID callerGroupId = resolveCallerGroupId(callerStudentId);

    List<LectureDto> generated = new ArrayList<>();
    for (Pair pair : pairRepository.findByWeekScheduleCycleId(weekScheduleCycleId)) {
      if (callerGroupId != null && pair.getGroups().stream().noneMatch(g -> g.getId().equals(callerGroupId))) {
        continue;
      }

      for (Lecture lecture : generateMissingLectures(pair)) {
        generated.add(lectureMapper.toDto(lecture));
      }
    }
    return generated;
  }

  @Override
  @Transactional
  public PairLecturesChange syncLecturesWithPair(UUID pairId) {
    Pair pair = pairRepository.findById(pairId)
        .orElseThrow(() -> new ResourceNotFoundException("Пара не найдена с ID: " + pairId));
    // Пока лекции из пары не генерировались, изменение пары их и не создаёт
    if (!lectureRepository.existsBySourcePairId(pairId)) {
      return PairLecturesChange.NONE;
    }

    List<Lecture> stale = lectureRepository.findBySourcePairIdWithoutAttendance(pairId);
    lectureRepository.deleteAll(stale);
    return new PairLecturesChange(stale.size(), generateMissingLectures(pair).size());
  }

  @Override
  @Transactional
  public PairLecturesChange deleteLecturesOfPair(UUID pairId) {
    List<Lecture> lectures = lectureRepository.findBySourcePairIdWithoutAttendance(pairId);
    lectureRepository.deleteAll(lectures);
    // Остались только лекции с отмеченной посещаемостью — они переживают пару
    lectureRepository.detachFromSourcePair(pairId);
    return new PairLecturesChange(lectures.size(), 0);
  }

  /**
   * Создать лекции пары на все даты её дня недели в границах семестра, подходящие по чётности
   * недели и ещё не занятые лекцией из этой же пары.
   *
   * @return только вновь созданные лекции
   */
  private List<Lecture> generateMissingLectures(Pair pair) {
    Semester semester = pair.getWeekScheduleCycle().getSemester();
    LocalDate semesterStart = semester.getStartDate();
    LocalDate semesterEnd = semester.getEndDate();
    Set<LocalDate> generatedDates = findGeneratedDates(pair.getId());

    List<Lecture> created = new ArrayList<>();
    LocalDate date = semesterStart.with(TemporalAdjusters.nextOrSame(pair.getDayOfWeek()));
    while (!date.isAfter(semesterEnd)) {
      WeekParity actualParity = WeekParity.of(semesterStart, date);
      boolean parityMatches = pair.getWeekParity() == WeekParity.BOTH || pair.getWeekParity() == actualParity;
      if (parityMatches && !generatedDates.contains(date)) {
        created.add(lectureRepository.save(buildLectureFromPair(pair, date)));
      }
      date = date.plusWeeks(1);
    }
    return created;
  }

  /**
   * Даты, на которые из пары уже сгенерирована лекция. Сравнение по дате, а не по точному времени:
   * пара бывает не чаще раза в день, а время лекции с отмеченной посещаемостью могло остаться прежним
   * после изменения пары (см. {@link #syncLecturesWithPair}).
   */
  private Set<LocalDate> findGeneratedDates(UUID pairId) {
    return lectureRepository.findScheduledTimesBySourcePairId(pairId).stream()
        .map(LocalDateTime::toLocalDate)
        .collect(Collectors.toSet());
  }

  private Lecture buildLectureFromPair(Pair pair, LocalDate date) {
    Lecture lecture = new Lecture();
    lecture.setTitle(pair.getCourse().getTitle());
    lecture.setScheduledTime(LocalDateTime.of(date, pair.getStartTime()));
    lecture.setDurationMinutes((int) ChronoUnit.MINUTES.between(pair.getStartTime(), pair.getEndTime()));
    lecture.setCourse(pair.getCourse());
    lecture.setTeacher(pair.getTeacher());
    lecture.setTeacherName(pair.getTeacherName());
    lecture.setRoom(pair.getRoom());
    lecture.setSourcePair(pair);
    lecture.setGroups(new HashSet<>(pair.getGroups()));
    return lecture;
  }

  @Override
  @Transactional(readOnly = true)
  public LectureDto getLectureById(UUID id) {
    Lecture lecture = lectureRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Лекция не найдена с ID: " + id));
    return lectureMapper.toDto(lecture);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<LectureDto> getLectures(Pageable pageable) {
    return lectureRepository.findAll(pageable).map(lectureMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<LectureDto> getLecturesByCourse(UUID courseId, Pageable pageable) {
    return lectureRepository.findByCourseId(courseId, pageable).map(lectureMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<LectureDto> getLecturesByGroup(UUID groupId, Pageable pageable) {
    return lectureRepository.findByGroupsId(groupId, pageable).map(lectureMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<LectureDto> getMyLectures(UUID studentId, Pageable pageable) {
    Student student = studentRepository.findById(studentId)
        .orElseThrow(() -> new ResourceNotFoundException("Студент не найден с ID: " + studentId));

    if (student.getGroup() == null) {
      return Page.empty(pageable);
    }

    return lectureRepository.findByGroupsId(student.getGroup().getId(), pageable)
        .map(lectureMapper::toDto);
  }

  @Override
  @Transactional
  public LectureDto updateLecture(UUID id, LectureDto lectureDto) {
    Lecture existing = lectureRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Лекция не найдена с ID: " + id));

    Course course = courseRepository.findById(lectureDto.courseId())
        .orElseThrow(() -> new ResourceNotFoundException("Учебный курс не найден с ID: " + lectureDto.courseId()));

    existing.setTitle(lectureDto.title());
    existing.setContent(lectureDto.content());
    existing.setScheduledTime(lectureDto.scheduledTime());
    existing.setDurationMinutes(lectureDto.durationMinutes() != null ? lectureDto.durationMinutes() : 90);
    existing.setCourse(course);
    existing.setTeacher(resolveTeacher(lectureDto.teacherId()));
    existing.setTeacherName(resolveTeacherName(lectureDto.teacherId(), lectureDto.teacherName()));
    existing.setRoom(lectureDto.room());
    existing.setGroups(resolveGroups(lectureDto.groupIds()));

    Lecture updated = lectureRepository.save(existing);
    return lectureMapper.toDto(updated);
  }

  @Override
  @Transactional
  public void deleteLecture(UUID id) {
    if (!lectureRepository.existsById(id)) {
      throw new ResourceNotFoundException("Лекция не найдена с ID: " + id);
    }
    lectureRepository.deleteById(id);
    auditService.record(AuditAction.DELETE, AuditEntityType.LECTURE, id);
  }

  /**
   * Проверка права STUDENT сгенерировать Lecture из конкретного Pair. {@code callerStudentId ==
   * null} означает вызов от ADMIN/TEACHER — без ограничений.
   */
  private void checkStudentCanGenerate(UUID callerStudentId, Set<Group> pairGroups) {
    if (callerStudentId == null) {
      return;
    }

    UUID callerGroupId = resolveCallerGroupId(callerStudentId);
    if (pairGroups.stream().noneMatch(g -> g.getId().equals(callerGroupId))) {
      throw new ValidationException("Студент может генерировать лекции только для своей группы");
    }
  }

  /**
   * {@code null}, если {@code callerStudentId == null} (вызов от ADMIN/TEACHER — без ограничений).
   * Иначе — ID группы вызывающего студента; студент без группы генерировать лекции не может.
   */
  private UUID resolveCallerGroupId(UUID callerStudentId) {
    if (callerStudentId == null) {
      return null;
    }

    Student student = studentRepository.findById(callerStudentId)
        .orElseThrow(() -> new ResourceNotFoundException("Студент не найден с ID: " + callerStudentId));
    if (student.getGroup() == null) {
      throw new ValidationException("Студент не привязан к группе — генерация расписания недоступна");
    }
    return student.getGroup().getId();
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
