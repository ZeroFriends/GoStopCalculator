# 앱 빌드 및 실제 디바이스/시뮬레이터 구동 검증 규칙

## 원칙
안드로이드(.apk) 및 아이폰(iOS) 앱 코드 수정 및 빌드가 완료되면, 단순히 컴파일 성공에 만족하지 않고 **실제 에뮬레이터/시뮬레이터에 앱을 설치 및 launch하여 프로세스가 크래시 없이 100% 실행되는지(PID / log show / logcat)까지 직접 구동 확인**한다.

## 세부 검증 단계
1. **iOS (iPhone Simulator)**:
   - `xcodebuild` 빌드 수행 (`iphonesimulator` 타겟)
   - `xcrun simctl install <device-id> <app-path>` 로 앱 재설치
   - `xcrun simctl launch <device-id> <bundle-id>` 로 앱 시동
   - `xcrun simctl spawn <device-id> log show` 또는 PID 획득을 통해 `Preconditions.kt`, Plist, Skia, DB 생성 예외 없이 정상 RUNNING 상태인지 검증

2. **Android (Emulator / Device)**:
   - `./gradlew :app:assembleDebug` 빌드 수행
   - `adb install -r <apk-path>` 로 앱 재설치
   - `adb shell am start -n <package>/<activity>` 로 앱 시동
   - `adb logcat` 또는 PID 획득을 통해 NPE, ClassNotFound, RuntimeException 없이 정상 RUNNING 상태인지 검증

3. **커밋 제어**:
   - 사용자가 명시적으로 커밋을 요청하거나 지시하기 전까지는 자동 커밋을 수행하지 않는다.
