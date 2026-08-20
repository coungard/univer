-- ===================================
-- Semester (Семестр — осенний/весенний в рамках курса обучения)
-- ===================================
CREATE TABLE semesters (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    study_year_id UUID NOT NULL,
    type VARCHAR(16) NOT NULL CHECK (type IN ('AUTUMN', 'SPRING')),
    start_date DATE NOT NULL,
    FOREIGN KEY (study_year_id) REFERENCES study_years(id) ON DELETE CASCADE
);

CREATE INDEX idx_semester_study_year ON semesters(study_year_id);
