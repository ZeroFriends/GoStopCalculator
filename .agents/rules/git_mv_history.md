# Git File Move and History Preservation Rule

## Rule
- 파일 삭제나 경로 이동 시 절대로 `rm` 명령어를 사용하지 않는다.
- 파일 이동 및 리팩토링 시 반드시 `git mv` 또는 `mv`를 사용하여 파일의 변경 이력(Git Commit History)이 유실되지 않고 추적되도록 유지한다.
