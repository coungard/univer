package com.coungard.univer.repository;

import com.coungard.univer.entity.LectureAttendance;
import com.coungard.univer.entity.LectureAttendanceId;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureAttendanceRepository extends JpaRepository<LectureAttendance, LectureAttendanceId> {

  Page<LectureAttendance> findByStudentId(UUID studentId, Pageable pageable);

  Page<LectureAttendance> findByLectureId(UUID lectureId, Pageable pageable);

  Optional<LectureAttendance> findByStudentIdAndLectureId(UUID studentId, UUID lectureId);

  long countByLectureId(UUID lectureId);

  long countByLectureIdAndAttendedTrue(UUID lectureId);

  long countByStudentIdAndLecture_CourseId(UUID studentId, UUID courseId);

  long countByStudentIdAndLecture_CourseIdAndAttendedTrue(UUID studentId, UUID courseId);
}
