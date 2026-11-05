package com.coungard.univer.dto.request;

import com.coungard.univer.dto.FeedbackStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Тело {@code PATCH /feedback/{id}}: администратор меняет статус отзыва.
 */
public record UpdateFeedbackStatusRequest(

    @NotNull(message = "Статус обязателен")
    FeedbackStatus status
) {
}
