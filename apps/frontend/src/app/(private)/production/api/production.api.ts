import { ProductionStatResponse } from '@/app/(private)/production/types/ProductionStatsType';
import { INVENTORY_ENDPOINTS } from '@/app/types/api';
import { PRODUCTION_ENDPOINTS } from '@/app/(private)/production/api/production.endpoints';
import {
  FetchQuotationSimulationParams,
  QuotationSimulationResponse,
} from '@/app/(private)/production/types/QuotationSimulationApiType';
import { QuotationPreviewResponse } from '@/app/(private)/production/types/QuotationPreviewApiType';
import { MpsListParams, MpsListResponse } from '@/app/(private)/production/types/MpsApiType';
import { FetchMesListParams, MesListResponse } from '../types/MesListApiType';
import { MesDetailResponse } from '../types/MesDetailApiType';
import { BomListResponse } from '../types/BomListApiType';
import { BomDetailResponse } from '../types/BomDetailApiType';
import { FetchQuotationParams, QuotationListResponse } from '../types/QuotationApiType';
import {
  FetchMrpPlannedOrdersListParams,
  MrpPlannedOrdersListResponse,
} from '../types/MrpPlannedOrdersListApiType';
import { MrpPlannedOrdersDetailResponse } from '../types/MrpPlannedOrdersDetailApiType';
import { KeyValueItem } from '@/app/types/CommonType';
import { BomRequestBody, MaterialResponse } from '../types/BomType';
import { PageRequest } from '@/app/types/Page';
import { MrpOrdersConvertReqeustBody } from '../types/MrpOrdersConvertApiType';
import { ItemResponse } from '../../inventory/types/ItemListType';
import { FetchMrpOrdersListParams, MrpOrdersListResponse } from '../types/MrpOrdersApiType';
import axios from '@/lib/axiosInstance';

// --- 상단 섹션 ---
// 구매 관리 지표
export const fetchProductionStats = async (): Promise<ProductionStatResponse | null> => {
  try {
    const res = await axios.get<ProductionStatResponse>(`${PRODUCTION_ENDPOINTS.STATISTICS}`);
    return res.data;
  } catch (error) {
    console.log(error);
    return null;
  }
};

// --- 견적 ---
// 견적 목록 조회
export const fetchQuotationList = async (
  params: FetchQuotationParams,
): Promise<QuotationListResponse> => {
  const res = await axios.get<QuotationListResponse>(`${PRODUCTION_ENDPOINTS.QUOTATIONS}`, {
    params,
  });
  return res.data;
};

// 견적에 대한 ATP(Available to Promise), MPS, MRP 시뮬레이션 실행 결과
export const fetchQuotationSimulationResult = async (
  params: FetchQuotationSimulationParams,
): Promise<QuotationSimulationResponse> => {
  const { quotationIds, page, size } = params;
  const query = new URLSearchParams({
    page: String(page),
    size: String(size),
  });
  const res = await axios.post<QuotationSimulationResponse>(
    `${PRODUCTION_ENDPOINTS.QUOTATION_SIMULATE}?${query.toString()}`,
    { quotationIds },
  );
  return res.data;
};

// 제안 납기 계획 프리뷰 조회
export const fetchQuotationPreview = async (
  params: string[],
): Promise<QuotationPreviewResponse> => {
  const res = await axios.post<QuotationPreviewResponse>(
    `${PRODUCTION_ENDPOINTS.QUOTATION_PREVIEW}`,
    params,
  );
  return res.data;
};

// 제품별 Master Production Schedule(MPS) 정보를 조회
export const fetchMpsList = async (params: MpsListParams): Promise<MpsListResponse> => {
  const res = await axios.get<MpsListResponse>(`${PRODUCTION_ENDPOINTS.MPS_PLANS}`, {
    params,
  });
  return res.data;
};

// 제안 납기 확정
export const fetchQuotationConfirm = async (params: string[]): Promise<void> => {
  await axios.post(`${PRODUCTION_ENDPOINTS.QUOTATION_CONFIRM}`, {
    quotationIds: params,
  });
};

// --- BOM ---
// BOM 목록 조회
export const fetchBomList = async (params: PageRequest): Promise<BomListResponse> => {
  const res = await axios.get<BomListResponse>(`${PRODUCTION_ENDPOINTS.BOMS}`, {
    params,
  });
  return res.data;
};

// BOM 자재 조회
export const fetchProduction = async (productId: string): Promise<MaterialResponse> => {
  const res = await axios.get<MaterialResponse>(
    `${PRODUCTION_ENDPOINTS.PRODUCTS_DETAIL(productId)}`,
  );
  return res.data;
};

// BOM 공정 조회
export const fetchOperationDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<KeyValueItem[]>(`${PRODUCTION_ENDPOINTS.OPERATIONS_DROPDOWN}`);
  return res.data;
};

// BOM 상세 조회
export const fetchBomDetail = async (bomId: string): Promise<BomDetailResponse> => {
  const res = await axios.get<BomDetailResponse>(`${PRODUCTION_ENDPOINTS.BOM_DETAIL(bomId)}`);
  return res.data;
};

// BOM 추가
export const postBomItem = async (body: BomRequestBody): Promise<void> => {
  await axios.post(`${PRODUCTION_ENDPOINTS.BOMS}`, body);
};

