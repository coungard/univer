-- Упразднение программ (issue #85), шаг 3 из 3: после переноса в V34 учебный год окончательно
-- принадлежит факультету, а программы удаляются. DROP COLUMN program_id снимает вместе с колонкой
-- её внешний ключ, UNIQUE (program_id, year_number) и индекс idx_study_year_program.
ALTER TABLE study_years ALTER COLUMN faculty_id SET NOT NULL;
ALTER TABLE study_years DROP COLUMN program_id;
ALTER TABLE study_years ADD CONSTRAINT uq_study_year_faculty_year_number UNIQUE (faculty_id, year_number);

DROP TABLE programs;
