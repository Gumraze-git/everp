import { SALES_ENDPOINTS, ApiResponse, ApiResponseNoData } from '@/app/types/api';
import {
  CustomerSalesStatResponse,
  SalesStatResponse,
} from '@/app/(private)/sales/types/SalesStatsType';
import { Quote, QuoteQueryParams } from '@/app/(private)/sales/types/SalesQuoteListType';
import { Inventories, QuoteDetail } from '@/app/(private)/sales/types/QuoteDetailModalType';
import { CustomerDetail } from '@/app/(private)/sales/types/SalesCustomerDetailType';
import {
  SalesCustomer,
  CustomerQueryParams,
} from '@/app/(private)/sales/types/SalesCustomerListType';
import { CustomerData, ServerResponse } from '@/app/(private)/sales/types/NewCustomerModalType';
import { AnalyticsQueryParams, SalesAnalysis } from '@/app/(private)/sales/types/SalesChartType';
import { Order, OrderQueryParams } from '@/app/(private)/sales/types/SalesOrderListType';
import { OrderDetail } from '@/app/(private)/sales/types/SalesOrderDetailType';
import { InventoryCheckRes } from './types/QuoteReviewModalType';
import { CustomerEditData, CustomerResponse } from './types/CustomerEditModalType';
import { Page } from '@/app/types/Page';
import { ItemResponse, NewOrderRequest } from './types/NewOrderModalType';
import axios from '@/lib/axiosInstance';

// ----------------------- 통계 지표 -----------------------
export const getSalesStats = async (): Promise<SalesStatResponse> => {
  const res = await axios.get<ApiResponse<SalesStatResponse>>(SALES_ENDPOINTS.STATS);
  return res.data.data;
};

export const getCustomerSalesStats = async (): Promise<CustomerSalesStatResponse> => {
  const res = await axios.get<ApiResponse<CustomerSalesStatResponse>>(
    SALES_ENDPOINTS.CSUTOMER_STATISTICS,
  );
  return res.data.data;
};

// ----------------------- 견적 관리 -----------------------
export const getQuoteList = async (
  params?: QuoteQueryParams,
): Promise<{ data: Quote[]; pageData: Page }> => {
  const query = new URLSearchParams({
    ...(params?.startDate && { startDate: params.startDate }),
    ...(params?.endDate && { endDate: params.endDate }),
    ...(params?.status && { status: params.status }),
    ...(params?.type && { type: params.type }),
    ...(params?.keyword && { keyword: params.keyword }),
    ...(params?.sort && { sort: params.sort }),
    ...(params?.page && { page: String(params.page) }),
    ...(params?.size && { size: String(params.size) }),
  }).toString();

  const res = await axios.get<ApiResponse<{ content: Quote[]; page: Page }>>(
    `${SALES_ENDPOINTS.QUOTES_LIST}?${query}`,
  );

  return { data: res.data.data.content, pageData: res.data.data.page };
};

export const getQuoteDetail = async (quotationId: string): Promise<QuoteDetail> => {
  const res = await axios.get<ApiResponse<QuoteDetail>>(SALES_ENDPOINTS.QUOTE_DETAIL(quotationId));
  return res.data.data;
};

export const postNewQuote = async (items: NewOrderRequest): Promise<NewOrderRequest> => {
  const res = await axios.post<ApiResponse<{ items: NewOrderRequest }>>(
    SALES_ENDPOINTS.NEW_ORDER,
    items,
  );
  return res.data.data.items;
};

// ----------------------- 주문 관리 -----------------------
export const getOrderList = async (
  params?: OrderQueryParams,
): Promise<{ data: Order[]; pageData: Page }> => {
  const query = new URLSearchParams({
    ...(params?.start && { start: params.start }),
    ...(params?.end && { end: params.end }),
    ...(params?.status && { status: params.status }),
    ...(params?.type && { type: params.type }),
    ...(params?.keyword && { keyword: params.keyword }),
    ...(params?.page && { page: String(params.page) }),
    ...(params?.size && { size: String(params.size) }),
  }).toString();

  const res = await axios.get<ApiResponse<{ content: Order[]; page: Page }>>(
    `${SALES_ENDPOINTS.ORDERS_LIST}?${query}`,
  );

  return { data: res.data.data.content, pageData: res.data.data.page };
};

