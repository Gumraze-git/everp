'use client';

import { Suspense } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getCustomerStats, getFinanceStats, getSupplierStats } from './finance.api';
import { mapCustomerSupplierStatsToCards, mapFinanceStatsToCards } from './finance.service';
import StatSection from '@/app/components/common/StatSection';
import FinanceTabs from './components/tabs/FinanceTabs';
import { EMPTY_STAT_CARDS_BY_PERIOD } from '@/app/types/StatType';
import { useAuthStore } from '@/store/authStore';

export default function FinancePage() {
  const authStatus = useAuthStore((state) => state.authStatus);
  const role = useAuthStore((state) => state.userInfo?.userRole);
  const isAuthenticated = authStatus === 'authenticated';

  const financeStatsQuery = useQuery({
    queryKey: ['financePageStats', role],
    queryFn: async () => {
      if (role === 'CUSTOMER_ADMIN') {
        return { kind: 'customer' as const, data: await getCustomerStats() };
      }

      if (role === 'SUPPLIER_ADMIN') {
        return { kind: 'supplier' as const, data: await getSupplierStats() };
      }

      return { kind: 'finance' as const, data: await getFinanceStats() };
    },
    enabled: isAuthenticated,
  });

  const financeStatsData = financeStatsQuery.data
    ? financeStatsQuery.data.kind === 'finance'
      ? mapFinanceStatsToCards(financeStatsQuery.data.data)
      : mapCustomerSupplierStatsToCards(financeStatsQuery.data.data)
    : EMPTY_STAT_CARDS_BY_PERIOD;

  return (
    <div className="min-h-screen bg-gray-50">
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <StatSection title="재무 관리" subTitle="전표 관리 및 재무 현황" statsData={financeStatsData} />
        <Suspense fallback={<div>Loading...</div>}>
          <FinanceTabs />
        </Suspense>
      </main>
    </div>
  );
}
