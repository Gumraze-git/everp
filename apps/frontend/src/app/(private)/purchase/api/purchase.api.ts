import { ApiResponse } from '@/app/types/api';
import {
  PurchaseStatResponse,
  SupplierPurchaseStatResponse,
} from '@/app/(private)/purchase/types/PurchaseStatsType';
import {
  PurchaseOrderDetailResponse,
  PurchaseOrderListResponse,
} from '@/app/(private)/purchase/types/PurchaseOrderType';
import {
  PurchaseReqDetailResponse,
  PurchaseReqListResponse,
} from '@/app/(private)/purchase/types/PurchaseReqType';
import {
  CreateSupplierRequest,
  ModSupplierRequestBody,
  SupplierDetailResponse,
  SupplierListResponse,
} from '@/app/(private)/purchase/types/SupplierType';
import {
  FetchPurchaseOrderParams,
  PurchaseReqParams,
  FetchSupplierListParams,
  PurchaseRequestBody,
  StockPurchaseRequestBody,
} from '@/app/(private)/purchase/types/PurchaseApiRequestType';
import { PURCHASE_ENDPOINTS } from '@/app/(private)/purchase/api/purchase.endpoints';
import { KeyValueItem } from '@/app/types/CommonType';
import axios from '@/lib/axiosInstance';

// 구매 관리 지표
export const fetchPurchaseStats = async (): Promise<PurchaseStatResponse | null> => {
  try {
    const res = await axios.get<ApiResponse<PurchaseStatResponse>>(
      `${PURCHASE_ENDPOINTS.STATISTICS}`,
    );
    return res.data.data;
    // console.log(res);
  } catch (error) {
    console.log(error);
    return null;
  }
};

export const fetchSupplierOrdersPurchaseStats =
  async (): Promise<SupplierPurchaseStatResponse | null> => {
    try {
      const res = await axios.get<ApiResponse<SupplierPurchaseStatResponse>>(
        `${PURCHASE_ENDPOINTS.SUPPLIER_ORDERS_STATISTICS}`,
      );
      return res.data.data;
    } catch (error) {
      console.log(error);
      return null;
    }
  };

// 구매 요청 목록
export const fetchPurchaseReqList = async (
  params: PurchaseReqParams,
): Promise<PurchaseReqListResponse> => {
  const res = await axios.get<ApiResponse<PurchaseReqListResponse>>(
    `${PURCHASE_ENDPOINTS.PURCHASE_REQUISITIONS}`,
    { params },
  );
  // console.log(res.data.data);
  return res.data.data;
};

// 구매 요청 승인
export const postApporvePurchaseReq = async (prId: string) => {
  const res = await axios.post<ApiResponse<null>>(
    `${PURCHASE_ENDPOINTS.PURCHASE_REQUISITION_RELEASE(prId)}`,
  );
  return res.data;
};

// 구매 요청 반려
export const postRejectPurchaseReq = async (prId: string, body: string) => {
  const res = await axios.post<ApiResponse<null>>(
    `${PURCHASE_ENDPOINTS.PURCHASE_REQUISITION_REJECT(prId)}`,
    { comment: body },
  );
  return res.data;
};

// 구매 요청 상세정보
export const fetchPurchaseReqDetail = async (
  purchaseId: string,
): Promise<PurchaseReqDetailResponse> => {
  const res = await axios.get<ApiResponse<PurchaseReqDetailResponse>>(
    `${PURCHASE_ENDPOINTS.PURCHASE_REQUISITION_DETAIL(purchaseId)}`,
  );

  // console.log(res.data.data);
  return res.data.data;
};

// 비재고성 구매 요청 등록
export const createPurchaseRequest = async (
  data: PurchaseRequestBody,
): Promise<ApiResponse<null>> => {
  const res = await axios.post<ApiResponse<null>>(
    `${PURCHASE_ENDPOINTS.PURCHASE_REQUISITIONS}`,
    data,
  );
  return res.data;
};

// 재고성 구매 요청 등록
export const createStockPurchaseRequest = async (data: StockPurchaseRequestBody) => {
  const res = await axios.post<ApiResponse<null>>(
    `${PURCHASE_ENDPOINTS.STOCK_PURCHASE_REQUISITIONS}`,
    data,
  );
  return res.data;
};

// 발주서 목록
export const fetchPurchaseOrderList = async (
  params: FetchPurchaseOrderParams,
): Promise<PurchaseOrderListResponse> => {
  const res = await axios.get<ApiResponse<PurchaseOrderListResponse>>(
    `${PURCHASE_ENDPOINTS.PURCHASE_ORDERS}`,
    { params },
  );
  // console.log(res.data.data);
  return res.data.data;
};

