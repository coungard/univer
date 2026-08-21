-- Реальное звонковое расписание ДГТУ (родитель ФКТиЭ/КТиЭ, university_id из
-- V2__insert_universities_data.sql), 6 пар. Закрывает пункт 3 из «Структурных пробелов модели» в
-- schedules/ktie_kurs1_nedeli1-3.txt — время Сценария 3 (08:00-09:30) было иллюстративным
-- предположением, здесь подставлены подтверждённые реальные времена звонков.
INSERT INTO bell_schedule_entries (id, university_id, pair_number, start_time, end_time, created_at, updated_at) VALUES
    ('6a6645a5-543c-4673-b922-4a350385a2b2', '796c5ad4-52ba-482f-a3bb-31c5de38762d', 1, '08:30', '10:00', now(), now()),
    ('c2f85672-6022-4cc1-96a9-a0ccb594f54e', '796c5ad4-52ba-482f-a3bb-31c5de38762d', 2, '10:10', '11:40', now(), now()),
    ('7f0b7115-18c2-40a5-8b01-fddbb8298f4b', '796c5ad4-52ba-482f-a3bb-31c5de38762d', 3, '12:20', '13:50', now(), now()),
    ('508c816a-5e13-4ab0-b86b-32dd02cab078', '796c5ad4-52ba-482f-a3bb-31c5de38762d', 4, '14:00', '15:30', now(), now()),
    ('564b0b16-75bf-4af2-b69c-bcb24b12b013', '796c5ad4-52ba-482f-a3bb-31c5de38762d', 5, '15:40', '17:10', now(), now()),
    ('ea36917e-bdd5-4e58-9d75-38168ae2beaa', '796c5ad4-52ba-482f-a3bb-31c5de38762d', 6, '17:20', '18:50', now(), now());
