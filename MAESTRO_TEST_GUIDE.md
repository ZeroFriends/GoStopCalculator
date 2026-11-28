# Maestro E2E 시나리오 테스트 & 다중 에뮬레이터 병렬 실행 가이드

본 프로젝트(`GoStopCalculator`)의 화면별 시나리오 검증, 다국어(i18n) 체크, 멀티 디바이스 병렬 테스트 환경 가이드입니다.

---

## 1. 환경 구성 요약

- **프레임워크**: [Maestro](https://maestro.mobile.dev) `v2.12.1` 설치 완료
- **MCP 연동**: `~/.gemini/config/mcp_config.json`에 `maestro mcp` 등록 완료
- **디렉토리 구조**:
  - [`.maestro/scenarios/`](file:///Users/kakao/sideproject/app/GoStopCalculator/.maestro/scenarios): 화면별/기능별 크로스 플랫폼(Android/iOS) E2E 시나리오 YAML 파일 (총 12종)
  - [`.maestro/common/assert_no_broken_text.yaml`](file:///Users/kakao/sideproject/app/GoStopCalculator/.maestro/common/assert_no_broken_text.yaml): 각 주요 화면 진입 시 미치환 포맷(`%d`, `%1$s`), `null`, `undefined`, `NaN`, 리터럴 `\n` 노출 차단 가드
  - [`.maestro/run_parallel_tests.sh`](file:///Users/kakao/sideproject/app/GoStopCalculator/.maestro/run_parallel_tests.sh): 부팅된 Android 에뮬레이터 및 iOS 시뮬레이터를 모두 감지해 `appId`를 자동 매핑하고 워커별로 병렬 분산 실행하는 스크립트 (`INCLUDE_PHYSICAL=1` 설정 시 USB 실기기 포함)
  - [`.maestro/i18n_audit.py`](file:///Users/kakao/sideproject/app/GoStopCalculator/.maestro/i18n_audit.py): `strings.xml`(ko/en/ja/default) 누락·포맷·미번역 및 UI 코드 내 하드코딩 한글 정적 감사
  - [`.maestro/run_android_ci.sh`](file:///Users/kakao/sideproject/app/GoStopCalculator/.maestro/run_android_ci.sh): CI에서 Debug APK 설치 후 Maestro E2E 스위트를 실행하는 래퍼 스크립트
  - [`.maestro/launch_emulators.sh`](file:///Users/kakao/sideproject/app/GoStopCalculator/.maestro/launch_emulators.sh): Android 에뮬레이터(5554, 5556) 및 iOS 시뮬레이터를 한 번에 부팅하는 헬퍼 스크립트

---

## 2. 전체 시나리오 매트릭스 (총 12종 + 정적 다국어 감사)

| 번호 | 시나리오 파일 | 검증 내용 |
| :--- | :--- | :--- |
| **01** | `01_happy_path_3players.yaml` | 3인 게임 생성 ➔ 규칙 설정 ➔ 1라운드 점수 계산(승자/패자) ➔ 보드판 반영 풀코스 |
| **02** | `02_player_validation_and_edit.yaml` | 2인 미만 '다음' 비활성화, **플레이어 이름 수정(`NameEditDialog`)·삭제**, 규칙 `도움말` 모달 및 복귀 검증 |
| **03** | `03_four_players_selling_flow.yaml` | **4인 게임 필수 흐름**: `광팔 수 있는 패` 안내 모달, 광팔기(`SellingScreen`) 장수·정산 확인 모달 및 `광팔이 · 2장` 결과 검증 |
| **04** | `04_multiple_penalty_options.yaml` | **복합 배수**: 첫 뻑 + 피 박·광 박 복합 옵션 적용 및 보드 반영 검증 |
| **05** | `05_manual_guide_dialog.yaml` | 홈 `Guide` · **`고스톱 룰`(5대 카테고리·48장 월별 패·광팔이 필터)** · 보드 **`룰·화투패`** · 점수기록 **스마트 탭 점프** 통합 검증 |
| **06** | `06_multi_round_and_settlement.yaml` | **다중 라운드(2판, 승자 교대)** 진행 후 **누적 정산내역(`받기`/`보내기`·`공유`)** 및 **라운드 상세보기** 검증 |
| **07** | `07_history_resume_and_delete.yaml` | 홈 화면 나갔다 복귀 시 **기존 게임 이어하기(복원)** 및 3점 버튼(`⋮`) 관리 바텀시트 ➔ 삭제 검증 |
| **08** | `08_gobak_and_go_multiplier_flow.yaml` | **3인 고박 독박·면제 + 3고 배수**: 독박 배너·타 패자 민트 `고박 면제` 뱃지, 확인 모달 `'다시 확인'` 복귀 및 대납/면제 상세 수식 검증 |
| **09** | `09_three_fuck_instant_win.yaml` | **삼연 뻑 즉시 승리 모달**: `'다시 확인'` 시 체크 해제 복귀, `'승리 확정하기'` 시 승자/패자 단계 스킵 및 전원 4배 정산 검증 |
| **10** | `10_two_player_matgo_gobak.yaml` | **2인 맞고 고박**: 2인전 고박 단독 `x2배` 적용(독박/면제 배너 미노출) 및 결과 정산 검증 |
| **11** | `11_shake_and_bomb_multiplier.yaml` | **승자 흔들기·폭탄 배수**: 승자 카드 탭 칩 토글(`총 ×4배`), 확인 모달(`+20점`) 및 상세보기(`2,000원`·칩·송금) E2E 검증 |
| **12** | `12_rename_game_and_round.yaml` | **게임·라운드 이름 변경**: 홈 3점(`⋮`) 바텀시트 이름 변경, 보드 3점 바텀시트 이름 변경·상세보기 반영 및 기본명 초기화 검증 |
| **i18n** | `i18n_audit.py` | **정적 리소스 감사**: `strings.xml`(ko/en/ja/default) 키 누락·포맷 지정자·하드코딩 한글 전수 검증 |

---

## 3. 다국어(i18n) 정적 감사

```bash
# 리소스 키·포맷 지정자·하드코딩 한글 정적 감사 (에뮬레이터 불필요)
python3 .maestro/i18n_audit.py
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

### ③ 12개 시나리오 병렬 분산 실행 🚀
```bash
./.maestro/run_parallel_tests.sh
```
- 부팅된 Android 에뮬레이터(`emulator-5554`, `emulator-5556`)와 iOS 시뮬레이터에 12개 시나리오가 자동 샤딩되어 **동시에 병렬 실행**됩니다.
- 각 기기별 로그는 `.maestro/logs/`에 저장되며, 터미널에 요약 결과가 출력됩니다.

---

## 5. GitHub Actions CI 자동화 ([`.github/workflows/maestro-e2e.yml`](.github/workflows/maestro-e2e.yml))

`main` 브랜치 푸시·PR 및 수동 실행(`workflow_dispatch`) 시 아래 3개 Job이 자동으로 실행됩니다.

1. **`i18n-static-audit`** (`ubuntu-latest`): `python3 .maestro/i18n_audit.py` 정적 감사 (약 10초)
2. **`android-maestro-e2e`** (`ubuntu-latest` + KVM): Android API 34 (`pixel_6`, `x86_64`) 에뮬레이터에서 한국어(`ko-KR`) 시나리오 12종 실행
3. **`ios-maestro-e2e`** (`macos-latest` Apple Silicon): `iosSimulatorArm64` KMP 프레임워크 빌드 후 한국어(`ko-KR`) iPhone 시뮬레이터에서 12개 시나리오 실행 (`run_ios` 옵션으로 수동 제외 가능)

- 실행 결과 로그 및 실패 스크린샷은 `maestro-android-results` / `maestro-ios-results` GitHub Artifact로 14일간 보관됩니다.
