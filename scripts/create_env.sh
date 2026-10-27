#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if [[ -f .env ]]; then echo '.env already exists; leaving unchanged'; exit 0; fi
if ! command -v openssl >/dev/null; then echo 'Install openssl first' >&2; exit 1; fi
{
 echo 'DB_NAME=studentsdb'
 echo 'DB_USER=studentapi'
 printf 'DB_PASSWORD=%s
' "$(openssl rand -hex 24)"
} > .env
chmod 600 .env
echo 'Created .env (local secret; DO NOT commit).'
