package com.coungard.univer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "Итоговая отметка посещаемости лекции у вызывающего студента")
public record MyAttendanceDto(
    UUID lectureId,

    boolean attended,

    @Schema(description = "TEACHER, если лекцию отметил преподаватель (его отметка главнее личной), иначе STUDENT")
    AttendanceSource source
) {
}
