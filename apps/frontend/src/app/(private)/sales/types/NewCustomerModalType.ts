export interface CustomerData {
  companyName: string;
  businessNumber: string;
  ceoName: string;
  contactPhone: string;
  contactEmail: string;
  zipCode: string;
  address: string;
  detailAddress: string;
  manager: {
    name: string;
    mobile: string;
    email: string;
  };
  note: string;
}

export interface ServerResponse {
  status: number;
  success: boolean;
  message: string;
  data: CreateCustomerResponse;
}

export interface CreateCustomerResponse {
  customerId: number;
  customerCode: string;
  companyName: string;
  ceoName: string;
  businessNumber: string;
  statusCode: string;
  statusLabel: string;
  contactPhone: string;
  contactEmail: string;
  address: string;
  manager: {
    name: string;
    mobile: string;
    email: string;
  };
  totalOrders: number;
  totalTransactionAmount: number;
  currency: string;
  note: string;
  createdAt: string;
  updatedAt: string;
}
