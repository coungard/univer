# AGENTS.md

Инструкции для AI-агентов при работе с кодом в этом репозитории. Файл читают DeepSeek Harness (автоматически,
как проектную инструкцию) и Claude Code (через `@AGENTS.md` в `CLAUDE.md`), а также любые другие агенты,
поддерживающие стандарт `AGENTS.md`. Вложенные `AGENTS.md`/`AGENTS.local.md` в подкаталогах уточняют эти
правила и имеют приоритет над этим файлом; сами эти правила не отменяют системные и пользовательские указания.

## Как работать в этом репозитории

- Общение с пользователем, комментарии и документация — **на русском**.
- Окружение разработки: Windows + PowerShell 7, Maven 3.9 (`mvn` в `PATH`), Docker Desktop, `docker compose` v2
  (старый синтаксис `docker-compose` тоже встречается в документации и работает). Команды ниже даны
  кроссплатформенно и выполняются в PowerShell как есть.
- Порядок работы с файлами: сначала прочитать файл, потом делать точечную правку; не переписывать файл
  целиком ради мелкого изменения и не переформатировать его «заодно».
- **Минимальная проверка перед сдачей работы** — `mvn -q test-compile` (без Docker, быстро). Если правка
  затрагивает поведение — прогоните точечные тесты затронутых классов, а перед передачей задачи
  пользователю — полный `mvn test` (нужен запущенный Docker).
- Не коммитить, не пушить, не создавать PR и не трогать удалённые ветки без явной просьбы. Не «чинить»
  CI ослаблением проверок в `.github/workflows` — правьте причину падения.
- Не редактировать уже применённые/закоммиченные миграции Flyway и не переименовывать существующие
  эндпоинты, поля DTO и роли без явной просьбы: это контракты для клиентов (`API.md`, `MOBILE.md`).
- Секреты (client secret Keycloak, пароли БД, ключи) не хардкодить: только env-переменные с dev-дефолтами в
  `application.yml`/`docker-compose.yml`, как это уже сделано для `keycloak.*` и `media.*`.
- Подробности по темам лежат в отдельных документах: `API.md` (эндпоинты), `SCENARIOS.md` (сценарии),
  `DEPLOY.md` (стенд, Caddy), `MOBILE.md` (браузерный флоу логина), `ROADMAP.md`/`TARGET.md` (планы и
  issue-трекинг). Ссылки на issue в комментариях (`issue #136`) — принятая в проекте практика, сохраняйте её.

## Обзор проекта

University — REST API на Spring Boot 3 (3.2.x) / Java 17 для управления данными университета: университетами,
факультетами, кафедрами, студентами, преподавателями, курсами, лекциями, зачислениями и посещаемостью.
Аутентификация делегирована Keycloak (OAuth2/JWT resource server). Хранение данных — PostgreSQL через
JPA/Hibernate, миграции — Flyway.

## Code style

При написании и форматировании кода руководствуйтесь схемой стиля из файла `code-style.xml` в корне проекта
(формат IntelliJ IDEA code style scheme), а не произвольным собственным стилем. Ключевые настройки оттуда:

- отступ — **2 пробела** (табы не используются), продолжение строки — 4 пробела;
- импорты без wildcard (`import ...*` не используется, порог on-demand = 999), статические импорты идут
  первым блоком, затем пустая строка и обычные импорты;
- не больше одной пустой строки подряд; перед закрывающей `}` пустых строк нет;
- стиль фигурных скобок — K&R, `@Override` на отдельной строке перед методом, как в существующем коде.

## Сборка, запуск, тесты

