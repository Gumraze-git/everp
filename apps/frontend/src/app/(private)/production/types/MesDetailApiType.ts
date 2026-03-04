export interface MesOperationManagerData {
  id: number;
  name: string;
}

export interface MesOperationData {
  mesOperationLogId: string;
  operationNumber: string;
  operationName: string;
  sequence: number;
  statusCode: string;
  startedAt: string;
  finishedAt: string;
  durationHours: number;
  manager: MesOperationManagerData;
  canStart: boolean;
  canComplete: boolean;
}

export interface MesPlanData {
  startDate: string;
  dueDate: string;
}

export interface MesDetailResponse {
  mesId: string;
  mesNumber: string;
  productId: string;
  productName: string;
  quantity: number;
  uomName: string;
  progressPercent: number;
  statusCode: string;
  plan: MesPlanData;
  currentOperation: string;
  operations: MesOperationData[];
  canStartMes: boolean;
  canCompleteMes: boolean;
}
