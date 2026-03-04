'use client';

import IconButton from '@/app/components/common/IconButton';
import { BomListResponse } from '@/app/(private)/production/types/BomListApiType';
import BomDetailModal from '@/app/(private)/production/components/modals/BomDetailModal';
// import BomInputFormModal from '@/app/(private)/production/components/modals/BomInputFormModal';
import { useQuery } from '@tanstack/react-query';
import { fetchBomList } from '../../api/production.api';
import { useModal } from '@/app/components/common/modal/useModal';
import BomInputFormModal from '../modals/BomInputFormModal';
import Pagination from '@/app/components/common/Pagination';
import { useMemo, useState } from 'react';
import { PageRequest } from '@/app/types/Page';
import Button from '@/app/components/common/Button';
import Input from '@/app/components/common/Input';
import StatusLabel from '@/app/components/common/StatusLabel';
import { formatDateTime } from '@/app/utils/date';

export default function BomTab() {
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;

  const { openModal } = useModal();

  const bomQueryParams = useMemo(
    (): PageRequest => ({
      page: currentPage - 1,
      size: pageSize,
    }),
    [currentPage, pageSize],
  );

  // bom 목록 조회
  const {
    data: bomResponse,
    isLoading,
    isError,
  } = useQuery<BomListResponse>({
    queryKey: ['bomList', bomQueryParams],
    queryFn: () => fetchBomList(bomQueryParams),
  });

  // content 배열 추출
  const bomList = bomResponse?.content || [];
  const pageInfo = bomResponse?.page;

  const handleCreate = () => {
    openModal(BomInputFormModal, { title: 'BOM 생성', width: '1200px', height: '800px' });
  };

  const handleViewDetail = (bomId: string) => {
    openModal(BomDetailModal, {
      title: 'BOM 상세 정보',
      bomId: bomId,
      width: '800px',
      height: '700px',
    });
  };

  return (
    <>
      <div className="flex items-center justify-end">
        <IconButton label="BOM 생성" icon="ri-add-line" onClick={handleCreate} />
      </div>

      {isLoading ? (
        <div className="text-center py-12">
          <i className="ri-loader-4-line animate-spin text-3xl text-gray-400"></i>
          <p className="mt-3 text-gray-500">로딩 중...</p>
        </div>
      ) : isError ? (
        <div className="text-center py-12">
          <i className="ri-error-warning-line text-3xl text-red-400"></i>
          <p className="mt-3 text-red-500">데이터를 불러오는데 실패했습니다.</p>
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  제품 정보
                </th>
                <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  BOM 번호
                </th>
                <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  버전
                </th>
                <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  상태
                </th>
                <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  최종 수정일
                </th>
                <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  작업
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {bomList.length > 0 ? (
                bomList.map((bom) => (
                  <tr key={bom.bomId} className="hover:bg-gray-50 text-center">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div>
                        <div className="text-sm font-medium text-gray-900">{bom.productName}</div>
                        <div className="text-sm text-gray-500">{bom.productNumber}</div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">{bom.bomNumber}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {bom.version}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <StatusLabel $statusCode={bom.statusCode} />
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {formatDateTime(bom.lastModifiedAt)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <Button
                        label="상세보기"
                        variant="ghost"
                        size="sm"
                        onClick={() => handleViewDetail(bom.bomId)}
                      />
                      {/* <Button label="수정" variant="ghost" size="sm" onClick={() => handleEdit()} /> */}
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center text-gray-500">
                    등록된 BOM이 없습니다.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {isError || isLoading ? null : (
        <Pagination
          currentPage={currentPage}
          totalPages={pageInfo?.totalPages ?? 1}
          totalElements={pageInfo?.totalElements}
          onPageChange={(page) => setCurrentPage(page)}
        />
      )}
    </>
  );
}
