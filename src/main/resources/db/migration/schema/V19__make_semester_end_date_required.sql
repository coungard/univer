-- Все существующие строки semesters уже получили end_date в V18 — можно требовать его для всех
-- новых записей.
ALTER TABLE semesters ALTER COLUMN end_date SET NOT NULL;
