'use client';
import { useQuery } from '@tanstack/react-query';
import { useState, useMemo } from 'react';
import {
  fetchDepartmentsDropdown,
  fetchPayRollList,
  fetchPayrollStatusDropdown,
} from '@/app/(private)/hrm/api/hrm.api';
import { KeyValueItem } from '@/app/types/CommonType';
import Dropdown from '@/app/components/common/Dropdown';
import { PayrollDetailModal } from '@/app/(private)/hrm/components/modals/PayrollDetailModal';
import Pagination from '@/app/components/common/Pagination';
import { PayRollList, PayrollRequestParams } from '@/app/(private)/hrm/types/HrmPayrollApiType';
import { useModal } from '@/app/components/common/modal/useModal';
import { useDropdown } from '@/app/hooks/useDropdown';
import { useDebouncedKeyword } from '@/app/hooks/useDebouncedKeyword';
import Input from '@/app/components/common/Input';
import StatusLabel from '@/app/components/common/StatusLabel';
import Button from '@/app/components/common/Button';

export default function PayrollManagement() {
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
  // 상태 드롭다운
  const { options: statusOptions } = useDropdown(
    'payrollStatusDropdown',
    fetchPayrollStatusDropdown,
    'include',
  );

  // --- 선택된 드롭다운 상태 ---
  const [selectedDepartment, setSelectedDepartment] = useState(''); // 부서
  const [selectedPayrollStatus, setSelectedPayrollStatus] = useState(''); // 상태

  // 년도와 월
  const now = new Date();
  const currentYear = String(now.getFullYear());
  const currentMonth = String(now.getMonth() + 1);
  const [selectedYear, setSelectedYear] = useState(currentYear);
  const [selectedMonth, setSelectedMonth] = useState(currentMonth);

  // --- 페이지 네이션 ---
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;

  // 년도 옵션 (최근 5년)
  const yearOptions: KeyValueItem[] = useMemo(() => {
    const currentYear = new Date().getFullYear();
    const years = [];
    for (let i = 0; i < 5; i++) {
      const year = (currentYear - i).toString();
      years.push({ key: year, value: `${year}년` });
    }
    return years;
  }, []);

  // 월 옵션
  const monthOptions: KeyValueItem[] = useMemo(() => {
    return Array.from({ length: 12 }, (_, i) => {
      const month = String(i + 1);
      return { key: month, value: `${i + 1}월` };
    });
  }, []);

  const payrollQueryParams = useMemo(
    (): PayrollRequestParams => ({
      year: Number(selectedYear),
      month: Number(selectedMonth),
      name: debouncedKeyword,
      department: selectedDepartment || undefined,
      statusCode: selectedPayrollStatus || undefined,
      page: currentPage - 1,
      size: pageSize,
    }),
    [
      selectedYear,
      selectedMonth,
      debouncedKeyword,
      selectedDepartment,
      selectedPayrollStatus,
      currentPage,
    ],
  );

  const {
    data: payrollData,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ['payrollList', payrollQueryParams],
    queryFn: () => fetchPayRollList(payrollQueryParams),
  });

  const payrollList = payrollData?.content ?? [];
  const pageInfo = payrollData?.page;

  const handleViewPayrollDetail = (payroll: PayRollList) => {
    openModal(PayrollDetailModal, {
      title: '급여 상세정보',
      payrollId: payroll.payrollId,
      payStatus: payroll.pay.statusCode,
      width: '600px',
      height: '680px',
    });
  };

  return (
    <>
      <div>
        {/* 필터링 및 검색 */}
        <div className="flex justify-between items-center gap-2 mb-4">
          <div className="flex pl-1 gap-2">
            <Dropdown
              placeholder="전체 년도"
              items={yearOptions}
              value={selectedYear}
              onChange={(year: string) => setSelectedYear(year)}
            />
            <Dropdown
              placeholder="전체 월"
              items={monthOptions}
              value={selectedMonth}
              onChange={(month: string) => setSelectedMonth(month)}
            />
          </div>

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
              items={statusOptions}
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
                  이름
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  부서
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  직급
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  기본급
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  초과근무비
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  공제액
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  실지급액
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  상태
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  작업
                </th>
              </tr>
            </thead>
            <tbody>
              {payrollList.map((payroll) => (
                <tr key={payroll.payrollId} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {payroll.employee.employeeName}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {payroll.employee.department}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {payroll.employee.position}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {payroll.pay.basePay.toLocaleString()}원
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {payroll.pay.overtimePay.toLocaleString()}원
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {payroll.pay.deduction.toLocaleString()}원
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {payroll.pay.netPay.toLocaleString()}원
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <StatusLabel $statusCode={payroll.pay.statusCode} />
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <Button
                      label="상세보기"
                      size="sm"
                      variant="ghost"
                      onClick={() => handleViewPayrollDetail(payroll)}
                    />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {isError || isLoading ? null : (
          <Pagination
            currentPage={currentPage}
            totalPages={pageInfo?.totalPages ?? 1}
            totalElements={pageInfo?.totalElements}
            onPageChange={(page) => setCurrentPage(page)}
          />
        )}
      </div>
    </>
  );
}
