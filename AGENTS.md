# Repository Agent Guide

## Workflow
- Follow `Issue -> Branch -> Commit -> Pull Request` for every new change.
- Start new work from a GitHub issue. Use the repository issue templates.
- Open pull requests with the repository PR template. If a section does not apply, write `N/A`.
- Keep each branch and PR scoped to a single issue or a tightly related delivery unit.

## Commit Messages
- All new commit messages must follow `type(scope): subject (#issue)`.
- Allowed types: `feat`, `fix`, `refac`, `test`, `chore`, `docs`.
- `scope` is optional but strongly recommended. Prefer module scopes such as `hrm`, `sd`, `pp`, `dashboard`, `mm`, `iv`, `im`, `gw`, `frontend`, `repo`.
- `subject` must be concise English in lowercase and must not end with a period.
- Every new commit must include the related GitHub issue number.

### Examples
- `refac(sd): align quotation routes (#2)`
- `docs(repo): add monorepo governance guides (#3)`
- `fix(gw): relay problem detail from downstream (#12)`

## Templates and Tracking
- Use the epic issue template for retrospective tracking, multi-step programs, or cross-module work.
- Use the task issue template for a single implementation unit.
- Record commit hashes in issue bodies when you are documenting work that was already published.
- Link the related issue in every pull request.

## Legacy History
- Do not rewrite published subtree import commits or already-pushed history just to satisfy the current convention.
- Treat commits created before this policy as legacy exceptions.
- For retrospective organization, create or update issues and list the relevant commit hashes instead of amending history.

## Rule Precedence
- Root repository governance takes precedence over app-local conventions when they conflict.
- App-local `commitlint` configs should follow the root `commitlint.config.js`.
