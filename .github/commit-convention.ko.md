# 커밋 메시지 규칙

이 저장소의 신규 커밋 메시지는 아래 Conventional Commit 형식을 따라야 합니다.

```text
type(scope): 한국어 제목

- 한국어 변경 요약 1
- 한국어 변경 요약 2
- 한국어 변경 요약 3
```

예시:

```text
refac(sd): 견적 경로 정리

- business와 gw 공개 경로를 리소스 중심으로 정리했습니다.
- 상태 기반 목록 조회를 query parameter 방식으로 통일했습니다.
- 기존 RPC 스타일 경로를 제거하고 관련 문서를 갱신했습니다.
```

## 규칙
- `type`은 `feat`, `fix`, `refac`, `test`, `chore`, `docs`만 사용합니다.
- `scope`는 선택이지만 가능한 한 작성합니다.
- `scope`는 모듈 단위 이름을 권장합니다.
  - 예: `hrm`, `sd`, `pp`, `dashboard`, `mm`, `iv`, `im`, `gw`, `frontend`, `repo`
- `subject`는 한국어로 작성합니다.
- `subject`는 한 줄 요약만 작성하고 마침표(`.`)로 끝내지 않습니다.
- 제목 뒤에 `(#12)` 같은 이슈 번호를 붙이지 않습니다.
- 제목 아래에는 빈 줄을 넣고, 한국어 bullet 본문을 `3~5개` 작성합니다.
- 일반 커밋의 본문은 bullet 외의 자유 서술형 문장을 두지 않습니다.

## 권장 흐름
1. 이슈를 만든다.
2. 브랜치를 만든다.
3. 커밋 제목과 본문을 규칙에 맞춰 작성한다.
4. PR 템플릿을 사용해 Pull Request를 만들고 `Closes #...` 또는 `Related #...`로 이슈를 연결한다.

## 예외
- subtree import 커밋과 정책 도입 이전의 공개 히스토리는 수정하지 않습니다.
- merge commit은 일반 커밋 본문 규칙의 예외로 봅니다.
- 과거 작업을 정리할 때는 커밋 메시지를 바꾸지 말고, 이슈 본문에 관련 커밋 해시를 기록합니다.

## 잘못된 예시

```text
Fix: update api
feat(GW): Add new route
refac(sd): align quotation routes
refac(sd): 견적 경로 정리 (#12)
refac(sd): 견적 경로 정리

자유 서술형 문장 한 줄만 작성
```
