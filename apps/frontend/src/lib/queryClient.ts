import { QueryClient } from '@tanstack/react-query';
import axios from 'axios';

function shouldRetry(failureCount: number, error: unknown) {
  if (axios.isAxiosError(error)) {
    const status = error.response?.status;

    if (status === 401 || status === 403) {
      return false;
    }
  }

  return failureCount < 1;
}

export function createQueryClient() {
  return new QueryClient({
    defaultOptions: {
      queries: {
        retry: shouldRetry,
        refetchOnWindowFocus: false,
      },
      mutations: {
        retry: shouldRetry,
      },
    },
  });
}

// 서버 & 클라이언트 모두 사용할 공통 QueryClient 생성 함수
export function getQueryClient() {
  return createQueryClient();
}
