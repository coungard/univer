-- ===================================
-- Group (Студенческая группа)
-- Таблица названа student_groups, а не group/groups — GROUP зарезервированное слово SQL,
-- а groups может конфликтовать с оконными функциями (frame unit GROUPS).
-- ===================================
CREATE TABLE student_groups (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    semester_id UUID NOT NULL,
    name VARCHAR(64) NOT NULL,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE
);

CREATE INDEX idx_student_group_semester ON student_groups(semester_id);

-- ===================================
-- Student -> Group (опциональная связь)
-- ===================================
ALTER TABLE students ADD COLUMN group_id UUID;
ALTER TABLE students ADD CONSTRAINT fk_student_group
    FOREIGN KEY (group_id) REFERENCES student_groups(id) ON DELETE SET NULL;

CREATE INDEX idx_student_group_id ON students(group_id);
