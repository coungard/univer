package com.coungard.univer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@IdClass(EnrollmentId.class)
@Table(name = "enrollment")
@Data
@NoArgsConstructor
public class Enrollment {

  @Id
  @Column(name = "student_id", insertable = false, updatable = false)
  private UUID studentId;

  @Id
  @Column(name = "course_id", insertable = false, updatable = false)
  private UUID courseId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id")
  private Student student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "course_id")
  private Course course;

  @Column(name = "enrolled_at")
  private LocalDateTime enrolledAt;

  @Column(length = 20)
  private String status;
}