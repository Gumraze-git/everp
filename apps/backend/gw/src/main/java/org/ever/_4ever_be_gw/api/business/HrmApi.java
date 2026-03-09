package org.ever._4ever_be_gw.api.business;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.ever._4ever_be_gw.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_gw.business.dto.*;
import org.ever._4ever_be_gw.business.dto.employee.EmployeeCreateRequestDto;
import org.ever._4ever_be_gw.business.dto.employee.EmployeeUpdateRequestDto;
import org.ever._4ever_be_gw.business.dto.hrm.CreateAuthUserResultDto;
import org.ever._4ever_be_gw.business.dto.hrm.UpdateDepartmentRequestDto;
import org.ever._4ever_be_gw.business.dto.response.*;
import org.ever._4ever_be_gw.business.service.HrmHttpService;
import org.ever._4ever_be_gw.business.service.HrmService;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.exception.ValidationException;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "인사관리(HRM)", description = "인사 관리 API")
@ApiServerErrorResponse
public interface HrmApi {

    @Operation(summary = "HRM 통계 조회", description = "기간별 인적자원 통계 정보를 조회합니다.")
    public ResponseEntity<?> getEmployeeStatistics(
        
        @RequestParam(name = "periods", required = false) String periods
    );

    @Operation(summary = "직원 신규 등록", description = "새로운 내부 직원을 등록합니다.")
    public Mono<ResponseEntity<CreateAuthUserResultDto>> signupEmployee(
        @Valid @RequestBody EmployeeCreateRequestDto requestDto
    );

    @Operation(summary = "직원 정보 수정", description = "기존 직원의 정보를 수정합니다.")
    public ResponseEntity<?> updateEmployee(
        
        @PathVariable("employeeId") String employeeId,
        @Valid @RequestBody EmployeeUpdateRequestDto requestDto
    );

    @Operation(summary = "직원 목록 조회", description = "직원 목록을 페이지네이션으로 조회합니다.")
    public ResponseEntity<?> getEmployees(
        
        @RequestParam(name = "departmentId", required = false) String departmentId,
        
        @RequestParam(name = "positionId", required = false) String positionId,
        
        @RequestParam(name = "name", required = false) String name,
        
        @RequestParam(name = "page", required = false) Integer page,
        
        @RequestParam(name = "size", required = false) Integer size
    );

    @Operation(summary = "직원 상세 조회", description = "직원 상세 정보를 조회합니다.")
    public ResponseEntity<?> getEmployeeDetail(
        
        @PathVariable("employeeId") String employeeId
    );

    @Operation(summary = "InternelUser ID로 직원 정보 및 교육 이력 조회", description = "InternelUser ID로 직원 상세 정보 및 교육 이력을 함께 조회합니다.")
    public ResponseEntity<?> getEmployeeWithTrainingByInternelUserId(
        
        @PathVariable("internelUserId") String internelUserId
    );

    @Operation(summary = "InternelUser ID로 수강 가능한 교육 프로그램 목록 조회", description = "InternelUser ID로 수강하지 않은 교육 프로그램 중 모집 중이 아닌 프로그램(IN_PROGRESS, COMPLETED)만 조회합니다.")
    public ResponseEntity<?> getAvailableTrainingsByInternelUserId(
        
        @PathVariable("internelUserId") String internelUserId
    );

    @Operation(summary = "CustomerUser ID로 고객 사용자 상세 정보 조회", description = "CustomerUser ID로 고객 사용자의 상세 정보를 조회합니다. (이메일, 사번, 입사일, 연락처, 주소, 가입기간)")
    public ResponseEntity<?> getCustomerUserDetailByUserId(
        
        @PathVariable("customerUserId") String customerUserId
    );

    @Operation(summary = "부서 목록 조회", description = "부서 목록을 조회합니다.")
    public ResponseEntity<?> getDepartments(
        
        @RequestParam(name = "status", required = false) String status,
        
        @RequestParam(name = "page", required = false) Integer page,
        
        @RequestParam(name = "size", required = false) Integer size
    );

    @Operation(summary = "전체 부서 목록 조회 (ID, Name만)", description = "전체 부서의 ID와 Name만 간단히 조회합니다.")
    public ResponseEntity<?> getAllDepartmentsSimple();

    @Operation(summary = "부서 구성원 목록 조회 (ID, Name만)", description = "특정 부서의 구성원 ID와 Name만 간단히 조회합니다.")
    public ResponseEntity<?> getDepartmentMembers(
        
        @PathVariable("departmentId") String departmentId
    );

