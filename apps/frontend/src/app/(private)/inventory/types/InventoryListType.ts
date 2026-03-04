export interface InventoryResponse {
  itemId: string;
  itemNumber: string;
  itemName: string;
  category: string;
  currentStock: number;
  forShipmentStock: number;
  reservedStock: number;
  safetyStock: number;
  uomName: string;
  unitPrice: number;
  totalAmount: number;
  warehouseName: string;
  warehouseType: string;
  statusCode: string;
  shelfNumber?: number;
}

export interface InventoryQueryParams {
  statusCode?: string;
  type?: string;
  keyword?: string;
  page?: number;
  size?: number;
}
