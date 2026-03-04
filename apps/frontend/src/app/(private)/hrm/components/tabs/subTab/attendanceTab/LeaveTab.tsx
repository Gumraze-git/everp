// tabs/LeaveTab.tsx
'use client';
import {
  fetchDepartmentsDropdown,
  fetchLeaveList,
  postLeaveReject,
  postLeaveRelease,
} from '@/app/(private)/hrm/api/hrm.api';
import { LeaveRequestParams } from '@/app/(private)/hrm/types/HrmLeaveApiType';
import Dropdown from '@/app/components/common/Dropdown';
import Input from '@/app/components/common/Input';
import { useModal } from '@/app/components/common/modal/useModal';
import Pagination from '@/app/components/common/Pagination';
import StatusLabel from '@/app/components/common/StatusLabel';
import { useDebouncedKeyword } from '@/app/hooks/useDebouncedKeyword';
import { useDropdown } from '@/app/hooks/useDropdown';
import { getQueryClient } from '@/lib/queryClient';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useMemo, useState } from 'react';

export default function LeaveTab() {
  // --- 모달 출력 ---
  const { keyword, handleKeywordChange, debouncedKeyword } = useDebouncedKeyword();

  // 부서 드롭다운
  const { options: departmentsOptions } = useDropdown(
    'departmentsDropdown',
    fetchDepartmentsDropdown,
    'include',
  );

  // --- 드롭다운 ---
  const [selectedDepartment, setSelectedDepartment] = useState(''); // 부서

  // --- 페이지 네이션 ---
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;

  const leaveQueryParams = useMemo(
    (): LeaveRequestParams => ({
      department: selectedDepartment || undefined,
      name: debouncedKeyword,
      page: currentPage - 1,
      size: pageSize,
    }),
    [selectedDepartment, debouncedKeyword, currentPage],
  );

  const queryClient = useQueryClient();
  const {
    data: leaveData,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ['leaveList', leaveQueryParams],
    queryFn: () => fetchLeaveList(leaveQueryParams),
  });

  const leaveList = leaveData?.content ?? [];
  const pageInfo = leaveData?.page;

  const { mutate: approveLeave } = useMutation({
    mutationFn: (requestId: string) => postLeaveRelease(requestId),
    onSuccess: () => {
      alert('승인이 완료되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['leaveList'], exact: false });
    },
    onError: (error) => {
      alert(`휴가 승인 중 오류가 발생했습니다. ${error}`);
    },
  });

  const { mutate: rejectLeave } = useMutation({
    mutationFn: (requestId: string) => postLeaveReject(requestId),
    onSuccess: () => {
      alert('반료되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['leaveList'], exact: false });
    },
    onError: (error) => {
      alert(`휴가 반려 중 오류가 발생했습니다. ${error}`);
    },
  });

  const handleApprove = (requestId: string) => {
    if (confirm('해당 요청을 승인하시겠습니까?')) {
      approveLeave(requestId);
    }
  };

  const handleReject = (requestId: string) => {
    if (confirm('해당 요청을 반려하시겠습니까?')) {
      rejectLeave(requestId);
    }
  };

  return (
    <div className="mt-8">
      <div className="flex items-center justify-end mb-4">
        <div className="flex items-center gap-3">
          <Dropdown
            placeholder="전체 부서"
            items={departmentsOptions}
            value={selectedDepartment}
            onChange={(dept: string) => {
              setSelectedDepartment(dept);
              setCurrentPage(1);
            }}
          />

          <Input
            value={keyword}
            onChange={handleKeywordChange}
            icon="ri-search-line"
            placeholder="직원 이름 검색"
          />
        </div>
      </div>
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                직원 정보
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                휴가 유형
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                시작 일자
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                종료 일자
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                일수
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                잔여 연차
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                작업
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {leaveList.map((leave) => (
              <tr key={leave.leaveRequestId} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap">
                  <div>
                    <div className="text-sm font-medium text-gray-900">
                      {leave.employee.employeeName}
                    </div>
                    <div className="text-sm text-gray-500">{leave.employee.position}</div>
                  </div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  <StatusLabel $statusCode={leave.leaveType} />
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {leave.startDate.split('T')[0]}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {leave.endDate.split('T')[0]}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {leave.numberOfLeaveDays}일
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {leave.remainingLeaveDays}일
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  {/* 휴가 승인 대기중일 때만 */}
                  {leave.status === 'PENDING' ? (
                    <div className="flex space-x-2">
                      <button
                        onClick={() => handleApprove(leave.leaveRequestId)}
                        className="text-green-600 hover:text-green-900 cursor-pointer"
                        title="승인"
                      >
                        <i className="ri-check-line"></i>
                      </button>
                      <button
                        onClick={() => handleReject(leave.leaveRequestId)}
                        className="text-red-600 hover:text-red-900 cursor-pointer"
                        title="반려"
                      >
                        <i className="ri-close-line"></i>
                      </button>
                    </div>
                  ) : (
                    <StatusLabel $statusCode={leave.status} />
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Pagination
        currentPage={currentPage}
        totalPages={pageInfo?.totalPages ?? 1}
        totalElements={pageInfo?.totalElements}
        onPageChange={(page) => setCurrentPage(page)}
      />
    </div>
  );
}
