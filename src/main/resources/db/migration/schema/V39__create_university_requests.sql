-- Заявка студента на добавление университета (issue #92).
--
-- Студент, не нашедший свой университет в справочнике, оставляет заявку: название и, если выбран,
-- регион. Администратор добавляет университет и закрывает заявку — университет проставляется в
-- профиль автора. Сам студент университеты не создаёт (issue #88 отменена).
CREATE TABLE university_requests (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    -- Регион, выбранный студентом на шаге «Регион». Может быть пуст: студент мог не найти и регион —
    -- тогда регион университета администратор указывает сам.
    region_id UUID,
    -- PENDING (необработанная) / COMPLETED (выполнена, university_id заполнен) / REJECTED (отклонена).
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'REJECTED')),
    -- Университет, которым заявка закрыта: добавленный администратором либо выбранный самим студентом,
    -- пока заявка ждала.
    university_id UUID,
    -- Необязательное пояснение администратора при отклонении: студент видит, что исправить.
    comment VARCHAR(500),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP,
    -- ON DELETE CASCADE: заявка без автора никому не нужна.
    CONSTRAINT fk_university_request_student
        FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_university_request_region
        FOREIGN KEY (region_id) REFERENCES regions(id) ON DELETE SET NULL,
    -- ON DELETE SET NULL: удаление университета не должно стирать историю заявок.
    CONSTRAINT fk_university_request_university
        FOREIGN KEY (university_id) REFERENCES universities(id) ON DELETE SET NULL
);

CREATE INDEX idx_university_request_status ON university_requests(status, created_at);

-- У студента не больше одной необработанной заявки: повторная отправка обновляет её. Сервис проверяет
-- это сам; индекс страхует от гонки двух одновременных запросов.
CREATE UNIQUE INDEX uq_university_request_pending_student
    ON university_requests(student_id) WHERE status = 'PENDING';

CREATE INDEX idx_university_request_student ON university_requests(student_id, created_at);
