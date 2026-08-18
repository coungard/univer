-- ===================================
-- StudyYear (Курс обучения — год обучения в рамках программы)
-- ===================================
CREATE TABLE study_years (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    program_id UUID NOT NULL,
    year_number INT NOT NULL,
    FOREIGN KEY (program_id) REFERENCES programs(id) ON DELETE CASCADE,
    UNIQUE (program_id, year_number)
);

CREATE INDEX idx_study_year_program ON study_years(program_id);
