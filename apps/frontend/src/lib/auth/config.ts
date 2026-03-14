const DEFAULT_AUTH_BASE_URL = 'http://localhost:18081';
const DEFAULT_REDIRECT_URI = 'http://localhost:13000/callback';
const DEFAULT_CLIENT_ID = 'everp-spa';

export function getAuthBaseUrl() {
  return process.env.NEXT_PUBLIC_AUTH_BASE_URL?.trim() || DEFAULT_AUTH_BASE_URL;
}

export function getOauthRedirectUri() {
  return process.env.NEXT_PUBLIC_OAUTH_REDIRECT_URI?.trim() || DEFAULT_REDIRECT_URI;
}

export function getOauthClientId() {
  return process.env.NEXT_PUBLIC_OAUTH_CLIENT_ID?.trim() || DEFAULT_CLIENT_ID;
}

export function getCurrentReturnTo() {
  if (typeof window === 'undefined') {
    return '/';
  }

  const { pathname, search, hash } = window.location;
  const returnTo = `${pathname}${search}${hash}`;
  return returnTo || '/';
}
