'use client';

import DateRangePicker from '@/app/components/common/DateRangePicker';
import Dropdown from '@/app/components/common/Dropdown';
import { useMemo, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { fetchMpsBomDropdown, fetchMpsList } from '@/app/(private)/production/api/production.api';
import { MpsListParams, MpsListResponse } from '@/app/(private)/production/types/MpsApiType';
import { useDropdown } from '@/app/hooks/useDropdown';
import SimplePagination from '@/app/components/common/SimplePagination';

export default function MpsTab() {
  const { options: dropdownOptions } = useDropdown('mpsBomsDropdown', fetchMpsBomDropdown);

  const date = new Date().toISOString().split('T')[0];
  const [selectedProduct, setSelectedProduct] = useState<string>('');
  const [startDate, setStartDate] = useState(date);
  const [endDate, setEndDate] = useState(date);
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 7;

  const queryParams = useMemo(
    (): MpsListParams => ({
      bomId: selectedProduct,
      startDate: startDate,
      endDate: endDate,
      page: currentPage - 1,
      size: pageSize,
    }),
    [selectedProduct, startDate, endDate, currentPage],
  );

  const {
    data: currentData,
    isLoading: isMpsLoading,
    isError: isMpsError,
    refetch,
  } = useQuery<MpsListResponse>({
    queryKey: ['mpsList', queryParams],
    queryFn: () => fetchMpsList(queryParams),
    enabled: !!startDate && !!endDate,
  });

  const mpsData = currentData?.content || [];
  const pageInfo = currentData?.page;

  const renderTableRow = (
    label: string,
    dataKey: keyof Omit<MpsListResponse['content'][number], 'week'>,
    isClickable: boolean,
    isHighlight: boolean = false,
  ) => {
    if (!mpsData || mpsData.length === 0) return null;

    return (
      <tr className="hover:bg-gray-50">
        <td
          className={`border border-gray-300 px-4 py-3 text-sm font-medium text-gray-900 ${
            isHighlight ? 'bg-blue-50' : 'bg-gray-50'
          }`}
        >
          {label}
        </td>
        {mpsData.map((weekData, index) => {
          const value = weekData[dataKey];
          const isDemandClickable = isClickable && typeof value === 'number' && value > 0;
          const isStartWeek = index === 3;

          return (
            <td
              key={`${label}-${index}`}
              className={`border border-gray-300 px-4 py-3 text-center text-sm ${
                isHighlight ? 'font-semibold text-blue-700 bg-blue-50' : 'text-gray-900'
              } ${isDemandClickable ? 'cursor-pointer hover:bg-blue-50' : ''} ${
                isStartWeek && !isHighlight ? 'bg-green-50' : ''
              }`}
            >
              {value ?? '-'}
            </td>
          );
        })}
      </tr>
    );
  };

  return (
    <>
      <div className="flex justify-between gap-4">
        <Dropdown
          placeholder="제품 선택"
          items={dropdownOptions}
          value={selectedProduct}
          onChange={(product: string) => setSelectedProduct(product)}
          autoSelectFirst
        />
        <DateRangePicker
          startDate={startDate}
          onStartDateChange={setStartDate}
          endDate={endDate}
          onEndDateChange={setEndDate}
        />
      </div>

      {isMpsLoading && (
        <div className="bg-white border border-gray-200 rounded-lg p-12 text-center">
          <i className="ri-loader-4-line animate-spin text-4xl text-blue-500 mb-4"></i>
          <p className="text-gray-500 text-lg">MPS 데이터를 불러오는 중입니다...</p>
        </div>
      )}

      {isMpsError && (
        <div className="bg-white border border-gray-200 rounded-lg p-12 text-center text-red-600">
          <i className="ri-error-warning-line text-4xl mb-4"></i>
          <p className="text-lg">데이터를 불러오는 데 실패했습니다.</p>
          <button
            onClick={() => refetch()}
            className="mt-4 px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
          >
            다시 시도
          </button>
        </div>
      )}

      {!isMpsLoading && !isMpsError && mpsData.length > 0 ? (
        <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
          <div className="overflow-x-auto">
            <table className="min-w-full border-collapse border border-gray-300">
              <thead>
                <tr className="bg-gray-50">
                  <th className="border border-gray-300 px-4 py-3 text-left text-sm font-medium text-gray-900">
                    구분
                  </th>
                  {mpsData.map((weekData, index) => (
                    <th
                      key={`week-${index}`}
                      className={`border border-gray-300 px-4 py-3 text-center text-sm font-medium ${
                        index === 3 ? 'bg-green-100 text-green-800' : 'text-gray-900'
                      }`}
                    >
                      <div className="flex flex-col items-center gap-1">
                        <span>{weekData.week}</span>
                        {index === 3 && (
                          <span className="text-xs bg-green-600 text-white px-2 py-0.5 rounded-full">
                            시작날짜
                          </span>
                        )}
                      </div>
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {renderTableRow('수요', 'demand', true)}
                {renderTableRow('재고 필요량', 'requiredInventory', false)}
                {renderTableRow('생산 소요량', 'productionNeeded', false)}
                {renderTableRow('계획 생산 (MPS)', 'plannedProduction', false, true)}
              </tbody>
            </table>
          </div>
        </div>
      ) : (
        !isMpsLoading &&
        !isMpsError && (
          <div className="bg-white border border-gray-200 rounded-lg p-12 text-center">
            <i className="ri-search-line text-6xl text-gray-300 mb-4"></i>
            <p className="text-gray-500 text-lg">조회된 MPS 데이터가 없습니다.</p>
          </div>
        )
      )}

      <SimplePagination
        currentPage={currentPage}
        onPageChange={setCurrentPage}
        totalPages={pageInfo?.totalPages ?? 1}
      />
    </>
  );
}
