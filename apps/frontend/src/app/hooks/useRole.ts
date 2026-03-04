import { useAuthStore } from '@/store/authStore';

export const useRole = () => {
  const userInfo = useAuthStore((state) => state.userInfo);
  return userInfo?.userRole;
};
