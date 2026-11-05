package com.coungard.univer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "Отзыв из приложения — ответ автору на POST /feedback")
public record FeedbackDto(
    UUID id,

    FeedbackType type,

    String message,

    FeedbackStatus status,

    Instant createdAt
) {
}
