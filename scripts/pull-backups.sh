#!/usr/bin/env bash
# Копия бэкапов стенда вне сервера (issue #138): забирает с VDS самые свежие дамп БД и архив медиа
# (deploy/backup.sh) в локальный каталог. Провайдер VDS бэкапов не делает — без этой копии потеря сервера
# означает потерю данных. Запускать регулярно с машины разработчика:
#
#   scripts/pull-backups.sh [каталог]     # по умолчанию ./backups (в .gitignore)
set -euo pipefail

HOST=${UNIVER_HOST:-root@81.177.48.254}
REMOTE_DIR=/opt/univer/backups
DEST=${1:-backups}

mkdir -p "$DEST"
for pattern in 'db-*.sql.gz' 'media-*.tar.gz'; do
  latest=$(ssh "$HOST" "ls -1t $REMOTE_DIR/$pattern 2>/dev/null | head -n 1")
  if [ -z "$latest" ]; then
    echo "На сервере нет $pattern" >&2
    exit 1
  fi
  name=$(basename "$latest")
  if [ -f "$DEST/$name" ]; then
    echo "Уже есть: $DEST/$name"
  else
    scp -q "$HOST:$latest" "$DEST/$name.part"
    mv "$DEST/$name.part" "$DEST/$name"
    echo "Скопирован: $DEST/$name"
  fi
done
