-- ===================================
-- Course: добавить недостающую колонку teacher_id
--
-- Обнаружено при реализации Pair (issue #36): миграция V1 создавала таблицу courses
-- без teacher_id, хотя entity Course.teacher существует с самого начала (issue #29).
-- Тесты этого не ловили, так как Testcontainers-тесты используют
-- spring.jpa.hibernate.ddl-auto=create-drop и пересоздают схему по entity-маппингам,
-- минуя реальные Flyway-миграции — в проде (только Flyway, без ddl-auto) колонки не было.
-- ===================================
ALTER TABLE courses ADD COLUMN teacher_id UUID;
ALTER TABLE courses ADD CONSTRAINT fk_course_teacher
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE SET NULL;

CREATE INDEX idx_course_teacher ON courses(teacher_id);
