'use client';

import { Suspense } from 'react';
import { useQuery } from '@tanstack/react-query';
import StatSection from '@/app/components/common/StatSection';
import { fetchHrmStats } from '@/app/(private)/hrm/api/hrm.api';
import { mapHrmStatsToCards } from '@/app/(private)/hrm/services/hrm.service';
import ErrorMessage from '@/app/components/common/ErrorMessage';
import TabNavigation from '@/app/components/common/TabNavigation';
import { HRM_TABS } from '@/app/(private)/hrm/constants';
import { EMPTY_STAT_CARDS_BY_PERIOD } from '@/app/types/StatType';
import { useAuthStore } from '@/store/authStore';

export default function HrmPage() {
  const authStatus = useAuthStore((state) => state.authStatus);
  const isAuthenticated = authStatus === 'authenticated';

  const hrmStatsQuery = useQuery({
    queryKey: ['hrmStats'],
    queryFn: fetchHrmStats,
    enabled: isAuthenticated,
  });

  const hrmStatsData = hrmStatsQuery.data
    ? mapHrmStatsToCards(hrmStatsQuery.data)
    : EMPTY_STAT_CARDS_BY_PERIOD;
  const hasStatsError = hrmStatsQuery.isError || (hrmStatsQuery.isSuccess && !hrmStatsQuery.data);

  return (
    <div className="min-h-screen bg-gray-50">
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {hasStatsError ? (
          <ErrorMessage message="인적자원관리 통계 데이터를 불러오는데 실패했습니다." />
        ) : (
          <StatSection
            title="인적자원관리"
            subTitle="직원 정보 및 인사 업무 관리 시스템"
            statsData={hrmStatsData}
          />
        )}

        <Suspense fallback={<div>Loading...</div>}>
          <TabNavigation tabs={HRM_TABS} />
        </Suspense>
      </main>
    </div>
  );
}
