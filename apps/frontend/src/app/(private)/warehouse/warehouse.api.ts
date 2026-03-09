import { WarehouseStatResponse } from './types/WarehouseStatsType';
import axios from '@/lib/axiosInstance';
import { WarehouseListQueryParams, WarehouseListResponse } from './types/WarehouseListType';
import { WarehouseDetailResponse } from './types/WarehouseDetailModalType';
import { AddWarehouseRequest, WarehouseManagerInfoResponse } from './types/AddWarehouseModalType';
import { EditWarehouseRequest } from './types/ManageWarehouseModalType';
import { WAREHOUSE_ENDPOINTS } from '@/app/types/api';
import { Page } from '@/app/types/Page';

// ----------------------- 창고 관리 -----------------------
export const getWarehouseStats = async (): Promise<WarehouseStatResponse> => {
  const res = await axios.get<WarehouseStatResponse>(WAREHOUSE_ENDPOINTS.STATS);
  return res.data;
};

export const getWarehouseList = async (
  params?: WarehouseListQueryParams,
): Promise<{
  data: WarehouseListResponse[];
  pageData: Page;
}> => {
  const query = new URLSearchParams({
    ...(params?.page && { page: String(params.page) }),
    ...(params?.size && { size: String(params.size) }),
  }).toString();
  const res = await axios.get<{ content: WarehouseListResponse[]; page: Page }>(
    `${WAREHOUSE_ENDPOINTS.WAREHOUSE_LIST}?${query}`,
  );
  return { data: res.data.content, pageData: res.data.page };
};

export const getWarehouseDetail = async (warehouseId: string): Promise<WarehouseDetailResponse> => {
  const res = await axios.get<WarehouseDetailResponse>(
    WAREHOUSE_ENDPOINTS.WAREHOUSE_DETAIL(warehouseId),
  );
  return res.data;
};

export const postAddWarehouse = async (payload: AddWarehouseRequest): Promise<void> => {
  await axios.post(WAREHOUSE_ENDPOINTS.ADD_WAREHOUSE, payload);
};

export const getWarehouseManagerInfo = async (): Promise<WarehouseManagerInfoResponse[]> => {
  const res = await axios.get<WarehouseManagerInfoResponse[]>(
    WAREHOUSE_ENDPOINTS.WAREHOUSE_MANAGER_INFO,
  );
  return res.data;
};

export const patchManageWarehouse = async ({
  warehouseId,
  payload,
}: {
  warehouseId: string;
  payload: EditWarehouseRequest;
}): Promise<void> => {
  await axios.put(WAREHOUSE_ENDPOINTS.WAREHOUSE_MANAGE(warehouseId), payload);
};
