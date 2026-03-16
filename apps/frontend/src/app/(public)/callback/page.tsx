'use client';

import { useEffect, useState } from 'react';
import axios from 'axios';
import { startAuthorization } from '@/lib/auth/startAuthorization';
import { USER_ENDPOINTS } from '@/app/types/api';
import { useAuthStore } from '@/store/authStore';
import Cookies from 'js-cookie';
import { getOauthClientId, getOauthRedirectUri } from '@/lib/auth/config';
import { buildAuthXsrfHeaders } from '@/lib/auth/csrf';

function cleanupPkce() {
  localStorage.removeItem('pkce_verifier');
  localStorage.removeItem('oauth_state');
}

export default function CallbackPage() {
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const clearAuth = useAuthStore((state) => state.clearAuth);
  const setAuthStatus = useAuthStore((state) => state.setAuthStatus);

  useEffect(() => {
    const returnTo = localStorage.getItem('oauth_return_to') || '/';

    (async () => {
      try {
        const q = new URLSearchParams(window.location.search);
        const code = q.get('code');
        const state = q.get('state');
        const expected = localStorage.getItem('oauth_state');

        if (!code || !state || !expected || state !== expected) {
          cleanupPkce();
          throw new Error('Invalid state or code');
        }

        const verifier = localStorage.getItem('pkce_verifier');
        if (!verifier) throw new Error('Missing PKCE verifier');

        const body = new URLSearchParams({
          grant_type: 'authorization_code',
          client_id: getOauthClientId(),
          redirect_uri: getOauthRedirectUri(),
          code,
          code_verifier: verifier,
        });

        const headers = await buildAuthXsrfHeaders();

        await axios.post(USER_ENDPOINTS.LOGIN, body.toString(), {
          headers,
          withCredentials: true,
        });
        cleanupPkce();
        localStorage.removeItem('oauth_return_to');
        localStorage.removeItem('oauth_state');
        Cookies.remove('role', { path: '/' });
        clearAuth();
        setAuthStatus('checking');

        window.location.replace(new URL(returnTo, window.location.origin).toString());
      } catch (error: unknown) {
        let errMessage = 'token_exchange_failed';
        if (axios.isAxiosError(error)) {
          errMessage =
            error.response?.data?.error ||
            error.response?.data?.message ||
            error.message ||
            'token_exchange_failed';
        } else if (error instanceof Error) {
          errMessage = error.message;
        }

        cleanupPkce();
        Cookies.remove('role', { path: '/' });
        clearAuth();

        if (errMessage === 'Invalid state or code' || errMessage === 'Missing PKCE verifier') {
          setAuthStatus('redirecting');
          startAuthorization(returnTo);
          return;
        }

        if (errMessage === 'invalid_grant') {
          setAuthStatus('redirecting');
          startAuthorization(returnTo);
          return;
        }

        setErrorMessage(errMessage);
      }
    })();
  }, [clearAuth, setAuthStatus]);

  if (errorMessage) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-white via-slate-50 to-slate-100 flex items-center justify-center px-4">
        <div className="w-full max-w-md rounded-3xl border border-red-200 bg-white shadow-xl shadow-slate-200/60 px-10 py-12 text-center space-y-4">
          <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-red-50 text-red-500">
            <i className="ri-error-warning-line text-3xl" />
          </div>
          <div className="space-y-2">
            <h1 className="text-2xl font-semibold text-slate-900">로그인 처리에 실패했어요</h1>
            <p className="text-sm leading-relaxed text-slate-600">
              인증 응답을 처리하는 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.
            </p>
          </div>
          <p className="rounded-2xl border border-red-100 bg-red-50 px-4 py-3 text-left text-sm text-red-600">
            {errorMessage}
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-white via-slate-50 to-slate-100 flex items-center justify-center px-4">
      <div className="w-full max-w-md rounded-3xl border border-slate-200 bg-white shadow-xl shadow-slate-200/60 px-10 py-12 text-center space-y-6">
        <div
          className="mx-auto h-16 w-16 rounded-full border-4 border-t-transparent border-red-400 animate-spin"
          aria-hidden
        />
        <div className="space-y-3">
          <h1 className="text-2xl font-semibold text-slate-900">로그인 정보를 확인하고 있어요</h1>
          <p className="text-sm leading-relaxed text-slate-600">
            계정 권한과 세션을 확인한 뒤 원래 화면으로 이동합니다. 잠시만 기다려 주세요.
          </p>
        </div>
        <div className="rounded-2xl border border-slate-200 bg-slate-50 px-6 py-5 text-left text-sm text-slate-600 space-y-2">
          <p className="font-medium text-slate-900">진행 중인 작업</p>
          <ul className="space-y-1">
            <li className="flex items-start gap-2">
              <span className="mt-1 text-red-400">•</span>
              <span>인증 서버에서 토큰을 교환하고 있어요.</span>
            </li>
            <li className="flex items-start gap-2">
              <span className="mt-1 text-red-400">•</span>
              <span>브라우저 쿠키를 정리한 뒤 원래 화면으로 돌아갑니다.</span>
            </li>
            <li className="flex items-start gap-2">
              <span className="mt-1 text-red-400">•</span>
              <span>준비가 완료되면 자동으로 이전 페이지로 돌아갑니다.</span>
            </li>
          </ul>
        </div>
        <p className="text-xs text-slate-400" role="status" aria-live="polite">
          화면이 오래 머물러 있으면 새로고침하거나 관리자에게 문의해 주세요.
        </p>
      </div>
    </div>
  );
}
