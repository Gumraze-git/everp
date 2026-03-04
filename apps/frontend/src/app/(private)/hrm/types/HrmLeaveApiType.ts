import { Page, PageRequest } from '@/app/types/Page';

export interface LeaveListData {
  leaveRequestId: string;
  employee: EmployeeSummary;
  leaveType: string;
  startDate: string;
  endDate: string;
  numberOfLeaveDays: number;
  remainingLeaveDays: number;
  status: string;
}

export interface EmployeeSummary {
  employeeId: string;
  employeeName: string;
  department: string;
  position: string;
}

export interface LeaveListResponse {
  content: LeaveListData[];
  page: Page;
}

export interface LeaveRequestParams extends PageRequest {
  department?: string;
  name?: string;
  type?: string;
}
