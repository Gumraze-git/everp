import { Page, PageRequest } from '@/app/types/Page';

export interface BomListData {
  bomId: string;
  bomNumber: string;
  productId: string;
  productNumber: string;
  productName: string;
  version: string;
  statusCode: string;
  lastModifiedAt: string;
}
export interface BomListResponse {
  page: Page;
  content: BomListData[];
}

export type BomListRequestParams = PageRequest;
