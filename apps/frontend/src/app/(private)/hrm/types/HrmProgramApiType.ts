import { Page, PageRequest } from '@/app/types/Page';

export interface ProgramListData {
  programId: string;
  programName: string;
  statusCode: string;
  category: string;
  trainingHour: number;
  isOnline: boolean;
  capacity: number;
}

export interface ProgramListResponse {
  content: ProgramListData[];
  page: Page;
}

export interface ProgramRequestParams extends PageRequest {
  name?: string;
  status?: string;
  category?: string;
}

export interface designatedEmployeeSummary {
  employeeId: string;
  employeeName: string;
  department: string;
  position: string;
  statusCode: string;
  completedAt: null;
}

export interface ProgramDetailResponse extends Page, Omit<ProgramListData, 'capacity'> {
  startDate: string;
  designatedEmployee: designatedEmployeeSummary[];
}

export interface CreateProgramRequest {
  programName: string;
  category: string;
  trainingHour: number;
  isOnline: boolean;
  startDate: string;
  capacity: number;
  requiredDepartments: string[];
  requiredPositions: string[];
  description: string;
}

export interface UpdateProgramRequest {
  programId: string;
  programName: string;
  statusCode: string;
}

export interface UpdateProgramToEmployeeRequest {
  employeeId: string;
  programId: string;
}
