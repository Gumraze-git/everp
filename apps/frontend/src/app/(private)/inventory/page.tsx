'use client';

import StatSection from '@/app/components/common/StatSection';
import { Suspense } from 'react';
import { getInventoryStats } from '@/app/(private)/inventory/inventory.api';
import { mapInventoryStatsToCards } from './inventory.service';
import { INVENTORY_TABS } from '@/app/types/componentConstant';
import TabNavigation from '@/app/components/common/TabNavigation';
import { useQuery } from '@tanstack/react-query';
import { EMPTY_STAT_CARDS_BY_PERIOD } from '@/app/types/StatType';
import { useAuthStore } from '@/store/authStore';

export default function InventoryPage() {
  const authStatus = useAuthStore((state) => state.authStatus);
  const isAuthenticated = authStatus === 'authenticated';

  const inventoryStatsQuery = useQuery({
    queryKey: ['inventoryStats'],
    queryFn: getInventoryStats,
    enabled: isAuthenticated,
  });

  const inventoryStatsData = inventoryStatsQuery.data
    ? mapInventoryStatsToCards(inventoryStatsQuery.data)
    : EMPTY_STAT_CARDS_BY_PERIOD;

  return (
    <div className="min-h-screen bg-gray-50">
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <StatSection
          title="재고 관리"
          subTitle="재고 현황 및 입출고 관리"
          statsData={inventoryStatsData}
        />
        <Suspense fallback={<div>Loading...</div>}>
          <TabNavigation tabs={INVENTORY_TABS} />
        </Suspense>
      </main>
    </div>
  );
}
