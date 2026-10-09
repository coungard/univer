-- Упразднение программ (issue #85), шаг 2 из 3: перенос учебных годов с программ на факультеты.
--
-- У факультета было по программе на направление, и у каждой — свой «1 курс» со своим семестром и
-- своим циклическим расписанием. Теперь курс — общий на факультет, поэтому дубли сливаются сверху
-- вниз: учебные годы → семестры → циклы расписания. Остаётся запись с наименьшим id (у циклов —
-- цикл остающегося семестра), на неё перевешивается всё, что висело на остальных.
--
-- Учебные годы программ без факультета (programs.faculty_id IS NULL) здесь не трогаются: привязать
-- их не к чему, а удалять вместе с группами молча нельзя. Если такие есть, V35 упадёт на NOT NULL —
-- их нужно разобрать вручную. В seed-данных и dev-БД таких нет.

-- 1. Факультет учебного года — факультет его программы
UPDATE study_years sy
SET faculty_id = p.faculty_id
FROM programs p
WHERE p.id = sy.program_id;

-- 2. Учебные годы: один на (факультет, номер курса); семестры — на остающийся
WITH keepers AS (
    SELECT id, FIRST_VALUE(id) OVER (PARTITION BY faculty_id, year_number ORDER BY id) AS keeper_id
    FROM study_years
    WHERE faculty_id IS NOT NULL
)
UPDATE semesters s
SET study_year_id = k.keeper_id
FROM keepers k
WHERE k.id = s.study_year_id
  AND k.id <> k.keeper_id;

WITH keepers AS (
    SELECT id, FIRST_VALUE(id) OVER (PARTITION BY faculty_id, year_number ORDER BY id) AS keeper_id
    FROM study_years
    WHERE faculty_id IS NOT NULL
)
DELETE FROM study_years sy
USING keepers k
WHERE k.id = sy.id
  AND k.id <> k.keeper_id;

-- 3. Семестры: после слияния у учебного года оказалось по семестру на одни и те же даты от каждой
--    программы. Оставляем один, иначе «актуальный семестр» (GET /groups?facultyId=&yearNumber=)
--    размазан по нескольким записям. Группы — на остающийся.
WITH keepers AS (
    SELECT id,
           FIRST_VALUE(id) OVER (PARTITION BY study_year_id, start_date, end_date ORDER BY id) AS keeper_id
    FROM semesters
)
UPDATE student_groups g
SET semester_id = k.keeper_id
FROM keepers k
WHERE k.id = g.semester_id
  AND k.id <> k.keeper_id;

-- 4. Циклы расписания: у семестра может быть только один (UNIQUE semester_id), поэтому циклы
--    сливаемых семестров тоже сливаются. Остаётся цикл остающегося семестра, а если у того цикла
--    не было — любой из сливаемых. Пары переезжают в него; сами пары и их группы не меняются.
--    Если хоть один из сливаемых циклов был черновиком, итоговый — тоже черновик: в нём оказались
--    несогласованные пары.
WITH semester_keepers AS (
    SELECT id,
           FIRST_VALUE(id) OVER (PARTITION BY study_year_id, start_date, end_date ORDER BY id) AS keeper_id
    FROM semesters
), cycle_keepers AS (
    SELECT c.id,
           c.status,
           FIRST_VALUE(c.id) OVER (PARTITION BY sk.keeper_id
               ORDER BY (c.semester_id = sk.keeper_id) DESC, c.id) AS keeper_id
    FROM week_schedule_cycles c
        JOIN semester_keepers sk ON sk.id = c.semester_id
)
UPDATE week_schedule_cycles c
SET status = 'DRAFT'
WHERE c.status <> 'DRAFT'
  AND EXISTS (
      SELECT 1 FROM cycle_keepers ck WHERE ck.keeper_id = c.id AND ck.status = 'DRAFT');

WITH semester_keepers AS (
    SELECT id,
           FIRST_VALUE(id) OVER (PARTITION BY study_year_id, start_date, end_date ORDER BY id) AS keeper_id
    FROM semesters
), cycle_keepers AS (
    SELECT c.id,
           FIRST_VALUE(c.id) OVER (PARTITION BY sk.keeper_id
               ORDER BY (c.semester_id = sk.keeper_id) DESC, c.id) AS keeper_id
    FROM week_schedule_cycles c
        JOIN semester_keepers sk ON sk.id = c.semester_id
)
UPDATE pairs p
SET week_schedule_cycle_id = ck.keeper_id
FROM cycle_keepers ck
WHERE ck.id = p.week_schedule_cycle_id
  AND ck.id <> ck.keeper_id;

WITH semester_keepers AS (
    SELECT id,
           FIRST_VALUE(id) OVER (PARTITION BY study_year_id, start_date, end_date ORDER BY id) AS keeper_id
    FROM semesters
), cycle_keepers AS (
    SELECT c.id,
           FIRST_VALUE(c.id) OVER (PARTITION BY sk.keeper_id
               ORDER BY (c.semester_id = sk.keeper_id) DESC, c.id) AS keeper_id
    FROM week_schedule_cycles c
        JOIN semester_keepers sk ON sk.id = c.semester_id
)
DELETE FROM week_schedule_cycles c
USING cycle_keepers ck
WHERE ck.id = c.id
  AND ck.id <> ck.keeper_id;

-- Оставшийся цикл мог принадлежать не остающемуся семестру (у того цикла не было) — перевешиваем
WITH semester_keepers AS (
    SELECT id,
           FIRST_VALUE(id) OVER (PARTITION BY study_year_id, start_date, end_date ORDER BY id) AS keeper_id
    FROM semesters
)
UPDATE week_schedule_cycles c
SET semester_id = sk.keeper_id
FROM semester_keepers sk
WHERE sk.id = c.semester_id
  AND sk.id <> sk.keeper_id;

-- 5. Опустевшие дубли семестров
WITH keepers AS (
    SELECT id,
           FIRST_VALUE(id) OVER (PARTITION BY study_year_id, start_date, end_date ORDER BY id) AS keeper_id
    FROM semesters
)
DELETE FROM semesters s
USING keepers k
WHERE k.id = s.id
  AND k.id <> k.keeper_id;
