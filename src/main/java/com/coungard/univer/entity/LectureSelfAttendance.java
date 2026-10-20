package com.coungard.univer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Личная отметка студента о посещении лекции (issue #117). Отметка преподавателя
 * ({@link LectureAttendance}) по той же лекции главнее.
 */
@Entity
@Table(name = "lecture_self_attendance")
@IdClass(LectureAttendanceId.class)
@NoArgsConstructor
@Data
public class LectureSelfAttendance extends AuditableEntity {

  @Id
  @Column(name = "student_id")
  private UUID studentId;

  @Id
  @Column(name = "lecture_id")
  private UUID lectureId;

  private boolean attended;
}
