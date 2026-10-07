-- Справочник субъектов РФ и привязка университета к региону (issue #80).

-- ===================================
-- Region (Субъект РФ)
-- ===================================
CREATE TABLE regions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    -- Двузначный код субъекта РФ ('05' — Дагестан, '77' — Москва).
    code VARCHAR(2) NOT NULL,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT uq_regions_code UNIQUE (code),
    CONSTRAINT uq_regions_name UNIQUE (name)
);

-- Пока nullable: у вузов без адреса (address_id IS NULL, см. V18/V19) регион ещё не определён.
ALTER TABLE universities
    ADD COLUMN region_id UUID,
    ADD CONSTRAINT fk_university_region FOREIGN KEY (region_id) REFERENCES regions(id);

CREATE INDEX idx_universities_region_id ON universities(region_id);
