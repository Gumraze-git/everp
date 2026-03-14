'use client';

import { useQuery } from '@tanstack/react-query';
import QuickActions from '@/app/(private)/dashboard/components/QuickActions';
import WorkflowStatus from '@/app/(private)/dashboard/components/WorkflowStatus';
import StatSection from '@/app/components/common/StatSection';
import { getDashboardStats, getWorkflowStatus } from '@/app/(private)/dashboard/dashboard.api';
import { mapDashboardStatsToCards } from './dashboard.service';
import { DashboardWorkflowRes } from './types/DashboardWorkflowType';
import { EMPTY_STAT_CARDS_BY_PERIOD } from '@/app/types/StatType';
import { useAuthStore } from '@/store/authStore';

const EMPTY_WORKFLOW_DATA: DashboardWorkflowRes = {
  tabs: [
    { tabCode: 'first', items: [] },
    { tabCode: 'second', items: [] },
  ],
};

export default function DashboardPage() {
  const authStatus = useAuthStore((state) => state.authStatus);
  const isAuthenticated = authStatus === 'authenticated';

  const dashboardStatsQuery = useQuery({
    queryKey: ['dashboardStats'],
    queryFn: getDashboardStats,
    enabled: isAuthenticated,
  });

  const workflowStatusQuery = useQuery({
    queryKey: ['workflowStatus'],
    queryFn: getWorkflowStatus,
    enabled: isAuthenticated,
  });

  const dashboardStatsData = dashboardStatsQuery.data
    ? mapDashboardStatsToCards(dashboardStatsQuery.data)
    : EMPTY_STAT_CARDS_BY_PERIOD;
  const workflowData = workflowStatusQuery.data ?? EMPTY_WORKFLOW_DATA;

  return (
    <div className="min-h-screen">
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <StatSection
          title="대시보드"
          subTitle="기업 자원 관리 현황"
          statsData={dashboardStatsData}
        />
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 mt-8 mb-8">
          <div className="lg:col-span-1">
            <QuickActions />
          </div>
          <div className="lg:col-span-2">
            <WorkflowStatus $workflowData={workflowData} />
          </div>
        </div>
      </main>
    </div>
  );
}
