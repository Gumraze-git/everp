import { USER_ENDPOINTS } from '@/app/types/api';
import { userInfoResponse } from './userInfoType';
import axios from '@/lib/axiosInstance';

interface LogoutResponse {
  success: boolean;
}

export const getUserInfo = async (): Promise<userInfoResponse> => {
  const res = await axios.get<userInfoResponse>(USER_ENDPOINTS.USER_INFO);
  return res.data;
};

export const logout = async (): Promise<LogoutResponse> => {
  const res = await axios.post<LogoutResponse>(USER_ENDPOINTS.LOGOUT, undefined, {
    withCredentials: true,
  });
  return res.data;
};
