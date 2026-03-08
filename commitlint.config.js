module.exports = {
  extends: ['@commitlint/config-conventional'],
  parserPreset: {
    parserOpts: {
      headerPattern:
        /^(feat|fix|refac|test|chore|docs)(?:\(([a-z0-9_-]+)\))?: ([a-z0-9][a-z0-9 _.,:()\[\]-]*) \(#\d+\)$/u,
      headerPatternFlags: 'u',
      headerCorrespondence: ['type', 'scope', 'subject'],
    },
  },
  rules: {
    'header-max-length': [2, 'always', 72],
    'header-case': [2, 'always', 'lower-case'],
    'type-case': [2, 'always', 'lower-case'],
    'scope-case': [2, 'always', 'lower-case'],
    'subject-case': [2, 'always', ['lower-case']],
    'subject-max-length': [2, 'always', 50],
    'subject-full-stop': [2, 'never', '.'],
    'type-enum': [2, 'always', ['feat', 'fix', 'refac', 'test', 'chore', 'docs']],
  },
};
