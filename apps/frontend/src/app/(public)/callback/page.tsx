'use client';

import { useEffect } from 'react';
import axios from 'axios';
import { startAuthorization } from '@/lib/auth/startAuthorization';
import { USER_ENDPOINTS } from '@/app/types/api';
import { getUserInfo } from './callback.api';
import { useAuthStore } from '@/store/authStore';
import Cookies from 'js-cookie';
import { persistAccessToken } from '@/lib/auth/tokenStorage';

// const REDIRECT_URI = 'http://localhost:3000/callback'; // 배포용
const REDIRECT_URI = 'https://everp.co.kr/callback'; // 서버용

function cleanupPkce() {
  localStorage.removeItem('pkce_verifier');
  localStorage.removeItem('oauth_state');
}

// 키 생성 로직 추가
function makeBasicAuthHeader(clientId: string, clientSecret: string): string {
  const plain = `${clientId}:${clientSecret}`;
  const utf8 = new TextEncoder().encode(plain);
  let binary = '';
  for (let i = 0; i < utf8.length; i++) binary += String.fromCharCode(utf8[i]);
  const encoded = btoa(binary);
  return `Basic ${encoded}`;
}

export default function CallbackPage() {
  const { setUserInfo } = useAuthStore();
  useEffect(() => {
    (async () => {
      try {
        const q = new URLSearchParams(window.location.search);
        const code = q.get('code');
        const state = q.get('state');
        const expected = localStorage.getItem('oauth_state');

        console.log('state', state);
        console.log('expected', expected);
        console.log(localStorage.getItem('oauth_state'));

        if (!code || !state || !expected || state !== expected) {
          cleanupPkce();
          throw new Error('Invalid state or code');
        }

        const verifier = localStorage.getItem('pkce_verifier');
        console.log('verify', verifier);
        if (!verifier) throw new Error('Missing PKCE verifier');

        const body = new URLSearchParams({
          grant_type: 'authorization_code',
          // client_id: 'everp-spa', // 로컬용
          client_id: 'everp', // 배포용
          redirect_uri: REDIRECT_URI,
          code,
          code_verifier: verifier,
        });

        const res = await axios.post(USER_ENDPOINTS.LOGIN, body.toString(), {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            // 동적으로 키 생성
            Authorization: makeBasicAuthHeader('everp', 'super-secret'),
          },
        });

        const { access_token, expires_in } = res.data;

        persistAccessToken(access_token, expires_in);

        const userInfo = await getUserInfo();
        setUserInfo(userInfo);
        Cookies.set('role', userInfo.userRole.toUpperCase(), { path: '/', sameSite: 'lax' });
        cleanupPkce();

        const returnTo = localStorage.getItem('oauth_return_to') || '/';
        localStorage.removeItem('oauth_return_to');
        localStorage.removeItem('oauth_state');

        window.history.replaceState({}, '', new URL(returnTo, window.location.origin).toString());
        window.location.replace(returnTo);
      } catch (error: unknown) {
        let errMessage = 'token_exchange_failed';
        alert(error);
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

        if (errMessage === 'invalid_grant') {
          startAuthorization('/');
          return;
        }

        throw new Error(errMessage);
      }
    })();
  }, [setUserInfo]);

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
              <span>사용자 정보를 불러와 역할을 확인하는 중입니다.</span>
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
