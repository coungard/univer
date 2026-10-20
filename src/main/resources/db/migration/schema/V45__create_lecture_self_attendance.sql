-- Личные отметки посещаемости студента: «был» / «не был» на прошедшем занятии (issue #117).
--
-- Хранятся отдельно от отметок преподавателя (lecture_attendance): те не меняются, их выборки и
-- статистика не видят личных отметок. Если по лекции есть отметка преподавателя, она главнее личной.
CREATE TABLE lecture_self_attendance (
    student_id UUID NOT NULL,
    lecture_id UUID NOT NULL,
    attended BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    PRIMARY KEY (student_id, lecture_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (lecture_id) REFERENCES lectures(id) ON DELETE CASCADE
);

CREATE INDEX idx_lecture_self_attendance_lecture ON lecture_self_attendance(lecture_id);
