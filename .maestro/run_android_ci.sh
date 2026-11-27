#!/usr/bin/env bash
# Runs the Android Maestro E2E suite inside one bash process so
# android-emulator-runner preserves exit codes cleanly.
set -uo pipefail

export PATH="$HOME/.maestro/bin:$PATH"

adb wait-for-device

# Suppress Android system ANR/crash dialogs (e.g., "Pixel Launcher isn't responding") on CI emulators
adb shell settings put global hide_error_dialogs 1 || true
adb shell settings put secure anr_show_background 0 || true
adb shell am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS || true
adb shell am force-stop com.google.android.apps.nexuslauncher || true

adb install -r app/build/outputs/apk/debug/app-debug.apk

echo "=== Running Maestro E2E Scenarios ==="
./.maestro/run_parallel_tests.sh
