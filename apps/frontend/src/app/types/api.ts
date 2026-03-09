// 공통 API Base URL
const DEFAULT_API_BASE_URL = 'http://localhost:18080/api';
const DEFAULT_AUTH_BASE_URL = 'http://localhost:18081';

const ENV_API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL?.trim();
const ENV_AUTH_BASE_URL = process.env.NEXT_PUBLIC_AUTH_BASE_URL?.trim();

export const API_BASE_URL =
  ENV_API_BASE_URL && ENV_API_BASE_URL.length > 0 ? ENV_API_BASE_URL : DEFAULT_API_BASE_URL;
export const AUTH_BASE_URL =
  ENV_AUTH_BASE_URL && ENV_AUTH_BASE_URL.length > 0 ? ENV_AUTH_BASE_URL : DEFAULT_AUTH_BASE_URL;

export const SALES_BASE_PATH = `${API_BASE_URL}/business/sd`;
export const FINANCE_BASE_PATH = `${API_BASE_URL}/business/fcm`;
export const DASHBOARD_BASE_PATH = `${API_BASE_URL}/dashboard`;
export const INVENTORY_BASE_PATH = `${API_BASE_URL}/scm-pp`;
export const HRM_BASE_PATH = `${API_BASE_URL}/business/hrm`;
export const PROFILE_BASE_PATH = `${API_BASE_URL}/business/profile`;

export interface ProblemDetail {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance: string;
  code?: number;
  errors?: unknown;
  traceId?: string;
  upstreamService?: string;
}

// ----------------------- SALES -----------------------
export const SALES_ENDPOINTS = {
  STATS: `${SALES_BASE_PATH}/metrics`,
  QUOTES_LIST: `${SALES_BASE_PATH}/quotations`,
  QUOTE_DETAIL: (id: string) => `${SALES_BASE_PATH}/quotations/${id}`,
  QUOTE_CONFIRM: (id: string) => `${SALES_BASE_PATH}/quotations/${id}/reviews`,
  INVENTORY_CHECK: `${SALES_BASE_PATH}/inventory-checks`,
  QUOTE_DELIVERY_PROCESS: (id: string) => `${SALES_BASE_PATH}/quotations/${id}/orders`,
  ORDERS_LIST: `${SALES_BASE_PATH}/orders`,
  ORDER_DETAIL: (id: string) => `${SALES_BASE_PATH}/orders/${id}`,
  CUSTOMERS_LIST: `${SALES_BASE_PATH}/customers`,
  CUSTOMER_DETAIL: (id: string) => `${SALES_BASE_PATH}/customers/${id}`,
  EDIT_CUSTOMER: (id: string) => `${SALES_BASE_PATH}/customers/${id}`,
  ANALYTICS: `${SALES_BASE_PATH}/analytics/sales`,
  NEW_ORDER: `${SALES_BASE_PATH}/quotations`,
  NEW_QUOTE_ITEM_TOGGLE: `${INVENTORY_BASE_PATH}/product-options`,
  CSUTOMER_STATISTICS: `${SALES_BASE_PATH}/customer-users/me/metrics/quotation-counts`,
} as const;

// ----------------------- FINANCE -----------------------

export const FINANCE_ENDPOINTS = {
  STATISTICS: `${FINANCE_BASE_PATH}/statictics`,
  PURCHASE_INVOICES_LIST: `${FINANCE_BASE_PATH}/invoice/ap`,
  PURCHASE_INVOICE_DETAIL: (invoiceId: string) => `${FINANCE_BASE_PATH}/invoice/ap/${invoiceId}`,
  SALES_INVOICES_LIST: `${FINANCE_BASE_PATH}/invoice/ar`,
  SALES_INVOICE_DETAIL: (invoiceId: string) => `${FINANCE_BASE_PATH}/invoice/ar/${invoiceId}`,

  PURCHASE_INVOICE_REQUEST: (invoiceId: string) =>
    `${FINANCE_BASE_PATH}/invoice/ap/receivable/request?invoiceId=${invoiceId}`,
  SALES_INVOICE_COMPLETE: (invoiceId: string) =>
    `${FINANCE_BASE_PATH}/invoice/ar/${invoiceId}/receivable/complete`,
  SUPPLIER_AP_COMPLETE: (invoiceId: string) =>
    `${FINANCE_BASE_PATH}/invoice/ap/${invoiceId}/payable/complete`,
  // SUPPLIER_AP_COMPLETE: (invoiceId: string) =>
  //   `${FINANCE_BASE_PATH}/invoice/ap/${invoiceId}/payable/complete`,
  CUSTOMER_STATISTICS: `${FINANCE_BASE_PATH}/statistics/customer/total-purchases`,
  SUPPLIER_STATISTICS: `${FINANCE_BASE_PATH}/statistics/supplier/total-sales`,
} as const;

// ----------------------- DASHBOARD -----------------------
export const DASHBOARD_ENDPOINTS = {
  STATS: `${DASHBOARD_BASE_PATH}/metrics`,
  WORKFLOW_STATUS: `${DASHBOARD_BASE_PATH}/workflows`,
} as const;

