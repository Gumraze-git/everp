import Providers from '@/app/providers';
import { getQueryClient } from '@/lib/queryClient';
import { dehydrate } from '@tanstack/react-query';
import { Suspense } from 'react';

import {
  mapPurchaseStatsToCards,
  mapSupplierPurchaseStatsToCards,
} from '@/app/(private)/purchase/services/purchase.service';
import { PURCHASE_TABS, SUPPLIER_PURCHASE_TABS } from '@/app/(private)/purchase/constants';
import TabNavigation from '@/app/components/common/TabNavigation';
import StatSection from '@/app/components/common/StatSection';
import ErrorMessage from '@/app/components/common/ErrorMessage';
import {
  fetchPurchaseOrderList,
  fetchPurchaseOrderSearchTypeDropdown,
  fetchPurchaseOrderStatusDropdown,
  fetchPurchaseReqList,
  fetchPurchaseRequisitionSearchTypeDropdown,
  fetchPurchaseRequisitionStatusDropdown,
  fetchPurchaseStats,
  fetchSupplierCategoryDropdown,
  fetchSupplierOrdersPurchaseStats,
  fetchSupplierSearchTypeDropdown,
  fetchSupplierStatusDropdown,
} from '@/app/(private)/purchase/api/purchase.api';
import {
  FetchPurchaseOrderParams,
  PurchaseReqParams,
} from '@/app/(private)/purchase/types/PurchaseApiRequestType';
import { cookies } from 'next/headers';
import { StatCardType } from '@/app/types/StatType';

export default async function PurchasePage() {
  const cookieStore = await cookies();
  const role = cookieStore.get('role')?.value ?? null;

  const isSupplier = role === 'SUPPLIER_ADMIN';
  const queryClient = getQueryClient();

  if (!isSupplier) {
    const initialPurchaseReqParams: PurchaseReqParams = {
      statusCode: '',
      type: '',
      keyword: '',
      startDate: '',
      endDate: '',
      page: 0,
      size: 10,
    };

    await Promise.all([
      // 구매요청 탭
      queryClient.prefetchQuery({
        queryKey: ['purchaseRequests', initialPurchaseReqParams],
        queryFn: () => fetchPurchaseReqList(initialPurchaseReqParams),
      }),
      // --- 드롭다운 prefetch ---
      // 구매요청
      queryClient.prefetchQuery({
        queryKey: ['purchaseRequisitionSearchTypeDropdown'],
        queryFn: fetchPurchaseRequisitionSearchTypeDropdown,
      }),
      queryClient.prefetchQuery({
        queryKey: ['purchaseRequisitionStatusDropdown'],
        queryFn: fetchPurchaseRequisitionStatusDropdown,
      }),
      // 발주서
      queryClient.prefetchQuery({
        queryKey: ['purchaseOrderSearchTypeDropdown'],
        queryFn: fetchPurchaseOrderSearchTypeDropdown,
      }),
      queryClient.prefetchQuery({
        queryKey: ['purchaseOrderStatusDropdown'],
        queryFn: fetchPurchaseOrderStatusDropdown,
      }),
      // 공급업체
      queryClient.prefetchQuery({
        queryKey: ['supplierCategoryDropdown'],
        queryFn: fetchSupplierCategoryDropdown,
      }),
      queryClient.prefetchQuery({
        queryKey: ['supplierSearchTypeDropdown'],
        queryFn: fetchSupplierSearchTypeDropdown,
      }),
      queryClient.prefetchQuery({
        queryKey: ['supplierStatusDropdown'],
        queryFn: fetchSupplierStatusDropdown,
      }),
    ]);
  } else {
    // SUPPLIER_ADMIN인 경우
    const initialPurchaseOrderParams: FetchPurchaseOrderParams = {
      statusCode: 'ALL',
      type: '',
      keyword: '',
      page: 0,
      size: 10,
      startDate: '',
      endDate: '',
    };

    await Promise.all([
      // 발주서 목록 prefetch
      queryClient.prefetchQuery({
        queryKey: ['purchaseOrders', initialPurchaseOrderParams],
        queryFn: () => fetchPurchaseOrderList(initialPurchaseOrderParams),
      }),
      // 발주서 드롭다운 prefetch
      queryClient.prefetchQuery({
        queryKey: ['purchaseOrderSearchTypeDropdown'],
        queryFn: fetchPurchaseOrderSearchTypeDropdown,
      }),
      queryClient.prefetchQuery({
        queryKey: ['purchaseOrderStatusDropdown'],
        queryFn: fetchPurchaseOrderStatusDropdown,
      }),
    ]);
  }

  const dehydratedState = dehydrate(queryClient);

  let statsData: Record<string, StatCardType[]> | null = null;

  // 공급업체 분기
  if (isSupplier) {
    const data = await fetchSupplierOrdersPurchaseStats();
    if (data) {
      statsData = mapSupplierPurchaseStatsToCards(data);
    }
  } else {
    const data = await fetchPurchaseStats();
    if (data) {
      statsData = mapPurchaseStatsToCards(data);
    }
  }
  return (
    <Providers dehydratedState={dehydratedState}>
      <div className="min-h-screen bg-gray-50">
        <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          {statsData ? (
            //구매 관리 주요 지표
            <StatSection
              title={isSupplier ? '영업관리' : '구매 및 조달 관리'} // 공급사인 경우 분기
              subTitle={isSupplier ? '발주서 관리' : '구매 요청부터 발주까지 전체 프로세스 관리'} // 공급사인 경우 분기
              statsData={statsData}
            />
          ) : (
            <ErrorMessage message={'구매 통계 데이터를 불러오는데 실패했습니다.'} />
          )}

          <Suspense fallback={<div>Loading...</div>}>
            {isSupplier ? (
              //  구매 관리 탭 / 발주서 탭 / 공급업체 탭
              <TabNavigation tabs={SUPPLIER_PURCHASE_TABS} />
            ) : (
              // 발주서 탭
              <TabNavigation tabs={PURCHASE_TABS} />
            )}
          </Suspense>
        </main>
      </div>
    </Providers>
  );
}
