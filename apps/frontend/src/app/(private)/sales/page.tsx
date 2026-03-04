import { getQueryClient } from '@/lib/queryClient';
import { dehydrate } from '@tanstack/react-query';
import { Suspense } from 'react';
import Providers from '@/app/providers';
import {
  getCustomerSalesStats,
  getQuoteList,
  getSalesStats,
} from '@/app/(private)/sales/sales.api';
import { QuoteQueryParams } from '@/app/(private)/sales/types/SalesQuoteListType';
import StatSection from '@/app/components/common/StatSection';
import {
  mapCustomerSalesStatsToCards,
  mapSalesStatsToCards,
} from '@/app/(private)/sales/sales.service';
import TabNavigation from '@/app/components/common/TabNavigation';
import { SALES_TABS } from '@/app/types/componentConstant';
import SalesTabs from './components/tabs/SalesTabs';
import { cookies } from 'next/headers';

export default async function SalesPage() {
  const cookieStore = await cookies();
  const role = cookieStore.get('role')?.value ?? null;
  const queryClient = getQueryClient();

  if (role === 'CUSTOMER_ADMIN') {
    await queryClient.prefetchQuery({
      queryKey: ['customerStats'],
      queryFn: getCustomerSalesStats,
    });
  } else {
    await queryClient.prefetchQuery({
      queryKey: ['stats'],
      queryFn: getSalesStats,
    });
  }

  let res;
  let salesStatsData;
  if (role === 'CUSTOMER_ADMIN') {
    res = await getCustomerSalesStats();
    salesStatsData = mapCustomerSalesStatsToCards(res);
  } else {
    res = await getSalesStats();
    salesStatsData = mapSalesStatsToCards(res);
  }

  await queryClient.prefetchQuery({
    queryKey: [
      'quoteList',
      { page: 0, size: 10, startDate: '', endDate: '', status: 'ALL', search: '' },
    ],
    queryFn: ({ queryKey }) => getQuoteList(queryKey[1] as QuoteQueryParams),
  });

  const dehydratedState = dehydrate(queryClient);
  // const salesStatsData = mapSalesStatsToCards(salesStats);
  return (
    <Providers dehydratedState={dehydratedState}>
      <div className="min-h-screen bg-gray-50">
        <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          {/* 페이지 헤더 */}
          <StatSection
            title={role === 'CUSTOMER_ADMIN' ? '구매 관리' : '영업관리'}
            subTitle={
              role === 'CUSTOMER_ADMIN'
                ? '주문, 견적 및 고객 관리 시스템'
                : '주문 및 고객 관리 시스템'
            }
            statsData={salesStatsData}
          />
          {/* 탭 콘텐츠 */}
          <Suspense fallback={<div>Loading...</div>}>
            <SalesTabs />
          </Suspense>
        </main>
      </div>
    </Providers>
  );
}
