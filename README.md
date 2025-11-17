# GoStopCalculator

![Build and Test](https://github.com/YOUR_USERNAME/GoStopCalculator/actions/workflows/main.yml/badge.svg)

고스톱 금액 계산기

[GooglePlay](https://play.google.com/store/apps/details?id=zero.friends.gostopcalculator)

## 🧪 테스트

이 프로젝트는 포괄적인 테스트 커버리지를 가지고 있습니다:

- **총 113개 테스트** ✅ (100% 통과)
- **Domain 모듈**: 113개 테스트
  - 광팜 계산: 5개
  - 패자 계산: 11개 (고박 포함)
  - 점수옵션: 11개 (뻑, 따닥)
  - 통합 시나리오: 7개
  - 엣지 케이스: 15개
  - 특정 케이스: 8개
  - 포괄적 검증: 49개 (5명 케이스 제거)
  - **통합 테스트: 7개** ⭐ NEW (Mock Repository)
- **자동화**: GitHub Actions를 통한 CI/CD
- **커밋마다 자동 테스트 실행**

### 테스트 실행
```bash
# 전체 테스트 (113개)
./gradlew test

# Domain 모듈만 (113개)
./gradlew :domain:test

# 통합 테스트만
./gradlew :domain:test --tests "CalculateGameResultUseCaseTest"
```

자세한 내용은 [테스트 요약](TEST_SUMMARY.md), [룰 명확화](RULES_CLARIFICATION.md) 및 [워크플로우 문서](.github/workflows/README.md)를 참고하세요.
