export interface Period {
  start: string;
  end: string;
  weekStart: string;
  weekEnd: string;
  weekCount: number;
}

export interface Trend {
  year: number;
  month: number;
  week: number;
  sale: number;
  orderCount: number;
}

export interface TrendScale {
  sale: {
    min: number;
    max: number;
  };
  orderCount: {
    min: number;
    max: number;
  };
}

export interface ProductShare {
  productCode: string;
  productName: string;
  sale: number;
  saleShare: number;
}

export interface TopCustomer {
  customerCode: string;
  customerName: string;
  orderCount: number;
  sale: number;
  active: boolean;
}

export interface SalesAnalysis {
  period: Period;
  trend: Trend[];
  trendScale: TrendScale;
  productShare: ProductShare[];
  topCustomers: TopCustomer[];
}

export interface AnalyticsQueryParams {
  startDate?: string;
  endDate?: string;
}
