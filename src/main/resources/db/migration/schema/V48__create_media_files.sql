-- Метаданные медиа-файлов (issue #137, аналитика — #135). Сами файлы лежат в хранилище (StorageService,
-- сейчас локальный диск), здесь — только ключ объекта, а не URL и не путь: ссылку для клиента строит
-- приложение из media.public-base-url, поэтому переезд хранилища не требует миграции данных.
CREATE TABLE media_files (
    id UUID PRIMARY KEY,
    -- Назначение файла; пока только AVATAR (позже — вложения отзывов, материалы курсов).
    purpose VARCHAR(32) NOT NULL CHECK (purpose IN ('AVATAR')),
    -- Ключ основного объекта. У аватара это avatars/{id}/512.jpg, рядом лежит превью avatars/{id}/128.jpg.
    storage_key VARCHAR(512) NOT NULL UNIQUE,
    content_type VARCHAR(100) NOT NULL,
    size_bytes BIGINT NOT NULL,
    -- Имя файла у клиента — только для справки администратору.
    original_name VARCHAR(255),
    -- Кто загрузил: subject JWT. Без FK — загрузить файл может и администратор, которого нет в persons.
    owner_id UUID NOT NULL,
    -- READY — файл записан и готов; PENDING — для будущих загрузок клиентом напрямую в хранилище.
    status VARCHAR(16) NOT NULL CHECK (status IN ('PENDING', 'READY')),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID
);

-- Уборка незавершённых загрузок: PENDING старше суток.
CREATE INDEX idx_media_files_status_created ON media_files(status, created_at);

-- Аватар человека (студента или преподавателя). Файл удаляется приложением явно вместе с профилем — без
-- ON DELETE, как и остальные связи (AccountServiceImpl).
ALTER TABLE persons ADD COLUMN avatar_file_id UUID REFERENCES media_files(id);
