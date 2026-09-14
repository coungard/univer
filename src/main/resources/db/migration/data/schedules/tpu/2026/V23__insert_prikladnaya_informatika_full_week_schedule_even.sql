-- Расписание направления «Прикладная информатика» (09.03.03) ЮТИ ТПУ (Юргинский технологический
-- институт, филиал ТПУ) на осенний семестр 2026-2027 учебного года — группы 17В42 (3 курс) и 17В51
-- (2 курс), см. explore/11-tpu.md ("Расписание"). В БД нет ни групп, ни курсов ТПУ — создаём всю
-- цепочку StudyYear -> Semester -> Group -> WeekScheduleCycle -> Course -> Pair с нуля (программа и
-- преподаватели уже заведены в V21__insert_tpu_data.sql).
--
-- Файлы odd/even (эта миграция и парная к ней) — не два диапазона недель, а чередование нечётных и
-- чётных недель семестра (WeekParity.ODD/EVEN), см. правило в schedules/SCHEDULES_RULES.md.
--
-- 17В51 реально учится по субботам — CHECK на pairs.day_of_week уже разрешает SATURDAY/SUNDAY
-- (schema/V20__allow_weekend_pairs.sql), отдельная миграция под ТПУ не нужна.
--
-- Сайт uti.tpu.ru/timetable/ не показывает преподавателей и аудитории для группы 17В51 (в отличие от
-- 17В42) — Pair.teacher_id/room оставлены NULL для всех её пар, это не пробел переноса, а то, что
-- реально опубликовано.

-- study_years/semesters/week_schedule_cycles/student_groups/courses уже заведены в
-- V22__insert_prikladnaya_informatika_full_week_schedule.sql — переиспользуются как есть, эта
-- миграция добавляет только Pair/pair_groups для чётной недели.

-- Чётная неделя (07.09-13.09 на момент проверки расписания).

-- === Pairs ===

