import { Stat, StatResponse } from '@/app/types/StatType';

interface InventoryStat {
  total_stock: Stat;
  store_complete: Stat;
  store_pending: Stat;
  delivery_complete: Stat;
  delivery_pending: Stat;
}

export type InventoryStatResponse = StatResponse<InventoryStat>;
