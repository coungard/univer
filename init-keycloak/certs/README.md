# Dev-сертификат для Keycloak (issue #54)

`keycloak-dev.crt` / `keycloak-dev.key` — самоподписанный сертификат **только для локальной разработки**,
сгенерированный командой:

```bash
openssl req -x509 -nodes -newkey rsa:2048 \
  -keyout keycloak-dev.key -out keycloak-dev.crt -days 3650 \
  -subj "/CN=localhost" \
  -addext "subjectAltName=DNS:localhost,DNS:keycloak,IP:127.0.0.1,IP:10.0.2.2"
```

SAN включает `10.0.2.2` — алиас хост-машины из гостевой сети Android-эмулятора.

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
