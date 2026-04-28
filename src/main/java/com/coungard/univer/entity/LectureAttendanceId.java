package com.coungard.univer.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class LectureAttendanceId implements Serializable {
    private UUID studentId;
    private UUID lectureId;

    public LectureAttendanceId() {
    }

    public LectureAttendanceId(UUID studentId, UUID lectureId) {
        this.studentId = studentId;
        this.lectureId = lectureId;
    }
}