// BOM 삭제
export const deletBomItem = async (bomId: string): Promise<void> => {
  await axios.delete(`${PRODUCTION_ENDPOINTS.BOM_DETAIL(bomId)}`);
};

// --- MRP ---
// MRP 순소요 목록 조회
export const fetchMrpOrdersList = async (
  params: FetchMrpOrdersListParams,
): Promise<MrpOrdersListResponse> => {
  const res = await axios.get<MrpOrdersListResponse>(`${PRODUCTION_ENDPOINTS.MRP_ORDERS}`, {
    params,
  });
  return res.data;
};

// MRP 계획 주문 전환
export const postMrpConvert = async (body: MrpOrdersConvertReqeustBody): Promise<void> => {
  await axios.post(`${PRODUCTION_ENDPOINTS.MRP_CONVERT}`, body);
};

// MRP 계획 주문 목록 조회
export const fetchMrpPlannedOrdersList = async (
  params: FetchMrpPlannedOrdersListParams,
): Promise<MrpPlannedOrdersListResponse> => {
  const res = await axios.get<MrpPlannedOrdersListResponse>(
    `${PRODUCTION_ENDPOINTS.MRP_PLANNED_ORDERS_LIST}`,
    { params },
  );
  return res.data;
};

// MRP 계획 주문 상세 조회
export const fetchMrpPlannedOrdersDetail = async (
  mrpId: string,
): Promise<MrpPlannedOrdersDetailResponse> => {
  const res = await axios.get<MrpPlannedOrdersDetailResponse>(
    `${PRODUCTION_ENDPOINTS.MRP_PLANNED_ORDER_DETAIL(mrpId)}`,
  );
  return res.data;
};

// 자재 상세 조회
export const postItemsInfo = async (body: string[]): Promise<ItemResponse[]> => {
  const res = await axios.post<ItemResponse[]>(`${INVENTORY_ENDPOINTS.MATERIALS_LIST}`, {
    itemIds: body,
  });
  return res.data;
};

// --- MES ---
// MES(Manufacturing Execution System) 작업 목록 조회
export const fetchMesList = async (params: FetchMesListParams): Promise<MesListResponse> => {
  const res = await axios.get<MesListResponse>(`${PRODUCTION_ENDPOINTS.MES_LIST}`, {
    params,
  });
  return res.data;
};

// MES 작업 상세 정보 조회
export const fetchMesDetail = async (mesId: string) => {
  const res = await axios.get<MesDetailResponse>(
    `${PRODUCTION_ENDPOINTS.MES_WORK_ORDER_DETAIL(mesId)}`,
  );
  return res.data;
};

// MES 시작
export const startMes = async (mesId: string): Promise<void> => {
  await axios.post(PRODUCTION_ENDPOINTS.MES_START(mesId));
};

// MES 완료
export const completeMes = async (mesId: string): Promise<void> => {
  await axios.post(PRODUCTION_ENDPOINTS.MES_COMPLETE(mesId));
};

// 공정 시작
export const startMesOperation = async (mesId: string, operationId: string): Promise<void> => {
  await axios.post(PRODUCTION_ENDPOINTS.MES_OPERATION_START(mesId, operationId));
};

// 공정 완료
export const completeMesOperation = async (mesId: string, operationId: string): Promise<void> => {
  await axios.post(PRODUCTION_ENDPOINTS.MES_OPERATION_COMPLETE(mesId, operationId));
};

// --- 드롭다운 조회 ---
// mps 제품 드롭다운
export const fetchMpsBomDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<KeyValueItem[]>(`${PRODUCTION_ENDPOINTS.MPS_TOGGLE_PRODUCTS}`);
  return res.data;
};

export const fetchProductDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<KeyValueItem[]>(`${PRODUCTION_ENDPOINTS.PRODUCTS}`);
  return res.data;
};

export const fetchAvailableStatusDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<KeyValueItem[]>(`${PRODUCTION_ENDPOINTS.AVAILABLE_STATUS_DROPDOWN}`);
  return res.data;
};

export const fetchQuotationStatusDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<KeyValueItem[]>(`${PRODUCTION_ENDPOINTS.QUOTATION_STATUS_DROPDOWN}`);
  return res.data;
};

// mrp 순소요 - 견적 드롭다운
export const fetchMrpQuotationsDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<KeyValueItem[]>(`${PRODUCTION_ENDPOINTS.MRP_QUOTATION_DROPDOWN}`);
  return res.data;
};

// mrp 순소요 - 가용 재고 상태 드롭다운
export const fetchMrpAvailableStatusDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<KeyValueItem[]>(
    `${PRODUCTION_ENDPOINTS.MRP_AVAILABLE_STATUS_DROPDOWN}`,
  );
  return res.data;
};

// mrp 계획 주문 -견적 드롭다운
export const fetchMrpPlannedOrderQuotationsDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<KeyValueItem[]>(
    `${PRODUCTION_ENDPOINTS.MRP_RUNS_QUOTATIONS_DROPDOWN}`,
  );
  return res.data;
};

// mrp 계획 주문 - 상태 드롭다운
export const fetchMrpPlannedOrderStatusDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<KeyValueItem[]>(`${PRODUCTION_ENDPOINTS.MRP_RUNS_STATUS_DROPDOWN}`);
  return res.data;
};

// mes 상태 드롭다운
export const fetchMesStatusDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<KeyValueItem[]>(`${PRODUCTION_ENDPOINTS.MES_STATUS_DROPDOWN}`);
  return res.data;
};
