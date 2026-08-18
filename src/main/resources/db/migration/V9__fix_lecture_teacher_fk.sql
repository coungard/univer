-- ===================================
-- Lecture: исправить FK teacher_id (ссылался на persons вместо teachers)
--
-- Обнаружено при реализации Lecture (issue #30), тот же класс бага, что уже
-- исправлен для courses.teacher_id (V7): Lecture.teacher — это Teacher, но
-- Teacher.id (полученный из Keycloak) не совпадает с Person.id, поэтому
-- исходный FK на persons(id) отвергал бы любой валидный teacher_id в реальной
-- БД (тесты не ловили — используют hibernate.ddl-auto=create-drop).
-- ===================================
ALTER TABLE lectures DROP CONSTRAINT lectures_teacher_id_fkey;
ALTER TABLE lectures ADD CONSTRAINT fk_lecture_teacher
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE SET NULL;
