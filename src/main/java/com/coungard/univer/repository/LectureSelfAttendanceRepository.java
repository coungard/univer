package com.coungard.univer.repository;

import com.coungard.univer.entity.LectureAttendanceId;
import com.coungard.univer.entity.LectureSelfAttendance;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LectureSelfAttendanceRepository extends JpaRepository<LectureSelfAttendance, LectureAttendanceId> {

  Optional<LectureSelfAttendance> findByStudentIdAndLectureId(UUID studentId, UUID lectureId);

  List<LectureSelfAttendance> findByStudentIdAndLectureIdIn(UUID studentId, Collection<UUID> lectureIds);

  /**
   * Удалить все личные отметки студента — при удалении его аккаунта (issue #133).
   */
  @Modifying
  @Query("DELETE FROM LectureSelfAttendance a WHERE a.studentId = :studentId")
  int deleteAllOfStudent(@Param("studentId") UUID studentId);
}
