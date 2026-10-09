-- Создание факультета студентом при регистрации (issue #87).
--
-- Факультет помнит, какой студент его создал: по этому полю считается лимит факультетов на одного
-- студента и проверяется право исправить название своего факультета. У факультетов, заведённых
-- администратором или seed-миграциями, поле пустое.
--
-- ON DELETE SET NULL: удаление студента не должно удалять факультет, который уже могли выбрать другие.
ALTER TABLE faculties ADD COLUMN created_by_student_id UUID;
ALTER TABLE faculties ADD CONSTRAINT fk_faculty_created_by_student
    FOREIGN KEY (created_by_student_id) REFERENCES students(id) ON DELETE SET NULL;

CREATE INDEX idx_faculty_created_by_student ON faculties(created_by_student_id);

-- Название факультета уникально в пределах университета без учёта регистра и крайних пробелов
-- (UNIQUE (name, university_id) из V1 ловит только точное совпадение). Сервис проверяет это сам и
-- отвечает 409; индекс страхует от гонки, когда двое студентов создают один факультет одновременно.
CREATE UNIQUE INDEX uq_faculty_university_name ON faculties (university_id, lower(btrim(name)));
