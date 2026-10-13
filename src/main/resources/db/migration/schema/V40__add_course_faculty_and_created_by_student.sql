-- Создание предмета студентом (issue #97).
--
-- До сих пор предмет был связан с университетом только через кафедру, а у факультета, созданного
-- студентом, кафедр нет. Прямая необязательная связь с факультетом: по ней отбираются предметы для
-- выбора при создании пары. У предметов, заведённых раньше, поле пустое — их факультет определяется
-- по кафедре.
--
-- ON DELETE SET NULL — как у department_id: удаление факультета не удаляет предмет, на который уже
-- могут ссылаться пары и занятия.
ALTER TABLE courses ADD COLUMN faculty_id UUID;
ALTER TABLE courses ADD CONSTRAINT fk_course_faculty
    FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE SET NULL;

CREATE INDEX idx_course_faculty ON courses(faculty_id);

-- Предмет помнит, какой студент его создал: по этому полю считается лимит предметов на одного
-- студента. У предметов, заведённых администратором, преподавателем или seed-миграциями, поле пустое.
--
-- ON DELETE SET NULL: удаление студента не должно удалять предмет, по которому уже есть пары.
ALTER TABLE courses ADD COLUMN created_by_student_id UUID;
ALTER TABLE courses ADD CONSTRAINT fk_course_created_by_student
    FOREIGN KEY (created_by_student_id) REFERENCES students(id) ON DELETE SET NULL;

CREATE INDEX idx_course_created_by_student ON courses(created_by_student_id);

-- Название предмета уникально в пределах факультета без учёта регистра и крайних пробелов. Сервис
-- проверяет это сам и отвечает 409; индекс страхует от гонки, когда двое студентов создают один
-- предмет одновременно. Предметов без прямой связи с факультетом правило не касается.
CREATE UNIQUE INDEX uq_course_faculty_title ON courses (faculty_id, lower(btrim(title)))
    WHERE faculty_id IS NOT NULL;
