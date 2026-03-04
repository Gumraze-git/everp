'use client';

import { useMemo, useState } from 'react';
import CustomerDetailModal from '@/app/(private)/sales/components/modals/CustomerDetailModal';
import NewCustomerModal from '@/app/(private)/sales/components/modals/NewCustomerModal';
import {
  CustomerQueryParams,
  CustomerStatus,
} from '@/app/(private)/sales/types/SalesCustomerListType';
import { useQuery } from '@tanstack/react-query';
import { getCustomerList } from '../../sales.api';
import { useDebounce } from 'use-debounce';
import { CustomerDetail } from '../../types/SalesCustomerDetailType';
import TableStatusBox from '@/app/components/common/TableStatusBox';
import {
  CUSTOMER_LIST_TABLE_HEADERS,
  CUSTOMER_SEARCH_KEYWORD_OPTIONS,
  CUSTOMER_STATUS_OPTIONS,
} from '@/app/(private)/sales/constant';
import Pagination from '@/app/components/common/Pagination';
import StatusLabel from '@/app/components/common/StatusLabel';
import IconButton from '@/app/components/common/IconButton';
import SearchBar from '@/app/components/common/SearchBar';
import Dropdown from '@/app/components/common/Dropdown';
import { useModal } from '@/app/components/common/modal/useModal';

const CustomerList = () => {
  const { openModal } = useModal();
  const [searchTerm, setSearchTerm] = useState('');
  const [searchType, setSearchType] = useState('customerName');
  const [statusFilter, setStatusFilter] = useState<CustomerStatus>('ALL');
  const [selectedCustomerId, setSelectedCustomerId] = useState<string>('');
  const [debouncedSearchTerm] = useDebounce(searchTerm, 200);
  const [currentPage, setCurrentPage] = useState(1);

  const handleCustomerRegisterClick = () => {
    openModal(NewCustomerModal, { width: '1190px', title: '신규 고객 등록' });
  };

  const queryParams = useMemo(
    () => ({
      page: currentPage - 1,
      size: 10,
      status: statusFilter || 'ALL',
      type: searchType || '',
      keyword: debouncedSearchTerm || '',
    }),
    [currentPage, statusFilter, debouncedSearchTerm, searchType],
  );
  const {
    data: customerRes,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ['customerList', queryParams],
    queryFn: ({ queryKey }) => getCustomerList(queryKey[1] as CustomerQueryParams),
  });

  const customers = customerRes?.data ?? [];
  const pageInfo = customerRes?.pageData;
  console.log(pageInfo);

  const handleViewClick = (id: string) => {
    setSelectedCustomerId(id);
    openModal(CustomerDetailModal, {
      width: '900px',
      height: '1050px',
      title: '고객 상세 정보',
      $selectedCustomerId: id,
      $setEditFormData: setEditFormData,
    });
  };

  const [showEditModal, setShowEditModal] = useState(false);
  const [editFormData, setEditFormData] = useState<CustomerDetail | null>(null);
  // const handleEditClick = (customer: CustomerDetail) => {
  //   setEditFormData({ ...customer });
  //   setShowEditModal(true);
  // };

  const totalPages = pageInfo?.totalPages ?? 1;

  return (
    <>
      {/* 헤더 */}
      <div className="border-b border-gray-200 flex justify-end items-center gap-4  py-2">
        <Dropdown
          placeholder="전체 상태"
          items={CUSTOMER_STATUS_OPTIONS}
          value={statusFilter}
          onChange={(status: string) => setStatusFilter(status as CustomerStatus)}
          autoSelectFirst
        />
        <SearchBar
          options={CUSTOMER_SEARCH_KEYWORD_OPTIONS}
          onTypeChange={(type: string) => setSearchType(type)}
          onKeywordSearch={(keyword) => {
            setSearchTerm(keyword);
            setCurrentPage(1);
          }}
          placeholder="검색어를 입력하세요"
        />
        <IconButton
          icon="ri-user-add-line"
          onClick={handleCustomerRegisterClick}
          label="고객 등록"
        />
      </div>

      {/* 테이블 */}
      <div className="overflow-x-auto">
        {isLoading ? (
          <TableStatusBox $type="loading" $message="고객 목록을 불러오는 중입니다..." />
        ) : isError ? (
          <TableStatusBox $type="error" $message="고객 목록을 불러오는 중 오류가 발생했습니다." />
        ) : !customers || customers.length === 0 ? (
          <TableStatusBox $type="empty" $message="고객 정보가 없습니다." />
        ) : (
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                {CUSTOMER_LIST_TABLE_HEADERS.map((header) => (
                  <th
                    key={header}
                    className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                  >
                    {header}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {customers.map((customer) => (
                <tr key={customer.customerId} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <div>
                        <div className="text-sm font-medium text-gray-900">
                          {customer.customerName}
                        </div>
                        <div className="text-xs text-gray-500">{customer.customerNumber}</div>
                      </div>
                    </div>
                  </td>

                  <td className="px-6 py-4">
                    <div className="text-sm text-gray-900">{customer.manager.managerName}</div>
                    <div className="text-xs text-gray-500">{customer.manager.managerPhone}</div>
                    <div className="text-xs text-gray-500">{customer.manager.managerEmail}</div>
                  </td>
                  <td className="px-6 py-4">
                    <div className="text-sm text-gray-900 max-w-xs truncate">
                      {customer.address}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm font-medium text-gray-900">
                      ₩{customer.totalTransactionAmount.toLocaleString()}
                    </div>
                    <div className="text-xs text-gray-500">{customer.orderCount}건</div>
                  </td>

                  <td className="px-6 py-4 whitespace-nowrap">
                    <StatusLabel $statusCode={customer.statusCode} />
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div className="flex items-center space-x-2">
                      <button
                        onClick={() => handleViewClick(customer.customerId)}
                        className="text-blue-600 hover:text-blue-800 cursor-pointer"
                        title="상세보기"
                      >
                        <i className="ri-eye-line"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* 페이지네이션 */}
      {!isLoading && !isError && customers.length > 0 && (
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          totalElements={pageInfo?.totalElements}
          onPageChange={(page) => setCurrentPage(page)}
        />
      )}
    </>
  );
};

export default CustomerList;