// ----------------------- INVENTORY -----------------------
export const INVENTORY_ENDPOINTS = {
  STATS: `${INVENTORY_BASE_PATH}/iv/metrics`,
  INVENTORY_LIST: `${INVENTORY_BASE_PATH}/iv/inventory-items`,
  INVENTORY_DETAIL: (itemId: string) => `${INVENTORY_BASE_PATH}/iv/items/${itemId}`,
  LOW_STOCK: `${INVENTORY_BASE_PATH}/iv/shortage-previews`,
  RECENT_STOCK_MOVEMENT: `${INVENTORY_BASE_PATH}/iv/stock-transfers`,
  PRODUCTION_LIST: `${INVENTORY_BASE_PATH}/sales-orders`,
  READY_TO_SHIP_LIST: `${INVENTORY_BASE_PATH}/sales-orders`,
  PENDING_LIST: `${INVENTORY_BASE_PATH}/purchase-orders`,
  RECEIVED_LIST: `${INVENTORY_BASE_PATH}/purchase-orders`,
  PRODUCTIONDETAIL: (itemId: string) => `${INVENTORY_BASE_PATH}/sales-orders/${itemId}`,
  READY_TO_SHIP_DETAIL: (itemId: string) => `${INVENTORY_BASE_PATH}/sales-orders/${itemId}`,
  MARKAS_READY_TO_SHIP_DETAIL: (orderId: string) =>
    `${INVENTORY_BASE_PATH}/sales-orders/${orderId}/shipments`,
  ADD_MATERIALS: `${INVENTORY_BASE_PATH}/iv/items`,
  MATERIALS_LIST: `${INVENTORY_BASE_PATH}/iv/items/search`,
  EDIT_SAFETY_STOCK: (itemId: string, safetyStock: number) =>
    `${INVENTORY_BASE_PATH}/iv/items/${itemId}/safety-stock?safetyStock=${safetyStock}`,
  // ---------- 메뉴 조회 ----------
  ITEM_TOGGLE: `${INVENTORY_BASE_PATH}/iv/item-options`,
  WAREHOUSE_TOGGLE: `${INVENTORY_BASE_PATH}/iv/warehouse-options`,
} as const;

// ----------------------- LOWSTOCK -----------------------
export const LOWSTOCK_ENDPOINTS = {
  STATS: `${INVENTORY_BASE_PATH}/iv/shortage-metrics`,
  LOW_STOCK_LIST: `${INVENTORY_BASE_PATH}/iv/shortage`,
};

// ----------------------- WAREHOUSE -----------------------
export const WAREHOUSE_ENDPOINTS = {
  STATS: `${INVENTORY_BASE_PATH}/iv/warehouse-metrics`,
  WAREHOUSE_LIST: `${INVENTORY_BASE_PATH}/iv/warehouses`,
  WAREHOUSE_DETAIL: (warehouseId: string) => `${INVENTORY_BASE_PATH}/iv/warehouses/${warehouseId}`,
  WAREHOUSE_MANAGE: (warehouseId: string) => `${INVENTORY_BASE_PATH}/iv/warehouses/${warehouseId}`,
  ADD_WAREHOUSE: `${INVENTORY_BASE_PATH}/iv/warehouses`,
  WAREHOUSE_MANAGER_INFO: `${INVENTORY_BASE_PATH}/iv/warehouse-manager-options`,
};

// ----------------------- USER -----------------------
export const USER_ENDPOINTS = {
  LOGIN: `${AUTH_BASE_URL}/oauth2/token`,
  LOGOUT: `${AUTH_BASE_URL}/logout`,
  USER_INFO: `${API_BASE_URL}/user`,
  USER_PROFILE_INFO: `${HRM_BASE_PATH}/employees/by-internel-user`,
};

// ----------------------- PROFILE -----------------------
export const PROFILE_ENDPOINTS = {
  VACATION: `${HRM_BASE_PATH}/leave/request`,
  PROFILE_INFO: `${PROFILE_BASE_PATH}`,
  ATTENDANCE_RECORDS: `${PROFILE_BASE_PATH}/attendance-records`,
  TODAY_ATTENDANCE: `${PROFILE_BASE_PATH}/today-attendance`,
  AVAILABLE_TRAINING: `${PROFILE_BASE_PATH}/training-items/available`,
  COMPLETED_TRAINING: `${PROFILE_BASE_PATH}/training-items/completed`,
  PROGRESS_TRAINING: `${PROFILE_BASE_PATH}/training-items/in-progress`,
  REGISTER_TRAINING: (trainingId: string) =>
    `${PROFILE_BASE_PATH}/training-enrollments?trainingId=${trainingId}`,
  CHECK_IN: `${HRM_BASE_PATH}/attendance/check-in`,
  CHECK_OUT: `${HRM_BASE_PATH}/attendance/check-out`,
  EDIT_PROFILE: `${PROFILE_BASE_PATH}`,
};
