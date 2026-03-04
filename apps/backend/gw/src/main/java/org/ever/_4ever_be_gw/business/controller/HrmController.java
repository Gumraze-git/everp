package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/business/hrm")
@Tag(name = "인사관리(HRM)", description = "인사 관리 API")
public class HrmController {
    private final HrmService hrmService;
    private final HrmHttpService hrmHttpService;

    public HrmController(HrmService hrmService, HrmHttpService hrmHttpService) {
        this.hrmService = hrmService;
        this.hrmHttpService = hrmHttpService;
    }

    // ==================== 인적자원 통계 ====================

    @GetMapping("/statistics")
    @Operation(
        summary = "HRM 통계 조회",
        description = "기간별 인적자원 통계 정보를 조회합니다."
    )
    public ResponseEntity<ApiResponse<HRStatisticsResponseDto>> getEmployeeStatistics(
        @Parameter(description = "조회 기간 목록(콤마 구분)")
        @RequestParam(name = "periods", required = false) String periods
    ) {
        return hrmHttpService.getHRStatistics();
    }

    // ==================== 직원 관리 ====================

    @PostMapping("/employee/signup")
    @PreAuthorize("hasAnyAuthority('HRM_USER', 'HRM_ADMIN', 'ALL_ADMIN')")
    @Operation(
        summary = "직원 신규 등록",
        description = "새로운 내부 직원을 등록합니다."
    )
    public Mono<ResponseEntity<ApiResponse<CreateAuthUserResultDto>>> signupEmployee(
        @Valid @RequestBody EmployeeCreateRequestDto requestDto
    ) {
        return hrmService.createInternalUser(requestDto)
                .map(response -> ResponseEntity.ok(
                        ApiResponse.success(
                                response.getData(),
                                "직원 등록이 완료 되었습니다.",
                                HttpStatus.OK
                        )
                ))
                .onErrorResume(error -> {
                    ApiResponse<CreateAuthUserResultDto> failResponse = ApiResponse.fail(
                            "직원 등록 중 오류가 발생했습니다.",
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            error.getMessage()
                    );
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failResponse));
                });
    }

    @PatchMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyAuthority('HRM_USER', 'HRM_ADMIN', 'ALL_ADMIN')")
    @Operation(
        summary = "직원 정보 수정",
        description = "기존 직원의 정보를 수정합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"직원 정보 수정이 완료되었습니다.\",\n  \"data\": null\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Void>> updateEmployee(
        @Parameter(description = "직원 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("employeeId") String employeeId,
        @Valid @RequestBody EmployeeUpdateRequestDto requestDto
    ) {
        return hrmHttpService.updateEmployee(employeeId, requestDto);
    }

    @GetMapping("/employee")
    @Operation(
        summary = "직원 목록 조회",
        description = "직원 목록을 페이지네이션으로 조회합니다."
    )
    public ResponseEntity<ApiResponse<Page<EmployeeListItemDto>>> getEmployees(
        @Parameter(description = "부서 ID 필터")
        @RequestParam(name = "departmentId", required = false) String departmentId,
        @Parameter(description = "직급 ID 필터")
        @RequestParam(name = "positionId", required = false) String positionId,
        @Parameter(description = "이름 검색")
        @RequestParam(name = "name", required = false) String name,
        @Parameter(description = "페이지 번호(0-base)")
        @RequestParam(name = "page", required = false) Integer page,
        @Parameter(description = "페이지 크기(최대 200)")
        @RequestParam(name = "size", required = false) Integer size
    ) {
        return hrmHttpService.getEmployeeList(departmentId, positionId, name, page, size);
    }

    @GetMapping("/employees/{employeeId}")
    @PreAuthorize("hasAnyAuthority('HRM_USER', 'HRM_ADMIN', 'ALL_ADMIN')")
    @Operation(
        summary = "직원 상세 조회",
        description = "직원 상세 정보를 조회합니다."
    )
    public ResponseEntity<ApiResponse<EmployeeDetailDto>> getEmployeeDetail(
        @Parameter(description = "직원 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("employeeId") String employeeId
    ) {
        return hrmHttpService.getEmployeeDetail(employeeId);
    }

    @GetMapping("/employees/by-internel-user/{internelUserId}")
    @Operation(
        summary = "InternelUser ID로 직원 정보 및 교육 이력 조회",
        description = "InternelUser ID로 직원 상세 정보 및 교육 이력을 함께 조회합니다."
    )
    public ResponseEntity<ApiResponse<EmployeeWithTrainingDto>> getEmployeeWithTrainingByInternelUserId(
        @Parameter(description = "InternelUser ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("internelUserId") String internelUserId
    ) {
        return hrmHttpService.getEmployeeWithTrainingByInternelUserId(internelUserId);
    }

    @GetMapping("/employees/by-internel-user/{internelUserId}/available-trainings")
    @Operation(
        summary = "InternelUser ID로 수강 가능한 교육 프로그램 목록 조회",
        description = "InternelUser ID로 수강하지 않은 교육 프로그램 중 모집 중이 아닌 프로그램(IN_PROGRESS, COMPLETED)만 조회합니다."
    )
    public ResponseEntity<ApiResponse<List<TrainingProgramSimpleDto>>> getAvailableTrainingsByInternelUserId(
        @Parameter(description = "InternelUser ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("internelUserId") String internelUserId
    ) {
        return hrmHttpService.getAvailableTrainingsByInternelUserId(internelUserId);
    }

    @GetMapping("/customers/by-customer-user/{customerUserId}")
    @Operation(
        summary = "CustomerUser ID로 고객 사용자 상세 정보 조회",
        description = "CustomerUser ID로 고객 사용자의 상세 정보를 조회합니다. (이메일, 사번, 입사일, 연락처, 주소, 가입기간)"
    )
    public ResponseEntity<ApiResponse<CustomerUserDetailDto>> getCustomerUserDetailByUserId(
        @Parameter(description = "CustomerUser ID", example = "customer1")
        @PathVariable("customerUserId") String customerUserId
    ) {
        return hrmHttpService.getCustomerUserDetailByUserId(customerUserId);
    }

    // ==================== 부서 관리 ====================

    @GetMapping("/departments")
    @Operation(
        summary = "부서 목록 조회",
        description = "부서 목록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<DepartmentListResponseDto>> getDepartments(
        @Parameter(description = "상태 필터: ACTIVE, INACTIVE")
        @RequestParam(name = "status", required = false) String status,
        @Parameter(description = "페이지(0-base)", example = "0")
        @RequestParam(name = "page", required = false) Integer page,
        @Parameter(description = "페이지 크기(최대 200)", example = "20")
        @RequestParam(name = "size", required = false) Integer size
    ) {
        return hrmHttpService.getDepartmentList(status, page, size);
    }

    @GetMapping("/departments/all")
    @Operation(
        summary = "전체 부서 목록 조회 (ID, Name만)",
        description = "전체 부서의 ID와 Name만 간단히 조회합니다."
    )
    public ResponseEntity<ApiResponse<List<DepartmentSimpleDto>>> getAllDepartmentsSimple() {
        return hrmHttpService.getAllDepartmentsSimple();
    }

    @GetMapping("/departments/{departmentId}/members")
    @Operation(
        summary = "부서 구성원 목록 조회 (ID, Name만)",
        description = "특정 부서의 구성원 ID와 Name만 간단히 조회합니다."
    )
    public ResponseEntity<ApiResponse<List<DepartmentMemberDto>>> getDepartmentMembers(
        @Parameter(description = "부서 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("departmentId") String departmentId
    ) {
        return hrmHttpService.getDepartmentMembers(departmentId);
    }

    @PatchMapping("/departments/{departmentId}")
    public ResponseEntity<ApiResponse<Void>> updateDepartment(
            @PathVariable String departmentId,
            @RequestBody UpdateDepartmentRequestDto requestDto
    ) {
        return hrmHttpService.updateDepartment(departmentId, requestDto);
    }

    // ==================== 직급 관리 ====================

    @GetMapping("/positions")
    @Operation(
        summary = "직급 목록 조회",
        description = "직급 목록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<List<PositionListItemDto>>> getPositions() {
        return hrmHttpService.getPositionList();
    }

    @GetMapping("/positions/{positionId}")
    @Operation(
        summary = "직급 상세 조회",
        description = "직급 상세 정보를 조회합니다."
    )
    public ResponseEntity<ApiResponse<PositionDetailDto>> getPositionDetail(
        @Parameter(description = "직급 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("positionId") String positionId
    ) {
        return hrmHttpService.getPositionDetail(positionId);
    }

    @GetMapping("/{departmentId}/positions/all")
    @Operation(
        summary = "부서별 직급 목록 조회",
        description = "특정 부서의 직급의 ID를 조회합니다."
    )
    public ResponseEntity<ApiResponse<List<PositionSimpleDto>>> getPositionsByDepartmentId(
        @Parameter(description = "부서 ID")
        @PathVariable("departmentId") String departmentId
    ) {
        return hrmHttpService.getPositionsByDepartmentId(departmentId);
    }

    // ==================== 출퇴근 관리 ====================

    @GetMapping("/attendance")
    @Operation(
        summary = "출퇴근 기록 조회",
        description = "출퇴근 기록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<Page<AttendanceListItemDto>>> getAttendance(
        @Parameter(description = "직원 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @RequestParam(name = "employeeId", required = false) String employeeId,
        @Parameter(description = "시작일(YYYY-MM-DD)")
        @RequestParam(name = "startDate", required = false) String startDate,
        @Parameter(description = "종료일(YYYY-MM-DD)")
        @RequestParam(name = "endDate", required = false) String endDate,
        @Parameter(description = "상태 필터: NORMAL, LATE, EARLY_LEAVE, ABSENT")
        @RequestParam(name = "status", required = false) String status,
        @Parameter(description = "페이지 번호(0-base)")
        @RequestParam(name = "page", required = false) Integer page,
        @Parameter(description = "페이지 크기(최대 200)")
        @RequestParam(name = "size", required = false) Integer size
    ) {
        return hrmHttpService.getAttendanceHistoryList(employeeId, startDate, endDate, status, page, size);
    }

    @PatchMapping("/attendance/check-in")
    @Operation(
        summary = "출근 처리 (InternelUser ID 기반)",
        description = "InternelUser ID를 사용하여 직원의 출근을 처리합니다. (임시: JWT 미구현으로 internelUserId를 서비스에서 주입)"
    )
    public ResponseEntity<ApiResponse<Void>> checkIn(
        @Parameter(description = "InternelUser ID (optional, JWT 구현 전까지)", example = "internel1")
        @AuthenticationPrincipal EverUserPrincipal principal
    ) {
        String targetInternelUserId = principal.getUserId();

        return hrmHttpService.checkIn(targetInternelUserId);
    }

    @PatchMapping("/attendance/check-out")
    @Operation(
        summary = "퇴근 처리 (InternelUser ID 기반)",
        description = "InternelUser ID를 사용하여 직원의 퇴근을 처리합니다. (임시: JWT 미구현으로 internelUserId를 서비스에서 주입)"
    )
    public ResponseEntity<ApiResponse<Void>> checkOut(
        @Parameter(description = "InternelUser ID (optional, JWT 구현 전까지)", example = "internel1")
        @AuthenticationPrincipal EverUserPrincipal everUser
    ) {
        String targetInternelUserId = everUser.getUserId();

        return hrmHttpService.checkOut(targetInternelUserId);
    }

    @GetMapping("/employees/by-internel-user/{internelUserId}/attendance-records")
    @Operation(
        summary = "InternelUser ID로 출퇴근 기록 목록 조회",
        description = "InternelUser ID를 사용하여 해당 직원의 모든 출퇴근 기록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<List<AttendanceRecordDto>>> getAttendanceRecordsByInternelUserId(
        @Parameter(description = "InternelUser ID", example = "internel1", required = true)
        @PathVariable String internelUserId
    ) {
        return hrmHttpService.getAttendanceRecordsByInternelUserId(internelUserId);
    }

    @GetMapping("/attendance/statuses")
    @Operation(
        summary = "출퇴근 상태 목록 조회 (enum 전체)",
        description = "출퇴근 상태 enum의 모든 값을 조회합니다."
    )
    public ResponseEntity<ApiResponse<List<AttendanceStatusDto>>> getAllAttendanceStatuses() {
        return hrmHttpService.getAllAttendanceStatuses();
    }

    // 월별 사내 급여 목록 조회
    @GetMapping("/payroll")
    @Operation(
        summary = "월별 급여 목록 조회",
        description = "월별 사내 급여 명세서 목록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<Page<PayrollListItemDto>>> getMonthlyPayrollList(
        @Parameter(description = "연도", example = "2025")
        @RequestParam(name = "year") Integer year,
        @Parameter(description = "월(1~12)", example = "10")
        @RequestParam(name = "month") Integer month,
        @Parameter(description = "상태코드 (PAYROLL_PAID, PAYROLL_UNPAID)")
        @RequestParam(required = false) String statusCode,
        @Parameter(description = "직원 이름(선택)")
        @RequestParam(name = "name", required = false) String employeeName,
        @Parameter(description = "부서 ID(선택)")
        @RequestParam(name = "department", required = false) String departmentId,
        @Parameter(description = "직급 ID(선택)")
        @RequestParam(name = "position", required = false) String positionId,
        @Parameter(description = "페이지 번호(0-base)")
        @RequestParam(name = "page", required = false) Integer page,
        @Parameter(description = "페이지 크기(최대 200)")
        @RequestParam(name = "size", required = false) Integer size
    ) {
        List<Map<String, String>> errors = new ArrayList<>();
        if (year == null || year < 1900 || year > 2100) {
            errors.add(Map.of("field", "year", "reason", "RANGE_1900_2100"));
        }
        if (month == null || month < 1 || month > 12) {
            errors.add(Map.of("field", "month", "reason", "RANGE_1_12"));
        }
        if (page != null && page < 0) {
            errors.add(Map.of("field", "page", "reason", "MIN_0"));
        }
        if (size != null && size > 200) {
            errors.add(Map.of("field", "size", "reason", "MAX_200"));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        return hrmHttpService.getPayrollList(year, month, employeeName, departmentId, positionId, statusCode, page, size);
    }

    // 급여 지급 완료 처리
    @PostMapping("/payroll/complete")
    @PreAuthorize("hasAnyAuthority('HRM_USER', 'HRM_ADMIN', 'ALL_ADMIN')")
    @Operation(
        summary = "급여 지급 완료 처리",
        description = "급여 지급을 완료 처리합니다."
    )
    public ResponseEntity<ApiResponse<Void>> completePayroll(
        @Valid @RequestBody PayrollCompleteRequestDto requestDto
    ) {
        return hrmHttpService.completePayroll(requestDto);
    }

    // 모든 직원 당월 급여 생성
    @GetMapping("/payroll/generate")
    @Operation(
        summary = "모든 직원 당월 급여 생성",
        description = "모든 직원의 당월 급여를 생성합니다. 이미 존재하는 급여는 건너뛰고 없는 직원의 급여만 생성합니다 (idempotent)."
    )
    public ResponseEntity<ApiResponse<Void>> generateMonthlyPayroll() {
        return hrmHttpService.generateMonthlyPayroll();
    }

    // 월별 사내 급여 상세 조회
    @GetMapping("/payroll/{payrollId}")
    @Operation(
        summary = "월별 급여 상세 조회",
        description = "월별 사내 급여 명세서 상세를 조회합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"급여 명세서 상세 조회에 성공했습니다.\",\n  \"data\": {\n    \"payrollId\": \"0193e7c8-1234-7abc-9def-0123456789ab\",\n    \"employee\": {\n      \"employeeId\": \"0193e7c8-5678-7abc-9def-fedcba987654\",\n      \"employeeNumber\": \"EMP001\",\n      \"employeeName\": \"김민수\",\n      \"department\": \"개발팀\",\n      \"position\": \"과장\"\n    },\n    \"pay\": {\n      \"basePay\": 4500000,\n      \"basePayItem\": [{\n        \"itemContent\": \"정기 급여\",\n        \"itemSum\": 4200000\n      },{\n        \"itemContent\": \"직책 수당\",\n        \"itemSum\": 300000\n      }],\n      \"overtimePay\": 150000,\n      \"overtimePayItem\": [{\n        \"itemContent\": \"야간 근무 수당 (5시간)\",\n        \"itemSum\": 100000\n      },{\n        \"itemContent\": \"휴일 근무 수당 (2시간)\",\n        \"itemSum\": 50000\n      }],\n      \"deduction\": -450000,\n      \"deductionItem\": [{\n        \"itemContent\": \"국민연금\",\n        \"itemSum\": -200000\n      },{\n        \"itemContent\": \"건강보험\",\n        \"itemSum\": -150000\n      },{\n        \"itemContent\": \"소득세\",\n        \"itemSum\": -100000\n      }],\n      \"netPay\": 4200000\n    },\n    \"statusCode\": \"COMPLETED\",\n    \"expectedDate\": \"2024-01-25\"\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<PaystubDetailDto>> getMonthlyPayrollDetail(
        @Parameter(description = "급여 명세서 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("payrollId") String payrollId
    ) {
        List<Map<String, String>> errors = new ArrayList<>();
        if (payrollId == null || payrollId.isBlank()) {
            errors.add(Map.of("field", "payrollId", "reason", "REQUIRED"));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        return hrmHttpService.getPaystubDetail(payrollId);
    }

    @GetMapping("/payroll/statuses")
    @Operation(
        summary = "급여 상태 목록 조회 (enum 전체)",
        description = "급여 상태 enum의 모든 값을 조회합니다."
    )
    public ResponseEntity<ApiResponse<List<PayrollStatusDto>>> getAllPayrollStatuses() {
        return hrmHttpService.getAllPayrollStatuses();
    }

    // ==================== 근태 기록 관리 ====================

    @PutMapping("/time-record/{timerecordId}")
    @Operation(
        summary = "출퇴근 기록 수정",
        description = "출퇴근 기록을 수정합니다."
    )
    public ResponseEntity<ApiResponse<Void>> updateTimeRecord(
        @Parameter(description = "근태 기록 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("timerecordId") String timerecordId,
        @Valid @RequestBody TimeRecordUpdateRequestDto requestDto
    ) {
        return hrmHttpService.updateTimeRecord(timerecordId, requestDto);
    }

    // ==================== 휴가 관리 ====================

    @PostMapping("/leave/request")
    @Operation(
        summary = "휴가 신청",
        description = "새로운 휴가를 신청합니다."
    )
    public ResponseEntity<ApiResponse<Void>> requestLeave(
        @AuthenticationPrincipal EverUserPrincipal principal,
        @Valid @RequestBody LeaveRequestDto requestDto
    ) {
        String InternelUserId = principal.getUserId();
        return hrmHttpService.requestLeave(requestDto,InternelUserId);
    }

    @PatchMapping("/leave/request/{requestId}/release")
    @Operation(
        summary = "휴가 신청 승인",
        description = "휴가 신청을 승인합니다."
    )
    public ResponseEntity<ApiResponse<Void>> approveLeaveRequest(
        @Parameter(description = "휴가 신청 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("requestId") String requestId
    ) {
        return hrmHttpService.approveLeaveRequest(requestId);
    }

    @PatchMapping("/leave/request/{requestId}/reject")
    @Operation(
        summary = "휴가 신청 반려",
        description = "휴가 신청을 반려합니다."
    )
    public ResponseEntity<ApiResponse<Void>> rejectLeaveRequest(
        @Parameter(description = "휴가 신청 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("requestId") String requestId
    ) {
        return hrmHttpService.rejectLeaveRequest(requestId);
    }

    // ==================== 기존 조회 API들 ====================
    // GET /api/business/tam/time-record?department=&position=&name=&date=&page=&size=
    @GetMapping("/time-record")
    @Operation(
        summary = "출퇴근 기록 목록 조회",
        description = "부서/직책/이름/일자로 출퇴근 기록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<Page<TimeRecordListItemDto>>> getTimeRecords(
        @Parameter(description = "부서 ID")
        @RequestParam(name = "department", required = false) String departmentId,
        @Parameter(description = "상태 (NORMAL, LATE)")
        @RequestParam(required = false) String statusCode,
        @Parameter(description = "직책 ID")
        @RequestParam(name = "position", required = false) String positionId,
        @Parameter(description = "직원 이름")
        @RequestParam(name = "name", required = false) String employeeName,
        @Parameter(description = "검색 일자(YYYY-MM-DD)")
        @RequestParam(name = "date") String date,
        @Parameter(description = "페이지 번호(0-base)")
        @RequestParam(name = "page", required = false) Integer page,
        @Parameter(description = "페이지 크기(최대 200)")
        @RequestParam(name = "size", required = false) Integer size
    ) {
        List<Map<String, String>> errors = new ArrayList<>();
        if (date != null) {
            try {
                LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                errors.add(Map.of("field", "date", "reason", "INVALID_DATE"));
            }
        }
        if (page != null && page < 0) {
            errors.add(Map.of("field", "page", "reason", "MIN_0"));
        }
        if (size != null && size > 200) {
            errors.add(Map.of("field", "size", "reason", "MAX_200"));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size < 1) ? 20 : size;

        return hrmHttpService.getAttendanceList(departmentId, positionId, employeeName, date, statusCode, p, s);
    }

    // 출퇴근 기록 상세 조회
    // GET /api/business/tam/time-record/{timerecordId}
    @GetMapping("/time-record/{timerecordId}")
    @Operation(
        summary = "출퇴근 기록 상세 조회",
        description = "단일 출퇴근 기록 상세 정보를 조회합니다."
    )
    public ResponseEntity<ApiResponse<TimeRecordDetailDto>> getTimeRecordDetail(
        @Parameter(description = "출퇴근 기록 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("timerecordId") String timerecordId
    ) {
        if (timerecordId == null || timerecordId.isBlank()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
                List.of(Map.of("field", "timerecordId", "reason", "REQUIRED")));
        }

        return hrmHttpService.getTimeRecordDetail(timerecordId);
    }

    // ==================== Mock 생성 함수 ====================

    // 휴가 신청 목록 조회
    // GET /api/business/tam/leave-request?department=&position=&name=&type=&page=&size=&sortOrder=
    @GetMapping("/leave-request")
    @Operation(
        summary = "휴가 신청 목록 조회",
        description = "부서/직책/이름/유형으로 휴가 신청 목록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<Page<LeaveRequestListItemDto>>> getLeaveRequestList(
        @Parameter(description = "부서 ID")
        @RequestParam(name = "department", required = false) String departmentId,
        @Parameter(description = "직책 ID")
        @RequestParam(name = "position", required = false) String positionId,
        @Parameter(description = "직원 이름")
        @RequestParam(name = "name", required = false) String employeeName,
        @Parameter(description = "휴가 유형: ANNUAL, SICK")
        @RequestParam(name = "type", required = false) String leaveType,
        @Parameter(description = "정렬: DESC(최신순) 또는 ASC(오래된 순)")
        @RequestParam(name = "sortOrder", required = false, defaultValue = "DESC") String sortOrder,
        @Parameter(description = "페이지 번호(0-base)")
        @RequestParam(name = "page", required = false) Integer page,
        @Parameter(description = "페이지 크기(최대 200)")
        @RequestParam(name = "size", required = false) Integer size
    ) {
        List<Map<String, String>> errors = new ArrayList<>();
        if (leaveType != null) {
            var allowed = java.util.Set.of("ANNUAL", "SICK");
            if (!allowed.contains(leaveType)) {
                errors.add(Map.of("field", "type", "reason", "ALLOWED_VALUES: ANNUAL, SICK"));
            }
        }
        if (sortOrder != null) {
            var allowedSort = java.util.Set.of("ASC", "DESC");
            if (!allowedSort.contains(sortOrder)) {
                errors.add(Map.of("field", "sortOrder", "reason", "ALLOWED_VALUES: ASC, DESC"));
            }
        }
        if (page != null && page < 0) {
            errors.add(Map.of("field", "page", "reason", "MIN_0"));
        }
        if (size != null && size > 200) {
            errors.add(Map.of("field", "size", "reason", "MAX_200"));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        return hrmHttpService.getLeaveRequestList(departmentId, positionId, employeeName, leaveType, sortOrder, page, size);
    }

    // ==================== 교육 신청 및 프로그램 관리 ====================

    @PostMapping("/employee/request")
    @Operation(
        summary = "교육 신청",
        description = "직원이 교육 프로그램에 신청합니다."
    )
    public ResponseEntity<ApiResponse<Void>> requestTraining(
        @Valid @RequestBody TrainingRequestDto requestDto
    ) {
        return hrmHttpService.requestTraining(requestDto);
    }

    @PostMapping("/program/{employeeId}")
    @Operation(
        summary = "직원에게 교육 프로그램 추가",
        description = "특정 직원에게 교육 프로그램을 할당합니다."
    )
    public ResponseEntity<ApiResponse<Void>> assignProgramToEmployee(
        @Parameter(description = "직원 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("employeeId") String employeeId,
        @Valid @RequestBody ProgramAssignRequestDto requestDto
    ) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
                List.of(Map.of("field", "employeeId", "reason", "REQUIRED")));
        }

        return hrmHttpService.assignProgramToEmployee(employeeId, requestDto);
    }

    @PostMapping("/program")
    @Operation(
        summary = "교육 프로그램 추가",
        description = "새로운 교육 프로그램을 추가합니다."
    )
    public ResponseEntity<ApiResponse<Void>> createProgram(
        @Valid @RequestBody ProgramCreateRequestDto requestDto
    ) {
        return hrmHttpService.createTrainingProgram(requestDto);
    }

    @PatchMapping("/program/{programId}")
    @Operation(
        summary = "교육 프로그램 수정",
        description = "기존 교육 프로그램 정보를 수정합니다."
    )
    public ResponseEntity<ApiResponse<Void>> modifyProgram(
        @Parameter(description = "프로그램 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("programId") String programId,
        @Valid @RequestBody ProgramModifyRequestDto requestDto
    ) {
        if (programId == null || programId.isBlank()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
                List.of(Map.of("field", "programId", "reason", "REQUIRED")));
        }

        return hrmHttpService.updateTrainingProgram(programId, requestDto);
    }

    @GetMapping("/trainings/categories")
    @Operation(
        summary = "교육 카테고리 목록 조회 (enum 전체)",
        description = "교육 카테고리 enum의 모든 값을 조회합니다."
    )
    public ResponseEntity<ApiResponse<List<TrainingCategoryDto>>> getAllTrainingCategories() {
        return hrmHttpService.getAllTrainingCategories();
    }

    @GetMapping("/trainings/programs")
    @Operation(
        summary = "전체 교육 프로그램 목록 조회 (ID, Name만)",
        description = "전체 교육 프로그램의 ID와 Name만 간단히 조회합니다."
    )
    public ResponseEntity<ApiResponse<List<TrainingProgramSimpleDto>>> getAllTrainingPrograms() {
        return hrmHttpService.getAllTrainingPrograms();
    }

    @GetMapping("/trainings/completion-statuses")
    @Operation(
        summary = "교육 완료 상태 목록 조회",
        description = "교육 완료 상태의 모든 값을 조회합니다. (완료: true, 미완료: false)"
    )
    public ResponseEntity<ApiResponse<List<TrainingCompletionStatusDto>>> getAllTrainingCompletionStatuses() {
        return hrmHttpService.getAllTrainingCompletionStatuses();
    }

    // ==================== 직원 교육 현황 조회 ====================
    @GetMapping("/training-status")
    @Operation(
        summary = "직원 교육 현황 목록 조회",
        description = "부서/직급/이름으로 직원 교육 현황 목록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<TrainingStatusResponseDto>> getTrainingStatusList(
        @Parameter(description = "부서 ID")
        @RequestParam(name = "department", required = false) String departmentId,
        @Parameter(description = "직급 ID")
        @RequestParam(name = "position", required = false) String positionId,
        @Parameter(description = "직원 이름")
        @RequestParam(name = "name", required = false) String employeeName,
        @Parameter(description = "페이지 번호(0-base)")
        @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
        @Parameter(description = "페이지 크기(최대 200)")
        @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
    ) {
        List<Map<String, String>> errors = new ArrayList<>();
        if (page != null && page < 0) {
            errors.add(Map.of("field", "page", "reason", "MIN_0"));
        }
        if (size != null && size > 200) {
            errors.add(Map.of("field", "size", "reason", "MAX_200"));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        int p = page == null ? 0 : page;
        int s = (size == null || size < 1) ? 20 : size;

        return hrmHttpService.getTrainingStatusList(departmentId, positionId, employeeName, p, s);
    }

    // 직원 교육 현황 상세 조회

    // GET /api/business/hrm/training/employee/{employeeId}
//    @GetMapping("/training/employee/{employeeId}")
//    @Operation(
//        summary = "직원 교육 현황 상세 조회",
//        description = "특정 직원의 교육 현황 및 이력을 조회합니다."
//    )
//    public ResponseEntity<ApiResponse<EmployeeTrainingSummaryDto>> getTrainingStatusDetail(
//        @Parameter(description = "직원 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
//        @PathVariable("employeeId") String employeeId
//    ) {
//        if (employeeId == null || employeeId.isBlank()) {
//            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
//                List.of(Map.of("field", "employeeId", "reason", "REQUIRED")));
//        }
//
//        return hrmHttpService.getEmployeeTrainingSummary(employeeId);
//    }

    @GetMapping("/training/employee/{employeeId}")
    @Operation(
            summary = "직원 교육 상세 조회",
            description = "해당 직원의 교육 이력(교육명, 완료 여부, 완료일 등)을 조회합니다."
    )
    public ResponseEntity<ApiResponse<EmployeeTrainingHistoryDto>> getEmployeeTrainingHistory(
            @PathVariable String employeeId
    ) {

        if (employeeId == null || employeeId.isBlank()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
                List.of(Map.of("field", "employeeId", "reason", "REQUIRED")));
        }

        return hrmHttpService.getEmployeeTrainingHistory(employeeId);
    }

    // 교육 프로그램 목록 조회
    @GetMapping("/program")
    @Operation(
        summary = "교육 프로그램 목록 조회",
        description = "프로그램 이름/상태/카테고리로 교육 프로그램 목록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<Page<TrainingListItemDto>>> getTrainingPrograms(
        @Parameter(description = "프로그램 이름")
        @RequestParam(name = "name", required = false) String programName,
        @Parameter(description = "상태: IN_PROGRESS, COMPLETED, RECRUITING")
        @RequestParam(name = "status", required = false) String status,
        @Parameter(description = "카테고리: BASIC_TRAINING, TECHNICAL_TRAINING, SOFT_SKILL_TRAINING, MARKETING_TRAINING")
        @RequestParam(name = "category", required = false) String category,
        @Parameter(description = "페이지 번호(0-base)")
        @RequestParam(name = "page", required = false) Integer page,
        @Parameter(description = "페이지 크기(최대 200)")
        @RequestParam(name = "size", required = false) Integer size
    ) {
        List<Map<String, String>> errors = new ArrayList<>();
        if (status != null) {
            var allowedStatus = Set.of("IN_PROGRESS", "COMPLETED", "RECRUITING");
            if (!allowedStatus.contains(status)) {
                errors.add(Map.of("field", "status", "reason",
                    "ALLOWED_VALUES: IN_PROGRESS, COMPLETED, RECRUITING"));
            }
        }
        if (category != null) {
            var allowedCategory = Set.of("BASIC_TRAINING", "TECHNICAL_TRAINING",
                "SOFT_SKILL_TRAINING", "MARKETING_TRAINING");
            if (!allowedCategory.contains(category)) {
                errors.add(Map.of("field", "category", "reason",
                    "ALLOWED_VALUES: BASIC_TRAINING, TECHNICAL_TRAINING, SOFT_SKILL_TRAINING, MARKETING_TRAINING"));
            }
        }
        if (page != null && page < 0) {
            errors.add(Map.of("field", "page", "reason", "MIN_0"));
        }
        if (size != null && size > 200) {
            errors.add(Map.of("field", "size", "reason", "MAX_200"));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        return hrmHttpService.getTrainingPrograms(programName, status, category, page, size);
    }

    // 교육 프로그램 상세 조회
    @GetMapping("/program/{programId}")
    @Operation(
        summary = "교육 프로그램 상세 조회",
        description = "단일 교육 프로그램 상세 정보를 조회합니다."
    )
    public ResponseEntity<ApiResponse<TrainingResponseDto>> getTrainingProgramDetail(
        @Parameter(description = "프로그램 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("programId") String programId
    ) {
        if (programId == null || programId.isBlank()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
                List.of(Map.of("field", "programId", "reason", "REQUIRED")));
        }

        return hrmHttpService.getProgramDetailInfo(programId);
    }
}
