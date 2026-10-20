package com.coungard.univer.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * Тело {@code PUT /attendance/me/lecture/{lectureId}}: личная отметка студента «был» / «не был»
 * (issue #117).
 */
public record MarkMyAttendanceRequest(

    @NotNull(message = "Отметка обязательна")
    Boolean attended
) {
}
