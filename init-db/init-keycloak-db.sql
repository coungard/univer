-- Этот скрипт выполнится при старте PostgreSQL
-- и создаст базу данных keycloak, если её нет

SELECT 'CREATE DATABASE keycloak'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'keycloak')
\gexec