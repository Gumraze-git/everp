import { Stat, StatResponse } from '@/app/types/StatType';

// 구매 지표 api 응답 데이터 구조
export interface PurchaseStat {
  purchaseOrderAmount: Stat;
  purchaseRequestCount: Stat;
}
export type PurchaseStatResponse = StatResponse<PurchaseStat>;

// 공급사 통계 카드 type
export interface SupplierPurchaseStat {
  orderCount: Stat;
}
export type SupplierPurchaseStatResponse = StatResponse<SupplierPurchaseStat>;
