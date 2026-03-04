import { Page, PageRequest } from '@/app/types/Page';

export interface TrainingListData {
  employeeId: string;
  name: string;
  department: string;
  position: string;
  completedCount: number;
  inProgressCount: number;
  requiredMissingCount: number;
  lastTrainingDate: string;
}

export interface TrainingListResponse {
  items: TrainingListData[];
  page: Page;
}

export interface TrainingRequestParams extends PageRequest {
  department?: string;
  name?: string;
}

export interface TrainingDetailResponse {
  employeeId: string;
  // employeeNumber: string;
  employeeName: string;
  department: string;
  position: string;
  completedCount: number;
  // inProgressCount: number;
  requiredMissingCount: number;
  lastTrainingDate: string;
  programHistory: {
    programId: string;
    programName: string;
    programStatus: 'COMPLETED' | 'INCOMPLETED';
    completedAt: string;
  }[];
}
