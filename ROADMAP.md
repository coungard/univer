# Roadmap — Univer Backend

> Univer · Backend · Дорожная карта
> От текущего кода до рабочего MVP

Путь по этапам без привязки к календарю — соло-разработка, один регистратор задач за раз.
Порядок этапов задан зависимостями между модулями, а не желаемой скоростью.

- **Ветка:** `add_teachers`
- **Аудит по коду от:** 17.08.2026
- **Формат:** этапы, не спринты

---

## Аудит текущего состояния

| Модуль | Статус | Комментарий |
|---|---|---|
| `University` | ⚠️ Долг | CRUD работает, но сервис без интерфейса, поля через `@Autowired` — выбивается из паттерна проекта |
| `Faculty` | ✅ Готово | Полный стек по эталонному паттерну (interface + Impl, `ResourceNotFoundException`), покрыт тестами |
| `Department` | ✅ Готово | Полный стек по эталонному паттерну, покрыт тестами |
| `Program` | ✅ Готово | Полный стек + insert-скрипт данных, покрыт тестами |
| `Student` | ✅ Готово | Полный стек, регистрация через Keycloak, единственный модуль с тестами и пагинацией |
| `Teacher` | ✅ Готово | Полный стек, регистрация через Keycloak, покрыт тестами (кроме `registerTeacher` — баг, см. находки) |
| `Course` | ❌ Нет API | Есть entity, repository и таблица в схеме — нет DTO/service/controller |
| `Lecture` | ❌ Нет API | Есть entity, repository и таблица в схеме — нет DTO/service/controller |
| `Enrollment` | ❌ Нет API | Есть entity, repository и таблица в схеме — нет DTO/service/controller |
| `LectureAttendance` | ❌ Нет API | Есть entity, repository и таблица в схеме — нет DTO/service/controller |

**Находки:**

- **Роль TEACHER отсутствует** — сущность Teacher есть, но `security.Role` знает только `ROLE_ADMIN` и `ROLE_STUDENT`. Вопрос, кто управляет курсами/лекциями, не решён.
- **`TeacherServiceImpl.registerTeacher` сломан** — `TeacherValidator` проверяет `departmentId` через `DepartmentRepository`, а сам метод тут же ищет по этому же ID факультет через `FacultyRepository`; `Teacher` вообще не связан с `Department`. На реальных данных ID кафедры и ID факультета не совпадают, поэтому `POST /api/v1/teachers/register` всегда падает с «Faculty not found». См. issue #28.
- **CORS не настроен явно** — в `SecurityConfig` вызов `.cors(cors -> {})` ничего не задаёт, поведение зависит от дефолтов Spring.
- **Секреты в открытом виде** — пароли Postgres и admin-креды Keycloak лежат прямо в `application.yml` и `docker-compose.yml`.
- **Само приложение не в docker-compose** — файл поднимает только Postgres и Keycloak, Dockerfile для самого сервиса отсутствует.
- **Пагинация только у Student** — остальные списковые эндпоинты отдают всё разом.

---

## Этапы

### 1. Фундамент

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
| Пагинация списков *(не блокирует MVP)* | `University`, `Faculty`, `Department`, `Program`, `Teacher` | `Pageable` вместо полной выдачи, как уже сделано у Student |

### 2. Курсы и лекции

*Course → Lecture*

Первый слой из заявленного в README, которого сейчас нет вообще. Lecture зависит от Course, поэтому Course идёт первым.

| Задача | Модуль | Результат |
|---|---|---|
| DTO + Mapper + Service/Impl + Controller | `Course` | CRUD, привязка к обязательному Department и опциональному Teacher |
| DTO + Mapper + Service/Impl + Controller | `Lecture` | CRUD, привязка к обязательному Course, расписание (`scheduledTime`, `durationMinutes`) |
| Решить вопрос прав доступа | `security.Role` | Ввести `ROLE_TEACHER` либо явно закрепить создание курсов/лекций за ADMIN |
| Тесты сервисов | `Course`, `Lecture` | Покрытие CRUD и связей с Department/Teacher |

### 3. Зачисление и посещаемость

*Enrollment → LectureAttendance*

Второй слой из README. LectureAttendance опирается на факт зачисления, поэтому Enrollment идёт первым.

| Задача | Модуль | Результат |
|---|---|---|
| DTO + Mapper + Service/Impl + Controller | `Enrollment` | Зачисление/отчисление студента на курс, список по студенту и по курсу; заменить свободную строку `status` на enum (ACTIVE/COMPLETED/DROPPED) |
| DTO + Mapper + Service/Impl + Controller | `LectureAttendance` | Отметка посещения, статистика по студенту/лекции/курсу |
| Бизнес-инвариант | `LectureAttendance` | Нельзя отметить посещение, если студент не зачислен на курс этой лекции |
| Сквозной тест | `Enrollment` + `Attendance` | Полный сценарий: зачисление → лекция → посещаемость → выборка статистики |

### 4. Прод-готовность

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

- **Все сущности из README рабочие** — Course, Lecture, Enrollment, LectureAttendance получают полноценный API — этапы 2–3
- **Тестовое покрытие ключевых сервисов** — каждый сервис (существующий и новый) получает Testcontainers-тесты по единому образцу
- **Согласованные модули** — University и Faculty приведены к паттерну interface + Impl, единый контракт ошибок
- **Деплой и прод-конфигурация** — секреты вне репозитория, приложение поднимается контейнером, CI собирает образ

---

Документ живой — по мере закрытия задач статусы в аудите и пилюли этапов стоит обновлять вручную.

**Следующий шаг → Этап 1**
