package org.ever._4ever_be_business.api.hr;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.common.exception.handler.ProblemDetailFactory;
import org.ever._4ever_be_business.hr.dto.request.*;
import org.ever._4ever_be_business.hr.dto.response.*;
import org.ever._4ever_be_business.hr.entity.CustomerUser;
import org.ever._4ever_be_business.hr.entity.Department;
import org.ever._4ever_be_business.hr.entity.InternelUser;
import org.ever._4ever_be_business.hr.entity.Position;
import org.ever._4ever_be_business.hr.enums.LeaveType;
import org.ever._4ever_be_business.hr.enums.PayrollStatus;
import org.ever._4ever_be_business.hr.enums.TrainingCategory;
import org.ever._4ever_be_business.hr.enums.TrainingStatus;
import org.ever._4ever_be_business.hr.repository.CustomerUserRepository;
import org.ever._4ever_be_business.hr.repository.DepartmentRepository;
import org.ever._4ever_be_business.hr.repository.InternelUserRepository;
import org.ever._4ever_be_business.hr.repository.PositionRepository;
import org.ever._4ever_be_business.hr.repository.TrainingRepository;
import org.ever._4ever_be_business.hr.service.*;
import org.ever._4ever_be_business.hr.vo.*;
import org.ever._4ever_be_business.sd.dto.response.DashboardWorkflowItemDto;
import org.ever._4ever_be_business.sd.dto.response.PageInfo;
import org.ever._4ever_be_business.tam.dto.request.CheckInRequestDto;
import org.ever._4ever_be_business.tam.dto.request.CheckOutRequestDto;
import org.ever._4ever_be_business.tam.dto.request.UpdateTimeRecordDto;
import org.ever._4ever_be_business.tam.dto.response.AttendanceListItemDto;
import org.ever._4ever_be_business.tam.dto.response.AttendanceListResponseDto;
import org.ever._4ever_be_business.tam.dto.response.AttendanceRecordDto;
import org.ever._4ever_be_business.tam.dto.response.AttendanceStatusDto;
import org.ever._4ever_be_business.tam.dto.response.TimeRecordDetailDto;
import org.ever._4ever_be_business.tam.dto.response.TimeRecordListItemDto;
import org.ever._4ever_be_business.tam.dto.response.TimeRecordListResponseDto;
import org.ever._4ever_be_business.tam.enums.AttendanceStatus;
import org.ever._4ever_be_business.tam.service.AttendanceService;
import org.ever._4ever_be_business.tam.service.TimeRecordService;
import org.ever._4ever_be_business.tam.vo.AttendanceListSearchConditionVo;
import org.ever._4ever_be_business.tam.vo.AttendanceSearchConditionVo;
import org.ever._4ever_be_business.tam.vo.TimeRecordDetailVo;
import org.ever.event.CreateAuthUserResultEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@ApiServerErrorResponse
public interface HrmApi {

    public HRStatisticsResponseDto getHRStatistics();

    public DepartmentDetailDto getDepartmentDetail(@PathVariable String departmentId);

    public List<DepartmentSimpleDto> getAllDepartments();

    public ResponseEntity<Void> updateDepartment(
            @PathVariable String departmentId,
            @RequestBody UpdateDepartmentRequestDto requestDto);

    public List<InventoryDepartmentEmployeeDto> getInventoryDepartmentEmployees();

    public List<DepartmentMemberDto> getDepartmentMembers(@PathVariable String departmentId);

    public List<PositionListItemDto> getPositionList();

    public PositionDetailDto getPositionDetail(@PathVariable String positionId);

    public List<PositionSimpleDto> getAllPositions();

    public List<PositionSimpleDto> getPositionsByDepartmentId(@PathVariable String departmentId);

