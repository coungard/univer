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

-- === StudyYear (year_number = 2 и 3 одной и той же программы «Прикладная информатика») ===
INSERT INTO study_years (id, program_id, year_number) VALUES
    ('51e2ea3f-afa3-4ac5-8ee5-0f192577cacd', '62e05b0f-08f0-434f-92fb-4a2dc1c9d779', 2), -- 17В51
    ('9c5d23fd-38a6-4b61-a2cc-5fe8d5358df8', '62e05b0f-08f0-434f-92fb-4a2dc1c9d779', 3); -- 17В42

-- === Semester (AUTUMN, осенний семестр 2026-2027 учебного года) ===
INSERT INTO semesters (id, study_year_id, type, start_date, end_date) VALUES
    ('5095c882-fe33-48a0-bc35-e96c27aff149', '51e2ea3f-afa3-4ac5-8ee5-0f192577cacd', 'AUTUMN', '2026-09-01', '2026-12-29'),
    ('5fa8a579-c958-4360-a3e0-30834e21d46f', '9c5d23fd-38a6-4b61-a2cc-5fe8d5358df8', 'AUTUMN', '2026-09-01', '2026-12-29');

-- === WeekScheduleCycle (1:1 к Semester) ===
INSERT INTO week_schedule_cycles (id, semester_id) VALUES
    ('c887a501-d586-459f-bc96-9674017099c1', '5095c882-fe33-48a0-bc35-e96c27aff149'),
    ('a5bf1d82-934c-421d-b229-5f7ff7f62ae4', '5fa8a579-c958-4360-a3e0-30834e21d46f');

-- === Group ===
INSERT INTO student_groups (id, semester_id, name) VALUES
    ('73662a3b-7870-405b-b801-b1f46b0952ea', '5095c882-fe33-48a0-bc35-e96c27aff149', '17В51'),
    ('72d97ecc-9930-4f7b-aca8-d4e2d5849e0b', '5fa8a579-c958-4360-a3e0-30834e21d46f', '17В42');

-- === Course ===
-- department_id/teacher_id оставлены NULL — преподаватель закреплён на уровне Pair, а однозначной
-- кафедры/отделения для конкретного предмета в seed-данных нет (см. подход V11 в dgtu/2026).
INSERT INTO courses (id, title, description, department_id, teacher_id) VALUES
    ('90b08200-4f06-4303-9727-d81a17f3e0d5', 'Базы данных', NULL, NULL, NULL),
    ('389a1b54-cdc8-4df9-9c4c-18ddc5a76a63', 'Статистика', NULL, NULL, NULL),
    ('80b645ff-8a37-4935-a945-b0593ee12650', 'Безопасность жизнедеятельности', NULL, NULL, NULL),
    ('0353ed70-ffb1-4770-b555-8cb8a3b49796', 'Проектирование информационных систем', NULL, NULL, NULL),
    ('50db8355-bc5c-45fe-9322-4d7fe0432a50', 'Анализ данных в Python', NULL, NULL, NULL),
    ('732cee7e-11b8-433c-9bd7-624f95035aaf', 'Оборудование и технологии аддитивного производства', NULL, NULL, NULL),
    ('b2491078-7303-4194-bd3b-35dcc598f960', 'Код ТПУ', NULL, NULL, NULL),
    ('4d812980-36a8-4083-aff4-6a00ee24cb9b', 'Физика 2.8', NULL, NULL, NULL),
    ('70e0cf39-d274-42bc-9b1e-1d4da480feec', 'Теория систем и системный анализ', NULL, NULL, NULL),
    ('9d555269-5303-466a-9593-510e4fa5fb84', 'Теория алгоритмов', NULL, NULL, NULL),
    ('df7fa1c8-4c6a-478e-90ed-77a8073f14e1', 'Психология общения (Ф)', NULL, NULL, NULL),
    ('6a38cae6-0d58-4f32-8830-57d002ba3172', 'Математика 3.3', NULL, NULL, NULL),
    ('2fbd6465-2327-46cd-81ca-cd8f58ab4bbd', 'Элективные дисциплины по физической культуре и спорту', NULL, NULL, NULL),
    ('ceb4696c-1e79-47a1-8b7e-ad2aa54c6ea6', 'Операционные системы', NULL, NULL, NULL),
    ('2d9f4df5-96a4-448b-8bd3-d05d8ee3f274', 'Физическая культура и спорт', NULL, NULL, NULL),
    ('167b7dd0-10a6-491b-b2ba-e8dd3b7da313', 'Исследование операций и методы оптимизации', NULL, NULL, NULL);

-- Нечётная неделя (31.08-06.09 на момент проверки расписания).

-- === Pairs ===

