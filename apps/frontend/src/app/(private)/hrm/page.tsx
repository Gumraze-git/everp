import Providers from '@/app/providers';
import { getQueryClient } from '@/lib/queryClient';
import { dehydrate } from '@tanstack/react-query';
import StatSection from '@/app/components/common/StatSection';
import {
  fetchAttendanceStatusDropdown,
  fetchDepartmentsDropdown,
  fetchEmployeesList,
  fetchHrmStats,
  fetchPayrollStatusDropdown,
  fetchPositionsList,
  fetchProgramListDropdown,
  fetchProgramStatusDropdown,
  fetchTrainingCategoryDropdown,
} from '@/app/(private)/hrm/api/hrm.api';
import { mapHrmStatsToCards } from '@/app/(private)/hrm/services/hrm.service';
import ErrorMessage from '@/app/components/common/ErrorMessage';
import { Suspense } from 'react';
import TabNavigation from '@/app/components/common/TabNavigation';
import { HRM_TABS } from '@/app/(private)/hrm/constants';

export default async function HrmPage() {
  const queryClient = getQueryClient();

  const initialParams = {
    department: undefined,
    position: undefined,
    page: 0,
    size: 10,
  };

  await Promise.all([
    queryClient.prefetchQuery({
      queryKey: ['employeesList', initialParams],
      queryFn: () => fetchEmployeesList(initialParams),
    }),

    // --- 드롭 다운 prefetch ---
    queryClient.prefetchQuery({
      queryKey: ['positionsList'],
      queryFn: fetchPositionsList,
    }),

    // --- 드롭다운 prefetch ---
    // 부서 드롭다운
    queryClient.prefetchQuery({
      queryKey: ['departmentsDropdown'],
      queryFn: fetchDepartmentsDropdown,
    }),

    // // 직급 드롭다운 (부서 ID 필요 시 기본값 전달)
    // queryClient.prefetchQuery({
    //   queryKey: ['positionsDropdown', defaultDepartmentId],
    //   queryFn: () => fetchPositionsDropdown(defaultDepartmentId),
    // }),

    // 출결 상태 드롭다운
    queryClient.prefetchQuery({
      queryKey: ['attendanceStatusDropdown'],
      queryFn: fetchAttendanceStatusDropdown,
    }),

    // 부서 구성원 드롭다운 (부서 ID 필요 시 기본값 전달)
    // queryClient.prefetchQuery({
    //   queryKey: ['deptMemberDropdown', defaultDepartmentId],
    //   queryFn: () => fetchDeptMemberDropdown(defaultDepartmentId),
    // }),

    // 급여 상태 드롭다운
    queryClient.prefetchQuery({
      queryKey: ['payrollStatusDropdown'],
      queryFn: fetchPayrollStatusDropdown,
    }),

    // 교육 카테고리 드롭다운
    queryClient.prefetchQuery({
      queryKey: ['trainingCategoryDropdown'],
      queryFn: fetchTrainingCategoryDropdown,
    }),

    // 교육 프로그램 드롭다운
    queryClient.prefetchQuery({
      queryKey: ['programListDropdown'],
      queryFn: fetchProgramListDropdown,
    }),

    // 교육 프로그램 모집 상태 드롭다운
    queryClient.prefetchQuery({
      queryKey: ['programStatusDropdown'],
      queryFn: fetchProgramStatusDropdown,
    }),
  ]);
  const dehydratedState = dehydrate(queryClient);

  const data = await fetchHrmStats();
  const hrmStatsData = data ? mapHrmStatsToCards(data ?? {}) : null;

  return (
    <Providers dehydratedState={dehydratedState}>
      <div className="min-h-screen bg-gray-50">
        <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          {hrmStatsData ? (
            <StatSection
              title="인적자원관리"
              subTitle="직원 정보 및 인사 업무 관리 시스템"
              statsData={hrmStatsData}
            />
          ) : (
            <ErrorMessage message={'인적자원관리 통계 데이터를 불러오는데 실패했습니다.'} />
          )}

          {/* 직원관리 탭 / 급여관리 탭 / 근태관리 탭 / 교육관리 탭 */}
          <Suspense fallback={<div>Loading...</div>}>
            <TabNavigation tabs={HRM_TABS} />
          </Suspense>
        </main>
      </div>
    </Providers>
  );
}
