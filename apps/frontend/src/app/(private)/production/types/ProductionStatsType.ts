import { Stat, StatResponse } from '@/app/types/StatType';

// 생산 지표 api 응답 데이터 구조
export interface ProductionStat {
  production_in: Stat;
  production_completed: Stat;
  bom_count: Stat;
}

export type ProductionStatResponse = StatResponse<ProductionStat>;
