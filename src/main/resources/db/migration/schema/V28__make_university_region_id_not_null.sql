-- Регион университета становится обязательным (issue #80): после V25-V27 он проставлен у всех вузов.
ALTER TABLE universities ALTER COLUMN region_id SET NOT NULL;
