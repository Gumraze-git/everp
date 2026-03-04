'use client';

import { useEffect, useMemo, useState } from 'react';
import {
  QuoteStatus,
  Quote,
  QuoteQueryParams,
} from '@/app/(private)/sales/types/SalesQuoteListType';
import QuoteDetailModal from '../modals/QuoteDetailModal';
import { useQuery } from '@tanstack/react-query';
import { getQuoteList } from '@/app/(private)/sales/sales.api';
import { useDebounce } from 'use-debounce';
import QuoteReviewModal from '../modals/QuoteReviewModal';
import TableStatusBox from '@/app/components/common/TableStatusBox';
import {
  getQuoteSearchKeywordOptions,
  QUOTE_LIST_TABLE_HEADERS,
} from '@/app/(private)/sales/constant';
import { QUOTE_STATUS_OPTIONS } from '@/app/(private)/sales/constant';
import Pagination from '@/app/components/common/Pagination';
import StatusLabel from '@/app/components/common/StatusLabel';
import { useRole } from '@/app/hooks/useRole';
import DateRangePicker from '@/app/components/common/DateRangePicker';
import Input from '@/app/components/common/Input';
import IconButton from '@/app/components/common/IconButton';
import Dropdown from '@/app/components/common/Dropdown';
import SearchBar from '@/app/components/common/SearchBar';
import { useModal } from '@/app/components/common/modal/useModal';

const SalesQuoteList = () => {
  const { openModal } = useModal();

  const [searchTerm, setSearchTerm] = useState('');
  const [searchType, setSearchType] = useState('quotationNumber');
  const [statusFilter, setStatusFilter] = useState<QuoteStatus>('ALL');
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [selectedQuotes, setSelectedQuotes] = useState<number[]>([]);
  const [selectedQuotationId, setSelectedQuotationId] = useState<string>('');

  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [debouncedSearchTerm] = useDebounce(searchTerm, 200);
  const [currentPage, setCurrentPage] = useState(1);

  const queryParams = useMemo(
    () => ({
      page: currentPage - 1,
      size: 10,
      startDate: startDate || '',
      endDate: endDate || '',
      status: statusFilter || 'ALL',
      type: searchType || '',
      keyword: debouncedSearchTerm || '',
    }),
    [startDate, endDate, currentPage, statusFilter, searchType, debouncedSearchTerm],
  );

  const {
    data: quoteRes,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ['quoteList', queryParams],
    queryFn: ({ queryKey }) => getQuoteList(queryKey[1] as QuoteQueryParams),
    staleTime: 1000,
  });

  const quotes = quoteRes?.data ?? [];
  const pageInfo = quoteRes?.pageData;
  const totalPages = pageInfo?.totalPages ?? 1;

  const handleViewQuote = (quote: Quote) => {
    setSelectedQuotationId(quote.quotationId);
    openModal(QuoteDetailModal, {
      width: '900px',
      height: '600px',
      title: `견적서 상세보기-${quote.quotationNumber}`,
      $selectedQuotationId: quote.quotationId,
    });
  };

  const handleCheckboxChange = (quotationId: string) => {
    setSelectedQuotationId((prev) => (prev === quotationId ? '' : quotationId));
  };
  const handleViewReview = () => {
    setShowReviewModal(true);
    openModal(QuoteReviewModal, {
      width: '900px',
      title: '견적 검토 요청',
      $selectedQuotationId: selectedQuotationId,
    });
  };

  const role = useRole();
  const quoteOptions = getQuoteSearchKeywordOptions(role as string);

  return (
    <>
      {/* 헤더 및 필터 */}
      <div className="flex flex-col space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <DateRangePicker
            startDate={startDate}
            onStartDateChange={setStartDate}
            endDate={endDate}
            onEndDateChange={setEndDate}
          />
          <div className="flex items-center space-x-4">
            <Dropdown
              placeholder="전체 상태"
              items={QUOTE_STATUS_OPTIONS}
              value={statusFilter}
              onChange={(status: string) => setStatusFilter(status as QuoteStatus)}
              autoSelectFirst
            />
            <SearchBar
              options={quoteOptions}
              onTypeChange={(type: string) => setSearchType(type)}
              onKeywordSearch={(keyword) => {
                setSearchTerm(keyword);
                setCurrentPage(1);
              }}
              placeholder="검색어를 입력하세요"
            />
            <IconButton
              icon="ri-add-line"
              label="견적 검토 요청"
              onClick={handleViewReview}
              disabled={selectedQuotationId === ''}
            />
          </div>
        </div>
      </div>

      {/* 견적 목록 */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200">
        <div className="overflow-x-auto">
          {isLoading ? (
            <TableStatusBox $type="loading" $message="견적서 목록을 불러오는 중입니다..." />
          ) : isError ? (
            <TableStatusBox
              $type="error"
              $message="견적서 목록을 불러오는 중 오류가 발생했습니다."
            />
          ) : !quotes || quotes.length === 0 ? (
            <TableStatusBox $type="empty" $message="등록된 견적서가 없습니다." />
          ) : (
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  {QUOTE_LIST_TABLE_HEADERS.map((header) =>
                    header === '선택' ? (
                      <th key={header} className="px-6 py-3 text-left">
                        <input
                          type="checkbox"
                          disabled
                          checked={selectedQuotes.length === quotes.length && quotes.length > 0}
                          className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                        />
                      </th>
                    ) : (
                      <th
                        key={header}
                        className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                      >
                        {header}
                      </th>
                    ),
                  )}
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {quotes.map((quote) => (
                  <tr key={quote.quotationId} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <input
                        type="checkbox"
                        checked={selectedQuotationId === quote.quotationId}
                        onChange={() => handleCheckboxChange(quote.quotationId)}
                        className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                      />
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {quote.quotationNumber}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {quote.customerName}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {quote.managerName}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {quote.quotationDate}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {quote.dueDate}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      ₩{quote.totalAmount.toLocaleString()}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      <StatusLabel $statusCode={quote.statusCode} />
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-center">
                      <div className="flex items-center justify-center space-x-2">
                        <button
                          onClick={() => handleViewQuote(quote)}
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
        {isError || isLoading ? null : (
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            totalElements={pageInfo?.totalElements}
            onPageChange={(page) => setCurrentPage(page)}
          />
        )}
      </div>
    </>
  );
};

export default SalesQuoteList;