// 발주서 승인
export const postApprovePurchaseOrder = async (poId: string) => {
  const res = await axios.post<ApiResponse<null>>(
    `${PURCHASE_ENDPOINTS.PURCHASE_ORDER_APPROVE(poId)}`,
  );
  return res.data;
};

// 발주서 반려
export const postRejectPurchaseOrder = async (poId: string, body: string) => {
  const res = await axios.post<ApiResponse<null>>(
    `${PURCHASE_ENDPOINTS.PURCHASE_ORDER_REJECT(poId)},`,
    { body },
  );
  return res.data;
};

// 승인된 발주서 배송
export const postDeliveryStartOrder = async (purchaseOrderId: string) => {
  const res = await axios.post<ApiResponse<null>>(
    `${PURCHASE_ENDPOINTS.PURCHASE_ORDER_DELIVERY(purchaseOrderId)}`,
  );
  return res.data;
};

// 발주서 상세정보
export const fetchPurchaseOrderDetail = async (
  purchaseId: string,
): Promise<PurchaseOrderDetailResponse> => {
  const res = await axios.get<ApiResponse<PurchaseOrderDetailResponse>>(
    `${PURCHASE_ENDPOINTS.PURCHASE_ORDER_DETAIL(purchaseId)}`,
  );

  console.log(res.data.data);
  return res.data.data;
};

// 공급업체 목록
export const fetchSupplierList = async (
  params: FetchSupplierListParams = {},
): Promise<SupplierListResponse> => {
  const res = await axios.get<ApiResponse<SupplierListResponse>>(`${PURCHASE_ENDPOINTS.SUPPLIER}`, {
    params,
  });
  return res.data.data;
};

// 공급업체 상세정보
export const fetchSupplierDetail = async (supplierId: string): Promise<SupplierDetailResponse> => {
  const res = await axios.get<ApiResponse<SupplierDetailResponse>>(
    `${PURCHASE_ENDPOINTS.SUPPLIER_DETAIL(supplierId)}`,
  );

  // console.log(res.data.data);
  return res.data.data;
};

// 공급업체 등록
export const createSupplyRequest = async (
  data: CreateSupplierRequest,
): Promise<ApiResponse<null>> => {
  const res = await axios.post<ApiResponse<null>>(`${PURCHASE_ENDPOINTS.SUPPLIER}`, data);
  return res.data;
};

// 공급업체 수정
export const patchSupplyRequest = async (
  supplierId: string,
  body: ModSupplierRequestBody,
): Promise<ApiResponse<null>> => {
  const res = await axios.patch<ApiResponse<null>>(
    `${PURCHASE_ENDPOINTS.SUPPLIER_DETAIL(supplierId)}`,
    body,
  );
  return res.data;
};

// --- 드롭다운 ---
// 구매요청서 상태
export const fetchPurchaseRequisitionStatusDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<ApiResponse<KeyValueItem[]>>(
    PURCHASE_ENDPOINTS.PURCHASE_REQUISITION_STATUS_TOGGLE,
  );
  return res.data.data;
};

// 구매요청서 검색 타입
export const fetchPurchaseRequisitionSearchTypeDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<ApiResponse<KeyValueItem[]>>(
    PURCHASE_ENDPOINTS.PURCHASE_REQUISITION_SEARCH_TYPE_TOGGLE,
  );
  return res.data.data;
};

// 발주서 상태
export const fetchPurchaseOrderStatusDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<ApiResponse<KeyValueItem[]>>(
    PURCHASE_ENDPOINTS.PURCHASE_ORDER_STATUS_TOGGLE,
  );
  return res.data.data;
};

// 발주서 검색 타입
export const fetchPurchaseOrderSearchTypeDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<ApiResponse<KeyValueItem[]>>(
    PURCHASE_ENDPOINTS.PURCHASE_ORDER_SEARCH_TYPE_TOGGLE,
  );
  return res.data.data;
};

// 공급업체 카테고리
export const fetchSupplierCategoryDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<ApiResponse<KeyValueItem[]>>(
    PURCHASE_ENDPOINTS.SUPPLIER_CATEGORY_TOGGLE,
  );
  return res.data.data;
};

// 공급업체 검색 타입
export const fetchSupplierSearchTypeDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<ApiResponse<KeyValueItem[]>>(
    PURCHASE_ENDPOINTS.SUPPLIER_SEARCH_TYPE_TOGGLE,
  );
  return res.data.data;
};

// 공급업체 상태
export const fetchSupplierStatusDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<ApiResponse<KeyValueItem[]>>(
    PURCHASE_ENDPOINTS.SUPPLIER_STATUS_TOGGLE,
  );
  return res.data.data;
};
