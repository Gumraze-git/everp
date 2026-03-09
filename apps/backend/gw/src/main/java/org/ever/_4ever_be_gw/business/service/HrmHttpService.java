package org.ever._4ever_be_gw.business.service;

import org.ever._4ever_be_gw.business.dto.LeaveRequestDto;
import org.ever._4ever_be_gw.business.dto.PayrollCompleteRequestDto;
import org.ever._4ever_be_gw.business.dto.ProgramAssignRequestDto;
import org.ever._4ever_be_gw.business.dto.ProgramCreateRequestDto;
import org.ever._4ever_be_gw.business.dto.ProgramModifyRequestDto;
import org.ever._4ever_be_gw.business.dto.TimeRecordUpdateRequestDto;
import org.ever._4ever_be_gw.business.dto.TrainingRequestDto;
import org.ever._4ever_be_gw.business.dto.employee.EmployeeUpdateRequestDto;
import org.ever._4ever_be_gw.business.dto.hrm.UpdateDepartmentRequestDto;
import org.ever._4ever_be_gw.business.dto.response.*;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * HRM(인적자원관리) HTTP 서비스 인터페이스
 * Business 서비스의 HRM 엔드포인트와 통신
 */
public interface HrmHttpService {

    // ==================== Statistics ====================

    /**
     * HR 대시보드 통계 조회
     */
    ResponseEntity<?> getHRStatistics();

    // ==================== Departments ====================

    /**
     * 부서 목록 조회
     */
    ResponseEntity<?> getDepartmentList(String status, Integer page, Integer size);

    /**
     * 부서 상세 정보 조회
     */
    ResponseEntity<?> getDepartmentDetail(String departmentId);

    /**
     * 전체 부서 목록 조회 (ID, Name만)
     */
    ResponseEntity<?> getAllDepartmentsSimple();

    /**
     * 부서 구성원 목록 조회 (ID, Name만)
     */
    ResponseEntity<?> getDepartmentMembers(String departmentId);

    ResponseEntity<?> updateDepartment(String departmentId, UpdateDepartmentRequestDto requestDto);

    // ==================== Positions ====================

    /**
     * 직급 목록 조회
     */
    ResponseEntity<?> getPositionList();

    /**
     * 직급 상세 정보 조회
     */
    ResponseEntity<?> getPositionDetail(String positionId);

    /**
     * 부서별 직급 목록 조회 (ID, Name만)
     */
    ResponseEntity<?> getPositionsByDepartmentId(String departmentId);

    // ==================== Employees ====================

    /**
     * 직원 목록 조회
     */
    ResponseEntity<?> getEmployeeList(
            String departmentId, String positionId, String name, Integer page, Integer size);

    /**
     * 직원 상세 정보 조회
     */
    ResponseEntity<?> getEmployeeDetail(String employeeId);

    /**
     * InternelUser ID로 직원 정보 및 교육 이력 조회
     */
    ResponseEntity<?> getEmployeeWithTrainingByInternelUserId(String internelUserId);

    /**
     * InternelUser ID로 수강 가능한 교육 프로그램 목록 조회
     */
    ResponseEntity<?> getAvailableTrainingsByInternelUserId(String internelUserId);

    /**
     * CustomerUser ID로 고객 사용자 상세 정보 조회
     */
    ResponseEntity<?> getCustomerUserDetailByUserId(String customerUserId);

    /**
     * 직원 정보 수정
     */
    ResponseEntity<?> updateEmployee(String employeeId, EmployeeUpdateRequestDto requestDto);

    /**
     * 교육 프로그램 신청
     */
    ResponseEntity<?> requestTraining(TrainingRequestDto requestDto);

    /**
     * 직원 교육 프로그램 등록
     */
    ResponseEntity<?> enrollTrainingProgram(String employeeId, ProgramAssignRequestDto requestDto);

    // ==================== Leave Requests ====================

    /**
     * 휴가 신청 목록 조회
     */
    ResponseEntity<?> getLeaveRequestList(
            String department, String position, String name, String type, String sortOrder, Integer page, Integer size);

    /**
     * 휴가 신청
     */
    ResponseEntity<?> createLeaveRequest(LeaveRequestDto requestDto);

    /**
     * 휴가 신청 승인
     */
    ResponseEntity<?> approveLeaveRequest(String requestId);

    /**
     * 휴가 신청 반려
     */
    ResponseEntity<?> rejectLeaveRequest(String requestId);

    // ==================== Payroll ====================

    /**
     * 급여 명세서 상세 조회
     */
    ResponseEntity<?> getPaystubDetail(String payrollId);

