'use client';

import { useEffect, useRef, useState } from 'react';
import { NOTIFICATION_ENDPOINTS } from '@/lib/api/notification.endpoints';
import { useQueryClient } from '@tanstack/react-query';
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { useAuthStore } from '@/store/authStore';

interface Alarm {
  alarmId: string;
  alarmType: string;
  targetId: string;
  title: string;
  message: string;
  linkId: string;
  linkType: string;
}

interface UseNotificationSSEOptions {
  enabled?: boolean;
  onAlarm?: (alarm: Alarm) => void;
  onUnreadCountChange?: (count: number) => void;
}

export function useNotificationSSE({
  enabled = true,
  onAlarm,
  onUnreadCountChange,
}: UseNotificationSSEOptions) {
  const authStatus = useAuthStore((state) => state.authStatus);
  const userInfo = useAuthStore((state) => state.userInfo);
  const userId = userInfo?.userId || '';
  const isAuthenticated = authStatus === 'authenticated';

  const abortControllerRef = useRef<AbortController | null>(null);
  const queryClient = useQueryClient();
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!enabled || !isAuthenticated || !userId) {
      console.info('[SSE] Not connecting: auth is not ready or userId is missing.', { userId });

      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
        abortControllerRef.current = null;
      }
      return;
    }

    const abortController = new AbortController();
    abortControllerRef.current = abortController;

    const connectSSE = async () => {
      const url = NOTIFICATION_ENDPOINTS.SUBSCRIBE;
      console.info('[SSE] Connecting to:', url);

      try {
        await fetchEventSource(url, {
          signal: abortController.signal,
          credentials: 'include',
          onopen: async (response) => {
            if (response.ok) {
              console.info('[SSE] Connection opened successfully');
              setError(null);
            } else if (response.status === 401) {
              setError('인증이 만료되었습니다. 다시 로그인해주세요.');
              throw new Error('Unauthorized');
            } else {
              setError('알림 서버 연결에 실패했습니다.');
              throw new Error('Failed to connect');
            }
          },
          onmessage: (event) => {
            console.log('[SSE] Message received:', event);

            switch (event.event) {
              case 'keepalive':
                console.log('[SSE] Keepalive:', event.data);
                break;

              case 'alarm':
                try {
                  const alarm: Alarm = JSON.parse(event.data);
                  queryClient.invalidateQueries({ queryKey: ['notificationList'] });
                  onAlarm?.(alarm);
                } catch (err) {
                  console.error('[SSE] Failed to parse alarm:', err);
                  setError('알림 데이터를 처리하는 중 오류가 발생했습니다.');
                }
                break;

              case 'unreadCount':
                try {
                  const count = parseInt(event.data, 10);
                  onUnreadCountChange?.(count);
                } catch (err) {
                  console.error('[SSE] Failed to parse unread count:', err);
                  setError('읽지 않은 알림 개수를 불러오는 중 오류가 발생했습니다.');
                }
                break;

              default:
                console.log('[SSE] Unknown event type:', event.event);
            }
          },
          onerror: (err) => {
            console.error('[SSE] Error:', err);
            setError('알림 서버 연결 중 오류가 발생했습니다.');
            throw err; // 재연결 시도를 멈추고 싶다면 throw
          },
        });
      } catch (err) {
        console.error('[SSE] Connection error:', err);
      }
    };

    connectSSE();

    return () => {
      console.info('[SSE] Closing connection');
      abortController.abort();
      abortControllerRef.current = null;
    };
  }, [enabled, isAuthenticated, userId, queryClient, onAlarm, onUnreadCountChange]);
  return {
    disconnect: () => {
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
        abortControllerRef.current = null;
      }
    },
    error,
  };
}
