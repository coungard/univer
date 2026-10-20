package com.coungard.univer.repository;

import com.coungard.univer.entity.LectureAttendanceId;
import com.coungard.univer.entity.LectureSelfAttendance;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureSelfAttendanceRepository extends JpaRepository<LectureSelfAttendance, LectureAttendanceId> {

  Optional<LectureSelfAttendance> findByStudentIdAndLectureId(UUID studentId, UUID lectureId);

  List<LectureSelfAttendance> findByStudentIdAndLectureIdIn(UUID studentId, Collection<UUID> lectureIds);
}
