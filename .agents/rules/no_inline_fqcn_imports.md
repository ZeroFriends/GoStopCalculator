# No Inline FQCN Imports Rule

- **규칙**: 코드를 작성하거나 수정할 때, 코드 본문 중간에 패키지 풀 경로(Fully Qualified Class Name, 예: `zero.friends.gostopcalculator.util.AdConfig`)를 인라인으로 직접 표기하지 않는다.
- **정석 방식**: 반드시 파일 상단 `import` 영역에 해당 클래스를 import 하고, 코드 본문 내에서는 클래스 단축명(예: `AdConfig`)만 사용한다.
