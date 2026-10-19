package com.coungard.univer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "Событие журнала аудита: кто, когда и что сделал")
public record AuditEventDto(
    UUID id,

    Instant occurredAt,

    @Schema(description = "Автор действия (subject JWT); null у действий без входа, например у регистрации")
    UUID actorId,

    @Schema(description = "Старшая роль автора на момент действия: ADMIN, TEACHER, STUDENT")
    String actorRole,

    AuditAction action,

    AuditEntityType entityType,

    UUID entityId,

    @Schema(description = "X-Request-Id запроса, в котором выполнено действие")
    String requestId,

    @Schema(description = "Подробности действия; набор полей зависит от action и entityType")
    Map<String, Object> details
) {
}
