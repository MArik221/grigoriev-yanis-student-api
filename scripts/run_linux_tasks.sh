#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
chmod 744 scripts/Grigoriev_Yanis_system_report.sh
: > logs/Grigoriev_Yanis_system_report.log
./scripts/Grigoriev_Yanis_system_report.sh
cp README.md backups/Grigoriev_Yanis_README_backup.md
ps aux | head -n 8
pgrep -af java || true
tail -n 5 logs/Grigoriev_Yanis_system_report.log
grep 'Grigoriev' logs/Grigoriev_Yanis_system_report.log > Grigoriev_Yanis_log_analysis.txt
ls -l scripts/Grigoriev_Yanis_system_report.sh backups/Grigoriev_Yanis_README_backup.md
cmp README.md backups/Grigoriev_Yanis_README_backup.md
printf '
Saved analysis:
'
cat Grigoriev_Yanis_log_analysis.txt
