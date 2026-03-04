// tabs/AttendanceTab.tsx
'use client';
import {
  fetchAttendanceList,
  fetchAttendanceStatusDropdown,
  fetchDepartmentsDropdown,
} from '@/app/(private)/hrm/api/hrm.api';
import {
  AttendanceListData,
  AttendanceRequestParams,
} from '@/app/(private)/hrm/types/HrmAttendanceApiType';
import Dropdown from '@/app/components/common/Dropdown';
import { useModal } from '@/app/components/common/modal/useModal';
import Pagination from '@/app/components/common/Pagination';
import { useQuery } from '@tanstack/react-query';
import { useState, useMemo } from 'react';
import { AttendanceEditModal } from '@/app/(private)/hrm/components/modals/AttendanceEditModal';
import { formatMinutesToHourMin, formatTime } from '@/app/utils/date';
import { useDropdown } from '@/app/hooks/useDropdown';
import { useDebouncedKeyword } from '@/app/hooks/useDebouncedKeyword';
import Input from '@/app/components/common/Input';
import CalendarButton from '@/app/components/common/CalendarButton';
import StatusLabel from '@/app/components/common/StatusLabel';

export default function AttendanceTab() {
  // --- 모달 출력 ---
  const { openModal } = useModal();

  const { keyword, handleKeywordChange, debouncedKeyword } = useDebouncedKeyword();

  // --- 드롭다운 ---
  // 부서 드롭다운
  const { options: departmentsOptions } = useDropdown(
    'departmentsDropdown',
    fetchDepartmentsDropdown,
    'include',
  );

  const {
    data: statusData,
    isLoading: statusLoading,
    isError: errorLoading,
  } = useQuery({
    queryKey: ['attendanceStatusDropdown'],
    queryFn: fetchAttendanceStatusDropdown,
    staleTime: Infinity,
  });

  // --- 선택된 드롭다운 상태 ---
  const [selectedDepartment, setSelectedDepartment] = useState(''); // 부서
  const [selectedPayrollStatus, setSelectedPayrollStatus] = useState(''); // 급여 지급 상태

  // --- 페이지 네이션 ---
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;

  // --- 달력 데이터 ---
  const today = new Date().toISOString().split('T')[0];
  const [selectedDate, setSelectedDate] = useState<string | null>(today);

  const attendanceQueryParams = useMemo(
    (): AttendanceRequestParams => ({
      date: selectedDate ?? '',
      department: selectedDepartment || undefined,
      name: debouncedKeyword,
      page: currentPage - 1,
      size: pageSize,
    }),
    [selectedDate, selectedDepartment, debouncedKeyword, currentPage],
  );

  const {
    data: attendanceData,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ['attendanceList', attendanceQueryParams],
    queryFn: () => fetchAttendanceList(attendanceQueryParams),
  });

  const attendanceList = attendanceData?.content ?? [];
  const pageInfo = attendanceData?.page;

  const handleEditAttendance = (attendance: AttendanceListData) => {
    openModal(AttendanceEditModal, {
      title: '출퇴근 정보 수정',
      attendance: attendance,
    });
  };

  return (
    <div className="mt-8">
      <div className="flex justify-between items-center mb-4">
        <CalendarButton
          selectedDate={selectedDate}
          onDateChange={setSelectedDate}
          placeholder="날짜 선택"
        />

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

          <Dropdown
            placeholder="전체 상태"
            items={statusData ?? []}
            value={selectedPayrollStatus}
            onChange={(status: string) => {
              setSelectedPayrollStatus(status);
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
                출근 시간
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                퇴근 시간
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                근무 시간
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                초과 근무
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                상태
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {attendanceList.map((attend) => (
              <tr key={attend.timerecordId} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap">
                  <div>
                    <div className="text-sm font-medium text-gray-900">
                      {attend.employee?.employeeName || '-'}
                    </div>
                    <div className="text-sm text-gray-500">{attend.employee?.position || '-'}</div>
                  </div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {formatTime(attend.checkInTime)}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {formatTime(attend.checkOutTime)}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {formatMinutesToHourMin(attend.totalWorkMinutes)}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {formatMinutesToHourMin(attend.overtimeMinutes)}
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <StatusLabel $statusCode={attend.statusCode} />
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
