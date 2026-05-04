package com.coungard.univer.entity;

import java.io.Serializable;
import java.util.UUID;
import lombok.Data;

@Data
public class EnrollmentId implements Serializable {

  private UUID studentId;
  private UUID courseId;

  public EnrollmentId() {
  }

  public EnrollmentId(UUID studentId, UUID courseId) {
    this.studentId = studentId;
    this.courseId = courseId;
  }
}