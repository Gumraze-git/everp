import { Stat, StatResponse } from '@/app/types/StatType';

interface DashboardStat {
  total_sales: Stat;
  total_purchases: Stat;
  net_profit: Stat;
  total_employees: Stat;
}

export type DashboardStatRes = StatResponse<DashboardStat>;
