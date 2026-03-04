import { Suspense } from 'react';
import { dehydrate } from '@tanstack/react-query';
import { getQueryClient } from '@/lib/queryClient';
import {
  getCustomerStats,
  getFinanceStats,
  getSalesInvoicesList,
  getSupplierStats,
} from '@/app/(private)/finance/finance.api';
import { InvoiceQueryParams } from './types/InvoiceListType';
import Providers from '@/app/providers';
import ErrorMessage from '@/app/components/common/ErrorMessage';
import { mapCustomerSupplierStatsToCards, mapFinanceStatsToCards } from './finance.service';
import StatSection from '@/app/components/common/StatSection';
import FinanceTabs from './components/tabs/FinanceTabs';
import { cookies } from 'next/headers';

export default async function FinancePage() {
  const cookieStore = await cookies();
  const role = cookieStore.get('role')?.value ?? null;

  const queryClient = getQueryClient();

  if (role === 'CUSTOMER_ADMIN') {
    await queryClient.prefetchQuery({
      queryKey: ['customerStats'],
      queryFn: getCustomerStats,
    });
  } else if (role === 'SUPPLIER_ADMIN') {
    await queryClient.prefetchQuery({
      queryKey: ['supplierStats'],
      queryFn: getSupplierStats,
    });
  } else {
    await queryClient.prefetchQuery({
      queryKey: ['financeStats'],
      queryFn: getFinanceStats,
    });
  }

  await queryClient.prefetchQuery({
    queryKey: ['salesInvoiceList', { page: 0, size: 10, status: 'ALL' }],
    queryFn: ({ queryKey }) => getSalesInvoicesList(queryKey[1] as InvoiceQueryParams),
  });

  let res;
  let financeStatsData;
  if (role === 'CUSTOMER_ADMIN') {
    res = await getCustomerStats();
    financeStatsData = mapCustomerSupplierStatsToCards(res.data);
  } else if (role === 'SUPPLIER_ADMIN') {
    res = await getSupplierStats();
    financeStatsData = mapCustomerSupplierStatsToCards(res.data);
  } else {
    res = await getFinanceStats();
    financeStatsData = mapFinanceStatsToCards(res.data);
  }

  const dehydratedState = dehydrate(queryClient);

  return (
    <Providers dehydratedState={dehydratedState}>
      <div className="min-h-screen bg-gray-50">
        <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          {/* 페이지 헤더 */}
          {res.success ? (
            <StatSection
              title="재무 관리"
              subTitle="전표 관리 및 재무 현황"
              statsData={financeStatsData}
            />
          ) : (
            <ErrorMessage message={'재무 통계 데이터를 불러오는데 실패했습니다.'} />
          )}

          {/* 탭 네비게이션 */}
          <Suspense fallback={<div>Loading...</div>}>
            <FinanceTabs />
          </Suspense>
        </main>
      </div>
    </Providers>
  );
}
