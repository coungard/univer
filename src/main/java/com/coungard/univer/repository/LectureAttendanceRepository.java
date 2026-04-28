package com.coungard.univer.repository;

import com.coungard.univer.entity.LectureAttendance;
import com.coungard.univer.entity.LectureAttendanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LectureAttendanceRepository extends JpaRepository<LectureAttendance, LectureAttendanceId> {
    List<LectureAttendance> findByStudentId(UUID studentId);
    List<LectureAttendance> findByLectureId(UUID lectureId);
}