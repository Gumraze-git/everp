'use client';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { QuotationSimulationData } from '@/app/(private)/production/types/QuotationSimulationApiType';
import { fetchQuotationPreview } from '@/app/(private)/production/api/production.api';
import {
  FetchQuotationPreviewParams,
  QuotationPreviewResponse,
} from '@/app/(private)/production/types/QuotationPreviewApiType';
import Button from '@/app/components/common/Button';
import { ModalProps } from '@/app/components/common/modal/types';

interface SimulationResultModalProps extends ModalProps {
  simulationResults: QuotationSimulationData[];
  selectedQuotes: string[];
  // MPS Preview 데이터를 부모에게 전달
  onConfirm: (previewData: QuotationPreviewResponse) => void;
}

export default function SimulationResultModal({
  simulationResults,
  selectedQuotes,
  onClose,
  onConfirm,
}: SimulationResultModalProps) {
  // 프론트엔드 페이지네이션 상태
  const [currentIndex, setCurrentIndex] = useState(0);

  const previewMutation = useMutation({
    mutationFn: (params: string[]) => fetchQuotationPreview(params),
    onSuccess: (data) => {
      onConfirm(data);
    },
    onError: (error) => {
      console.error('MPS 프리뷰 조회 실패:', error);
      alert('MPS 프리뷰를 가져오는데 실패했습니다.');
    },
  });

  const handleConfirmClick = () => {
    previewMutation.mutate(selectedQuotes);
  };

  const handlePrevious = () => {
    if (currentIndex > 0) {
      setCurrentIndex(currentIndex - 1);
    }
  };

  const handleNext = () => {
    if (currentIndex < simulationResults.length - 1) {
      setCurrentIndex(currentIndex + 1);
    }
  };

  const getStatusBadge = (status: string) => {
    const config =
      status === 'PASS'
        ? { label: '충족', class: 'bg-green-100 text-green-600' }
        : { label: '부족', class: 'bg-red-100 text-red-600' };

    return (
      <span className={`inline-flex px-2 py-1 rounded-full text-xs font-semibold ${config.class}`}>
        {config.label}
      </span>
    );
  };

  // 현재 표시할 결과
  const currentResult = simulationResults[currentIndex];
  const totalCount = simulationResults.length;

  return (
    <>
      {/* 시뮬레이션 결과 표시 (현재 인덱스만) */}
      <div className="space-y-6">
        <div className="border border-gray-200 rounded-lg p-4">
          {/* 견적 정보 */}
          <div className="mb-4">
            <h3 className="text-lg font-semibold text-gray-900">
              견적 {currentResult.quotationNumber}
            </h3>
            <div className="mt-2 grid grid-cols-2 gap-4 text-sm">
              <div>
                <span className="text-gray-500">고객사:</span>
                <span className="ml-2 text-gray-900">{currentResult.customerCompanyName}</span>
              </div>
              <div>
                <span className="text-gray-500">제품:</span>
                <span className="ml-2 text-gray-900">{currentResult.productName}</span>
              </div>
              <div>
                <span className="text-gray-500">요청 수량:</span>
                <span className="ml-2 text-gray-900">
                  {currentResult.requestQuantity.toLocaleString()}EA
                </span>
              </div>
              <div>
                <span className="text-gray-500">요청 납기:</span>
                <span className="ml-2 text-gray-900">
                  {new Date(currentResult.requestDueDate).toLocaleDateString('ko-KR')}
                </span>
              </div>
            </div>
          </div>

          {/* 시뮬레이션 정보 */}
          <div className="mb-4 bg-blue-50 p-3 rounded">
            <div className="grid grid-cols-3 gap-4 text-sm">
              <div>
                <span className="text-gray-600">가용 수량:</span>
                <span className="ml-2 font-semibold text-blue-600">
                  {currentResult.simulation.availableQuantity.toLocaleString()}EA
                </span>
              </div>
              <div>
                <span className="text-gray-600">제안 납기:</span>
                <span className="ml-2 font-semibold text-blue-600">
                  {currentResult.simulation.suggestedDueDate}
                </span>
              </div>
              <div>
                <span className="text-gray-600">상태:</span>
                <span className="ml-2">{getStatusBadge(currentResult.simulation.status)}</span>
              </div>
            </div>
            <div className="mt-2 text-xs text-gray-500">
              생성 시간: {new Date(currentResult.simulation.generatedAt).toLocaleString('ko-KR')}
            </div>
          </div>

          {/* 부족 재고 테이블 */}
          {currentResult.shortages && currentResult.shortages.length > 0 && (
            <div>
              <h4 className="text-sm font-semibold text-gray-700 mb-2">부족 재고 목록</h4>
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500">
                        자재명
                      </th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500">
                        필요 수량
                      </th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500">
                        현재 재고
                      </th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500">
                        부족 수량
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {currentResult.shortages.map((shortage, index) => (
                      <tr key={index}>
                        <td className="px-4 py-2 text-sm text-gray-900">{shortage.itemName}</td>
                        <td className="px-4 py-2 text-sm text-gray-900">
                          {shortage.requiredQuantity.toLocaleString()}
                        </td>
                        <td className="px-4 py-2 text-sm text-gray-900">
                          {shortage.currentStock.toLocaleString()}
                        </td>
                        <td className="px-4 py-2 text-sm font-semibold text-red-600">
                          {shortage.shortQuantity.toLocaleString()}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* 네비게이션 버튼 (페이지네이션) */}
      {totalCount > 1 && (
        <div className="flex justify-center items-center gap-4 mt-6 pt-4 border-t">
          <button
            onClick={handlePrevious}
            disabled={currentIndex === 0}
            className={`px-4 py-2 text-sm font-medium rounded-lg transition-colors ${
              currentIndex === 0
                ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-50 cursor-pointer'
            }`}
          >
            <i className="ri-arrow-left-line mr-1"></i>
            이전
          </button>

          <div className="text-sm text-gray-600">
            {currentIndex + 1} / {totalCount}
          </div>

          <button
            onClick={handleNext}
            disabled={currentIndex === totalCount - 1}
            className={`px-4 py-2 text-sm font-medium rounded-lg transition-colors ${
              currentIndex === totalCount - 1
                ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-50 cursor-pointer'
            }`}
          >
            다음
            <i className="ri-arrow-right-line ml-1"></i>
          </button>
        </div>
      )}

      {/* 액션 버튼 */}
      <div className="flex justify-end">
        <Button
          label={previewMutation.isPending ? 'MPS 프리뷰 로딩 중...' : '제안 납기 확정'}
          onClick={handleConfirmClick}
          disabled={previewMutation.isPending}
        />
      </div>
    </>
  );
}
