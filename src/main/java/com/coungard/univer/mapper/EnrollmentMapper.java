package com.coungard.univer.mapper;

import com.coungard.univer.dto.EnrollmentDto;
import com.coungard.univer.dto.EnrollmentStatus;
import com.coungard.univer.entity.Enrollment;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentMapper {

  public EnrollmentDto toDto(Enrollment enrollment) {
    if (enrollment == null) {
      return null;
    }
    return EnrollmentDto.builder()
        .studentId(enrollment.getStudent().getId())
        .courseId(enrollment.getCourse().getId())
        .enrolledAt(enrollment.getEnrolledAt())
        .status(enrollment.getStatus())
        .build();
  }

  public Enrollment toEntity(EnrollmentDto dto) {
    if (dto == null) {
      return null;
    }
    Enrollment enrollment = new Enrollment();
    enrollment.setEnrolledAt(dto.enrolledAt() != null ? dto.enrolledAt() : LocalDateTime.now());
    enrollment.setStatus(dto.status() != null ? dto.status() : EnrollmentStatus.ACTIVE);
    return enrollment;
  }
}
