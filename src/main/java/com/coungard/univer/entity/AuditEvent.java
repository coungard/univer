package com.coungard.univer.entity;

import com.coungard.univer.dto.AuditAction;
import com.coungard.univer.dto.AuditEntityType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Запись журнала бизнес-событий (issue #111). Ссылки на автора и сущность — просто UUID, без связей:
 * запись должна пережить удаление и того, и другого.
 */
@Entity
@Table(name = "audit_events")
@NoArgsConstructor
@Data
public class AuditEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "occurred_at", nullable = false)
  private Instant occurredAt;

  @Column(name = "actor_id")
  private UUID actorId;

  @Column(name = "actor_role", length = 32)
  private String actorRole;

  @Enumerated(EnumType.STRING)
  @Column(name = "action", nullable = false, length = 32)
  private AuditAction action;

  @Enumerated(EnumType.STRING)
  @Column(name = "entity_type", nullable = false, length = 32)
  private AuditEntityType entityType;

  @Column(name = "entity_id")
  private UUID entityId;

  @Column(name = "request_id", length = 64)
  private String requestId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "details", columnDefinition = "jsonb")
  private Map<String, Object> details;
}
