package com.coungard.univer.mapper;

import com.coungard.univer.dto.AuditEventDto;
import com.coungard.univer.entity.AuditEvent;
import org.springframework.stereotype.Component;

@Component
public class AuditEventMapper {

  public AuditEventDto toDto(AuditEvent event) {
    if (event == null) {
      return null;
    }
    return AuditEventDto.builder()
        .id(event.getId())
        .occurredAt(event.getOccurredAt())
        .actorId(event.getActorId())
        .actorRole(event.getActorRole())
        .action(event.getAction())
        .entityType(event.getEntityType())
        .entityId(event.getEntityId())
        .requestId(event.getRequestId())
        .details(event.getDetails())
        .build();
  }
}
