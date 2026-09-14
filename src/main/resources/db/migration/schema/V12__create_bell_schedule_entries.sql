-- Звонковое расписание (bell schedule) — справочник "номер пары -> время начала/окончания",
-- per-university (см. TARGET.md: сетка времён пар конфигурируема, у разных университетов может
-- быть разное число пар и разная длительность). university_id = NULL — системная запись по
-- умолчанию, применяется, если у конкретного университета нет своей записи на этот pair_number.
-- Это только источник для заполнения Pair.start_time/end_time при создании пары, а не жёсткая
-- связь — сами эти колонки на pairs остаются самостоятельными редактируемыми (см. V8).
CREATE TABLE bell_schedule_entries (
    id UUID PRIMARY KEY,
    university_id UUID REFERENCES universities(id) ON DELETE CASCADE,
    pair_number INTEGER NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT chk_bell_schedule_entry_pair_number CHECK (pair_number >= 1),
    CONSTRAINT chk_bell_schedule_entry_time_order CHECK (end_time > start_time)
);

-- NULL в обычном UNIQUE не считается равным самому себе в Postgres, поэтому обычный
-- UNIQUE(university_id, pair_number) не помешает задвоить дефолтные (university_id IS NULL)
-- записи для одного и того же pair_number — используем два partial unique index.
CREATE UNIQUE INDEX uq_bell_schedule_entry_university_pair
    ON bell_schedule_entries (university_id, pair_number)
    WHERE university_id IS NOT NULL;

CREATE UNIQUE INDEX uq_bell_schedule_entry_default_pair
    ON bell_schedule_entries (pair_number)
    WHERE university_id IS NULL;
