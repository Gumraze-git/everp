export type CustomerStatus = 'ALL' | 'ACTIVE' | 'DEACTIVE';
export interface SalesCustomer {
  customerId: string;
  customerNumber: string;
  customerName: string;
  manager: {
    managerName: string;
    managerPhone: string;
    managerEmail: string;
  };
  address: string;
  totalTransactionAmount: number;
  orderCount: number;
  lastOrderDate: string;
  statusCode: string;
}

export interface CustomerQueryParams {
  status?: string;
  keyword?: string;
  page?: number;
  size?: number;
  type?: string;
}
