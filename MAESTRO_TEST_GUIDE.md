# Maestro E2E 시나리오 테스트 & 다중 에뮬레이터 병렬 실행 가이드

본 프로젝트(`GoStopCalculator`)의 화면별 시나리오 검증, 다국어(i18n) 체크, 멀티 디바이스 병렬 테스트 환경 가이드입니다.

---

## 1. 환경 구성 요약

- **프레임워크**: [Maestro](https://maestro.mobile.dev) `v2.12.1` 설치 완료
- **MCP 연동**: `~/.gemini/config/mcp_config.json`에 `maestro mcp` 등록 완료
- **디렉토리 구조**:
  - [`.maestro/scenarios/`](file:///Users/kakao/sideproject/app/GoStopCalculator/.maestro/scenarios): 화면별/기능별 크로스 플랫폼(Android/iOS) E2E 시나리오 YAML 파일 (총 7종)
  - [`.maestro/common/assert_no_broken_text.yaml`](file:///Users/kakao/sideproject/app/GoStopCalculator/.maestro/common/assert_no_broken_text.yaml): 각 주요 화면 진입 시 미치환 포맷(`%d`, `%1$s`), `null`, `undefined`, `NaN`, 리터럴 `\n` 노출 차단 가드
  - [`.maestro/run_parallel_tests.sh`](file:///Users/kakao/sideproject/app/GoStopCalculator/.maestro/run_parallel_tests.sh): 부팅된 Android 에뮬레이터 및 iOS 시뮬레이터를 모두 감지해 `appId`를 자동 매핑하고 워커별로 병렬 분산 실행하는 스크립트 (`INCLUDE_PHYSICAL=1` 설정 시 USB 실기기 포함)
  - [`.maestro/i18n_audit.py`](file:///Users/kakao/sideproject/app/GoStopCalculator/.maestro/i18n_audit.py): `strings.xml`(ko/en/ja/default) 누락·포맷·미번역 및 UI 코드 내 하드코딩 한글 정적 감사
  - [`.maestro/i18n_screen_crawl.py`](file:///Users/kakao/sideproject/app/GoStopCalculator/.maestro/i18n_screen_crawl.py): 3개 언어(`en-US`, `ja-JP`, `ko-KR`)로 13개 화면을 자동 순회하며 View Hierarchy 기반 깨진 문자열/미번역 한글을 전수 검증하는 런타임 크롤러
  - [`.maestro/launch_emulators.sh`](file:///Users/kakao/sideproject/app/GoStopCalculator/.maestro/launch_emulators.sh): Android 에뮬레이터(5554, 5556) 및 iOS 시뮬레이터를 한 번에 부팅하는 헬퍼 스크립트

---

## 2. 전체 시나리오 매트릭스 (총 7종 + 상시 다국어 검증)

| 번호 | 시나리오 파일 | 검증 내용 |
| :--- | :--- | :--- |
| **01** | `01_happy_path_3players.yaml` | 3인 게임 생성 ➔ 규칙 설정 ➔ 1라운드 점수 계산(승자/패자) ➔ 보드판 반영 풀코스 |
| **02** | `02_player_validation_and_edit.yaml` | 2인 미만일 때 '다음' 버튼 비활성화 및 화면 복귀 유효성 검증 |
| **03** | `03_four_players_selling_flow.yaml` | **4인 게임 필수 흐름**: 광팔기(`SellingScreen`) 장수 입력 및 승패 정산 검증 |
| **04** | `04_multiple_penalty_options.yaml` | **복합 배수**: 첫 뻑 + 피 박 복합 옵션 적용 및 보드 반영 검증 |
| **05** | `05_manual_guide_dialog.yaml` | 점수 계산 중 '고스톱 설명서' 전체화면 다이얼로그 호출 및 닫기 검증 |
| **06** | `06_multi_round_and_settlement.yaml` | 라운드 완료 후 **최종 정산내역(3명 개별 행 및 금액)** 및 **라운드 상세보기** 검증 |
| **07** | `07_history_resume_and_delete.yaml` | 홈 화면 나갔다 복귀 시 **기존 게임 이어하기(복원)** 및 상태 유지 검증 |
| **i18n** | `i18n_audit.py` + `i18n_screen_crawl.py` | **정적 + 13개 화면 런타임 크롤링**: 영어(`en-US`), 일본어(`ja-JP`), 한국어(`ko-KR`) 전 화면 문구 깨짐·미번역 전수 검증 |

---

## 3. 다국어(i18n) 정적 감사 & 13개 화면 크롤링

```bash
# 1) 리소스 키·포맷 지정자·하드코딩 한글 정적 감사
python3 .maestro/i18n_audit.py

# 2) 13개 화면 × 3개 언어(en-US, ja-JP, ko-KR) 런타임 크롤링 (리포트: .maestro/reports/i18n_runtime_report.md)
python3 .maestro/i18n_screen_crawl.py --device emulator-5554 --device emulator-5556
```

---

## 4. 실행 방법

### ① Android 에뮬레이터 + iOS 시뮬레이터 기동
```bash
./.maestro/launch_emulators.sh
```

### ② 최신 앱 빌드 및 설치
```bash
# Android
./gradlew :app:assembleDebug
adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk
adb -s emulator-5556 install -r app/build/outputs/apk/debug/app-debug.apk

# iOS Simulator (부팅된 iPhone 시뮬레이터 UDID 확인 후)
SIM_UDID=$(xcrun simctl list devices | grep "(Booted)" | grep -E -o '[0-9A-F-]{36}' | head -n 1)
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination "id=$SIM_UDID" -derivedDataPath build/ios build
xcrun simctl install "$SIM_UDID" build/ios/Build/Products/Debug-iphonesimulator/iosApp.app
```

### ③ 7개 시나리오 병렬 분산 실행 🚀
```bash
./.maestro/run_parallel_tests.sh
```
- 부팅된 Android 에뮬레이터(`emulator-5554`, `emulator-5556`)와 iOS 시뮬레이터에 7개 시나리오가 자동 샤딩되어 **동시에 병렬 실행**됩니다.
- 각 기기별 로그는 `.maestro/logs/`에 저장되며, 터미널에 요약 결과가 출력됩니다.

---

## 5. GitHub Actions CI 자동화 ([`.github/workflows/maestro-e2e.yml`](.github/workflows/maestro-e2e.yml))

`main` 브랜치 푸시·PR 및 수동 실행(`workflow_dispatch`) 시 아래 3개 Job이 자동으로 실행됩니다.

1. **`i18n-static-audit`** (`ubuntu-latest`): `python3 .maestro/i18n_audit.py` 정적 감사 (약 10초)
2. **`android-maestro-e2e`** (`ubuntu-latest` + KVM): Android API 34 (`pixel_6`, `x86_64`) 에뮬레이터에서 7개 시나리오 및 `i18n_screen_crawl.py` (13화면 × 3개 언어) 실행
3. **`ios-maestro-e2e`** (`macos-latest` Apple Silicon): `iosSimulatorArm64` KMP 프레임워크 빌드 후 한국어(`ko-KR`) iPhone 시뮬레이터에서 7개 시나리오 실행 (`run_ios` 옵션으로 수동 제외 가능)

- 실행 결과 로그, 런타임 리포트 및 실패 스크린샷은 `maestro-android-results` / `maestro-ios-results` GitHub Artifact로 14일간 보관됩니다.
