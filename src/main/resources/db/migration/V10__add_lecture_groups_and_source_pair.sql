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
-- Lecture: опциональная ссылка на Pair-шаблон, из которого лекция сгенерирована
-- (в дополнение к ручному созданию — см. issue #30)
-- ===================================
ALTER TABLE lectures ADD COLUMN source_pair_id UUID;
ALTER TABLE lectures ADD CONSTRAINT fk_lecture_source_pair
    FOREIGN KEY (source_pair_id) REFERENCES pairs(id) ON DELETE SET NULL;

CREATE INDEX idx_lecture_source_pair ON lectures(source_pair_id);
