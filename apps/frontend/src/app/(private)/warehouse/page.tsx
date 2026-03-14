'use client';

import Link from 'next/link';
import WarehouseList from './components/WarehouseList';
import { Suspense } from 'react';
import StatSection from '@/app/components/common/StatSection';
import { getWarehouseStats } from './warehouse.api';
import { mapWarehouseStatsToCards } from './warehouse.service';
import { useQuery } from '@tanstack/react-query';
import { EMPTY_STAT_CARDS_BY_PERIOD } from '@/app/types/StatType';
import { useAuthStore } from '@/store/authStore';

export default function WarehousePage() {
  const authStatus = useAuthStore((state) => state.authStatus);
  const isAuthenticated = authStatus === 'authenticated';

  const warehouseStatsQuery = useQuery({
    queryKey: ['warehouseStats'],
    queryFn: getWarehouseStats,
    enabled: isAuthenticated,
  });

  const warehouseStatsData = warehouseStatsQuery.data
    ? mapWarehouseStatsToCards(warehouseStatsQuery.data)
    : EMPTY_STAT_CARDS_BY_PERIOD;

  return (
    <div className="min-h-screen bg-gray-50">
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <StatSection
          title="창고 관리"
          subTitle="창고 현황 및 관리"
          statsData={warehouseStatsData}
        />
        <div className="w-[100%] flex justify-end ">
          <Link
            href="/inventory"
            className="px-4 py-2  text-gray-600 hover:text-gray-900 cursor-pointer"
          >
            <i className="ri-arrow-go-back-line mr-2" />
            재고관리로 돌아가기
          </Link>
        </div>

        <Suspense fallback={<div>Loading...</div>}>
          <WarehouseList />
        </Suspense>
      </main>
    </div>
  );
}
