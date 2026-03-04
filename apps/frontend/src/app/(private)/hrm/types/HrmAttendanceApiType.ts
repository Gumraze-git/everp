import { Page, PageRequest } from '@/app/types/Page';

export interface EmployeeInfo {
  employeeId: string;
  employeeName: string;
  departmentId: string;
  department: string;
  positionId: string;
  position: string;
}

export interface AttendanceListData {
  timerecordId: string;
  employee: EmployeeInfo;
  workDate: string; // YYYY-MM-DD
  checkInTime: string; // ISO 8601: YYYY-MM-DDTHH:mm:ss
  checkOutTime: string; // ISO 8601
  totalWorkMinutes: number;
  overtimeMinutes: number;
  statusCode: string; // "ON_TIME" 등
}

export interface AttendanceListResponse {
  content: AttendanceListData[];
  page: Page;
}

export interface AttendanceRequestParams extends PageRequest {
  date: string;
  department?: string;
  name?: string;
}

export interface UpdateTimeRecord {
  timerecordId: string;
  checkInTime: string;
  checkOutTime: string;
}
