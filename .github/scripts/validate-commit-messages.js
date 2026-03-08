#!/usr/bin/env node

const { execFileSync } = require('node:child_process');

const commits = process.argv.slice(2).filter(Boolean);

if (commits.length === 0) {
  console.log('No commits to validate.');
  process.exit(0);
}

function git(args) {
  return execFileSync('git', args, { encoding: 'utf8' }).trimEnd();
}

function isLegacyException(subject, parentLine) {
  const parentCount = parentLine ? parentLine.split(' ').length - 1 : 0;

  return (
    parentCount > 1 ||
    subject.startsWith('Merge ') ||
    (subject.startsWith("Add '") && subject.includes("from commit '"))
  );
}

function validateBody(sha, subject, message) {
  const lines = message.split('\n');

  if (lines.length < 5) {
    return `${sha}: 본문은 빈 줄과 3~5개의 한국어 bullet이 필요합니다.`;
  }

  if (lines[1] !== '') {
    return `${sha}: 제목과 본문 사이에 빈 줄이 필요합니다.`;
  }

  const contentLines = lines.slice(2).filter((line) => line.trim() !== '');

  if (contentLines.length < 3 || contentLines.length > 5) {
    return `${sha}: 본문 bullet은 3~5개여야 합니다.`;
  }

  for (const line of contentLines) {
    if (!/^- .+/u.test(line)) {
      return `${sha}: 본문은 bullet 형식('- ')만 사용할 수 있습니다.`;
    }

    if (!/[가-힣]/u.test(line)) {
      return `${sha}: 각 bullet은 한국어 설명을 포함해야 합니다.`;
    }
  }

  if (/\(#\d+\)/u.test(subject)) {
    return `${sha}: 제목 뒤에 이슈 번호를 붙일 수 없습니다.`;
  }

  if (!/[가-힣]/u.test(subject)) {
    return `${sha}: 제목은 한국어를 포함해야 합니다.`;
  }

  return null;
}

const errors = [];

for (const sha of commits) {
  const message = git(['show', '-s', '--format=%B', sha]);
  const subject = message.split('\n')[0] ?? '';
  const parentLine = git(['rev-list', '--parents', '-n', '1', sha]);

  if (isLegacyException(subject, parentLine)) {
    continue;
  }

  const error = validateBody(sha, subject, message);

  if (error) {
    errors.push(error);
  }
}

if (errors.length > 0) {
  console.error('Commit message validation failed:');
  for (const error of errors) {
    console.error(`- ${error}`);
  }
  process.exit(1);
}

console.log(`Validated ${commits.length} commit message(s).`);
