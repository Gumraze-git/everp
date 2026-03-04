export interface LowStockListResponse {
  itemId: string;
  itemName: string;
  itemNumber: string;
  category: string;
  currentStock: number;
  uomName: string;
  safetyStock: number;
  unitPrice: number;
  totalAmount: number;
  warehouseName: string;
  warehouseNumber: string;
  statusCode: 'NORMAL' | 'CAUTION' | 'URGENT';
}

export interface LowStockListQueryParams {
  statusCode?: string;
  page?: number;
  size?: number;
}
