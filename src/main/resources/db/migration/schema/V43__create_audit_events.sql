-- Журнал бизнес-событий: кто, когда и что сделал (issue #111).
--
-- Запись добавляет сервис в той же транзакции, что и само действие: откатилось действие — откатилась и
-- запись. Внешних ключей на сущности и на автора нет намеренно: журнал должен пережить их удаление.
CREATE TABLE audit_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    occurred_at TIMESTAMP NOT NULL DEFAULT NOW(),
    -- subject JWT (= id студента/преподавателя). Пуст у действий без входа — например, у регистрации.
    actor_id UUID,
    -- Старшая роль автора на момент действия: ADMIN, TEACHER, STUDENT.
    actor_role VARCHAR(32),
    -- REGISTER, CREATE, UPDATE, DELETE, STATUS_CHANGE, MARK, COMPLETE, REJECT.
    action VARCHAR(32) NOT NULL,
    entity_type VARCHAR(32) NOT NULL,
    entity_id UUID,
    -- X-Request-Id запроса (issue #110): связывает событие со строками лога.
    request_id VARCHAR(64),
    -- Подробности действия, например число удалённых и созданных занятий при изменении пары.
    details JSONB
);

CREATE INDEX idx_audit_event_entity ON audit_events(entity_type, entity_id, occurred_at);

CREATE INDEX idx_audit_event_actor ON audit_events(actor_id, occurred_at);

CREATE INDEX idx_audit_event_occurred_at ON audit_events(occurred_at);
