import { Page, PageRequest } from '@/app/types/Page';

export interface PayrollRequestParams extends PageRequest {
  year: number;
  month: number;
  name?: string;
  department?: string;
  statusCode?: string;
}

export interface EmployeeSummary {
  employeeId: string;
  employeeName: string;
  departmentId: string;
  department: string;
  positionId: string;
  position: string;
}

export interface PaySummary {
  basePay: number;
  overtimePay: number;
  deduction: number;
  netPay: number;
  statusCode: string;
}

export interface PayRollList {
  payrollId: string;
  employee: EmployeeSummary;
  pay: PaySummary;
}

export interface PayRollListResponse {
  content: PayRollList[];
  page: Page;
}

export interface PayItemData {
  itemContent: string;
  itemSum: number;
}

export interface PayRollDetailResponse {
  payrollId: string;
  employee: EmployeeSummary;
  pay: {
    basePay: number; // 기본급
    basePayItem: PayItemData[];
    overtimePay: number; // 초과 근무비
    overtimePayItem: PayItemData[];
    deduction: number; // 공제액
    deductionItem: PayItemData[];
    netPay: number; // 실지급액
  };
  statusCode: string;
  expectedDate: string;
}

export interface PayRollCompleteRequestParams {
  payrollId: string;
}
