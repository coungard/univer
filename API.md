# API.md — Справочник эндпоинтов REST API

> Краткое описание всех эндпоинтов `/api/v1/...` (метод, путь, авторизация, тело запроса/ответа) — для
> переноса в проект мобильного приложения. Про то, как мобильный клиент должен получать JWT (Authorization
> Code + PKCE, публичный Keycloak-клиент `univer-mobile`), см. `MOBILE.md` — этот документ его не дублирует
> и описывает только сам HTTP API, который тот JWT авторизует.

---

## Общие сведения

- **Base URL (dev):** `http://localhost:8023/api/v1` (порт из `application.yml`, `server.port`; в проде —
  своя конфигурация, аналогично `auth-server-url` в `MOBILE.md`).
- **Авторизация:** для всех эндпоинтов, кроме явно помеченных «Public» ниже, обязателен заголовок
  `Authorization: Bearer <access_token>` с валидным JWT от Keycloak (`univer-realm`). Эндпоинт без роли в
  колонке «Auth» всё равно требует валидный JWT (`anyRequest().authenticated()` в `SecurityConfig`) — просто
  без ограничения по конкретной роли.
- **Роли:** `ADMIN`, `TEACHER`, `STUDENT` используются в проверках на эндпоинтах ниже (в Keycloak заведены
  также `APPLICANT`, `GUEST`, но текущий API их нигде не проверяет).
- **Content-Type:** `application/json` и для тела запроса, и для ответа.
- **ID сущностей:** везде `UUID` (строка вида `a8f2e7b1-1c2d-4e3f-8a9b-1c2d3e4f5a6b`).
- **Даты/время:** `LocalDate` → `"2026-09-01"`, `LocalDateTime` → `"2026-09-01T09:00:00"`, `LocalTime` →
  `"09:00:00"`, `Instant` (аудит-поля `createdAt`/`updatedAt`) → ISO-8601 с зоной, например
  `"2026-08-30T10:15:30.123456Z"`.

### Пагинация

Списковые эндпоинты принимают query-параметры `page` (по умолчанию `0`) и `size` (по умолчанию `10`) и
возвращают стандартный Spring Data `Page<T>`:

```json
{
  "content": [ /* массив DTO */ ],
  "totalElements": 42,
  "totalPages": 5,
  "number": 0,
  "size": 10,
  "first": true,
  "last": false,
  "numberOfElements": 10,
  "empty": false
}
```
(плюс служебные поля `pageable`, `sort` от Spring Data — обычно мобильному клиенту не нужны).

### Формат ошибок

| Статус | Когда | Тело |
|---|---|---|
| `400 Bad Request` | Невалидное тело запроса (`@Valid`, аннотации на полях DTO) | `{ "имяПоля": "текст ошибки", ... }` — плоская карта поле → сообщение |
| `401 Unauthorized` | Нет токена / токен невалиден или просрочен | стандартный ответ Spring Security (пустое тело или `WWW-Authenticate` заголовок) |
| `403 Forbidden` | Токен валиден, но роли не хватает (`@PreAuthorize`) | стандартный ответ Spring Security |
| `404 Not Found` | Сущность не найдена (`ResourceNotFoundException`) | `{ "timestamp", "status": 404, "error": "Not Found", "message", "path": "/" }` |
| `409 Conflict` | Значение уже занято: email или логин при регистрации, название группы в семестре, название факультета в университете, название предмета на факультете (`ConflictException`) | `{ "timestamp", "status": 409, "error": "Conflict", "message", "field", "path": "/" }` — `field` называет поле запроса с занятым значением: `"email"`, `"username"`, `"name"` или `"title"` (у предмета); `null`, если определить не удалось. Для названия группы, факультета или предмета в теле есть ещё `id` — ID уже существующей записи с таким названием; для email и логина поля `id` нет |
| `422 Unprocessable Entity` | Бизнес-валидация не прошла (`ValidationException`) | то же тело, что и 404 (`"status": 422`), поле `"error"` тоже всегда `"Not Found"` — известная неточность в `GlobalExceptionHandler`, не полагайтесь на текст `error`, только на `status`/`message` |
| `500 Internal Server Error` | Непредвиденная ошибка сервера | `{ "timestamp", "status": 500, "error": "Internal Server Error", "message": "Внутренняя ошибка сервера", "requestId", "path" }` — без деталей ошибки; `path` здесь — реальный путь запроса |

Во всех случаях выше с телом (`400`/`404`/`409`/`422`) поле `path` в теле **не** содержит реальный путь запроса —
всегда захардкожено `"/"` либо отсутствует (для `400`). Определять эндпоинт нужно по контексту запроса на
стороне клиента, не по телу ответа.

### Идентификатор запроса (`X-Request-Id`)

Каждый ответ, включая ошибки и `401`/`403`, несёт заголовок `X-Request-Id`. Клиент может прислать свой
id в этом же заголовке (до 64 символов: латиница, цифры, `.`, `_`, `-`) — сервер вернёт его как есть,
иначе сгенерирует UUID. По этому id в логах сервера находятся все строки запроса, включая ошибку `500`
(в её теле он же — поле `requestId`). Мобильному клиенту стоит показывать или отправлять его в отчёте
об ошибке.

---

## Схемы данных (DTO)

Одна и та же схема, как правило, используется и для запроса (create/update), и для ответа — поля,
специфичные для запроса или для ответа, отмечены отдельно. `★` — поле обязательно (`@NotNull`/`@NotBlank`
и т.п.) при создании/обновлении.

### AddressDto
`id`, `address`★, `country`★, `region`★, `city`★, `street`★, `postalCode`, `phone`, `email`, `website`.
`id` — если передан, используется существующий адрес; если нет — создаётся новый по остальным полям.

