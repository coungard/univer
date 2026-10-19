package com.coungard.univer.repository;

import com.coungard.univer.entity.AuditEvent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * {@link JpaSpecificationExecutor} — для отбора по любому сочетанию необязательных фильтров
 * {@code GET /audit-events}.
 */
public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID>, JpaSpecificationExecutor<AuditEvent> {
}
