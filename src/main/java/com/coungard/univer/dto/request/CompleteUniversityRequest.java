package com.coungard.univer.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Тело {@code POST /university-requests/{id}/complete}: администратор закрывает заявку студента,
 * указывая университет — только что созданный или уже существующий, если студент его просто не нашёл.
 */
public record CompleteUniversityRequest(

    @NotNull(message = "Университет обязателен")
    UUID universityId
) {
}
