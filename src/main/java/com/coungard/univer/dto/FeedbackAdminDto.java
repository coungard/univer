package com.coungard.univer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "Отзыв из приложения с автором и устройством — для администратора")
public record FeedbackAdminDto(
    UUID id,

    FeedbackType type,

    String message,

    FeedbackStatus status,

    @Schema(description = "Автор — subject JWT (= id студента или преподавателя)")
    UUID userId,

    @Schema(description = "Старшая роль автора на момент отправки: ADMIN, TEACHER или STUDENT")
    String role,

    @Schema(description = "Логин автора на момент отправки")
    String username,

    String appVersion,

    FeedbackPlatform platform,

    String osVersion,

    Instant createdAt,

    Instant updatedAt,

    UUID createdBy,

    @Schema(description = "Кто последним менял статус")
    UUID updatedBy
) {
}
