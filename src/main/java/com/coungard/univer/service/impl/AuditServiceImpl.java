package com.coungard.univer.service.impl;

import com.coungard.univer.dto.AuditAction;
import com.coungard.univer.dto.AuditEntityType;
import com.coungard.univer.dto.AuditEventDto;
import com.coungard.univer.entity.AuditEvent;
import com.coungard.univer.filter.RequestIdFilter;
import com.coungard.univer.mapper.AuditEventMapper;
import com.coungard.univer.repository.AuditEventRepository;
import com.coungard.univer.security.Role;
import com.coungard.univer.service.AuditService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

  /**
   * Роли по старшинству: в журнал пишется первая из тех, что есть у автора.
   */
  private static final List<Role> ROLES_BY_PRIORITY = List.of(Role.ROLE_ADMIN, Role.ROLE_TEACHER, Role.ROLE_STUDENT);

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
    Authentication authentication = currentAuthentication();

    AuditEvent event = new AuditEvent();
    event.setOccurredAt(Instant.now());
    event.setActorId(resolveActorId(authentication));
    event.setActorRole(resolveActorRole(authentication));
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

  /**
   * Аутентификация автора или {@code null}, если запрос анонимный (регистрация) либо выполняется вне
   * запроса.
   */
  private static Authentication currentAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
      return null;
    }
    return authentication;
  }

  private static UUID resolveActorId(Authentication authentication) {
    if (authentication == null) {
      return null;
    }
    try {
      return UUID.fromString(authentication.getName());
    } catch (IllegalArgumentException ex) {
      // Subject Keycloak всегда UUID; иное имя автора в журнал не попадает
      return null;
    }
  }

  private static String resolveActorRole(Authentication authentication) {
    if (authentication == null) {
      return null;
    }
    Set<String> authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
    return ROLES_BY_PRIORITY.stream()
        .filter(role -> authorities.contains(role.name()))
        .map(role -> role.name().substring("ROLE_".length()))
        .findFirst()
        .orElse(null);
  }
}
