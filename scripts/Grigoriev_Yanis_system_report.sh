#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
REPORT="$ROOT/logs/Grigoriev_Yanis_system_report.log"
mkdir -p "$ROOT/logs"
{
  echo '==== Grigoriev Yanis system report ===='
  echo 'Student: Grigoriev Yanis | Group: IT22303 | Student ID: 37038'
  echo "Date: $(date -Is)"
  echo "User: $(whoami)"
  echo "OS: $( (grep '^PRETTY_NAME=' /etc/os-release || true) | head -1)"
  echo "Kernel: $(uname -sr)"
  echo 'CPU:'
  (lscpu | grep -E '^Model name:|^CPU\(s\):' || true)
  echo 'Memory:'
  free -h
  echo 'Disks:'
  df -h "$ROOT"
  echo 'Available disk:'
  df -h --output=avail "$ROOT"
  echo '==== End report ===='
} | tee -a "$REPORT"