    /**
     * 급여 명세서 목록 조회
     */
    ResponseEntity<?> getPayrollList(
            Integer year, Integer month, String name, String department, String position, String statusCode, Integer page, Integer size);

    /**
     * 급여 지급 완료 처리
     */
    ResponseEntity<?> completePayroll(PayrollCompleteRequestDto requestDto);

    /**
     * 모든 직원 당월 급여 생성
     */
    ResponseEntity<?> generateMonthlyPayroll();

    /**
     * 급여 상태 목록 조회 (enum 전체)
     */
    ResponseEntity<?> getAllPayrollStatuses();

    // ==================== Attendance ====================

    /**
     * 출퇴근 상태 목록 조회 (enum 전체)
     */
    ResponseEntity<?> getAllAttendanceStatuses();

    /**
     * 대시보드 근태 목록 조회
     */
    ResponseEntity<?> getDashboardAttendanceList(String userId, Integer size);

    /**
     * 대시보드 휴가 신청 목록 조회
     */
    ResponseEntity<?> getDashboardLeaveRequestList(String userId, Integer size);

    // ==================== Training ====================

    /**
     * 교육 프로그램 상세 정보 조회
     */
    ResponseEntity<?> getProgramDetailInfo(String programId);

    /**
     * 교육 프로그램 목록 조회
     */
    ResponseEntity<?> getTrainingList(
            String name, String status, String category, Integer page, Integer size);

    /**
     * 교육 프로그램 생성
     */
    ResponseEntity<?> createTrainingProgram(ProgramCreateRequestDto requestDto);

    /**
     * 교육 프로그램 수정
     */
    ResponseEntity<?> updateTrainingProgram(String programId, ProgramModifyRequestDto requestDto);

    /**
     * 교육 카테고리 목록 조회 (enum 전체)
     */
    ResponseEntity<?> getAllTrainingCategories();

    /**
     * 전체 교육 프로그램 목록 조회 (ID, Name만)
     */
    ResponseEntity<?> getAllTrainingPrograms();

    /**
     * 교육 완료 상태 목록 조회
     */
    ResponseEntity<?> getAllTrainingCompletionStatuses();

    /**
     * 직원 교육 이력 조회
     */
    ResponseEntity<?> getEmployeeTrainingHistory(String employeeId);

    /**
     * 직원 교육 현황 목록 조회
     */
    ResponseEntity<?> getEmployeeTrainingList(
            String department, String position, String name, Integer page, Integer size);

    /**
     * 직원 교육 현황 통계 조회
     */
    ResponseEntity<?> getTrainingStatusList(
            String department, String position, String name, Integer page, Integer size);

    /**
     * 직원별 교육 요약 정보 조회
     */
    ResponseEntity<?> getEmployeeTrainingSummary(String employeeId);

    // ==================== Time Records ====================

    /**
     * 근태 기록 상세 정보 조회
     */
    ResponseEntity<?> getTimeRecordDetail(String timerecordId);

    /**
     * 근태 기록 수정
     */
    ResponseEntity<?> updateTimeRecord(String timerecordId, TimeRecordUpdateRequestDto requestDto);

    /**
     * 근태 기록 목록 조회
     */
    ResponseEntity<?> getAttendanceList(
            String department, String position, String name, String date, String statusCode, Integer page, Integer size);

    // ==================== Attendance ====================

    /**
     * 출퇴근 기록 조회
     */
    ResponseEntity<?> getAttendanceHistoryList(
            String employeeId, String startDate, String endDate, String status, Integer page, Integer size);

    /**
     * 출근 처리 (InternelUser ID 기반)
     */
    ResponseEntity<?> checkIn(String internelUserId);

    /**
     * 퇴근 처리 (InternelUser ID 기반)
     */
    ResponseEntity<?> checkOut(String internelUserId);

    /**
     * InternelUser ID로 출퇴근 기록 목록 조회
     */
    ResponseEntity<?> getAttendanceRecordsByInternelUserId(String internelUserId);

    /**
     * 휴가 신청
     */
    ResponseEntity<?> requestLeave(LeaveRequestDto requestDto, String internelUserId);

    /**
     * 교육 프로그램 목록 조회
     */
    ResponseEntity<?> getTrainingPrograms(
            String programName, String status, String category, Integer page, Integer size);

    /**
     * 직원에게 교육 프로그램 할당
     */
    ResponseEntity<?> assignProgramToEmployee(String employeeId, ProgramAssignRequestDto requestDto);
}
