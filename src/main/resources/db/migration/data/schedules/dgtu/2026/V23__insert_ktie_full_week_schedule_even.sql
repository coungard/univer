-- Дубликат V16__insert_ktie_full_week_schedule_even.sql (dgtu/2025) для учебного года 2026-2027 —
-- чётные недели (2, 4), пара к V22__insert_ktie_full_week_schedule.sql (нечётные недели, week_parity
-- = ODD) на те же StudyYear/Semester/Group/WeekScheduleCycle 2026-2027 года (не создаются заново).
-- Programs/Teachers/Courses переиспользуются как есть (те же UUID, что и в 2025/V15-V16, включая 2
-- курса «Инф.-обр. занятие», заведённых в 2025/V16) — предметы/преподаватели не меняются год от
-- года, меняются только Pair, у которых здесь заново сгенерированы UUID.
--
-- Все смысловые решения идентичны V16 (dgtu/2025) — см. полные комментарии там, здесь не
-- дублируются.

-- =====================================================================================
-- ПОНЕДЕЛЬНИК
-- =====================================================================================

-- Пара 1 (08:30-10:00) и Пара 2 (10:10-11:40): в источнике одинаковое содержимое повторено дважды
-- (сдвоенное занятие) — 2 набора Pair-строк с одинаковыми course/teacher/room, как «Пара 3-4» в V15.
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('d7eabc04-e8ac-4eb9-a753-12ea6cb0378b', '1c35ef07-28ef-4aa0-b52e-9fe10d560efb', 'MONDAY', 'EVEN', 1, '08:30', '10:00', '93980989-55a4-4036-b206-44fbf44db8fc', 'd05c213f-efcf-4327-9e1c-65455fa68845', '548'), -- У532 История России, Казакбиева О.И.
    ('edf7933d-fa13-44a9-b3f8-f135704bce78', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'MONDAY', 'EVEN', 1, '08:30', '10:00', 'f3b7c4fb-8fda-4512-9f0f-f6728f23c00f', '060282dc-b717-441d-84a4-3e3a01903ee3', '555'), -- У533 Программирование, Денгаев А.М.
    ('ca62f480-2175-4fd3-8021-1055c74f3ac4', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'MONDAY', 'EVEN', 1, '08:30', '10:00', 'd2c73633-f7ae-402b-81ec-f61c3ccf06ea', '5bd2bba5-03bf-492c-a224-0edb7a04f200', 'Зал 8'), -- У530 Введение в программирование на языке Python, Айгумов Т.Г.
    ('0cb00967-0e9f-4cf8-9418-0a15b4a80349', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'MONDAY', 'EVEN', 1, '08:30', '10:00', '9bd3db49-891a-478b-8906-0fe055ce6568', 'c4fb96fb-7d7f-456b-bc5e-72240ec0b1ff', '350'), -- У534 Введение в программирование, Савзиханова С.Э.
    ('5c3d9367-8c54-40c8-ade8-7b7aa55e0ccd', '1c35ef07-28ef-4aa0-b52e-9fe10d560efb', 'MONDAY', 'EVEN', 2, '10:10', '11:40', '93980989-55a4-4036-b206-44fbf44db8fc', 'd05c213f-efcf-4327-9e1c-65455fa68845', '548'),
    ('4fd9a563-b454-4032-b6a7-82a75976f87a', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'MONDAY', 'EVEN', 2, '10:10', '11:40', 'f3b7c4fb-8fda-4512-9f0f-f6728f23c00f', '060282dc-b717-441d-84a4-3e3a01903ee3', '555'),
    ('5d4d0d5d-7141-4fa4-8cd9-ebd7d3476df8', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'MONDAY', 'EVEN', 2, '10:10', '11:40', 'd2c73633-f7ae-402b-81ec-f61c3ccf06ea', '5bd2bba5-03bf-492c-a224-0edb7a04f200', 'Зал 8'),
    ('dbcfb332-6049-43f0-b22a-109b1ba47b2a', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'MONDAY', 'EVEN', 2, '10:10', '11:40', '9bd3db49-891a-478b-8906-0fe055ce6568', 'c4fb96fb-7d7f-456b-bc5e-72240ec0b1ff', '350');

