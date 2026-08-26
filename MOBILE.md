# MOBILE.md — Мобильный клиент: авторизация и работа с API

> В отличие от `SCENARIOS.md`, это не прогнанный против живого приложения сценарий, а целевой план —
> реализация ещё не начата, см. issue #49. Документ фиксирует, как мобильное приложение должно
> авторизовываться и работать с текущим REST API, чтобы решения не менялись на ходу по мере реализации.
> По мере того как шаги реально проверяются против живого Keycloak/API, соответствующие разделы будут
> помечаться `[✅ проверено]`, по аналогии с `[✅ решено]` в `SCHEDULES_RULES.md`.

---

## Общий принцип

Мобильное приложение — обычный клиент того же REST API (`http://localhost:8023/api/v1/...` в dev) и того
же Keycloak realm (`univer-realm`), что и любой другой клиент. Отдельный backend-for-frontend/mobile-gateway
не заводится: разница между вебом и мобилкой — только в том, какой Keycloak-клиент используется для входа
и какие `/me`-эндпоинты вызываются (см. `GET /api/v1/lectures/me` как образец паттерна). Сам бэкенд-репозиторий
структурно не меняется.

---

## Почему не `password` grant

Скрипты в `SCENARIOS.md` получают токен через `grant_type=password` на клиенте `univer-client` — это
удобно для curl, но не подходит мобильному приложению:

- `univer-client` — конфиденциальный клиент с `client_secret`; секрет, зашитый в мобильное приложение,
  извлекается тривиально (APK/IPA распаковывается) — это не секрет.
- `password` grant означает, что само приложение видит логин/пароль пользователя, а не только Keycloak —
  лишняя поверхность атаки и путь, который OAuth2 в принципе не рекомендует для third-party/публичных
  клиентов.

Правильная схема для мобилки — **Authorization Code + PKCE** на **публичном** клиенте: секрета нет по
определению, пароль пользователь вводит только на странице самого Keycloak (в системном браузере), а не
внутри приложения.

---

## Публичный Keycloak-клиент `univer-mobile`

Заводится отдельно от `univer-client`, с настройками:

| Параметр | Значение |
|---|---|
| Client ID | `univer-mobile` |
| Client authentication (secret) | выключено — публичный клиент |
| Standard flow (Authorization Code) | включён |
| Direct access grants (`password`) | выключен |
| PKCE Code Challenge Method | `S256`, обязателен |
| Valid redirect URIs | `univer://auth/callback` (кастомная схема приложения, не `localhost`) |
| Valid post logout redirect URIs | `univer://auth/logout` |

Client ID и redirect URI в мобильном приложении — конфигурация (build-конфиг/`.xcconfig`/`gradle.properties`
по среде dev/prod), не хардкод, по аналогии с тем, как `keycloak.auth-server-url`/`keycloak.realm` вынесены
в `application.yml` на бэкенде.

---

## Флоу авторизации (Authorization Code + PKCE)

1. Приложение генерирует `code_verifier` (случайная строка) и `code_challenge = BASE64URL(SHA256(code_verifier))`.
2. Приложение открывает системный браузер/веб-вью с поддержкой сессий ОС (Custom Tabs на Android,
   `ASWebAuthenticationSession` на iOS — не встроенный `WebView`, чтобы работал единый вход и не было
   риска перехвата пароля самим приложением) на
   `{auth-server-url}/realms/univer-realm/protocol/openid-connect/auth` с `client_id=univer-mobile`,
   `response_type=code`, `code_challenge`, `code_challenge_method=S256`, `redirect_uri=univer://auth/callback`.
3. Пользователь логинится на странице Keycloak (в контексте браузера/ОС, не приложения).
4. Keycloak редиректит на `univer://auth/callback?code=...` — ОС передаёт это обратно в приложение
   (deep link / App Link).
5. Приложение обменивает `code` + исходный `code_verifier` на access/refresh токены —
   `POST {auth-server-url}/realms/univer-realm/protocol/openid-connect/token`,
   `grant_type=authorization_code`, без `client_secret` (публичный клиент подтверждает себя `code_verifier`,
   не секретом).
6. Access-токен (JWT) используется как `Authorization: Bearer` к `/api/v1/...` — ровно так же, как в
   `SCENARIOS.md`; `sub` в токене — тот же UUID, что и `Student.id`/`Teacher.id` (см. флоу регистрации,
   `CLAUDE.md`).

---

## Хранение и обновление токенов

- Access и refresh токены — только в защищённом хранилище ОС: Keychain (iOS), Keystore-backed
  `EncryptedSharedPreferences`/`DataStore` (Android). Не в обычных `UserDefaults`/`SharedPreferences`
  открытым текстом.
- Access-токен короткоживущий; по истечении — тихое обновление через
  `grant_type=refresh_token` без повторного похода пользователя в браузер.
- Протухший/отозванный refresh-токен → разлогин, обратно на экран входа (шаг 2).

---

## Вне рамок этого документа

Следующие шаги после того, как авторизация заработает — отдельные последующие задачи, не блокируют
issue #49:

- Push-уведомления (device-токены, FCM/APNs, триггер из `LectureService`/`AttendanceService` при
  изменениях расписания).
- Офлайн-кэш расписания и синхронизация по `updatedAt` (`Auditable` уже даёт для этого базу).
- Сам мобильный клиент (репозиторий, стек, экраны) — вне этого бэкенд-репозитория.

---

## Статус

План, ничего из описанного выше ещё не реализовано и не проверено против живого Keycloak — см. issue #49.
