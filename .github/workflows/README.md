# GitHub Actions Workflows

이 프로젝트는 단일 GitHub Actions 워크플로우를 사용하여 CI/CD를 자동화합니다.

## 📋 워크플로우

### 🏗️ Build and Test - `main.yml`
**실행 시점**: `main` 브랜치에 푸시할 때만

**실행 내용**:
- 전체 테스트 실행 (113개 테스트)
- **Release APK 빌드**
- 테스트 결과 및 Release APK 업로드 (30일 보관)
- 테스트 요약 자동 생성

**사용 사례**: 프로덕션 배포용 Release APK 생성 및 검증

---

## 🚀 사용 방법

### Main 브랜치에 배포
```bash
# develop에서 작업 완료 후
git checkout main
git merge develop
git push origin main
```
→ GitHub Actions가 자동으로 실행됩니다!

**자동 실행 항목**:
1. ✅ 113개 테스트 자동 실행
2. ✅ Release APK 빌드
3. ✅ 테스트 결과 리포트
4. ✅ Release APK 다운로드 가능

---

## 📊 상태 배지

README.md에 다음 배지를 추가할 수 있습니다:

```markdown
![Build and Test](https://github.com/YOUR_USERNAME/GoStopCalculator/actions/workflows/main.yml/badge.svg)
```

---

## 📦 아티팩트

테스트 및 빌드가 완료되면 다음을 다운로드할 수 있습니다:
- **app-release**: Release APK (30일 보관)
- **test-results**: 테스트 결과 XML 및 HTML 리포트 (30일 보관)

GitHub Actions 페이지에서 워크플로우 실행 내역을 확인하고 아티팩트를 다운로드할 수 있습니다.

---

## ⚙️ 환경 설정

- **Java**: JDK 21 (Temurin)
- **Gradle**: Wrapper 사용
- **캐싱**: Gradle 캐시를 사용하여 빌드 속도 향상
- **테스트**: 모든 모듈의 테스트 자동 실행 (113개)

---

## 🎯 테스트 커버리지

자동으로 검증되는 테스트:
- ✅ **Domain 모듈**: 113개 테스트
  - 광팜 계산: 5개
  - 패자 계산: 11개 (고박 포함)
  - 점수옵션: 11개 (뻑, 따닥)
  - 통합 시나리오: 7개
  - 엣지 케이스: 15개
  - 특정 케이스: 8개
  - 포괄적 검증: 49개
  - Mock Repository 통합 테스트: 7개

---

## 💡 로컬 테스트

배포 전에 로컬에서 테스트를 실행하는 것을 권장합니다:

```bash
# 전체 테스트
./gradlew test

# Release APK 빌드
./gradlew assembleRelease

# 특정 모듈 테스트
./gradlew :domain:test
```

---

## 🔧 워크플로우 수정

워크플로우는 `.github/workflows/main.yml` 파일로 관리됩니다.

수정이 필요한 경우 파일을 직접 편집하고 커밋하면 즉시 반영됩니다.

---

## 📝 간단한 구조

단일 워크플로우로 모든 것을 처리합니다:

```
main 브랜치 푸시
       ↓
  ┌──────────┐
  │ main.yml │
  └──────────┘
       ↓
  ┌─────────────────┐
  │ 1. 테스트 실행   │ (113개)
  │ 2. Release 빌드  │
  │ 3. 아티팩트 업로드│
  └─────────────────┘
       ↓
   ✅ 완료!
```

---

## 🎉 장점

1. **단순함** ✅
   - 하나의 워크플로우만 관리
   - 이해하기 쉬움

2. **명확함** ✅
   - main 브랜치 = Release APK
   - 혼란 없음

3. **효율적** ✅
   - 불필요한 중복 제거
   - 리소스 절약

4. **안정성** ✅
   - main 브랜치에만 적용
   - 113개 테스트로 검증 후 빌드
