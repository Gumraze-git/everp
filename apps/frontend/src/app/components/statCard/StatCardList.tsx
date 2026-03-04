import { Period, StatCardType } from '@/app/types/StatType';
import StatCardItem from '@/app/components/statCard/StatCardItem';

interface StatsGridProps {
  stats: StatCardType[];
  period: Period;
}

export default function StatCardList({ stats, period }: StatsGridProps) {
  const comparisonLabelMap: Record<Period, string> = {
    week: '전주 대비',
    month: '전월 대비',
    quarter: '전분기 대비',
    year: '전년 대비',
  };

  const currentPeriodLabel = comparisonLabelMap[period];

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      {stats.map((stat, index) => (
        <StatCardItem key={index} stat={stat} currentPeriodLabel={currentPeriodLabel} />
      ))}
    </div>
  );
}
