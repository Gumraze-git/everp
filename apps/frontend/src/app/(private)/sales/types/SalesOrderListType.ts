export interface OrderItem {
  name: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

export type OrderStatus =
  | 'ALL'
  | 'MATERIAL_PREPARATION'
  | 'IN_PRODUCTION'
  | 'READY_FOR_SHIPMENT'
  | 'DELIVERING'
  | 'DELIVERED';

export interface Order {
  salesOrderId: string;
  salesOrderNumber: string;
  customerName: string;
  manager: {
    managerName: string;
    managerPhone: string;
    managerEmail: string;
  };
  orderDate: string;
  dueDate: string;
  totalAmount: number;
  statusCode: OrderStatus;
}

export interface OrderQueryParams {
  start: string;
  end: string;
  keyword: string;
  type: string;
  status: string;
  page: number;
  size: number;
}
