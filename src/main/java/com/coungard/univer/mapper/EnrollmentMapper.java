package com.coungard.univer.mapper;

import com.coungard.univer.dto.EnrollmentDto;
import com.coungard.univer.dto.EnrollmentStatus;
import com.coungard.univer.entity.Enrollment;
import com.coungard.univer.entity.Person;
import com.coungard.univer.service.AvatarUrls;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnrollmentMapper {

  private final AvatarUrls avatarUrls;

  public EnrollmentDto toDto(Enrollment enrollment) {
    if (enrollment == null) {
      return null;
    }
    Person student = enrollment.getStudent().getPerson();
    return EnrollmentDto.builder()
        .studentId(enrollment.getStudent().getId())
        .courseId(enrollment.getCourse().getId())
        .enrolledAt(enrollment.getEnrolledAt())
        .status(enrollment.getStatus())
        .studentFullname(student.getFullname())
        .studentAvatarUrl(avatarUrls.full(student.getAvatarFile()))
        .studentAvatarThumbnailUrl(avatarUrls.thumbnail(student.getAvatarFile()))
        .createdAt(enrollment.getCreatedAt())
        .updatedAt(enrollment.getUpdatedAt())
        .createdBy(enrollment.getCreatedBy())
        .updatedBy(enrollment.getUpdatedBy())
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
