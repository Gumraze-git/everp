import { Page } from '@/app/types/Page';

type SourceType = 'PR' | 'SD' | 'IM' | 'FCM' | 'HRM' | 'PP' | 'CUS' | 'SUP';

// isRead: false;
export interface NotificationData {
  notificationId: string;
  notificationTitle: string;
  notificationMessage: string;
  linkType: string;
  linkId: string;
  source: SourceType;
  createdAt: string;
  isRead: boolean;
}

// 알림 목록 조회
export interface NotificationListResponse {
  content: NotificationData[];
  page: Page;
}

// 알림 갯수 조회
export interface NotificationCountResponse {
  count: number;
}
