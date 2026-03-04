import StatSection from '@/app/components/common/StatSection';
import TabNavigation from '@/app/components/common/TabNavigation';
import Providers from '@/app/providers';
import { getQueryClient } from '@/lib/queryClient';
import { dehydrate } from '@tanstack/react-query';
import { Suspense } from 'react';
import {
  getCurrentStockMovement,
  getInventoryList,
  getInventoryStats,
  getLowStockItems,
} from '@/app/(private)/inventory/inventory.api';
import { mapInventoryStatsToCards } from './inventory.service';
import { InventoryQueryParams } from './types/InventoryListType';
import { INVENTORY_TABS } from '@/app/types/componentConstant';
import WarehouseVisualizer from '../warehouse/components/warehouseVisualizer/components/WarehouseVisualizer';

export default async function InventoryPage() {
  const queryClient = getQueryClient();

  await queryClient.prefetchQuery({
    queryKey: ['inventoryStats'],
    queryFn: getInventoryStats,
  });

  await queryClient.prefetchQuery({
    queryKey: [
      'inventoryList',
      { page: 0, size: 10, category: '', warehouse: '', statusCode: 'ALL', itemName: '' },
    ],
    queryFn: ({ queryKey }) => getInventoryList(queryKey[1] as InventoryQueryParams),
  });

  await queryClient.prefetchQuery({
    queryKey: [
      'inventoryVisualizer',
      { page: 0, size: 60, statusCode: 'ALL', category: '', warehouse: '', itemName: '' },
    ],
    queryFn: () =>
      getInventoryList({
        statusCode: 'ALL',
        type: '',
        keyword: '',
        page: 0,
        size: 60,
      }),
  });

  await queryClient.prefetchQuery({
    queryKey: ['stockMovement'],
    queryFn: getCurrentStockMovement,
  });

  await queryClient.prefetchQuery({
    queryKey: ['lowStockItems'],
    queryFn: getLowStockItems,
  });

  const dehydratedState = dehydrate(queryClient);

  const inventoryStats = await getInventoryStats();
  const inventoryStatsData = mapInventoryStatsToCards(inventoryStats);

  return (
    <Providers dehydratedState={dehydratedState}>
      <div className="min-h-screen bg-gray-50">
        <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          {/* 페이지 헤더 */}
          <StatSection
            title="재고 관리"
            subTitle="재고 현황 및 입출고 관리"
            statsData={inventoryStatsData}
          />
          {/* 탭 콘텐츠 */}
          <Suspense fallback={<div>Loading...</div>}>
            <TabNavigation tabs={INVENTORY_TABS} />
          </Suspense>
        </main>
      </div>
    </Providers>
  );
}
