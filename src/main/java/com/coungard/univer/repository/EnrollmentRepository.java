package com.coungard.univer.repository;

import com.coungard.univer.dto.EnrollmentStatus;
import com.coungard.univer.entity.Enrollment;
import com.coungard.univer.entity.EnrollmentId;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentId> {

  Page<Enrollment> findByStudentId(UUID studentId, Pageable pageable);

  Page<Enrollment> findByCourseId(UUID courseId, Pageable pageable);

  Optional<Enrollment> findByStudentIdAndCourseId(UUID studentId, UUID courseId);

  boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId);

  boolean existsByStudentIdAndCourseIdAndStatus(UUID studentId, UUID courseId, EnrollmentStatus status);
}
