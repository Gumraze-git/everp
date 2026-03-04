// 자재 목록 타입
export interface OrderItemData {
  itemId: string;
  itemName: string;
  quantity: number;
  uomName: string;
  unitPrice: number;
}

// MRP 계획 주문 상세 최상위 응답 타입
export interface MrpPlannedOrdersDetailResponse {
  mrpId: string;
  quotationId: string;
  quotationCode: string;
  requesterId: string;
  requesterName: string;
  departmentName: string;
  requestDate: string;
  desiredDueDate: string;
  status: string;
  orderItems: OrderItemData[];
  totalAmount: number;
}
