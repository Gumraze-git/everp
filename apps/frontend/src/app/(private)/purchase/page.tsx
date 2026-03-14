'use client';

import { Suspense } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  mapPurchaseStatsToCards,
  mapSupplierPurchaseStatsToCards,
} from '@/app/(private)/purchase/services/purchase.service';
import { PURCHASE_TABS, SUPPLIER_PURCHASE_TABS } from '@/app/(private)/purchase/constants';
import TabNavigation from '@/app/components/common/TabNavigation';
import StatSection from '@/app/components/common/StatSection';
import ErrorMessage from '@/app/components/common/ErrorMessage';
import {
  fetchPurchaseStats,
  fetchSupplierOrdersPurchaseStats,
} from '@/app/(private)/purchase/api/purchase.api';
import { EMPTY_STAT_CARDS_BY_PERIOD } from '@/app/types/StatType';
import { useAuthStore } from '@/store/authStore';

export default function PurchasePage() {
  const authStatus = useAuthStore((state) => state.authStatus);
  const role = useAuthStore((state) => state.userInfo?.userRole);
  const isAuthenticated = authStatus === 'authenticated';
  const isSupplier = role === 'SUPPLIER_ADMIN';

  const purchaseStatsQuery = useQuery({
    queryKey: ['purchasePageStats', role],
    queryFn: async () => {
      if (isSupplier) {
        return { kind: 'supplier' as const, data: await fetchSupplierOrdersPurchaseStats() };
      }

      return { kind: 'purchase' as const, data: await fetchPurchaseStats() };
    },
    enabled: isAuthenticated,
  });

  const statsData =
    purchaseStatsQuery.data?.data == null
      ? EMPTY_STAT_CARDS_BY_PERIOD
      : purchaseStatsQuery.data.kind === 'supplier'
        ? mapSupplierPurchaseStatsToCards(purchaseStatsQuery.data.data)
        : mapPurchaseStatsToCards(purchaseStatsQuery.data.data);
  const hasStatsError =
    purchaseStatsQuery.isError ||
    (purchaseStatsQuery.isSuccess && purchaseStatsQuery.data?.data == null);

  return (
    <div className="min-h-screen bg-gray-50">
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {hasStatsError ? (
          <ErrorMessage message="구매 통계 데이터를 불러오는데 실패했습니다." />
        ) : (
          <StatSection
            title={isSupplier ? '영업관리' : '구매 및 조달 관리'}
            subTitle={isSupplier ? '발주서 관리' : '구매 요청부터 발주까지 전체 프로세스 관리'}
            statsData={statsData}
          />
        )}

        <Suspense fallback={<div>Loading...</div>}>
          {isSupplier ? <TabNavigation tabs={SUPPLIER_PURCHASE_TABS} /> : <TabNavigation tabs={PURCHASE_TABS} />}
        </Suspense>
      </main>
    </div>
  );
}
