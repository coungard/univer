package com.coungard.univer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Тело {@code PUT /students/me/university-request}: студент оставляет заявку на добавление
 * университета, которого нет в справочнике, либо обновляет свою необработанную заявку.
 */
public record SubmitUniversityRequest(

    @NotBlank(message = "Название университета обязательно")
    @Size(max = 255, message = "Название университета не длиннее 255 символов")
    String name,

    UUID regionId
) {
}
