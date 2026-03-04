'use client';

import { useMemo, useState } from 'react';
import {
  INVENTORY_SEARCH_KEYWORD_OPTIONS,
  INVENTORY_STATUS_OPTIONS,
  INVENTORY_TABLE_HEADERS,
} from '../../inventory.constants';
import Pagination from '@/app/components/common/Pagination';
import { useDebounce } from 'use-debounce';
import { useQuery } from '@tanstack/react-query';
import { InventoryQueryParams, InventoryResponse } from '../../types/InventoryListType';
import { getInventoryList } from '../../inventory.api';
import StatusLabel from '@/app/components/common/StatusLabel';
import InventoryDetailModal from '../modals/InventoryDetailModal';
import LowStockAlert from '../LowStockAlert';
import StockMovement from '../StockMovement';
import AddInventoryModal from '../modals/AddInventoryModal';
import Link from 'next/link';
import { Page } from '@/app/types/Page';
import TableStatusBox from '@/app/components/common/TableStatusBox';
import Button from '@/app/components/common/Button';
import IconButton from '@/app/components/common/IconButton';
import Dropdown from '@/app/components/common/Dropdown';
import SearchBar from '@/app/components/common/SearchBar';
import { useModal } from '@/app/components/common/modal/useModal';
import InventoryPurchaseRequestModal from '../modals/InventoryPurchaseRequestModal';

const InventoryList = () => {
  const { openModal } = useModal();
  const [showAddModal, setShowAddModal] = useState(false);
  const [searchType, setSearchType] = useState('warehouse');
  const [selectedItemId, setSelectedItemId] = useState<string>('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [searchTerm, setSearchTerm] = useState('');

  const handleItemDetail = (itemId: string) => {
    setSelectedItemId(itemId);
    openModal(InventoryDetailModal, {
      title: '재고 이동 기록',
      width: '1000px',
      height: '1600px',
      $selectedItemId: itemId,
      $setSelectedItemId: setSelectedItemId,
    });
  };

  const [currentPage, setCurrentPage] = useState(1);
  const [debouncedSearchTerm] = useDebounce(searchTerm, 200);
  const queryParams = useMemo(
    () => ({
      page: currentPage - 1,
      size: 10,
      type: searchType || '',
      keyword: debouncedSearchTerm || '',
      statusCode: statusFilter || 'ALL',
    }),
    [currentPage, statusFilter, debouncedSearchTerm, searchType],
  );

  const {
    data: InventoryRes,
    isLoading,
    isError,
  } = useQuery<{
    data: InventoryResponse[];
    pageData: Page;
  }>({
    queryKey: ['inventoryList', queryParams],
    queryFn: ({ queryKey }) => getInventoryList(queryKey[1] as InventoryQueryParams),
    staleTime: 1000,
  });

  const inventories = InventoryRes?.data || [];
  const pageInfo = InventoryRes?.pageData;
  const totalPages = pageInfo?.totalPages ?? 1;

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <LowStockAlert />
        <StockMovement />
      </div>
      <div className="bg-white rounded-lg border border-gray-200 mt-6">
        <div className="p-6 border-b border-gray-200">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold text-gray-900">재고 목록</h2>
            <div className="flex items-center gap-4">
              <div className="flex gap-2">
                <Dropdown
                  placeholder="전체 상태"
                  items={INVENTORY_STATUS_OPTIONS}
                  value={statusFilter}
                  onChange={(status: string) => setStatusFilter(status)}
                  autoSelectFirst
                />
                <SearchBar
                  options={INVENTORY_SEARCH_KEYWORD_OPTIONS}
                  onTypeChange={(type: string) => setSearchType(type)}
                  onKeywordSearch={(keyword) => {
                    setSearchTerm(keyword);
                    setCurrentPage(1);
                  }}
                  placeholder="검색어를 입력하세요"
                />

                <Link href="/warehouse">
                  <Button label="창고 관리" variant="soft" />
                </Link>
                <IconButton
                  icon="ri-add-line mr-1"
                  label="원자재 추가"
                  onClick={() => {
                    openModal(AddInventoryModal, { title: '원자재 추가', width: '700px' });
                  }}
                />
              </div>
            </div>
          </div>
        </div>
        <div className="overflow-x-auto">
          {isLoading ? (
            <TableStatusBox $type="loading" $message="재고 목록을 불러오는 중입니다..." />
          ) : isError ? (
            <TableStatusBox $type="error" $message="재고 목록을 불러오는 중 오류가 발생했습니다." />
          ) : !inventories || inventories.length === 0 ? (
            <TableStatusBox $type="empty" $message="등록된 재고가 없습니다." />
          ) : (
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  {INVENTORY_TABLE_HEADERS.map((header) => (
                    <th
                      key={header}
                      className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                    >
                      {header}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {inventories.map((inventory) => (
                  <tr key={inventory.itemId} className="hover:bg-gray-50">
                    <td className="px-4 py-4 whitespace-nowrap">
                      <div>
                        <div className="text-sm font-medium text-gray-900">
                          {inventory.itemName}
                        </div>
                        <div className="text-sm text-gray-500">{inventory.itemNumber}</div>
                      </div>
                    </td>
                    <td className="px-4 py-4 whitespace-nowrap">
                      <span className="px-2 py-1 text-xs font-medium bg-gray-100 text-gray-800 rounded-full">
                        {inventory.category}
                      </span>
                    </td>
                    <td className="px-4 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">
                        {inventory.currentStock.toLocaleString()} {inventory.uomName}
                      </div>
                    </td>
                    <td className="px-4 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">
                        {inventory.forShipmentStock.toLocaleString()} {inventory.uomName}
                      </div>
                    </td>
                    <td className="px-4 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">
                        {inventory.reservedStock.toLocaleString()} {inventory.uomName}
                      </div>
                    </td>
                    <td className="px-4 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-500">
                        {inventory.safetyStock.toLocaleString()} {inventory.uomName}
                      </div>
                    </td>
                    <td className="px-4 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">
                        ₩{inventory.unitPrice.toLocaleString()}
                      </div>
                    </td>
                    <td className="px-4 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">
                        ₩{inventory.totalAmount.toLocaleString()}
                      </div>
                    </td>
                    <td className="px-4 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">{inventory.warehouseName}</div>
                      <div className="text-sm text-gray-500">{inventory.warehouseType}</div>
                    </td>
                    <td className="px-4 py-4 whitespace-nowrap">
                      <StatusLabel $statusCode={inventory.statusCode} />
                    </td>
                    <td className="px-4 py-4 whitespace-nowrap text-sm font-medium">
                      <button
                        onClick={() => handleItemDetail(inventory.itemId)}
                        className="text-blue-600 hover:text-blue-900 cursor-pointer"
                      >
                        <i className="ri-eye-line"></i>
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
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
      </div>
    </div>
  );
};

export default InventoryList;
