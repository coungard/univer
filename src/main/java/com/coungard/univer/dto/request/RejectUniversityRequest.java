package com.coungard.univer.dto.request;

import jakarta.validation.constraints.Size;

/**
 * Необязательное тело {@code POST /university-requests/{id}/reject}: администратор поясняет студенту,
 * почему заявка отклонена и что с этим делать.
 */
public record RejectUniversityRequest(

    @Size(max = 500, message = "Пояснение не длиннее 500 символов")
    String comment
) {
}
