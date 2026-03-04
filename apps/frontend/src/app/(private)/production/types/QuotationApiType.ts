import { AvailableStockStatus } from '@/app/(private)/production/constants';
import { Page, PageRequest } from '@/app/types/Page';
import { QuotationStatus } from '@/app/(private)/production/constants';
import { DateRequest } from '@/app/types/Date';

export interface itemsData {
  productId: string;
  productName: string;
  quantity: number;
  uomName: string;
  unitPrice: number;
}

export interface QuotationData {
  quotationId: string;
  quotationNumber: string;
  customerName: string;
  requestDate: string;
  dueDate: string;
  statusCode: QuotationStatus;
  availableStatus: AvailableStockStatus;
  items: itemsData[];
}

// 견적 관리 리스트 최상위 응답 타입
export interface QuotationListResponse {
  content: QuotationData[];
  page: Page;
}

// 응답 요청시 request params
export interface FetchQuotationParams extends DateRequest, PageRequest {
  // stockStatusCode: AvailableStockStatus;
  availableStatusCode: string;
  statusCode: QuotationStatus;
}
