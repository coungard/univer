package com.coungard.univer.dto;

import java.util.UUID;

public record LectureAttendanceDto(
    UUID studentId,
    UUID lectureId,
    boolean attended
) {}