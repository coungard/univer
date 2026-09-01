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

## Cookie сессии логина: почему Keycloak поднят на двух портах (issue #54) `[✅ проверено]`

Authorization Code + PKCE флоу (в отличие от `password` grant в `SCENARIOS.md`) использует браузерную
страницу логина Keycloak, а она полагается на сессионную cookie `AUTH_SESSION_ID`. Эта cookie у Keycloak
всегда идёт с `SameSite=None; Secure` — это жёстко закодировано (не зависит от `Require SSL`/`sslRequired`
realm'а, подтверждено экспериментально на 22.0.1 и официальной позицией мейнтейнеров Keycloak: см.
[keycloak-dev](https://groups.google.com/g/keycloak-dev/c/Z5gYfW-9B5s),
[discussion #36631](https://github.com/keycloak/keycloak/discussions/36631),
[discussion #27592](https://github.com/keycloak/keycloak/discussions/27592)). Браузер обязан отбросить
`Secure`-cookie, полученную не по HTTPS — единственное исключение, которое допускают некоторые движки, это
буквально `http://localhost`, распознаваемый как «potentially trustworthy origin»; в WebView мобильного
эмулятора это исключение не сработало даже через `adb reverse` на `localhost:8082`.

Решение: локальный Keycloak (`docker-compose.yml`) теперь слушает **два** порта —

- `8082` (HTTP) — как раньше, для `password` grant (`SCENARIOS.md`) и любых back-channel вызовов
  (`KeycloakAdminService`, обмен `code` на токен). Cookie здесь не участвуют, HTTPS не нужен.
- `8443` (HTTPS, самоподписанный dev-сертификат — `init-keycloak/certs/`) — только для браузерной
  страницы логина (шаги 2–4 флоу ниже). Именно этот адрес приложение должно открывать в системном
  браузере/Custom Tabs/`ASWebAuthenticationSession`.

Шаг 5 (обмен `code` на токены) можно и нужно выполнять как обычно через `http://localhost:8082/...` —
это back-channel запрос, cookie в нём не участвует. Полный флоу прогнан против живого Keycloak+backend
(логин `student1` → `code` → `POST /token` на 8082 → `GET /api/v1/lectures/me` на бэкенде → `200 OK`).

**Важный нюанс:** итоговый access-токен, полученный через `https://localhost:8443/...`, несёт
`iss: https://localhost:8443/realms/univer-realm` — не совпадает с `iss` токенов, полученных через
`http://localhost:8082/...` (`password` grant). Бэкенд это сейчас пропускает только потому, что
`SecurityConfig.jwtDecoder()` собран через `NimbusJwtDecoder.withJwkSetUri(...)` **без** явного
issuer-валидатора — проверяются только подпись и `exp`/`nbf`, но не `iss`. Если в будущем кто-то добавит
строгую проверку issuer (например, перейдёт на `NimbusJwtDecoder.withIssuerLocation(...)` или
`JwtValidators.createDefaultWithIssuer(...)`), нужно будет одновременно разрешить оба адреса
(`http://localhost:8082/realms/univer-realm` и `https://localhost:8443/realms/univer-realm`) как
валидные issuer'ы для одного и того же realm — иначе токены мобильного флоу начнут получать 401.

В `docker-compose.yml` намеренно снята фиксация `KC_HOSTNAME_PORT: 8082` — она раньше жёстко приклеивала
этот порт ко всем URL, которые Keycloak генерирует (включая `action` формы логина), из-за чего при заходе
через `8443` форма логина всё равно указывала на `https://localhost:8082` — порт, где реально слушает
только HTTP, и сабмит формы падал бы соединением. Без фиксации Keycloak берёт схему/порт из Host-заголовка
конкретного запроса, поэтому оба порта работают корректно одновременно.

**Для мобильного репозитория:** сертификат `8443` самоподписанный (см. `init-keycloak/certs/README.md`) —
системный браузер/Custom Tabs/`ASWebAuthenticationSession` будет показывать предупреждение о недоверенном
сертификате, пока CA не добавлен в доверенные на устройстве/эмуляторе (для Android-эмулятора — импорт
`.crt` через Settings → Security → Encryption & credentials → Install a certificate, либо `adb push` +
перезапуск с `-writable-system`). Это отдельная разовая настройка окружения на стороне мобильного
репозитория, не блокирует работу самого бэкенда.

---

## Флоу авторизации (Authorization Code + PKCE)

1. Приложение генерирует `code_verifier` (случайная строка) и `code_challenge = BASE64URL(SHA256(code_verifier))`.
2. Приложение открывает системный браузер/веб-вью с поддержкой сессий ОС (Custom Tabs на Android,
   `ASWebAuthenticationSession` на iOS — не встроенный `WebView`, чтобы работал единый вход и не было
   риска перехвата пароля самим приложением) на
   `{auth-server-https-url}/realms/univer-realm/protocol/openid-connect/auth` с `client_id=univer-mobile`,
   `response_type=code`, `code_challenge`, `code_challenge_method=S256`, `redirect_uri=univer://auth/callback`.
   В dev — именно HTTPS-адрес (`https://localhost:8443` или его аналог, см. раздел про cookie ниже), не
   HTTP-порт `8082`: странице логина нужен настоящий TLS, иначе браузер отбросит сессионную cookie.
3. Пользователь логинится на странице Keycloak (в контексте браузера/ОС, не приложения).
4. Keycloak редиректит на `univer://auth/callback?code=...` — ОС передаёт это обратно в приложение
   (deep link / App Link).
5. Приложение обменивает `code` + исходный `code_verifier` на access/refresh токены —
   `POST {auth-server-url}/realms/univer-realm/protocol/openid-connect/token` (в dev — можно и нужно
   через обычный HTTP-порт `8082`, это back-channel запрос без cookie), `grant_type=authorization_code`,
   без `client_secret` (публичный клиент подтверждает себя `code_verifier`, не секретом).
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
