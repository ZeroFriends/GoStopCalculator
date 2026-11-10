# Maestro E2E 시나리오 테스트 & 다중 에뮬레이터 병렬 실행 가이드

본 프로젝트(`GoStopCalculator`)의 화면별 시나리오 검증, 다국어(i18n) 체크, 멀티 디바이스 병렬 테스트 환경 가이드입니다.

---

## 1. 환경 구성 요약

- **프레임워크**: [Maestro](https://maestro.mobile.dev) `v2.12.1` 설치 완료
- **MCP 연동**: `~/.gemini/config/mcp_config.json`에 `maestro mcp` 등록 완료
- **디렉토리 구조**:
  - [`.maestro/scenarios/`](file:///Users/kakao/sideproject/app/GoStopCalculator/.maestro/scenarios): 화면별/기능별 E2E 시나리오 YAML 파일 (총 8종)
  - [`.maestro/run_parallel_tests.sh`](file:///Users/kakao/sideproject/app/GoStopCalculator/.maestro/run_parallel_tests.sh): 연결된 모든 기기(Android/iOS)를 감지하여 시나리오를 병렬로 분산 실행하는 스크립트
  - [`.maestro/launch_emulators.sh`](file:///Users/kakao/sideproject/app/GoStopCalculator/.maestro/launch_emulators.sh): 여러 대의 에뮬레이터를 서로 다른 포트로 한 번에 띄우는 헬퍼 스크립트

---

## 2. 전체 시나리오 매트릭스 (총 8종)

| 번호 | 시나리오 파일 | 검증 내용 |
| :--- | :--- | :--- |
| **01** | `01_happy_path_3players.yaml` | 3인 게임 생성 ➔ 규칙 설정 ➔ 1라운드 점수 계산(승자/패자) ➔ 보드판 반영 풀코스 |
| **02** | `02_player_validation_and_edit.yaml` | 2인 미만일 때 '다음' 버튼 비활성화 및 화면 복귀 유효성 검증 |
| **03** | `03_four_players_selling_flow.yaml` | **4인 게임 필수 흐름**: 광팔기(SellingScreen) 및 장수 입력/정산 검증 |
| **04** | `04_multiple_penalty_options.yaml` | **복합 배수**: 첫 뻑 + 피 박 + 광 박 동시 체크 및 패널티 계산 검증 |
| **05** | `05_manual_guide_dialog.yaml` | 점수 계산 중 '고스톱 설명서' 전체화면 다이얼로그 호출 및 닫기 검증 |
| **06** | `06_multi_round_and_settlement.yaml` | 2라운드 이상 진행 후 **최종 정산(Calculate)** 및 **라운드 상세보기** 검증 |
| **07** | `07_history_resume_and_delete.yaml` | 홈 화면 나갔다 복귀 시 **기존 게임 이어하기(복원)** 및 상태 유지 검증 |
| **08** | `08_multilingual_i18n_check.yaml` | **다국어 검증**: 영어(`en-US`), 일본어(`ja-JP`), 한국어(`ko-KR`) 로케일 전환 및 텍스트 렌더링 검증 |

---

## 3. 다국어(i18n) 테스트 원리

Maestro는 `setLocale` 커맨드를 기본 지원하므로, 에뮬레이터 설정을 수동으로 바꾸지 않고도 **스크립트 실행 중 자동으로 언어를 전환**할 수 있습니다.

```yaml
# 영어로 변경 후 렌더링 확인
- setLocale: "en-US"
- launchApp:
    clearState: true
- assertVisible: "Start"
- assertVisible: "Today's Game.*"

# 일본어로 변경 후 렌더링 확인
- setLocale: "ja-JP"
- launchApp:
    clearState: true
- assertVisible: "始める"
- assertVisible: "本日のゲーム.*"

# 한국어로 복귀
- setLocale: "ko-KR"
- launchApp:
    clearState: true
- assertVisible: "시작하기"
```

---

## 4. 실행 방법

### ① 에뮬레이터 2대 동시 기동
```bash
./.maestro/launch_emulators.sh
```

### ② 최신 앱 빌드 및 설치
```bash
./gradlew :app:assembleDebug
adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk
adb -s emulator-5556 install -r app/build/outputs/apk/debug/app-debug.apk
```

### ③ 8개 시나리오 병렬 분산 실행 🚀
```bash
./.maestro/run_parallel_tests.sh
```
- 에뮬레이터 2대에 8개의 시나리오가 4개씩 분산되어 **동시에 병렬 실행**됩니다.
- 각 기기별 로그는 `.maestro/logs/`에 저장되며, 터미널에 요약 결과가 출력됩니다.
