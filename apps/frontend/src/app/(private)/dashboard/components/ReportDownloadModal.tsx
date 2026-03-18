'use client';

import React from 'react';
import { ReportDownloadModalProps } from '../types/ReportDownloadModalType';

const ReportDownloadModal = ({ isOpen, onClose, selectedPeriod }: ReportDownloadModalProps) => {
  if (!isOpen) return null;

  const handleReportDownload = (reportType: string, format: string) => {
    const today = new Date();
    const dateString = today.toISOString().split('T')[0];

    let reportData = '';
    let fileName = '';

    if (reportType === 'summary') {
      const summaryData = [
        ['항목', '금액', '변화율', '기간'],
        ['완성품 매출', '₩125,000,000', '+12.5%', selectedPeriod],
        ['원자재 매입', '₩85,000,000', '+8.2%', selectedPeriod],
        ['영업이익', '₩40,000,000', '+15.3%', selectedPeriod],
        ['미결 발주', '23건', '-5건', selectedPeriod],
      ];
      reportData = summaryData.map((row) => row.join(',')).join('\n');
      fileName = `대시보드_요약리포트_${dateString}.${format}`;
    } else if (reportType === 'detailed') {
      const detailedData = [
        ['날짜', '구분', '거래처', '품목', '수량', '단가', '금액', '상태'],
        ['2024-01-15', '매출', 'Hanil Motors Co.', '전방 범퍼 커버', '100', '148,000', '14,800,000', '완료'],
        ['2024-01-15', '매입', '한빛 오토 외장', 'ABS 레진 펠렛', '320', '31,000', '9,920,000', '완료'],
        ['2024-01-14', '매출', '현대모비스 영업본부', '라디에이터 그릴', '85', '132,000', '11,220,000', '완료'],
        ['2024-01-14', '매입', 'Seohan Body Panels', '알루미늄 패널 시트', '140', '78,000', '10,920,000', '진행중'],
        ['2024-01-13', '매출', '넥스트드라이브 모터스', '도어 트림 어셈블리', '72', '118,000', '8,496,000', '완료'],
        ['2024-01-13', '매입', 'Prime Bumper & Grill', '크롬 트림 스트립', '180', '43,000', '7,740,000', '완료'],
        ['2024-01-12', '매출', '동운 모빌리티', '램프 하우징', '64', '164,000', '10,496,000', '완료'],
        ['2024-01-12', '매입', '성우 모터스 트림', '구조용 접착제 팩', '90', '38,000', '3,420,000', '진행중'],
      ];
      reportData = detailedData.map((row) => row.join(',')).join('\n');
      fileName = `대시보드_상세리포트_${dateString}.${format}`;
    } else if (reportType === 'financial') {
      const financialData = [
        ['계정과목', '차변', '대변', '잔액'],
        ['현금', '50,000,000', '30,000,000', '20,000,000'],
        ['외상매출금', '80,000,000', '60,000,000', '20,000,000'],
        ['원자재재고', '100,000,000', '70,000,000', '30,000,000'],
        ['외상매입금', '40,000,000', '60,000,000', '-20,000,000'],
        ['자본금', '100,000,000', '0', '100,000,000'],
        ['제품매출', '0', '125,000,000', '-125,000,000'],
        ['원재료매입', '85,000,000', '0', '85,000,000'],
        ['물류운영비', '15,000,000', '0', '15,000,000'],
      ];
      reportData = financialData.map((row) => row.join(',')).join('\n');
      fileName = `대시보드_재무리포트_${dateString}.${format}`;
    }

    const BOM = '\uFEFF';
    const blob = new Blob([BOM + reportData], { type: 'text/csv;charset=utf-8;' });

    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', fileName);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    alert(`리포트가 성공적으로 다운로드되었습니다.\n파일명: ${fileName}\n기간: ${selectedPeriod}`);
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-md">
        <div className="flex items-center justify-between mb-6">
          <h3 className="text-lg font-semibold text-gray-900">리포트 다운로드</h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600 cursor-pointer">
            <i className="ri-close-line text-xl"></i>
          </button>
        </div>

        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-3">
              리포트 유형을 선택하세요
            </label>
            <div className="space-y-3">
              <div className="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 cursor-pointer transition-colors duration-200">
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-3">
                    <div className="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                      <i className="ri-pie-chart-line text-blue-600"></i>
                    </div>
                    <div>
                      <div className="font-medium text-gray-900">요약 리포트</div>
                      <div className="text-sm text-gray-500">주요 지표 및 통계 요약</div>
                    </div>
                  </div>
                  <button
                    onClick={() => handleReportDownload('summary', 'csv')}
                    className="px-3 py-1 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors duration-200 cursor-pointer whitespace-nowrap text-sm"
                  >
                    다운로드
                  </button>
                </div>
              </div>

              <div className="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 cursor-pointer transition-colors duration-200">
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-3">
                    <div className="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                      <i className="ri-file-list-3-line text-green-600"></i>
                    </div>
                    <div>
                      <div className="font-medium text-gray-900">상세 리포트</div>
                      <div className="text-sm text-gray-500">모든 거래 내역 상세 정보</div>
                    </div>
                  </div>
                  <button
                    onClick={() => handleReportDownload('detailed', 'csv')}
                    className="px-3 py-1 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors duration-200 cursor-pointer whitespace-nowrap text-sm"
                  >
                    다운로드
                  </button>
                </div>
              </div>

              <div className="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 cursor-pointer transition-colors duration-200">
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-3">
                    <div className="w-10 h-10 bg-purple-100 rounded-lg flex items-center justify-center">
                      <i className="ri-money-dollar-circle-line text-purple-600"></i>
                    </div>
                    <div>
                      <div className="font-medium text-gray-900">재무 리포트</div>
                      <div className="text-sm text-gray-500">계정별 재무 현황 분석</div>
                    </div>
                  </div>
                  <button
                    onClick={() => handleReportDownload('financial', 'csv')}
                    className="px-3 py-1 bg-purple-600 text-white rounded-md hover:bg-purple-700 transition-colors duration-200 cursor-pointer whitespace-nowrap text-sm"
                  >
                    다운로드
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div className="bg-gray-50 rounded-lg p-4">
            <div className="flex items-center space-x-2">
              <i className="ri-calendar-line text-gray-600"></i>
              <span className="text-sm font-medium text-gray-700">선택된 기간:</span>
              <span className="text-sm text-gray-900 font-semibold">{selectedPeriod}</span>
            </div>
          </div>
        </div>

        <div className="flex justify-end space-x-3 pt-6 border-t border-gray-200 mt-6">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 cursor-pointer whitespace-nowrap"
          >
            닫기
          </button>
        </div>
      </div>
    </div>
  );
};

export default ReportDownloadModal;
