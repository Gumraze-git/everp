import { Stat, StatResponse } from '@/app/types/StatType';

// 인적자원관리 지표 api 응답 데이터 구조
export interface HrmStat {
  totalEmployeeCount: Stat;
  newEmployeeCount: Stat;
}

export type HrmStatResponse = StatResponse<HrmStat>;
