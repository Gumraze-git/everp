import React from 'react';
import { StatCardType } from '@/app/types/StatType';

interface StatCardProps {
  stat: StatCardType;
  currentPeriodLabel: string;
}

export default function StatCardItem({ stat, currentPeriodLabel }: StatCardProps) {
  return (
    <div className="bg-white rounded-lg border border-gray-200 p-6">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-gray-600">{stat.title}</p>
          <p className="text-2xl font-bold text-gray-900 mt-2">{stat.value}</p>

          <div className="flex items-center mt-2">
            <span
              className={`text-sm font-medium ${
                stat.changeType === 'increase' ? 'text-green-600' : 'text-red-600'
              }`}
            >
              {stat.change}
            </span>
            <span className="text-sm text-gray-500 ml-2">{currentPeriodLabel}</span>
          </div>
        </div>
      </div>
    </div>
  );
}
