#!/usr/bin/env bash
# Runs the Android Maestro E2E suite and the 13-screen runtime i18n crawl inside
# one bash process so android-emulator-runner preserves exit codes and executes
# both phases before reporting overall status.
set -uo pipefail

export PATH="$HOME/.maestro/bin:$PATH"

adb wait-for-device
adb install -r app/build/outputs/apk/debug/app-debug.apk

echo "=== [1/2] Running Maestro E2E Scenarios ==="
./.maestro/run_parallel_tests.sh
E2E_STATUS=$?

echo "=== [2/2] Running i18n 13-Screen Runtime Crawl ==="
python3 .maestro/i18n_screen_crawl.py --device emulator-5554
CRAWL_STATUS=$?

echo "Summary: E2E=$E2E_STATUS, CRAWL=$CRAWL_STATUS"
if [[ $E2E_STATUS -ne 0 || $CRAWL_STATUS -ne 0 ]]; then
  exit 1
fi
