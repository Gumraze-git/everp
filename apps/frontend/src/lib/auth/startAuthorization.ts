import { generateRandomBase64Url, createCodeChallenge } from './pkce';

export async function startAuthorization(returnTo?: string) {
  const AUTH_URL = process.env.NEXT_PUBLIC_AUTH_BASE_URL?.trim() || 'http://localhost:18081';
  const REDIRECT_URI =
    process.env.NEXT_PUBLIC_OAUTH_REDIRECT_URI?.trim() || 'http://localhost:13000/callback';
  const CLIENT_ID = process.env.NEXT_PUBLIC_OAUTH_CLIENT_ID?.trim() || 'everp-spa';

  const codeVerifier = generateRandomBase64Url(32);
  const codeChallenge = await createCodeChallenge(codeVerifier);
  const state = generateRandomBase64Url(16);
  console.log('state 생성', state);

  localStorage.setItem('pkce_verifier', codeVerifier);
  localStorage.setItem('oauth_state', state);
  if (returnTo) localStorage.setItem('oauth_return_to', returnTo);

  const params = new URLSearchParams({
    response_type: 'code',
    client_id: CLIENT_ID,
    redirect_uri: REDIRECT_URI,
    scope: 'erp.user.profile offline_access',
    state,
    code_challenge: codeChallenge,
    code_challenge_method: 'S256',
  });

  window.location.href = `${AUTH_URL}/oauth2/authorize?${params.toString()}`;
}
