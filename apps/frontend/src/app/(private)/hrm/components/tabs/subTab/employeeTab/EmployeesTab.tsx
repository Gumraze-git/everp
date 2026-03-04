'use client';

import { fetchDepartmentsDropdown, fetchEmployeesList } from '@/app/(private)/hrm/api/hrm.api';
import {
  EmployeeData,
  EmployeeListRequestParams,
} from '@/app/(private)/hrm/types/HrmEmployeesApiType';
import Dropdown from '@/app/components/common/Dropdown';
import IconButton from '@/app/components/common/IconButton';
import { useModal } from '@/app/components/common/modal/useModal';
import Pagination from '@/app/components/common/Pagination';
import TableStatusBox from '@/app/components/common/TableStatusBox';
import { useQuery } from '@tanstack/react-query';
import { useMemo, useState } from 'react';
import { EmployeeDetailModal } from '@/app/(private)/hrm/components/modals/EmployeeDetailModal';
import { EmployeeEditModal } from '@/app/(private)/hrm/components/modals/EmployeeEditModal';
import EmployeeRegisterModal from '@/app/(private)/hrm/components/modals/EmployeeRegisterModal';
import { useDropdown } from '@/app/hooks/useDropdown';
import { useDebouncedKeyword } from '@/app/hooks/useDebouncedKeyword';
import Input from '@/app/components/common/Input';
import Button from '@/app/components/common/Button';

export default function EmployeesTab() {
  const { keyword, handleKeywordChange, debouncedKeyword } = useDebouncedKeyword();

  const [selectedDepartment, setSelectedDepartment] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;

  // 모달창
  const { openModal } = useModal();

  // 부서 드롭다운
  const { options: departmentsOptions } = useDropdown(
    'departmentsDropdown',
    fetchDepartmentsDropdown,
    'include',
  );

  const employeesQueryParams = useMemo(
    (): EmployeeListRequestParams => ({
      departmentId: selectedDepartment || undefined,
      name: debouncedKeyword,
      page: currentPage - 1,
      size: pageSize,
    }),
    [selectedDepartment, debouncedKeyword, currentPage, pageSize],
  );

  const {
    data: employeesData,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ['employeesList', employeesQueryParams],
    queryFn: () => fetchEmployeesList(employeesQueryParams),
    staleTime: 1000,
  });

  // console.log(employeesData);

  const employees = employeesData?.content ?? [];
  const pageInfo = employeesData?.page;

  const handleFilterChange = () => {
    setCurrentPage(1);
  };

  const handleEditEmployeeDetail = (emp: EmployeeData) => {
    openModal(EmployeeEditModal, { title: '직원정보 수정', employee: emp });
  };

  const handleViewEmployeeDetail = (emp: EmployeeData) => {
    openModal(EmployeeDetailModal, {
      title: '직원 상세보기',
      employee: emp,
      onEdit: () => handleEditEmployeeDetail(emp),
    });
  };

  const handleViewEmployeeRegister = () => {
    openModal(EmployeeRegisterModal, {
      title: '신규 직원 등록',
    });
  };

  return (
    <>
      <div className="flex justify-end items-center gap-4 mb-6 p-2 rounded-lg">
        <Dropdown
          placeholder="전체 부서"
          items={departmentsOptions}
          value={selectedDepartment}
          onChange={(dept: string) => setSelectedDepartment(dept)}
        />
        <Input
          value={keyword}
          onChange={handleKeywordChange}
          icon="ri-search-line"
          placeholder="직원 이름 검색"
        />
        <IconButton
          icon="ri-user-add-line"
          label="신규 직원 등록"
          onClick={handleViewEmployeeRegister}
        />
      </div>

      <div className="overflow-x-auto">
        {isLoading ? (
          <TableStatusBox $type="loading" $message="직원 정보를 불러오는 중입니다..." />
        ) : isError ? (
          <TableStatusBox $type="error" $message="직원 정보를 불러오는 중 오류가 발생했습니다." />
        ) : !employees || employees.length === 0 ? (
          <TableStatusBox $type="empty" $message="등록된 직원이 없습니다." />
        ) : (
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  직원 정보
                </th>
                <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  부서/직급
                </th>
                <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  입사일
                </th>
                <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  연락처
                </th>
                <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  작업
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {employees.map((employee) => (
                <tr key={employee.employeeId} className="text-center">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <div className="text-sm font-medium text-gray-900">{employee.name}</div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <div className="text-sm text-gray-900">{employee.department}</div>
                      <div className="text-sm text-gray-500">{employee.position}</div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {employee.hireDate}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <div className="text-sm text-gray-900">{employee.phone}</div>
                      <div className="text-sm text-gray-500">{employee.email}</div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <Button
                      label="상세보기"
                      size="sm"
                      variant="ghost"
                      onClick={() => handleViewEmployeeDetail(employee)}
                    />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

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