    public ResponseEntity<?> updateDepartment(
            @PathVariable String departmentId,
            @RequestBody UpdateDepartmentRequestDto requestDto
    );

    @Operation(summary = "직급 목록 조회", description = "직급 목록을 조회합니다.")
    public ResponseEntity<?> getPositions();

    @Operation(summary = "직급 상세 조회", description = "직급 상세 정보를 조회합니다.")
    public ResponseEntity<?> getPositionDetail(
        
        @PathVariable("positionId") String positionId
    );

    @Operation(summary = "부서별 직급 목록 조회", description = "특정 부서의 직급의 ID를 조회합니다.")
    public ResponseEntity<?> getPositionsByDepartmentId(
        
        @PathVariable("departmentId") String departmentId
    );

    @Operation(summary = "출퇴근 기록 조회", description = "출퇴근 기록을 조회합니다.")
    public ResponseEntity<?> getAttendance(
        
        @RequestParam(name = "employeeId", required = false) String employeeId,
        
        @RequestParam(name = "startDate", required = false) String startDate,
        
        @RequestParam(name = "endDate", required = false) String endDate,
        
        @RequestParam(name = "status", required = false) String status,
        
        @RequestParam(name = "page", required = false) Integer page,
        
        @RequestParam(name = "size", required = false) Integer size
    );

    @Operation(summary = "출근 처리 (InternelUser ID 기반)", description = "InternelUser ID를 사용하여 직원의 출근을 처리합니다. (임시: JWT 미구현으로 internelUserId를 서비스에서 주입)")
    public ResponseEntity<?> checkIn(
        
        @AuthenticationPrincipal EverUserPrincipal principal
    );

    @Operation(summary = "퇴근 처리 (InternelUser ID 기반)", description = "InternelUser ID를 사용하여 직원의 퇴근을 처리합니다. (임시: JWT 미구현으로 internelUserId를 서비스에서 주입)")
    public ResponseEntity<?> checkOut(
        
        @AuthenticationPrincipal EverUserPrincipal everUser
    );

    @Operation(summary = "InternelUser ID로 출퇴근 기록 목록 조회", description = "InternelUser ID를 사용하여 해당 직원의 모든 출퇴근 기록을 조회합니다.")
    public ResponseEntity<?> getAttendanceRecordsByInternelUserId(
        
        @PathVariable String internelUserId
    );

    @Operation(summary = "출퇴근 상태 목록 조회 (enum 전체)", description = "출퇴근 상태 enum의 모든 값을 조회합니다.")
    public ResponseEntity<?> getAllAttendanceStatuses();

    @Operation(summary = "급여 지급 완료 처리", description = "급여 지급을 완료 처리합니다.")
    public ResponseEntity<?> completePayroll(
        @Valid @RequestBody PayrollCompleteRequestDto requestDto
    );

    @Operation(summary = "모든 직원 당월 급여 생성", description = "모든 직원의 당월 급여를 생성합니다. 이미 존재하는 급여는 건너뛰고 없는 직원의 급여만 생성합니다 (idempotent).")
    public ResponseEntity<?> generateMonthlyPayroll();

    @Operation(summary = "월별 급여 상세 조회", description = "월별 사내 급여 명세서 상세를 조회합니다.")
    public ResponseEntity<?> getMonthlyPayrollDetail(
        
        @PathVariable("payrollId") String payrollId
    );

    @Operation(summary = "급여 상태 목록 조회 (enum 전체)", description = "급여 상태 enum의 모든 값을 조회합니다.")
    public ResponseEntity<?> getAllPayrollStatuses();

    @Operation(summary = "출퇴근 기록 수정", description = "출퇴근 기록을 수정합니다.")
    public ResponseEntity<?> updateTimeRecord(
        
        @PathVariable("timerecordId") String timerecordId,
        @Valid @RequestBody TimeRecordUpdateRequestDto requestDto
    );

    @Operation(summary = "휴가 신청", description = "새로운 휴가를 신청합니다.")
    public ResponseEntity<?> requestLeave(
        @AuthenticationPrincipal EverUserPrincipal principal,
        @Valid @RequestBody LeaveRequestDto requestDto
    );

    @Operation(summary = "휴가 신청 승인", description = "휴가 신청을 승인합니다.")
    public ResponseEntity<?> approveLeaveRequest(
        
        @PathVariable("requestId") String requestId
    );

