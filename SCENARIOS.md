# Сценарии — Univer Backend

> Пошаговые прикладные сценарии работы с REST API: реальные ID из dev-БД (`docker-compose.yml`,
> Postgres на `5435`), запросы через `curl` к приложению на `http://localhost:8023`. Сценарий 1 реально
> прогнан 20.08.2026 против живого приложения — все ID в примерах ответов ниже (`StudyYear`, `Semester`,
> `Group`) настоящие, из dev-БД, а не выдуманные плейсхолдеры; каждая строка дополнительно подтверждена
> прямым SQL-запросом (`docker exec postgres-univer psql`), не только ответом API.

---

## Сценарий 1: Создание группы 1 курса РПиС (очное отделение)

**Цель:** создать студенческую группу первого курса по программе «Разработка программно-информационных
систем» (РПиС, код `09.03.04`, очная форма обучения), привязанную к Факультету компьютерных технологий
и энергетики.

Группа (`Group`) не привязывается к факультету и программе напрямую — связь идёт по цепочке
`Group → Semester → StudyYear → Program → Faculty` (см. `TARGET.md`, раздел «Циклическое расписание»).
Поэтому прежде чем создать саму группу, нужно создать курс обучения (`StudyYear`, номер 1) и семестр
(`Semester`) для нужной программы — если их ещё нет.

### Исходные данные (уже есть в БД)

| Сущность | Значение | ID |
|---|---|---|
| Университет | ДГТУ | `796c5ad4-52ba-482f-a3bb-31c5de38762d` |
| Факультет | Факультет компьютерных технологий и энергетики | `9980825f-f630-426a-ac6a-5a185b30f944` |
| Программа | 09.03.04 «Программная инженерия» (направленность «Разработка программно-информационных систем», РПиС), **очная**, 4 года | `0812c171-3317-4ee2-bc3c-cba404fd53d6` |

У программы `09.03.04` в БД есть и заочный вариант (5 лет, другой ID) — для сценария важно использовать
именно очный (`education_form: FULL_TIME`), как указано в задаче.

### Шаг 0. Получить access-токен ADMIN

Через Swagger UI (`http://localhost:8023/swagger-ui/index.html` → Authorize → username/password, см.
настройку в этой же сессии) либо напрямую у Keycloak (password grant, клиент публичный секрета не
требует):

```bash
curl -s -X POST "http://localhost:8082/realms/univer-realm/protocol/openid-connect/token" \
  -d "client_id=univer-client" \
  -d "client_secret=univer-client-secret" \
  -d "grant_type=password" \
  -d "username=super" \
  -d "password=1234" \
  -d "scope=openid" \
  | jq -r .access_token
```

Дальше `<ACCESS_TOKEN>` — значение из ответа. Все POST-эндпоинты ниже требуют роль `ADMIN`.

### Шаг 1. Создать курс обучения (`StudyYear`) — 1 курс программы РПиС

```bash
curl -s -X POST "http://localhost:8023/api/v1/study-years" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
        "programId": "0812c171-3317-4ee2-bc3c-cba404fd53d6",
        "yearNumber": 1
      }'
```

Фактический ответ (`201 Created`):

```json
{
  "id": "86395c37-ed33-404d-be41-7680c3bf605e",
  "programId": "0812c171-3317-4ee2-bc3c-cba404fd53d6",
  "yearNumber": 1
}
```

### Шаг 2. Создать семестр (`Semester`) — осенний семестр 1 курса

```bash
curl -s -X POST "http://localhost:8023/api/v1/semesters" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
        "studyYearId": "86395c37-ed33-404d-be41-7680c3bf605e",
        "type": "AUTUMN",
        "startDate": "2026-09-01"
      }'
```

Фактический ответ (`201 Created`):

```json
{
  "id": "96ba5814-c675-4bc4-9831-3960edffeae4",
  "studyYearId": "86395c37-ed33-404d-be41-7680c3bf605e",
  "type": "AUTUMN",
  "startDate": "2026-09-01"
}
```

`startDate` важен не только как дата начала — от него считается чётность недели циклического
расписания (`WeekScheduleCycle`/`Pair`, Этап 3), если для этой группы позже понадобится расписание.

### Шаг 3. Создать группу (`Group`)

```bash
curl -s -X POST "http://localhost:8023/api/v1/groups" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
        "semesterId": "96ba5814-c675-4bc4-9831-3960edffeae4",
        "name": "У533 РПИС"
      }'
```

Фактический ответ (`201 Created`):

