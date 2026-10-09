-- Создание группы студентом (issue #86): группа помнит, какой студент её создал. По этому полю
-- считается лимит групп на одного студента и проверяется право исправить название своей группы.
-- У групп, заведённых администратором или seed-миграциями, поле пустое.
--
-- ON DELETE SET NULL: удаление студента не должно удалять группу, в которой уже могут быть другие.
ALTER TABLE student_groups ADD COLUMN created_by_student_id UUID;
ALTER TABLE student_groups ADD CONSTRAINT fk_student_group_created_by_student
    FOREIGN KEY (created_by_student_id) REFERENCES students(id) ON DELETE SET NULL;

CREATE INDEX idx_student_group_created_by_student ON student_groups(created_by_student_id);
