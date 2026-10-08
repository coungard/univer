-- Упразднение программ (issue #85), шаг 1 из 3: учебный год привязывается к факультету напрямую,
-- без промежуточной программы. Колонка пока NULL-able — заполняется и дедуплицируется в V34
-- (data), а NOT NULL, уникальность и удаление programs — в V35 (schema).
--
-- Без ON DELETE CASCADE, как и было у programs.faculty_id: факультет с заведёнными курсами,
-- семестрами и группами не должен удаляться молча вместе с ними.
ALTER TABLE study_years ADD COLUMN faculty_id UUID;
ALTER TABLE study_years ADD CONSTRAINT fk_study_year_faculty
    FOREIGN KEY (faculty_id) REFERENCES faculties(id);
