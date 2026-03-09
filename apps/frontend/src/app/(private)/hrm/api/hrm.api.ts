import { HRM_ENDPOINTS } from '@/app/(private)/hrm/api/hrm.endpoints';
import axios from '@/lib/axiosInstance';
import { HrmStatResponse } from '@/app/(private)/hrm/types/HrmStatsApiType';
import {
  EmployeeListRequestParams,
  EmployeeListResponse,
  EmployeeRegisterRequest,
  EmployeeUpdateRequest,
} from '@/app/(private)/hrm/types/HrmEmployeesApiType';
import {
  PositionDataResponse,
  PositionDetailResponse,
} from '@/app/(private)/hrm/types/HrmPositionsApiType';
import {
  DepartmentsListResponse,
  DepartmentsRequestBody,
} from '@/app/(private)/hrm/types/HrmDepartmentsApiType';
import {
  PayrollRequestParams,
  PayRollDetailResponse,
  PayRollListResponse,
  PayRollCompleteRequestParams,
} from '@/app/(private)/hrm/types/HrmPayrollApiType';
import {
  TrainingDetailResponse,
  TrainingListResponse,
  TrainingRequestParams,
} from '@/app/(private)/hrm/types/HrmTrainingApiType';
import {
  CreateProgramRequest,
  ProgramDetailResponse,
  ProgramListResponse,
  ProgramRequestParams,
  UpdateProgramRequest,
  UpdateProgramToEmployeeRequest,
} from '@/app/(private)/hrm/types/HrmProgramApiType';
import {
  AttendanceListResponse,
  AttendanceRequestParams,
  UpdateTimeRecord,
} from '@/app/(private)/hrm/types/HrmAttendanceApiType';
import { LeaveListResponse, LeaveRequestParams } from '@/app/(private)/hrm/types/HrmLeaveApiType';
import { KeyValueItem } from '@/app/types/CommonType';

// 인적자원관리 지표 조회
export const fetchHrmStats = async (): Promise<HrmStatResponse | null> => {
  try {
    const res = await axios.get<HrmStatResponse>(`${HRM_ENDPOINTS.STATISTICS}`);
    return res.data;
  } catch (error) {
    console.log(error);
    return null;
  }
};

// 직원 목록 조회
export const fetchEmployeesList = async (
  params: EmployeeListRequestParams,
): Promise<EmployeeListResponse> => {
  const res = await axios.get<EmployeeListResponse>(`${HRM_ENDPOINTS.EMPLOYEE}`, {
    params,
  });
  return res.data;
};

// 직급 목록 조회
export const fetchPositionsList = async (): Promise<PositionDataResponse> => {
  const res = await axios.get<PositionDataResponse>(`${HRM_ENDPOINTS.POSITIONS}`);
  return res.data;
};

// 직급 상세 조회
export const fetchPositionsDetail = async (positionId: string): Promise<PositionDetailResponse> => {
  const res = await axios.get<PositionDetailResponse>(
    `${HRM_ENDPOINTS.POSITION_DETAIL(positionId)}`,
  );
  return res.data;
};

// 부서 목록 조회
export const fetchDepartmentsList = async (): Promise<DepartmentsListResponse> => {
  const res = await axios.get<DepartmentsListResponse>(`${HRM_ENDPOINTS.DEPARTMENTS}`);
  return res.data;
};

// 부서 수정
export const patchDepartments = async (departmentId: string, body: DepartmentsRequestBody) => {
  await axios.patch(`${HRM_ENDPOINTS.DEPARTMENTS_DETAIL(departmentId)}`, body);
};

// 월별 급여 목록 조회
export const fetchPayRollList = async (
  params: PayrollRequestParams,
): Promise<PayRollListResponse> => {
  const res = await axios.get<PayRollListResponse>(`${HRM_ENDPOINTS.PAYROLL}`, {
    params,
  });
  return res.data;
};

// 월별 급여 상세 조회
export const fetchPayRollDetail = async (payrollId: string): Promise<PayRollDetailResponse> => {
  const res = await axios.get<PayRollDetailResponse>(`${HRM_ENDPOINTS.PAYROLL_DETAIL(payrollId)}`);
  return res.data;
};

// 직원 교육 목록 조회
export const fetchTrainingList = async (
  params: TrainingRequestParams,
): Promise<TrainingListResponse> => {
  const res = await axios.get<TrainingListResponse>(`${HRM_ENDPOINTS.TRAINING_STATUS}`, { params });
  return res.data;
};

// 직원 교육 상세 조회
export const fetchTrainingDetail = async (employeeId: string): Promise<TrainingDetailResponse> => {
  const res = await axios.get<TrainingDetailResponse>(
    `${HRM_ENDPOINTS.TRAINING_EMPLOYEE_DETAIL(employeeId)}`,
  );
  return res.data;
};

// 교육 프로그램 목록 조회
export const fetchProgramList = async (
  params: ProgramRequestParams,
): Promise<ProgramListResponse> => {
  const res = await axios.get<ProgramListResponse>(`${HRM_ENDPOINTS.PROGRAM}`, {
    params,
  });
  return res.data;
};

