# Repository Agent Guide

## Workflow
- Follow `Issue -> Branch -> Commit -> Pull Request` for every new change.
- Start new work from a GitHub issue. Use the repository issue templates.
- Title issues with a type prefix such as `[refac]`, `[feat]`, `[fix]`, `[docs]`, `[chore]`, or `[test]`.
- Open pull requests with the repository PR template. If a section does not apply, write `N/A`.
- Keep each branch and PR scoped to a single issue or a tightly related delivery unit.

## Commit Messages
- All new commit messages must follow `type(scope): Korean subject`.
- Allowed types: `feat`, `fix`, `refac`, `test`, `chore`, `docs`.
- `scope` is optional but strongly recommended. Prefer module scopes such as `hrm`, `sd`, `pp`, `dashboard`, `mm`, `iv`, `im`, `gw`, `frontend`, `repo`.
- `subject` must be a concise Korean summary on a single line.
- Do not append `(#issue)` to the commit title.
- Add a blank line after the title, then write `3-5` Korean bullet points in the body.
- Manage issue links in issues and pull requests, not in commit titles.

### Examples
- `refac(sd): 견적 경로 정리`
- `docs(repo): 깃허브 운영 규칙 정비`
- `fix(gw): 하위 서비스 오류 응답 전달 개선`

## Templates and Tracking
- Use the single repository issue template for both new work and retrospective tracking.
- Record commit hashes in issue bodies when you are documenting work that was already published.
- Link the related issue in every pull request.

## Legacy History
- Do not rewrite published subtree import commits or already-pushed history just to satisfy the current convention.
- Treat commits created before this policy as legacy exceptions.
- For retrospective organization, create or update issues and list the relevant commit hashes instead of amending history.

## Rule Precedence
- Root repository governance takes precedence over app-local conventions when they conflict.
- App-local `commitlint` configs should follow the root `commitlint.config.js`.
