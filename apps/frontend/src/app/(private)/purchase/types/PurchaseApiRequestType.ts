import { DateRequest } from '@/app/types/Date';
import { KeywordRequest } from '@/app/types/KeywordType';
import { PageRequest } from '@/app/types/Page';

export interface PurchaseReqParams extends PageRequest, KeywordRequest, DateRequest {
  statusCode?: string;
}

export interface FetchPurchaseOrderParams extends PageRequest, DateRequest {
  type?: string;
  statusCode?: string;
  keyword?: string;
}

// 화면에서 관리용 (id 포함)
export interface PurchaseRequestItem {
  id: string;
  itemName: string;
  quantity: number;
  uomName: string;
  expectedUnitPrice: number;
  preferredSupplierName: string;
  dueDate: string;
  purpose: string;
  note?: string;
}

export interface StockPurchaseRequestItem {
  productId: string;
  quantity: number;
  mrpRunId?: string; // 재고관리 쪽에선 안 넣어도 됨
}

// 서버 전송용 (id 제외)
export type PurchaseRequestItemBody = Omit<PurchaseRequestItem, 'id'>;

// 비재고성 구매 요청 바디
export interface PurchaseRequestBody {
  requesterId: string;
  items: PurchaseRequestItemBody[];
}

// 재고성 구매 요청 바디
export interface StockPurchaseRequestBody {
  items: StockPurchaseRequestItem[];
}

export interface FetchSupplierListParams extends PageRequest {
  category?: string;
  status?: string;
  searchKeyword?: string;
}
