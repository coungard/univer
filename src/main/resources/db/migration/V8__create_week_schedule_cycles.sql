-- ===================================
-- WeekScheduleCycle (Циклическое расписание семестра — по чётности недели)
-- Один цикл на семестр: чётность недели — свойство календаря семестра, общее для всех
-- групп в нём (не отдельной группы), см. TARGET.md.
-- ===================================
CREATE TABLE week_schedule_cycles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    semester_id UUID NOT NULL UNIQUE,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE
);

-- ===================================
-- Pair (Пара — конкретный слот циклического расписания)
-- ===================================
CREATE TABLE pairs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    week_schedule_cycle_id UUID NOT NULL,
    day_of_week VARCHAR(16) NOT NULL
        CHECK (day_of_week IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY')),
    week_parity VARCHAR(8) NOT NULL CHECK (week_parity IN ('ODD', 'EVEN', 'BOTH')),
    pair_number INT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    course_id UUID NOT NULL,
    teacher_id UUID,
    FOREIGN KEY (week_schedule_cycle_id) REFERENCES week_schedule_cycles(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE SET NULL
);

CREATE INDEX idx_pair_cycle ON pairs(week_schedule_cycle_id);
CREATE INDEX idx_pair_course ON pairs(course_id);

-- ===================================
-- Pair <-> Group — многие-ко-многим (поток: лекция может идти нескольким группам разом)
-- ===================================
CREATE TABLE pair_groups (
    pair_id UUID NOT NULL,
    group_id UUID NOT NULL,
    PRIMARY KEY (pair_id, group_id),
    FOREIGN KEY (pair_id) REFERENCES pairs(id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES student_groups(id) ON DELETE CASCADE
);

CREATE INDEX idx_pair_groups_group ON pair_groups(group_id);
