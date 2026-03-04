export interface ProductionListResponse {
  salesOrderId: string;
  salesOrderNumber: string;
  customerName: string;
  orderDate: string;
  dueDate: string;
  progress: number;
  totalAmount: number;
  statusCode: 'PRODUCTION';
}

export interface ReadyToShipListResponse {
  salesOrderId: string;
  salesOrderNumber: string;
  customerName: string;
  orderDate: string;
  dueDate: string;
  productionCompletionDate: string;
  readyToShipDate: string;
  totalAmount: number;
  statusCode: 'READT_TO_SHIP';
}

export interface ManageMentCommonQueryParams {
  page?: number;
  size?: number;
  startDate?: string;
  endDate?: string;
}
