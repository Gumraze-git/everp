import { generateRandomBase64Url, createCodeChallenge } from './pkce';

export async function startAuthorization(returnTo?: string) {
  const AUTH_URL = 'https://auth.everp.co.kr';
  // const REDIRECT_URI = 'http://localhost:3000/callback'; // 로컬용
  const REDIRECT_URI = 'https://everp.co.kr/callback'; // 배포용

  const codeVerifier = generateRandomBase64Url(32);
  const codeChallenge = await createCodeChallenge(codeVerifier);
  const state = generateRandomBase64Url(16);
  console.log('state 생성', state);

  localStorage.setItem('pkce_verifier', codeVerifier);
  localStorage.setItem('oauth_state', state);
  if (returnTo) localStorage.setItem('oauth_return_to', returnTo);

  const params = new URLSearchParams({
    response_type: 'code',
    client_id: 'everp', // vercel
    // client_id: 'everp-spa', // local
    redirect_uri: REDIRECT_URI,
    scope: 'erp.user.profile offline_access',
    state,
    code_challenge: codeChallenge,
    code_challenge_method: 'S256',
  });

  window.location.href = `${AUTH_URL}/oauth2/authorize?${params.toString()}`;
}