```json
{
  "id": "30ddeb19-db99-435a-b3f3-207eb32a209e",
  "semesterId": "96ba5814-c675-4bc4-9831-3960edffeae4",
  "name": "У533 РПИС"
}
```

`name` — свободная строка (см. `GroupDto`), связи с факультетом/программой/курсом — источник истины, не
текст в названии; `У533 РПИС` здесь читается как «РПиС, 1 курс, группа 1» по аналогии с существующими
тестовыми данными (`У532 КСиТ`), а не как зафиксированный в проекте формат.

> **Примечание для Windows (Git Bash/MINGW).** curl-команда выше с инлайновым `-d '{ ... кириллица ... }'`
> при прогоне 20.08.2026 упала `400 Bad Request` — тело с кириллицей внутри одинарных кавычек уходит не в
> UTF-8 (кодировка консоли/шелла, а не баг Spring/Jackson на стороне сервера). Обходной путь — сохранить
> JSON в файл в явном UTF-8 и отправить через `--data-binary @file`:
> ```bash
> printf '{"semesterId": "96ba5814-c675-4bc4-9831-3960edffeae4", "name": "\xd0\xa3533 \xd0\xa0\xd0\x9f\xd0\x98\xd0\xa1"}' > group.json
> curl -s -X POST "http://localhost:8023/api/v1/groups" \
>   -H "Authorization: Bearer <ACCESS_TOKEN>" \
>   -H "Content-Type: application/json; charset=utf-8" \
>   --data-binary @group.json
> ```
> После этого запрос отработал (`201 Created`, `name` вернулось корректно). То же стоит иметь в виду для
> любого другого запроса в этом документе с кириллицей в теле — не только для Шага 3.

### Шаг 4. Проверить результат

```bash
curl -s "http://localhost:8023/api/v1/groups/30ddeb19-db99-435a-b3f3-207eb32a209e" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

или список групп семестра:

```bash
curl -s "http://localhost:8023/api/v1/groups/semester/96ba5814-c675-4bc4-9831-3960edffeae4?page=0&size=10" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Итог

Создана цепочка `Program (09.03.04, очно) → StudyYear (курс 1) → Semester (осень 2026) → Group (У533 РПИС)`,
привязанная через программу к Факультету компьютерных технологий и энергетики. Группа готова к
дальнейшим сценариям Этапа 3–5: привязка `Student` к группе (`group_id`), циклическое расписание
(`WeekScheduleCycle`/`Pair`), массовое зачисление группы на курс (`POST
/api/v1/enrollments/group/{groupId}/course/{courseId}`, Этап 5).

---

## Сценарий 2: Регистрация студента и привязка к группе из Сценария 1

**Цель:** зарегистрировать нового студента и привязать его к группе `У533 РПИС`, созданной в Сценарии 1.

Прогнан 20.08.2026 против живого приложения; все ID и тела ответов ниже — фактические, подтверждены
прямым SQL-запросом к Postgres.

### Исходные данные (уже есть в БД / из Сценария 1)

| Сущность | Значение | ID |
|---|---|---|
| Университет | ДГТУ | `796c5ad4-52ba-482f-a3bb-31c5de38762d` |
| Группа | У533 РПИС (Сценарий 1) | `30ddeb19-db99-435a-b3f3-207eb32a209e` |

### Шаг 0. Получить access-токен ADMIN

См. Сценарий 1, Шаг 0. Токен нужен только для Шага 2 (`PUT /students/{id}` требует роль `ADMIN`) — сама
регистрация (Шаг 1) публична и токена не требует.

### Шаг 1. Зарегистрировать студента

`POST /api/v1/students/register` — публичный эндпоинт (`permitAll()` в `SecurityConfig`), доступен без
токена. Регистрация создаёт пользователя в Keycloak (роль `ROLE_STUDENT`) и локальную сущность `Student`
с тем же ID, что и Keycloak user ID (см. `CLAUDE.md`, «Флоу регистрации»).

```bash
curl -s -X POST "http://localhost:8023/api/v1/students/register" \
  -H "Content-Type: application/json; charset=utf-8" \
  -d '{
        "username": "ivanov.i",
        "firstname": "Иван",
        "lastname": "Иванов",
        "fullname": "Иванов Иван Иванович",
        "email": "ivanov.i@dstu-student.ru",
        "password": "P@ssw0rd123",
        "enrollmentDate": "2026-08-15",
        "universityId": "796c5ad4-52ba-482f-a3bb-31c5de38762d"
      }'
```

> На Windows/Git Bash см. примечание про кодировку кириллицы у Шага 3 Сценария 1 — тот же приём
> (`--data-binary @file` с явным UTF-8) применим и здесь.

