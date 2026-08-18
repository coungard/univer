package com.coungard.univer.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Запрос на генерацию {@code Lecture} на конкретную дату из шаблона {@code Pair} циклического
 * расписания (Этап 3), в дополнение к ручному созданию лекции.
 */
public record GenerateLectureRequest(

    @NotNull(message = "ID пары обязателен")
    UUID pairId,

    @NotNull(message = "Дата лекции обязательна")
    LocalDate date
) {
}
