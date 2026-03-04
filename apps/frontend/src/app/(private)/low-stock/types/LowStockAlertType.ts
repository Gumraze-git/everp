export interface LowStockItemResponse {
  itemId: string;
  itemName: string;
  currentStock: number;
  uomName: string;
  safetyStock: number;
  statusCode: string;
}