INSERT INTO pair_groups (pair_id, group_id) VALUES
    ('d7eabc04-e8ac-4eb9-a753-12ea6cb0378b', '0ded6e78-f5f7-4184-ab25-10aa91030d8e'),
    ('edf7933d-fa13-44a9-b3f8-f135704bce78', '11c4f49a-af05-4ce5-bd17-118909fee773'),
    ('ca62f480-2175-4fd3-8021-1055c74f3ac4', 'becfddf9-63b2-4443-b60f-c163b7f263fb'),
    ('0cb00967-0e9f-4cf8-9418-0a15b4a80349', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122'),
    ('5c3d9367-8c54-40c8-ade8-7b7aa55e0ccd', '0ded6e78-f5f7-4184-ab25-10aa91030d8e'),
    ('4fd9a563-b454-4032-b6a7-82a75976f87a', '11c4f49a-af05-4ce5-bd17-118909fee773'),
    ('5d4d0d5d-7141-4fa4-8cd9-ebd7d3476df8', 'becfddf9-63b2-4443-b60f-c163b7f263fb'),
    ('dbcfb332-6049-43f0-b22a-109b1ba47b2a', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122');

-- Пара 3 (12:20-13:50) и Пара 4 (14:00-15:30): снова сдвоенное занятие, одинаковое содержимое дважды.
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('db0a5e86-5e15-45ed-bd53-5e4394cb2e64', '1c35ef07-28ef-4aa0-b52e-9fe10d560efb', 'MONDAY', 'EVEN', 3, '12:20', '13:50', '207bd4a8-ab29-454d-9c63-d0339ad3747c', '08b5a8aa-e59b-4183-8d4d-09c67d45b482', '542'), -- У532 Информатика, Эседова Г.С.
    ('fdc606c9-8083-4d77-a3ee-d20cd96363e1', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'MONDAY', 'EVEN', 3, '12:20', '13:50', 'f3b7c4fb-8fda-4512-9f0f-f6728f23c00f', '060282dc-b717-441d-84a4-3e3a01903ee3', 'Зал 9'), -- У533 Программирование (лаб, 2п/гр), Денгаев А.М.
    ('041abb6e-86b6-4e5a-8a1d-9771d57dbdb7', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'MONDAY', 'EVEN', 3, '12:20', '13:50', '93980989-55a4-4036-b206-44fbf44db8fc', 'd05c213f-efcf-4327-9e1c-65455fa68845', '548'), -- У530 История России, Казакбиева О.И.
    ('54955e7a-c4bf-42a7-b08c-6a09fc5f4076', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'MONDAY', 'EVEN', 3, '12:20', '13:50', '9bd3db49-891a-478b-8906-0fe055ce6568', 'c4fb96fb-7d7f-456b-bc5e-72240ec0b1ff', '352'), -- У534 Введение в программирование (лаб, 2п/гр), Савзиханова С.Э.
    ('dd45475a-3fc5-4b71-884d-93e246a6e5b3', '1c35ef07-28ef-4aa0-b52e-9fe10d560efb', 'MONDAY', 'EVEN', 4, '14:00', '15:30', '207bd4a8-ab29-454d-9c63-d0339ad3747c', '08b5a8aa-e59b-4183-8d4d-09c67d45b482', '542'),
    ('3cd93730-3bdd-4157-a76d-a4f29b7fbe10', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'MONDAY', 'EVEN', 4, '14:00', '15:30', 'f3b7c4fb-8fda-4512-9f0f-f6728f23c00f', '060282dc-b717-441d-84a4-3e3a01903ee3', 'Зал 9'),
    ('08ea15ef-bad2-4d4f-ab5e-e08feaf20fb7', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'MONDAY', 'EVEN', 4, '14:00', '15:30', '93980989-55a4-4036-b206-44fbf44db8fc', 'd05c213f-efcf-4327-9e1c-65455fa68845', '548'),
    ('e5bf75f5-7bf8-460f-b2ae-8be83a40ca44', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'MONDAY', 'EVEN', 4, '14:00', '15:30', '9bd3db49-891a-478b-8906-0fe055ce6568', 'c4fb96fb-7d7f-456b-bc5e-72240ec0b1ff', '352');

INSERT INTO pair_groups (pair_id, group_id) VALUES
    ('db0a5e86-5e15-45ed-bd53-5e4394cb2e64', '0ded6e78-f5f7-4184-ab25-10aa91030d8e'),
    ('fdc606c9-8083-4d77-a3ee-d20cd96363e1', '11c4f49a-af05-4ce5-bd17-118909fee773'),
    ('041abb6e-86b6-4e5a-8a1d-9771d57dbdb7', 'becfddf9-63b2-4443-b60f-c163b7f263fb'),
    ('54955e7a-c4bf-42a7-b08c-6a09fc5f4076', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122'),
    ('dd45475a-3fc5-4b71-884d-93e246a6e5b3', '0ded6e78-f5f7-4184-ab25-10aa91030d8e'),
    ('3cd93730-3bdd-4157-a76d-a4f29b7fbe10', '11c4f49a-af05-4ce5-bd17-118909fee773'),
    ('08ea15ef-bad2-4d4f-ab5e-e08feaf20fb7', 'becfddf9-63b2-4443-b60f-c163b7f263fb'),
    ('e5bf75f5-7bf8-460f-b2ae-8be83a40ca44', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122');

-- =====================================================================================
-- ВТОРНИК
-- =====================================================================================

-- Пара 1 (08:30-10:00): История России, Казакбиева О.И., каб. 125 — поток у всех 4 групп (как в V15).
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('76688ff9-5d0e-43eb-9922-4b1b0480d642', '1c35ef07-28ef-4aa0-b52e-9fe10d560efb', 'TUESDAY', 'EVEN', 1, '08:30', '10:00', '93980989-55a4-4036-b206-44fbf44db8fc', 'd05c213f-efcf-4327-9e1c-65455fa68845', '125'),
    ('94ebe79e-9540-4412-a023-47a8debd989e', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'TUESDAY', 'EVEN', 1, '08:30', '10:00', '93980989-55a4-4036-b206-44fbf44db8fc', 'd05c213f-efcf-4327-9e1c-65455fa68845', '125'),
    ('b4d56505-4adf-4647-876f-4fa55f189aa1', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'TUESDAY', 'EVEN', 1, '08:30', '10:00', '93980989-55a4-4036-b206-44fbf44db8fc', 'd05c213f-efcf-4327-9e1c-65455fa68845', '125');

INSERT INTO pair_groups (pair_id, group_id) VALUES
    ('76688ff9-5d0e-43eb-9922-4b1b0480d642', '0ded6e78-f5f7-4184-ab25-10aa91030d8e'),
    ('94ebe79e-9540-4412-a023-47a8debd989e', '11c4f49a-af05-4ce5-bd17-118909fee773'),
    ('94ebe79e-9540-4412-a023-47a8debd989e', 'becfddf9-63b2-4443-b60f-c163b7f263fb'),
    ('b4d56505-4adf-4647-876f-4fa55f189aa1', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122');

-- Пара 2 (10:10-11:40): у каждой группы свой предмет — У533/У530 расходятся (Мат.анализ vs ОПИ лб),
-- поэтому не поток, 4 отдельных Pair.
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('590e66f7-53e5-4b76-b435-d9a74499002c', '1c35ef07-28ef-4aa0-b52e-9fe10d560efb', 'TUESDAY', 'EVEN', 2, '10:10', '11:40', '4b1db0c5-28de-4cd1-89d4-c98bb850e3bf', '6aaa2aa7-29b6-4c17-bd08-8d7b981378a7', '547'), -- У532 Математика, Шамов Э.Ш.
    ('f58a8148-f627-4811-bd98-039ba8065712', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'TUESDAY', 'EVEN', 2, '10:10', '11:40', '77645c49-4582-4440-9e50-1b5c9313feee', 'f9b063cf-3010-4dfa-a7a9-86c70728426a', '555'), -- У533 Математический анализ, Нурмагомедов А.М.
    ('45d76af7-6dc3-4ae5-a9c4-9d365a6766b6', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'TUESDAY', 'EVEN', 2, '10:10', '11:40', '3fb91377-8b1a-4197-b089-fba5bc6b0ea1', '78deebfe-dc9e-4748-b41c-ac1b1678d432', 'Зал 9'), -- У530 Основы программной инженерии (лаб, 2п/гр), Ирзаев Г.Х.
    ('ea83e69e-8f9b-4758-8e63-31dd3b671b37', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'TUESDAY', 'EVEN', 2, '10:10', '11:40', '5f0c4175-7906-4bf5-81f3-9ba0c2404abd', 'a4f7ef51-0c7d-4822-ae0a-18ac91c0847d', '546'); -- У534 Дискретная математика, Умалатов С.Д.

INSERT INTO pair_groups (pair_id, group_id) VALUES
    ('590e66f7-53e5-4b76-b435-d9a74499002c', '0ded6e78-f5f7-4184-ab25-10aa91030d8e'),
    ('f58a8148-f627-4811-bd98-039ba8065712', '11c4f49a-af05-4ce5-bd17-118909fee773'),
    ('45d76af7-6dc3-4ae5-a9c4-9d365a6766b6', 'becfddf9-63b2-4443-b60f-c163b7f263fb'),
    ('ea83e69e-8f9b-4758-8e63-31dd3b671b37', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122');

-- Пара 3 (12:20-13:50). У532: слэш-ячейка «511 Ин.яз...Мамедова Г.Б. / 252 Ин.яз...Авчиева Д.Т.» —
-- первый вариант (Мамедова Г.Б.) как teacher_id, оба кабинета через " / " (issue #46).
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('6387e128-58ad-49ff-8889-28932887a838', '1c35ef07-28ef-4aa0-b52e-9fe10d560efb', 'TUESDAY', 'EVEN', 3, '12:20', '13:50', '083b565e-4ce4-44ff-8575-2731dcf53547', 'acc0a397-c477-4673-b29d-c817984b7a22', '511 / 252'), -- У532 Иностранный язык, Мамедова Г.Б.
    ('77e6a495-1bc8-4568-89b8-3fe38f0cf604', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'TUESDAY', 'EVEN', 3, '12:20', '13:50', '3fb91377-8b1a-4197-b089-fba5bc6b0ea1', '78deebfe-dc9e-4748-b41c-ac1b1678d432', 'Зал 9'), -- У533 Основы программной инженерии (лаб, "1п/гр-2нед,2п/гр-4нед" не переносится), Ирзаев Г.Х.
    ('c79b5f9b-451f-4218-aca9-7beaef1c5b03', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'TUESDAY', 'EVEN', 3, '12:20', '13:50', '77645c49-4582-4440-9e50-1b5c9313feee', 'f9b063cf-3010-4dfa-a7a9-86c70728426a', '555'), -- У530 Математический анализ, Нурмагомедов А.М.
    ('7e477eaa-1737-4f04-bc22-6fe81c0b6df8', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'TUESDAY', 'EVEN', 3, '12:20', '13:50', '5f0c4175-7906-4bf5-81f3-9ba0c2404abd', 'a4f7ef51-0c7d-4822-ae0a-18ac91c0847d', '546'); -- У534 Дискретная математика (пз), Умалатов С.Д.

INSERT INTO pair_groups (pair_id, group_id) VALUES
    ('6387e128-58ad-49ff-8889-28932887a838', '0ded6e78-f5f7-4184-ab25-10aa91030d8e'),
    ('77e6a495-1bc8-4568-89b8-3fe38f0cf604', '11c4f49a-af05-4ce5-bd17-118909fee773'),
    ('c79b5f9b-451f-4218-aca9-7beaef1c5b03', 'becfddf9-63b2-4443-b60f-c163b7f263fb'),
    ('7e477eaa-1737-4f04-bc22-6fe81c0b6df8', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122');

-- Пара 4 (14:00-15:30). У532: «РЯиКР» исправлено на «РИиКР» — опечатка, см. шапку файла.
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('52ca6138-c1bd-4393-8813-cc5d66fa14ad', '1c35ef07-28ef-4aa0-b52e-9fe10d560efb', 'TUESDAY', 'EVEN', 4, '14:00', '15:30', '4200bef3-64d9-4cd9-b089-5f75c401fd96', 'dc0ba359-e60a-41aa-83af-315c184b0e80', '546'), -- У532 РИиКР, Магомедова Х.М.
    ('d4b6d8a5-b2ee-44aa-9b13-47db02310dba', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'TUESDAY', 'EVEN', 4, '14:00', '15:30', '3fb91377-8b1a-4197-b089-fba5bc6b0ea1', '78deebfe-dc9e-4748-b41c-ac1b1678d432', 'Зал 9'), -- У533 Основы программной инженерии (лаб), Ирзаев Г.Х.
    ('b7782af9-801b-4ae3-af58-517eba5b8070', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'TUESDAY', 'EVEN', 4, '14:00', '15:30', '19502be0-4def-4917-aa70-8078b54d71b1', 'df70386b-cbda-42a8-a507-7609c49d2558', '221'), -- У530 Иностранный язык (1п/гр не переносится), Авчиева Д.Т.
    ('ebbd1b1f-990f-48af-8ab9-55f72fecec0f', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'TUESDAY', 'EVEN', 4, '14:00', '15:30', 'cdf2e654-c2b3-4ac5-909f-aa745d5b12d5', 'd05c213f-efcf-4327-9e1c-65455fa68845', '547'); -- У534 Правовые основы профессиональной деятельности (лк), Казакбиева О.И.

INSERT INTO pair_groups (pair_id, group_id) VALUES
    ('52ca6138-c1bd-4393-8813-cc5d66fa14ad', '0ded6e78-f5f7-4184-ab25-10aa91030d8e'),
    ('d4b6d8a5-b2ee-44aa-9b13-47db02310dba', '11c4f49a-af05-4ce5-bd17-118909fee773'),
    ('b7782af9-801b-4ae3-af58-517eba5b8070', 'becfddf9-63b2-4443-b60f-c163b7f263fb'),
    ('ebbd1b1f-990f-48af-8ab9-55f72fecec0f', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122');

-- =====================================================================================
-- СРЕДА
-- =====================================================================================

-- Пара 1 (08:30-10:00): Физика, Ахмедов Г.Я., каб. 125 — поток КСиТ+РПиС (У533+У530), СПиКТ отдельно.
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('67c2edd1-2a9a-49f2-9539-706ceb7eb474', '1c35ef07-28ef-4aa0-b52e-9fe10d560efb', 'WEDNESDAY', 'EVEN', 1, '08:30', '10:00', 'ac08dab8-6da6-4d58-9811-bcd7b3a94133', '6c0c1c33-9859-486d-82f7-a14e3c8727a0', '125'), -- У532 Физика, Ахмедов Г.Я.
    ('df078a95-0568-4a40-9ed6-2a19196a9b27', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'WEDNESDAY', 'EVEN', 1, '08:30', '10:00', '77f74fec-0572-4c55-9ef0-b382d196a392', '6c0c1c33-9859-486d-82f7-a14e3c8727a0', '125'), -- У533+У530 Физика (поток), Ахмедов Г.Я.
    ('34580f5f-b637-48c9-98b5-0cc0154a9690', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'WEDNESDAY', 'EVEN', 1, '08:30', '10:00', '2e6a9049-0ad9-4db2-b5c8-f3f9b6307177', 'f9b063cf-3010-4dfa-a7a9-86c70728426a', '555'); -- У534 Алгебра и аналитическая геометрия, Нурмагомедов А.М.

INSERT INTO pair_groups (pair_id, group_id) VALUES
    ('67c2edd1-2a9a-49f2-9539-706ceb7eb474', '0ded6e78-f5f7-4184-ab25-10aa91030d8e'),
    ('df078a95-0568-4a40-9ed6-2a19196a9b27', '11c4f49a-af05-4ce5-bd17-118909fee773'),
    ('df078a95-0568-4a40-9ed6-2a19196a9b27', 'becfddf9-63b2-4443-b60f-c163b7f263fb'),
    ('34580f5f-b637-48c9-98b5-0cc0154a9690', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122');

-- Пара 2 (10:10-11:40): элективные курсы по физкультуре, сквозной курс для всех программ (как в V15) —
-- первый вариант слэш-списка (Абдуллаев А.А.) как teacher_id, остальные только в комментарии.
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('3eac07ff-a32d-4782-a8c1-a904bb8407d8', '1c35ef07-28ef-4aa0-b52e-9fe10d560efb', 'WEDNESDAY', 'EVEN', 2, '10:10', '11:40', '2b44e7de-32df-41b9-a67b-d428af9f2b66', '3511c34a-ab5d-4fe4-a3d0-7e7d23808d62', 'спортзал УЛК 2'),
    ('9b2fb8e5-70f8-4792-af93-881e505175f2', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'WEDNESDAY', 'EVEN', 2, '10:10', '11:40', '2b44e7de-32df-41b9-a67b-d428af9f2b66', '3511c34a-ab5d-4fe4-a3d0-7e7d23808d62', 'спортзал УЛК 2'),
    ('fb45dd0e-b7a3-49bf-9fb4-ea62182e9af3', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'WEDNESDAY', 'EVEN', 2, '10:10', '11:40', '2b44e7de-32df-41b9-a67b-d428af9f2b66', '3511c34a-ab5d-4fe4-a3d0-7e7d23808d62', 'спортзал УЛК 2');

INSERT INTO pair_groups (pair_id, group_id) VALUES
    ('3eac07ff-a32d-4782-a8c1-a904bb8407d8', '0ded6e78-f5f7-4184-ab25-10aa91030d8e'),
    ('9b2fb8e5-70f8-4792-af93-881e505175f2', '11c4f49a-af05-4ce5-bd17-118909fee773'),
    ('9b2fb8e5-70f8-4792-af93-881e505175f2', 'becfddf9-63b2-4443-b60f-c163b7f263fb'),
    ('fb45dd0e-b7a3-49bf-9fb4-ea62182e9af3', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122');

-- Пара 3 (12:20-13:50). У530 пуст в источнике («-») — пропускаем.
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('361c9d93-39c2-4ab7-b6ec-0bacf650e8be', '1c35ef07-28ef-4aa0-b52e-9fe10d560efb', 'WEDNESDAY', 'EVEN', 3, '12:20', '13:50', '93980989-55a4-4036-b206-44fbf44db8fc', 'd05c213f-efcf-4327-9e1c-65455fa68845', '548'), -- У532 История России, Казакбиева О.И.
    ('0505d8f2-db6b-481f-87b3-642909b1de38', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'WEDNESDAY', 'EVEN', 3, '12:20', '13:50', '77f74fec-0572-4c55-9ef0-b382d196a392', '6c0c1c33-9859-486d-82f7-a14e3c8727a0', '438'), -- У533 Физика (пз), Ахмедов Г.Я.
    ('c64109f1-980b-4e53-b09f-d051043c0fdc', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'WEDNESDAY', 'EVEN', 3, '12:20', '13:50', '2e6a9049-0ad9-4db2-b5c8-f3f9b6307177', 'f9b063cf-3010-4dfa-a7a9-86c70728426a', '555'); -- У534 Алгебра и аналитическая геометрия (пз), Нурмагомедов А.М.

INSERT INTO pair_groups (pair_id, group_id) VALUES
    ('361c9d93-39c2-4ab7-b6ec-0bacf650e8be', '0ded6e78-f5f7-4184-ab25-10aa91030d8e'),
    ('0505d8f2-db6b-481f-87b3-642909b1de38', '11c4f49a-af05-4ce5-bd17-118909fee773'),
    ('c64109f1-980b-4e53-b09f-d051043c0fdc', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122');

-- Пара 4 (14:00-15:30). У533/У530 пусты в источнике («-») — пропускаем.
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('6d0be518-f80a-49e7-8cc1-d4f4b7d94ff4', '1c35ef07-28ef-4aa0-b52e-9fe10d560efb', 'WEDNESDAY', 'EVEN', 4, '14:00', '15:30', '93980989-55a4-4036-b206-44fbf44db8fc', 'd05c213f-efcf-4327-9e1c-65455fa68845', '548'), -- У532 История России, Казакбиева О.И.
    ('5df547dd-a793-4f3c-8d74-81abac8b3c21', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'WEDNESDAY', 'EVEN', 4, '14:00', '15:30', 'cdf2e654-c2b3-4ac5-909f-aa745d5b12d5', 'd994c46d-0f7f-467c-9080-80deb4730118', '547'); -- У534 Правовые основы профессиональной деятельности (пз), Магомедова М.А.

INSERT INTO pair_groups (pair_id, group_id) VALUES
    ('6d0be518-f80a-49e7-8cc1-d4f4b7d94ff4', '0ded6e78-f5f7-4184-ab25-10aa91030d8e'),
    ('5df547dd-a793-4f3c-8d74-81abac8b3c21', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122');

-- =====================================================================================
-- ЧЕТВЕРГ
-- =====================================================================================

-- Пара 1 (08:30-10:00). У533/У530/У534 пусты в источнике («-») — пропускаем.
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('ecebb440-e387-4f02-a04d-21bf8b66f381', '1c35ef07-28ef-4aa0-b52e-9fe10d560efb', 'THURSDAY', 'EVEN', 1, '08:30', '10:00', 'ffbd3cdb-bdcc-49d4-a7c3-eadb89556e3d', '5bd2bba5-03bf-492c-a224-0edb7a04f200', 'Зал 9'); -- У532 Программирование (лаб, 1п/гр), Айгумов Т.Г.

INSERT INTO pair_groups (pair_id, group_id) VALUES
    ('ecebb440-e387-4f02-a04d-21bf8b66f381', '0ded6e78-f5f7-4184-ab25-10aa91030d8e');

-- Пара 2 (10:10-11:40). У533: слэш-ячейка «221 Ин.яз...Абуева Н.Н. / 252 Ин.яз...Авчиева Д.Т.» —
-- первый вариант (Абуева Н.Н., опечатка «Н.П.» в источнике исправлена — см. шапку файла) как
-- teacher_id, оба кабинета через " / ".
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('6cc94238-0c41-457c-866f-7dd3c06339fa', '1c35ef07-28ef-4aa0-b52e-9fe10d560efb', 'THURSDAY', 'EVEN', 2, '10:10', '11:40', 'ffbd3cdb-bdcc-49d4-a7c3-eadb89556e3d', '5bd2bba5-03bf-492c-a224-0edb7a04f200', 'Зал 9'), -- У532 Программирование (лаб, 1п/гр), Айгумов Т.Г.
    ('57978a09-10a1-46fe-8033-9c20a202b8df', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'THURSDAY', 'EVEN', 2, '10:10', '11:40', '19502be0-4def-4917-aa70-8078b54d71b1', '01d9e270-1777-487f-85a8-f65063362be6', '221 / 252'), -- У533 Иностранный язык, Абуева Н.Н.
    ('4ca82cee-ff90-43b2-b92f-b585c9fac01e', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'THURSDAY', 'EVEN', 2, '10:10', '11:40', '3fb91377-8b1a-4197-b089-fba5bc6b0ea1', '78deebfe-dc9e-4748-b41c-ac1b1678d432', '311'), -- У530 Основы программной инженерии (пз), Ирзаев Г.Х.
    ('4f692abd-e166-412c-a57c-9afc0c22397a', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'THURSDAY', 'EVEN', 2, '10:10', '11:40', '869b7201-b565-41a1-8333-ccb036952515', 'e58af1cf-fa0a-4224-972d-3ab19b8d4ea9', '538'); -- У534 Философия, Шайдаева Г.М.

INSERT INTO pair_groups (pair_id, group_id) VALUES
    ('6cc94238-0c41-457c-866f-7dd3c06339fa', '0ded6e78-f5f7-4184-ab25-10aa91030d8e'),
    ('57978a09-10a1-46fe-8033-9c20a202b8df', '11c4f49a-af05-4ce5-bd17-118909fee773'),
    ('4ca82cee-ff90-43b2-b92f-b585c9fac01e', 'becfddf9-63b2-4443-b60f-c163b7f263fb'),
    ('4f692abd-e166-412c-a57c-9afc0c22397a', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122');

-- Пара 3 (12:20-13:50). У534: слэш-ячейка «244 Ин.яз...Агасиева И.Р. / 511 Ин.яз...Мамедова Г.Б.» —
-- первый вариант (Агасиева И.Р., опечатка «Н.Р.» в источнике исправлена — см. шапку файла) как
-- teacher_id.
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('d922d968-9a1c-441e-b8e5-5c5a38791bef', '1c35ef07-28ef-4aa0-b52e-9fe10d560efb', 'THURSDAY', 'EVEN', 3, '12:20', '13:50', 'ffbd3cdb-bdcc-49d4-a7c3-eadb89556e3d', '5bd2bba5-03bf-492c-a224-0edb7a04f200', 'Зал 9'), -- У532 Программирование (лаб, 1п/гр), Айгумов Т.Г.
    ('7e7cd624-6aab-4e80-864d-fe0c47536a28', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'THURSDAY', 'EVEN', 3, '12:20', '13:50', '3fb91377-8b1a-4197-b089-fba5bc6b0ea1', '78deebfe-dc9e-4748-b41c-ac1b1678d432', '311'), -- У533 Основы программной инженерии (пз), Ирзаев Г.Х.
    ('071d0584-36a3-4599-b633-829f59017ef4', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'THURSDAY', 'EVEN', 3, '12:20', '13:50', '19502be0-4def-4917-aa70-8078b54d71b1', 'df70386b-cbda-42a8-a507-7609c49d2558', '252'), -- У530 Иностранный язык (2п/гр не переносится), Авчиева Д.Т.
    ('0f5e26a1-8bbf-4801-8d46-7db23e912c1b', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'THURSDAY', 'EVEN', 3, '12:20', '13:50', '4cbeea29-e53a-406b-8181-69fb62185b0e', '87675fa1-af1b-4938-b182-366ffde8bbb7', '244 / 511'); -- У534 Иностранный язык, Агасиева И.Р.

INSERT INTO pair_groups (pair_id, group_id) VALUES
    ('d922d968-9a1c-441e-b8e5-5c5a38791bef', '0ded6e78-f5f7-4184-ab25-10aa91030d8e'),
    ('7e7cd624-6aab-4e80-864d-fe0c47536a28', '11c4f49a-af05-4ce5-bd17-118909fee773'),
    ('071d0584-36a3-4599-b633-829f59017ef4', 'becfddf9-63b2-4443-b60f-c163b7f263fb'),
    ('0f5e26a1-8bbf-4801-8d46-7db23e912c1b', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122');

-- Пара 4 (14:00-15:30). У533 пуст в источнике («-») — пропускаем. У530: новый курс «Инф.-обр.
-- занятие» и новый преподаватель Качаева Г.И. (в БД уже есть, впервые встречается в расписании).
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('97d19327-0505-463e-953c-6d4634b91c91', '1c35ef07-28ef-4aa0-b52e-9fe10d560efb', 'THURSDAY', 'EVEN', 4, '14:00', '15:30', 'ffbd3cdb-bdcc-49d4-a7c3-eadb89556e3d', '5bd2bba5-03bf-492c-a224-0edb7a04f200', 'Зал 9'), -- У532 Программирование (лаб, 2п/гр), Айгумов Т.Г.
    ('e88c2f42-e75e-47d2-9caf-f09d4c504727', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'THURSDAY', 'EVEN', 4, '14:00', '15:30', '7fe04902-138a-49f1-bea7-792b2de047ff', '85eb596c-fd92-4383-9fa8-9a8cd1a9f261', '307'), -- У530 Инф.-обр. занятие, Качаева Г.И.
    ('c5c17fae-f056-4fbc-95d4-1137067163a6', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'THURSDAY', 'EVEN', 4, '14:00', '15:30', '5f0c4175-7906-4bf5-81f3-9ba0c2404abd', 'a4f7ef51-0c7d-4822-ae0a-18ac91c0847d', '555'); -- У534 Дискретная математика (пз), Умалатов С.Д.

INSERT INTO pair_groups (pair_id, group_id) VALUES
    ('97d19327-0505-463e-953c-6d4634b91c91', '0ded6e78-f5f7-4184-ab25-10aa91030d8e'),
    ('e88c2f42-e75e-47d2-9caf-f09d4c504727', 'becfddf9-63b2-4443-b60f-c163b7f263fb'),
    ('c5c17fae-f056-4fbc-95d4-1137067163a6', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122');

-- =====================================================================================
-- ПЯТНИЦА
-- =====================================================================================

-- Пара 1 (08:30-10:00). У530 пуст в источнике («-») — пропускаем. У534: новый преподаватель
-- Котенко М.Е. (в БД уже есть, впервые встречается в расписании).
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('2b32cf43-1703-455e-ade2-2d61e710ee09', '1c35ef07-28ef-4aa0-b52e-9fe10d560efb', 'FRIDAY', 'EVEN', 1, '08:30', '10:00', '4b1db0c5-28de-4cd1-89d4-c98bb850e3bf', '6aaa2aa7-29b6-4c17-bd08-8d7b981378a7', '548'), -- У532 Математика, Шамов Э.Ш.
    ('d9f2f4b2-0fa2-42d3-9c32-0d5b615688bc', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'FRIDAY', 'EVEN', 1, '08:30', '10:00', 'd2c73633-f7ae-402b-81ec-f61c3ccf06ea', '5bd2bba5-03bf-492c-a224-0edb7a04f200', '125'), -- У533 Введение в программирование на языке Python, Айгумов Т.Г.
    ('41913ee2-b40e-4786-84a0-24499c05bbba', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'FRIDAY', 'EVEN', 1, '08:30', '10:00', 'a9b42e78-9d40-4208-98f5-757d58c01e34', '764c455b-239d-4ab2-b306-209d8c1df6f5', '202'); -- У534 Безопасность жизнедеятельности (лк), Котенко М.Е.

INSERT INTO pair_groups (pair_id, group_id) VALUES
    ('2b32cf43-1703-455e-ade2-2d61e710ee09', '0ded6e78-f5f7-4184-ab25-10aa91030d8e'),
    ('d9f2f4b2-0fa2-42d3-9c32-0d5b615688bc', '11c4f49a-af05-4ce5-bd17-118909fee773'),
    ('41913ee2-b40e-4786-84a0-24499c05bbba', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122');

-- Пара 2 (10:10-11:40)
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('d710370b-6446-4310-88bc-e25a57f9c4c6', '1c35ef07-28ef-4aa0-b52e-9fe10d560efb', 'FRIDAY', 'EVEN', 2, '10:10', '11:40', '477dadff-5923-4cca-8e81-0a5a75eb77c9', '904e14a2-d8d6-4bb8-b1e3-fa9dae8de92e', '542'), -- У532 ТВИМС (лаб), Хаиров Р.А.
    ('d08abbb1-a7d4-4ec5-9b20-bb18bbd1bb3a', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'FRIDAY', 'EVEN', 2, '10:10', '11:40', 'd2c73633-f7ae-402b-81ec-f61c3ccf06ea', '5bd2bba5-03bf-492c-a224-0edb7a04f200', '449'), -- У533 Введение в программирование на языке Python (пз), Айгумов Т.Г.
    ('25b7c104-721c-4449-8f6b-62629f54afc0', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'FRIDAY', 'EVEN', 2, '10:10', '11:40', 'f3b7c4fb-8fda-4512-9f0f-f6728f23c00f', '060282dc-b717-441d-84a4-3e3a01903ee3', '555'), -- У530 Программирование, Денгаев А.М.
    ('6f10fb8b-e665-45ed-91d4-affecf39879a', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'FRIDAY', 'EVEN', 2, '10:10', '11:40', 'a9b42e78-9d40-4208-98f5-757d58c01e34', 'a7f43d67-6269-4dae-876d-6aa1fe5d38b0', '202'); -- У534 Безопасность жизнедеятельности (пз), Магомедалиева З.А.

INSERT INTO pair_groups (pair_id, group_id) VALUES
    ('d710370b-6446-4310-88bc-e25a57f9c4c6', '0ded6e78-f5f7-4184-ab25-10aa91030d8e'),
    ('d08abbb1-a7d4-4ec5-9b20-bb18bbd1bb3a', '11c4f49a-af05-4ce5-bd17-118909fee773'),
    ('25b7c104-721c-4449-8f6b-62629f54afc0', 'becfddf9-63b2-4443-b60f-c163b7f263fb'),
    ('6f10fb8b-e665-45ed-91d4-affecf39879a', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122');

-- Пара 3 (12:20-13:50). У532 пуст в источнике («-») — пропускаем.
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('a09d6069-244b-4ce3-92e6-1f9eee81701a', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'FRIDAY', 'EVEN', 3, '12:20', '13:50', '77f74fec-0572-4c55-9ef0-b382d196a392', '47eb6a00-1893-4851-bf59-298c104b7793', '436'), -- У533 Физика (лаб, "2нед-1п/гр,4нед-2п/гр" не переносится), Ризаханова С.У.
    ('69b0309f-8091-4f70-a7da-4aef631dabb1', 'fa6a23a1-620c-45e8-813c-87ada5836a9c', 'FRIDAY', 'EVEN', 3, '12:20', '13:50', 'f3b7c4fb-8fda-4512-9f0f-f6728f23c00f', '060282dc-b717-441d-84a4-3e3a01903ee3', 'Зал 6'), -- У530 Программирование (лаб, 1п/гр), Денгаев А.М.
    ('d0772b3f-8407-4ce3-91c7-30f5e7b5ee40', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'FRIDAY', 'EVEN', 3, '12:20', '13:50', 'b41bed9a-fdda-4e61-a9a2-5fd91778f302', '04e90fc9-c390-494c-85cc-555cf5061f36', '538'); -- У534 Математический анализ (лк), Салахов А.З.

INSERT INTO pair_groups (pair_id, group_id) VALUES
    ('a09d6069-244b-4ce3-92e6-1f9eee81701a', '11c4f49a-af05-4ce5-bd17-118909fee773'),
    ('69b0309f-8091-4f70-a7da-4aef631dabb1', 'becfddf9-63b2-4443-b60f-c163b7f263fb'),
    ('d0772b3f-8407-4ce3-91c7-30f5e7b5ee40', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122');

-- Пара 4 (14:00-15:30). У533/У530 пусты в источнике («-») — пропускаем.
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('9203f2cb-d649-40ac-bae8-1c01989fc979', '1c35ef07-28ef-4aa0-b52e-9fe10d560efb', 'FRIDAY', 'EVEN', 4, '14:00', '15:30', 'ffbd3cdb-bdcc-49d4-a7c3-eadb89556e3d', '5bd2bba5-03bf-492c-a224-0edb7a04f200', '449'), -- У532 Программирование, Айгумов Т.Г.
    ('50505cf2-4100-44a4-8f77-09f2f0cff355', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'FRIDAY', 'EVEN', 4, '14:00', '15:30', 'b41bed9a-fdda-4e61-a9a2-5fd91778f302', '04e90fc9-c390-494c-85cc-555cf5061f36', '538'); -- У534 Математический анализ (пз), Салахов А.З.

INSERT INTO pair_groups (pair_id, group_id) VALUES
    ('9203f2cb-d649-40ac-bae8-1c01989fc979', '0ded6e78-f5f7-4184-ab25-10aa91030d8e'),
    ('50505cf2-4100-44a4-8f77-09f2f0cff355', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122');

-- Пара 5 (15:40-17:10). Только У534 — новый курс «Инф.-обр. занятие» и новый преподаватель
-- Канаев М.М. (в БД уже есть, впервые встречается в расписании). У532/У533/У530 пусты в источнике.
INSERT INTO pairs (id, week_schedule_cycle_id, day_of_week, week_parity, pair_number, start_time, end_time, course_id, teacher_id, room) VALUES
    ('3e62f603-5401-43af-b65c-ddb419a09baf', '69c3b8e2-6643-4cff-ae77-e781c6550a32', 'FRIDAY', 'EVEN', 5, '15:40', '17:10', 'c2be8883-0c13-4ef0-988b-c1b795fceaac', 'e1ebdb82-c1b9-4c87-950f-54d1d62d4915', '354'); -- У534 Инф.-обр. занятие, Канаев М.М.

INSERT INTO pair_groups (pair_id, group_id) VALUES
    ('3e62f603-5401-43af-b65c-ddb419a09baf', '6d60d3dd-41fc-4cd7-a084-f72b2ef31122');
