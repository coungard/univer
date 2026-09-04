-- ===================================
-- Lecture (Лекция)
-- ===================================
-- Создаётся после WeekScheduleCycle/Pair (V7), а не в базовой схеме (V1) — Lecture.source_pair_id
-- ссылается на pairs(id), которой ещё нет на момент V1.
CREATE TABLE lectures (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    content TEXT,
    scheduled_time TIMESTAMP NOT NULL,
    duration_minutes INT DEFAULT 90,
    course_id UUID NOT NULL,
    -- Lecture.teacher — это Teacher, не Person (Teacher.id, полученный из Keycloak, не совпадает с
    -- Person.id) — FK сразу на teachers.
    teacher_id UUID,
    -- Room — простое опциональное поле-строка, пробрасываемое из Pair.room при генерации лекции из
    -- шаблона (см. V7).
    room VARCHAR(32),
    -- Опциональная ссылка на Pair-шаблон, из которого лекция сгенерирована (в дополнение к ручному
    -- созданию).
    source_pair_id UUID,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE SET NULL,
    FOREIGN KEY (source_pair_id) REFERENCES pairs(id) ON DELETE SET NULL
);

CREATE INDEX idx_lecture_course ON lectures(course_id);
CREATE INDEX idx_lecture_source_pair ON lectures(source_pair_id);

-- ===================================
-- Lecture <-> Group — многие-ко-многим (поток, как и у Pair, см. TARGET.md)
-- ===================================
CREATE TABLE lecture_groups (
    lecture_id UUID NOT NULL,
    group_id UUID NOT NULL,
    PRIMARY KEY (lecture_id, group_id),
    FOREIGN KEY (lecture_id) REFERENCES lectures(id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES student_groups(id) ON DELETE CASCADE
);

CREATE INDEX idx_lecture_groups_group ON lecture_groups(group_id);

-- ===================================
-- Enrollment (Зачисление студента на курс)
-- ===================================
CREATE TABLE enrollment (
    -- Enrollment.student — это Student, не Person (Student.id переиспользует Keycloak user ID при
    -- регистрации, см. CLAUDE.md) — FK сразу на students.
    student_id UUID NOT NULL,
    course_id UUID NOT NULL,
    enrolled_at TIMESTAMP DEFAULT NOW(),
    -- Хранится через @Enumerated(EnumType.STRING) — значения в верхнем регистре.
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'COMPLETED', 'DROPPED')),
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

CREATE INDEX idx_enrollment_student ON enrollment(student_id);

-- ===================================
-- LectureAttendance (Посещение лекций)
-- ===================================
CREATE TABLE lecture_attendance (
    -- LectureAttendance.student — это Student, не Person, тот же случай, что и у Enrollment выше.
    student_id UUID NOT NULL,
    lecture_id UUID NOT NULL,
    attended BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (student_id, lecture_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (lecture_id) REFERENCES lectures(id) ON DELETE CASCADE
);

CREATE INDEX idx_lecture_attendance_student ON lecture_attendance(student_id);
