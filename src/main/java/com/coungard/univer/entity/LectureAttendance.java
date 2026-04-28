package com.coungard.univer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "lecture_attendance")
@IdClass(LectureAttendanceId.class)
@NoArgsConstructor
@Data
public class LectureAttendance {

    @Id
    @Column(name = "student_id", insertable = false, updatable = false)
    private UUID studentId;

    @Id
    @Column(name = "lecture_id", insertable = false, updatable = false)
    private UUID lectureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    private boolean attended = true;
}