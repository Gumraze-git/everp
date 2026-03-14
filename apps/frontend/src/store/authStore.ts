import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { userInfoResponse } from '@/app/(public)/callback/userInfoType';

export type AuthStatus = 'checking' | 'authenticated' | 'redirecting';

interface AuthState {
  authStatus: AuthStatus;
  userInfo: userInfoResponse | null;
  setAuthStatus: (status: AuthStatus) => void;
  setAuthenticatedUser: (info: userInfoResponse) => void;
  clearUserInfo: () => void;
  clearAuth: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      authStatus: 'checking',
      userInfo: null,

      setAuthStatus: (status) => set({ authStatus: status }),

      setAuthenticatedUser: (info) => set({ userInfo: info, authStatus: 'authenticated' }),

      clearUserInfo: () => set({ userInfo: null }),

      clearAuth: () => set({ authStatus: 'checking', userInfo: null }),
    }),
    {
      name: 'user-info-storage',
      partialize: (state) => ({ userInfo: state.userInfo }),
    },
  ),
);
