import { Page, PageRequest } from '@/app/types/Page';

// 주별 수요/생산 데이터
export interface WeekData {
  week: string; // 예: "2025-12-1W"
  demand: number;
  requiredInventory: number;
  productionNeeded: number;
  plannedProduction: number;
}

export interface MpsListResponse {
  bomId: string;
  productName: string;
  content: WeekData[];
  page: Page;
}

export interface MpsListParams extends PageRequest {
  bomId: string;
  startDate: string;
  endDate: string;
}
