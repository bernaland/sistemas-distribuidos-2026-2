#!/bin/sh
set -eu
for database in users customers suppliers catalog carts orders inventory payments notifications; do
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" --set=db="$database" --set=dbpass="$DB_PASSWORD" <<'SQL'
CREATE ROLE :"db" LOGIN PASSWORD :'dbpass';
CREATE DATABASE :"db" OWNER :"db";
REVOKE CONNECT ON DATABASE :"db" FROM PUBLIC;
GRANT CONNECT ON DATABASE :"db" TO :"db";
SQL
done