-- --- 17В42 (3 курс) ---
-- ВТОРНИК
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('410b3a93-b64f-4885-a7da-d0521ee81c07', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'TUESDAY', 'ODD', 1, '08:30', '10:05', '90b08200-4f06-4303-9727-d81a17f3e0d5', '82c263fe-a0b8-4e0d-84bd-97ae6faf2ab1', 'гл.17 КК'); -- Базы данных, Лекция
INSERT INTO pair_groups (pair_id, group_id) VALUES ('410b3a93-b64f-4885-a7da-d0521ee81c07', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('8d70ef6c-fe6c-48d8-8896-90dbdae15f46', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'TUESDAY', 'ODD', 2, '10:20', '11:55', '389a1b54-cdc8-4df9-9c4c-18ddc5a76a63', '87e39cdb-7c3b-45b8-a114-d4217cf8ae1e', 'гл.1 ЛК'); -- Статистика, Лекция
INSERT INTO pair_groups (pair_id, group_id) VALUES ('8d70ef6c-fe6c-48d8-8896-90dbdae15f46', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('e3c9f3af-04b7-4309-94cf-b4d675fdd367', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'TUESDAY', 'ODD', 3, '12:45', '14:20', '80b645ff-8a37-4935-a945-b0593ee12650', '7c055e8c-babe-481d-8e3c-89d6cb976e55', '6-25, ДМиОК'); -- Безопасность жизнедеятельности, Лекция
INSERT INTO pair_groups (pair_id, group_id) VALUES ('e3c9f3af-04b7-4309-94cf-b4d675fdd367', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('2e9d1d0d-0884-4d64-99d2-9a362cfcd1f7', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'TUESDAY', 'ODD', 4, '14:35', '16:10', '389a1b54-cdc8-4df9-9c4c-18ddc5a76a63', '87e39cdb-7c3b-45b8-a114-d4217cf8ae1e', 'гл.15 КК'); -- Статистика, Практика
INSERT INTO pair_groups (pair_id, group_id) VALUES ('2e9d1d0d-0884-4d64-99d2-9a362cfcd1f7', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
-- ЧЕТВЕРГ
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('e6faae25-eb65-441c-ad6a-5479dc0e0473', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'THURSDAY', 'ODD', 1, '08:30', '10:05', '0353ed70-ffb1-4770-b555-8cb8a3b49796', '6de4cf6f-cd53-48d4-8e74-00062d394bce', 'гл.1 ЛК'); -- Проектирование информационных систем, Лекция
INSERT INTO pair_groups (pair_id, group_id) VALUES ('e6faae25-eb65-441c-ad6a-5479dc0e0473', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('e01e192a-bc64-46cb-af9d-016e448d8f20', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'THURSDAY', 'ODD', 2, '10:20', '11:55', '90b08200-4f06-4303-9727-d81a17f3e0d5', '685c3d8f-51c0-4053-886c-afca3a1c6f55', 'гл.17 КК'); -- Базы данных, Лаб. работа (п/гр 1)
INSERT INTO pair_groups (pair_id, group_id) VALUES ('e01e192a-bc64-46cb-af9d-016e448d8f20', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('30f42740-87a4-4218-b213-0891412ecc41', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'THURSDAY', 'ODD', 3, '12:45', '14:20', '50db8355-bc5c-45fe-9322-4d7fe0432a50', 'fa672833-fae8-4d86-998d-cc5ed32bf59b', 'гл.17 КК'); -- Анализ данных в Python, Практика
INSERT INTO pair_groups (pair_id, group_id) VALUES ('30f42740-87a4-4218-b213-0891412ecc41', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('a4c4fc7f-1db9-456d-a3c0-f8433ce9027b', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'THURSDAY', 'ODD', 4, '14:35', '16:10', '732cee7e-11b8-433c-9bd7-624f95035aaf', '8610214a-a841-45c7-8b2a-09e81defa102', '6-15 КК'); -- Оборудование и технологии аддитивного производства, Лаб. работа (п/гр 1)
INSERT INTO pair_groups (pair_id, group_id) VALUES ('a4c4fc7f-1db9-456d-a3c0-f8433ce9027b', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
-- ПЯТНИЦА
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('d70dd016-bf54-4aa9-b7d4-18bc118f991a', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'FRIDAY', 'ODD', 1, '08:30', '10:05', '90b08200-4f06-4303-9727-d81a17f3e0d5', '685c3d8f-51c0-4053-886c-afca3a1c6f55', 'гл.17 КК'); -- Базы данных, Лаб. работа (п/гр 1)
INSERT INTO pair_groups (pair_id, group_id) VALUES ('d70dd016-bf54-4aa9-b7d4-18bc118f991a', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('e59d9692-6110-4c98-992b-ec6af536f467', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'FRIDAY', 'ODD', 2, '10:20', '11:55', '0353ed70-ffb1-4770-b555-8cb8a3b49796', '6de4cf6f-cd53-48d4-8e74-00062d394bce', 'гл.15 КК'); -- Проектирование информационных систем, Практика
INSERT INTO pair_groups (pair_id, group_id) VALUES ('e59d9692-6110-4c98-992b-ec6af536f467', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('1bcc8895-4be9-4a5c-bf6f-4c21eb026a93', 'a5bf1d82-934c-421d-b229-5f7ff7f62ae4', 'FRIDAY', 'ODD', 3, '12:45', '14:20', '80b645ff-8a37-4935-a945-b0593ee12650', '7c055e8c-babe-481d-8e3c-89d6cb976e55', '6-15 КК'); -- Безопасность жизнедеятельности, Практика
INSERT INTO pair_groups (pair_id, group_id) VALUES ('1bcc8895-4be9-4a5c-bf6f-4c21eb026a93', '72d97ecc-9930-4f7b-aca8-d4e2d5849e0b');

-- --- 17В51 (2 курс) ---
-- ВТОРНИК
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('43a66292-eb98-43b8-90f1-ab805d66d5c7', 'c887a501-d586-459f-bc96-9674017099c1', 'TUESDAY', 'ODD', 2, '10:20', '11:55', '4d812980-36a8-4083-aff4-6a00ee24cb9b', NULL, NULL); -- Физика 2.8, Лекция
INSERT INTO pair_groups (pair_id, group_id) VALUES ('43a66292-eb98-43b8-90f1-ab805d66d5c7', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('9e045841-8000-427c-87db-5b5880aa020b', 'c887a501-d586-459f-bc96-9674017099c1', 'TUESDAY', 'ODD', 3, '12:45', '14:20', '4d812980-36a8-4083-aff4-6a00ee24cb9b', NULL, NULL); -- Физика 2.8, Практика
INSERT INTO pair_groups (pair_id, group_id) VALUES ('9e045841-8000-427c-87db-5b5880aa020b', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('c15142ea-2e80-45b1-9787-a413b9daa89b', 'c887a501-d586-459f-bc96-9674017099c1', 'TUESDAY', 'ODD', 4, '14:35', '16:10', '70e0cf39-d274-42bc-9b1e-1d4da480feec', NULL, NULL); -- Теория систем и системный анализ, Лекция
INSERT INTO pair_groups (pair_id, group_id) VALUES ('c15142ea-2e80-45b1-9787-a413b9daa89b', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('7a7bc410-ac0a-4894-99fe-8334954d8f80', 'c887a501-d586-459f-bc96-9674017099c1', 'TUESDAY', 'ODD', 5, '16:20', '17:55', '9d555269-5303-466a-9593-510e4fa5fb84', NULL, NULL); -- Теория алгоритмов, Лаб. работа (п/гр 1)
INSERT INTO pair_groups (pair_id, group_id) VALUES ('7a7bc410-ac0a-4894-99fe-8334954d8f80', '73662a3b-7870-405b-b801-b1f46b0952ea');
-- СРЕДА
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('3c8a699f-4ad1-4655-b4c2-2498c93b9742', 'c887a501-d586-459f-bc96-9674017099c1', 'WEDNESDAY', 'ODD', 1, '08:30', '10:05', 'df7fa1c8-4c6a-478e-90ed-77a8073f14e1', NULL, NULL); -- Психология общения (Ф), Лекция
INSERT INTO pair_groups (pair_id, group_id) VALUES ('3c8a699f-4ad1-4655-b4c2-2498c93b9742', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('24edaffd-cd47-4bf9-a6c6-91ade5c218ea', 'c887a501-d586-459f-bc96-9674017099c1', 'WEDNESDAY', 'ODD', 2, '10:20', '11:55', 'b2491078-7303-4194-bd3b-35dcc598f960', NULL, NULL); -- Код ТПУ, Практика
INSERT INTO pair_groups (pair_id, group_id) VALUES ('24edaffd-cd47-4bf9-a6c6-91ade5c218ea', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('b236a144-f1fc-4b84-80d4-fad46b782442', 'c887a501-d586-459f-bc96-9674017099c1', 'WEDNESDAY', 'ODD', 3, '12:45', '14:20', '9d555269-5303-466a-9593-510e4fa5fb84', NULL, NULL); -- Теория алгоритмов, Практика
INSERT INTO pair_groups (pair_id, group_id) VALUES ('b236a144-f1fc-4b84-80d4-fad46b782442', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('1f20de6d-d375-41f2-960d-6f8b41335d70', 'c887a501-d586-459f-bc96-9674017099c1', 'WEDNESDAY', 'ODD', 4, '14:35', '16:10', '6a38cae6-0d58-4f32-8830-57d002ba3172', NULL, NULL); -- Математика 3.3, Лекция
INSERT INTO pair_groups (pair_id, group_id) VALUES ('1f20de6d-d375-41f2-960d-6f8b41335d70', '73662a3b-7870-405b-b801-b1f46b0952ea');
-- ЧЕТВЕРГ
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('8a9c93d1-b807-4ba9-b09c-7e32e5ad9701', 'c887a501-d586-459f-bc96-9674017099c1', 'THURSDAY', 'ODD', 1, '08:30', '10:05', '9d555269-5303-466a-9593-510e4fa5fb84', NULL, NULL); -- Теория алгоритмов, Лаб. работа (п/гр 2)
INSERT INTO pair_groups (pair_id, group_id) VALUES ('8a9c93d1-b807-4ba9-b09c-7e32e5ad9701', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('d36aa45f-bf6d-4309-8da9-d3f79a1821ea', 'c887a501-d586-459f-bc96-9674017099c1', 'THURSDAY', 'ODD', 2, '10:20', '11:55', '4d812980-36a8-4083-aff4-6a00ee24cb9b', NULL, NULL); -- Физика 2.8, Лекция
INSERT INTO pair_groups (pair_id, group_id) VALUES ('d36aa45f-bf6d-4309-8da9-d3f79a1821ea', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('13a10d4c-bd32-4efc-a2a5-95b79093743c', 'c887a501-d586-459f-bc96-9674017099c1', 'THURSDAY', 'ODD', 3, '12:45', '14:20', '2fbd6465-2327-46cd-81ca-cd8f58ab4bbd', NULL, NULL); -- Элективные дисциплины по физической культуре и спорту, Практика (п/гр 1)
INSERT INTO pair_groups (pair_id, group_id) VALUES ('13a10d4c-bd32-4efc-a2a5-95b79093743c', '73662a3b-7870-405b-b801-b1f46b0952ea');
-- ПЯТНИЦА
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('8a5620ad-7f2d-4d13-bc45-d9e71e8c37b7', 'c887a501-d586-459f-bc96-9674017099c1', 'FRIDAY', 'ODD', 2, '10:20', '11:55', '2fbd6465-2327-46cd-81ca-cd8f58ab4bbd', NULL, NULL); -- Элективные дисциплины по физической культуре и спорту, Практика (п/гр 1)
INSERT INTO pair_groups (pair_id, group_id) VALUES ('8a5620ad-7f2d-4d13-bc45-d9e71e8c37b7', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('32c5dc83-cf89-4acb-b6ea-4820829eb310', 'c887a501-d586-459f-bc96-9674017099c1', 'FRIDAY', 'ODD', 3, '12:45', '14:20', '4d812980-36a8-4083-aff4-6a00ee24cb9b', NULL, NULL); -- Физика 2.8, Лаб. работа
INSERT INTO pair_groups (pair_id, group_id) VALUES ('32c5dc83-cf89-4acb-b6ea-4820829eb310', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('6c93c129-7c2e-4edf-9435-12858d82b5e3', 'c887a501-d586-459f-bc96-9674017099c1', 'FRIDAY', 'ODD', 4, '14:35', '16:10', '6a38cae6-0d58-4f32-8830-57d002ba3172', NULL, NULL); -- Математика 3.3, Лекция
INSERT INTO pair_groups (pair_id, group_id) VALUES ('6c93c129-7c2e-4edf-9435-12858d82b5e3', '73662a3b-7870-405b-b801-b1f46b0952ea');
-- СУББОТА
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('e604bfda-5f71-4a69-b8d0-4e91b7fb553f', 'c887a501-d586-459f-bc96-9674017099c1', 'SATURDAY', 'ODD', 1, '08:30', '10:05', 'ceb4696c-1e79-47a1-8b7e-ad2aa54c6ea6', NULL, NULL); -- Операционные системы, Практика
INSERT INTO pair_groups (pair_id, group_id) VALUES ('e604bfda-5f71-4a69-b8d0-4e91b7fb553f', '73662a3b-7870-405b-b801-b1f46b0952ea');
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('d3c30cf9-3ca9-40e5-8f58-b863013a1684', 'c887a501-d586-459f-bc96-9674017099c1', 'SATURDAY', 'ODD', 2, '10:20', '11:55', '6a38cae6-0d58-4f32-8830-57d002ba3172', NULL, NULL); -- Математика 3.3, Практика
INSERT INTO pair_groups (pair_id, group_id) VALUES ('d3c30cf9-3ca9-40e5-8f58-b863013a1684', '73662a3b-7870-405b-b801-b1f46b0952ea');
