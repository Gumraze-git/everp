import { API_BASE_URL } from '@/app/types/api';

export const HRM_BASE_PATH = `${API_BASE_URL}/business/hrm`;

export const HRM_ENDPOINTS = {
  // 드롭다운
  DEPARTMENTS_DROPDOWN: `${HRM_BASE_PATH}/departments/options`, // 부서 드롭다운
  POSITIONS_DROPDOWN: (departmentId: string) =>
    `${HRM_BASE_PATH}/departments/${departmentId}/positions/options`, // 부서에 따른 직급 드롭다운
  ATTENDANCE_STATUS_DROPDOWN: `${HRM_BASE_PATH}/attendance/statuses`, // 출결 상태 드롭다운
  DEPT_MEMBER_DROPDOWN: (departmentId: string) =>
    `${HRM_BASE_PATH}/departments/${departmentId}/members`, // 부서 구성원 목록 드롭다운
  PAYROLL_STATUS_DROPDOWN: `${HRM_BASE_PATH}/payroll/statuses`, // 급여 상태 드롭다운
  TRAINING_CATE_DROPDOWN: `${HRM_BASE_PATH}/trainings/categories`, //교육 카테고리 드롭다운
  PROGRAM_LIST_DROPDOWN: `${HRM_BASE_PATH}/trainings/programs`, // 교육 프로그램 드롭다운
  PROGRAM_COMPLETION_DROPDOWN: `${HRM_BASE_PATH}/trainings/completion-statuses`, // 교육완료 상태 드롭다운

  // 근태
  ATTENDANCE: `${HRM_BASE_PATH}/attendance`, // 출퇴근 기록 조회
  ATTENDANCE_SELF: `${HRM_BASE_PATH}/attendance/self`, // 내 출퇴근 상태 변경

  // 부서
  DEPARTMENTS: `${HRM_BASE_PATH}/departments`, // 부서 목록 조회
  DEPARTMENTS_DETAIL: (departmentId: string) => `${HRM_BASE_PATH}/departments/${departmentId}`, // 부서 수정

  // 직원
  EMPLOYEE: `${HRM_BASE_PATH}/employees`, // 직원 목록 조회, 등록
  EMPLOYEE_DETAIL: (employeeId: string) => `${HRM_BASE_PATH}/employees/${employeeId}`, // 직원 상세 조회, 수정
  EMPLOYEE_SIGNUP: `${HRM_BASE_PATH}/employees`, // 신규 직원 등록

  // 휴가
  LEAVE_REQUESTS: `${HRM_BASE_PATH}/leave-requests`, // 휴가 신청 목록 조회
  LEAVE_REQUEST: `${HRM_BASE_PATH}/leave-requests`, // 휴가 신청 생성
  LEAVE_REQUEST_DETAIL: (requestId: string) => `${HRM_BASE_PATH}/leave-requests/${requestId}`, // 휴가 신청 상태 변경

  // 급여
  PAYROLL: `${HRM_BASE_PATH}/payroll`, // 급여 목록 조회
  PAYROLL_DETAIL: (payrollId: string) => `${HRM_BASE_PATH}/payroll/${payrollId}`, // 급여 상세 조회
  PAYROLL_COMPLETE: `${HRM_BASE_PATH}/payroll/complete`, // 급여 지급 완료 처리
  PAYROLL_GENERATE: `${HRM_BASE_PATH}/payroll/generate`, // 모든 직원 당월 급여 생성

  // 직책
  POSITIONS: `${HRM_BASE_PATH}/positions`, // 직책 목록 조회
  POSITION_DETAIL: (positionId: string) => `${HRM_BASE_PATH}/positions/${positionId}`, // 직책 상세 조회

  // 교육
  PROGRAM: `${HRM_BASE_PATH}/programs`, // 교육 프로그램 목록 조회, 추가
  PROGRAM_DETAIL: (programId: string) => `${HRM_BASE_PATH}/programs/${programId}`, // 교육 프로그램 상세 조회, 수정
  PROGRAM_EMPLOYEE_ADD: (employeeId: string) => `${HRM_BASE_PATH}/employees/${employeeId}/programs`, // 직원에게 교육 프로그램 추가

  TRAINING_STATUS: `${HRM_BASE_PATH}/training-records`, // 직원 교육 현황 조회
  TRAINING_EMPLOYEE_DETAIL: (employeeId: string) =>
    `${HRM_BASE_PATH}/training-records/${employeeId}`, // 직원 교육 현황 상세 조회
  TRAINING_CATEGORIES: `${HRM_BASE_PATH}/trainings/categories`, // 교육 카테고리 목록 조회 (enum 전체)
  TRAINING_COMPLETION_STATUSES: `${HRM_BASE_PATH}/trainings/completion-statuses`, // 교육 완료 상태 목록 조회
  TRAINING_PROGRAMS: `${HRM_BASE_PATH}/trainings/programs`, // 전체 교육 프로그램 목록 조회 (ID, Name만)

  // 통계
  STATISTICS: `${HRM_BASE_PATH}/metrics`, // HRM 통계 조회

  // 근무 기록
  TIME_RECORD: `${HRM_BASE_PATH}/attendance-records`, // 출퇴근 기록 목록 조회
  TIME_RECORD_DETAIL: (timeRecordId: string) => `${HRM_BASE_PATH}/attendance-records/${timeRecordId}`, // 출퇴근 기록 상세 조회
  TIME_RECORD_UPDATE: (timeRecordId: string) => `${HRM_BASE_PATH}/attendance-records/${timeRecordId}`, // 출퇴근 기록 수정 (PUT)
};
