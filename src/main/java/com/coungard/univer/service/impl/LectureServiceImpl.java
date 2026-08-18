package com.coungard.univer.service.impl;

import com.coungard.univer.dto.LectureDto;
import com.coungard.univer.dto.WeekParity;
import com.coungard.univer.dto.request.GenerateLectureRequest;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Lecture;
import com.coungard.univer.entity.Pair;
import com.coungard.univer.entity.Teacher;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.mapper.LectureMapper;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.GroupRepository;
import com.coungard.univer.repository.LectureRepository;
import com.coungard.univer.repository.PairRepository;
import com.coungard.univer.repository.TeacherRepository;
import com.coungard.univer.service.LectureService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
public class LectureServiceImpl implements LectureService {

  private final LectureRepository lectureRepository;
  private final CourseRepository courseRepository;
  private final TeacherRepository teacherRepository;
  private final GroupRepository groupRepository;
  private final PairRepository pairRepository;
  private final LectureMapper lectureMapper;

  @Override
  @Transactional
  public LectureDto createLecture(LectureDto lectureDto) {
    Course course = courseRepository.findById(lectureDto.courseId())
        .orElseThrow(() -> new ResourceNotFoundException("Учебный курс не найден с ID: " + lectureDto.courseId()));

    Lecture lecture = lectureMapper.toEntity(lectureDto);
    lecture.setCourse(course);
    lecture.setTeacher(resolveTeacher(lectureDto.teacherId()));
    lecture.setGroups(resolveGroups(lectureDto.groupIds()));

    Lecture saved = lectureRepository.save(lecture);
    return lectureMapper.toDto(saved);
  }

  @Override
  @Transactional
  public LectureDto generateFromPair(GenerateLectureRequest request) {
    Pair pair = pairRepository.findById(request.pairId())
        .orElseThrow(() -> new ResourceNotFoundException("Пара не найдена с ID: " + request.pairId()));

    LocalDate date = request.date();
    if (date.getDayOfWeek() != pair.getDayOfWeek()) {
      throw new ValidationException(
          "Дата " + date + " (" + date.getDayOfWeek() + ") не совпадает с днём недели пары: "
              + pair.getDayOfWeek());
    }

    LocalDate semesterStart = pair.getWeekScheduleCycle().getSemester().getStartDate();
    WeekParity actualParity = computeWeekParity(semesterStart, date);
    if (pair.getWeekParity() != WeekParity.BOTH && pair.getWeekParity() != actualParity) {
      throw new ValidationException(
          "Дата " + date + " приходится на " + actualParity
              + " неделю семестра, а пара рассчитана на " + pair.getWeekParity());
    }

    LocalDateTime scheduledTime = LocalDateTime.of(date, pair.getStartTime());
    if (lectureRepository.existsBySourcePairIdAndScheduledTime(pair.getId(), scheduledTime)) {
      throw new ValidationException(
          "Лекция из пары " + pair.getId() + " на дату " + date + " уже сгенерирована");
    }

    Lecture lecture = new Lecture();
    lecture.setTitle(pair.getCourse().getTitle());
    lecture.setScheduledTime(scheduledTime);
    lecture.setDurationMinutes((int) ChronoUnit.MINUTES.between(pair.getStartTime(), pair.getEndTime()));
    lecture.setCourse(pair.getCourse());
    lecture.setTeacher(pair.getTeacher());
    lecture.setSourcePair(pair);
    lecture.setGroups(new HashSet<>(pair.getGroups()));

    Lecture saved = lectureRepository.save(lecture);
    return lectureMapper.toDto(saved);
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
  }

  private WeekParity computeWeekParity(LocalDate semesterStart, LocalDate date) {
    long daysBetween = ChronoUnit.DAYS.between(semesterStart, date);
    long weekNumber = Math.floorDiv(daysBetween, 7) + 1;
    return weekNumber % 2 == 1 ? WeekParity.ODD : WeekParity.EVEN;
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