Фактический ответ (`201 Created`):

```json
{
  "id": "a1164570-56b8-4138-9f9a-3c7b8a43a663",
  "username": "ivanov.i",
  "firstname": "Иван",
  "lastname": "Иванов",
  "fullname": "Иванов Иван Иванович",
  "createdAt": "2026-08-20T08:53:28.861184800Z",
  "updatedAt": "2026-08-20T08:53:28.861184800Z",
  "email": "ivanov.i@dstu-student.ru",
  "enrollmentDate": "2026-08-15",
  "universityId": "796c5ad4-52ba-482f-a3bb-31c5de38762d",
  "groupId": null
}
```

> **Исправленный баг (20.08.2026).** `StudentMapper.toDto` не мапил `enrollmentDate` в `StudentDto` —
> любой ответ со студентом (`register`/`GET`/`PUT`) отдавал `enrollmentDate: null`, хотя в БД значение
> сохранялось верно. Это ломало естественный workflow «получить студента → переслать тело с добавленным
> `groupId`»: наивный `PUT` в Шаге 2 падал `400 {"enrollmentDate":"Дата зачисления обязательна"}`,
> потому что `StudentDto.enrollmentDate` помечено `@NotNull`. Исправлено — `StudentMapper.toDto` теперь
> явно мапит `.enrollmentDate(student.getEnrollmentDate())`. Ответ выше — уже с исправлением; `groupId`
> пока `null`, так как связь с группой выставляется отдельным шагом ниже.

### Шаг 2. Привязать студента к группе

Прямого эндпоинта «привязать студента к группе» нет — связь выставляется через `PUT /api/v1/students/{id}`
(требует `hasRole('ADMIN')`) с полным телом `StudentDto`, где `groupId` — единственное новое поле;
остальные поля берутся из ответа Шага 1. `PUT` — не PATCH: все `@NotNull`/`@NotBlank` поля `StudentDto`
обязательны, включая `username` — хотя `StudentServiceImpl.updateStudent` его валидирует по DTO, но
фактически не применяет к сущности (обновляются только `firstname`/`lastname`/`fullname`/`email`/
`enrollmentDate`/`university`/`group`; логин менять через этот эндпоинт нельзя).

```bash
curl -s -X PUT "http://localhost:8023/api/v1/students/a1164570-56b8-4138-9f9a-3c7b8a43a663" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json; charset=utf-8" \
  -d '{
        "username": "ivanov.i",
        "firstname": "Иван",
        "lastname": "Иванов",
        "fullname": "Иванов Иван Иванович",
        "email": "ivanov.i@dstu-student.ru",
        "enrollmentDate": "2026-08-15",
        "universityId": "796c5ad4-52ba-482f-a3bb-31c5de38762d",
        "groupId": "30ddeb19-db99-435a-b3f3-207eb32a209e"
      }'
```

Фактический ответ (`200 OK`):

```json
{
  "id": "a1164570-56b8-4138-9f9a-3c7b8a43a663",
  "username": "ivanov.i",
  "firstname": "Иван",
  "lastname": "Иванов",
  "fullname": "Иванов Иван Иванович",
  "createdAt": "2026-08-20T08:53:28.861185Z",
  "updatedAt": "2026-08-20T08:53:40.605967Z",
  "email": "ivanov.i@dstu-student.ru",
  "enrollmentDate": "2026-08-15",
  "universityId": "796c5ad4-52ba-482f-a3bb-31c5de38762d",
  "groupId": "30ddeb19-db99-435a-b3f3-207eb32a209e"
}
```

### Шаг 3. Проверить результат

`GET /api/v1/students/{id}` требует роль `STUDENT` (не `ADMIN`) — токеном ADMIN его не вызвать (403).
Для проверки под ADMIN удобнее списковый эндпоинт:

```bash
curl -s "http://localhost:8023/api/v1/students?page=0&size=10" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

В ответе у нужного студента `"groupId": "30ddeb19-db99-435a-b3f3-207eb32a209e"`.

### Итог

Зарегистрирован студент Иван Иванов (`ivanov.i`, `id = a1164570-56b8-4138-9f9a-3c7b8a43a663`) через
`POST /students/register` (Keycloak-пользователь + локальная сущность с тем же ID) и привязан к группе
`У533 РПИС` из Сценария 1 через `PUT /students/{id}` с `groupId`. Заодно найден и исправлен баг маппинга
`enrollmentDate` в `StudentMapper`, ломавший естественный round-trip «прочитать → дозаполнить → отправить»
для любого клиента Student API.
