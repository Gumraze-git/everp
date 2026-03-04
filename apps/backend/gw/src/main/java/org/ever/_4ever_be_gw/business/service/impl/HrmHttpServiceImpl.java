package org.ever._4ever_be_gw.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.ever._4ever_be_gw.business.service.HrmHttpService;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HrmHttpServiceImpl implements HrmHttpService {

    private final WebClientProvider webClientProvider;

    // ==================== Statistics ====================

    @Override
    public ResponseEntity<ApiResponse<HRStatisticsResponseDto>> getHRStatistics() {
        log.debug("HR 통계 조회 요청");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/statistics")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            // ongoingProgramCount와 completedProgramCount 제거
            if (response != null && response.getData() != null && response.getData() instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> data = (java.util.Map<String, Object>) response.getData();
                data.remove("ongoingProgramCount");
                data.remove("completedProgramCount");
            }

            log.info("HR 통계 조회 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("HR 통계 조회", ex);
        } catch (Exception e) {
            log.error("HR 통계 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("HR 통계 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Departments ====================

    @Override
    public ResponseEntity<ApiResponse<DepartmentListResponseDto>> getDepartmentList(String status, Integer page, Integer size) {
        log.debug("부서 목록 조회 요청 - status: {}, page: {}, size: {}", status, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/departments");
                        if (status != null) builder.queryParam("status", status);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 20);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("부서 목록 조회 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("부서 목록 조회", ex);
        } catch (Exception e) {
            log.error("부서 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("부서 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> updateDepartment(String departmentId, UpdateDepartmentRequestDto requestDto) {
        log.debug("부서 정보 수정 요청 - departmentId: {}, body: {}", departmentId, requestDto);

        try {
            WebClient businessWebClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessWebClient.patch()
                    .uri("/hrm/departments/{departmentId}", departmentId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("부서 정보 수정 완료 - departmentId: {}", departmentId);

            ApiResponse<Void> result = (ApiResponse<Void>) response;
            return ResponseEntity.ok(result);

        } catch (org.springframework.web.reactive.function.client.WebClientResponseException ex) {
            return handleWebClientError("부서 정보 수정", ex);
        } catch (Exception e) {
            log.error("부서 정보 수정 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("부서 정보 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<DepartmentDetailDto>> getDepartmentDetail(String departmentId) {
        log.debug("부서 상세 조회 요청 - departmentId: {}", departmentId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/organization/department/{departmentId}", departmentId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("부서 상세 조회 성공 - departmentId: {}", departmentId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("부서 상세 조회", ex);
        } catch (Exception e) {
            log.error("부서 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("부서 상세 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<DepartmentSimpleDto>>> getAllDepartmentsSimple() {
        log.debug("전체 부서 목록 조회 요청 (ID, Name만)");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/departments/simple")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("전체 부서 목록 조회 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("전체 부서 목록 조회", ex);
        } catch (Exception e) {
            log.error("전체 부서 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("전체 부서 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<DepartmentMemberDto>>> getDepartmentMembers(String departmentId) {
        log.debug("부서 구성원 목록 조회 요청 - departmentId: {}", departmentId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/departments/{departmentId}/members", departmentId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("부서 구성원 목록 조회 성공 - departmentId: {}", departmentId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("부서 구성원 목록 조회", ex);
        } catch (Exception e) {
            log.error("부서 구성원 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("부서 구성원 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Positions ====================

    @Override
    public ResponseEntity<ApiResponse<List<PositionListItemDto>>> getPositionList() {
        log.debug("직급 목록 조회 요청");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/positions")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("직급 목록 조회 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("직급 목록 조회", ex);
        } catch (Exception e) {
            log.error("직급 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("직급 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<PositionSimpleDto>>> getPositionsByDepartmentId(String departmentId) {
        log.debug("부서별 직급 목록 조회 요청 - departmentId: {}", departmentId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/" + departmentId + "/positions/all")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("부서별 직급 목록 조회 성공 - departmentId: {}", departmentId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("부서별 직급 목록 조회", ex);
        } catch (Exception e) {
            log.error("부서별 직급 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("부서별 직급 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<PositionDetailDto>> getPositionDetail(String positionId) {
        log.debug("직급 상세 조회 요청 - positionId: {}", positionId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/organization/position/{positionId}", positionId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("직급 상세 조회 성공 - positionId: {}", positionId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("직급 상세 조회", ex);
        } catch (Exception e) {
            log.error("직급 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("직급 상세 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Employees ====================

    @Override
    public ResponseEntity<ApiResponse<Page<EmployeeListItemDto>>> getEmployeeList(
            String departmentId, String positionId, String name, Integer page, Integer size) {
        log.debug("직원 목록 조회 요청 - departmentId: {}, positionId: {}, name: {}, page: {}, size: {}",
                departmentId, positionId, name, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/employee");
                        if (departmentId != null) builder.queryParam("departmentId", departmentId);
                        if (positionId != null) builder.queryParam("positionId", positionId);
                        if (name != null) builder.queryParam("name", name);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 20);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("직원 목록 조회 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("직원 목록 조회", ex);
        } catch (Exception e) {
            log.error("직원 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("직원 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<EmployeeDetailDto>> getEmployeeDetail(String employeeId) {
        log.debug("직원 상세 조회 요청 - employeeId: {}", employeeId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/employee/{employeeId}", employeeId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("직원 상세 조회 성공 - employeeId: {}", employeeId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("직원 상세 조회", ex);
        } catch (Exception e) {
            log.error("직원 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("직원 상세 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<EmployeeWithTrainingDto>> getEmployeeWithTrainingByInternelUserId(String internelUserId) {
        log.debug("InternelUser ID로 직원 정보 및 교육 이력 조회 요청 - internelUserId: {}", internelUserId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/employees/{internelUserId}", internelUserId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("InternelUser ID로 직원 정보 및 교육 이력 조회 성공 - internelUserId: {}", internelUserId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("InternelUser ID로 직원 정보 및 교육 이력 조회", ex);
        } catch (Exception e) {
            log.error("InternelUser ID로 직원 정보 및 교육 이력 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("InternelUser ID로 직원 정보 및 교육 이력 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<TrainingProgramSimpleDto>>> getAvailableTrainingsByInternelUserId(String internelUserId) {
        log.debug("InternelUser ID로 수강 가능한 교육 프로그램 목록 조회 요청 - internelUserId: {}", internelUserId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/employees/{internelUserId}/available-trainings", internelUserId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("InternelUser ID로 수강 가능한 교육 프로그램 목록 조회 성공 - internelUserId: {}", internelUserId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("InternelUser ID로 수강 가능한 교육 프로그램 목록 조회", ex);
        } catch (Exception e) {
            log.error("InternelUser ID로 수강 가능한 교육 프로그램 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("InternelUser ID로 수강 가능한 교육 프로그램 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<CustomerUserDetailDto>> getCustomerUserDetailByUserId(String customerUserId) {
        log.debug("CustomerUser ID로 고객 사용자 상세 정보 조회 요청 - customerUserId: {}", customerUserId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/customers/by-customer-user/{customerUserId}", customerUserId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("CustomerUser ID로 고객 사용자 상세 정보 조회 성공 - customerUserId: {}", customerUserId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("CustomerUser ID로 고객 사용자 상세 정보 조회", ex);
        } catch (Exception e) {
            log.error("CustomerUser ID로 고객 사용자 상세 정보 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("CustomerUser ID로 고객 사용자 상세 정보 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> updateEmployee(String employeeId, EmployeeUpdateRequestDto requestDto) {
        log.debug("직원 정보 수정 요청 - employeeId: {}, body: {}", employeeId, requestDto);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.patch()
                    .uri("/hrm/employee/{employeeId}", employeeId)
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("직원 정보 수정 성공 - employeeId: {}", employeeId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("직원 정보 수정", ex);
        } catch (Exception e) {
            log.error("직원 정보 수정 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("직원 정보 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> requestTraining(TrainingRequestDto requestDto) {
        log.debug("교육 프로그램 신청 요청 - body: {}", requestDto);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.post()
                    .uri("/hrm/employee/request")
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("교육 프로그램 신청 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("교육 프로그램 신청", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 신청 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("교육 프로그램 신청 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> enrollTrainingProgram(String employeeId, ProgramAssignRequestDto requestDto) {
        log.debug("교육 프로그램 등록 요청 - employeeId: {}, body: {}", employeeId, requestDto);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.post()
                    .uri("/hrm/program/{employeeId}", employeeId)
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("교육 프로그램 등록 성공 - employeeId: {}", employeeId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("교육 프로그램 등록", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 등록 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("교육 프로그램 등록 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Leave Requests ====================

    @Override
    public ResponseEntity<ApiResponse<Page<LeaveRequestListItemDto>>> getLeaveRequestList(
            String department, String position, String name, String type, String sortOrder, Integer page, Integer size) {
        log.debug("휴가 신청 목록 조회 요청 - department: {}, position: {}, name: {}, type: {}, sortOrder: {}, page: {}, size: {}",
                department, position, name, type, sortOrder, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/leave/request");
                        if (department != null) builder.queryParam("department", department);
                        if (position != null) builder.queryParam("position", position);
                        if (name != null) builder.queryParam("name", name);
                        if (type != null) builder.queryParam("type", type);
                        if (sortOrder != null) builder.queryParam("sortOrder", sortOrder);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 10);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("휴가 신청 목록 조회 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("휴가 신청 목록 조회", ex);
        } catch (Exception e) {
            log.error("휴가 신청 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("휴가 신청 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> createLeaveRequest(LeaveRequestDto requestDto) {
        log.debug("휴가 신청 요청 - body: {}", requestDto);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.post()
                    .uri("/hrm/leave/request")
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("휴가 신청 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("휴가 신청", ex);
        } catch (Exception e) {
            log.error("휴가 신청 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("휴가 신청 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> approveLeaveRequest(String requestId) {
        log.debug("휴가 신청 승인 요청 - requestId: {}", requestId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.patch()
                    .uri("/hrm/leave/request/{requestId}/release", requestId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("휴가 신청 승인 성공 - requestId: {}", requestId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("휴가 신청 승인", ex);
        } catch (Exception e) {
            log.error("휴가 신청 승인 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("휴가 신청 승인 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> rejectLeaveRequest(String requestId) {
        log.debug("휴가 신청 반려 요청 - requestId: {}", requestId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.patch()
                    .uri("/hrm/leave/request/{requestId}/reject", requestId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("휴가 신청 반려 성공 - requestId: {}", requestId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("휴가 신청 반려", ex);
        } catch (Exception e) {
            log.error("휴가 신청 반려 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("휴가 신청 반려 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Payroll ====================

    @Override
    public ResponseEntity<ApiResponse<PaystubDetailDto>> getPaystubDetail(String payrollId) {
        log.debug("급여 명세서 상세 조회 요청 - payrollId: {}", payrollId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/payroll/{payrollId}", payrollId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("급여 명세서 상세 조회 성공 - payrollId: {}", payrollId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("급여 명세서 상세 조회", ex);
        } catch (Exception e) {
            log.error("급여 명세서 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("급여 명세서 상세 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Page<PayrollListItemDto>>> getPayrollList(
            Integer year, Integer month, String name, String department, String position,String statusCode, Integer page, Integer size) {
        log.debug("급여 명세서 목록 조회 요청 - year: {}, month: {}, name: {}, department: {}, position: {}, page: {}, size: {}",
                year, month, name, department, position, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/payroll");
                        if (year != null) builder.queryParam("year", year);
                        if (month != null) builder.queryParam("month", month);
                        if (name != null) builder.queryParam("name", name);
                        if (department != null) builder.queryParam("department", department);
                        if (position != null) builder.queryParam("position", position);
                        if (statusCode != null) builder.queryParam("statusCode", statusCode);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 10);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("급여 명세서 목록 조회 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("급여 명세서 목록 조회", ex);
        } catch (Exception e) {
            log.error("급여 명세서 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("급여 명세서 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> completePayroll(PayrollCompleteRequestDto requestDto) {
        log.debug("급여 지급 완료 처리 요청 - body: {}", requestDto);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.post()
                    .uri("/hrm/payroll/complete")
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("급여 지급 완료 처리 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("급여 지급 완료 처리", ex);
        } catch (Exception e) {
            log.error("급여 지급 완료 처리 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("급여 지급 완료 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> generateMonthlyPayroll() {
        log.debug("모든 직원 당월 급여 생성 요청");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/payroll/generate")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("모든 직원 당월 급여 생성 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("모든 직원 당월 급여 생성", ex);
        } catch (Exception e) {
            log.error("모든 직원 당월 급여 생성 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("모든 직원 당월 급여 생성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<PayrollStatusDto>>> getAllPayrollStatuses() {
        log.debug("급여 상태 목록 조회 요청");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/payroll/statuses")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("급여 상태 목록 조회 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("급여 상태 목록 조회", ex);
        } catch (Exception e) {
            log.error("급여 상태 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("급여 상태 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Attendance ====================

    @Override
    public ResponseEntity<ApiResponse<List<AttendanceStatusDto>>> getAllAttendanceStatuses() {
        log.debug("출퇴근 상태 목록 조회 요청");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/attendance/statuses")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("출퇴근 상태 목록 조회 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("출퇴근 상태 목록 조회", ex);
        } catch (Exception e) {
            log.error("출퇴근 상태 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("출퇴근 상태 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Training ====================

    @Override
    public ResponseEntity<ApiResponse<TrainingResponseDto>> getProgramDetailInfo(String programId) {
        log.debug("교육 프로그램 상세 정보 조회 요청 - programId: {}", programId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/trainings/program/{programId}", programId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("교육 프로그램 상세 정보 조회 성공 - programId: {}", programId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("교육 프로그램 상세 정보 조회", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 상세 정보 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("교육 프로그램 상세 정보 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Page<TrainingListItemDto>>> getTrainingList(
            String name, String status, String category, Integer page, Integer size) {
        log.debug("교육 프로그램 목록 조회 요청 - name: {}, status: {}, category: {}, page: {}, size: {}",
                name, status, category, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/trainings/program");
                        if (name != null) builder.queryParam("name", name);
                        if (status != null) builder.queryParam("status", status);
                        if (category != null) builder.queryParam("category", category);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 10);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("교육 프로그램 목록 조회 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("교육 프로그램 목록 조회", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("교육 프로그램 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> createTrainingProgram(ProgramCreateRequestDto requestDto) {
        log.debug("교육 프로그램 생성 요청 - body: {}", requestDto);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.post()
                    .uri("/hrm/trainings/program")
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("교육 프로그램 생성 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("교육 프로그램 생성", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 생성 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("교육 프로그램 생성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> updateTrainingProgram(String programId, ProgramModifyRequestDto requestDto) {
        log.debug("교육 프로그램 수정 요청 - programId: {}, body: {}", programId, requestDto);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.patch()
                    .uri("/hrm/program/{programId}", programId)
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("교육 프로그램 수정 성공 - programId: {}", programId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("교육 프로그램 수정", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 수정 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("교육 프로그램 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<TrainingCategoryDto>>> getAllTrainingCategories() {
        log.debug("교육 카테고리 목록 조회 요청");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/trainings/categories")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("교육 카테고리 목록 조회 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("교육 카테고리 목록 조회", ex);
        } catch (Exception e) {
            log.error("교육 카테고리 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("교육 카테고리 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardAttendanceList(String userId, Integer size) {
        log.debug("[DASHBOARD][HRM] 근태 목록 요청 - userId: {}, size: {}", userId, size);

        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.fail("hrm userId is required", HttpStatus.BAD_REQUEST, null)
            );
        }

        final int pageSize = (size != null && size > 0) ? size : 5;

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<List<DashboardWorkflowItemDto>> body = businessClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/hrm/dashboard/attendance")
                            .queryParam("userId", userId)
                            .queryParam("size", pageSize)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<DashboardWorkflowItemDto>>>() {})
                    .block();

            if (body == null) {
                log.error("[ERROR][DASHBOARD][HRM] 비즈니스 서버 응답이 null");
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "비즈니스 서버 응답이 비어 있습니다.");
            }

            List<DashboardWorkflowItemDto> data = body.getData();
            if (data == null) {
                log.error("[ERROR][DASHBOARD][HRM] 비즈니스 서버 응답에 data 필드가 없음");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "비즈니스 서버 응답 형식이 올바르지 않습니다.");
            }

            log.info("[INFO][DASHBOARD][HRM] 근태 목록 조회 성공");
            return ResponseEntity.ok(ApiResponse.success(data, "근태 목록 조회 성공", HttpStatus.OK));
        } catch (WebClientResponseException ex) {
            return handleWebClientError("대시보드 근태 목록 조회", ex);
        } catch (Exception e) {
            log.error("[ERROR][DASHBOARD][HRM] 근태 목록 조회 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("대시보드 근태 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardLeaveRequestList(String userId, Integer size) {
        log.debug("[DASHBOARD][HRM] 휴가 신청 목록 요청 - userId: {}, size: {}", userId, size);

        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.fail("hrm userId is required", HttpStatus.BAD_REQUEST, null)
            );
        }

        final int pageSize = (size != null && size > 0) ? size : 5;

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<List<DashboardWorkflowItemDto>> body = businessClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/hrm/dashboard/leave-requests")
                            .queryParam("userId", userId)
                            .queryParam("size", pageSize)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<DashboardWorkflowItemDto>>>() {})
                    .block();

            if (body == null) {
                log.error("[ERROR][DASHBOARD][HRM] 비즈니스 서버 응답이 null");
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "비즈니스 서버 응답이 비어 있습니다.");
            }

            List<DashboardWorkflowItemDto> data = body.getData();
            if (data == null) {
                log.error("[ERROR][DASHBOARD][HRM] 비즈니스 서버 응답에 data 필드가 없음");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "비즈니스 서버 응답 형식이 올바르지 않습니다.");
            }

            log.info("[INFO][DASHBOARD][HRM] 휴가 신청 목록 조회 성공");
            return ResponseEntity.ok(ApiResponse.success(data, "휴가 신청 목록 조회 성공", HttpStatus.OK));
        } catch (WebClientResponseException ex) {
            return handleWebClientError("대시보드 휴가 신청 목록 조회", ex);
        } catch (Exception e) {
            log.error("[ERROR][DASHBOARD][HRM] 휴가 신청 목록 조회 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("대시보드 휴가 신청 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<TrainingProgramSimpleDto>>> getAllTrainingPrograms() {
        log.debug("전체 교육 프로그램 목록 조회 요청");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/trainings/programs")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("전체 교육 프로그램 목록 조회 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("전체 교육 프로그램 목록 조회", ex);
        } catch (Exception e) {
            log.error("전체 교육 프로그램 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("전체 교육 프로그램 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<TrainingCompletionStatusDto>>> getAllTrainingCompletionStatuses() {
        log.debug("교육 완료 상태 목록 조회 요청");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/trainings/completion-statuses")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("교육 완료 상태 목록 조회 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("교육 완료 상태 목록 조회", ex);
        } catch (Exception e) {
            log.error("교육 완료 상태 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("교육 완료 상태 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<EmployeeTrainingHistoryDto>> getEmployeeTrainingHistory(String employeeId) {
        log.debug("직원 교육 이력 조회 요청 - employeeId: {}", employeeId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/trainings/employee/{employeeId}/training-history", employeeId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("직원 교육 이력 조회 성공 - employeeId: {}", employeeId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("직원 교육 이력 조회", ex);
        } catch (Exception e) {
            log.error("직원 교육 이력 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("직원 교육 이력 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<EmployeeTrainingListResponseDto>> getEmployeeTrainingList(
            String department, String position, String name, Integer page, Integer size) {
        log.debug("직원 교육 현황 목록 조회 요청 - department: {}, position: {}, name: {}, page: {}, size: {}",
                department, position, name, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/trainings");
                        if (department != null) builder.queryParam("department", department);
                        if (position != null) builder.queryParam("position", position);
                        if (name != null) builder.queryParam("name", name);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 20);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("직원 교육 현황 목록 조회 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("직원 교육 현황 목록 조회", ex);
        } catch (Exception e) {
            log.error("직원 교육 현황 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("직원 교육 현황 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<TrainingStatusResponseDto>> getTrainingStatusList(
            String department, String position, String name, Integer page, Integer size) {
        log.debug("직원 교육 현황 통계 조회 요청 - department: {}, position: {}, name: {}, page: {}, size: {}",
                department, position, name, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/trainings/training-status");
                        if (department != null) builder.queryParam("department", department);
                        if (position != null) builder.queryParam("position", position);
                        if (name != null) builder.queryParam("name", name);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 20);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("직원 교육 현황 통계 조회 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("직원 교육 현황 통계 조회", ex);
        } catch (Exception e) {
            log.error("직원 교육 현황 통계 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("직원 교육 현황 통계 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<EmployeeTrainingSummaryDto>> getEmployeeTrainingSummary(String employeeId) {
        log.debug("직원별 교육 요약 정보 조회 요청 - employeeId: {}", employeeId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/trainings/training/employee/{employeeId}", employeeId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("직원별 교육 요약 정보 조회 성공 - employeeId: {}", employeeId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("직원별 교육 요약 정보 조회", ex);
        } catch (Exception e) {
            log.error("직원별 교육 요약 정보 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("직원별 교육 요약 정보 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Time Records ====================

    @Override
    public ResponseEntity<ApiResponse<TimeRecordDetailDto>> getTimeRecordDetail(String timerecordId) {
        log.debug("근태 기록 상세 정보 조회 요청 - timerecordId: {}", timerecordId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/time-records/time-record/{timerecordId}", timerecordId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("근태 기록 상세 정보 조회 성공 - timerecordId: {}", timerecordId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("근태 기록 상세 정보 조회", ex);
        } catch (Exception e) {
            log.error("근태 기록 상세 정보 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("근태 기록 상세 정보 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> updateTimeRecord(String timerecordId, TimeRecordUpdateRequestDto requestDto) {
        log.debug("근태 기록 수정 요청 - timerecordId: {}, body: {}", timerecordId, requestDto);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.patch()
                    .uri("/hrm/time-records/time-record/{timerecordId}", timerecordId)
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("근태 기록 수정 성공 - timerecordId: {}", timerecordId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("근태 기록 수정", ex);
        } catch (Exception e) {
            log.error("근태 기록 수정 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("근태 기록 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Page<TimeRecordListItemDto>>> getAttendanceList(
            String department, String position, String name, String date,String statusCode, Integer page, Integer size) {
        log.debug("근태 기록 목록 조회 요청 - department: {}, position: {}, name: {}, date: {}, page: {}, size: {}",
                department, position, name, date, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/time-records/time-record");
                        if (department != null) builder.queryParam("department", department);
                        if (position != null) builder.queryParam("position", position);
                        if (statusCode != null) builder.queryParam("statusCode", statusCode);
                        if (name != null) builder.queryParam("name", name);
                        if (date != null) builder.queryParam("date", date);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 20);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("근태 기록 목록 조회 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("근태 기록 목록 조회", ex);
        } catch (Exception e) {
            log.error("근태 기록 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("근태 기록 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Attendance ====================

    @Override
    public ResponseEntity<ApiResponse<Page<AttendanceListItemDto>>> getAttendanceHistoryList(
            String employeeId, String startDate, String endDate, String status, Integer page, Integer size) {
        log.debug("출퇴근 기록 조회 요청 - employeeId: {}, startDate: {}, endDate: {}, status: {}, page: {}, size: {}",
                employeeId, startDate, endDate, status, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/attendance");
                        if (employeeId != null) builder.queryParam("employeeId", employeeId);
                        if (startDate != null) builder.queryParam("startDate", startDate);
                        if (endDate != null) builder.queryParam("endDate", endDate);
                        if (status != null) builder.queryParam("status", status);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 20);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("출퇴근 기록 조회 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("출퇴근 기록 조회", ex);
        } catch (Exception e) {
            log.error("출퇴근 기록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("출퇴근 기록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> checkIn(String internelUserId) {
        log.debug("출근 처리 요청 - internelUserId: {}", internelUserId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            Map<String, Object> requestBody = Map.of("employeeId", internelUserId);

            ApiResponse<?> response = businessClient.patch()
                    .uri("/hrm/attendance/check-in")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("출근 처리 성공 - internelUserId: {}", internelUserId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("출근 처리", ex);
        } catch (Exception e) {
            log.error("출근 처리 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("출근 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> checkOut(String internelUserId) {
        log.debug("퇴근 처리 요청 - internelUserId: {}", internelUserId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            Map<String, Object> requestBody = Map.of("employeeId", internelUserId);

            ApiResponse<?> response = businessClient.patch()
                    .uri("/hrm/attendance/check-out")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("퇴근 처리 성공 - internelUserId: {}", internelUserId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("퇴근 처리", ex);
        } catch (Exception e) {
            log.error("퇴근 처리 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("퇴근 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<AttendanceRecordDto>>> getAttendanceRecordsByInternelUserId(String internelUserId) {
        log.debug("InternelUser ID로 출퇴근 기록 목록 조회 요청 - internelUserId: {}", internelUserId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/hrm/employees/{internelUserId}/attendance-records", internelUserId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("InternelUser ID로 출퇴근 기록 목록 조회 성공 - internelUserId: {}", internelUserId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("InternelUser ID로 출퇴근 기록 목록 조회", ex);
        } catch (Exception e) {
            log.error("InternelUser ID로 출퇴근 기록 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("InternelUser ID로 출퇴근 기록 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> requestLeave(LeaveRequestDto requestDto, String internelUserId) {
        log.debug("휴가 신청 요청 - requestBody: {}", requestDto);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/hrm/leave/request")  // URL 경로 설정
                            .queryParam("InternelUserId", internelUserId)
                            .build())  // queryParam 오타 수정
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();  // 동기식 호출


            log.info("휴가 신청 성공 - employeeId: {}", internelUserId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("휴가 신청", ex);
        } catch (Exception e) {
            log.error("휴가 신청 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("휴가 신청 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Page<TrainingListItemDto>>> getTrainingPrograms(
            String programName, String status, String category, Integer page, Integer size) {
        log.debug("교육 프로그램 목록 조회 요청 - name: {}, status: {}, category: {}, page: {}, size: {}",
                programName, status, category, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/trainings/program");
                        if (programName != null) builder.queryParam("name", programName);
                        if (status != null) builder.queryParam("status", status);
                        if (category != null) builder.queryParam("category", category);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 10);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("교육 프로그램 목록 조회 성공");
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("교육 프로그램 목록 조회", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("교육 프로그램 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> assignProgramToEmployee(String employeeId, ProgramAssignRequestDto requestDto) {
        log.debug("직원에게 교육 프로그램 할당 요청 - employeeId: {}, body: {}", employeeId, requestDto);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.post()
                    .uri("/hrm/program/{employeeId}", employeeId)
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("직원에게 교육 프로그램 할당 성공 - employeeId: {}", employeeId);
            @SuppressWarnings("unchecked")
            ApiResponse result = (ApiResponse) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("직원 교육 프로그램 할당", ex);
        } catch (Exception e) {
            log.error("직원 교육 프로그램 할당 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("직원 교육 프로그램 할당 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    /**
     * WebClient 오류를 처리하고 로깅하는 공통 메서드
     */
    private <T> ResponseEntity<ApiResponse<T>> handleWebClientError(String operation, WebClientResponseException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String errorBody = ex.getResponseBodyAsString();

        log.error("{} 실패 - Status: {}, Body: {}", operation, ex.getStatusCode(), errorBody);

        return ResponseEntity.status(status).body(
                ApiResponse.fail(operation + " 중 오류가 발생했습니다.", status, null)
        );
    }
}