### UniversityDto
`id`, `name`, `description`, `rector`, `foundingYear`, `studentCount`, `createdAt`, `updatedAt` (только
в ответе), `address: AddressDto`, `regionId`★, `facultyCount` (только в ответе), `faculties: FacultyDto[]` (в ответе; при создании/обновлении
можно не передавать — по умолчанию `[]`). `rector`/`foundingYear`/`studentCount` необязательны (`foundingYear`,
если передан, — не меньше 1000; `studentCount`, если передан, — не отрицательный). `regionId` — ID региона
из `GET /regions`, обязателен: регион есть у каждого вуза (issue #80). Без `regionId` `POST`/`PUT`
вернут `400`, с несуществующим — `404`. Текстовый `address.region` от `regionId` не зависит.

`facultyCount` (только в ответе) — число факультетов университета, целое, `0`, если факультетов нет
(issue #94). Учитывает и факультеты, созданные студентами через `POST /students/me/faculty`. В отличие
от `studentCount`, который вводит администратор, значение вычисляется сервером: в теле `POST`/`PUT`
поле игнорируется. Приходит во всех ответах с `UniversityDto`, включая каждую строку
`GET /universities`, — запрашивать `GET /faculties/university/{id}` ради подписи «Факультетов пока
нет» не нужно.

### RegionDto (только ответ)
`id`, `code` (двузначный код субъекта РФ, например `05`), `name` (официальное название, например
`Республика Дагестан`).

### FacultyDto
`id`, `name`, `description`, `universityId`, `departments: DepartmentDto[]` (по умолчанию `[]`),
`createdByStudentId` (только в ответе: ID студента, создавшего факультет через
`POST /students/me/faculty`; у факультетов, заведённых администратором, поля в ответе нет; в запросах
игнорируется). Название уникально в пределах университета без учёта регистра и крайних пробелов.

### CreateStudentFacultyRequest (только запрос, `POST` и `PUT /students/me/faculty`)
`name`★ (до 255 символов; крайние пробелы отбрасываются). Университета в запросе нет — он берётся из
профиля студента.

### DepartmentDto
`id`, `name`★, `description`, `facultyId`.

### StudyYearDto
`id`, `facultyId`★, `yearNumber`★ (`≥ 1`). Учебный год — это курс факультета («1 курс», «2 курс»);
пара `facultyId` + `yearNumber` уникальна.

### SemesterDto
`id`, `studyYearId`★, `type: SemesterType`★, `startDate`★, `endDate`★.
`SemesterType` (enum): `AUTUMN` | `SPRING`.

### WeekScheduleCycleDto
`id`, `semesterId`★, `status: WeekScheduleCycleStatus` (только в ответе — при создании форсируется
`DRAFT` независимо от присланного значения, менять — только через `PUT /{id}/status`). Циклическое
расписание семестра — контейнер для шаблонов `Pair`. У семестра не больше одного цикла.
`WeekScheduleCycleStatus` (enum): `DRAFT` (черновик, доступен для правок `ADMIN` и `STUDENT` своей
группы) | `AGREED` (согласовано, правки только `ADMIN`).

### UpdateWeekScheduleCycleStatusRequest (только запрос, `PUT /week-schedule-cycles/{id}/status`)
`status: WeekScheduleCycleStatus`★.

### BellScheduleEntryDto
`id`, `universityId` (`null` = дефолт для университетов без своей записи на этот `pairNumber`),
`pairNumber`★ (`≥ 1`), `startTime`★, `endTime`★. Записи по умолчанию заведены на пары 1–8.

### PairDto
`id`, `weekScheduleCycleId`★, `dayOfWeek`★ (`MONDAY`…`SUNDAY`), `weekParity: WeekParity`★,
`pairNumber`★ (`≥ 1`), `startTime` (опционально — если не задано, подставляется из
`BellScheduleEntry` по университету группы и `pairNumber`), `endTime`, `courseId`★, `teacherId`, `room`,
`groupIds: UUID[]`★ (непусто). Шаблон повторяющегося занятия в циклическом расписании — из него
генерируются конкретные `Lecture` на даты.
`WeekParity` (enum): `ODD` (нечётная неделя) | `EVEN` (чётная) | `BOTH` (каждую неделю).

### GroupDto
`id`, `semesterId`★, `name`★ (до 64 символов; крайние пробелы отбрасываются), `fullName` (полная
расшифровка названия, например «Разработка программных и информационных систем» для «У530»;
необязательна, до 255 символов, пустая строка сохраняется как `null`), `createdByStudentId` (только в
ответе: ID студента, создавшего группу через `POST /students/me/group`; `null` у групп, заведённых
администратором; в запросах игнорируется). Название уникально в пределах семестра без учёта регистра
и крайних пробелов.

### CreateStudentGroupRequest (только запрос, `POST` и `PUT /students/me/group`)
`name`★ (до 64 символов), `fullName` (до 255 символов) — как в `GroupDto`. Факультета, курса и
семестра в запросе нет: они берутся из профиля студента и подбираются сервером.

### CourseDto
`id`, `title`★, `description`, `departmentId`, `facultyId` (прямая привязка к факультету — для
предметов без кафедры, в том числе созданных студентами), `teacherId`, `createdByStudentId` (только в
ответе: ID студента, создавшего предмет через `POST /students/me/course`; у предметов, заведённых
администратором или преподавателем, поля в ответе нет; в запросах игнорируется). Это учебный
предмет («Физика», «Иностранный язык») — не путать с курсом как годом обучения (`yearNumber`).
Если `facultyId` заполнен, название уникально среди предметов факультета без учёта регистра и
крайних пробелов, а крайние пробелы отбрасываются.
(Сериализуется с `@JsonInclude(NON_NULL)` — `null`-поля в ответе могут отсутствовать вовсе, а не быть `null`.)

### CreateStudentCourseRequest (только запрос, `POST /students/me/course`)
`title`★ (до 255 символов). Факультета в запросе нет: он берётся из профиля студента.

### LectureDto
`id`, `title`★, `content`, `scheduledTime`★, `durationMinutes`, `courseId`★, `teacherId`, `room`,
`sourcePairId` (заполняется автоматически при генерации из `Pair` — не заполнять вручную; пусто у
занятий, созданных вручную, и у занятий с отмеченной посещаемостью, чья пара удалена), `groupIds`★
(непусто).

### GenerateLectureRequest (только запрос)
`pairId`★, `date`★.

### StudentDto
`id`, `username`★, `firstname`★, `lastname`★, `fullname`, `createdAt`, `updatedAt` (только в ответе),
`email`★ (валидный email), `enrollmentDate` (не в будущем, необязательна — назначается администратором
после регистрации, не собирается на самой регистрации), `birthday` (не в будущем), `universityId`
(`null`, пока студент не выбрал университет), `facultyId`, `yearNumber` (номер курса, `≥ 1`) — оба
только в ответе, `null`, пока не выбраны; меняются через `PATCH /students/me`, а `PUT /students/{id}`
их игнорирует, — `groupId`.

### UpdateStudentProfileRequest (только запрос, `PATCH /students/me`)
`universityId`, `facultyId`, `yearNumber` (от 1 до 6), `groupId` — все необязательны. Поле, которого нет
в теле, не меняется; поле с явным `null` очищается.

### UniversityRequestDto (только ответ)
Заявка студента на добавление университета. `id`, `name` (название, как его ввёл студент), `regionId`,
`regionName` (оба `null`, если студент региона не указал), `status: UniversityRequestStatus`,
`universityId` (университет, которым заявка закрыта; заполнен только у `COMPLETED`), `comment`
(пояснение администратора при отклонении; бывает только у `REJECTED`, и то не всегда), `studentId`,
`studentUsername`, `studentFullname` (автор заявки), `createdAt`, `updatedAt`.
`UniversityRequestStatus` (enum): `PENDING` (необработанная) | `COMPLETED` (выполнена) | `REJECTED`
(отклонена).

### SubmitUniversityRequest (только запрос, `PUT /students/me/university-request`)
`name`★ (до 255 символов; крайние пробелы отбрасываются), `regionId` (необязателен — ID региона из
`GET /regions`).

### CompleteUniversityRequest (только запрос, `POST /university-requests/{id}/complete`)
`universityId`★.

### RejectUniversityRequest (только запрос, `POST /university-requests/{id}/reject`)
`comment` (необязателен, до 500 символов; крайние пробелы отбрасываются, пустой сохраняется как
`null`). Тело можно не передавать вовсе.

### RegisterStudentRequest (только запрос, `POST /students/register`)
`username`★, `firstname`★, `lastname`★, `fullname`, `email`★, `password`★, `enrollmentDate` (не в
будущем, необязательна — форма регистрации её больше не запрашивает, см.
`coungard/univer_mobile#46`), `birthday`★ (не в будущем), `universityId` (необязателен — аккаунт
можно создать до выбора университета или вовсе без него; если передан, должен существовать, иначе
`404`). Пароль и остальные данные
регистрации уходят в Keycloak — `id` итогового `StudentDto` в ответе равен Keycloak user ID (см. флоу
регистрации в `CLAUDE.md`).

### TeacherDto
`id`, `username`★, `firstname`★, `lastname`★, `fullname`, `email`★, `phone`, `birthday`, `createdAt`,
`updatedAt` (только в ответе), `facultyId`★ (только для `PUT`/`POST /teachers` — на регистрации не
приходит, см. ниже, может быть `null` в ответе, пока кафедра не назначена), `position`★, `registered`
(`Boolean`, только в ответе — зарегистрирован ли преподаватель в Keycloak).

### RegisterTeacherRequest (только запрос, `POST /teachers/register`)
`username`★, `firstname`★, `lastname`★, `fullname`, `password`★, `email`★, `departmentId` (необязателен
— форма регистрации больше не выбирает кафедру, см. `coungard/univer_mobile#47`; кафедра
назначается позже через `PUT /teachers/{id}`), `birthday`★ (не в будущем), `position`★.

### EnrollmentDto
`studentId`★, `courseId`★, `enrolledAt` (заполняется сервером при `POST`), `status: EnrollmentStatus`.
`EnrollmentStatus` (enum): `ACTIVE` | `COMPLETED` | `DROPPED`. У `Enrollment` нет собственного `id` —
идентифицируется парой (`studentId`, `courseId`).

### LectureAttendanceDto
`studentId`★, `lectureId`★, `attended` (`boolean`).

### AttendanceStatsDto (только ответ)
`totalMarked` (`long`), `attendedCount` (`long`), `attendanceRate` (`double`, `0..1`).

### AuditEventDto (только ответ)
`id`, `occurredAt` (`Instant`), `actorId` (subject JWT автора; `null` у анонимных действий — регистрации),
`actorRole` (старшая роль автора: `ADMIN` | `TEACHER` | `STUDENT`), `action: AuditAction`,
`entityType: AuditEntityType`, `entityId`, `requestId` (`X-Request-Id` запроса), `details` (объект, набор
полей зависит от действия; может быть `null`).
`AuditAction` (enum): `REGISTER` | `CREATE` | `UPDATE` | `DELETE` | `STATUS_CHANGE` | `MARK` | `COMPLETE` |
`REJECT`. `AuditEntityType` (enum): `BELL_SCHEDULE_ENTRY` | `COURSE` | `DEPARTMENT` | `FACULTY` | `GROUP` |
`LECTURE` | `LECTURE_ATTENDANCE` | `PAIR` | `SEMESTER` | `STUDENT` | `STUDY_YEAR` | `TEACHER` | `UNIVERSITY` |
`UNIVERSITY_REQUEST` | `WEEK_SCHEDULE_CYCLE`.

---

## Universities — `/api/v1/universities`

| Метод | Путь | Auth | Тело запроса | Тело ответа |
|---|---|---|---|---|
| GET | `/` | публично | — (`?search&regionId&page&size`) | `Page<UniversityDto>` |
| GET | `/{id}` | публично | — | `UniversityDto` |
| POST | `/` | `ADMIN` | `UniversityDto` | `201` + `UniversityDto` |
| PUT | `/{id}` | `ADMIN` | `UniversityDto` | `UniversityDto` |
| DELETE | `/{id}` | `ADMIN` | — | `204` |

> `GET`-эндпоинты сделаны публичными намеренно: экран регистрации студента ещё не имеет токена,
> но должен дать выбрать университет (`RegisterStudentRequest.universityId`) до входа в систему.
>
> `search` — необязательный, регистронезависимый поиск по подстроке в `name`; без него — все
> университеты постранично, как раньше.
>
> `regionId` — необязательный фильтр по региону (ID из `GET /regions`), сочетается с `search`.

## UniversityRequests — `/api/v1/university-requests`

Заявки студентов на добавление университета, которого нет в справочнике (issue #92). Студент
оставляет и смотрит свою заявку через `PUT`/`GET /students/me/university-request`; здесь — разбор
заявок администратором.

| Метод | Путь | Auth | Тело запроса | Тело ответа |
|---|---|---|---|---|
| GET | `/` | `ADMIN` | — (`?status&page&size`) | `Page<UniversityRequestDto>` |
| POST | `/{id}/complete` | `ADMIN` | `CompleteUniversityRequest` | `UniversityRequestDto` |
| POST | `/{id}/reject` | `ADMIN` | `RejectUniversityRequest` (необязательно) | `UniversityRequestDto` |

- **`GET /`** — сначала самые давние заявки. `status` — необязательный фильтр (`PENDING`,
  `COMPLETED`, `REJECTED`); без него — заявки во всех статусах. Одинаковые заявки разных студентов
  не группируются — каждая закрывается отдельно.
- **`POST /{id}/complete`** — закрыть заявку, указав университет: только что созданный через
  `POST /universities` или уже существующий, если студент его просто не нашёл. Заявка переходит в
  `COMPLETED`, университет проставляется в профиль автора, **если `universityId` там всё ещё
  пуст**. Заявка без региона допустима, но университет без региона создать нельзя — регион
  администратор указывает сам при создании университета.
- **`POST /{id}/reject`** — отклонить заявку (не университет, дубль, мусор): статус `REJECTED`,
  профиль автора не меняется. В необязательном `comment` администратор поясняет, что не так, —
  студент увидит его в `GET /students/me/university-request` и поймёт, исправить ли название или
  искать университет под другим именем.
- `GET /?status=` с неизвестным статусом — `400`.
- Закрыть или отклонить можно только необработанную заявку: для уже обработанной — `422`.
  Несуществующая заявка или университет — `404`.

## Regions — `/api/v1/regions`

| Метод | Путь | Auth | Тело запроса | Тело ответа |
|---|---|---|---|---|
| GET | `/` | публично | — | `RegionDto[]` |

> Справочник всех 89 субъектов РФ, отсортирован по названию, без пагинации. Наполняется миграцией
> (`V24__insert_regions.sql`), эндпоинтов изменения нет. Публичный по той же причине, что и
> `GET /universities`: нужен на экране регистрации до входа.

## Faculties — `/api/v1/faculties`

| Метод | Путь | Auth | Тело запроса | Тело ответа |
|---|---|---|---|---|
| POST | `/` | `ADMIN` | `FacultyDto` | `201` + `FacultyDto` |
| GET | `/university/{universityId}` | публично | — (`?page&size`) | `Page<FacultyDto>` |
| GET | `/{id}` | публично | — | `FacultyDto` |
| PUT | `/{id}` | `ADMIN` | `FacultyDto` | `FacultyDto` |
| DELETE | `/{id}` | `ADMIN` | — | `204` |

- **`POST /`, `PUT /{id}`, `DELETE /{id}`** — только `ADMIN` (с issue #87; раньше были доступны любому
  аутентифицированному пользователю). Студент создаёт факультет через `POST /students/me/faculty`.
- **`POST /`, `PUT /{id}`** — если в университете уже есть факультет с таким названием (без учёта
  регистра и крайних пробелов) — `409` с `field: "name"` и `id` существующего факультета (см. «Формат
  ошибок»).
- У факультета, созданного студентом, нет описания и кафедр (`departments: []`) — как и у большинства
  факультетов справочника.

> `GET`-эндпоинты остаются публичными по историческим причинам (раньше — выбор кафедры на
> регистрации преподавателя; с issue #79 форма регистрации кафедру больше не запрашивает,
> `departmentId` необязателен — см. `RegisterTeacherRequest` выше), а также потому что на
> факультеты/кафедры ссылаются списки курсов.
>
> В отличие от большинства ресурсов, у `Departments` (ниже) нет `@PreAuthorize` на
> create/update/delete — эти операции доступны любому аутентифицированному пользователю, не только `ADMIN`.
> При переносе в мобильное приложение стоит перепроверить это перед тем, как показывать соответствующий
> функционал не-админам — похоже на недосмотр в текущей реализации, а не сознательное решение. У
> `Faculties` этот недосмотр закрыт в issue #87.

## Departments — `/api/v1/departments`

| Метод | Путь | Auth | Тело запроса | Тело ответа |
|---|---|---|---|---|
| POST | `/` | любая роль | `DepartmentDto` | `201` + `DepartmentDto` |
| GET | `/faculty/{facultyId}` | публично | — (`?page&size`) | `Page<DepartmentDto>` |
| GET | `/university/{universityId}` | публично | — (`?page&size`) | `Page<DepartmentDto>` |
| GET | `/{id}` | публично | — | `DepartmentDto` |
| PUT | `/{id}` | любая роль | `DepartmentDto` | `DepartmentDto` |
| DELETE | `/{id}` | любая роль | — | `204` |

> `GET`-эндпоинты публичны по той же причине, что и у `Faculties`/`Universities` — нужны для выбора
> кафедры на экране регистрации преподавателя.

## StudyYears — `/api/v1/study-years`

| Метод | Путь | Auth | Тело запроса | Тело ответа |
|---|---|---|---|---|
| GET | `/` | любая роль | — (`?page&size`) | `Page<StudyYearDto>` |
| GET | `/faculty/{facultyId}` | любая роль | — (`?page&size`) | `Page<StudyYearDto>` |
| GET | `/{id}` | любая роль | — | `StudyYearDto` |
| POST | `/` | `ADMIN` | `StudyYearDto` | `201` + `StudyYearDto` |
| PUT | `/{id}` | `ADMIN` | `StudyYearDto` | `StudyYearDto` |
| DELETE | `/{id}` | `ADMIN` | — | `204` |

## Semesters — `/api/v1/semesters`

| Метод | Путь | Auth | Тело запроса | Тело ответа |
|---|---|---|---|---|
| GET | `/` | любая роль | — (`?page&size`) | `Page<SemesterDto>` |
| GET | `/study-year/{studyYearId}` | любая роль | — (`?page&size`) | `Page<SemesterDto>` |
| GET | `/{id}` | любая роль | — | `SemesterDto` |
| GET | `/{id}/week` | любая роль | — (`?date=YYYY-MM-DD`, по умолчанию — сегодня) | `SemesterWeekDto` |
| POST | `/` | `ADMIN` | `SemesterDto` | `201` + `SemesterDto` |
| PUT | `/{id}` | `ADMIN` | `SemesterDto` | `SemesterDto` |
| DELETE | `/{id}` | `ADMIN` | — | `204` |

- **`GET /{id}/week`** — номер и чётность календарной недели (пн–вс) семестра, на которую приходится
  `date`. Правило то же, что при генерации занятий (см. «Чётность недели» в разделе `Lectures`); клиентам
  не нужно повторять его у себя. `SemesterWeekDto`: `semesterId`, `date`, `weekStart` (понедельник),
  `weekEnd` (воскресенье), `inSemester`, `weekNumber`, `weekParity` (`ODD`/`EVEN`). `inSemester` — неделя
  пересекается с семестром, а не дата внутри него: при начале семестра в среду понедельник той же недели —
  уже `weekNumber: 1`. Для недель вне семестра — `200` с `inSemester: false`, `weekNumber` и `weekParity`
  — `null`, чтобы расписание можно было листать за границы семестра.

## WeekScheduleCycles — `/api/v1/week-schedule-cycles`

Циклическое расписание семестра (контейнер шаблонов `Pair`).

| Метод | Путь | Auth | Тело запроса | Тело ответа |
|---|---|---|---|---|
| GET | `/` | любая роль | — (`?page&size`) | `Page<WeekScheduleCycleDto>` |
| GET | `/semester/{semesterId}` | любая роль | — | `WeekScheduleCycleDto` |
| GET | `/{id}` | любая роль | — | `WeekScheduleCycleDto` |
| POST | `/` | `ADMIN` | `WeekScheduleCycleDto` | `201` + `WeekScheduleCycleDto` |
| PUT | `/{id}/status` | `ADMIN` | `UpdateWeekScheduleCycleStatusRequest` | `WeekScheduleCycleDto` |
| DELETE | `/{id}` | `ADMIN` | — | `204` |

- **`status`** (`WeekScheduleCycleDto`) — `DRAFT`/`AGREED`. Новый цикл всегда создаётся в `DRAFT`
  (значение `status` в теле `POST /` игнорируется). Пока `DRAFT` — `Pair` цикла может редактировать
  `ADMIN` без ограничений либо `STUDENT` своей группы; в `AGREED` — только `ADMIN` (см. раздел
  `Pairs`). Смена статуса — `PUT /{id}/status`, только `ADMIN`, переход разрешён в обе стороны
  (в т.ч. откат `AGREED` → `DRAFT` для исправлений).
- **Цикл без администратора.** `POST /` доступен только `ADMIN`, но студенту он не нужен: цикл в
  статусе `DRAFT` появляется сам, когда студент создаёт группу (`POST /students/me/group`), и по
  запросу `PUT /students/me/week-schedule-cycle` — для семестра группы студента, кем бы этот семестр
  ни был заведён.
- **`GET /semester/{semesterId}`** — если цикла у семестра нет, `404`. Для студента это не тупик:
  `PUT /students/me/week-schedule-cycle` вернёт цикл семестра его группы, создав его при отсутствии.

## BellScheduleEntries — `/api/v1/bell-schedule-entries`

Справочник «номер пары → время начала/окончания» по университетам (звонковое расписание).

| Метод | Путь | Auth | Тело запроса | Тело ответа |
|---|---|---|---|---|
| GET | `/` | любая роль | — (`?page&size`) | `Page<BellScheduleEntryDto>` |
| GET | `/university/{universityId}` | любая роль | — (`?page&size`) | `Page<BellScheduleEntryDto>` |
| GET | `/university/{universityId}/effective` | любая роль | — | `BellScheduleEntryDto[]` |
| GET | `/{id}` | любая роль | — | `BellScheduleEntryDto` |
| POST | `/` | `ADMIN` | `BellScheduleEntryDto` | `201` + `BellScheduleEntryDto` |
| PUT | `/{id}` | `ADMIN` | `BellScheduleEntryDto` | `BellScheduleEntryDto` |
| DELETE | `/{id}` | `ADMIN` | — | `204` |

- **Записи по умолчанию** (`universityId: null`) действуют для любого университета, у которого нет
  своей записи на этот номер пары. Заведены на пары 1–8: `08:30–10:00`, `10:10–11:40`, `12:20–13:50`,
  `14:00–15:30`, `15:40–17:10`, `17:20–18:50`, `19:00–20:30`, `20:40–22:10`. Править их, как и записи
  университетов, может только `ADMIN`.
- **`GET /university/{universityId}/effective`** — звонки, действующие для университета: по одной
  записи на номер пары, массив без пагинации, отсортирован по `pairNumber`. На каждый номер —
  собственная запись университета, а если её нет — запись по умолчанию (у неё `universityId: null`).
  Это то время, которое сервер подставит в пару, созданную без `startTime`/`endTime`, — его можно
  показывать рядом с номером пары. У несуществующего университета — записи по умолчанию, не ошибка.
- **`GET /university/{universityId}`** — только собственные записи университета; если их нет — пустая
  страница, записи по умолчанию сюда не попадают.

## Pairs — `/api/v1/pairs`

Шаблоны занятий циклического расписания (день недели + чётность недели + номер пары), из которых
генерируются конкретные `Lecture`.

| Метод | Путь | Auth | Тело запроса | Тело ответа |
|---|---|---|---|---|
| GET | `/` | любая роль | — (`?page&size`) | `Page<PairDto>` |
| GET | `/week-schedule-cycle/{weekScheduleCycleId}` | любая роль | — (`?page&size`) | `Page<PairDto>` |
| GET | `/group/{groupId}` | любая роль | — (`?page&size`) | `Page<PairDto>` — расписание группы |
| GET | `/{id}` | любая роль | — | `PairDto` |
| POST | `/` | `ADMIN`/`STUDENT`¹ | `PairDto` | `201` + `PairDto` |
| PUT | `/{id}` | `ADMIN`/`STUDENT`¹ | `PairDto` | `PairDto` |
| DELETE | `/{id}` | `ADMIN`/`STUDENT`¹ | — | `204` |

¹ `STUDENT` может писать `Pair` только своей группы (`groupIds` должен состоять ровно из его группы,
без потока на другие группы) и только пока `WeekScheduleCycle.status == DRAFT` (см. раздел
`WeekScheduleCycles`) — как для текущего, так и для нового состояния пары при `PUT`. Нарушение любого
из двух условий — `422` с соответствующим сообщением. `ADMIN` не ограничен ни группой, ни статусом.

`POST`/`PUT /pairs` также проверяют конфликты преподаватель/аудитория — для **всех** ролей, включая
`ADMIN`: в пределах одного `weekScheduleCycleId` два `Pair` не могут одновременно иметь совпадающий
`dayOfWeek`, пересекающуюся `weekParity` (с учётом `BOTH`) и пересекающиеся `[startTime, endTime)`,
если при этом у них совпадает `teacherId` или `room`. Нарушение — `422`. Защиты от гонки при
параллельных сохранениях нет (осознанно, для MVP).

**Время пары.** `startTime`/`endTime` в `POST`/`PUT /pairs` необязательны: если хотя бы одно не
передано, оба подставляются из звонков по `pairNumber` — тех же, что отдаёт
`GET /bell-schedule-entries/university/{universityId}/effective`. Университет определяется по группе
пары (группа → семестр → учебный год → факультет), а не по кафедре предмета: у предмета кафедры может
не быть. Если на этот номер пары нет ни записи университета, ни записи по умолчанию (номер больше 8
и своей записи нет) — `422`. Переданные явно `startTime` и `endTime` сохраняются как есть.

**Занятия пары.** `PUT` и `DELETE /pairs/{id}` сами приводят в соответствие занятия (`Lecture`),
сгенерированные из этой пары (`LectureDto.sourcePairId`), — клиенту не нужно ни удалять их, ни
запускать генерацию заново:

- **`PUT /{id}`** — занятия пары удаляются и создаются заново под её новое состояние (день недели,
  чётность, время, предмет, преподаватель, аудитория, группы) на все подходящие даты семестра, как в
  `POST /lectures/generate/semester/{id}`. У созданных заново занятий новые `id`. Если из пары
  занятия ещё не генерировались, `PUT` их не создаёт.
- **`DELETE /{id}`** — занятия пары удаляются вместе с ней.
- **Занятия с отмеченной посещаемостью** (есть хотя бы одна запись `LectureAttendance`) не
  удаляются и не меняются: при `PUT` остаются на прежних дате и времени, а второе занятие той же
  пары на эту дату не создаётся; при `DELETE` остаются в расписании с пустым `sourcePairId`.
- Прошедшие и будущие занятия не различаются: занятие без отметок посещаемости пересоздаётся или
  удаляется независимо от даты.
- Занятия, созданные вручную (`POST /lectures`, без `sourcePairId`), не затрагиваются.

## Groups — `/api/v1/groups`

| Метод | Путь | Auth | Тело запроса | Тело ответа |
|---|---|---|---|---|
| GET | `/` | любая роль | — (`?facultyId&yearNumber&page&size`) | `Page<GroupDto>` |
| GET | `/semester/{semesterId}` | любая роль | — (`?page&size`) | `Page<GroupDto>` |
| GET | `/{id}` | любая роль | — | `GroupDto` |
| POST | `/` | `ADMIN` | `GroupDto` | `201` + `GroupDto` |
| PUT | `/{id}` | `ADMIN` | `GroupDto` | `GroupDto` |
| DELETE | `/{id}` | `ADMIN` | — | `204` |

- **`GET /?facultyId=…&yearNumber=…`** — группы факультета на указанном курсе,
  одним запросом вместо обхода цепочки учебный год → семестр → группа
  (последний шаг регистрации студента). Оба параметра необязательны и работают по отдельности: только
  `facultyId` — все курсы факультета, только `yearNumber` — этот курс на всех факультетах. Без обоих
  параметров — прежнее поведение: все группы, без отбора по семестру.
- С любым из фильтров в выдачу попадают только группы **актуального семестра**. Он выбирается на
  сегодняшнюю дату отдельно для каждого учебного года (факультет + курс), в три ступени:
  1. семестр, который идёт сейчас (`startDate ≤ сегодня ≤ endDate`, границы включаются);
  2. если такого нет (каникулы) — ближайший будущий;
  3. если нет и будущих (следующий семестр ещё не заведён) — последний закончившийся.
- Сортировка при фильтрах — по `name`. Если подходящих групп нет (в том числе `facultyId` не
  существует) — `200` с пустой страницей, не ошибка. Студент в этом случае может создать группу сам:
  `POST /students/me/group`.
- **`POST /`, `PUT /{id}`** — если в семестре уже есть группа с таким названием (без учёта регистра и
  крайних пробелов) — `409` с `field: "name"` и `id` существующей группы (см. «Формат ошибок»).

## Courses — `/api/v1/courses`

| Метод | Путь | Auth | Тело запроса | Тело ответа |
|---|---|---|---|---|
| GET | `/` | любая роль | — (`?facultyId&page&size`) | `Page<CourseDto>` |
| GET | `/department/{departmentId}` | любая роль | — (`?page&size`) | `Page<CourseDto>` |
| GET | `/{id}` | любая роль | — | `CourseDto` |
| POST | `/` | `ADMIN`/`TEACHER` | `CourseDto` | `201` + `CourseDto` |
| PUT | `/{id}` | `ADMIN`/`TEACHER` | `CourseDto` | `CourseDto` |
| DELETE | `/{id}` | `ADMIN`/`TEACHER` | — | `204` |

- **`GET /?facultyId=…`** — предметы факультета для выбора при создании пары: привязанные к нему
  напрямую (`facultyId`, в том числе созданные студентами) и привязанные через его кафедры
  (`departmentId`). Сортировка — по `title`. Предметы других факультетов и предметы без привязки в
  выдачу не попадают. Если подходящих предметов нет (в том числе `facultyId` не существует) — `200`
  с пустой страницей, не ошибка. Студент в этом случае может создать предмет сам:
  `POST /students/me/course`. Без параметра — прежнее поведение: все предметы всех университетов.
- **Предметы без привязки.** У предмета без `facultyId` и без `departmentId` факультета нет — он
  виден только в общем списке `GET /`. Чтобы он попал в список факультета, администратор указывает
  `facultyId` через `PUT /{id}`.
- **`POST /`, `PUT /{id}`** с `facultyId`:
  - на факультете уже есть предмет с таким названием (без учёта регистра и крайних пробелов; считаются
    и предметы его кафедр) — `409` с `field: "title"` и `id` существующего предмета (см. «Формат
    ошибок»);
  - указаны и `departmentId`, и `facultyId`, а кафедра относится к другому факультету — `422`;
  - `PUT` заменяет привязку целиком: запрос без `facultyId` снимает её, в том числе у предмета,
    созданного студентом.
  Без `facultyId` проверки названия нет, как и раньше.

## Lectures — `/api/v1/lectures`

Конкретные занятия на дату/время — либо создаются вручную, либо генерируются из шаблона `Pair`.

| Метод | Путь | Auth | Query/Path | Тело запроса | Тело ответа |
|---|---|---|---|---|---|
| GET | `/` | любая роль | `?page&size` | — | `Page<LectureDto>` |
| GET | `/course/{courseId}` | любая роль | `?page&size` | — | `Page<LectureDto>` |
| GET | `/group/{groupId}` | любая роль | `?page&size` | — | `Page<LectureDto>` |
| GET | `/me` | `STUDENT` | `?page&size` | — | `Page<LectureDto>`, отсортировано по `scheduledTime` ASC |
| GET | `/{id}` | любая роль | — | — | `LectureDto` |
| POST | `/` | `ADMIN`/`TEACHER` | — | `LectureDto` | `201` + `LectureDto` |
| POST | `/generate` | `ADMIN`/`TEACHER`/`STUDENT`² | — | `GenerateLectureRequest` | `201` + `LectureDto` |
| POST | `/generate/semester/{weekScheduleCycleId}` | `ADMIN`/`TEACHER`/`STUDENT`² | — | — | `200` + `LectureDto[]` |
| PUT | `/{id}` | `ADMIN`/`TEACHER` | — | `LectureDto` | `LectureDto` |
| DELETE | `/{id}` | `ADMIN`/`TEACHER` | — | — | `204` |

- **`GET /me`** — расписание текущего студента: `id` берётся из `sub` в JWT (равен `Student.id`, см.
  «Флоу регистрации» в `CLAUDE.md`), а не из path/query. Возвращает лекции группы, к которой привязан
  студент; если студент ещё не привязан к группе — пустая страница, а не ошибка. Это основной эндпоинт для
  экрана «моё расписание» в мобильном приложении.
- ² `STUDENT` может генерировать лекции только для своей группы: `POST /generate` — целевой `Pair`
  должен принадлежать его группе; `POST /generate/semester/{id}` — генерирует только по `Pair` своей
  группы, остальные `Pair` цикла пропускаются молча (не ошибка). Статус цикла (`DRAFT`/`AGREED`, см.
  `WeekScheduleCycles`) на генерацию не влияет. `ADMIN`/`TEACHER` не ограничены.
- **`POST /generate`** — генерирует одну `Lecture` на конкретную `date` из шаблона `Pair` (курс,
  преподаватель, группы копируются из `Pair`); `date` должна попадать в границы семестра
  (`Semester.startDate`…`Semester.endDate`) и соответствовать `dayOfWeek`/`weekParity` пары.
- **Чётность недели** — недели семестра календарные (пн–вс): неделя, в которую попадает
  `Semester.startDate`, — 1-я (нечётная), даже если семестр начался не в понедельник; дальше недели
  меняются по понедельникам. Например, при начале семестра во вторник `2026-09-01` понедельник
  `2026-09-07` — уже 2-я (чётная) неделя.
- **`POST /generate/semester/{weekScheduleCycleId}`** — генерирует лекции на весь семестр разом для всех
  `Pair` данного цикла, в границах `Semester.startDate`…`Semester.endDate`; уже сгенерированные пара+дата
  пропускаются без ошибки — вызывать повторно безопасно (идемпотентно). Дата считается занятой, если
  на неё есть занятие из этой пары на любое время.
- **Изменение и удаление `Pair`** — занятия, сгенерированные из пары, сервер сам пересоздаёт при
  `PUT /pairs/{id}` и удаляет при `DELETE /pairs/{id}` (см. «Занятия пары» в разделе `Pairs`).
  `DELETE /lectures/{id}` для этого не нужен. Генерацию нужно вызывать только для новых пар.

## Students — `/api/v1/students`

| Метод | Путь | Auth | Тело запроса | Тело ответа |
|---|---|---|---|---|
| GET | `/` | `ADMIN` | — (`?page&size`) | `Page<StudentDto>` |
| GET | `/me` | `STUDENT` | — | `StudentDto` |
| PATCH | `/me` | `STUDENT` | `UpdateStudentProfileRequest` | `StudentDto` |
| GET | `/me/university-request` | `STUDENT` | — | `UniversityRequestDto` |
| PUT | `/me/university-request` | `STUDENT` | `SubmitUniversityRequest` | `UniversityRequestDto` |
| POST | `/me/faculty` | `STUDENT` | `CreateStudentFacultyRequest` | `201` + `StudentDto` |
| PUT | `/me/faculty` | `STUDENT` | `CreateStudentFacultyRequest` | `FacultyDto` |
| POST | `/me/group` | `STUDENT` | `CreateStudentGroupRequest` | `201` + `StudentDto` |
| PUT | `/me/group` | `STUDENT` | `CreateStudentGroupRequest` | `GroupDto` |
| PUT | `/me/week-schedule-cycle` | `STUDENT` | — | `WeekScheduleCycleDto` |
| POST | `/me/course` | `STUDENT` | `CreateStudentCourseRequest` | `201` + `CourseDto` |
| GET | `/{id}` | `STUDENT` | — | `StudentDto` |
| POST | `/register` | **Public** (без токена) | `RegisterStudentRequest` | `201` + `StudentDto` |
| PUT | `/{id}` | `ADMIN` | `StudentDto` | `StudentDto` |
| DELETE | `/{id}` | `ADMIN` | — | `204` |

- **`POST /register`** — единственный публичный эндпоинт создания студента (не требует JWT). Создаёт
  пользователя в Keycloak, назначает роль `STUDENT`, сохраняет локальную сущность с `id`, равным Keycloak
  user ID. Возможные ответы: `201` успех, `400` невалидные данные, `409` email или логин уже занят — в теле `field`: `"email"` либо `"username"` (см. «Формат ошибок»). Занятость проверяется и среди студентов, и в Keycloak, где логины и email общие с преподавателями.
- **Студент без университета** (`universityId: null`) — валидное состояние: `GET /lectures/me` отдаёт
  ему пустую страницу, а не ошибку (группы у него тоже ещё нет).
- **`PUT /{id}`** — `universityId: null` в теле снимает привязку к университету (как `groupId: null` —
  к группе). Переданный `universityId` закрывает необработанную заявку студента на добавление
  университета. `facultyId` и `yearNumber` этот эндпоинт не меняет и согласованность цепочки не
  проверяет — это делает только `PATCH /me`.
- **`GET /me`** — профиль вызывающего студента; студент определяется по `sub` из JWT (как в
  `GET /lectures/me`), подставлять свой ID в `GET /{id}` не нужно.
- **`PATCH /me`** — студент сам заполняет профиль: университет → факультет → курс → группа,
  подтверждение администратора не требуется. Можно остановиться на любом шаге. Правила:
  - **Смена поля сбрасывает всё, что ниже по цепочке**: сменил университет — очищаются факультет,
    курс и группа; факультет — курс и группа; курс — группа. Поля ниже, переданные в том же запросе,
    применяются после сброса, так что перевод в другой вуз можно сделать одним запросом со всеми
    четырьмя полями. Повторная отправка текущего значения сменой не считается и ничего не
    сбрасывает. Явный `null` очищает поле и всё, что ниже.
  - **Незаполненное выше по цепочке проставляется автоматически**: `groupId` проставляет
    `facultyId` и `yearNumber` по цепочке группы (группа → семестр → учебный год → факультет),
    а `facultyId` или `groupId` — ещё и `universityId`, если он не выбран.
  - **Уже заполненное обязано согласовываться**, иначе `422` с пояснением в `message`: факультет
    должен принадлежать университету студента, группа — его факультету и курсу; курс нельзя выбрать
    без факультета.
  - Несуществующий `universityId`/`facultyId`/`groupId` — `404`; `yearNumber` вне диапазона 1–6 —
    `400`.
  - После выбора группы `GET /lectures/me` сразу отдаёт её расписание.
  - Если в профиле появился университет, необработанная заявка студента на добавление университета
    закрывается автоматически (см. `PUT /me/university-request`).
- **`PUT /me/university-request`** — студент не нашёл свой университет в `GET /universities` и
  оставляет заявку на его добавление. Сам он университеты не создаёт: заявку разбирает
  администратор (см. «UniversityRequests»). В ответе `200` и `UniversityRequestDto` в статусе
  `PENDING`.
  - У студента не больше одной необработанной заявки: повторный запрос обновляет её название и
    регион, а не создаёт вторую. `regionId`, не переданный в теле, очищается. Два одновременных
    запроса (двойное нажатие «Отправить») тоже дают одну заявку: оба получают `200` с ней.
  - Профиль заявка не меняет: `universityId` остаётся пустым, пока администратор её не закроет.
    Уведомления о закрытии нет — клиент увидит университет в `GET /me`.
  - После закрытия или отклонения заявки следующий `PUT` создаёт новую необработанную.
  - Регион необязателен: студент мог не найти и регион. Несуществующий `regionId` — `404`.
  - В профиле уже выбран университет — `422`: такая заявка закрылась бы сразу. Чтобы оставить
    заявку, университет сначала снимается через `PATCH /me` (`universityId: null`).
  - Заявка закрывается автоматически (`COMPLETED` с `universityId` выбранного университета), если
    студент, не дожидаясь администратора, выбрал университет сам через `PATCH /me` — напрямую либо
    выбором факультета или группы — или университет ему назначил администратор через `PUT /{id}`.
- **`GET /me/university-request`** — последняя заявка студента: необработанная, а если её нет —
  последняя закрытая или отклонённая. У закрытой заполнен `universityId`, у отклонённой может быть
  `comment` с причиной. Студент заявок не
  оставлял — `404`.
- **`POST /me/faculty`** — студент создаёт факультет, которого нет в
  `GET /faculties/university/{id}`, и сразу выбирает его: в ответе `StudentDto` с новым `facultyId`,
  в заголовке `Location` — адрес факультета. Подтверждение администратора не требуется, факультет
  сразу виден всем в списке факультетов университета.
  - Университет берётся из профиля студента. Если `universityId` в профиле не заполнен — `422`.
  - `yearNumber` и `groupId` в профиле сбрасываются — как при смене факультета через `PATCH /me`.
    Дальше студент выбирает курс и создаёт группу (`POST /me/group`).
  - Факультет с таким названием в этом университете уже есть (без учёта регистра и крайних
    пробелов) — `409` с `field: "name"` и `id` существующего факультета: клиент может предложить
    выбрать его через `PATCH /me`. Профиль студента при этом не меняется.
  - Один студент может создать не больше 3 факультетов; четвёртая попытка — `422`. Считаются
    существующие факультеты, созданные им (`FacultyDto.createdByStudentId`), в том числе те, с
    которых он уже ушёл.
- **`PUT /me/faculty`** — студент исправляет название факультета, выбранного в его профиле, —
  например, опечатку. В ответе обновлённый `FacultyDto`; профиль студента, курсы и группы
  факультета не меняются.
  - Разрешено, только если студент сам создал этот факультет и кроме него факультет никто из
    студентов не выбрал. Иначе `422`: у студента не выбран факультет; факультет создал не он (в
    том числе администратор); факультет уже выбрали другие студенты — тогда название меняет только
    администратор через `PUT /faculties/{id}`.
  - Другой факультет с таким названием в этом университете уже есть — `409`, как в
    `POST /me/faculty`.
- **`POST /me/group`** — студент создаёт группу, которой нет в `GET /groups?facultyId=&yearNumber=`,
  и сразу в неё зачисляется: в ответе `StudentDto` с новым `groupId`, в заголовке `Location` — адрес
  группы. Подтверждение администратора не требуется, группа сразу видна всем в списке групп курса.
  - Факультет и курс берутся из профиля студента. Если `facultyId` или `yearNumber` в профиле не
    заполнены — `422`.
  - Семестр подбирает сервер: актуальный семестр учебного года (факультет + курс) по тому же
    правилу, что и в `GET /groups` с фильтрами. Если учебного года или семестров у него ещё нет —
    они создаются. Новый семестр — текущее календарное полугодие: осенний с 1 сентября по 31 января,
    весенний с 1 февраля по 30 июня; в июле–августе создаётся ближайший осенний.
  - Если у семестра группы нет цикла расписания, он создаётся в статусе `DRAFT` — студент может
    сразу добавлять пары (`POST /pairs`). Уже существующий цикл не меняется.
  - Группа с таким названием в этом семестре уже есть (без учёта регистра и крайних пробелов) —
    `409` с `field: "name"` и `id` существующей группы: клиент может предложить выбрать её через
    `PATCH /me`. Профиль студента при этом не меняется.
  - Если у студента уже была группа, он переходит в созданную; прежняя группа не удаляется.
  - Один студент может создать не больше 3 групп; четвёртая попытка — `422`. Считаются существующие
    группы, созданные им (`GroupDto.createdByStudentId`), в том числе те, из которых он уже вышел.
- **`PUT /me/group`** — студент исправляет название (и расшифровку) группы, в которой состоит, —
  например, опечатку. В ответе обновлённый `GroupDto`; семестр и состав группы не меняются.
  `fullName`, не переданный в теле, очищается.
  - Разрешено, только если студент сам создал эту группу и кроме него в ней никого нет. Иначе
    `422`: у студента нет группы; группу создал не он (в том числе администратор); в группе уже
    есть другие студенты — тогда название меняет только администратор через `PUT /groups/{id}`.
  - Клиент может заранее понять, показывать ли правку: `GroupDto.createdByStudentId` совпадает с
    `StudentDto.id`. Есть ли в группе другие студенты, по DTO не видно — это покажет только `422`.
  - Другая группа с таким названием в этом семестре уже есть — `409`, как в `POST /me/group`.
- **`PUT /me/week-schedule-cycle`** — цикл расписания семестра, в котором учится группа студента;
  тела у запроса нет. Если цикла ещё нет, он создаётся в статусе `DRAFT` — администратор для этого
  не нужен. `id` из ответа — это `weekScheduleCycleId` для `POST /pairs`.
  - Существующий цикл возвращается как есть, в том числе `AGREED` (тогда пары правит только
    `ADMIN`, см. раздел `Pairs`). Статус запрос не меняет.
  - Повторные и одновременные запросы дают один и тот же цикл: у семестра он один.
  - У студента не выбрана группа — `422`.
  - Нужен для групп, чей семестр заведён без цикла: группы, созданные администратором, и группы,
    созданные студентами до появления этого эндпоинта. У новой группы, созданной через
    `POST /me/group`, цикл уже есть.
- **`POST /me/course`** — студент создаёт предмет, которого нет в `GET /courses?facultyId=`: в ответе
  `CourseDto`, в заголовке `Location` — адрес предмета. `id` из ответа — это `courseId` для
  `POST /pairs`. Подтверждение администратора не требуется.
  - Предмет привязывается к факультету из профиля студента и сразу виден всем в списке предметов
    этого факультета; студентам других факультетов того же университета — нет. Если `facultyId` в
    профиле не заполнен — `422`.
  - Профиль студента не меняется. Кафедры и преподавателя у созданного предмета нет.
  - На факультете уже есть предмет с таким названием (без учёта регистра и крайних пробелов) — `409`
    с `field: "title"` и `id` существующего предмета: клиент подставляет его в пару вместо создания
    дубля.
  - Один студент может создать не больше 50 предметов; следующая попытка — `422`. Считаются
    существующие предметы, созданные им (`CourseDto.createdByStudentId`).
  - Исправить название или удалить созданный предмет студент не может — только `ADMIN`/`TEACHER`
    через `PUT`/`DELETE /courses/{id}`.
- **`GET /{id}`** требует роль `STUDENT` у самого вызывающего (эндпоинт не проверяет, что `id` совпадает с
  `sub` вызывающего токена — то есть любой `STUDENT` технически может запросить чужой профиль по ID).

## Teachers — `/api/v1/teachers`

| Метод | Путь | Auth | Тело запроса | Тело ответа |
|---|---|---|---|---|
| GET | `/` | любая роль | — (`?page&size`) | `Page<TeacherDto>` |
| GET | `/{id}` | любая роль | — | `TeacherDto` |
| POST | `/register` | **Public** (без токена) | `RegisterTeacherRequest` | `201` + `TeacherDto` |
| POST | `/` | любая роль | `TeacherDto` | `201` + `TeacherDto` |
| PUT | `/{id}` | `ADMIN` | `TeacherDto` | `TeacherDto` |
| DELETE | `/{id}` | `ADMIN` | — | `204` |

- **`POST /register`** — аналог регистрации студента, но для преподавателя: создаёт пользователя в
  Keycloak с ролью `TEACHER`, `id` сущности = Keycloak user ID. Ответы: `201`/`400`/`409`, как у студента.
- **`POST /`** — создание `Teacher` без прохождения через Keycloak (нет `@PreAuthorize`, доступно любой
  аутентифицированной роли) — использовать для регистрации, скорее всего, не стоит: `id` в этом случае
  генерируется локально и не будет совпадать ни с каким Keycloak user ID.

## Enrollments — `/api/v1/enrollments`

Зачисление студентов на курсы.

| Метод | Путь | Auth | Тело запроса | Тело ответа |
|---|---|---|---|---|
| GET | `/{studentId}/{courseId}` | любая роль | — | `EnrollmentDto` |
| GET | `/student/{studentId}` | любая роль | — (`?page&size`) | `Page<EnrollmentDto>` |
| GET | `/course/{courseId}` | любая роль | — (`?page&size`) | `Page<EnrollmentDto>` |
| POST | `/` | `ADMIN`/`TEACHER` | `EnrollmentDto` | `201` + `EnrollmentDto` |
| POST | `/group/{groupId}/course/{courseId}` | `ADMIN`/`TEACHER` | — | `200` + `EnrollmentDto[]` |
| PUT | `/{studentId}/{courseId}` | `ADMIN`/`TEACHER` | `EnrollmentDto` | `EnrollmentDto` |
| DELETE | `/{studentId}/{courseId}` | `ADMIN`/`TEACHER` | — | `204` |

- **`POST /group/{groupId}/course/{courseId}`** — массово зачисляет на курс всех студентов группы, ещё не
  зачисленных на него; уже зачисленные молча пропускаются (не ошибка).
- **`PUT /{studentId}/{courseId}`** — смена статуса зачисления (`ACTIVE`/`COMPLETED`/`DROPPED`) через то же
  тело `EnrollmentDto`.

## Attendance — `/api/v1/attendance`

Посещаемость лекций и статистика.

| Метод | Путь | Auth | Тело запроса | Тело ответа |
|---|---|---|---|---|
| POST | `/` | `ADMIN`/`TEACHER` | `LectureAttendanceDto` | `200` + `LectureAttendanceDto` |
| GET | `/student/{studentId}` | любая роль | — (`?page&size`) | `Page<LectureAttendanceDto>` |
| GET | `/lecture/{lectureId}` | любая роль | — (`?page&size`) | `Page<LectureAttendanceDto>` |
| GET | `/lecture/{lectureId}/stats` | любая роль | — | `AttendanceStatsDto` |
| GET | `/student/{studentId}/course/{courseId}/stats` | любая роль | — | `AttendanceStatsDto` |

- **`POST /`** — студент должен быть активно (`ACTIVE`) зачислен на курс этой лекции, иначе ошибка
  (`422`/`ValidationException`, см. «Формат ошибок» выше).
- **`GET /lecture/{lectureId}/stats`** — статистика по лекции: сколько студентов отметилось из
  ожидаемых/зачисленных (`totalMarked`, `attendedCount`, `attendanceRate`).
- **`GET /student/{studentId}/course/{courseId}/stats`** — статистика по студенту в рамках курса: сколько
  лекций курса он посетил из уже отмеченных.

## AuditEvents — `/api/v1/audit-events`

Журнал бизнес-событий: кто, когда и что сделал (issue #111). Событие записывается в той же транзакции,
что и само действие: если действие не удалось, события нет.

| Метод | Путь | Auth | Тело запроса | Тело ответа |
|---|---|---|---|---|
| GET | `/` | `ADMIN` | — (`?entityType&entityId&actorId&from&to&page&size`) | `Page<AuditEventDto>` |

- Сначала новые события, `size` по умолчанию `20`. Все фильтры необязательны и объединяются через «и».
  `from` (включительно) и `to` (не включительно) — ISO-8601 с зоной, например `2026-10-01T00:00:00Z`.
- Что записывается и что лежит в `details`:

| Действие | `entityType` / `action` | `details` |
|---|---|---|
| Регистрация студента/преподавателя | `STUDENT`/`TEACHER`, `REGISTER` | — (автора нет) |
| Студент создал факультет/группу/предмет | `FACULTY`/`GROUP`/`COURSE`, `CREATE` | где создано и название |
| Создание пары | `PAIR`, `CREATE` | состояние пары |
| Изменение пары | `PAIR`, `UPDATE` | `before`, `after`, `lecturesDeleted`, `lecturesCreated` |
| Удаление пары | `PAIR`, `DELETE` | `before`, `lecturesDeleted` |
| Смена статуса цикла | `WEEK_SCHEDULE_CYCLE`, `STATUS_CHANGE` | `from`, `to` |
| Отметка посещаемости | `LECTURE_ATTENDANCE`, `MARK` (`entityId` — лекция) | `studentId`, `attended`, `previous` |
| Закрытие заявки на университет | `UNIVERSITY_REQUEST`, `COMPLETE` | `studentId`, `universityId`; `chosenByStudent: true`, если университет выбрал сам студент |
| Отклонение заявки | `UNIVERSITY_REQUEST`, `REJECT` | `studentId`, `comment` |
| Любое удаление через `DELETE` | тип сущности, `DELETE` | — |

> ФИО, email и телефон в `details` не пишутся. Ссылок на сущности у события нет, поэтому запись
> остаётся после удаления сущности и автора.

---

## Открытые вопросы при переносе в мобильное приложение

- `Faculties`/`Departments` create/update/delete не защищены ролью (см. врезку в разделе `Faculties`
  выше) — стоит уточнить у бэкенд-команды, до того как открывать соответствующие экраны не-`ADMIN`
  пользователям.
- `Teachers`: `POST /` (без `/register`) не привязан к Keycloak и не имеет ограничения по роли — не
  использовать этот путь для функции «регистрация преподавателя» в приложении, только `POST /register`.
