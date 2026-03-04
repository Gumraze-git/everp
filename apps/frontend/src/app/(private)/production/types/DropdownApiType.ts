// 제품 드롭다운 응답 api
export interface MpsDropdownData {
  productId: string;
  productName: string;
}

export type MpsDropdownResponse = MpsDropdownData[];

export interface MrpPlannedOrderStatus {
  productId: string;
  productName: string;
}

export type MrpPlannedOrderStatusResponse = MrpPlannedOrderStatus[];
