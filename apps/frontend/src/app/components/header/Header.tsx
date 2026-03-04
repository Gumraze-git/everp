'use client';

import { useEffect, useState } from 'react';
import Logo from '@/app/components/header/Logo';
import Navigation from '@/app/components/header/Navigation';
import ProfileDropdown from '@/app/components/header/ProfileDropdown';
import NotificationDropdown from './Notification/NoificationDropdown';
import { useAuthStore } from '@/store/authStore';
import { useNotificationSSE } from './Notification/useNotificationSSE';

function SSEConnector() {
  const { userInfo } = useAuthStore();
  const enabled = !!userInfo?.userId;

  useNotificationSSE({
    enabled: enabled,
    onAlarm: (alarm) => {
      // 알림 수신 시 처리
      console.log('Header/SSEConnector: New alarm received:', alarm);
    },
    onUnreadCountChange: (count) => {
      // 읽지 않은 개수 업데이트 처리
      console.log('Header/SSEConnector: Global Unread count:', count);
    },
  });

  return null;
}

export default function Header() {
  const [isScrolled, setIsScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 0);
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <header
      className={`fixed top-0 left-0 right-0 z-40 transition-all duration-300 ${
        isScrolled ? 'bg-white shadow-sm border-gray-100' : 'bg-gray-50'
      }`}
    >
      <SSEConnector />
      <div className="min-w-full mx-auto px-8 sm:px-6 lg:px-4">
        <div className="flex justify-between items-center h-16">
          {/* 좌측: 로고 + 네비게이션 바*/}
          <div className="flex gap-4">
            <Logo />
            <Navigation />
          </div>

          {/* 우측: 알림 + 프로필 */}
          <div className="flex items-center space-x-4">
            <NotificationDropdown />
            <ProfileDropdown />
          </div>
        </div>
      </div>
    </header>
  );
}
