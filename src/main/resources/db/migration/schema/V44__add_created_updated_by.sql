-- Кто и когда создал и последним изменил запись — для всех изменяемых сущностей (issue #112).
--
-- created_by/updated_by — subject JWT автора (= id студента/преподавателя); пусто у действий без входа
-- (регистрация), у записей из seed-миграций и у всех строк, появившихся до этой миграции. Ссылок на
-- students/teachers нет намеренно: автор может быть администратором, которого в этих таблицах нет.
--
-- Новые created_at добавляются без значения по умолчанию, а DEFAULT NOW() ставится отдельной командой:
-- иначе PostgreSQL проставил бы существующим строкам время этой миграции вместо честного «неизвестно».

ALTER TABLE address
    ADD COLUMN created_at TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP,
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;
ALTER TABLE address ALTER COLUMN created_at SET DEFAULT NOW();

ALTER TABLE courses
    ADD COLUMN created_at TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP,
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;
ALTER TABLE courses ALTER COLUMN created_at SET DEFAULT NOW();

ALTER TABLE departments
    ADD COLUMN created_at TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP,
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;
ALTER TABLE departments ALTER COLUMN created_at SET DEFAULT NOW();

ALTER TABLE enrollment
    ADD COLUMN created_at TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP,
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;
ALTER TABLE enrollment ALTER COLUMN created_at SET DEFAULT NOW();

ALTER TABLE faculties
    ADD COLUMN created_at TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP,
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;
ALTER TABLE faculties ALTER COLUMN created_at SET DEFAULT NOW();

ALTER TABLE lecture_attendance
    ADD COLUMN created_at TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP,
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;
ALTER TABLE lecture_attendance ALTER COLUMN created_at SET DEFAULT NOW();

ALTER TABLE lectures
    ADD COLUMN created_at TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP,
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;
ALTER TABLE lectures ALTER COLUMN created_at SET DEFAULT NOW();

ALTER TABLE pairs
    ADD COLUMN created_at TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP,
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;
ALTER TABLE pairs ALTER COLUMN created_at SET DEFAULT NOW();

ALTER TABLE persons
    ADD COLUMN created_at TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP,
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;
ALTER TABLE persons ALTER COLUMN created_at SET DEFAULT NOW();

ALTER TABLE semesters
    ADD COLUMN created_at TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP,
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;
ALTER TABLE semesters ALTER COLUMN created_at SET DEFAULT NOW();

ALTER TABLE student_groups
    ADD COLUMN created_at TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP,
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;
ALTER TABLE student_groups ALTER COLUMN created_at SET DEFAULT NOW();

ALTER TABLE study_years
    ADD COLUMN created_at TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP,
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;
ALTER TABLE study_years ALTER COLUMN created_at SET DEFAULT NOW();

ALTER TABLE week_schedule_cycles
    ADD COLUMN created_at TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP,
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;
ALTER TABLE week_schedule_cycles ALTER COLUMN created_at SET DEFAULT NOW();

-- Временные метки у этих таблиц уже есть (V1, V12, V39) — добавляются только авторы.
ALTER TABLE bell_schedule_entries
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;

ALTER TABLE students
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;

ALTER TABLE teachers
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;

ALTER TABLE universities
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;

ALTER TABLE university_requests
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;
