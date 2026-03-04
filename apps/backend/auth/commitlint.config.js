module.exports = {
  extends: ['@commitlint/config-conventional'],
  // 헤더 파싱 규칙을 커스터마이즈하여 "#123 type(scope): subject" 형태를 허용
  parserPreset: {
    parserOpts: {
      // type + optional scope + subject + required trailing issue number in parentheses
      // e.g. "feat(scope): subject (#123)"
      // subject는 소문자/숫자/공백/일부 구두점/한글만 허용(대문자 차단)
      headerPattern: /^(feat|fix|refac|test|chore|docs)(?:\(([a-z0-9_-]+)\))?: ([a-z0-9 가-힣_\-,:()\[\]]+) \(#\d+\)$/u,
      headerPatternFlags: 'u',
      headerCorrespondence: ['type', 'scope', 'subject']
    }
  },
  rules: {
    'header-max-length': [2, 'always', 72],
    // 헤더 전체 대문자 금지(보다 강한 방어선)
    'header-case': [2, 'always', 'lower-case'],
    'type-case': [2, 'always', 'lower-case'],
    'scope-case': [2, 'always', 'lower-case'],
    // 제목(Subject)에 대문자 사용 금지
    'subject-case': [2, 'always', ['lower-case']],
    'subject-max-length': [2, 'always', 50],
    'subject-full-stop': [2, 'never', '.'],
    // 허용 타입을 명시(프로젝트 사용 패턴 반영)
    'type-enum': [2, 'always', ['feat', 'fix', 'refac', 'test', 'chore', 'docs']]
  }
};
