import { Stat, StatResponse } from '@/app/types/StatType';

interface WarehouseStat {
  total_warehouse: Stat;
  in_operation_warehouse: Stat;
}

export type WarehouseStatResponse = StatResponse<WarehouseStat>;
