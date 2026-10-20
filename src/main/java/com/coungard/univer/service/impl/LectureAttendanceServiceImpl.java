package com.coungard.univer.service.impl;

import com.coungard.univer.dto.AttendanceSource;
import com.coungard.univer.dto.AttendanceStatsDto;
import com.coungard.univer.dto.AttendanceSummaryDto;
import com.coungard.univer.dto.AuditAction;
import com.coungard.univer.dto.AuditEntityType;
import com.coungard.univer.dto.EnrollmentStatus;
import com.coungard.univer.dto.LectureAttendanceDto;
import com.coungard.univer.dto.MyAttendanceDto;
import com.coungard.univer.entity.Lecture;
import com.coungard.univer.entity.LectureAttendance;
import com.coungard.univer.entity.LectureSelfAttendance;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.Student;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.mapper.LectureAttendanceMapper;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.EnrollmentRepository;
import com.coungard.univer.repository.LectureAttendanceRepository;
import com.coungard.univer.repository.LectureRepository;
import com.coungard.univer.repository.LectureSelfAttendanceRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.service.AuditService;
import com.coungard.univer.service.LectureAttendanceService;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LectureAttendanceServiceImpl implements LectureAttendanceService {

  private final LectureAttendanceRepository attendanceRepository;
  private final LectureSelfAttendanceRepository selfAttendanceRepository;
  private final StudentRepository studentRepository;
  private final LectureRepository lectureRepository;
  private final CourseRepository courseRepository;
  private final EnrollmentRepository enrollmentRepository;
  private final LectureAttendanceMapper attendanceMapper;
  private final AuditService auditService;

  @Override
  @Transactional
  public LectureAttendanceDto markAttendance(LectureAttendanceDto attendanceDto) {
    Student student = studentRepository.findById(attendanceDto.studentId())
        .orElseThrow(() -> new ResourceNotFoundException("Студент не найден с ID: " + attendanceDto.studentId()));
    Lecture lecture = lectureRepository.findById(attendanceDto.lectureId())
        .orElseThrow(() -> new ResourceNotFoundException("Лекция не найдена с ID: " + attendanceDto.lectureId()));

    UUID courseId = lecture.getCourse().getId();
    if (!enrollmentRepository.existsByStudentIdAndCourseIdAndStatus(student.getId(), courseId, EnrollmentStatus.ACTIVE)) {
      throw new ValidationException(
          "Студент " + student.getId() + " не зачислен (ACTIVE) на курс " + courseId
              + " этой лекции — отметить посещение нельзя");
    }

    Optional<LectureAttendance> existing = attendanceRepository
        .findByStudentIdAndLectureId(student.getId(), lecture.getId());
    // Прежняя отметка — в журнал: видно, кто и когда исправил «был» на «не был»
    Boolean previous = existing.map(LectureAttendance::isAttended).orElse(null);
    LectureAttendance attendance = existing.orElseGet(() -> {
      LectureAttendance created = new LectureAttendance();
      created.setStudentId(student.getId());
      created.setLectureId(lecture.getId());
      created.setStudent(student);
      created.setLecture(lecture);
      return created;
    });
    attendance.setAttended(attendanceDto.attended());

    LectureAttendance saved = attendanceRepository.saveAndFlush(attendance);
    auditService.record(AuditAction.MARK, AuditEntityType.LECTURE_ATTENDANCE, lecture.getId(),
        AuditService.details(
            "studentId", student.getId(),
            "attended", attendanceDto.attended(),
            "previous", previous));
    return attendanceMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<LectureAttendanceDto> getAttendanceByStudent(UUID studentId, Pageable pageable) {
    return attendanceRepository.findByStudentId(studentId, pageable).map(attendanceMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<LectureAttendanceDto> getAttendanceByLecture(UUID lectureId, Pageable pageable) {
    return attendanceRepository.findByLectureId(lectureId, pageable).map(attendanceMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public AttendanceStatsDto getLectureStats(UUID lectureId) {
    if (!lectureRepository.existsById(lectureId)) {
      throw new ResourceNotFoundException("Лекция не найдена с ID: " + lectureId);
    }

    long totalMarked = attendanceRepository.countByLectureId(lectureId);
    long attendedCount = attendanceRepository.countByLectureIdAndAttendedTrue(lectureId);
    return buildStats(totalMarked, attendedCount);
  }

  @Override
  @Transactional(readOnly = true)
  public AttendanceStatsDto getStudentCourseStats(UUID studentId, UUID courseId) {
    if (!studentRepository.existsById(studentId)) {
      throw new ResourceNotFoundException("Студент не найден с ID: " + studentId);
    }
    if (!courseRepository.existsById(courseId)) {
      throw new ResourceNotFoundException("Учебный курс не найден с ID: " + courseId);
    }

    long totalMarked = attendanceRepository.countByStudentIdAndLecture_CourseId(studentId, courseId);
    long attendedCount = attendanceRepository.countByStudentIdAndLecture_CourseIdAndAttendedTrue(studentId, courseId);
    return buildStats(totalMarked, attendedCount);
  }

  @Override
  @Transactional
  public MyAttendanceDto markMyAttendance(UUID studentId, UUID lectureId, boolean attended) {
    Student student = studentRepository.findById(studentId)
        .orElseThrow(() -> new ResourceNotFoundException("Студент не найден с ID: " + studentId));
    Lecture lecture = lectureRepository.findById(lectureId)
        .orElseThrow(() -> new ResourceNotFoundException("Лекция не найдена с ID: " + lectureId));

    if (student.getGroup() == null || !lectureRepository.existsByIdAndGroupsId(lectureId, student.getGroup().getId())) {
      throw new ValidationException("Студент может отмечать только лекции своей группы");
    }
    if (lecture.getScheduledTime().isAfter(LocalDateTime.now())) {
      throw new ValidationException("Отметить посещение можно только после начала лекции");
    }

    LectureSelfAttendance mark = selfAttendanceRepository.findByStudentIdAndLectureId(studentId, lectureId)
        .orElseGet(() -> {
          LectureSelfAttendance created = new LectureSelfAttendance();
          created.setStudentId(studentId);
          created.setLectureId(lectureId);
          return created;
        });
    mark.setAttended(attended);
    selfAttendanceRepository.save(mark);

    return resolveMarks(studentId, List.of(lectureId)).get(lectureId);
  }

  @Override
  @Transactional
  public void unmarkMyAttendance(UUID studentId, UUID lectureId) {
    selfAttendanceRepository.findByStudentIdAndLectureId(studentId, lectureId)
        .ifPresent(selfAttendanceRepository::delete);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<MyAttendanceDto> getMyAttendance(UUID studentId, Pageable pageable) {
    Page<Lecture> lectures = lectureRepository.findMarkedByStudentId(studentId, pageable);
    Map<UUID, MyAttendanceDto> marks = resolveMarks(studentId,
        lectures.stream().map(Lecture::getId).toList());
    return lectures.map(lecture -> marks.get(lecture.getId()));
  }

  @Override
  @Transactional(readOnly = true)
  public AttendanceSummaryDto getMyCourseSummary(UUID studentId, UUID courseId) {
    Student student = studentRepository.findById(studentId)
        .orElseThrow(() -> new ResourceNotFoundException("Студент не найден с ID: " + studentId));
    if (!courseRepository.existsById(courseId)) {
      throw new ResourceNotFoundException("Учебный курс не найден с ID: " + courseId);
    }

    List<UUID> pastLectureIds = List.of();
    if (student.getGroup() != null) {
      // Группа живёт в одном семестре — это и есть текущий семестр студента
      Semester semester = student.getGroup().getSemester();
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime semesterEnd = semester.getEndDate().atTime(LocalTime.MAX);
      pastLectureIds = lectureRepository.findIdsByGroupAndCourseBetween(
          student.getGroup().getId(), courseId,
          semester.getStartDate().atStartOfDay(), now.isBefore(semesterEnd) ? now : semesterEnd);
    }

    Collection<MyAttendanceDto> marks = resolveMarks(studentId, pastLectureIds).values();
    long attended = marks.stream().filter(MyAttendanceDto::attended).count();
    long missed = marks.size() - attended;
    return AttendanceSummaryDto.builder()
        .pastLectures(pastLectureIds.size())
        .attended(attended)
        .missed(missed)
        .unmarked(pastLectureIds.size() - marks.size())
        .build();
  }

  /** Итоговые отметки студента по лекциям: отметка преподавателя перекрывает личную. */
  private Map<UUID, MyAttendanceDto> resolveMarks(UUID studentId, Collection<UUID> lectureIds) {
    Map<UUID, MyAttendanceDto> marks = new HashMap<>();
    if (lectureIds.isEmpty()) {
      return marks;
    }
    for (LectureSelfAttendance mark : selfAttendanceRepository.findByStudentIdAndLectureIdIn(studentId, lectureIds)) {
      marks.put(mark.getLectureId(), myAttendance(mark.getLectureId(), mark.isAttended(), AttendanceSource.STUDENT));
    }
    for (LectureAttendance mark : attendanceRepository.findByStudentIdAndLectureIdIn(studentId, lectureIds)) {
      marks.put(mark.getLectureId(), myAttendance(mark.getLectureId(), mark.isAttended(), AttendanceSource.TEACHER));
    }
    return marks;
  }

  private static MyAttendanceDto myAttendance(UUID lectureId, boolean attended, AttendanceSource source) {
    return MyAttendanceDto.builder()
        .lectureId(lectureId)
        .attended(attended)
        .source(source)
        .build();
  }

  private AttendanceStatsDto buildStats(long totalMarked, long attendedCount) {
    double rate = totalMarked == 0 ? 0.0 : (double) attendedCount / totalMarked;
    return AttendanceStatsDto.builder()
        .totalMarked(totalMarked)
        .attendedCount(attendedCount)
        .attendanceRate(rate)
        .build();
  }
}
