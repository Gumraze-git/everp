// 랜덤한 문자열을 Base 64 URL-safe 형태로 생성하는 함수
export function generateRandomBase64Url(length: number): string {
  // 지정한 길이만큼 난수 배열 생성
  const array = new Uint8Array(length);

  // web crypto를 활용하여 난수값 채움
  crypto.getRandomValues(array);

  // 난수 배열 문자열로 변환 후 base64 인코딩
  return btoa(String.fromCharCode(...array)) // 각 바이트 값을 문자로 변환 후 Base64로 인코딩 (btoa)
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '');
}

// pkce에서 code_challenge를 생성하기 위한 함수
export async function createCodeChallenge(verifier: string): Promise<string> {
  // verifier 문자열을 utf-8 바이트 배열로 인코딩
  const data = new TextEncoder().encode(verifier);

  // SHA-256 알고리즘으로 해시 생성
  const digest = await crypto.subtle.digest('SHA-256', data);

  // 해시 결과 바이트 배열로 변환 후 문자열로 변환
  const hash = String.fromCharCode(...new Uint8Array(digest));

  // 해시를 base64로 인코딩 후 URL-safe 형태로 변환
  return btoa(hash).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
}
