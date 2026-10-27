#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
BASE=http://localhost:18080
wait_api() {
  for _ in $(seq 1 60); do
    if curl -fsS "$BASE/actuator/health" >/dev/null 2>&1; then return 0; fi
    sleep 2
  done
  echo 'API did not become ready. Check: docker compose logs api' >&2
  return 1
}
wait_api
echo '1) Student identity:'
curl -fsS "$BASE/api/about"; echo
NUMBER="37038-demo-$(date +%s)"
PAYLOAD=$(printf '{"name":"Yanis","surname":"Grigoriev","groupCode":"IT22303","studentNumber":"%s"}' "$NUMBER")
echo '2) Create:'
CREATED=$(curl -fsS -X POST "$BASE/api/students" -H 'Content-Type: application/json' -d "$PAYLOAD")
echo "$CREATED"
ID=$(printf '%s' "$CREATED" | python3 -c 'import sys,json; print(json.load(sys.stdin)["id"])')
echo '3) List:'
curl -fsS "$BASE/api/students"; echo
echo "4) Read ID=$ID:"
curl -fsS "$BASE/api/students/$ID"; echo
echo '5) Update:'
UPDATE=$(printf '{"name":"Yanis","surname":"Grigoriev","groupCode":"IT22303-UPDATED","studentNumber":"%s"}' "$NUMBER")
curl -fsS -X PUT "$BASE/api/students/$ID" -H 'Content-Type: application/json' -d "$UPDATE"; echo
echo '6) Restart BOTH services without deleting the PostgreSQL named volume:'
docker compose down
docker compose up -d
wait_api
echo '7) Read same record again after restart (persistence):'
curl -fsS "$BASE/api/students/$ID"; echo
echo '8) Delete:'
curl -sS -o /dev/null -w 'HTTP %{http_code}\n' -X DELETE "$BASE/api/students/$ID"
echo '9) Read deleted record, expect HTTP 404:'
CODE=$(curl -sS -o /dev/null -w '%{http_code}' "$BASE/api/students/$ID")
echo "HTTP $CODE"
[[ "$CODE" == 404 ]]
echo 'CRUD + PostgreSQL persistence demonstration passed.'
