package com.coungard.univer.service.impl;

import com.coungard.univer.dto.AttendanceStatsDto;
import com.coungard.univer.dto.EnrollmentStatus;
import com.coungard.univer.dto.LectureAttendanceDto;
import com.coungard.univer.entity.Lecture;
import com.coungard.univer.entity.LectureAttendance;
import com.coungard.univer.entity.Student;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.mapper.LectureAttendanceMapper;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.EnrollmentRepository;
import com.coungard.univer.repository.LectureAttendanceRepository;
import com.coungard.univer.repository.LectureRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.service.LectureAttendanceService;
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
  private final StudentRepository studentRepository;
  private final LectureRepository lectureRepository;
  private final CourseRepository courseRepository;
  private final EnrollmentRepository enrollmentRepository;
  private final LectureAttendanceMapper attendanceMapper;

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

    LectureAttendance attendance = attendanceRepository
        .findByStudentIdAndLectureId(student.getId(), lecture.getId())
        .orElseGet(() -> {
          LectureAttendance created = new LectureAttendance();
          created.setStudentId(student.getId());
          created.setLectureId(lecture.getId());
          created.setStudent(student);
          created.setLecture(lecture);
          return created;
        });
    attendance.setAttended(attendanceDto.attended());

    LectureAttendance saved = attendanceRepository.save(attendance);
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

  private AttendanceStatsDto buildStats(long totalMarked, long attendedCount) {
    double rate = totalMarked == 0 ? 0.0 : (double) attendedCount / totalMarked;
    return AttendanceStatsDto.builder()
        .totalMarked(totalMarked)
        .attendedCount(attendedCount)
        .attendanceRate(rate)
        .build();
  }
}