-- --- 17В42 (3 курс) ---
-- ПОНЕДЕЛЬНИК
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('8017e79b-f7f3-4cef-8873-01afb2b39194', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'MONDAY', 'EVEN', 3, '12:45', '14:20', '732cee7e-11b8-433c-9bd7-624f95035aaf', 'f1745e86-cf73-44db-8fc6-3cc278c40688', '3-31 ЛК'); -- Оборудование и технологии аддитивного производства, Лекция
INSERT INTO pair_groups (pair_id, group_id) VALUES ('8017e79b-f7f3-4cef-8873-01afb2b39194', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('def10432-b205-44da-a8c3-de21967b418e', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'MONDAY', 'EVEN', 4, '14:35', '16:10', '90b08200-4f06-4303-9727-d81a17f3e0d5', '82c263fe-a0b8-4e0d-84bd-97ae6faf2ab1', 'гл.17 КК'); -- Базы данных, Лекция
INSERT INTO pair_groups (pair_id, group_id) VALUES ('def10432-b205-44da-a8c3-de21967b418e', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
-- ВТОРНИК
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('f47cf066-86d8-4406-bec2-828fd37dfdf4', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'TUESDAY', 'EVEN', 1, '08:30', '10:05', '0353ed70-ffb1-4770-b555-8cb8a3b49796', '6de4cf6f-cd53-48d4-8e74-00062d394bce', 'гл.17 КК'); -- Проектирование информационных систем, Лаб. работа (п/гр 1)
INSERT INTO pair_groups (pair_id, group_id) VALUES ('f47cf066-86d8-4406-bec2-828fd37dfdf4', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('859d3331-b1fb-454b-839f-eea2f356b8e7', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'TUESDAY', 'EVEN', 2, '10:20', '11:55', '0353ed70-ffb1-4770-b555-8cb8a3b49796', '6de4cf6f-cd53-48d4-8e74-00062d394bce', 'гл.20 ЛК'); -- Проектирование информационных систем, Лекция
INSERT INTO pair_groups (pair_id, group_id) VALUES ('859d3331-b1fb-454b-839f-eea2f356b8e7', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
-- СРЕДА
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('207c0101-1be5-456b-a984-04f591db08f9', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'WEDNESDAY', 'EVEN', 2, '10:20', '11:55', '90b08200-4f06-4303-9727-d81a17f3e0d5', '685c3d8f-51c0-4053-886c-afca3a1c6f55', 'гл.17 КК'); -- Базы данных, Лаб. работа (п/гр 1)
INSERT INTO pair_groups (pair_id, group_id) VALUES ('207c0101-1be5-456b-a984-04f591db08f9', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('72f75405-a79a-4e7b-a34e-0d6c5904eef4', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'WEDNESDAY', 'EVEN', 3, '12:45', '14:20', '389a1b54-cdc8-4df9-9c4c-18ddc5a76a63', '87e39cdb-7c3b-45b8-a114-d4217cf8ae1e', 'гл.10 КК'); -- Статистика, Лаб. работа (п/гр 1)
INSERT INTO pair_groups (pair_id, group_id) VALUES ('72f75405-a79a-4e7b-a34e-0d6c5904eef4', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
-- ЧЕТВЕРГ
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('f6477cf9-48f2-405e-9482-5003b501c804', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'THURSDAY', 'EVEN', 1, '08:30', '10:05', '389a1b54-cdc8-4df9-9c4c-18ddc5a76a63', '87e39cdb-7c3b-45b8-a114-d4217cf8ae1e', 'гл.15 КК'); -- Статистика, Практика
INSERT INTO pair_groups (pair_id, group_id) VALUES ('f6477cf9-48f2-405e-9482-5003b501c804', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('db65aa6c-bd26-4160-a879-28fd1d86217a', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'THURSDAY', 'EVEN', 2, '10:20', '11:55', '0353ed70-ffb1-4770-b555-8cb8a3b49796', '6de4cf6f-cd53-48d4-8e74-00062d394bce', 'гл.15 КК'); -- Проектирование информационных систем, Лаб. работа
INSERT INTO pair_groups (pair_id, group_id) VALUES ('db65aa6c-bd26-4160-a879-28fd1d86217a', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
-- ПЯТНИЦА
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('c24faa62-eeff-4092-b6d4-6488ec3b0669', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'FRIDAY', 'EVEN', 1, '08:30', '10:05', 'b2491078-7303-4194-bd3b-35dcc598f960', '82c263fe-a0b8-4e0d-84bd-97ae6faf2ab1', 'гл.11 ЛК'); -- Код ТПУ, Практика
INSERT INTO pair_groups (pair_id, group_id) VALUES ('c24faa62-eeff-4092-b6d4-6488ec3b0669', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('14f3aa6a-ec19-4233-84c9-6fcad83c4a0f', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'FRIDAY', 'EVEN', 2, '10:20', '11:55', '80b645ff-8a37-4935-a945-b0593ee12650', '7c055e8c-babe-481d-8e3c-89d6cb976e55', '6-15 КК'); -- Безопасность жизнедеятельности, Лаб. работа
INSERT INTO pair_groups (pair_id, group_id) VALUES ('14f3aa6a-ec19-4233-84c9-6fcad83c4a0f', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('90d3e7f3-efd4-45b9-9c3e-15d83fd52815', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'FRIDAY', 'EVEN', 3, '12:45', '14:20', '50db8355-bc5c-45fe-9322-4d7fe0432a50', 'fa672833-fae8-4d86-998d-cc5ed32bf59b', 'гл.17 КК'); -- Анализ данных в Python, Лекция
INSERT INTO pair_groups (pair_id, group_id) VALUES ('90d3e7f3-efd4-45b9-9c3e-15d83fd52815', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');

-- --- 17В51 (2 курс) ---
-- ПОНЕДЕЛЬНИК
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('85fad962-d5be-4d4e-8bcb-1abfe457bda8', 'c887a501-d586-459f-bc96-9674017099c1', 'MONDAY', 'EVEN', 1, '08:30', '10:05', 'df7fa1c8-4c6a-478e-90ed-77a8073f14e1', NULL, NULL); -- Психология общения (Ф), Практика
INSERT INTO pair_groups (pair_id, group_id) VALUES ('85fad962-d5be-4d4e-8bcb-1abfe457bda8', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('970ba905-b478-403a-bbf6-b1a29fa3eb30', 'c887a501-d586-459f-bc96-9674017099c1', 'MONDAY', 'EVEN', 2, '10:20', '11:55', '6a38cae6-0d58-4f32-8830-57d002ba3172', NULL, NULL); -- Математика 3.3, Лекция
INSERT INTO pair_groups (pair_id, group_id) VALUES ('970ba905-b478-403a-bbf6-b1a29fa3eb30', '73662a3b-7870-405b-b801-b1f46b0952ea');
-- ВТОРНИК
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('28bf9def-21c1-4fea-90d8-2e6a7543d591', 'c887a501-d586-459f-bc96-9674017099c1', 'TUESDAY', 'EVEN', 2, '10:20', '11:55', '6a38cae6-0d58-4f32-8830-57d002ba3172', NULL, NULL); -- Математика 3.3, Практика
INSERT INTO pair_groups (pair_id, group_id) VALUES ('28bf9def-21c1-4fea-90d8-2e6a7543d591', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('c6623943-d61c-46e1-b13f-257681be4158', 'c887a501-d586-459f-bc96-9674017099c1', 'TUESDAY', 'EVEN', 3, '12:45', '14:20', '2fbd6465-2327-46cd-81ca-cd8f58ab4bbd', NULL, NULL); -- Элективные дисциплины по физической культуре и спорту, Практика (п/гр 1)
INSERT INTO pair_groups (pair_id, group_id) VALUES ('c6623943-d61c-46e1-b13f-257681be4158', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('b475a28e-f0dd-4808-96f4-3fbc0ef753bc', 'c887a501-d586-459f-bc96-9674017099c1', 'TUESDAY', 'EVEN', 4, '14:35', '16:10', '2d9f4df5-96a4-448b-8bd3-d05d8ee3f274', NULL, NULL); -- Физическая культура и спорт, Лекция
INSERT INTO pair_groups (pair_id, group_id) VALUES ('b475a28e-f0dd-4808-96f4-3fbc0ef753bc', '73662a3b-7870-405b-b801-b1f46b0952ea');
-- СРЕДА
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('980dd1fc-2c44-45ba-a8d2-f298ac86493a', 'c887a501-d586-459f-bc96-9674017099c1', 'WEDNESDAY', 'EVEN', 5, '16:20', '17:55', '70e0cf39-d274-42bc-9b1e-1d4da480feec', NULL, NULL); -- Теория систем и системный анализ, Практика (п/гр 1)
INSERT INTO pair_groups (pair_id, group_id) VALUES ('980dd1fc-2c44-45ba-a8d2-f298ac86493a', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('6cd53914-831a-4443-9a57-42a6e6493432', 'c887a501-d586-459f-bc96-9674017099c1', 'WEDNESDAY', 'EVEN', 6, '18:05', '19:40', 'ceb4696c-1e79-47a1-8b7e-ad2aa54c6ea6', NULL, NULL); -- Операционные системы, Лаб. работа (п/гр 1)
INSERT INTO pair_groups (pair_id, group_id) VALUES ('6cd53914-831a-4443-9a57-42a6e6493432', '73662a3b-7870-405b-b801-b1f46b0952ea');
-- ПЯТНИЦА
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('d18efb3c-f220-4a25-9022-dc7dbf8fc7f7', 'c887a501-d586-459f-bc96-9674017099c1', 'FRIDAY', 'EVEN', 2, '10:20', '11:55', '167b7dd0-10a6-491b-b2ba-e8dd3b7da313', NULL, NULL); -- Исследование операций и методы оптимизации, Лекция
INSERT INTO pair_groups (pair_id, group_id) VALUES ('d18efb3c-f220-4a25-9022-dc7dbf8fc7f7', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('70c554e8-7dce-48f5-9b40-718c1f782c5a', 'c887a501-d586-459f-bc96-9674017099c1', 'FRIDAY', 'EVEN', 3, '12:45', '14:20', '167b7dd0-10a6-491b-b2ba-e8dd3b7da313', NULL, NULL); -- Исследование операций и методы оптимизации, Практика
INSERT INTO pair_groups (pair_id, group_id) VALUES ('70c554e8-7dce-48f5-9b40-718c1f782c5a', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('8189bdee-af04-440e-8463-d1a82526305b', 'c887a501-d586-459f-bc96-9674017099c1', 'FRIDAY', 'EVEN', 4, '14:35', '16:10', '167b7dd0-10a6-491b-b2ba-e8dd3b7da313', NULL, NULL); -- Исследование операций и методы оптимизации, Практика
INSERT INTO pair_groups (pair_id, group_id) VALUES ('8189bdee-af04-440e-8463-d1a82526305b', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('3cc675bc-5453-42ae-8709-72b7cf9f3608', 'c887a501-d586-459f-bc96-9674017099c1', 'FRIDAY', 'EVEN', 5, '16:20', '17:55', '6a38cae6-0d58-4f32-8830-57d002ba3172', NULL, NULL); -- Математика 3.3, Практика
INSERT INTO pair_groups (pair_id, group_id) VALUES ('3cc675bc-5453-42ae-8709-72b7cf9f3608', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('9571f764-37c5-46c1-bb0f-3e5833fa5504', 'c887a501-d586-459f-bc96-9674017099c1', 'FRIDAY', 'EVEN', 6, '18:05', '19:40', 'ceb4696c-1e79-47a1-8b7e-ad2aa54c6ea6', NULL, NULL); -- Операционные системы, Лекция
INSERT INTO pair_groups (pair_id, group_id) VALUES ('9571f764-37c5-46c1-bb0f-3e5833fa5504', '73662a3b-7870-405b-b801-b1f46b0952ea');
-- СУББОТА
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('17cb80ef-1f23-4476-864b-3a6df7db99ba', 'c887a501-d586-459f-bc96-9674017099c1', 'SATURDAY', 'EVEN', 1, '08:30', '10:05', 'ceb4696c-1e79-47a1-8b7e-ad2aa54c6ea6', NULL, NULL); -- Операционные системы, Лаб. работа (п/гр 2)
INSERT INTO pair_groups (pair_id, group_id) VALUES ('17cb80ef-1f23-4476-864b-3a6df7db99ba', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('64e9fccf-30ab-495d-b2cd-1c80ee694bc0', 'c887a501-d586-459f-bc96-9674017099c1', 'SATURDAY', 'EVEN', 2, '10:20', '11:55', '2fbd6465-2327-46cd-81ca-cd8f58ab4bbd', NULL, NULL); -- Элективные дисциплины по физической культуре и спорту, Практика (п/гр 1)
INSERT INTO pair_groups (pair_id, group_id) VALUES ('64e9fccf-30ab-495d-b2cd-1c80ee694bc0', '73662a3b-7870-405b-b801-b1f46b0952ea');
