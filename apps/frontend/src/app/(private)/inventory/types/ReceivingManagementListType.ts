export interface ReceivedListResponse {
  purchaseOrderId: string;
  purchaseOrderNumber: string;
  supplierCompanyName: string;
  orderDate: string;
  dueDate: string;
  totalAmount: number;
  statusCode: 'PENDING' | 'RECEIVED';
}
