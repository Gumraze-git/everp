export interface Page {
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
}

export interface PageRequest {
  page?: number;
  size?: number;
}
