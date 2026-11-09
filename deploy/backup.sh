#!/usr/bin/env bash
# Ежедневный бэкап стенда (issue #2, #138): обе базы Postgres (univerdb и keycloak) и медиа-файлы из volume
# media_data — в /opt/univer/backups. Провайдер VDS бэкапов не делает, поэтому копию вне сервера забирает
# scripts/pull-backups.sh (DEPLOY.md, «Бэкап»).
#
# Запуск — cron (deploy/univer-backup.cron), вручную: /opt/univer/deploy/backup.sh
# Заодно печатает объём медиа и свободное место — пороги переезда медиа в облако (issue #135).
set -euo pipefail

cd /opt/univer
BACKUP_DIR=${BACKUP_DIR:-/opt/univer/backups}
KEEP_DB=${KEEP_DB:-7}        # дампов БД — по одному в день, неделя
KEEP_MEDIA=${KEEP_MEDIA:-2}  # архивы медиа — полные, поэтому храним меньше
MEDIA_LIMIT_GB=15            # пороги из issue #135
MIN_FREE_GB=10

compose() {
  docker compose -f docker-compose.prod.yml --env-file .env.prod "$@"
}

log() {
  echo "$(date '+%F %T') $*"
}

# Оставить только $2 самых свежих файлов по шаблону $1
rotate() {
  { ls -1t $BACKUP_DIR/$1 2>/dev/null || true; } | tail -n +$(($2 + 1)) | xargs -r rm -f
}

mkdir -p "$BACKUP_DIR"
chmod 700 "$BACKUP_DIR"
STAMP=$(date +%Y%m%d-%H%M)
STARTED=$(date +%s)

# Пишем во временный файл и переименовываем только при успехе: оборванный бэкап не вытеснит ротацией целый
log "Postgres: pg_dumpall"
compose exec -T postgres pg_dumpall -U postgres | gzip > "$BACKUP_DIR/db-$STAMP.sql.gz.part"
mv "$BACKUP_DIR/db-$STAMP.sql.gz.part" "$BACKUP_DIR/db-$STAMP.sql.gz"

# Архив снимает Caddy: volume у него уже примонтирован read-only, отдельный контейнер не нужен
log "Медиа: tar"
compose exec -T caddy tar -C /srv/media -czf - . > "$BACKUP_DIR/media-$STAMP.tar.gz.part"
mv "$BACKUP_DIR/media-$STAMP.tar.gz.part" "$BACKUP_DIR/media-$STAMP.tar.gz"

rotate 'db-*.sql.gz' "$KEEP_DB"
rotate 'media-*.tar.gz' "$KEEP_MEDIA"
rm -f "$BACKUP_DIR"/*.part

ELAPSED=$(($(date +%s) - STARTED))
MEDIA_KB=$(compose exec -T caddy du -sk /srv/media | cut -f1)
FREE_KB=$(df -Pk / | awk 'NR == 2 {print $4}')
log "Готово за ${ELAPSED} с: $(du -h "$BACKUP_DIR/db-$STAMP.sql.gz" | cut -f1) БД," \
  "$(du -h "$BACKUP_DIR/media-$STAMP.tar.gz" | cut -f1) медиа; медиа в volume $((MEDIA_KB / 1024)) МБ," \
  "свободно на диске $((FREE_KB / 1024 / 1024)) ГБ"

if [ "$MEDIA_KB" -ge $((MEDIA_LIMIT_GB * 1024 * 1024)) ]; then
  log "ВНИМАНИЕ: медиа ≥ ${MEDIA_LIMIT_GB} ГБ — порог переезда в облако (issue #135)"
fi
if [ "$FREE_KB" -lt $((MIN_FREE_GB * 1024 * 1024)) ]; then
  log "ВНИМАНИЕ: свободно меньше ${MIN_FREE_GB} ГБ — порог переезда медиа в облако (issue #135)"
fi
if [ "$ELAPSED" -ge 3600 ]; then
  log "ВНИМАНИЕ: бэкап шёл дольше часа — порог переезда медиа в облако (issue #135)"
fi
