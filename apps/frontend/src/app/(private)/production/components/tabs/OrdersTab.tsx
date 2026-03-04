'use client';
import { useMemo, useState } from 'react';
import Dropdown from '@/app/components/common/Dropdown';
import Button from '@/app/components/common/Button';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  fetchMrpAvailableStatusDropdown,
  fetchMrpOrdersList,
  fetchMrpQuotationsDropdown,
  postMrpConvert,
} from '../../api/production.api';
import TableStatusBox from '@/app/components/common/TableStatusBox';
import Pagination from '@/app/components/common/Pagination';
import {
  FetchMrpOrdersListParams,
  MrpOrdersListData,
  MrpOrdersListResponse,
} from '../../types/MrpOrdersApiType';
import { MrpOrdersConvertReqeustBody } from '../../types/MrpOrdersConvertApiType';
import { useRouter } from 'next/navigation';
import { useDropdown } from '@/app/hooks/useDropdown';
import StatusLabel from '@/app/components/common/StatusLabel';

export default function OrdersTab() {
  // mrp 순소요 - 견적 드롭다운
  const { options: mrpQuotationOptions } = useDropdown(
    'mrpQuotationsDropdown',
    fetchMrpQuotationsDropdown,
  );
  // mrp 순소요 - 가용 재고 상태 드롭다운
  const { options: mrpAvailableStatusOptions } = useDropdown(
    'mrpAvailableStatusDropdown',
    fetchMrpAvailableStatusDropdown,
  );

  const [selectedQuote, setSelectedQuote] = useState('');
  const [selectedStockStatus, setSelectedStockStatus] = useState('');

  const [selectedRequirements, setSelectedRequirements] = useState<string[]>([]);

  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;

  const queryParams = useMemo(
    (): FetchMrpOrdersListParams => ({
      quotationId: selectedQuote,
      availableStatusCode: selectedStockStatus,
      page: currentPage - 1,
      size: pageSize,
    }),
    [selectedQuote, selectedStockStatus, currentPage],
  );

  const {
    data: orders,
    isLoading,
    isError,
  } = useQuery<MrpOrdersListResponse>({
    queryKey: ['mrpOrdersList', queryParams],
    queryFn: ({ queryKey }) => fetchMrpOrdersList(queryKey[1] as FetchMrpOrdersListParams),
    staleTime: 1000,
  });

  const orderItems: MrpOrdersListData[] = orders?.content ?? [];
  const pageInfo = orders?.page;

  const totalPages = pageInfo?.totalPages ?? 1;

  const handleSelectAllRequirements = () => {
    if (!orders) return;
    if (selectedRequirements.length === orderItems.length) {
      setSelectedRequirements([]);
    } else {
      setSelectedRequirements(orderItems.map((item) => item.itemId));
    }
  };

  const handleRequirementSelection = (id: string) => {
    setSelectedRequirements((prev) =>
      prev.includes(id) ? prev.filter((itemId) => itemId !== id) : [...prev, id],
    );
  };

  // 계획 주문 전환 mutation
  const router = useRouter();
  const queryClient = useQueryClient();

  const { mutate: mrpConvert } = useMutation({
    mutationFn: (body: MrpOrdersConvertReqeustBody) => postMrpConvert(body),
    onSuccess: () => {
      alert('계획 주문 전환이 완료되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['mrpOrdersList'] });
      router.push('/production?tab=mrp&subTab=orders');
    },
    onError: (error) => {
      console.log('계획 주문 전환 실패', error);
      alert('계획 주문 전환에 실패했습니다.');
    },
  });

  // 계획 주문 전환 실행
  const handleConvertToPlannedOrder = () => {
    if (selectedRequirements.length === 0) {
      alert('전환할 항목을 선택해주세요.');
      return;
    }

    const selectedItems = orderItems
      .filter((item) => selectedRequirements.includes(item.itemId))
      .map((item) => ({
        quotationId: item.quotationId,
        itemId: item.itemId,
        quantity: item.requiredQuantity,
      }));

    const body = {
      items: selectedItems,
    };

    mrpConvert(body);
  };

  return (
    <>
      <div className="flex justify-end items-center p-4 border-b border-gray-200">
        <div className="flex gap-4 items-center">
          <Dropdown
            placeholder="견적 선택"
            items={mrpQuotationOptions}
            value={selectedQuote}
            onChange={(quote: string) => {
              setSelectedQuote(quote);
              setCurrentPage(1);
            }}
            autoSelectFirst
          />
          <Dropdown
            placeholder="전체 상태"
            items={mrpAvailableStatusOptions}
            value={selectedStockStatus}
            onChange={(status: string) => {
              setSelectedStockStatus(status);
              setCurrentPage(1);
            }}
          />
          <Button
            label="계획 주문 전환"
            onClick={handleConvertToPlannedOrder}
            disabled={selectedRequirements.length === 0}
          />
        </div>
      </div>

      {isLoading ? (
        <TableStatusBox $type="loading" $message="순소요 목록을 불러오는 중입니다..." />
      ) : isError ? (
        <TableStatusBox $type="error" $message="순소요 목록을 불러오는 중 오류가 발생했습니다." />
      ) : orderItems.length === 0 ? (
        <TableStatusBox $type="empty" $message="조회된 순소요 데이터가 없습니다" />
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr className="whitespace-nowrap text-center">
                <th className="px-4 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  <input
                    type="checkbox"
                    checked={selectedRequirements.length === orderItems.length}
                    onChange={handleSelectAllRequirements}
                    className="rounded border-gray-300 text-purple-600 focus:ring-purple-500 cursor-pointer"
                  />
                </th>
                <th className="px-4 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  자재
                </th>
                <th className="px-4 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  소요 수량
                </th>
                <th className="px-4 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  가용 재고
                </th>
                <th className="px-4 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  소모량
                </th>
                <th className="px-4 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  가용 재고 상태
                </th>
                <th className="px-4 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  부족 재고
                </th>
                <th className="px-4 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  자재 유형
                </th>
                <th className="px-4 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  구매 권장일
                </th>
                <th className="px-4 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  예상 도착일
                </th>
                <th className="px-4 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  공급사
                </th>
                <th className="px-4 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  전환 상태
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {orderItems.map((item) => (
                <tr key={item.itemId} className="hover:bg-gray-50 whitespace-nowrap truncate">
                  <td className="px-4 py-3">
                    {item.convertStatus !== 'NOT_CONVERTED' ? (
                      <input
                        type="checkbox"
                        disabled
                        className="rounded border-gray-300 text-gray-300"
                      />
                    ) : (
                      <input
                        type="checkbox"
                        checked={selectedRequirements.includes(item.itemId)}
                        onChange={() => handleRequirementSelection(item.itemId)}
                        className="rounded border-gray-300 cursor-pointer"
                      />
                    )}
                  </td>
                  <td className="px-4 py-3 text-sm font-medium text-gray-900">{item.itemName}</td>
                  <td className="px-4 py-3 text-sm text-gray-900">
                    {item.requiredQuantity.toLocaleString()}
                  </td>

                  <td className="px-4 py-3 text-sm text-gray-900">
                    {item.availableStock.toLocaleString()}
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-900">
                    {item.consumptionQuantity.toLocaleString()}
                  </td>
                  <td className="px-4 py-3">
                    <StatusLabel $statusCode={item.availableStatusCode} />
                  </td>
                  <td className="px-4 py-3 text-sm">
                    {item.shortageQuantity > 0 ? (
                      <span className="text-red-600 font-medium">
                        {item.shortageQuantity.toLocaleString()}
                      </span>
                    ) : (
                      0
                    )}
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-900">
                    <StatusLabel $statusCode={item.itemType} />
                  </td>

                  <td className="px-4 py-3 text-sm text-gray-900">
                    {item.procurementStartDate || '-'}
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-900">
                    {item.expectedArrivalDate || '-'}
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-900">{item.supplierCompanyName}</td>
                  <td className="px-4 py-3 text-sm font-medium text-gray-900">
                    <StatusLabel $statusCode={item.convertStatus} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

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
