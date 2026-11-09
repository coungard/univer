# Деплой стенда (issue #2)

Стенд — копия бэкенда в интернете для мобильного приложения и тестировщиков: настоящий HTTPS, никаких
самоподписанных сертификатов и LAN-адресов (ср. `init-keycloak/certs/README.md`). Разработка по-прежнему
локальная, через `docker-compose.yml`.

| | |
|---|---|
| Сервер | `ssh root@81.177.48.254` (1Gb.ru, Ubuntu 26.04, 2 vCPU / 4 ГБ / 40 ГБ), вход только по ключу |
| API | `https://api.univerapp.ru` (Swagger UI — `/swagger-ui/index.html`) |
| Keycloak | `https://auth.univerapp.ru`, realm `univer-realm` |
| Домен | `univerapp.ru`, куплен и обслуживается в 1Gb.ru (DNS: `ns1.1gb.ru`, `ns2.1gb.ru`, `ns3.1gb-ru.com`) |
| Файлы | `/opt/univer`: `docker-compose.prod.yml`, `deploy/` (`Caddyfile`, `backup.sh`), `init-keycloak/`, `.env.prod` |
| Бэкапы | `/opt/univer/backups` (ежедневно, `deploy/backup.sh`), копия вне сервера — `scripts/pull-backups.sh` |
| Секреты | `/opt/univer/.env.prod` на сервере (права 600, в git не попадает) |

DNS-записи в панели 1Gb.ru: A `api` и A `auth` → `81.177.48.254`. Корень и `www` тоже указывают на сервер, но
пока не обслуживаются. CAA панель не поддерживает.

## Как устроено

```
интернет ──443──▶ caddy ──▶ app:8023        (api.<домен>)
                       ├──▶ volume media_data, только чтение   (api.<домен>/media/*)
                       └──▶ keycloak:8080   (auth.<домен>, кроме /admin — 403)
                  app ──▶ keycloak:8080, postgres:5432   (внутренняя docker-сеть)
                  app ──▶ volume media_data, /data/media  (запись)
```

- Наружу открыты только 80/443 (Caddy) и 22 (SSH); `ufw` на сервере пропускает ещё 1234 — агент панели 1Gb.ru.
  Postgres, Keycloak и бэкенд портов не публикуют: Docker обходит `ufw`, опубликованный порт был бы виден снаружи.
- Сертификаты Caddy получает и продлевает сам, хранит в volume `caddy_data`.
- Keycloak работает в режиме `start` за прокси (`KC_PROXY=edge`), hostname не зафиксирован (`KC_HOSTNAME_STRICT=false`):
  адреса строятся из запроса — `https://auth.<домен>` через Caddy, `http://localhost:8081` через туннель к админке.
  Realm импортируется из
  `init-keycloak/realm-config.json` **только при первом запуске**; секрет `univer-client`, пароли `super` и
  `student1` и redirect URI для Swagger UI подставляются из `.env.prod` (`${VAR:значение для dev}` в файле realm).
  Дальше они живут в базе Keycloak — меняйте их в админке, правка `.env.prod` на них уже не влияет.
