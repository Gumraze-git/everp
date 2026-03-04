'use client';

import { ReactNode, useEffect, useState } from 'react';
import { trySilentRefresh } from '@/lib/auth/refresh';
import { startAuthorization } from '@/lib/auth/startAuthorization';
import { useQuery } from '@tanstack/react-query';
import { getUserInfo } from '../(public)/callback/callback.api';
import { useAuthStore } from '@/store/authStore';
import Cookies from 'js-cookie';
import { clearAccessToken, readStoredToken } from '@/lib/auth/tokenStorage';

export default function PrivateGuard({ children }: { children: ReactNode }) {
  const [ready, setReady] = useState(false);
  const { userInfo, setUserInfo } = useAuthStore();

  useEffect(() => {
    (async () => {
      const { token, expiresAt } = readStoredToken();

      if (!token || !expiresAt || Date.now() > expiresAt) {
        clearAccessToken();
        try {
          await trySilentRefresh();
          // startAuthorization(window.location.pathname);
        } catch {
          startAuthorization(window.location.pathname);
          return;
        }
      }

      setReady(true);
    })();
  }, [setReady]);

  const { data } = useQuery({
    queryKey: ['userInfo'],
    queryFn: getUserInfo,
    enabled: ready && !userInfo, // 스토어에 값이 없고 가드가 준비된 경우에만 호출
  });

  useEffect(() => {
    if (data) {
      setUserInfo(data);
      Cookies.set('role', data.userRole.toUpperCase(), { path: '/', sameSite: 'lax' });
    }
  }, [data, setUserInfo]);

  if (!ready) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-white via-slate-50 to-slate-100 flex items-center justify-center px-4">
        <div className="w-full max-w-md rounded-3xl border border-slate-200 bg-white shadow-xl shadow-slate-200/60 px-10 py-12 text-center space-y-6">
          <div
            className="mx-auto h-16 w-16 rounded-full border-4 border-t-transparent border-red-400 animate-spin"
            aria-hidden
          />
          <div className="space-y-3">
            <h1 className="text-2xl font-semibold text-slate-900">세션을 확인하고 있어요</h1>
            <p className="text-sm leading-relaxed text-slate-600">
              로그인 상태와 세션 만료 여부를 확인한 뒤 계속 진행합니다. 잠시만 기다려 주세요.
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
                <span>필요 시 자동으로 로그인 페이지로 이동합니다.</span>
              </li>
              <li className="flex items-start gap-2">
                <span className="mt-1 text-red-400">•</span>
                <span>확인이 완료되면 대시보드로 이동합니다.</span>
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
