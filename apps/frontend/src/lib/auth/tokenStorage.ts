'use client';

import Cookies from 'js-cookie';

const ACCESS_TOKEN_KEY = 'access_token';
const ACCESS_TOKEN_EXP_KEY = 'access_token_expires_at';

const cookieBaseOptions = {
  path: '/',
  sameSite: 'lax' as const,
  secure: process.env.NODE_ENV === 'production',
};

export function persistAccessToken(accessToken: string, expiresInSeconds: number) {
  const expiresAt = Date.now() + expiresInSeconds * 1000;
  const expiresDate = new Date(expiresAt);

  Cookies.set(ACCESS_TOKEN_KEY, accessToken, { ...cookieBaseOptions, expires: expiresDate });
  Cookies.set(ACCESS_TOKEN_EXP_KEY, String(expiresAt), {
    ...cookieBaseOptions,
    expires: expiresDate,
  });

  return expiresAt;
}

export function clearAccessToken() {
  Cookies.remove(ACCESS_TOKEN_KEY, { path: cookieBaseOptions.path });
  Cookies.remove(ACCESS_TOKEN_EXP_KEY, { path: cookieBaseOptions.path });
}

export function readStoredToken() {
  if (typeof document === 'undefined') {
    return { token: null, expiresAt: null };
  }

  const token = Cookies.get(ACCESS_TOKEN_KEY) ?? null;
  const expiresAtRaw = Cookies.get(ACCESS_TOKEN_EXP_KEY) ?? null;
  const expiresAt = expiresAtRaw ? Number(expiresAtRaw) : null;

  return { token, expiresAt };
}
