package com.coungard.univer.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class EnrollmentId implements Serializable {
    private UUID studentId;
    private UUID courseId;

    public EnrollmentId() {}

    public EnrollmentId(UUID studentId, UUID courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }
}