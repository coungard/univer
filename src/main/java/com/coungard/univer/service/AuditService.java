package com.coungard.univer.service;

import com.coungard.univer.dto.AuditAction;
import com.coungard.univer.dto.AuditEntityType;
import com.coungard.univer.dto.AuditEventDto;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Журнал бизнес-событий (issue #111): кто, когда и что сделал. Сервисы вызывают
 * {@link #record} явно, как и {@code *Validator}, — в той же транзакции, что и само действие.
 */
public interface AuditService {

  /**
   * Записать событие без подробностей.
   *
   * @see #record(AuditAction, AuditEntityType, UUID, Map)
   */
  void record(AuditAction action, AuditEntityType entityType, UUID entityId);

  /**
   * Записать событие. Автор — subject JWT текущего запроса (пуст у анонимных действий, например у
   * регистрации), request id — из MDC. Вызывается только внутри транзакции действия: откатилось
   * действие — откатилась и запись.
   *
   * @param details подробности действия; значения — строки, числа, логические значения, UUID или
   *                enum'ы. Персональные данные (ФИО, email, телефон) сюда не пишутся
   */
  void record(AuditAction action, AuditEntityType entityType, UUID entityId, Map<String, Object> details);

  /**
   * Получить события, сначала новые. Все фильтры необязательны и объединяются через «и».
   *
   * @param from начало периода включительно
   * @param to   конец периода не включительно
   */
  Page<AuditEventDto> getEvents(AuditEntityType entityType, UUID entityId, UUID actorId, Instant from,
      Instant to, Pageable pageable);

  /**
   * Подробности события из пар ключ–значение. В отличие от {@link Map#of}, допускает {@code null} в
   * значениях и сохраняет порядок ключей.
   */
  static Map<String, Object> details(Object... keysAndValues) {
    if (keysAndValues.length % 2 != 0) {
      throw new IllegalArgumentException("Ожидаются пары ключ–значение");
    }
    Map<String, Object> details = new LinkedHashMap<>();
    for (int i = 0; i < keysAndValues.length; i += 2) {
      details.put((String) keysAndValues[i], keysAndValues[i + 1]);
    }
    return details;
  }
}
