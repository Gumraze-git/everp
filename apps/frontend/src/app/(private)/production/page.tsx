'use client';

import { Suspense } from 'react';
import { useQuery } from '@tanstack/react-query';
import TabNavigation from '@/app/components/common/TabNavigation';
import { PRODUCTION_TABS } from '@/app/(private)/production/constants';
import StatSection from '@/app/components/common/StatSection';
import { fetchProductionStats } from '@/app/(private)/production/api/production.api';
import ErrorMessage from '@/app/components/common/ErrorMessage';
import { mapProductionStatsToCards } from '@/app/(private)/production/services/production.service';
import { EMPTY_STAT_CARDS_BY_PERIOD } from '@/app/types/StatType';
import { useAuthStore } from '@/store/authStore';

export default function ProductionPage() {
  const authStatus = useAuthStore((state) => state.authStatus);
  const isAuthenticated = authStatus === 'authenticated';

  const productionStatsQuery = useQuery({
    queryKey: ['productionStats'],
    queryFn: fetchProductionStats,
    enabled: isAuthenticated,
  });

  const productionStatsData = productionStatsQuery.data
    ? mapProductionStatsToCards(productionStatsQuery.data)
    : EMPTY_STAT_CARDS_BY_PERIOD;
  const hasStatsError =
    productionStatsQuery.isError || (productionStatsQuery.isSuccess && !productionStatsQuery.data);

  return (
    <div className="min-h-screen bg-gray-50">
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {hasStatsError ? (
          <ErrorMessage message="생산 통계 데이터를 불러오는데 실패했습니다." />
        ) : (
          <StatSection
            title="생산 관리"
            subTitle="견적, MPS, MRP, MES, BOM 등 생산 전반 관리"
            statsData={productionStatsData}
          />
        )}

        <Suspense fallback={<div>Loading..</div>}>
          <TabNavigation tabs={PRODUCTION_TABS} />
        </Suspense>
      </main>
    </div>
  );
}
