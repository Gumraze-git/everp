export interface CustomerDetail {
  customerId: string;
  customerNumber: string;
  customerName: string;
  ceoName: string;
  businessNumber: string;
  statusCode: string;
  customerPhone: string;
  customerEmail: string;
  baseAddress: string;
  detailAddress: string;
  manager: {
    managerName: string;
    managerPhone: string;
    managerEmail: string;
  };
  totalOrders: number;
  totalTransactionAmount: number;
  note: string;
}
