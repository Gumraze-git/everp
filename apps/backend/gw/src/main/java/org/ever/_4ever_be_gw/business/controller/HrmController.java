package org.ever._4ever_be_gw.business.controller;

import org.ever._4ever_be_gw.api.business.HrmApi;
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

public class HrmController implements HrmApi {
    private final HrmService hrmService;
    private final HrmHttpService hrmHttpService;

    public HrmController(HrmService hrmService, HrmHttpService hrmHttpService) {
        this.hrmService = hrmService;
        this.hrmHttpService = hrmHttpService;
    }

    // ==================== 인적자원 통계 ====================

    @GetMapping("/metrics")

    public ResponseEntity<?> getEmployeeStatistics(

        @RequestParam(name = "periods", required = false) String periods
    ) {
        return hrmHttpService.getHRStatistics();
    }

    // ==================== 직원 관리 ====================

    @PostMapping("/employees")
    @PreAuthorize("hasAnyAuthority('HRM_USER', 'HRM_ADMIN', 'ALL_ADMIN')")

    public Mono<ResponseEntity<CreateAuthUserResultDto>> signupEmployee(
        @Valid @RequestBody EmployeeCreateRequestDto requestDto
    ) {
        return hrmService.createInternalUser(requestDto)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/employees/{employeeId}")
    @PreAuthorize("hasAnyAuthority('HRM_USER', 'HRM_ADMIN', 'ALL_ADMIN')")

    public ResponseEntity<?> updateEmployee(

        @PathVariable("employeeId") String employeeId,
        @Valid @RequestBody EmployeeUpdateRequestDto requestDto
    ) {
        return hrmHttpService.updateEmployee(employeeId, requestDto);
    }

    @GetMapping("/employees")

    public ResponseEntity<?> getEmployees(

        @RequestParam(name = "departmentId", required = false) String departmentId,

        @RequestParam(name = "positionId", required = false) String positionId,

        @RequestParam(name = "name", required = false) String name,

        @RequestParam(name = "page", required = false) Integer page,

        @RequestParam(name = "size", required = false) Integer size
    ) {
        return hrmHttpService.getEmployeeList(departmentId, positionId, name, page, size);
    }

    @GetMapping("/employees/{employeeId}")
    @PreAuthorize("hasAnyAuthority('HRM_USER', 'HRM_ADMIN', 'ALL_ADMIN')")

    public ResponseEntity<?> getEmployeeDetail(

        @PathVariable("employeeId") String employeeId
    ) {
        return hrmHttpService.getEmployeeDetail(employeeId);
    }

    @GetMapping("/employees/by-internal-user/{internelUserId}")

    public ResponseEntity<?> getEmployeeWithTrainingByInternelUserId(

        @PathVariable("internelUserId") String internelUserId
    ) {
        return hrmHttpService.getEmployeeWithTrainingByInternelUserId(internelUserId);
    }

    @GetMapping("/employees/by-internal-user/{internelUserId}/available-trainings")

    public ResponseEntity<?> getAvailableTrainingsByInternelUserId(

        @PathVariable("internelUserId") String internelUserId
    ) {
        return hrmHttpService.getAvailableTrainingsByInternelUserId(internelUserId);
    }

    @GetMapping("/customers/by-customer-user/{customerUserId}")

    public ResponseEntity<?> getCustomerUserDetailByUserId(

        @PathVariable("customerUserId") String customerUserId
    ) {
        return hrmHttpService.getCustomerUserDetailByUserId(customerUserId);
    }

    // ==================== 부서 관리 ====================

    @GetMapping("/departments")

    public ResponseEntity<?> getDepartments(

        @RequestParam(name = "status", required = false) String status,

        @RequestParam(name = "page", required = false) Integer page,

        @RequestParam(name = "size", required = false) Integer size
    ) {
        return hrmHttpService.getDepartmentList(status, page, size);
    }

    @GetMapping("/departments/options")

    public ResponseEntity<?> getAllDepartmentsSimple() {
        return hrmHttpService.getAllDepartmentsSimple();
    }

    @GetMapping("/departments/{departmentId}/members")

    public ResponseEntity<?> getDepartmentMembers(

        @PathVariable("departmentId") String departmentId
    ) {
        return hrmHttpService.getDepartmentMembers(departmentId);
    }

    @PatchMapping("/departments/{departmentId}")
    public ResponseEntity<?> updateDepartment(
            @PathVariable String departmentId,
            @RequestBody UpdateDepartmentRequestDto requestDto
    ) {
        return hrmHttpService.updateDepartment(departmentId, requestDto);
    }

    // ==================== 직급 관리 ====================

    @GetMapping("/positions")

    public ResponseEntity<?> getPositions() {
        return hrmHttpService.getPositionList();
    }

    @GetMapping("/positions/{positionId}")

    public ResponseEntity<?> getPositionDetail(

        @PathVariable("positionId") String positionId
    ) {
        return hrmHttpService.getPositionDetail(positionId);
    }

    @GetMapping("/departments/{departmentId}/positions/options")

    public ResponseEntity<?> getPositionsByDepartmentId(

        @PathVariable("departmentId") String departmentId
    ) {
        return hrmHttpService.getPositionsByDepartmentId(departmentId);
    }

    // ==================== 출퇴근 관리 ====================

    @GetMapping("/attendance")

    public ResponseEntity<?> getAttendance(

        @RequestParam(name = "employeeId", required = false) String employeeId,

        @RequestParam(name = "startDate", required = false) String startDate,

        @RequestParam(name = "endDate", required = false) String endDate,

        @RequestParam(name = "status", required = false) String status,

        @RequestParam(name = "page", required = false) Integer page,

        @RequestParam(name = "size", required = false) Integer size
    ) {
        return hrmHttpService.getAttendanceHistoryList(employeeId, startDate, endDate, status, page, size);
    }

    @PatchMapping("/attendance/check-in")

    public ResponseEntity<?> checkIn(

        @AuthenticationPrincipal EverUserPrincipal principal
    ) {
        String targetInternelUserId = principal.getUserId();

        return hrmHttpService.checkIn(targetInternelUserId);
    }

    @PatchMapping("/attendance/check-out")

    public ResponseEntity<?> checkOut(

        @AuthenticationPrincipal EverUserPrincipal everUser
    ) {
        String targetInternelUserId = everUser.getUserId();

        return hrmHttpService.checkOut(targetInternelUserId);
    }

    @PatchMapping("/attendance/self")
    public ResponseEntity<?> updateOwnAttendance(
        @AuthenticationPrincipal EverUserPrincipal principal,
        @RequestBody Map<String, String> requestBody
    ) {
        String targetInternelUserId = principal.getUserId();
        String status = requestBody.get("status");

        if ("CHECKED_IN".equalsIgnoreCase(status)) {
            return hrmHttpService.checkIn(targetInternelUserId);
        }
        if ("CHECKED_OUT".equalsIgnoreCase(status)) {
            return hrmHttpService.checkOut(targetInternelUserId);
        }

        throw new ValidationException(
            ErrorCode.VALIDATION_FAILED,
            List.of(Map.of("field", "status", "reason", "ALLOWED_VALUES: CHECKED_IN, CHECKED_OUT"))
        );
    }

    @GetMapping("/employees/by-internal-user/{internelUserId}/attendance-records")

    public ResponseEntity<?> getAttendanceRecordsByInternelUserId(

        @PathVariable String internelUserId
    ) {
        return hrmHttpService.getAttendanceRecordsByInternelUserId(internelUserId);
    }

    @GetMapping("/attendance/statuses")

    public ResponseEntity<?> getAllAttendanceStatuses() {
        return hrmHttpService.getAllAttendanceStatuses();
    }

    // 월별 사내 급여 목록 조회
    @GetMapping("/payroll")

    public ResponseEntity<?> getMonthlyPayrollList(

        @RequestParam(name = "year") Integer year,

        @RequestParam(name = "month") Integer month,

        @RequestParam(required = false) String statusCode,

        @RequestParam(name = "name", required = false) String employeeName,

        @RequestParam(name = "department", required = false) String departmentId,

        @RequestParam(name = "position", required = false) String positionId,

        @RequestParam(name = "page", required = false) Integer page,

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

    public ResponseEntity<?> completePayroll(
        @Valid @RequestBody PayrollCompleteRequestDto requestDto
    ) {
        return hrmHttpService.completePayroll(requestDto);
    }

    // 모든 직원 당월 급여 생성
    @GetMapping("/payroll/generate")

    public ResponseEntity<?> generateMonthlyPayroll() {
        return hrmHttpService.generateMonthlyPayroll();
    }

    // 월별 사내 급여 상세 조회
    @GetMapping("/payroll/{payrollId}")

    public ResponseEntity<?> getMonthlyPayrollDetail(

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

    public ResponseEntity<?> getAllPayrollStatuses() {
        return hrmHttpService.getAllPayrollStatuses();
    }

    // ==================== 근태 기록 관리 ====================

    @PutMapping("/attendance-records/{timerecordId}")

    public ResponseEntity<?> updateTimeRecord(

        @PathVariable("timerecordId") String timerecordId,
        @Valid @RequestBody TimeRecordUpdateRequestDto requestDto
    ) {
        return hrmHttpService.updateTimeRecord(timerecordId, requestDto);
    }

    // ==================== 휴가 관리 ====================

    @PostMapping("/leave-requests")

    public ResponseEntity<?> requestLeave(
        @AuthenticationPrincipal EverUserPrincipal principal,
        @Valid @RequestBody LeaveRequestDto requestDto
    ) {
        String InternelUserId = principal.getUserId();
        return hrmHttpService.requestLeave(requestDto,InternelUserId);
    }

    @PatchMapping("/leave/request/{requestId}/release")

    public ResponseEntity<?> approveLeaveRequest(

        @PathVariable("requestId") String requestId
    ) {
        return hrmHttpService.approveLeaveRequest(requestId);
    }

    @PatchMapping("/leave/request/{requestId}/reject")

    public ResponseEntity<?> rejectLeaveRequest(

        @PathVariable("requestId") String requestId
    ) {
        return hrmHttpService.rejectLeaveRequest(requestId);
    }

    // ==================== 기존 조회 API들 ====================
    // GET /api/business/tam/time-record?department=&position=&name=&date=&page=&size=
    @GetMapping("/attendance-records")

    public ResponseEntity<?> getTimeRecords(

        @RequestParam(name = "department", required = false) String departmentId,

        @RequestParam(required = false) String statusCode,

        @RequestParam(name = "position", required = false) String positionId,

        @RequestParam(name = "name", required = false) String employeeName,

        @RequestParam(name = "date") String date,

        @RequestParam(name = "page", required = false) Integer page,

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
    @GetMapping("/attendance-records/{timerecordId}")

    public ResponseEntity<?> getTimeRecordDetail(

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
    @GetMapping("/leave-requests")

    public ResponseEntity<?> getLeaveRequestList(

        @RequestParam(name = "department", required = false) String departmentId,

        @RequestParam(name = "position", required = false) String positionId,

        @RequestParam(name = "name", required = false) String employeeName,

        @RequestParam(name = "type", required = false) String leaveType,

        @RequestParam(name = "sortOrder", required = false, defaultValue = "DESC") String sortOrder,

        @RequestParam(name = "page", required = false) Integer page,

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

    @PostMapping("/training-enrollments")

    public ResponseEntity<?> requestTraining(
        @Valid @RequestBody TrainingRequestDto requestDto
    ) {
        return hrmHttpService.requestTraining(requestDto);
    }

    @PostMapping("/employees/{employeeId}/programs")

    public ResponseEntity<?> assignProgramToEmployee(

        @PathVariable("employeeId") String employeeId,
        @Valid @RequestBody ProgramAssignRequestDto requestDto
    ) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
                List.of(Map.of("field", "employeeId", "reason", "REQUIRED")));
        }

        return hrmHttpService.assignProgramToEmployee(employeeId, requestDto);
    }

    @PostMapping("/programs")

    public ResponseEntity<?> createProgram(
        @Valid @RequestBody ProgramCreateRequestDto requestDto
    ) {
        return hrmHttpService.createTrainingProgram(requestDto);
    }

    @PatchMapping("/programs/{programId}")

    public ResponseEntity<?> modifyProgram(

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

    public ResponseEntity<?> getAllTrainingCategories() {
        return hrmHttpService.getAllTrainingCategories();
    }

    @GetMapping("/trainings/programs")

    public ResponseEntity<?> getAllTrainingPrograms() {
        return hrmHttpService.getAllTrainingPrograms();
    }

    @GetMapping("/trainings/completion-statuses")

    public ResponseEntity<?> getAllTrainingCompletionStatuses() {
        return hrmHttpService.getAllTrainingCompletionStatuses();
    }

    // ==================== 직원 교육 현황 조회 ====================
    @GetMapping("/training-records")

    public ResponseEntity<?> getTrainingStatusList(

        @RequestParam(name = "department", required = false) String departmentId,

        @RequestParam(name = "position", required = false) String positionId,

        @RequestParam(name = "name", required = false) String employeeName,

        @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,

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
//
//    public ResponseEntity<?> getTrainingStatusDetail(
//
//        @PathVariable("employeeId") String employeeId
//    ) {
//        if (employeeId == null || employeeId.isBlank()) {
//            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
//                List.of(Map.of("field", "employeeId", "reason", "REQUIRED")));
//        }
//
//        return hrmHttpService.getEmployeeTrainingSummary(employeeId);
//    }

    @GetMapping("/training-records/{employeeId}")

    public ResponseEntity<?> getEmployeeTrainingHistory(
            @PathVariable String employeeId
    ) {

        if (employeeId == null || employeeId.isBlank()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
                List.of(Map.of("field", "employeeId", "reason", "REQUIRED")));
        }

        return hrmHttpService.getEmployeeTrainingHistory(employeeId);
    }

    // 교육 프로그램 목록 조회
    @GetMapping("/programs")

    public ResponseEntity<?> getTrainingPrograms(

        @RequestParam(name = "name", required = false) String programName,

        @RequestParam(name = "status", required = false) String status,

        @RequestParam(name = "category", required = false) String category,

        @RequestParam(name = "page", required = false) Integer page,

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
    @GetMapping("/programs/{programId}")
    public ResponseEntity<?> getTrainingProgramDetail(

        @PathVariable("programId") String programId
    ) {
        if (programId == null || programId.isBlank()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
                List.of(Map.of("field", "programId", "reason", "REQUIRED")));
        }

        return hrmHttpService.getProgramDetailInfo(programId);
    }

    @PatchMapping("/leave-requests/{requestId}")
    public ResponseEntity<?> updateLeaveRequestStatus(
        @PathVariable("requestId") String requestId,
        @RequestBody Map<String, String> requestBody
    ) {
        String status = requestBody.get("status");

        if ("APPROVED".equalsIgnoreCase(status)) {
            return hrmHttpService.approveLeaveRequest(requestId);
        }
        if ("REJECTED".equalsIgnoreCase(status)) {
            return hrmHttpService.rejectLeaveRequest(requestId);
        }

        throw new ValidationException(
            ErrorCode.VALIDATION_FAILED,
            List.of(Map.of("field", "status", "reason", "ALLOWED_VALUES: APPROVED, REJECTED"))
        );
    }
}