- Токены, выданные стендом, имеют `iss: https://auth.<домен>/realms/univer-realm`.
- **Медиа-файлы** (аватары, issue #138) лежат в volume `media_data`: бэкенд пишет их в `/data/media`, Caddy
  отдаёт по `https://api.<домен>/media/<ключ>` прямо с диска (read-only), без бэкенда и без токена. Отдаются
  только `*.jpg`/`*.jpeg`/`*.png`/`*.webp`, листинга каталогов нет (404), кэш —
  `public, max-age=31536000, immutable` (новая загрузка — новый ключ). Тела запросов к API больше 6 МБ Caddy
  отклоняет с 413. Контейнер бэкенда работает от root, так что прав на запись в volume хватает.

## Первый запуск

```bash
ssh root@81.177.48.254
mkdir -p /opt/univer && cd /opt/univer
# скопировать docker-compose.prod.yml, .env.prod.example, deploy/Caddyfile, init-keycloak/{realm-config.json,init-keycloak-db.sql}
cp .env.prod.example .env.prod && chmod 600 .env.prod
# заполнить пароли: openssl rand -base64 24
docker compose -f docker-compose.prod.yml --env-file .env.prod up -d
```

Keycloak на этом сервере стартует около минуты, бэкенд ждёт его healthcheck. Затем — бэкап (раздел «Бэкап»).

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

После обновления, затрагивающего `docker-compose.prod.yml` или `deploy/` (новый volume, Caddyfile), скопировать
эти файлы на сервер и пересоздать затронутые сервисы: `up -d` (без имени сервиса). Caddyfile без пересоздания
контейнера перечитывается так: `docker compose ... exec caddy caddy reload --config /etc/caddy/Caddyfile`.

## Медиа-файлы

Проверка после выкладки (подставить ключ из ответа `PUT /api/v1/users/me/avatar`):

```bash
curl -sI https://api.univerapp.ru/media/avatars/<fileId>/128.jpg  # 200, image/jpeg, Cache-Control ... immutable
curl -sI https://api.univerapp.ru/media/                          # 404 — листинга нет
curl -sI https://api.univerapp.ru/media/avatars/                  # 404
```

Сколько занимают и сколько места осталось (те же цифры каждый день пишет бэкап в `/var/log/univer-backup.log`):

```bash
cd /opt/univer
docker compose -f docker-compose.prod.yml --env-file .env.prod exec caddy du -sh /srv/media
df -h /
```

Пороги переезда медиа в облачное S3 — в issue #135: медиа ≥ 15 ГБ или свободно < 10 ГБ, трафик `/media/*` в
пиковый час ≥ 30% канала, бэкап дольше часа, второй инстанс бэкенда, видео. О первых и последнем бэкап
пишет `ВНИМАНИЕ` в лог. Трафик пока не считается: access-лог Caddy не включён — это часть #114. Скорость канала
VDS ещё не уточнена (в расчётах #135 — 100 Мбит/с): посмотреть в тарифе в панели 1Gb.ru и вписать сюда.

## Бэкап

`deploy/backup.sh` — `pg_dumpall` (обе базы: `univerdb` и `keycloak`, с ролями) и архив volume `media_data` в
`/opt/univer/backups` (`db-<дата>.sql.gz` — 7 последних, `media-<дата>.tar.gz` — 2 последних: архив медиа полный).
Недописанный бэкап остаётся `.part` и не вытесняет целые при ротации. Установка:

```bash
cd /opt/univer
chmod +x deploy/backup.sh
deploy/backup.sh                                  # первый прогон вручную, смотреть вывод
cp deploy/univer-backup.cron /etc/cron.d/univer-backup && chmod 644 /etc/cron.d/univer-backup
tail /var/log/univer-backup.log                   # результат ночных прогонов (03:30 по времени сервера)
```

**Копия вне сервера.** Провайдер VDS бэкапов не делает: бэкап на том же диске не спасёт при потере сервера.
Самые свежие дамп и архив медиа забирает на машину разработчика `scripts/pull-backups.sh` (в `./backups`,
каталог в `.gitignore`) — запускать регулярно, хотя бы раз в неделю.

Восстановление:

```bash
cd /opt/univer
C="docker compose -f docker-compose.prod.yml --env-file .env.prod"
$C stop app keycloak
gunzip -c backups/db-<дата>.sql.gz | $C exec -T postgres psql -U postgres -d postgres   # на пустой Postgres
$C run --rm --no-deps -T --entrypoint sh app -c 'tar -C /data/media -xzf -' < backups/media-<дата>.tar.gz
$C up -d
```

`pg_dumpall` пересоздаёт базы и роли, поэтому накатывать его — на свежий volume Postgres (`$C down`,
`docker volume rm univer_postgres_data`, `$C up -d postgres`). Файлы-сироты, если архив медиа свежее дампа,
через сутки уберёт `MediaSweeper`; аватары, которых нет в архиве, в профилях будут отдавать 404.

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

1. A-записи `api.<домен>` и `auth.<домен>` → `81.177.48.254`. Дождаться, пока они резолвятся у публичных DNS
   (`dig +short api.<домен> @8.8.8.8`), иначе Caddy не пройдёт проверку Let's Encrypt.
2. `API_DOMAIN` / `AUTH_DOMAIN` в `.env.prod`, затем `up -d` — Caddy выпустит новые сертификаты, бэкенд получит
   новый `KEYCLOAK_PUBLIC_URL`. Keycloak перезапускать не нужно: адреса он берёт из запроса. Первую минуту
   после `up -d` HTTPS на новых адресах обрывается на рукопожатии (Caddy ещё получает сертификаты), а API
   отвечает 502 (бэкенд стартует ~20 с) — это нормально, проверять после `certificate obtained successfully`
   в `docker logs univer-caddy-1`.
3. Redirect URI и web origin клиента `univer-client` (для Swagger UI). Realm уже импортирован, из `.env.prod` они
   не обновятся — меняем через `kcadm.sh` в контейнере:
   ```bash
   cd /opt/univer
   docker compose -f docker-compose.prod.yml --env-file .env.prod exec keycloak bash -c '
     K=/opt/keycloak/bin/kcadm.sh
     $K config credentials --server http://localhost:8080 --realm master --user "$KEYCLOAK_ADMIN" --password "$KEYCLOAK_ADMIN_PASSWORD"
     ID=$($K get clients -r univer-realm -q clientId=univer-client --fields id --format csv --noquotes)
     $K update clients/$ID -r univer-realm \
       -s "redirectUris=[\"http://localhost:8023/*\",\"https://api.<домен>/*\"]" \
       -s "webOrigins=[\"http://localhost:*\",\"https://localhost:*\",\"https://api.<домен>\",\"+\"]"'
   ```
4. В мобильном приложении — новые адреса. Токены, выданные на старом адресе, имеют другой `iss`, и Keycloak
   не обновит их на новом — пользователям нужно один раз войти заново.

## Ещё не сделано (issue #2)

- Копия бэкапов вне сервера по расписанию — пока вручную, `scripts/pull-backups.sh` (см. «Бэкап»).
- Автоматический деплой из GitHub Actions.
