import { Page, PageRequest } from '@/app/types/Page';

export interface MrpOrdersListData {
  quotationId: string;
  itemId: string;
  itemName: string;
  requiredQuantity: number; // 소요 수량
  currentStock: number;
  reservedStock: number; // 예약 수량
  actualAvailableStock: number;
  safetyStock: number; // 필요없음
  availableStock: number; // 가용재고
  availableStatusCode: string;
  shortageQuantity: number;
  consumptionQuantity: number; // 소모량
  itemType: string;
  procurementStartDate: string;
  expectedArrivalDate: string;
  supplierCompanyName: string;
  convertStatus: string;
}

// MRP 순소요 목록 최상위 응답 타입
export interface MrpOrdersListResponse {
  content: MrpOrdersListData[];
  page: Page;
}

export interface FetchMrpOrdersListParams extends PageRequest {
  bomId?: string;
  quotationId: string;
  availableStatusCode: string;
}
