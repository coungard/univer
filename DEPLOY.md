# Деплой стенда (issue #2)

Стенд — копия бэкенда в интернете для мобильного приложения и тестировщиков: настоящий HTTPS, никаких
самоподписанных сертификатов и LAN-адресов (ср. `init-keycloak/certs/README.md`). Разработка по-прежнему
локальная, через `docker-compose.yml`.

| | |
|---|---|
| Сервер | `ssh root@81.177.48.254` (1Gb.ru, Ubuntu 26.04, 2 vCPU / 4 ГБ / 40 ГБ), вход только по ключу |
| API | `https://api.81-177-48-254.sslip.io` (Swagger UI — `/swagger-ui/index.html`) |
| Keycloak | `https://auth.81-177-48-254.sslip.io`, realm `univer-realm` |
| Файлы | `/opt/univer`: `docker-compose.prod.yml`, `deploy/Caddyfile`, `init-keycloak/`, `.env.prod` |
| Секреты | `/opt/univer/.env.prod` на сервере (права 600, в git не попадает) |

Домена пока нет, поэтому адреса на [sslip.io](https://sslip.io): `<имя>.81-177-48-254.sslip.io` резолвится в IP
сервера, и Let's Encrypt выдаёт на такие имена обычные сертификаты.

## Как устроено

```
интернет ──443──▶ caddy ──▶ app:8023        (api.<домен>)
                       └──▶ keycloak:8080   (auth.<домен>, кроме /admin — 403)
                  app ──▶ keycloak:8080, postgres:5432   (внутренняя docker-сеть)
```

- Наружу открыты только 80/443 (Caddy) и 22 (SSH); `ufw` на сервере пропускает ещё 1234 — агент панели 1Gb.ru.
  Postgres, Keycloak и бэкенд портов не публикуют: Docker обходит `ufw`, опубликованный порт был бы виден снаружи.
- Сертификаты Caddy получает и продлевает сам, хранит в volume `caddy_data`.
- Keycloak работает в режиме `start` за прокси (`KC_PROXY=edge`). Realm импортируется из
  `init-keycloak/realm-config.json` **только при первом запуске**; секрет `univer-client`, пароли `super` и
  `student1` и redirect URI для Swagger UI подставляются из `.env.prod` (`${VAR:значение для dev}` в файле realm).
  Дальше они живут в базе Keycloak — меняйте их в админке, правка `.env.prod` на них уже не влияет.
- Токены, выданные стендом, имеют `iss: https://auth.<домен>/realms/univer-realm`.

## Первый запуск

```bash
ssh root@81.177.48.254
mkdir -p /opt/univer && cd /opt/univer
# скопировать docker-compose.prod.yml, .env.prod.example, deploy/Caddyfile, init-keycloak/{realm-config.json,init-keycloak-db.sql}
cp .env.prod.example .env.prod && chmod 600 .env.prod
# заполнить пароли: openssl rand -base64 24
docker compose -f docker-compose.prod.yml --env-file .env.prod up -d
```

Keycloak на этом сервере стартует около минуты, бэкенд ждёт его healthcheck.

## Обновление бэкенда

Образ собирается не на сервере (на 2 vCPU/4 ГБ Maven-сборка рядом с работающим стеком слишком тяжела), а
локально или в CI, и пушится в Docker Hub (`coungard/univer:backend`, issue #125). На сервере:

```bash
cd /opt/univer
docker compose -f docker-compose.prod.yml --env-file .env.prod pull app
docker compose -f docker-compose.prod.yml --env-file .env.prod up -d app
docker image prune -f
```

Миграции Flyway применяются при старте бэкенда. Откат — `BACKEND_IMAGE=<прежний тег>` в `.env.prod` и тот же `up -d`.

## Админка Keycloak

Снаружи закрыта, открывается через SSH-туннель:

```bash
ssh -L 8081:127.0.0.1:8081 root@81.177.48.254
```

и в браузере `http://localhost:8081/admin/` — логин `admin`, пароль `KEYCLOAK_ADMIN_PASSWORD` из `.env.prod`.

## Логи и состояние

```bash
cd /opt/univer
docker compose -f docker-compose.prod.yml --env-file .env.prod ps
docker compose -f docker-compose.prod.yml --env-file .env.prod logs -f --tail=100 app
docker stats --no-stream
```

## Смена домена

1. A-записи `api.<домен>` и `auth.<домен>` → `81.177.48.254`.
2. `API_DOMAIN` / `AUTH_DOMAIN` в `.env.prod`, затем `up -d` — Caddy выпустит новые сертификаты.
3. В админке Keycloak у клиента `univer-client` заменить redirect URI и web origin на `https://api.<домен>`
   (realm уже импортирован, из `.env.prod` они не обновятся).
4. В мобильном приложении — новые адреса.

## Ещё не сделано (issue #2)

- Ежедневный бэкап Postgres (`pg_dump`) с копией вне сервера — провайдер бэкапов не делает.
- Автоматический деплой из GitHub Actions.
