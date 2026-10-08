-- Занятия (Lecture) групп «У530 РПиС» и «У533 РПиС» на весь осенний семестр 2026 — чтобы в dev-БД
-- была группа с готовым расписанием: `GET /lectures/me` отдаёт именно Lecture, а V14/V15 заводят
-- только шаблоны Pair, из которых лекции иначе появляются лишь после ручного вызова
-- `POST /lectures/generate/semester/{cycleId}`. Нужна для прогона сценариев многошаговой регистрации
-- студента (см. README.md, «Тестовые данные для сценариев регистрации»): группы РПиС — «группа с
-- готовым расписанием», а «У532 КСиТ» и «У534 СПиКТ» намеренно остаются без лекций — «у группы нет
-- расписания».
--
-- Повторяет LectureServiceImpl#generateSemesterLectures для цикла РПиС: по каждой Pair — все даты
-- семестра с её днём недели и чётностью недели (номер недели считается от Semester.start_date, первая
-- неделя — нечётная). Пара + дата, для которых лекция уже есть (сгенерирована через API), пропускаются,
-- как и в сервисе.
WITH generated AS (
    INSERT INTO lectures (id, title, scheduled_time, duration_minutes, course_id, teacher_id, room, source_pair_id)
    SELECT uuid_generate_v4(),
           c.title,
           d::date + p.start_time,
           (EXTRACT(EPOCH FROM (p.end_time - p.start_time)) / 60)::int,
           p.course_id,
           p.teacher_id,
           p.room,
           p.id
    FROM pairs p
        JOIN courses c ON c.id = p.course_id
        JOIN week_schedule_cycles wsc ON wsc.id = p.week_schedule_cycle_id
        JOIN semesters s ON s.id = wsc.semester_id
        CROSS JOIN LATERAL generate_series(s.start_date::timestamp, s.end_date::timestamp, INTERVAL '1 day') d
    WHERE wsc.id = 'fa6a23a1-620c-45e8-813c-87ada5836a9c' -- WeekScheduleCycle РПиС (V14)
      AND TRIM(TO_CHAR(d, 'DAY')) = p.day_of_week
      AND (p.week_parity = 'BOTH'
          OR p.week_parity = CASE WHEN ((d::date - s.start_date) / 7 + 1) % 2 = 1 THEN 'ODD' ELSE 'EVEN' END)
      AND NOT EXISTS (
          SELECT 1 FROM lectures l
          WHERE l.source_pair_id = p.id AND l.scheduled_time = d::date + p.start_time)
    RETURNING id, source_pair_id
)
INSERT INTO lecture_groups (lecture_id, group_id)
SELECT g.id, pg.group_id
FROM generated g
    JOIN pair_groups pg ON pg.pair_id = g.source_pair_id;
