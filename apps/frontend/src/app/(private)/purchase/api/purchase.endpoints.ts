import { API_BASE_URL } from '@/app/types/api';

export const PURCHASE_BASE_PATH = `${API_BASE_URL}/scm-pp/mm`;

export const PURCHASE_ENDPOINTS = {
  // --- 드롭다운 ---
  PURCHASE_REQUISITION_STATUS_TOGGLE: `${PURCHASE_BASE_PATH}/purchase-requisition/status/toggle`,
  PURCHASE_REQUISITION_SEARCH_TYPE_TOGGLE: `${PURCHASE_BASE_PATH}/purchase-requisition/search-type/toggle`,
  PURCHASE_ORDER_STATUS_TOGGLE: `${PURCHASE_BASE_PATH}/purchase-orders/status/toggle`,
  PURCHASE_ORDER_SEARCH_TYPE_TOGGLE: `${PURCHASE_BASE_PATH}/purchase-orders/search-type/toggle`,
  SUPPLIER_CATEGORY_TOGGLE: `${PURCHASE_BASE_PATH}/supplier/category/toggle`,
  SUPPLIER_SEARCH_TYPE_TOGGLE: `${PURCHASE_BASE_PATH}/supplier/search-type/toggle`,
  SUPPLIER_STATUS_TOGGLE: `${PURCHASE_BASE_PATH}/supplier/status/toggle`,

  // --- 통계 ---
  STATISTICS: `${PURCHASE_BASE_PATH}/statistics`, // MM 통계 조회
  SUPPLIER_ORDERS_STATISTICS: `${PURCHASE_BASE_PATH}/supplier/orders/statistics`, // 공급사 발주서 통계 조회

  // --- 구매 요청 ---
  PURCHASE_REQUISITIONS: `${PURCHASE_BASE_PATH}/purchase-requisitions`, // 구매 요청 목록 조회, 비재고성 자재 구매 요청서 생성
  STOCK_PURCHASE_REQUISITIONS: `${PURCHASE_BASE_PATH}/stock-purchase-requisitions`, // 재고성 자재 구매 요청서 생성

  PURCHASE_REQUISITION_DETAIL: (prId: string) =>
    `${PURCHASE_BASE_PATH}/purchase-requisitions/${prId}`, // 구매 요청서 상세조회, 수정, 삭제
  PURCHASE_REQUISITION_RELEASE: (prId: string) =>
    `${PURCHASE_BASE_PATH}/purchase-requisitions/${prId}/release`, // 구매요청서 승인
  PURCHASE_REQUISITION_REJECT: (prId: string) =>
    `${PURCHASE_BASE_PATH}/purchase-requisitions/${prId}/reject`, // 구매요청서 반려
  // PURCHASE_REQUISITION_BY_PURCHASE: (purchaseId: string) =>
  //   `${PURCHASE_BASE_PATH}/purchase-requisitions/${purchaseId}`, // 구매요청 상세 조회

  // --- 발주서 ---
  PURCHASE_ORDERS: `${PURCHASE_BASE_PATH}/purchase-orders`, // 발주서 목록 조회
  PURCHASE_ORDER_APPROVE: (poId: string) => `${PURCHASE_BASE_PATH}/purchase-orders/${poId}/approve`, // 발주서 승인
  PURCHASE_ORDER_REJECT: (poId: string) => `${PURCHASE_BASE_PATH}/purchase-orders/${poId}/reject`, // 발주서 반려
  PURCHASE_ORDER_DETAIL: (purchaseId: string) =>
    `${PURCHASE_BASE_PATH}/purchase-orders/${purchaseId}`, // 발주서 상세 조회
  PURCHASE_ORDER_DELIVERY: (purchaseOrderId: string) =>
    `${PURCHASE_BASE_PATH}/${purchaseOrderId}/start-delivery`, // 배송 시작
  // --- 공급업체 ---
  SUPPLIER: `${PURCHASE_BASE_PATH}/supplier`, // 공급업체 목록 조회, 등록
  SUPPLIER_DETAIL: (supplierId: string) => `${PURCHASE_BASE_PATH}/supplier/${supplierId}`, // 공급업체 상세 조회, 수정
};
