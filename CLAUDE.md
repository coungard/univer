# CLAUDE.md

Этот файл содержит инструкции для Claude Code (claude.ai/code) при работе с кодом в этом репозитории.

## Обзор проекта

University — REST API на Spring Boot 3 / Java 17 для управления данными университета: университетами,
факультетами, кафедрами, образовательными программами, студентами, преподавателями, курсами, лекциями,
зачислениями и посещаемостью. Аутентификация делегирована Keycloak (OAuth2/JWT resource server). Хранение
данных — PostgreSQL через JPA/Hibernate, миграции — Flyway.

## Сборка, запуск, тесты

Для локальной разработки нужны Postgres и Keycloak — поднимаются через `docker-compose.yml` (Postgres на
`5435`, Keycloak на `8082`). Поднимайте их перед запуском приложения или любых тестов, не изолированных
через Testcontainers:

```bash
docker-compose up --build
```

Само приложение работает на порту `8023` (`server.port` в `application.yml`).

```bash
mvn compile                 # компиляция
mvn spring-boot:run         # запуск приложения (нужны поднятые сервисы из docker-compose)
mvn test                    # запуск всех тестов
mvn test -Dtest=StudentServiceTest              # запуск одного тестового класса
mvn test -Dtest=StudentServiceTest#shouldUpdateStudent   # запуск одного тестового метода
```

Тесты используют `@Testcontainers` с реальным `PostgreSQLContainer`, поэтому **Docker должен быть запущен**
даже для `mvn test` — тесты не работают на in-memory БД. CI (`.github/workflows/maven.yml`) прогоняет
`mvn -B compile`, затем `mvn -B test` при push/PR в `master`.

## Архитектура

Классическая слоистая структура в `src/main/java/com/coungard/univer/`:

- `controller` — `@RestController`'ы, по одному на каждый корневой агрегат (например, `StudentController`).
  Авторизация на уровне методов через `@PreAuthorize("hasRole('ADMIN')")` и т.п. (`@EnableMethodSecurity` в
  `SecurityConfig`). Эндпоинты регистрации (`/register`) и несколько списковых эндпоинтов намеренно публичные
  — см. `permitAll()`-матчеры в `SecurityConfig`.
- `service` / `service.impl` — интерфейс + `*ServiceImpl`, `@Transactional` на уровне метода
  (`readOnly = true` для чтения). Бизнес-логика, оркестрация между сущностями и вызовы Keycloak живут здесь,
  а не в контроллерах.
- `repository` — обычные интерфейсы `JpaRepository<Entity, UUID>`, кастомных реализаций пока нет.
- `entity` — JPA-сущности. ID — `UUID`, самостоятельно генерируется в `@PrePersist` (см.
  `Student.generateId()`), кроме случаев, когда в качестве ID сущности переиспользуется ID пользователя,
  выданный Keycloak (см. флоу регистрации ниже). Аудит (`createdAt`/`updatedAt`) реализован через
  `Auditable` + `@EntityListeners(AuditingEntityListener.class)` + `@CreatedDate`/`@LastModifiedDate` —
  реализуйте `Auditable` для любой новой сущности, которой нужны временные метки.
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
- `listener` — `StartupListener` для хуков на старте приложения.

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

Миграции Flyway лежат в `src/main/resources/db/migration`, называются `V<n>__описание.sql`
(`baseline-on-migrate: true`, применяются автоматически при старте). Новые миграции добавляйте со следующим
по порядку номером — никогда не редактируйте уже применённую/закоммиченную миграцию.

### Модель авторизации

Значимы две realm-роли: `ADMIN` и `STUDENT` (`security.Role`). JWT валидируются относительно realm Keycloak,
заданного в `application.yml` (`spring.security.oauth2.resourceserver.jwt.issuer-uri`,
`keycloak.realm`/`auth-server-url`). Именно `KeycloakRoleConverter` заставляет работать проверки вида
`hasRole('ADMIN')` с форматом claim'ов токена Keycloak — не полагайтесь на стандартный маппинг ролей Spring
Security для JWT.
