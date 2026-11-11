#!/usr/bin/env bash

# ==============================================================================
# 다중 에뮬레이터 동시 실행 헬퍼 스크립트
# ==============================================================================

EMULATOR_BIN="$HOME/Library/Android/sdk/emulator/emulator"

if [[ ! -f "$EMULATOR_BIN" ]]; then
    echo "❌ 에뮬레이터 바이너리를 찾을 수 없습니다: $EMULATOR_BIN"
    exit 1
fi

AVDS=($("$EMULATOR_BIN" -list-avds | grep -E "^Pixel_" || "$EMULATOR_BIN" -list-avds))

if [[ ${#AVDS[@]} -eq 0 ]]; then
    echo "❌ 등록된 Android AVD가 없습니다. Android Studio에서 AVD를 먼저 생성해주세요."
    exit 1
fi

echo "📋 사용 가능한 AVD 목록:"
for i in "${!AVDS[@]}"; do
    echo "  [$i] ${AVDS[$i]}"
done

echo ""
echo "🚀 1번째 에뮬레이터 시작 (포트: 5554)..."
"$EMULATOR_BIN" -avd "${AVDS[0]}" -port 5554 -no-snapshot-load > /dev/null 2>&1 &
echo "  ➔ PID: $! (포트 5554)"

if [[ ${#AVDS[@]} -ge 2 ]]; then
    echo "🚀 2번째 에뮬레이터 시작 (포트: 5556)..."
    "$EMULATOR_BIN" -avd "${AVDS[1]}" -port 5556 -no-snapshot-load > /dev/null 2>&1 &
    echo "  ➔ PID: $! (포트 5556)"
else
    echo "ℹ️ 등록된 AVD가 1개뿐입니다. AVD를 1개 더 생성하시면 2대를 동시에 띄울 수 있습니다."
fi

# 3. iOS 시뮬레이터 부팅 (이미 부팅된 기기가 없을 경우)
if command -v xcrun &> /dev/null; then
    BOOTED_IOS=$(xcrun simctl list devices | grep "(Booted)" | head -n 1 || true)
    if [[ -n "$BOOTED_IOS" ]]; then
        echo "🍎 이미 실행 중인 iOS 시뮬레이터: $BOOTED_IOS"
    else
        IOS_UDID=$(xcrun simctl list devices available | grep -E "iPhone " | head -n 1 | grep -E -o '[0-9A-F-]{36}' || true)
        if [[ -n "$IOS_UDID" ]]; then
            echo "🚀 iOS 시뮬레이터 시작 ($IOS_UDID)..."
            xcrun simctl boot "$IOS_UDID" > /dev/null 2>&1 || true
        fi
    fi
fi

echo ""
echo "⏳ 에뮬레이터/시뮬레이터 부팅이 완료될 때까지 잠시 대기하세요 (adb devices / xcrun simctl list 로 확인 가능)."
