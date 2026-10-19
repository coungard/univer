package com.coungard.univer.service.impl;

import com.coungard.univer.dto.AuditAction;
import com.coungard.univer.dto.AuditEntityType;
import com.coungard.univer.dto.AuditEventDto;
import com.coungard.univer.entity.AuditEvent;
import com.coungard.univer.filter.RequestIdFilter;
import com.coungard.univer.mapper.AuditEventMapper;
import com.coungard.univer.repository.AuditEventRepository;
import com.coungard.univer.security.CurrentActor;
import com.coungard.univer.service.AuditService;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

  private final AuditEventRepository auditEventRepository;
  private final AuditEventMapper auditEventMapper;

  @Override
  @Transactional(propagation = Propagation.MANDATORY)
  public void record(AuditAction action, AuditEntityType entityType, UUID entityId) {
    record(action, entityType, entityId, null);
  }

  @Override
  @Transactional(propagation = Propagation.MANDATORY)
  public void record(AuditAction action, AuditEntityType entityType, UUID entityId, Map<String, Object> details) {
    AuditEvent event = new AuditEvent();
    event.setOccurredAt(Instant.now());
    event.setActorId(CurrentActor.id().orElse(null));
    event.setActorRole(CurrentActor.role().orElse(null));
    event.setAction(action);
    event.setEntityType(entityType);
    event.setEntityId(entityId);
    event.setRequestId(MDC.get(RequestIdFilter.MDC_KEY));
    event.setDetails(details == null || details.isEmpty() ? null : details);

    auditEventRepository.save(event);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<AuditEventDto> getEvents(AuditEntityType entityType, UUID entityId, UUID actorId, Instant from,
      Instant to, Pageable pageable) {

    Specification<AuditEvent> spec = Specification.<AuditEvent>where(null)
        .and(equalTo("entityType", entityType))
        .and(equalTo("entityId", entityId))
        .and(equalTo("actorId", actorId))
        .and(from == null ? null : (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("occurredAt"), from))
        .and(to == null ? null : (root, query, cb) -> cb.lessThan(root.get("occurredAt"), to));
    return auditEventRepository.findAll(spec, pageable).map(auditEventMapper::toDto);
  }

  private static Specification<AuditEvent> equalTo(String attribute, Object value) {
    return value == null ? null : (root, query, cb) -> cb.equal(root.get(attribute), value);
  }
}
