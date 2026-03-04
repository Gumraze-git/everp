// 직급 데이터 형식
export interface PositionData {
  positionId: string;
  positionName: string;
  headCount: number;
  payment: number;
}

export type PositionDataResponse = PositionData[];

export interface EmployeeSummary {
  employeeId: string;
  employeeCode: string;
  employeeName: string;
  positionId: string;
  position: string;
  departmentId: string;
  department: string;
  hireDate: string;
}

export interface PositionDetailResponse extends PositionData {
  employees: EmployeeSummary[];
}
