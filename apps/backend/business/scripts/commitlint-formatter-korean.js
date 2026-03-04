// 커스텀 커밋lint 포매터 (한국어)
// 사용법: commitlint --format ./scripts/commitlint-formatter-korean.js

const RULE_HINTS = {
  'header-max-length': '헤더(첫 줄)는 72자 이하여야 합니다.',
  'type-case': 'type은 소문자여야 합니다. 예) feat, fix, docs',
  'scope-case': 'scope는 소문자/숫자/하이픈/언더스코어만 허용합니다.',
  'subject-max-length': '제목(subject)은 50자 이하여야 합니다.',
  'subject-full-stop': '제목(subject) 끝에 마침표(.)를 사용하지 않습니다.',
  'subject-case': '제목(subject)은 소문자여야 합니다.',
  'type-enum': 'type은 feat|fix|refac|test|chore|docs 중 하나여야 합니다.',
};

function formatIssues(issues = []) {
  if (!issues.length) return '';
  return issues
    .map((i, idx) => {
      const name = i.name || i.rule || '규칙 위반';
      const hint = RULE_HINTS[name];
      const detail = i.message || '';
      const msg = hint ? `- [${name}] ${hint}\n    └ 상세: ${detail}` : `- [${name}] ${detail}`;
      return `${idx + 1}. ${msg}`;
    })
    .join('\n');
}

module.exports = (report) => {
  try {
    const results = report && report.results ? report.results : [];
    const result = results[0] || {};
    const { valid, errors = [], warnings = [], input = '' } = result;

    if (valid) {
      return `✅ 커밋 메시지 검증 통과\n`;
    }

    const lines = [];
    lines.push('❌ 커밋 메시지 규칙을 위반했습니다.');
    if (input) {
      lines.push(`입력된 메시지: "${input}"`);
    }
    if (errors.length) {
      lines.push('\n[오류] 다음 항목을 수정해주세요:');
      lines.push(formatIssues(errors));
    }
    if (warnings.length) {
      lines.push('\n[경고] 참고하세요:');
      lines.push(formatIssues(warnings));
    }

    lines.push('\n형식 가이드:');
    lines.push('  type(scope): subject (#123)');
    lines.push('예시:');
    lines.push('  feat(auth): 로그인 실패 처리 추가 (#456)');

    return lines.join('\n') + '\n';
  } catch (e) {
    return `❌ 커밋 메시지 포맷터 오류: ${e.message}\n`;
  }
};

