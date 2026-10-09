package com.coungard.univer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "Заявка студента на добавление университета")
public record UniversityRequestDto(
    UUID id,

    @Schema(description = "Название университета, как его ввёл студент")
    String name,

    @Schema(description = "Регион, выбранный студентом; null, если студент региона не указал")
    UUID regionId,

    String regionName,

    UniversityRequestStatus status,

    @Schema(description = "Университет, которым заявка закрыта; заполнен только у COMPLETED")
    UUID universityId,

    @Schema(description = "Пояснение администратора при отклонении; заполнено только у REJECTED и не всегда")
    String comment,

    @Schema(description = "Автор заявки")
    UUID studentId,

    String studentUsername,

    String studentFullname,

    Instant createdAt,

    Instant updatedAt
) {
}
