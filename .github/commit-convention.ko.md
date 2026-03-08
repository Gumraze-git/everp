# 커밋 메시지 규칙

이 저장소의 신규 커밋 메시지는 아래 Conventional Commit 형식을 따라야 합니다.

```text
type(scope): subject (#issue)
```

예시:

```text
feat(frontend): add warehouse filter panel (#14)
fix(gw): relay downstream problem detail (#21)
refac(repo): standardize issue and pr templates (#3)
```

## 규칙
- `type`은 `feat`, `fix`, `refac`, `test`, `chore`, `docs`만 사용합니다.
- `scope`는 선택이지만 가능한 한 작성합니다.
- `scope`는 모듈 단위 이름을 권장합니다.
  - 예: `hrm`, `sd`, `pp`, `dashboard`, `mm`, `iv`, `im`, `gw`, `frontend`, `repo`
- `subject`는 영어 소문자로 작성합니다.
- `subject`는 간결하게 유지하고 마침표(`.`)로 끝내지 않습니다.
- 모든 신규 커밋에는 관련 GitHub 이슈 번호를 반드시 포함합니다.

## 권장 흐름
1. 이슈를 만든다.
2. 브랜치를 만든다.
3. 커밋 메시지에 이슈 번호를 포함한다.
4. PR 템플릿을 사용해 Pull Request를 만든다.

## 예외
- subtree import 커밋과 정책 도입 이전의 공개 히스토리는 수정하지 않습니다.
- 과거 작업을 정리할 때는 커밋 메시지를 바꾸지 말고, 이슈 본문에 관련 커밋 해시를 기록합니다.

## 잘못된 예시

```text
Fix: update api
feat(GW): Add new route
refac(sd): align quotation routes
#12 feat(sd): align quotation routes
```
