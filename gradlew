#!/usr/bin/env bash
set -euo pipefail
APP_HOME="$(cd "$(dirname "$0")" && pwd)"
if [[ ! -f "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" ]]; then
  "$APP_HOME/scripts/fetch_gradle_wrapper.sh"
fi
exec java -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
