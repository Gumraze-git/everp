export interface EmployeeData {
  employeeId: string;
  employeeName: string;
  position: string;
  hireDate: string;
}

export interface DepartmentsData {
  departmentId: string;
  departmentNumber: string;
  departmentName: string;
  description: string;
  managerName: string;
  managerId: string;
  location: string;
  statusCode: string;
  employeeCount: number;
  establishedDate: string;
  employees: EmployeeData[];
}

export interface DepartmentsListResponse {
  content: DepartmentsData[];
}

export interface DepartmentsRequestBody {
  managerId: string;
  description: string;
}
