# Roadmap — Univer Backend

> Univer · Backend · Дорожная карта
> От текущего кода до рабочего MVP

Путь по этапам без привязки к календарю — соло-разработка, один регистратор задач за раз.
Порядок этапов задан зависимостями между модулями, а не желаемой скоростью.

- **Ветка:** `master`
- **Аудит по коду от:** 17.08.2026
- **Формат:** этапы, не спринты

---

## Аудит текущего состояния

| Модуль | Статус | Комментарий |
|---|---|---|
| `University` | ✅ Готово | Полный стек, конструкторная инъекция, покрыт тестами |
| `Faculty` | ✅ Готово | Полный стек по эталонному паттерну (interface + Impl, `ResourceNotFoundException`), покрыт тестами |
| `Department` | ✅ Готово | Полный стек по эталонному паттерну, покрыт тестами |
| `Program` | ✅ Готово | Полный стек + insert-скрипт данных, покрыт тестами |
| `Student` | ✅ Готово | Полный стек, регистрация через Keycloak, покрыт тестами и пагинацией |
| `Teacher` | ✅ Готово | Полный стек, регистрация через Keycloak, покрыт тестами (баг `registerTeacher` исправлен) |
| `Course` | ✅ Готово | Полный стек (DTO/Mapper/Service/Controller), CRUD + пагинация, покрыт тестами (issue #29) |
| `StudyYear` | ✅ Готово | Полный стек, привязка к `Program`, валидация номера курса против длительности программы, тесты (issue #33) |
| `Semester` | ✅ Готово | Полный стек, привязка к `StudyYear`, `startDate` для чётности недели, тесты (issue #34) |
| `Group` | ✅ Готово | Полный стек, привязка к `Semester`; `Student` получил опциональную связь с `Group`, тесты (issue #35) |
| `WeekScheduleCycle` / `Pair` | ✅ Готово | Один цикл на семестр, `Pair` связан с `Group` многие-ко-многим (поток), валидация буднего дня и времени, тесты (issue #36) |
| `Lecture` | ❌ Нет API | Есть entity, repository и таблица в схеме — нет DTO/service/controller. По `TARGET.md` должна уметь порождаться из `Pair` на конкретную дату, не только создаваться вручную |
| `Enrollment` | ❌ Нет API | Есть entity, repository и таблица в схеме — нет DTO/service/controller |
| `LectureAttendance` | ❌ Нет API | Есть entity, repository и таблица в схеме — нет DTO/service/controller |

**Находки:**

- ~~Роль TEACHER отсутствует~~ — уточнение: `ROLE_TEACHER` в `security.Role` уже существует, но до исправления #28 `registerTeacher` по ошибке назначал `ROLE_STUDENT` (копипаста из `StudentServiceImpl`). Вопрос, кто управляет курсами/лекциями на уровне `@PreAuthorize`, всё ещё не решён.
- ~~`TeacherServiceImpl.registerTeacher` сломан~~ — ✅ исправлено (issue #28): факультет теперь берётся из найденной по `departmentId` кафедры (`department.getFaculty()`), а не по чужому ID напрямую; заодно исправлено назначение роли — `ROLE_TEACHER` вместо `ROLE_STUDENT`.
- **CORS не настроен явно** — в `SecurityConfig` вызов `.cors(cors -> {})` ничего не задаёт, поведение зависит от дефолтов Spring.
- **Секреты в открытом виде** — пароли Postgres и admin-креды Keycloak лежат прямо в `application.yml` и `docker-compose.yml`.
- **Само приложение не в docker-compose** — файл поднимает только Postgres и Keycloak, Dockerfile для самого сервиса отсутствует.
- ~~Пагинация только у Student~~ — ✅ добавлена во все списковые эндпоинты: `University` (`getUniversities`),
  `Faculty` (`getFacultiesByUniversity`), `Department` (`getDepartmentsByFaculty`, `getDepartmentsByUniversity`);
  `Program` и `Teacher` уже были пагинированы ранее.
- ~~Целевая модель расписания зафиксирована в `TARGET.md`, но нигде в коде не спроектирована~~ —
  ✅ реализована Этапом 3 (issues #33–#37): `StudyYear` → `Semester` → `Group` → `WeekScheduleCycle`/`Pair`.
- **Обнаружена и исправлена отсутствующая колонка `courses.teacher_id`** — миграция V1 создавала `courses`
  без неё, хотя `Course.teacher` в entity есть с самого начала (#29); тесты не ловили это, так как
  используют `hibernate.ddl-auto=create-drop` и пересоздают схему по entity-маппингам, минуя реальные
  Flyway-миграции. В проде `POST /courses` с `teacherId` падал бы. Исправлено миграцией V7, проверено
  реальным `mvn spring-boot:run` против dev Postgres — этот же риск стоит держать в уме для остальных
  сущностей: тесты не гарантируют совпадение entity-маппинга с Flyway-схемой.

---

## Этапы

### 1. Фундамент ✅ Готово

*до старта новых модулей*

Закрыть технический долг в уже работающих модулях, прежде чем на них опираться дальше — Course зависит от Department, Enrollment от Student.

| Задача | Модуль | Результат |
|---|---|---|
| ~~Вынести интерфейс + Impl~~ | `University` | ✅ Готово — конструкторная инъекция вместо `@Autowired`-полей, единый паттерн со всеми остальными сервисами |
| ~~Вынести интерфейс + Impl~~ | `Faculty` | ✅ Готово — `ResourceNotFoundException` вместо `ResponseStatusException`, единый формат ошибок через `GlobalExceptionHandler` |
| ~~Тесты сервиса~~ | `Faculty` | ✅ Готово — `FacultyServiceTest` (Testcontainers) по образцу `StudentServiceTest` |
| ~~Тесты сервиса~~ | `Department` | ✅ Готово — `DepartmentServiceTest` (Testcontainers) по образцу `StudentServiceTest` |
| ~~Тесты сервиса~~ | `Program` | ✅ Готово — `ProgramServiceTest` (Testcontainers) по образцу `StudentServiceTest` |
| ~~Тесты сервиса~~ | `Teacher` | ✅ Готово — `TeacherServiceTest` (Testcontainers) по образцу `StudentServiceTest` |
| ~~Пагинация списков~~ | `University`, `Faculty`, `Department`, `Program`, `Teacher` | ✅ Готово — `Pageable` вместо полной выдачи во всех списковых эндпоинтах |

### 2. Учебные курсы ✅ Готово

*Course*

Первый слой из заявленного в README. `Lecture` раньше шла тут же следующим шагом, но перенесена в Этап 4 —
она зависит от циклического расписания (Этап 3), а не только от `Course`, см. `TARGET.md`.

| Задача | Модуль | Результат |
|---|---|---|
| ~~DTO + Mapper + Service/Impl + Controller~~ | `Course` | ✅ Готово (issue #29, закрыт) — CRUD, привязка к обязательному Department и опциональному Teacher, пагинация, тесты |

### 3. Циклическое расписание ✅ Готово

*StudyYear → Semester → WeekScheduleCycle/Pair, Group — многие-ко-многим с Pair*

Целевая модель зафиксирована в `TARGET.md`. Стоит перед `Lecture` намеренно: `Lecture` должна уметь
порождаться из шаблона `Pair` на конкретную дату, а для этого сначала нужны `Semester` (откуда считать
чётность недели — `WeekScheduleCycle` принадлежит семестру, не отдельной группе) и `Group` (с кем связан
`Pair`, причём один `Pair` может быть связан сразу с несколькими группами — поточная лекция).

| Задача | Модуль | Результат |
|---|---|---|
| ~~DTO + Mapper + Service/Impl + Controller~~ | `StudyYear` | ✅ Готово (issue #33) — курс обучения 1..N, привязан к `Program` (N = `Program.durationOfStudy.years`) |
| ~~DTO + Mapper + Service/Impl + Controller~~ | `Semester` | ✅ Готово (issue #34) — осенний/весенний, привязан к `StudyYear`, хранит `startDate` для расчёта чётности недели |
| ~~DTO + Mapper + Service/Impl + Controller~~ | `Group` | ✅ Готово (issue #35) — студенческая группа, привязана к `Semester`; название — свободная строка (напр. «У532 КСиТ»), связи — источник истины, не текст |
| ~~Миграция + связь~~ | `students` | ✅ Готово (issue #35) — nullable `group_id` в таблице `students`, `Student` получил опциональную связь с `Group` |
| ~~DTO + Mapper + Service/Impl + Controller~~ | `WeekScheduleCycle` + `Pair` | ✅ Готово (issue #36) — циклическое расписание семестра: нечётная/чётная неделя × Пн–Пт; `Pair` ссылается на `Course` и опционально на `Teacher`, а с `Group` связан многие-ко-многим (join-таблица) — лекция может идти потоком нескольким группам разом |
| ~~Тесты сервисов~~ | `StudyYear`, `Semester`, `Group`, `WeekScheduleCycle` | ✅ Готово (issue #37) — Testcontainers-тесты по образцу существующих сервисов, включая сценарий потока (один `Pair` — несколько `Group`) написаны вместе с реализацией каждой сущности |

### 4. Лекции

*Lecture*

Зависит от Этапа 3 — конкретная лекция создаётся либо вручную, либо генерируется на дату из шаблона `Pair`.
⚠️ Issue #30 создан ещё под старую линейную схему («CRUD, привязка к обязательному Course») — при взятии в
работу стоит перепроверить его описание на соответствие этому этапу и `TARGET.md`.

| Задача | Модуль | Результат |
|---|---|---|
| DTO + Mapper + Service/Impl + Controller | `Lecture` | issue #30 — CRUD; расписание (`scheduledTime`, `durationMinutes`); опция создания на дату из шаблона `Pair` |
| Решить вопрос прав доступа | `security.Role` | issue #31 — ввести `ROLE_TEACHER` в `@PreAuthorize` либо явно закрепить создание курсов/лекций за ADMIN |
| Тесты сервисов | `Lecture` | issue #32 — покрытие CRUD, связи с Course/Teacher, генерации из `Pair` |

### 5. Зачисление и посещаемость

*Enrollment → LectureAttendance*

Второй слой из README. LectureAttendance опирается на факт зачисления, поэтому Enrollment идёт первым.
С появлением `Group` (Этап 3) зачисление и посещаемость должны уметь работать сразу на всю группу, а не
только на одного студента — иначе на группу из 25 человек придётся дёргать эндпоинт 25 раз.

| Задача | Модуль | Результат |
|---|---|---|
| DTO + Mapper + Service/Impl + Controller | `Enrollment` | Зачисление/отчисление студента на курс, список по студенту и по курсу; заменить свободную строку `status` на enum (ACTIVE/COMPLETED/DROPPED) |
| Массовая операция | `Enrollment` | Зачисление всей `Group` на курс разом, а не по одному студенту |
| DTO + Mapper + Service/Impl + Controller | `LectureAttendance` | Отметка посещения, статистика по студенту/лекции/курсу |
| Бизнес-инвариант | `LectureAttendance` | Нельзя отметить посещение, если студент не зачислен на курс этой лекции |
| Сквозной тест | `Enrollment` + `Attendance` | Полный сценарий: зачисление группы → лекция из `Pair` → посещаемость → выборка статистики |

### 6. Прод-готовность

*деплой, конфиг, безопасность*

Всё API из README работает — теперь закрыть то, что мешает поднять это за пределами локальной машины разработчика.

| Задача | Модуль | Результат |
|---|---|---|
| Секреты в переменные окружения | `application.yml`, `docker-compose` | DB-креды и admin-креды Keycloak — через env, не в репозитории |
| Dockerfile приложения | `docker-compose.yml` | Сам сервис поднимается вместе с Postgres/Keycloak одной командой |
| Явная настройка CORS | `SecurityConfig` | Осознанный список allowed origins вместо пустого блока |
| Профили Spring | `application-dev.yml` / `-prod.yml` | Разделение локальной и боевой конфигурации |
| CI/CD | `.github/workflows` | Сборка Docker-образа поверх текущего compile+test, health-check через Actuator |

---

## Что считается MVP

- **Все сущности из README рабочие** — Course, Lecture, Enrollment, LectureAttendance получают полноценный API — этапы 2, 4–5
- **Циклическое расписание работает** — StudyYear/Semester/Group/WeekScheduleCycle реализованы, Lecture может быть сгенерирована из Pair — этап 3–4
- **Тестовое покрытие ключевых сервисов** — каждый сервис (существующий и новый) получает Testcontainers-тесты по единому образцу
- **Согласованные модули** — University и Faculty приведены к паттерну interface + Impl, единый контракт ошибок
- **Деплой и прод-конфигурация** — секреты вне репозитория, приложение поднимается контейнером, CI собирает образ

---

Документ живой — по мере закрытия задач статусы в аудите и пилюли этапов стоит обновлять вручную.

**Статус на 18.08.2026:** Этапы 1–3 закрыты. Этап 3 «Циклическое расписание» реализован целиком:
`StudyYear` (#33), `Semester` (#34), `Group` + связь со `Student` (#35), `WeekScheduleCycle`/`Pair` с
многие-ко-многим к `Group` (#36), тесты написаны вместе с каждой сущностью (#37). Заодно исправлена
обнаруженная попутно отсутствующая колонка `courses.teacher_id` (миграция V7). Полный `mvn test` —
115/115 зелёных.

**Следующий шаг → Этап 4 (Лекции)** — начиная с #30 (`Lecture`), не забыть перепроверить его описание на
соответствие циклическому расписанию перед тем как браться за реализацию.
