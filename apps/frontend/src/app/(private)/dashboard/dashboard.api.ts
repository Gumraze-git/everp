import { DashboardStatRes } from './types/DashboardStatsType';
import { ApiResponse, DASHBOARD_ENDPOINTS } from '@/app/types/api';
import { DashboardWorkflowRes } from './types/DashboardWorkflowType';
import axios from '@/lib/axiosInstance';

// ----------------------- 통계 지표 -----------------------
export const getDashboardStats = async (): Promise<DashboardStatRes> => {
  const res = await axios.get<ApiResponse<DashboardStatRes>>(DASHBOARD_ENDPOINTS.STATS);
  return res.data.data;
};

// ----------------------- 워크플로우 현황 -----------------------
export const getWorkflowStatus = async (): Promise<DashboardWorkflowRes> => {
  const res = await axios.get<ApiResponse<DashboardWorkflowRes>>(
    DASHBOARD_ENDPOINTS.WORKFLOW_STATUS,
  );
  return res.data.data;
};
