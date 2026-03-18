import { API_BASE_URL } from '@/app/types/api';

export const PURCHASE_BASE_PATH = `${API_BASE_URL}/scm-pp/mm`;

export const PURCHASE_ENDPOINTS = {
  // --- 드롭다운 ---
  PURCHASE_REQUISITION_STATUS_TOGGLE: `${PURCHASE_BASE_PATH}/purchase-requisition-status-options`,
  PURCHASE_REQUISITION_SEARCH_TYPE_TOGGLE: `${PURCHASE_BASE_PATH}/purchase-requisition-search-type-options`,
  PURCHASE_ORDER_STATUS_TOGGLE: `${PURCHASE_BASE_PATH}/purchase-order-status-options`,
  PURCHASE_ORDER_SEARCH_TYPE_TOGGLE: `${PURCHASE_BASE_PATH}/purchase-order-search-type-options`,
  SUPPLIER_CATEGORY_TOGGLE: `${PURCHASE_BASE_PATH}/supplier-category-options`,
  SUPPLIER_SEARCH_TYPE_TOGGLE: `${PURCHASE_BASE_PATH}/supplier-search-type-options`,
  SUPPLIER_STATUS_TOGGLE: `${PURCHASE_BASE_PATH}/supplier-status-options`,

  // --- 통계 ---
  STATISTICS: `${PURCHASE_BASE_PATH}/metrics`, // MM 통계 조회
  SUPPLIER_ORDERS_STATISTICS: `${PURCHASE_BASE_PATH}/supplier-users/me/metrics/order-counts`, // 공급사 발주서 통계 조회

  // --- 구매 요청 ---
  PURCHASE_REQUISITIONS: `${PURCHASE_BASE_PATH}/purchase-requisitions`, // 구매 요청 목록 조회, 비재고성 자재 구매 요청서 생성
  STOCK_PURCHASE_REQUISITIONS: `${PURCHASE_BASE_PATH}/stock-purchase-requisitions`, // 재고성 자재 구매 요청서 생성

  PURCHASE_REQUISITION_DETAIL: (prId: string) =>
    `${PURCHASE_BASE_PATH}/purchase-requisitions/${prId}`, // 구매 요청서 상세조회, 수정, 삭제
  PURCHASE_REQUISITION_STATUS: (prId: string) =>
    `${PURCHASE_BASE_PATH}/purchase-requisitions/${prId}`, // 구매요청서 상태 변경
  // PURCHASE_REQUISITION_BY_PURCHASE: (purchaseId: string) =>
  //   `${PURCHASE_BASE_PATH}/purchase-requisitions/${purchaseId}`, // 구매요청 상세 조회

  // --- 발주서 ---
  PURCHASE_ORDERS: `${PURCHASE_BASE_PATH}/purchase-orders`, // 발주서 목록 조회
  PURCHASE_ORDER_DETAIL: (purchaseId: string) =>
    `${PURCHASE_BASE_PATH}/purchase-orders/${purchaseId}`, // 발주서 상세 조회
  PURCHASE_ORDER_STATUS: (purchaseOrderId: string) =>
    `${PURCHASE_BASE_PATH}/purchase-orders/${purchaseOrderId}`, // 발주서 상태 변경
  // --- 공급업체 ---
  SUPPLIER: `${PURCHASE_BASE_PATH}/suppliers`, // 공급업체 목록 조회, 등록
  SUPPLIER_DETAIL: (supplierId: string) => `${PURCHASE_BASE_PATH}/suppliers/${supplierId}`, // 공급업체 상세 조회, 수정
};
