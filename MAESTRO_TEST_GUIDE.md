# Maestro E2E 시나리오 테스트 & 다중 에뮬레이터 병렬 실행 가이드

본 프로젝트(`GoStopCalculator`)의 화면별 시나리오 검증 및 멀티 디바이스 병렬 테스트 환경 가이드입니다.

---

## 1. 환경 구성 요약

- **프레임워크**: [Maestro](https://maestro.mobile.dev) `v2.12.1` 설치 완료
- **MCP 연동**: `~/.gemini/config/mcp_config.json`에 `maestro mcp` 도구 등록 완료
  - AI 에이전트가 에뮬레이터 화면을 직접 보고 조작하며 테스트 작성/검증 가능
- **디렉토리 구조**:
  - [`.maestro/scenarios/`](file:///Users/kakao/sideproject/app/GoStopCalculator/.maestro/scenarios): 화면별 E2E 시나리오 YAML 파일
  - [`.maestro/run_parallel_tests.sh`](file:///Users/kakao/sideproject/app/GoStopCalculator/.maestro/run_parallel_tests.sh): 연결된 모든 기기(Android/iOS)를 감지하여 시나리오를 병렬로 분산 실행하는 스크립트
  - [`.maestro/launch_emulators.sh`](file:///Users/kakao/sideproject/app/GoStopCalculator/.maestro/launch_emulators.sh): 여러 대의 에뮬레이터를 서로 다른 포트로 한 번에 띄우는 헬퍼 스크립트

---

## 2. 기본 사용법

### ① 에뮬레이터 여러 대 동시에 띄우기
```bash
./.maestro/launch_emulators.sh
```
또는 수동으로 서로 다른 포트 번호를 지정하여 실행:
```bash
~/Library/Android/sdk/emulator/emulator -avd Pixel_3a_API_34_extension_level_7_arm64-v8a -port 5554 &
~/Library/Android/sdk/emulator/emulator -avd 13.5_Freeform_API_34 -port 5556 &
```

부팅 확인:
```bash
adb devices
# List of devices attached
# emulator-5554    device
# emulator-5556    device
```

### ② 디바이스들에 최신 앱 빌드 및 설치
```bash
# Debug APK 빌드
./gradlew :app:assembleDebug

# 5554 및 5556에 동시 설치
adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk
adb -s emulator-5556 install -r app/build/outputs/apk/debug/app-debug.apk
```

### ③ 병렬 시나리오 테스트 일괄 실행 🚀
```bash
./.maestro/run_parallel_tests.sh
```
- 감지된 기기 수(N대)에 맞춰 등록된 시나리오들을 라운드로빈 방식으로 병렬 분산 실행합니다.
- 각 기기별 실행 로그는 `.maestro/logs/`에 저장되며, 터미널에 실시간 성공/실패 여부가 출력됩니다.

---

## 3. 작성된 시나리오 목록

1. **`01_happy_path_3players.yaml`**:
   - 앱 실행 ➔ 히스토리 메인 진입
   - 3인 플레이어 추가 ➔ 다음
   - 게임 규칙 설정 ➔ 완료
   - 게임 보드 진입 ➔ 판 시작 ➔ 승자 선택 ➔ 점수 기록 ➔ 패자 기록 ➔ 보드에 1라운드 점수 정상 반영 확인
2. **`02_player_validation_and_edit.yaml`**:
   - 플레이어 2인 미만일 때 다음 버튼 비활성화 검증
   - 플레이어 추가 후 뒤로가기 동작 검증

---

## 4. Maestro Studio로 새 시나리오 쉽게 만들기

```bash
maestro studio
```
- 브라우저에 실시간 에뮬레이터 화면이 표시됩니다.
- 원하는 요소를 마우스로 클릭하면 **Maestro YAML 코드가 즉시 생성**되므로 복사하여 `.maestro/scenarios/`에 새 파일로 추가하시면 됩니다.

---

## 5. iOS 시뮬레이터와 동시 테스트하는 방법

1. iOS용 idb-companion 설치:
   ```bash
   brew tap facebook/fb
   brew install idb-companion
   ```
2. iOS 시뮬레이터 부팅 (`open -a Simulator`)
3. `./.maestro/run_parallel_tests.sh`를 실행하면 Android 에뮬레이터와 iOS 시뮬레이터가 모두 자동 감지되어 동시에 병렬로 테스트가 진행됩니다!