export const getOrderDetail = async (salesOrderId: string): Promise<OrderDetail> => {
  const res = await axios.get<ApiResponse<OrderDetail>>(SALES_ENDPOINTS.ORDER_DETAIL(salesOrderId));
  return res.data.data;
};

export const postQuotationConfirm = async (quotationId: string): Promise<ApiResponseNoData> => {
  const res = await axios.post<ApiResponseNoData>(SALES_ENDPOINTS.QUOTE_CONFIRM, {
    quotationId: quotationId,
  });
  return res.data;
};

export const postInventoryCheck = async (items: Inventories[]): Promise<InventoryCheckRes[]> => {
  const res = await axios.post<ApiResponse<{ items: InventoryCheckRes[] }>>(
    SALES_ENDPOINTS.INVENTORY_CHECK,
    {
      items,
    },
  );
  return res.data.data.items;
};

export const postDeliveryProcess = async (quotationId: string): Promise<ApiResponseNoData> => {
  const res = await axios.post<ApiResponseNoData>(
    SALES_ENDPOINTS.QUOTE_DELIVERY_PROCESS(quotationId),
  );
  return res.data;
};

// ----------------------- 고객 관리 -----------------------
export const getCustomerList = async (
  params?: CustomerQueryParams,
): Promise<{ data: SalesCustomer[]; pageData: Page }> => {
  const query = new URLSearchParams({
    ...(params?.status && { status: params.status }),
    ...(params?.keyword && { keyword: params.keyword }),
    ...(params?.type && { type: params.type }),
    ...(params?.page && { page: String(params.page) }),
    ...(params?.size && { size: String(params.size) }),
  }).toString();

  const res = await axios.get<ApiResponse<{ customers: SalesCustomer[]; page: Page }>>(
    `${SALES_ENDPOINTS.CUSTOMERS_LIST}?${query}`,
  );

  return { data: res.data.data.customers, pageData: res.data.data.page };
};

export const getCustomerDetail = async (customerId: string): Promise<CustomerDetail> => {
  const res = await axios.get<ApiResponse<CustomerDetail>>(
    SALES_ENDPOINTS.CUSTOMER_DETAIL(customerId),
  );
  return res.data.data;
};

export const postCustomer = async (customer: CustomerData): Promise<ServerResponse> => {
  const res = await axios.post<ApiResponse<ServerResponse>>(
    SALES_ENDPOINTS.CUSTOMERS_LIST,
    customer,
  );
  return res.data.data;
};

export const patchCustomer = async (
  customerId: string,
  customer: CustomerEditData,
): Promise<CustomerResponse> => {
  const res = await axios.patch<ApiResponse<CustomerResponse>>(
    SALES_ENDPOINTS.EDIT_CUSTOMER(customerId),
    customer,
  );
  return res.data.data;
};

// ----------------------- 매출 분석 -----------------------
export const getAnalytics = async (params?: AnalyticsQueryParams): Promise<SalesAnalysis> => {
  const query = new URLSearchParams({
    ...(params?.startDate && { startDate: params.startDate }),
    ...(params?.endDate && { endDate: params.endDate }),
  }).toString();

  const res = await axios.get<ApiResponse<SalesAnalysis>>(`${SALES_ENDPOINTS.ANALYTICS}?${query}`);
  return res.data.data;
};

// 신규 견적 요청을 위한 자재 가져오기
export const getItemInfoForNewQuote = async (): Promise<ItemResponse[]> => {
  const res = await axios.get<ApiResponse<{ products: ItemResponse[] }>>(
    SALES_ENDPOINTS.NEW_QUOTE_ITEM_TOGGLE,
  );
  return res.data.data.products;
};
