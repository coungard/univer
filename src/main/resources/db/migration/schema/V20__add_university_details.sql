-- Справочные, необязательные поля университета: ректор, год основания, число студентов.
-- Для уже существующих записей (в т.ч. засеянного ДГТУ) остаются NULL до заполнения через
-- PUT /api/v1/universities/{id}.
ALTER TABLE universities ADD COLUMN rector VARCHAR(255);
ALTER TABLE universities ADD COLUMN founding_year INT;
ALTER TABLE universities ADD COLUMN student_count INT;
