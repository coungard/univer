-- Самостоятельное заполнение профиля студентом (issue #82): студент по шагам выбирает университет,
-- факультет, курс и группу. Группы в справочнике может ещё не быть, поэтому выбранные факультет и
-- курс хранятся у самого студента, а не выводятся из группы.
ALTER TABLE students ADD COLUMN faculty_id UUID;
ALTER TABLE students ADD CONSTRAINT fk_student_faculty
    FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE SET NULL;
ALTER TABLE students ADD COLUMN year_number INT;
ALTER TABLE students ADD CONSTRAINT chk_student_year_number CHECK (year_number >= 1);

CREATE INDEX idx_student_faculty_id ON students(faculty_id);
