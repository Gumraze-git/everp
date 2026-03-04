import { Page, PageRequest } from '@/app/types/Page';

// MRP 계획 주문 목록 타입
export interface MrpPlannedOrderList {
  mrpRunId: string;
  quotationNumber: string; // 참조 견적서
  itemId: string;
  itemName: string; // 자재
  quantity: number; // 수량
  status: string; // 상태
  procurementStartDate: string; // 조달일
  expectedArrivalDate: string;
}

// MRP 계획 주문 목록 최상위 응답 타입
export interface MrpPlannedOrdersListResponse {
  page: Page;
  content: MrpPlannedOrderList[];
}

export interface FetchMrpPlannedOrdersListParams extends PageRequest {
  status?: string;
  quotationId?: string;
}
