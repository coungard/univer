-- Дата окончания семестра. Нужна, чтобы иметь объективную границу для массовой генерации Lecture
-- на весь семестр из шаблонов Pair (см. issue про генерацию расписания). Пока nullable — у уже
-- существующих строк semesters (заведённых V15) её ещё нет; проставляется в V18, после чего
-- V19 переводит колонку в NOT NULL.
ALTER TABLE semesters ADD COLUMN end_date DATE;

ALTER TABLE semesters ADD CONSTRAINT chk_semester_end_after_start
    CHECK (end_date IS NULL OR end_date > start_date);
