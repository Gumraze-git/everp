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
  withCredentials?: boolean;
};

let refreshPromise: Promise<void> | null = null;
const axiosInstance = axios.create({
  withCredentials: true,
  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN',
});

function isAuthRequest(url?: string) {
  if (!url) {
    return false;
  }

  return url.startsWith(AUTH_BASE_URL) || url.includes('/oauth2/');
}

function clearClientAuthArtifacts() {
  Cookies.remove('role', { path: '/' });
  useAuthStore.getState().clearAuth();
}

axiosInstance.interceptors.response.use(
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
      originalRequest.withCredentials = true;
      return axiosInstance(originalRequest);
    } catch (refreshError) {
      clearClientAuthArtifacts();
      useAuthStore.getState().setAuthStatus('redirecting');
      await startAuthorization(getCurrentReturnTo());
      return Promise.reject(refreshError);
    }
  },
);

export default axiosInstance;
