import { Page } from '@/app/types/Page';

export interface PurchaseReqItem {
  itemId: number;
  itemName: string;
  dueDate: string;
  quantity: number;
  uomCode: string; // 단위 (예: 'EA')
  unitPrice: number;
  amount: number;
}

export interface PurchaseReqDetailResponse {
  id: string;
  purchaseRequisitionNumber: string;
  requesterId: string;
  requesterName: string;
  departmentId: string;
  departmentName: string;
  requestDate: string; // "2024-01-15"
  statusCode: string;
  items: PurchaseReqItem[];
  totalAmount: number;
}

export interface PurchaseReqResponse {
  purchaseRequisitionId: string;
  purchaseRequisitionNumber: string;
  requesterId: string;
  requesterName: string;
  departmentId: string;
  departmentName: string;
  statusCode: string;
  requestDate: string;
  totalAmount: number;
}

// 최상위 응답 타입
export interface PurchaseReqListResponse {
  content: PurchaseReqResponse[];
  page: Page;
}
