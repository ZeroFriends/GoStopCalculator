#!/usr/bin/env bash

# ==============================================================================
# Maestro Multi-Device Parallel Test Runner
# 각 디바이스에 시나리오를 분배하고 디바이스별로 병렬 실행합니다.
# ==============================================================================

set -eo pipefail

export PATH="$PATH:$HOME/.maestro/bin:/opt/homebrew/bin:/Users/kakao/Library/Android/sdk/platform-tools"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SCENARIO_DIR="$SCRIPT_DIR/scenarios"
LOG_DIR="$SCRIPT_DIR/logs"

rm -rf "$LOG_DIR"
mkdir -p "$LOG_DIR"

echo "=================================================="
echo "🚀 Maestro 병렬 테스트 러너 시작"
echo "=================================================="

# 1. 연결된 Android 에뮬레이터 탐색 (INCLUDE_PHYSICAL=1 시 실기기 포함)
ANDROID_DEVICES=()
while IFS= read -r line; do
    device_id=$(echo "$line" | awk '{print $1}')
    if [[ -n "$device_id" && "$device_id" != "List" ]]; then
        if [[ "${INCLUDE_PHYSICAL:-0}" == "1" || "$device_id" == emulator-* ]]; then
            ANDROID_DEVICES+=("$device_id")
        else
            echo "ℹ️ 실기기($device_id)는 데이터 보호를 위해 제외됨 (포함하려면 INCLUDE_PHYSICAL=1 사용)"
        fi
    fi
done < <(adb devices | grep -w "device" || true)

# 2. 실행 중인 iOS 시뮬레이터 탐색
IOS_DEVICES=()
if command -v xcrun &> /dev/null; then
    while IFS= read -r sim_udid; do
        if [[ -n "$sim_udid" ]]; then
            IOS_DEVICES+=("$sim_udid")
        fi
    done < <(xcrun simctl list devices | grep "(Booted)" | grep -E -o '[0-9A-F-]{36}' || true)
fi

