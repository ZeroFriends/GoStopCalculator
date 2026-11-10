#!/usr/bin/env bash

# ==============================================================================
# Maestro Multi-Device Parallel Test Runner
# 여러 대의 에뮬레이터/시뮬레이터를 자동 감지하고 시나리오를 병렬로 분산 실행합니다.
# ==============================================================================

set -eo pipefail

export PATH="$PATH:$HOME/.maestro/bin:/opt/homebrew/bin:/Users/kakao/Library/Android/sdk/platform-tools"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SCENARIO_DIR="$SCRIPT_DIR/scenarios"
LOG_DIR="$SCRIPT_DIR/logs"

mkdir -p "$LOG_DIR"

echo "=================================================="
echo "🚀 Maestro 병렬 테스트 러너 시작"
echo "=================================================="

# 1. 연결된 Android 디바이스/에뮬레이터 탐색
DEVICES=()
while IFS= read -r line; do
    device_id=$(echo "$line" | awk '{print $1}')
    if [[ -n "$device_id" && "$device_id" != "List" ]]; then
        DEVICES+=("$device_id")
    fi
done < <(adb devices | grep -w "device")

# 2. 실행 중인 iOS 시뮬레이터 탐색 (설치되어 있는 경우)
if command -v xcrun &> /dev/null; then
    while IFS= read -r sim_udid; do
        if [[ -n "$sim_udid" ]]; then
            DEVICES+=("$sim_udid")
        fi
    done < <(xcrun simctl list devices | grep "(Booted)" | grep -E -o '[0-9A-F-]{36}' || true)
fi

DEVICE_COUNT=${#DEVICES[@]}

if [[ $DEVICE_COUNT -eq 0 ]]; then
    echo "❌ 실행 중인 에뮬레이터 또는 디바이스가 없습니다."
    echo "👉 에뮬레이터를 먼저 실행해주세요. (예: ~/Library/Android/sdk/emulator/emulator -avd Pixel_3a_...)"
    exit 1
fi

echo "📱 감지된 디바이스 ($DEVICE_COUNT대):"
for dev in "${DEVICES[@]}"; do
    echo "  - $dev"
done
echo "--------------------------------------------------"

# 3. 실행할 시나리오 파일 탐색
SCENARIOS=($(find "$SCENARIO_DIR" -name "*.yaml" -type f | sort))
SCENARIO_COUNT=${#SCENARIOS[@]}

if [[ $SCENARIO_COUNT -eq 0 ]]; then
    echo "❌ 실행할 시나리오 파일(.yaml)이 $SCENARIO_DIR 에 없습니다."
    exit 1
fi

echo "📋 실행할 시나리오 ($SCENARIO_COUNT개):"
for sc in "${SCENARIOS[@]}"; do
    echo "  - $(basename "$sc")"
done
echo "=================================================="

PIDS=()
RESULTS=()

# 4. 시나리오를 디바이스에 분산하여 병렬 실행
for i in "${!SCENARIOS[@]}"; do
    scenario="${SCENARIOS[$i]}"
    scenario_name=$(basename "$scenario" .yaml)
    
    # 라운드로빈 방식으로 디바이스 매핑
    device_index=$(( i % DEVICE_COUNT ))
    target_device="${DEVICES[$device_index]}"
    log_file="$LOG_DIR/${scenario_name}_${target_device}.log"

    echo "▶️ [시작] [$scenario_name] ➔ 디바이스: $target_device"
    
    # 백그라운드로 Maestro 실행
    (
        maestro --device "$target_device" test "$scenario" > "$log_file" 2>&1
        exit_code=$?
        if [[ $exit_code -eq 0 ]]; then
            echo "✅ [성공] [$scenario_name] (디바이스: $target_device)"
        else
            echo "❌ [실패] [$scenario_name] (디바이스: $target_device, 로그: $log_file)"
            exit $exit_code
        fi
    ) &
    
    PIDS+=($!)
done

echo "⏳ 모든 병렬 테스트 완료를 기다리는 중..."
OVERALL_SUCCESS=0

for pid in "${PIDS[@]}"; do
    if ! wait "$pid"; then
        OVERALL_SUCCESS=1
    fi
done

echo "=================================================="
if [[ $OVERALL_SUCCESS -eq 0 ]]; then
    echo "🎉 축하합니다! 모든 시나리오 테스트가 성공적으로 통과했습니다."
else
    echo "⚠️ 일부 시나리오 테스트가 실패했습니다. $LOG_DIR 폴더의 로그를 확인하세요."
fi
echo "=================================================="
exit $OVERALL_SUCCESS
