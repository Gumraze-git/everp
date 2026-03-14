import { generateRandomBase64Url, createCodeChallenge } from './pkce';
import { getAuthBaseUrl, getCurrentReturnTo, getOauthClientId, getOauthRedirectUri } from './config';

let authorizationRequested = false;

export async function startAuthorization(returnTo?: string) {
  if (typeof window === 'undefined' || authorizationRequested) {
    return;
  }

  authorizationRequested = true;

  const authUrl = getAuthBaseUrl();
  const redirectUri = getOauthRedirectUri();
  const clientId = getOauthClientId();

  const codeVerifier = generateRandomBase64Url(32);
  const codeChallenge = await createCodeChallenge(codeVerifier);
  const state = generateRandomBase64Url(16);
  const nextReturnTo = returnTo ?? getCurrentReturnTo();

  localStorage.setItem('pkce_verifier', codeVerifier);
  localStorage.setItem('oauth_state', state);
  localStorage.setItem('oauth_return_to', nextReturnTo);

  const params = new URLSearchParams({
    response_type: 'code',
    client_id: clientId,
    redirect_uri: redirectUri,
    scope: 'erp.user.profile offline_access',
    state,
    code_challenge: codeChallenge,
    code_challenge_method: 'S256',
  });

  window.location.href = `${authUrl}/oauth2/authorize?${params.toString()}`;
}
