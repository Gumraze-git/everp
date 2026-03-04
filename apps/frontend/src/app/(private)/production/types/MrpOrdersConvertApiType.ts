export interface MrpOrdersSelectedItems {
  quotationId: string;
  itemId: string;
  quantity: number;
}

export interface MrpOrdersConvertReqeustBody {
  items: MrpOrdersSelectedItems[];
}
