-- Статус согласования циклического расписания: DRAFT (формируется, доступен для правок ADMIN и
-- STUDENT своей группы) / AGREED (финализирован, правки только ADMIN). Существующие циклы получают
-- DRAFT через DEFAULT.
ALTER TABLE week_schedule_cycles
    ADD COLUMN status VARCHAR(16) NOT NULL DEFAULT 'DRAFT'
    CHECK (status IN ('DRAFT', 'AGREED'));