// 교육 프로그램 상세 조회
export const fetchProgramDetail = async (programId: string): Promise<ProgramDetailResponse> => {
  const res = await axios.get<ProgramDetailResponse>(`${HRM_ENDPOINTS.PROGRAM_DETAIL(programId)}`);
  return res.data;
};

// 출퇴근기록 목록 조회
export const fetchAttendanceList = async (
  params: AttendanceRequestParams,
): Promise<AttendanceListResponse> => {
  const res = await axios.get<AttendanceListResponse>(`${HRM_ENDPOINTS.TIME_RECORD}`, {
    params,
  });
  return res.data;
};

// 휴가 목록 조회
export const fetchLeaveList = async (params: LeaveRequestParams): Promise<LeaveListResponse> => {
  const res = await axios.get<LeaveListResponse>(`${HRM_ENDPOINTS.LEAVE_REQUESTS}`, {
    params,
  });
  return res.data;
};

// 신규 직원 등록
// 추후 생성

// 직원 정보 수정
export const putEmployee = async (
  employeeId: string,
  data: EmployeeUpdateRequest,
): Promise<void> => {
  await axios.patch(`${HRM_ENDPOINTS.EMPLOYEE_DETAIL(employeeId)}`, data);
};

// 휴가 신청 승인
export const postLeaveRelease = async (requestId: string) => {
  await axios.patch(`${HRM_ENDPOINTS.LEAVE_REQUEST_RELEASE(requestId)}`);
};

// 휴가 신청 반려
export const postLeaveReject = async (requestId: string) => {
  await axios.patch(`${HRM_ENDPOINTS.LEAVE_REQUEST_REJECT(requestId)}`);
};

// 교육 프로그램 추가
export const postProgram = async (data: CreateProgramRequest) => {
  await axios.post(`${HRM_ENDPOINTS.PROGRAM}`, data);
};

// 교육 프로그램 수정
export const patchProgram = async (params: UpdateProgramRequest) => {
  const { programId, programName, statusCode } = params;
  await axios.patch(`${HRM_ENDPOINTS.PROGRAM_DETAIL(programId)}`, {
    programName,
    statusCode,
  });
};

// 교육 대상자에게 교육 프로그램 지정
export const postProgramToEmployee = async (params: UpdateProgramToEmployeeRequest) => {
  const { employeeId, programId } = params;
  await axios.post(`${HRM_ENDPOINTS.PROGRAM_DETAIL(employeeId)}`, {
    programId,
  });
};

// 출퇴근 기록 수정
export const putTimeRecord = async (params: UpdateTimeRecord) => {
  const { timerecordId, checkInTime, checkOutTime } = params;
  await axios.put(`${HRM_ENDPOINTS.TIME_RECORD_UPDATE(timerecordId)}`, {
    checkInTime,
    checkOutTime,
  });
};

// 급여 지급 완료 처리
export const postPayrollComplete = async (params: PayRollCompleteRequestParams) => {
  await axios.post(`${HRM_ENDPOINTS.PAYROLL_COMPLETE}`, params);
};

// 직원 등록
export const postEmployeeRegister = async (body: EmployeeRegisterRequest) => {
  await axios.post(`${HRM_ENDPOINTS.EMPLOYEE_SIGNUP}`, body);
};

// --- 드롭다운 API ---
// 부서 드롭다운 조회
export const fetchDepartmentsDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<KeyValueItem[]>(HRM_ENDPOINTS.DEPARTMENTS_DROPDOWN);
  return res.data;
};

// 직급 드롭다운 조회
export const fetchPositionsDropdown = async (departmentId: string): Promise<KeyValueItem[]> => {
  const res = await axios.get<KeyValueItem[]>(HRM_ENDPOINTS.POSITIONS_DROPDOWN(departmentId));
  return res.data;
};

// 출결 상태 드롭다운 조회
export const fetchAttendanceStatusDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<KeyValueItem[]>(HRM_ENDPOINTS.ATTENDANCE_STATUS_DROPDOWN);
  return res.data;
};

// 부서 구성원 목록 드롭다운 조회
export const fetchDeptMemberDropdown = async (departmentId: string): Promise<KeyValueItem[]> => {
  const res = await axios.get<KeyValueItem[]>(HRM_ENDPOINTS.DEPT_MEMBER_DROPDOWN(departmentId));
  return res.data;
};

// 급여 상태 드롭다운 조회
export const fetchPayrollStatusDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<KeyValueItem[]>(HRM_ENDPOINTS.PAYROLL_STATUS_DROPDOWN);
  return res.data;
};

// 교육 카테고리 드롭다운 조회
export const fetchTrainingCategoryDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<KeyValueItem[]>(HRM_ENDPOINTS.TRAINING_CATE_DROPDOWN);
  return res.data;
};

// 교육 프로그램 드롭다운 조회
export const fetchProgramListDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<KeyValueItem[]>(HRM_ENDPOINTS.PROGRAM_LIST_DROPDOWN);
  return res.data;
};

// 교육 프로그램 상태 조회
export const fetchProgramStatusDropdown = async (): Promise<KeyValueItem[]> => {
  const res = await axios.get<KeyValueItem[]>(HRM_ENDPOINTS.PROGRAM_COMPLETION_DROPDOWN);
  return res.data;
};
