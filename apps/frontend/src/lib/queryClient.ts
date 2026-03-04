import { QueryClient } from '@tanstack/react-query';

// 서버 & 클라이언트 모두 사용할 공통 QueryClient 생성 함수
export function getQueryClient() {
  return new QueryClient();
}
