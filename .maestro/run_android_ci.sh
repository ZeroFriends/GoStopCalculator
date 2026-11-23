#!/usr/bin/env bash
# Runs the Android Maestro E2E suite inside one bash process so
# android-emulator-runner preserves exit codes cleanly.
set -uo pipefail

export PATH="$HOME/.maestro/bin:$PATH"

adb wait-for-device
adb install -r app/build/outputs/apk/debug/app-debug.apk

echo "=== Running Maestro E2E Scenarios ==="
./.maestro/run_parallel_tests.sh
