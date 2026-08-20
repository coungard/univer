-- ===================================
-- Enrollment / LectureAttendance: исправить FK student_id (ссылался на persons вместо students)
--
-- Обнаружено при реализации Этапа 5 (issues #38, #39) — тот же класс бага, что уже исправлен для
-- courses.teacher_id (V7) и lectures.teacher_id (V9): Enrollment.student и
-- LectureAttendance.student — это Student, но Student.id (переиспользует Keycloak user ID при
-- регистрации, см. CLAUDE.md) не совпадает с Person.id, поэтому исходный FK на persons(id)
-- отвергал бы любой валидный student_id в реальной БД. Тесты не ловили — используют
-- hibernate.ddl-auto=create-drop и пересоздают схему по entity-маппингам, минуя Flyway.
-- ===================================
ALTER TABLE enrollment DROP CONSTRAINT enrollment_student_id_fkey;
ALTER TABLE enrollment ADD CONSTRAINT fk_enrollment_student
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE;

ALTER TABLE lecture_attendance DROP CONSTRAINT lecture_attendance_student_id_fkey;
ALTER TABLE lecture_attendance ADD CONSTRAINT fk_lecture_attendance_student
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE;

-- ===================================
-- Enrollment.status: переход со свободной строки на enum EnrollmentStatus
-- (ACTIVE/COMPLETED/DROPPED), хранится через @Enumerated(EnumType.STRING) — значения в верхнем
-- регистре вместо исходных строчных ('active'/'completed'/'dropped').
-- ===================================
ALTER TABLE enrollment DROP CONSTRAINT enrollment_status_check;
UPDATE enrollment SET status = UPPER(status);
ALTER TABLE enrollment ALTER COLUMN status SET DEFAULT 'ACTIVE';
ALTER TABLE enrollment ADD CONSTRAINT enrollment_status_check
    CHECK (status IN ('ACTIVE', 'COMPLETED', 'DROPPED'));
