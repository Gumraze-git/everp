import { Page, PageRequest } from '@/app/types/Page';

export interface EmployeeData {
  employeeId: string;
  employeeNumber: string;
  name: string;
  email: string;
  phone: string;
  position: string;
  department: string;
  statusCode: string;
  hireDate: string;
  birthDate: string;
  address: string;
  createdAt: string;
  updatedAt: string;
}

export interface EmployeeListResponse {
  content: EmployeeData[];
  page: Page;
}

// 응답 요청시 request params
export interface EmployeeListRequestParams extends PageRequest {
  departmentId?: string;
  name?: string;
}

export interface EmployeeRegisterRequest {
  name: string;
  departmentId: string;
  positionId: string;
  email: string;
  phoneNumber: string;
  hireDate: string;
  birthDate: string;
  baseAddress: string;
  detailAddress: string;
}

export interface EmployeeUpdateRequest {
  employeeName: string;
  departmentId: string;
  positionId: string;
}
