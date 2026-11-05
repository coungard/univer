-- Отзывы из мобильного приложения (issue #130): ошибка, предложение по доработке или идея.
--
-- Хранятся только здесь — никуда не пересылаются. Автор берётся из токена: user_id — subject JWT, ссылок
-- на students/teachers нет намеренно, отзыв может оставить и администратор, которого в этих таблицах нет.
-- username — снимок preferred_username на момент отправки: администратору не нужно искать автора по id, и
-- запись остаётся читаемой после удаления пользователя.
CREATE TABLE feedback (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    -- Старшая роль автора на момент отправки: ADMIN / TEACHER / STUDENT.
    role VARCHAR(16) NOT NULL,
    username VARCHAR(255),
    type VARCHAR(16) NOT NULL CHECK (type IN ('BUG', 'IMPROVEMENT', 'IDEA')),
    message TEXT NOT NULL,
    app_version VARCHAR(32),
    platform VARCHAR(16) CHECK (platform IN ('IOS', 'ANDROID')),
    os_version VARCHAR(32),
    -- NEW → IN_PROGRESS → DONE | REJECTED; меняет только администратор.
    status VARCHAR(16) NOT NULL DEFAULT 'NEW' CHECK (status IN ('NEW', 'IN_PROGRESS', 'DONE', 'REJECTED')),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID
);

-- Список для администратора: фильтр по статусу, новые сверху.
CREATE INDEX idx_feedback_status_created ON feedback(status, created_at DESC);

-- Ограничение частоты: сколько отзывов пользователь оставил за последний час.
CREATE INDEX idx_feedback_user_created ON feedback(user_id, created_at);
