# Dev-сертификат для Keycloak (issue #54, #127)

`keycloak-dev.crt` / `keycloak-dev.key` — самоподписанный сертификат **только для локальной разработки**.
Выпускается скриптом `generate.sh` рядом, который выполняет:

```bash
openssl req -x509 -nodes -newkey rsa:2048 \
  -keyout keycloak-dev.key -out keycloak-dev.crt -days 825 \
  -subj "/CN=localhost" \
  -addext "subjectAltName=DNS:localhost,DNS:keycloak,IP:127.0.0.1,IP:10.0.2.2" \
  -addext "extendedKeyUsage=serverAuth"
```

SAN включает `10.0.2.2` — алиас хост-машины из гостевой сети Android-эмулятора.

**Срок — 825 дней, не больше.** С iOS 13 Apple не принимает серверные TLS-сертификаты со сроком действия
больше 825 дней и без EKU `serverAuth`: `URLSession` рвёт соединение, даже если корневой сертификат добавлен
в доверенные (`xcrun simctl keychain booted add-root-cert`) — см.
[Apple: Requirements for trusted certificates in iOS 13 and macOS 10.15](https://support.apple.com/en-us/103769).
Прежний сертификат был выпущен на 10 лет, и на iOS-симуляторе не работал автовход после регистрации
(`grant_type=password` через `fetch`). Текущий действует до 2029-02-06.

## Перевыпуск

```bash
./init-keycloak/certs/generate.sh                            # базовые SAN
./init-keycloak/certs/generate.sh 192.168.1.42 my-mac.local  # + адреса Mac для реального iPhone
```

Дополнительные аргументы попадают в SAN: IP-адреса как `IP:`, остальное как `DNS:`. На реальном устройстве
`localhost` указывает на сам телефон, поэтому там нужен LAN IP Mac или его `<имя>.local`. Этот же адрес
передайте Keycloak через `KC_HOSTNAME`, иначе форма логина будет отправляться на `localhost`:

```bash
KC_HOSTNAME=192.168.1.42 docker compose up
```

После перевыпуска перезапустите Keycloak (`docker compose restart keycloak`) и заново добавьте сертификат
везде, где он был в доверенных: в iOS-симулятор (`xcrun simctl keychain booted add-root-cert
keycloak-dev.crt`, см. coungard/univer_mobile#73), в Android-эмулятор, на устройство.
Сертификат с адресами конкретного Mac коммитить не нужно — это локальная настройка.

## Зачем и почему его можно коммитить

Он используется исключительно для того, чтобы браузерный флоу логина (Authorization Code + PKCE,
см. `MOBILE.md`) отдавал сессионную cookie `AUTH_SESSION_ID` по настоящему HTTPS — без этого браузер
её отбрасывает (флаг `Secure` у неё жёстко закодирован в Keycloak, см. разбор в `MOBILE.md`). Никакой
секретной ценности в приватном ключе нет: сертификат самоподписанный, ему никто не доверяет за пределами
локальной docker-сети, и коммитить его в репозиторий так же безопасно, как уже закоммиченные dev-пароли
(`admin`/`admin`, `postgres`/`postgres`) в `docker-compose.yml`.

**Не использовать в проде.** Боевой деплой (issue #2) должен стоять за настоящим TLS-сертификатом
(например, через reverse-proxy/Let's Encrypt), а не за этим самоподписанным.

Backend продолжает ходить в Keycloak по HTTP на `8082` (issuer-uri/auth-server-url в `application.yml`
не менялись) — HTTPS на `8443` нужен только браузеру/мобильному клиенту для страницы логина.
