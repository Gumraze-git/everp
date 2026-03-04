'use client';

import { useState } from 'react';
import { Period, StatCardType } from '@/app/types/StatType';
import { STAT_PERIODS } from '@/app/(private)/purchase/constants';
import PageTitle from '@/app/components/common/PageTitle';
import StatCardList from '@/app/components/statCard/StatCardList';
import SlidingNavBar from './SlidingNavBar';

interface StatSectionProps {
  title: string;
  subTitle?: string;
  statsData: Record<Period, StatCardType[]>;
}

export default function StatSection({ title, subTitle, statsData }: StatSectionProps) {
  const DEFAULT_PERIOD: Period = 'week'; // 이번 주
  const [selectedPeriod, setSelectedPeriod] = useState<Period>(DEFAULT_PERIOD);
  const stats = statsData[selectedPeriod];
  const handlePeriodSelect = (key: string) => {
    setSelectedPeriod(key as Period);
  };

  return (
    <div className="">
      <div className="flex justify-between">
        {/* 페이지 제목 */}
        <PageTitle title={title} subTitle={subTitle} />

        <div className="pt-4">
          {/* 기간 선택 필터 */}
          <SlidingNavBar
            items={STAT_PERIODS}
            selectedKey={selectedPeriod}
            onSelect={handlePeriodSelect}
          />
        </div>
      </div>

      {/* 지표 리스트 */}
      <StatCardList stats={stats} period={selectedPeriod} />
    </div>
  );
}
