package com.coungard.univer.controller;

import com.coungard.univer.dto.AuditEntityType;
import com.coungard.univer.dto.AuditEventDto;
import com.coungard.univer.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit-events")
@RequiredArgsConstructor
@Tag(name = "AuditEvents", description = "Журнал бизнес-событий: кто, когда и что сделал")
@SecurityRequirement(name = "bearerAuth")
public class AuditEventController {

  private final AuditService auditService;

  @Operation(
      summary = "Получить события журнала с пагинацией",
      description = "Сначала новые. Все фильтры необязательны и объединяются через «и»."
  )
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<AuditEventDto>> getEvents(
      @RequestParam(required = false) AuditEntityType entityType,
      @RequestParam(required = false) UUID entityId,
      @RequestParam(required = false) UUID actorId,
      @Parameter(description = "Начало периода включительно, ISO-8601, например 2026-10-01T00:00:00Z")
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
      @Parameter(description = "Конец периода не включительно, ISO-8601")
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("occurredAt").descending());
    Page<AuditEventDto> events = auditService.getEvents(entityType, entityId, actorId, from, to, pageable);
    return ResponseEntity.ok(events);
  }
}
