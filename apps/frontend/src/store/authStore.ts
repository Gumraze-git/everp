import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { userInfoResponse } from '@/app/(public)/callback/userInfoType';

interface AuthState {
  userInfo: userInfoResponse | null;
  setUserInfo: (info: userInfoResponse) => void;
  clearUserInfo: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      userInfo: null,

      setUserInfo: (info) => set({ userInfo: info }),

      clearUserInfo: () => set({ userInfo: null }),
    }),
    {
      name: 'user-info-storage',
      partialize: (state) => ({ userInfo: state.userInfo }),
    },
  ),
);
