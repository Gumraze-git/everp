'use client';

import { ReactNode, useEffect, useRef } from 'react';
import { trySilentRefresh } from '@/lib/auth/refresh';
import { startAuthorization } from '@/lib/auth/startAuthorization';
import { getUserInfo } from '../(public)/callback/callback.api';
import { useAuthStore } from '@/store/authStore';
import Cookies from 'js-cookie';
import { clearAccessToken, readStoredToken } from '@/lib/auth/tokenStorage';
import { getCurrentReturnTo } from '@/lib/auth/config';

export default function PrivateGuard({ children }: { children: ReactNode }) {
  const authStatus = useAuthStore((state) => state.authStatus);
  const setAuthStatus = useAuthStore((state) => state.setAuthStatus);
  const setAuthenticatedUser = useAuthStore((state) => state.setAuthenticatedUser);
  const clearAuth = useAuthStore((state) => state.clearAuth);
  const bootstrappedRef = useRef(false);

  useEffect(() => {
    if (bootstrappedRef.current) {
      return;
    }

    bootstrappedRef.current = true;
    let cancelled = false;

    const bootstrap = async () => {
      setAuthStatus('checking');

      try {
        const { token, expiresAt } = readStoredToken();

        if (!token || !expiresAt || Date.now() > expiresAt) {
          clearAccessToken();
          await trySilentRefresh();
        }

        const userInfo = await getUserInfo();

        if (cancelled) {
          return;
        }

        setAuthenticatedUser(userInfo);
        Cookies.set('role', userInfo.userRole.toUpperCase(), { path: '/', sameSite: 'lax' });
      } catch {
        if (cancelled) {
          return;
        }

        clearAccessToken();
        Cookies.remove('role', { path: '/' });
        clearAuth();
        setAuthStatus('redirecting');
        startAuthorization(getCurrentReturnTo());
      }
    };

    void bootstrap();

    return () => {
      cancelled = true;
    };
  }, [clearAuth, setAuthenticatedUser, setAuthStatus]);

  if (authStatus !== 'authenticated') {
    return (
      <div className="min-h-screen bg-gradient-to-br from-white via-slate-50 to-slate-100 flex items-center justify-center px-4">
        <div className="w-full max-w-md rounded-3xl border border-slate-200 bg-white shadow-xl shadow-slate-200/60 px-10 py-12 text-center space-y-6">
          <div
            className="mx-auto h-16 w-16 rounded-full border-4 border-t-transparent border-red-400 animate-spin"
            aria-hidden
          />
          <div className="space-y-3">
            <h1 className="text-2xl font-semibold text-slate-900">
              {authStatus === 'redirecting'
                ? '로그인 페이지로 이동하고 있어요'
                : '세션을 확인하고 있어요'}
            </h1>
            <p className="text-sm leading-relaxed text-slate-600">
              {authStatus === 'redirecting'
                ? '인증이 필요해 로그인 페이지로 이동합니다. 잠시만 기다려 주세요.'
                : '로그인 상태와 세션 만료 여부를 확인한 뒤 계속 진행합니다. 잠시만 기다려 주세요.'}
            </p>
          </div>
          <div className="rounded-2xl border border-slate-200 bg-slate-50 px-6 py-5 text-left text-sm text-slate-600 space-y-2">
            <p className="font-medium text-slate-900">진행 중인 작업</p>
            <ul className="space-y-1">
              <li className="flex items-start gap-2">
                <span className="mt-1 text-red-400">•</span>
                <span>사용자 상태를 확인하고 있습니다.</span>
              </li>
              <li className="flex items-start gap-2">
                <span className="mt-1 text-red-400">•</span>
                <span>필요 시 자동으로 토큰을 갱신하거나 로그인 페이지로 이동합니다.</span>
              </li>
              <li className="flex items-start gap-2">
                <span className="mt-1 text-red-400">•</span>
                <span>확인이 완료되면 원래 보던 화면을 이어서 엽니다.</span>
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

  return <>{children}</>;
}
