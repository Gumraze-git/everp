import axios from 'axios';
import Cookies from 'js-cookie';
import { AUTH_BASE_URL } from '@/app/types/api';
import { trySilentRefresh } from './auth/refresh';
import { getCurrentReturnTo } from './auth/config';
import { startAuthorization } from './auth/startAuthorization';
import { useAuthStore } from '@/store/authStore';

type RetryableRequestConfig = {
  headers?: Record<string, string>;
  url?: string;
  _retry?: boolean;
};

let refreshPromise: Promise<void> | null = null;

async function resolveAccessToken(): Promise<string | null> {
  if (typeof window !== 'undefined') {
    return Cookies.get('access_token') ?? null;
  }

  const { cookies } = await import('next/headers');
  const cookieStore = await cookies();
  return cookieStore.get('access_token')?.value ?? null;
}

function isAuthRequest(url?: string) {
  if (!url) {
    return false;
  }

  return url.startsWith(AUTH_BASE_URL) || url.includes('/oauth2/');
}

function clearClientAuthArtifacts() {
  Cookies.remove('access_token', { path: '/' });
  Cookies.remove('access_token_expires_at', { path: '/' });
  Cookies.remove('role', { path: '/' });
  useAuthStore.getState().clearAuth();
}

axios.interceptors.request.use(
  async (config) => {
    const token = await resolveAccessToken();

    if (token) {
      config.headers = config.headers ?? {};
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error),
);

axios.interceptors.response.use(
  (response) => response,
  async (error) => {
    const responseStatus = error.response?.status;
    const originalRequest = error.config as RetryableRequestConfig | undefined;

    if (
      typeof window === 'undefined' ||
      responseStatus !== 401 ||
      !originalRequest ||
      originalRequest._retry ||
      isAuthRequest(originalRequest.url)
    ) {
      return Promise.reject(error);
    }

    originalRequest._retry = true;

    try {
      refreshPromise ??= trySilentRefresh().finally(() => {
        refreshPromise = null;
      });

      await refreshPromise;

      const token = await resolveAccessToken();
      if (token) {
        originalRequest.headers = originalRequest.headers ?? {};
        originalRequest.headers.Authorization = `Bearer ${token}`;
      }

      return axios(originalRequest);
    } catch (refreshError) {
      clearClientAuthArtifacts();
      useAuthStore.getState().setAuthStatus('redirecting');
      await startAuthorization(getCurrentReturnTo());
      return Promise.reject(refreshError);
    }
  },
);

export default axios;
