export interface AddInventoryItemsToggleResponse {
  itemId: string;
  unitPrice: number;
  supplierCompanyName: string;
  uomName: string;
  supplierCompanyId: string;
  itemName: string;
}

export interface WarehouseToggleResponse {
  warehouseId: string;
  warehouseName: string;
  warehouseNumber: string;
}

export interface AddInventoryItemsRequest {
  itemId: string;
  safetyStock: number;
  currentStock: number;
  warehouseId: string;
}

export interface WarehouseToggleQueryParams {
  warehouseId: string;
}
