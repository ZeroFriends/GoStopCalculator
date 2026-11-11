#!/usr/bin/env bash

# ==============================================================================
# 다국어(i18n) 자동화 검증 스크립트 (English, Japanese, Korean)
# ==============================================================================

set -eo pipefail

export PATH="$PATH:$HOME/.maestro/bin:/opt/homebrew/bin:/Users/kakao/Library/Android/sdk/platform-tools"

DEVICE="${1:-emulator-5554}"
PKG="zero.friends.gostopcalculator"

echo "=================================================="
echo "🌐 다국어(i18n) 렌더링 자동 검증 시작 (디바이스: $DEVICE)"
echo "=================================================="

# 1. 영문(English) 검증
echo "▶️ [1/3] 영어(en-US) 로케일 설정 및 검증..."
adb -s "$DEVICE" shell "cmd locale set-app-locales $PKG --locales en-US"
maestro --device "$DEVICE" test -e EXPECTED_START_TEXT="Start" -e EXPECTED_TITLE_TEXT="Today.*Game.*" .maestro/i18n_smoke.yaml
echo "   ✅ 영어 텍스트 정상 검증 완료!"

# 2. 일본어(Japanese) 검증
echo "▶️ [2/3] 일본어(ja-JP) 로케일 설정 및 검증..."
adb -s "$DEVICE" shell "cmd locale set-app-locales $PKG --locales ja-JP"
maestro --device "$DEVICE" test -e EXPECTED_START_TEXT="始める" -e EXPECTED_TITLE_TEXT="本日のゲーム.*" .maestro/i18n_smoke.yaml
echo "   ✅ 일본어 텍스트 정상 검증 완료!"

# 3. 한국어(Korean) 복구 및 검증
echo "▶️ [3/3] 한국어(ko-KR) 로케일 복구 및 검증..."
adb -s "$DEVICE" shell "cmd locale set-app-locales $PKG --locales ko-KR"
maestro --device "$DEVICE" test -e EXPECTED_START_TEXT="시작하기" -e EXPECTED_TITLE_TEXT="오늘의 게임.*" .maestro/i18n_smoke.yaml
echo "   ✅ 한국어 텍스트 정상 검증 완료!"

echo "=================================================="
echo "🎉 모든 언어(EN / JA / KO) 다국어 렌더링 검증 완료!"
echo "=================================================="
