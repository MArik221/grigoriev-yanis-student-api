#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
# The sandbox cannot fetch binaries. On the student machine use the official Gradle distribution.
VER=8.14.3
BASE="https://services.gradle.org/distributions/gradle-${VER}-wrapper.jar"
mkdir -p gradle/wrapper
if [[ -f gradle/wrapper/gradle-wrapper.jar ]]; then
 echo 'gradle-wrapper.jar already available'; exit 0
fi
command -v curl >/dev/null || { echo 'Install curl' >&2; exit 1; }
curl -fL --retry 3 -o gradle/wrapper/gradle-wrapper.jar "$BASE"
curl -fL --retry 3 -o gradle/wrapper/gradle-wrapper.jar.sha256 "$BASE.sha256"
(cd gradle/wrapper && sed -E 's/^([0-9a-fA-F]{64}).*/\1  gradle-wrapper.jar/' gradle-wrapper.jar.sha256 | sha256sum -c -)
rm gradle/wrapper/gradle-wrapper.jar.sha256
echo 'Official Gradle Wrapper downloaded and checked.'
