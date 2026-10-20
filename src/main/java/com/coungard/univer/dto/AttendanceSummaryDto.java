package com.coungard.univer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Посещаемость студентом курса в текущем семестре по итоговым отметкам")
public record AttendanceSummaryDto(
    @Schema(description = "Уже начавшиеся лекции курса у группы студента")
    long pastLectures,

    long attended,

    long missed,

    @Schema(description = "Начавшиеся лекции без отметки — не считаются ни посещёнными, ни пропусками")
    long unmarked
) {
}
