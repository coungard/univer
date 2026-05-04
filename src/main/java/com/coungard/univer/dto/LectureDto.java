package com.coungard.univer.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LectureDto(
    UUID id,
    String title,
    String content,
    LocalDateTime scheduledTime,
    Integer durationMinutes,
    UUID courseId,
    UUID teacherId
) {}