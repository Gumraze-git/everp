import { FINANCE_ENDPOINTS } from '@/app/types/api';
import {
  CustomerSupplierStatResponse,
  FinanceStatResponse,
} from '@/app/(private)/finance/types/FinanceStatsType';
import { InvoiceListRes, InvoiceQueryParams } from './types/InvoiceListType';
import { InvoicetDetailRes } from './types/InvoiceDetailModalType';
import { Page } from '@/app/types/Page';
import axios from '@/lib/axiosInstance';

// ----------------------- 통계 지표 -----------------------
export const getFinanceStats = async (): Promise<FinanceStatResponse> => {
  const res = await axios.get<FinanceStatResponse>(FINANCE_ENDPOINTS.STATISTICS);
  return res.data;
};

export const getCustomerStats = async (): Promise<CustomerSupplierStatResponse> => {
  const res = await axios.get<CustomerSupplierStatResponse>(FINANCE_ENDPOINTS.CUSTOMER_STATISTICS);
  return res.data;
};

export const getSupplierStats = async (): Promise<CustomerSupplierStatResponse> => {
  const res = await axios.get<CustomerSupplierStatResponse>(FINANCE_ENDPOINTS.SUPPLIER_STATISTICS);
  return res.data;
};

// ----------------------- 매입 전표(AP) -----------------------
export const getPurchaseInvoicesList = async (
  params?: InvoiceQueryParams,
): Promise<{ data: InvoiceListRes[]; pageData: Page }> => {
  const query = new URLSearchParams({
    ...(params?.status && { status: params.status }),
    ...(params?.page && { page: String(params.page) }),
    ...(params?.size && { size: String(params.size) }),
  }).toString();

  const res = await axios.get<{ content: InvoiceListRes[]; page: Page }>(
    `${FINANCE_ENDPOINTS.PURCHASE_INVOICES_LIST}?${query}`,
  );
  return { data: res.data.content, pageData: res.data.page };
};

export const getPurchaseInvoiceDetail = async (invoiceId: string): Promise<InvoicetDetailRes> => {
  const res = await axios.get<InvoicetDetailRes>(
    FINANCE_ENDPOINTS.PURCHASE_INVOICE_DETAIL(invoiceId),
  );
  return res.data;
};

export const postApInvoice = async (invoiceId: string): Promise<void> => {
  await axios.post(FINANCE_ENDPOINTS.PURCHASE_INVOICE_REQUEST(invoiceId));
};

export const postArInvoice = async (invoiceId: string): Promise<void> => {
  await axios.post(FINANCE_ENDPOINTS.SALES_INVOICE_COMPLETE(invoiceId));
};

export const postSupplierApInvoice = async (invoiceId: string): Promise<void> => {
  await axios.post(FINANCE_ENDPOINTS.SUPPLIER_AP_COMPLETE(invoiceId));
};

// ----------------------- 매출 전표(AS) -----------------------
export const getSalesInvoicesList = async (
  params?: InvoiceQueryParams,
): Promise<{ data: InvoiceListRes[]; pageData: Page }> => {
  const query = new URLSearchParams({
    ...(params?.status && { status: params.status }),
    ...(params?.page && { page: String(params.page) }),
    ...(params?.size && { size: String(params.size) }),
  }).toString();

  const res = await axios.get<{ content: InvoiceListRes[]; page: Page }>(
    `${FINANCE_ENDPOINTS.SALES_INVOICES_LIST}?${query}`,
  );
  return { data: res.data.content, pageData: res.data.page };
};

export const getSalesInvoiceDetail = async (invoiceId: string): Promise<InvoicetDetailRes> => {
  const res = await axios.get<InvoicetDetailRes>(FINANCE_ENDPOINTS.SALES_INVOICE_DETAIL(invoiceId));
  return res.data;
};