ANDROID_COUNT=${#ANDROID_DEVICES[@]}
IOS_COUNT=${#IOS_DEVICES[@]}
TOTAL_DEVICES=$((ANDROID_COUNT + IOS_COUNT))

if [[ $TOTAL_DEVICES -eq 0 ]]; then
    echo "❌ 실행 중인 에뮬레이터 또는 디바이스가 없습니다."
    exit 1
fi

echo "📱 감지된 디바이스 (총 ${TOTAL_DEVICES}대: Android ${ANDROID_COUNT}대 / iOS ${IOS_COUNT}대):"
for dev in "${ANDROID_DEVICES[@]}"; do
    echo "  - $dev (Android)"
done
for dev in "${IOS_DEVICES[@]}"; do
    echo "  - $dev (iOS Simulator)"
done
echo "--------------------------------------------------"

# 임시 작업/상태 디렉터리 준비 및 일괄 정리
ANDROID_WORK_DIR=$(mktemp -d)
IOS_WORK_DIR=$(mktemp -d)
DEVICE_STATUS_DIR=$(mktemp -d)
trap 'rm -rf "$ANDROID_WORK_DIR" "$IOS_WORK_DIR" "$DEVICE_STATUS_DIR"' EXIT

prepare_platform_scenarios() {
    local dest_dir="$1"
    local sed_expr="$2"
    cp -R "$SCRIPT_DIR/common" "$dest_dir/common"
    cp -R "$SCRIPT_DIR/scenarios" "$dest_dir/scenarios"
    while IFS= read -r yaml_file; do
        local tmp_yaml
        tmp_yaml=$(mktemp)
        sed -E "$sed_expr" "$yaml_file" > "$tmp_yaml"
        mv "$tmp_yaml" "$yaml_file"
    done < <(find "$dest_dir" -name "*.yaml" -type f)
}

# Android는 pm clear와 cmd locale set-app-locales 순서를 러너가 제어하도록 clearState: false로 치환
prepare_platform_scenarios "$ANDROID_WORK_DIR" 's/^([[:space:]]*clearState:[[:space:]]*)true/\1false/'
prepare_platform_scenarios "$IOS_WORK_DIR" 's/^appId: zero\.friends\.gostopcalculator$/appId: zero.friends.gostopcalculator.iosApp/'

reset_android_app_state() {
    local dev="$1"
    local pkg="zero.friends.gostopcalculator"
    adb -s "$dev" shell am force-stop "$pkg" > /dev/null 2>&1 || true
    adb -s "$dev" shell pm clear "$pkg" > /dev/null 2>&1 || true
    # Android ActivityTaskManager의 remove-task 정리 타이머(1s)와 새 launch 충돌 방지
    sleep 2
    adb -s "$dev" shell cmd locale set-app-locales "$pkg" --locales ko-KR > /dev/null 2>&1 || true
}

# 3. 실행할 시나리오 파일 탐색
SCENARIOS=($(find "$SCENARIO_DIR" -name "*.yaml" -type f | sort))
SCENARIO_COUNT=${#SCENARIOS[@]}

if [[ $SCENARIO_COUNT -eq 0 ]]; then
    echo "❌ 실행할 시나리오 파일이 없습니다."
    exit 1
fi

echo "📋 플랫폼별 전체 시나리오(${SCENARIO_COUNT}개)를 각 플랫폼 풀 내에서 병렬 분산 실행합니다."
echo "=================================================="

DEVICE_PIDS=()
WORKER_STAGGER=0

launch_platform_pool() {
    local platform="$1"
    local scenarios_root="$2"
    shift 2
    local pool_devices=("$@")
    local pool_count=${#pool_devices[@]}

    [[ $pool_count -eq 0 ]] && return 0

    for dev_idx in "${!pool_devices[@]}"; do
        local dev="${pool_devices[$dev_idx]}"
        local dev_scenarios=()
        for s_idx in "${!SCENARIOS[@]}"; do
            if [[ $(( s_idx % pool_count )) -eq $dev_idx ]]; then
                dev_scenarios+=("$scenarios_root/$(basename "${SCENARIOS[$s_idx]}")")
            fi
        done

        [[ ${#dev_scenarios[@]} -eq 0 ]] && continue

        echo "▶️ [$platform Worker $dev_idx] 디바이스: $dev (${#dev_scenarios[@]}개 시나리오 할당)"
        for sc in "${dev_scenarios[@]}"; do
            echo "    └ $(basename "$sc")"
        done

        local stagger=$((WORKER_STAGGER * 3))
        WORKER_STAGGER=$((WORKER_STAGGER + 1))

        (
            sleep "$stagger"
            dev_failed=0
            first_run=1
            for sc in "${dev_scenarios[@]}"; do
                sc_name=$(basename "$sc" .yaml)
                log_file="$LOG_DIR/${platform}_${sc_name}_${dev}.log"
                if [[ "$platform" == "android" ]]; then
                    reset_android_app_state "$dev"
                    if [[ $first_run -eq 1 ]]; then
                        applied_locale=$(adb -s "$dev" shell cmd locale get-app-locales zero.friends.gostopcalculator 2>&1 | tr -d '\r' || true)
                        echo "   🌐 [$platform:$dev] App locale: ${applied_locale:-unknown}"
                    fi
                fi
                echo "   ⏳ [$platform:$dev] 실행 중: $sc_name..."

                extra_flags=()
                if [[ $first_run -eq 0 ]]; then
                    extra_flags+=(--no-reinstall-driver)
                fi

                if maestro --device "$dev" test "${extra_flags[@]}" "$sc" > "$log_file" 2>&1; then
                    first_run=0
                    echo "   ✅ [$platform:$dev] 성공: $sc_name"
                else
                    echo "   ❌ [$platform:$dev] 실패: $sc_name (로그: $log_file)"
                    dev_failed=1
                fi
            done

            if [[ $dev_failed -ne 0 ]]; then
                touch "$DEVICE_STATUS_DIR/failed_${platform}_${dev_idx}"
            fi
        ) &

        DEVICE_PIDS+=($!)
    done
}

launch_platform_pool "android" "$ANDROID_WORK_DIR/scenarios" "${ANDROID_DEVICES[@]}"
launch_platform_pool "ios" "$IOS_WORK_DIR/scenarios" "${IOS_DEVICES[@]}"

echo "--------------------------------------------------"
echo "⏳ 모든 디바이스 워커의 병렬 실행 완료를 대기 중..."

for pid in "${DEVICE_PIDS[@]}"; do
    wait "$pid" || true
done

echo "=================================================="
FAILED_COUNT=$(ls -1 "$DEVICE_STATUS_DIR" 2>/dev/null | wc -l | tr -d ' ')

if [[ "$FAILED_COUNT" -eq 0 ]]; then
    echo "🎉 축하합니다! 모든 플랫폼/시나리오 테스트가 성공적으로 통과했습니다!"
    exit 0
else
    echo "⚠️ 일부 시나리오가 실패했습니다. .maestro/logs 폴더의 로그를 확인하세요."
    exit 1
fi
