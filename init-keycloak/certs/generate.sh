#!/usr/bin/env bash
# Перевыпуск самоподписанного dev-сертификата Keycloak (issue #54, #127).
#
#   ./generate.sh                              # только базовые SAN (localhost, keycloak, 127.0.0.1, 10.0.2.2)
#   ./generate.sh 192.168.1.42 my-mac.local    # + адреса Mac для запуска на реальном iPhone
#
# Каждый аргумент добавляется в subjectAltName: IPv4/IPv6-адрес как IP, всё остальное как DNS.
# Срок — 825 дней: максимум, который iOS 13+ принимает для серверного TLS-сертификата
# (https://support.apple.com/en-us/103769). Подробности — README.md рядом.
set -euo pipefail

# Git Bash на Windows иначе превращает "/CN=localhost" в путь к файлу
export MSYS_NO_PATHCONV=1

cd "$(dirname "$0")"

san="DNS:localhost,DNS:keycloak,IP:127.0.0.1,IP:10.0.2.2"
for host in "$@"; do
  if [[ "$host" =~ ^[0-9]+(\.[0-9]+){3}$ || "$host" == *:* ]]; then
    san+=",IP:$host"
  else
    san+=",DNS:$host"
  fi
done

openssl req -x509 -nodes -newkey rsa:2048 \
  -keyout keycloak-dev.key -out keycloak-dev.crt -days 825 \
  -subj "/CN=localhost" \
  -addext "subjectAltName=$san" \
  -addext "extendedKeyUsage=serverAuth"

# Keycloak в контейнере работает не от root и должен прочитать ключ из ro-маунта;
# openssl на macOS/Linux создаёт ключ с правами 600
chmod 644 keycloak-dev.key keycloak-dev.crt

echo "subjectAltName: $san"
openssl x509 -in keycloak-dev.crt -noout -enddate