    @Operation(summary = "휴가 신청 반려", description = "휴가 신청을 반려합니다.")
    public ResponseEntity<?> rejectLeaveRequest(
        
        @PathVariable("requestId") String requestId
    );

    @Operation(summary = "출퇴근 기록 목록 조회", description = "부서/직책/이름/일자로 출퇴근 기록을 조회합니다.")
    public ResponseEntity<?> getTimeRecords(
        
        @RequestParam(name = "department", required = false) String departmentId,
        
        @RequestParam(required = false) String statusCode,
        
        @RequestParam(name = "position", required = false) String positionId,
        
        @RequestParam(name = "name", required = false) String employeeName,
        
        @RequestParam(name = "date") String date,
        
        @RequestParam(name = "page", required = false) Integer page,
        
        @RequestParam(name = "size", required = false) Integer size
    );

    @Operation(summary = "출퇴근 기록 상세 조회", description = "단일 출퇴근 기록 상세 정보를 조회합니다.")
    public ResponseEntity<?> getTimeRecordDetail(
        
        @PathVariable("timerecordId") String timerecordId
    );

    @Operation(summary = "휴가 신청 목록 조회", description = "부서/직책/이름/유형으로 휴가 신청 목록을 조회합니다.")
    public ResponseEntity<?> getLeaveRequestList(
        
        @RequestParam(name = "department", required = false) String departmentId,
        
        @RequestParam(name = "position", required = false) String positionId,
        
        @RequestParam(name = "name", required = false) String employeeName,
        
        @RequestParam(name = "type", required = false) String leaveType,
        
        @RequestParam(name = "sortOrder", required = false, defaultValue = "DESC") String sortOrder,
        
        @RequestParam(name = "page", required = false) Integer page,
        
        @RequestParam(name = "size", required = false) Integer size
    );

    @Operation(summary = "교육 신청", description = "직원이 교육 프로그램에 신청합니다.")
    public ResponseEntity<?> requestTraining(
        @Valid @RequestBody TrainingRequestDto requestDto
    );

    @Operation(summary = "직원에게 교육 프로그램 추가", description = "특정 직원에게 교육 프로그램을 할당합니다.")
    public ResponseEntity<?> assignProgramToEmployee(
        
        @PathVariable("employeeId") String employeeId,
        @Valid @RequestBody ProgramAssignRequestDto requestDto
    );

    @Operation(summary = "교육 프로그램 추가", description = "새로운 교육 프로그램을 추가합니다.")
    public ResponseEntity<?> createProgram(
        @Valid @RequestBody ProgramCreateRequestDto requestDto
    );

    @Operation(summary = "교육 프로그램 수정", description = "기존 교육 프로그램 정보를 수정합니다.")
    public ResponseEntity<?> modifyProgram(
        
        @PathVariable("programId") String programId,
        @Valid @RequestBody ProgramModifyRequestDto requestDto
    );

    @Operation(summary = "교육 카테고리 목록 조회 (enum 전체)", description = "교육 카테고리 enum의 모든 값을 조회합니다.")
    public ResponseEntity<?> getAllTrainingCategories();

    @Operation(summary = "교육 완료 상태 목록 조회", description = "교육 완료 상태의 모든 값을 조회합니다. (완료: true, 미완료: false)")
    public ResponseEntity<?> getAllTrainingCompletionStatuses();

    @Operation(summary = "직원 교육 상세 조회", description = "해당 직원의 교육 이력(교육명, 완료 여부, 완료일 등)을 조회합니다.")
    public ResponseEntity<?> getEmployeeTrainingHistory(
            @PathVariable String employeeId
    );

    @Operation(summary = "교육 프로그램 목록 조회", description = "프로그램 이름/상태/카테고리로 교육 프로그램 목록을 조회합니다.")
    public ResponseEntity<?> getTrainingPrograms(
        
        @RequestParam(name = "name", required = false) String programName,
        
        @RequestParam(name = "status", required = false) String status,
        
        @RequestParam(name = "category", required = false) String category,
        
        @RequestParam(name = "page", required = false) Integer page,
        
        @RequestParam(name = "size", required = false) Integer size
    );

    @Operation(summary = "교육 프로그램 상세 조회", description = "단일 교육 프로그램 상세 정보를 조회합니다.")
    public ResponseEntity<?> getTrainingProgramDetail(
        
        @PathVariable("programId") String programId
    );

}
