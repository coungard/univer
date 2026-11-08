-- Удаление аккаунта пользователем (issue #133): его отзывы не удаляются, а обезличиваются — user_id,
-- username и created_by обнуляются, текст и статус остаются для разбора. Поэтому user_id может быть NULL.
ALTER TABLE feedback ALTER COLUMN user_id DROP NOT NULL;
