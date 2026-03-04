import Link from 'next/link';
import WarehouseList from './components/WarehouseList';
import { Suspense } from 'react';
import StatSection from '@/app/components/common/StatSection';
import { dehydrate } from '@tanstack/react-query';
import { getQueryClient } from '@/lib/queryClient';
import { WarehouseListQueryParams } from './types/WarehouseListType';
import Providers from '@/app/providers';
import { getWarehouseList, getWarehouseStats } from './warehouse.api';
import { mapWarehouseStatsToCards } from './warehouse.service';

export default async function WarehousePage() {
  const queryClient = getQueryClient();

  await queryClient.prefetchQuery({
    queryKey: ['warehouseStats'],
    queryFn: getWarehouseStats,
  });

  await queryClient.prefetchQuery({
    queryKey: ['warehouseList', { page: 0, size: 10 }],
    queryFn: ({ queryKey }) => getWarehouseList(queryKey[1] as WarehouseListQueryParams),
  });

  const warehouseStats = await getWarehouseStats();
  const warehouseStatsData = mapWarehouseStatsToCards(warehouseStats);

  const dehydratedState = dehydrate(queryClient);
  return (
    <Providers dehydratedState={dehydratedState}>
      <div className="min-h-screen bg-gray-50">
        <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          {/* 페이지 헤더 */}
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
    </Providers>
  );
}
