package com.coungard.univer.service.impl;

import com.coungard.univer.dto.EnrollmentDto;
import com.coungard.univer.dto.EnrollmentStatus;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Enrollment;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Student;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.mapper.EnrollmentMapper;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.EnrollmentRepository;
import com.coungard.univer.repository.GroupRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.service.EnrollmentService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

  private final EnrollmentRepository enrollmentRepository;
  private final StudentRepository studentRepository;
  private final CourseRepository courseRepository;
  private final GroupRepository groupRepository;
  private final EnrollmentMapper enrollmentMapper;

  @Override
  @Transactional
  public EnrollmentDto enroll(EnrollmentDto enrollmentDto) {
    Student student = studentRepository.findById(enrollmentDto.studentId())
        .orElseThrow(() -> new ResourceNotFoundException("Студент не найден с ID: " + enrollmentDto.studentId()));
    Course course = courseRepository.findById(enrollmentDto.courseId())
        .orElseThrow(() -> new ResourceNotFoundException("Учебный курс не найден с ID: " + enrollmentDto.courseId()));

    if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
      throw new ValidationException(
          "Студент " + student.getId() + " уже зачислен на курс " + course.getId());
    }

    Enrollment enrollment = enrollmentMapper.toEntity(enrollmentDto);
    enrollment.setStudentId(student.getId());
    enrollment.setCourseId(course.getId());
    enrollment.setStudent(student);
    enrollment.setCourse(course);

    Enrollment saved = enrollmentRepository.save(enrollment);
    return enrollmentMapper.toDto(saved);
  }

  @Override
  @Transactional
  public List<EnrollmentDto> enrollGroup(UUID groupId, UUID courseId) {
    Group group = groupRepository.findById(groupId)
        .orElseThrow(() -> new ResourceNotFoundException("Группа не найдена с ID: " + groupId));
    Course course = courseRepository.findById(courseId)
        .orElseThrow(() -> new ResourceNotFoundException("Учебный курс не найден с ID: " + courseId));

    List<Student> students = studentRepository.findByGroupId(group.getId());

    return students.stream()
        .filter(student -> !enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId()))
        .map(student -> {
          Enrollment enrollment = new Enrollment();
          enrollment.setStudentId(student.getId());
          enrollment.setCourseId(course.getId());
          enrollment.setStudent(student);
          enrollment.setCourse(course);
          enrollment.setEnrolledAt(LocalDateTime.now());
          enrollment.setStatus(EnrollmentStatus.ACTIVE);
          return enrollmentMapper.toDto(enrollmentRepository.save(enrollment));
        })
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public EnrollmentDto getEnrollment(UUID studentId, UUID courseId) {
    Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Зачисление не найдено: студент " + studentId + ", курс " + courseId));
    return enrollmentMapper.toDto(enrollment);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<EnrollmentDto> getEnrollmentsByStudent(UUID studentId, Pageable pageable) {
    return enrollmentRepository.findByStudentId(studentId, pageable).map(enrollmentMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<EnrollmentDto> getEnrollmentsByCourse(UUID courseId, Pageable pageable) {
    return enrollmentRepository.findByCourseId(courseId, pageable).map(enrollmentMapper::toDto);
  }

  @Override
  @Transactional
  public EnrollmentDto updateStatus(UUID studentId, UUID courseId, EnrollmentDto enrollmentDto) {
    Enrollment existing = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Зачисление не найдено: студент " + studentId + ", курс " + courseId));

    if (enrollmentDto.status() != null) {
      existing.setStatus(enrollmentDto.status());
    }

    Enrollment updated = enrollmentRepository.save(existing);
    return enrollmentMapper.toDto(updated);
  }

  @Override
  @Transactional
  public void unenroll(UUID studentId, UUID courseId) {
    Enrollment existing = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Зачисление не найдено: студент " + studentId + ", курс " + courseId));
    enrollmentRepository.delete(existing);
  }
}