    public EmployeeListResponseDto getEmployeeList(
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String positionId,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    public EmployeeDetailDto getEmployeeDetail(@PathVariable String employeeId);

    public EmployeeWithTrainingDto getEmployeeWithTrainingByInternelUserId(@PathVariable String internelUserId);

    public List<TrainingProgramSimpleDto> getAvailableTrainingsByInternelUserId(@PathVariable String internelUserId);

    public UserNameResponse getInternalUserName(@PathVariable String userId);

    public UserNameResponse getCustomerUserName(@PathVariable String userId);

    public CustomerUserDetailDto getCustomerUserDetailByUserId(@PathVariable String customerUserId);

    public ResponseEntity<Void> updateEmployee(
            @PathVariable String employeeId,
            @RequestBody UpdateEmployeeRequestDto requestDto);

    public ResponseEntity<Void> requestTraining(@RequestBody TrainingRequestDto requestDto);

    public ResponseEntity<Void> enrollTrainingProgram(
            @PathVariable String employeeId,
            @RequestBody TrainingRequestDto requestDto);

    public LeaveRequestListResponseDto getLeaveRequestList(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) LeaveType type,
            @RequestParam(required = false, defaultValue = "DESC") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    public ResponseEntity<Void> createLeaveRequest(
            @RequestBody CreateLeaveRequestDto requestDto,
            @RequestParam String InternelUserId
    );

    public ResponseEntity<Void> approveLeaveRequest(@PathVariable String requestId);

    public ResponseEntity<Void> rejectLeaveRequest(@PathVariable String requestId);

    public RemainingLeaveDaysDto getRemainingLeaveDays(@RequestParam String userId);

    public PaystubDetailDto getPaystubDetail(@PathVariable String payrollId);

    public PayrollListResponseDto getPayrollList(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String statusCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    public ResponseEntity<Void> completePayroll(@RequestBody CompletePayrollRequestDto requestDto);

    public ResponseEntity<Void> generateMonthlyPayroll();

    public DeferredResult<ResponseEntity<?>> getProgramDetailInfo(@PathVariable String programId);

    public TrainingListResponseDto getTrainingList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) TrainingStatus status,
            @RequestParam(required = false) TrainingCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    public ResponseEntity<Void> createTrainingProgram(@RequestBody CreateTrainingProgramDto requestDto);

    public ResponseEntity<Void> updateTrainingProgram(
            @PathVariable String programId,
            @RequestBody UpdateTrainingProgramDto requestDto);

    public List<TrainingCategoryDto> getAllTrainingCategories();

    public List<TrainingProgramSimpleDto> getAllTrainingPrograms();

    public List<TrainingCompletionStatusDto> getAllTrainingCompletionStatuses();

    public EmployeeTrainingHistoryDto getEmployeeTrainingHistory(@PathVariable String employeeId);

    public EmployeeTrainingListResponseDto getEmployeeTrainingList(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    public TrainingStatusResponseDto getTrainingStatusList(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    public EmployeeTrainingSummaryDto getEmployeeTrainingSummary(@PathVariable String employeeId);

    public TimeRecordDetailDto getTimeRecordDetail(@PathVariable String timerecordId);

    public ResponseEntity<Void> updateTimeRecord(
            @PathVariable String timerecordId,
            @RequestBody UpdateTimeRecordDto requestDto);

    public TimeRecordListResponseDto getAttendanceList(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String name,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String statusCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    public AttendanceListResponseDto getAttendanceHistoryList(
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    public ResponseEntity<Void> checkIn(@RequestBody CheckInRequestDto requestDto);

    public ResponseEntity<Void> checkOut(@RequestBody CheckOutRequestDto requestDto);

    public ResponseEntity<Void> checkInByInternelUserId(@RequestBody CheckInRequestDto requestDto);

    public ResponseEntity<Void> checkOutByInternelUserId(@RequestBody CheckOutRequestDto requestDto);

    public List<AttendanceRecordDto> getAttendanceRecordsByInternelUserId(@PathVariable String internelUserId);

    public List<AttendanceStatusDto> getAllAttendanceStatuses();

    public HrmEmployeeBasicInfoDto getEmployeeBasicInfo(@PathVariable String userId);

    public List<HrmEmployeeBasicInfoDto> getEmployeesBasicInfo(@RequestBody EmployeesMultipleRequestDto requestDto);

    @Operation(summary = "내부 사용자 생성", description = "내부 사용자 생성을 비동기로 처리합니다.")
    public DeferredResult<ResponseEntity<?>> createEmployeeUser(
            @RequestBody @Valid EmployeeCreateRequestDto requestDto
    );

    public EmployeeProfileDto getEmployeeProfile(@PathVariable String internelUserId);

    public List<EmployeeAttendanceRecordDto> getEmployeeAttendanceRecords(@PathVariable String internelUserId);

    public TodayAttendanceDto getTodayAttendance(@PathVariable String internelUserId);

    public List<TrainingItemDto> getInProgressTrainings(@PathVariable String internelUserId);

    public List<TrainingItemDto> getAvailableTrainings(@PathVariable String internelUserId);

    public List<TrainingItemDto> getCompletedTrainings(@PathVariable String internelUserId);

    public ResponseEntity<Void> updateProfile(
            @PathVariable String internelUserId,
            @RequestBody UpdateProfileRequestDto requestDto);

    public org.ever._4ever_be_business.hr.dto.response.CustomerInfoDto getCustomerInfo(
            @PathVariable String customerUserId);

    public ResponseEntity<Void> postTrainingProgram(
            @RequestParam String internelUserId,
            @RequestParam String programId);

    public List<DashboardWorkflowItemDto> getDashboardAttendanceList(
            @RequestParam("userId") String userId,
            @RequestParam(value = "size", defaultValue = "5") int size
    );

    public List<DashboardWorkflowItemDto> getDashboardLeaveRequestList(
            @RequestParam("userId") String userId,
            @RequestParam(value = "size", defaultValue = "5") int size
    );

}
