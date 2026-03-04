export interface WarehouseListResponse {
  warehouseId: string;
  warehouseNumber: string;
  warehouseName: string;
  statusCode: 'ACTIVE' | 'INACTIVE' | string;
  warehouseType: string;
  location: string;
  manager: string;
  managerPhone: string;
}

export interface WarehouseListQueryParams {
  page?: number;
  size?: number;
}