Для локальной разработки нужны Postgres и Keycloak — поднимаются через `docker-compose.yml` (Postgres на
`5435`, Keycloak на `8082` по HTTP и на `8443` по HTTPS с dev-сертификатом — второй нужен только браузерному
флоу логина, Authorization Code + PKCE, см. `MOBILE.md`, issue #54). Поднимайте их перед запуском приложения
или любых тестов, не изолированных через Testcontainers:

```bash
docker compose up --build
```

Само приложение работает на порту `8023` (`server.port` в `application.yml`). Его можно поднять и в Docker
вместе с остальными сервисами — `docker compose --profile app up --build` (multi-stage `Dockerfile`, сервис
`app` в профиле `app`, чтобы обычный `docker compose up` не занимал 8023). В контейнере адреса Postgres и
Keycloak переопределены env-переменными на имена сервисов; `keycloak.auth-server-url` — back-channel адрес
(Keycloak admin client), `keycloak.public-url` — адрес для браузера (tokenUrl Swagger UI), по умолчанию
совпадают. Новые настройки, зависящие от сетевого окружения, держите переопределяемыми так же.

```bash
mvn compile                 # компиляция
mvn spring-boot:run         # запуск приложения (нужны поднятые сервисы из docker compose)
mvn test                    # запуск всех тестов
mvn test -Dtest=StudentServiceTest                       # один тестовый класс
mvn test -Dtest=StudentServiceTest#shouldUpdateStudent   # один тестовый метод
```

Тесты используют `@Testcontainers` с реальным `PostgreSQLContainer` (`postgres:15`), поэтому **Docker должен
быть запущен** даже для `mvn test` — тесты не работают на in-memory БД. CI (`.github/workflows/maven.yml`)
прогоняет `mvn -B compile`, затем `mvn -B test` при push/PR в `master`.

## Архитектура

Классическая слоистая структура в `src/main/java/com/coungard/univer/`:

- `controller` — `@RestController`'ы, по одному на каждый корневой агрегат (например, `StudentController`).
  Авторизация на уровне методов через `@PreAuthorize("hasRole('ADMIN')")` и т.п. (`@EnableMethodSecurity` в
  `SecurityConfig`). Эндпоинты регистрации (`/register`) и несколько списковых эндпоинтов намеренно публичные
  — см. `permitAll()`-матчеры в `SecurityConfig`.
- `service` / `service.impl` — интерфейс + `*ServiceImpl`, `@Transactional` на уровне метода
  (`readOnly = true` для чтения). Бизнес-логика, оркестрация между сущностями и вызовы Keycloak живут здесь,
  а не в контроллерах. Значимые изменения данных (удаления, смены статусов, изменения расписания, действия
  студента над общими справочниками) записываются в журнал `audit_events` явным вызовом
  `AuditService.record(...)` в той же транзакции — добавляйте его в новые методы такого рода (issue #111).
- `repository` — обычные интерфейсы `JpaRepository<Entity, UUID>`, кастомных реализаций пока нет.
- `entity` — JPA-сущности. ID — `UUID`, самостоятельно генерируется в `@PrePersist` (см.
  `Student.generateId()`), кроме случаев, когда в качестве ID сущности переиспользуется ID пользователя,
  выданный Keycloak (см. флоу регистрации ниже). Кто и когда создал и изменил запись
  (`createdAt`/`updatedAt`/`createdBy`/`updatedBy`) — общий `@MappedSuperclass` `AuditableEntity`, поля
  заполняет Spring Data JPA auditing, автор — subject JWT (`CurrentActor`, `JpaAuditingConfig`). Любую новую
  изменяемую сущность наследуйте от `AuditableEntity` и добавляйте её таблице эти четыре столбца в
  миграции. Нативные `INSERT`/`UPDATE` и bulk-`@Modifying` минуют auditing — заполняйте поля в них сами
  (issue #112). Эти поля отдаются в DTO сущности (только ответ, во входящих запросах игнорируются,
  issue #119): добавляйте их в DTO новой сущности и в её маппер. `updatedAt`/`updatedBy` проставляются в
  `@PreUpdate`, то есть только на flush, поэтому метод, который изменяет существующую сущность и
  возвращает её DTO, сохраняет её через `saveAndFlush`, а не `save` — иначе в ответе останется прежний
  редактор.
- `dto` — Java `record`'ы (в стиле builder, например `StudentDto.builder()...build()`), включая подпакет
  `dto.registration` для форм запросов/ответов регистрации и `dto.request` для прочих request-only DTO.
- `mapper` — написанные вручную `@Component`-мапперы (entity ↔ DTO), *не* сгенерированные через MapStruct,
  несмотря на то что MapStruct есть в зависимостях — следуйте существующему ручному стилю (см.
  `StudentMapper`), если не попросили перейти на MapStruct.
- `validation` — компоненты `*Validator`, вызываемые явно из сервисных методов (например,
  `studentValidator.validateRegisterData(...)`), а не только через JSR-380.
- `exception` — доменные исключения (`ResourceNotFoundException`, `ValidationException`) и единый
  `GlobalExceptionHandler` (`@RestControllerAdvice`), который преобразует их (и
  `MethodArgumentNotValidException`) в единообразное JSON-тело ошибки.
- `security` — интеграция с Keycloak: `KeycloakAdminService` (админские REST-вызовы для создания/удаления
  пользователей, назначения ролей), `KeycloakRoleConverter` (маппит realm/resource-роли Keycloak в Spring
  `GrantedAuthority`, используемые в `hasRole(...)`), enum `Role`.
- `config` — `SecurityConfig` (JWT resource server + авторизация маршрутов), `KeycloakConfig`, `OpenApiConfig`
  (Swagger UI с OAuth2/PKCE, привязанный к realm Keycloak).
- `filter` — сервлет-фильтры: `RequestIdFilter` (заголовок `X-Request-Id`, кладёт id в MDC, issue #110) и
  `RequestLoggingFilter` (лог метода, пути, статуса, длительности и subject'а; тело и `Authorization`
  намеренно не логируются, issue #69). 401/403 логируют `LoggingAuthenticationEntryPoint` и
  `LoggingAccessDeniedHandler`.
- `listener` — `StartupListener` для хуков на старте приложения.
- Медиа-файлы (аватары и т.п.) — через `StorageService` (`FileSystemStorageService`: каталог `media.storage-dir`
  на локальном диске, issue #136), не напрямую через `java.nio.file`. В БД храните **ключ** объекта
  (`avatars/{fileId}/512.jpg`), а не URL или путь — ссылку для клиента строит `StorageService.publicUrl(...)`
  из `media.public-base-url`; так переезд в облачное S3 (критерии — issue #135) не потребует миграции данных.
  Локально `/media/**` отдаёт само приложение (`media.serve-from-app`), на стенде — Caddy из volume.

### Флоу регистрации (студент/преподаватель)

Регистрация — единственный по-настоящему нетривиальный кросс-системный сценарий записи данных, следуйте
этому паттерну для аналогичных задач:

1. Валидация запроса через соответствующий `*Validator`.
2. Создание пользователя в Keycloak через `KeycloakAdminService.createUser(...)`, с сохранением
   возвращённого Keycloak user ID.
3. Назначение соответствующей `Role` в Keycloak.
4. Сохранение локальной сущности с использованием **Keycloak user ID в качестве `id` самой сущности**, чтобы
   локальные записи и идентичности в Keycloak имели один и тот же UUID.
5. При любой ошибке после создания пользователя в Keycloak — по возможности удалить этого пользователя в
   `catch`-блоке перед повторным пробросом исключения, чтобы не оставлять "осиротевшие" аккаунты в Keycloak.

### Миграции БД

Миграции Flyway разнесены по двум подпапкам `src/main/resources/db/migration/` (обе перечислены в
`spring.flyway.locations` в `application.yml` и применяются автоматически при старте,
`baseline-on-migrate: true`): `schema/` — DDL (`CREATE`/`ALTER`), `data/` — DML (`INSERT`, seed-данные,
например `V2__insert_universities_data.sql`). Каждый файл должен содержать только один вид команд, не
оба сразу. Нумерация `V<n>__описание.sql` — **общая на обе подпапки**: следующий свободный номер для
любой новой миграции, DDL или DML, — это следующий номер после самого большого уже занятого в любой из
двух подпапок (Flyway применяет миграции из всех `locations` одним общим упорядоченным списком по
номеру версии, а не независимо по каждой папке). Новые миграции добавляйте со следующим по порядку
номером — никогда не редактируйте уже применённую/закоммиченную миграцию. На момент написания файла
последний занятый номер — `V49`.

### Модель авторизации

Значимы две realm-роли: `ADMIN` и `STUDENT` (`security.Role`). JWT валидируются относительно realm Keycloak,
заданного в `application.yml` (`spring.security.oauth2.resourceserver.jwt.issuer-uri`,
`keycloak.realm`/`auth-server-url`). Именно `KeycloakRoleConverter` заставляет работать проверки вида
`hasRole('ADMIN')` с форматом claim'ов токена Keycloak — не полагайтесь на стандартный маппинг ролей Spring
Security для JWT.

## Совместимость с Claude Code

`CLAUDE.md` в корне — тонкая обёртка, которая импортирует этот файл (`@AGENTS.md`), чтобы правила не
дублировались в двух местах и не расходились. Правьте проектную документацию здесь; `CLAUDE.md` трогайте
только если меняется сам способ подключения (например, Claude Code перестанет поддерживать импорты).
