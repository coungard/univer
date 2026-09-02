package com.coungard.univer.dto.request;

import com.coungard.univer.dto.WeekScheduleCycleStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Запрос на смену статуса согласования циклического расписания ({@code WeekScheduleCycle}). Отдельный
 * request-объект вместо переиспользования {@code WeekScheduleCycleDto} — там обязателен
 * {@code semesterId}, не относящийся к этой операции.
 */
public record UpdateWeekScheduleCycleStatusRequest(

    @NotNull(message = "Статус обязателен")
    WeekScheduleCycleStatus status
) {
}
