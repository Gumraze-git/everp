'use client';

import { Suspense } from 'react';
import { useQuery } from '@tanstack/react-query';
import StatSection from '@/app/components/common/StatSection';
import SalesTabs from './components/tabs/SalesTabs';
import { getCustomerSalesStats, getSalesStats } from '@/app/(private)/sales/sales.api';
import {
  mapCustomerSalesStatsToCards,
  mapSalesStatsToCards,
} from '@/app/(private)/sales/sales.service';
import { EMPTY_STAT_CARDS_BY_PERIOD } from '@/app/types/StatType';
import { useAuthStore } from '@/store/authStore';

export default function SalesPage() {
  const authStatus = useAuthStore((state) => state.authStatus);
  const role = useAuthStore((state) => state.userInfo?.userRole);
  const isAuthenticated = authStatus === 'authenticated';
  const isCustomerAdmin = role === 'CUSTOMER_ADMIN';

  const salesStatsQuery = useQuery({
    queryKey: ['salesPageStats', role],
    queryFn: async () => {
      if (isCustomerAdmin) {
        return { kind: 'customer' as const, data: await getCustomerSalesStats() };
      }

      return { kind: 'sales' as const, data: await getSalesStats() };
    },
    enabled: isAuthenticated,
  });

  const salesStatsData = salesStatsQuery.data
    ? salesStatsQuery.data.kind === 'customer'
      ? mapCustomerSalesStatsToCards(salesStatsQuery.data.data)
      : mapSalesStatsToCards(salesStatsQuery.data.data)
    : EMPTY_STAT_CARDS_BY_PERIOD;

  return (
    <div className="min-h-screen bg-gray-50">
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <StatSection
          title={isCustomerAdmin ? '구매 관리' : '영업관리'}
          subTitle={
            isCustomerAdmin ? '주문, 견적 및 고객 관리 시스템' : '주문 및 고객 관리 시스템'
          }
          statsData={salesStatsData}
        />
        <Suspense fallback={<div>Loading...</div>}>
          <SalesTabs />
        </Suspense>
      </main>
    </div>
  );
}
