'use client';

import { useMemo, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import SupplierFormModal from '@/app/(private)/purchase/components/modals/SupplierFormModal';
import SupplierDetailModal from '@/app/(private)/purchase/components/modals/SupplierDetailModal';
import {
  SupplierListRequestParams,
  SupplierListResponse,
} from '@/app/(private)/purchase/types/SupplierType';
import IconButton from '@/app/components/common/IconButton';
import {
  fetchSupplierCategoryDropdown,
  fetchSupplierList,
  fetchSupplierSearchTypeDropdown,
  fetchSupplierStatusDropdown,
} from '@/app/(private)/purchase/api/purchase.api';
import Dropdown from '@/app/components/common/Dropdown';
import Pagination from '@/app/components/common/Pagination';
import { useModal } from '@/app/components/common/modal/useModal';
import { useDropdown } from '@/app/hooks/useDropdown';
import StatusLabel from '@/app/components/common/StatusLabel';

export default function SupplierListTab() {
  const { openModal } = useModal();

  // 공급업체 카테고리 드롭다운
  const { options: supplierCategoryOptions } = useDropdown(
    'supplierCategoryDropdown',
    fetchSupplierCategoryDropdown,
  );
  // 공급업체 상태 드롭다운
  const { options: supplierStatusOptions } = useDropdown(
    'supplierStatusDropdown',
    fetchSupplierStatusDropdown,
  );
  // 공급업체 검색타입 드롭다운
  const { options: supplierSearchTypeOptions } = useDropdown(
    'supplierSearchTypeDropdown',
    fetchSupplierSearchTypeDropdown,
  );

  const [selectedCategory, setSelectedCategory] = useState<string>('');
  const [selectedStatus, setSelectedStatus] = useState<string>('');
  const [selectedSearchType, setSelectedSearchType] = useState<string>('');
  const [keyword, setKeyword] = useState<string>('');

  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;

  const queryParams = useMemo(
    (): SupplierListRequestParams => ({
      category: selectedCategory,
      statusCode: selectedStatus,
      type: selectedSearchType,
      keyword: keyword,
      page: currentPage - 1,
      size: pageSize,
    }),
    [selectedCategory, selectedStatus, selectedSearchType, keyword, currentPage],
  );

  // React Query로 데이터 가져오기 - 쿼리 파라미터 전달
  const {
    data: supplierData,
    isLoading,
    isError,
  } = useQuery<SupplierListResponse>({
    queryKey: ['supplierList', queryParams],
    queryFn: () => fetchSupplierList(queryParams),
  });

  if (isLoading) return <p>불러오는 중...</p>;
  if (isError || !supplierData) return <p>데이터를 불러오지 못했습니다.</p>;

  const suppliers = supplierData.content || [];
  const pageInfo = supplierData.page;
  const totalPages = pageInfo?.totalPages ?? 1;

  const handleViewAddSupplier = () => {
    openModal(SupplierFormModal, { title: '공급업체 등록', height: '800px' });
  };

  const handleViewDetail = (supplierId: string) => {
    openModal(SupplierDetailModal, { title: '공급업체 상세', supplierId: supplierId });
  };

  return (
    <>
      <div className="flex justify-between items-center">
        <h3 className="text-lg font-semibold text-gray-900">공급업체 목록</h3>
        <div className="flex items-center space-x-4">
          {/* 공급업체 카테고리 드롭다운 */}
          <Dropdown
            placeholder="전체 카테고리"
            items={supplierCategoryOptions}
            value={selectedCategory}
            onChange={(category: string): void => {
              setSelectedCategory(category);
              setCurrentPage(1);
            }}
          />
          <Dropdown
            placeholder="전체 상태"
            items={supplierStatusOptions}
            value={selectedStatus}
            onChange={(status: string): void => {
              setSelectedStatus(status);
              setCurrentPage(1);
            }}
          />
          <Dropdown
            placeholder="검색 타입"
            items={supplierSearchTypeOptions}
            value={selectedSearchType}
            onChange={(type: string): void => {
              setSelectedSearchType(type);
              setCurrentPage(1);
            }}
          />
          <IconButton label="공급업체 등록" icon="ri-add-line" onClick={handleViewAddSupplier} />
        </div>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full">
          <thead className="bg-gray-50">
            <tr className="text-center">
              <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                공급업체 코드
              </th>
              <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                업체명
              </th>
              <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                카테고리
              </th>
              <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                연락처
              </th>
              <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                주소
              </th>
              <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                배송 기간
              </th>
              <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                상태
              </th>
              <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                작업
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {suppliers.map((supplier) => {
              const { supplierInfo } = supplier;
              return (
                <tr key={supplierInfo.supplierId} className="hover:bg-gray-50 text-center">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {supplierInfo.supplierNumber}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {supplierInfo.supplierName}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {supplierInfo.category}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <div className="flex flex-col">
                      <span>{supplierInfo.supplierEmail}</span>
                      <span>{supplierInfo.supplierPhone}</span>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <div className="flex flex-col">
                      <span>{supplierInfo.supplierBaseAddress}</span>
                      <span>{supplierInfo.supplierDetailAddress}</span>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {supplierInfo.deliveryLeadTime}일
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <StatusLabel $statusCode={supplierInfo.supplierStatusCode} />
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <div className="flex space-x-2">
                      <button
                        onClick={() => handleViewDetail(supplierInfo.supplierId)}
                        className="w-8 h-8 flex items-center justify-center text-blue-500 hover:bg-blue-50 rounded cursor-pointer"
                        title="상세보기"
                      >
                        <i className="ri-eye-line"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              );
            })}
            {suppliers.length === 0 && (
              <tr>
                <td colSpan={7} className="px-6 py-8 text-center text-gray-500 text-sm">
                  공급업체가 없습니다.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* 페이지네이션 */}
      {isError || isLoading ? null : (
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          totalElements={pageInfo?.totalElements}
          onPageChange={(page) => setCurrentPage(page)}
        />
      )}
    </>
  );
}
