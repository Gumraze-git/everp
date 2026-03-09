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
import org.ever._4ever_be_gw.common.exception.handler.ProblemDetailFactory;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.restclient.RestClientProvider;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HrmHttpServiceImpl implements HrmHttpService {

    private final RestClientProvider restClientProvider;

    // ==================== Statistics ====================

    @Override
    public ResponseEntity<?> getHRStatistics() {
        log.debug("HR 통계 조회 요청");

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/statistics")
                    .retrieve()
                    .body(Object.class);

            // ongoingProgramCount와 completedProgramCount 제거
            if (response instanceof java.util.Map<?, ?>) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> data = (java.util.Map<String, Object>) response;
                data.remove("ongoingProgramCount");
                data.remove("completedProgramCount");
            }

            log.info("HR 통계 조회 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("HR 통계 조회", ex);
        } catch (Exception e) {
            log.error("HR 통계 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "HR 통계 조회 중 오류가 발생했습니다.", "HR 통계 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    // ==================== Departments ====================

    @Override
    public ResponseEntity<?> getDepartmentList(String status, Integer page, Integer size) {
        log.debug("부서 목록 조회 요청 - status: {}, page: {}, size: {}", status, page, size);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/departments");
                        if (status != null) builder.queryParam("status", status);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 20);
                        return builder.build();
                    })
                    .retrieve()
                    .body(Object.class);

            log.info("부서 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("부서 목록 조회", ex);
        } catch (Exception e) {
            log.error("부서 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "부서 목록 조회 중 오류가 발생했습니다.", "부서 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> updateDepartment(String departmentId, UpdateDepartmentRequestDto requestDto) {
        log.debug("부서 정보 수정 요청 - departmentId: {}, body: {}", departmentId, requestDto);

        try {
            RestClient businessWebClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessWebClient.patch()
                    .uri("/hrm/departments/{departmentId}", departmentId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(requestDto)
                    .retrieve()
                    .body(Object.class);

            log.info("부서 정보 수정 완료 - departmentId: {}", departmentId);

            return ResponseEntity.ok(response);

        } catch (org.springframework.web.client.RestClientResponseException ex) {
            return handleWebClientError("부서 정보 수정", ex);
        } catch (Exception e) {
            log.error("부서 정보 수정 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "부서 정보 수정 중 오류가 발생했습니다.", "부서 정보 수정 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getDepartmentDetail(String departmentId) {
        log.debug("부서 상세 조회 요청 - departmentId: {}", departmentId);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/organization/department/{departmentId}", departmentId)
                    .retrieve()
                    .body(Object.class);

            log.info("부서 상세 조회 성공 - departmentId: {}", departmentId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("부서 상세 조회", ex);
        } catch (Exception e) {
            log.error("부서 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "부서 상세 조회 중 오류가 발생했습니다.", "부서 상세 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getAllDepartmentsSimple() {
        log.debug("전체 부서 목록 조회 요청 (ID, Name만)");

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/departments/simple")
                    .retrieve()
                    .body(Object.class);

            log.info("전체 부서 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("전체 부서 목록 조회", ex);
        } catch (Exception e) {
            log.error("전체 부서 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "전체 부서 목록 조회 중 오류가 발생했습니다.", "전체 부서 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getDepartmentMembers(String departmentId) {
        log.debug("부서 구성원 목록 조회 요청 - departmentId: {}", departmentId);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/departments/{departmentId}/members", departmentId)
                    .retrieve()
                    .body(Object.class);

            log.info("부서 구성원 목록 조회 성공 - departmentId: {}", departmentId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("부서 구성원 목록 조회", ex);
        } catch (Exception e) {
            log.error("부서 구성원 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "부서 구성원 목록 조회 중 오류가 발생했습니다.", "부서 구성원 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    // ==================== Positions ====================

    @Override
    public ResponseEntity<?> getPositionList() {
        log.debug("직급 목록 조회 요청");

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/positions")
                    .retrieve()
                    .body(Object.class);

            log.info("직급 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("직급 목록 조회", ex);
        } catch (Exception e) {
            log.error("직급 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "직급 목록 조회 중 오류가 발생했습니다.", "직급 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getPositionsByDepartmentId(String departmentId) {
        log.debug("부서별 직급 목록 조회 요청 - departmentId: {}", departmentId);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/" + departmentId + "/positions/all")
                    .retrieve()
                    .body(Object.class);

            log.info("부서별 직급 목록 조회 성공 - departmentId: {}", departmentId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("부서별 직급 목록 조회", ex);
        } catch (Exception e) {
            log.error("부서별 직급 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "부서별 직급 목록 조회 중 오류가 발생했습니다.", "부서별 직급 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getPositionDetail(String positionId) {
        log.debug("직급 상세 조회 요청 - positionId: {}", positionId);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/organization/position/{positionId}", positionId)
                    .retrieve()
                    .body(Object.class);

            log.info("직급 상세 조회 성공 - positionId: {}", positionId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("직급 상세 조회", ex);
        } catch (Exception e) {
            log.error("직급 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "직급 상세 조회 중 오류가 발생했습니다.", "직급 상세 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    // ==================== Employees ====================

    @Override
    public ResponseEntity<?> getEmployeeList(
            String departmentId, String positionId, String name, Integer page, Integer size) {
        log.debug("직원 목록 조회 요청 - departmentId: {}, positionId: {}, name: {}, page: {}, size: {}",
                departmentId, positionId, name, page, size);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
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
                    .body(Object.class);

            log.info("직원 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("직원 목록 조회", ex);
        } catch (Exception e) {
            log.error("직원 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "직원 목록 조회 중 오류가 발생했습니다.", "직원 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getEmployeeDetail(String employeeId) {
        log.debug("직원 상세 조회 요청 - employeeId: {}", employeeId);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/employee/{employeeId}", employeeId)
                    .retrieve()
                    .body(Object.class);

            log.info("직원 상세 조회 성공 - employeeId: {}", employeeId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("직원 상세 조회", ex);
        } catch (Exception e) {
            log.error("직원 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "직원 상세 조회 중 오류가 발생했습니다.", "직원 상세 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getEmployeeWithTrainingByInternelUserId(String internelUserId) {
        log.debug("InternelUser ID로 직원 정보 및 교육 이력 조회 요청 - internelUserId: {}", internelUserId);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/employees/{internelUserId}", internelUserId)
                    .retrieve()
                    .body(Object.class);

            log.info("InternelUser ID로 직원 정보 및 교육 이력 조회 성공 - internelUserId: {}", internelUserId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("InternelUser ID로 직원 정보 및 교육 이력 조회", ex);
        } catch (Exception e) {
            log.error("InternelUser ID로 직원 정보 및 교육 이력 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "InternelUser ID로 직원 정보 및 교육 이력 조회 중 오류가 발생했습니다.", "InternelUser ID로 직원 정보 및 교육 이력 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getAvailableTrainingsByInternelUserId(String internelUserId) {
        log.debug("InternelUser ID로 수강 가능한 교육 프로그램 목록 조회 요청 - internelUserId: {}", internelUserId);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/employees/{internelUserId}/available-trainings", internelUserId)
                    .retrieve()
                    .body(Object.class);

            log.info("InternelUser ID로 수강 가능한 교육 프로그램 목록 조회 성공 - internelUserId: {}", internelUserId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("InternelUser ID로 수강 가능한 교육 프로그램 목록 조회", ex);
        } catch (Exception e) {
            log.error("InternelUser ID로 수강 가능한 교육 프로그램 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "InternelUser ID로 수강 가능한 교육 프로그램 목록 조회 중 오류가 발생했습니다.", "InternelUser ID로 수강 가능한 교육 프로그램 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getCustomerUserDetailByUserId(String customerUserId) {
        log.debug("CustomerUser ID로 고객 사용자 상세 정보 조회 요청 - customerUserId: {}", customerUserId);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/customers/by-customer-user/{customerUserId}", customerUserId)
                    .retrieve()
                    .body(Object.class);

            log.info("CustomerUser ID로 고객 사용자 상세 정보 조회 성공 - customerUserId: {}", customerUserId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("CustomerUser ID로 고객 사용자 상세 정보 조회", ex);
        } catch (Exception e) {
            log.error("CustomerUser ID로 고객 사용자 상세 정보 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "CustomerUser ID로 고객 사용자 상세 정보 조회 중 오류가 발생했습니다.", "CustomerUser ID로 고객 사용자 상세 정보 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> updateEmployee(String employeeId, EmployeeUpdateRequestDto requestDto) {
        log.debug("직원 정보 수정 요청 - employeeId: {}, body: {}", employeeId, requestDto);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.patch()
                    .uri("/hrm/employee/{employeeId}", employeeId)
                    .body(requestDto)
                    .retrieve()
                    .body(Object.class);

            log.info("직원 정보 수정 성공 - employeeId: {}", employeeId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("직원 정보 수정", ex);
        } catch (Exception e) {
            log.error("직원 정보 수정 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "직원 정보 수정 중 오류가 발생했습니다.", "직원 정보 수정 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> requestTraining(TrainingRequestDto requestDto) {
        log.debug("교육 프로그램 신청 요청 - body: {}", requestDto);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.post()
                    .uri("/hrm/employee/request")
                    .body(requestDto)
                    .retrieve()
                    .body(Object.class);

            log.info("교육 프로그램 신청 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("교육 프로그램 신청", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 신청 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "교육 프로그램 신청 중 오류가 발생했습니다.", "교육 프로그램 신청 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> enrollTrainingProgram(String employeeId, ProgramAssignRequestDto requestDto) {
        log.debug("교육 프로그램 등록 요청 - employeeId: {}, body: {}", employeeId, requestDto);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.post()
                    .uri("/hrm/program/{employeeId}", employeeId)
                    .body(requestDto)
                    .retrieve()
                    .body(Object.class);

            log.info("교육 프로그램 등록 성공 - employeeId: {}", employeeId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("교육 프로그램 등록", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 등록 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "교육 프로그램 등록 중 오류가 발생했습니다.", "교육 프로그램 등록 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    // ==================== Leave Requests ====================

    @Override
    public ResponseEntity<?> getLeaveRequestList(
            String department, String position, String name, String type, String sortOrder, Integer page, Integer size) {
        log.debug("휴가 신청 목록 조회 요청 - department: {}, position: {}, name: {}, type: {}, sortOrder: {}, page: {}, size: {}",
                department, position, name, type, sortOrder, page, size);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
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
                    .body(Object.class);

            log.info("휴가 신청 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("휴가 신청 목록 조회", ex);
        } catch (Exception e) {
            log.error("휴가 신청 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "휴가 신청 목록 조회 중 오류가 발생했습니다.", "휴가 신청 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> createLeaveRequest(LeaveRequestDto requestDto) {
        log.debug("휴가 신청 요청 - body: {}", requestDto);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.post()
                    .uri("/hrm/leave/request")
                    .body(requestDto)
                    .retrieve()
                    .body(Object.class);

            log.info("휴가 신청 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("휴가 신청", ex);
        } catch (Exception e) {
            log.error("휴가 신청 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "휴가 신청 중 오류가 발생했습니다.", "휴가 신청 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> approveLeaveRequest(String requestId) {
        log.debug("휴가 신청 승인 요청 - requestId: {}", requestId);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.patch()
                    .uri("/hrm/leave/request/{requestId}/release", requestId)
                    .retrieve()
                    .body(Object.class);

            log.info("휴가 신청 승인 성공 - requestId: {}", requestId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("휴가 신청 승인", ex);
        } catch (Exception e) {
            log.error("휴가 신청 승인 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "휴가 신청 승인 중 오류가 발생했습니다.", "휴가 신청 승인 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> rejectLeaveRequest(String requestId) {
        log.debug("휴가 신청 반려 요청 - requestId: {}", requestId);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.patch()
                    .uri("/hrm/leave/request/{requestId}/reject", requestId)
                    .retrieve()
                    .body(Object.class);

            log.info("휴가 신청 반려 성공 - requestId: {}", requestId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("휴가 신청 반려", ex);
        } catch (Exception e) {
            log.error("휴가 신청 반려 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "휴가 신청 반려 중 오류가 발생했습니다.", "휴가 신청 반려 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    // ==================== Payroll ====================

    @Override
    public ResponseEntity<?> getPaystubDetail(String payrollId) {
        log.debug("급여 명세서 상세 조회 요청 - payrollId: {}", payrollId);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/payroll/{payrollId}", payrollId)
                    .retrieve()
                    .body(Object.class);

            log.info("급여 명세서 상세 조회 성공 - payrollId: {}", payrollId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("급여 명세서 상세 조회", ex);
        } catch (Exception e) {
            log.error("급여 명세서 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "급여 명세서 상세 조회 중 오류가 발생했습니다.", "급여 명세서 상세 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getPayrollList(
            Integer year, Integer month, String name, String department, String position,String statusCode, Integer page, Integer size) {
        log.debug("급여 명세서 목록 조회 요청 - year: {}, month: {}, name: {}, department: {}, position: {}, page: {}, size: {}",
                year, month, name, department, position, page, size);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
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
                    .body(Object.class);

            log.info("급여 명세서 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("급여 명세서 목록 조회", ex);
        } catch (Exception e) {
            log.error("급여 명세서 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "급여 명세서 목록 조회 중 오류가 발생했습니다.", "급여 명세서 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> completePayroll(PayrollCompleteRequestDto requestDto) {
        log.debug("급여 지급 완료 처리 요청 - body: {}", requestDto);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.post()
                    .uri("/hrm/payroll/complete")
                    .body(requestDto)
                    .retrieve()
                    .body(Object.class);

            log.info("급여 지급 완료 처리 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("급여 지급 완료 처리", ex);
        } catch (Exception e) {
            log.error("급여 지급 완료 처리 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "급여 지급 완료 처리 중 오류가 발생했습니다.", "급여 지급 완료 처리 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> generateMonthlyPayroll() {
        log.debug("모든 직원 당월 급여 생성 요청");

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/payroll/generate")
                    .retrieve()
                    .body(Object.class);

            log.info("모든 직원 당월 급여 생성 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("모든 직원 당월 급여 생성", ex);
        } catch (Exception e) {
            log.error("모든 직원 당월 급여 생성 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "모든 직원 당월 급여 생성 중 오류가 발생했습니다.", "모든 직원 당월 급여 생성 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getAllPayrollStatuses() {
        log.debug("급여 상태 목록 조회 요청");

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/payroll/statuses")
                    .retrieve()
                    .body(Object.class);

            log.info("급여 상태 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("급여 상태 목록 조회", ex);
        } catch (Exception e) {
            log.error("급여 상태 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "급여 상태 목록 조회 중 오류가 발생했습니다.", "급여 상태 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    // ==================== Attendance ====================

    @Override
    public ResponseEntity<?> getAllAttendanceStatuses() {
        log.debug("출퇴근 상태 목록 조회 요청");

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/attendance/statuses")
                    .retrieve()
                    .body(Object.class);

            log.info("출퇴근 상태 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("출퇴근 상태 목록 조회", ex);
        } catch (Exception e) {
            log.error("출퇴근 상태 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "출퇴근 상태 목록 조회 중 오류가 발생했습니다.", "출퇴근 상태 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    // ==================== Training ====================

    @Override
    public ResponseEntity<?> getProgramDetailInfo(String programId) {
        log.debug("교육 프로그램 상세 정보 조회 요청 - programId: {}", programId);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/trainings/program/{programId}", programId)
                    .retrieve()
                    .body(Object.class);

            log.info("교육 프로그램 상세 정보 조회 성공 - programId: {}", programId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("교육 프로그램 상세 정보 조회", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 상세 정보 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "교육 프로그램 상세 정보 조회 중 오류가 발생했습니다.", "교육 프로그램 상세 정보 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getTrainingList(
            String name, String status, String category, Integer page, Integer size) {
        log.debug("교육 프로그램 목록 조회 요청 - name: {}, status: {}, category: {}, page: {}, size: {}",
                name, status, category, page, size);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
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
                    .body(Object.class);

            log.info("교육 프로그램 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("교육 프로그램 목록 조회", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "교육 프로그램 목록 조회 중 오류가 발생했습니다.", "교육 프로그램 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> createTrainingProgram(ProgramCreateRequestDto requestDto) {
        log.debug("교육 프로그램 생성 요청 - body: {}", requestDto);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.post()
                    .uri("/hrm/trainings/program")
                    .body(requestDto)
                    .retrieve()
                    .body(Object.class);

            log.info("교육 프로그램 생성 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("교육 프로그램 생성", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 생성 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "교육 프로그램 생성 중 오류가 발생했습니다.", "교육 프로그램 생성 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> updateTrainingProgram(String programId, ProgramModifyRequestDto requestDto) {
        log.debug("교육 프로그램 수정 요청 - programId: {}, body: {}", programId, requestDto);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.patch()
                    .uri("/hrm/program/{programId}", programId)
                    .body(requestDto)
                    .retrieve()
                    .body(Object.class);

            log.info("교육 프로그램 수정 성공 - programId: {}", programId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("교육 프로그램 수정", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 수정 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "교육 프로그램 수정 중 오류가 발생했습니다.", "교육 프로그램 수정 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getAllTrainingCategories() {
        log.debug("교육 카테고리 목록 조회 요청");

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/trainings/categories")
                    .retrieve()
                    .body(Object.class);

            log.info("교육 카테고리 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("교육 카테고리 목록 조회", ex);
        } catch (Exception e) {
            log.error("교육 카테고리 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "교육 카테고리 목록 조회 중 오류가 발생했습니다.", "교육 카테고리 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getDashboardAttendanceList(String userId, Integer size) {
        log.debug("[DASHBOARD][HRM] 근태 목록 요청 - userId: {}, size: {}", userId, size);

        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(
                    ProblemDetailFactory.badRequest("hrm userId is required", "hrm userId is required", null, null, ErrorCode.INVALID_INPUT_VALUE.getCode())
            );
        }

        final int pageSize = (size != null && size > 0) ? size : 5;

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object body = businessClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/hrm/dashboard/attendance")
                            .queryParam("userId", userId)
                            .queryParam("size", pageSize)
                            .build())
                    .retrieve()
                    .body(Object.class);

            if (body == null) {
                log.error("[ERROR][DASHBOARD][HRM] 비즈니스 서버 응답이 null");
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "비즈니스 서버 응답이 비어 있습니다.");
            }

            if (!(body instanceof List<?> data) || data == null) {
                log.error("[ERROR][DASHBOARD][HRM] 비즈니스 서버 응답 형식이 올바르지 않음");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "비즈니스 서버 응답 형식이 올바르지 않습니다.");
            }

            log.info("[INFO][DASHBOARD][HRM] 근태 목록 조회 성공");
            return ResponseEntity.ok(body);
        } catch (RestClientResponseException ex) {
            return handleWebClientError("대시보드 근태 목록 조회", ex);
        } catch (Exception e) {
            log.error("[ERROR][DASHBOARD][HRM] 근태 목록 조회 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "대시보드 근태 목록 조회 중 오류가 발생했습니다.", "대시보드 근태 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getDashboardLeaveRequestList(String userId, Integer size) {
        log.debug("[DASHBOARD][HRM] 휴가 신청 목록 요청 - userId: {}, size: {}", userId, size);

        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(
                    ProblemDetailFactory.badRequest("hrm userId is required", "hrm userId is required", null, null, ErrorCode.INVALID_INPUT_VALUE.getCode())
            );
        }

        final int pageSize = (size != null && size > 0) ? size : 5;

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object body = businessClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/hrm/dashboard/leave-requests")
                            .queryParam("userId", userId)
                            .queryParam("size", pageSize)
                            .build())
                    .retrieve()
                    .body(Object.class);

            if (body == null) {
                log.error("[ERROR][DASHBOARD][HRM] 비즈니스 서버 응답이 null");
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "비즈니스 서버 응답이 비어 있습니다.");
            }

            if (!(body instanceof List<?> data) || data == null) {
                log.error("[ERROR][DASHBOARD][HRM] 비즈니스 서버 응답 형식이 올바르지 않음");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "비즈니스 서버 응답 형식이 올바르지 않습니다.");
            }

            log.info("[INFO][DASHBOARD][HRM] 휴가 신청 목록 조회 성공");
            return ResponseEntity.ok(body);
        } catch (RestClientResponseException ex) {
            return handleWebClientError("대시보드 휴가 신청 목록 조회", ex);
        } catch (Exception e) {
            log.error("[ERROR][DASHBOARD][HRM] 휴가 신청 목록 조회 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "대시보드 휴가 신청 목록 조회 중 오류가 발생했습니다.", "대시보드 휴가 신청 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getAllTrainingPrograms() {
        log.debug("전체 교육 프로그램 목록 조회 요청");

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/trainings/programs")
                    .retrieve()
                    .body(Object.class);

            log.info("전체 교육 프로그램 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("전체 교육 프로그램 목록 조회", ex);
        } catch (Exception e) {
            log.error("전체 교육 프로그램 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "전체 교육 프로그램 목록 조회 중 오류가 발생했습니다.", "전체 교육 프로그램 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getAllTrainingCompletionStatuses() {
        log.debug("교육 완료 상태 목록 조회 요청");

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/trainings/completion-statuses")
                    .retrieve()
                    .body(Object.class);

            log.info("교육 완료 상태 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("교육 완료 상태 목록 조회", ex);
        } catch (Exception e) {
            log.error("교육 완료 상태 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "교육 완료 상태 목록 조회 중 오류가 발생했습니다.", "교육 완료 상태 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getEmployeeTrainingHistory(String employeeId) {
        log.debug("직원 교육 이력 조회 요청 - employeeId: {}", employeeId);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/trainings/employee/{employeeId}/training-history", employeeId)
                    .retrieve()
                    .body(Object.class);

            log.info("직원 교육 이력 조회 성공 - employeeId: {}", employeeId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("직원 교육 이력 조회", ex);
        } catch (Exception e) {
            log.error("직원 교육 이력 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "직원 교육 이력 조회 중 오류가 발생했습니다.", "직원 교육 이력 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getEmployeeTrainingList(
            String department, String position, String name, Integer page, Integer size) {
        log.debug("직원 교육 현황 목록 조회 요청 - department: {}, position: {}, name: {}, page: {}, size: {}",
                department, position, name, page, size);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
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
                    .body(Object.class);

            log.info("직원 교육 현황 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("직원 교육 현황 목록 조회", ex);
        } catch (Exception e) {
            log.error("직원 교육 현황 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "직원 교육 현황 목록 조회 중 오류가 발생했습니다.", "직원 교육 현황 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getTrainingStatusList(
            String department, String position, String name, Integer page, Integer size) {
        log.debug("직원 교육 현황 통계 조회 요청 - department: {}, position: {}, name: {}, page: {}, size: {}",
                department, position, name, page, size);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
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
                    .body(Object.class);

            log.info("직원 교육 현황 통계 조회 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("직원 교육 현황 통계 조회", ex);
        } catch (Exception e) {
            log.error("직원 교육 현황 통계 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "직원 교육 현황 통계 조회 중 오류가 발생했습니다.", "직원 교육 현황 통계 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getEmployeeTrainingSummary(String employeeId) {
        log.debug("직원별 교육 요약 정보 조회 요청 - employeeId: {}", employeeId);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/trainings/training/employee/{employeeId}", employeeId)
                    .retrieve()
                    .body(Object.class);

            log.info("직원별 교육 요약 정보 조회 성공 - employeeId: {}", employeeId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("직원별 교육 요약 정보 조회", ex);
        } catch (Exception e) {
            log.error("직원별 교육 요약 정보 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "직원별 교육 요약 정보 조회 중 오류가 발생했습니다.", "직원별 교육 요약 정보 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    // ==================== Time Records ====================

    @Override
    public ResponseEntity<?> getTimeRecordDetail(String timerecordId) {
        log.debug("근태 기록 상세 정보 조회 요청 - timerecordId: {}", timerecordId);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/time-records/time-record/{timerecordId}", timerecordId)
                    .retrieve()
                    .body(Object.class);

            log.info("근태 기록 상세 정보 조회 성공 - timerecordId: {}", timerecordId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("근태 기록 상세 정보 조회", ex);
        } catch (Exception e) {
            log.error("근태 기록 상세 정보 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "근태 기록 상세 정보 조회 중 오류가 발생했습니다.", "근태 기록 상세 정보 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> updateTimeRecord(String timerecordId, TimeRecordUpdateRequestDto requestDto) {
        log.debug("근태 기록 수정 요청 - timerecordId: {}, body: {}", timerecordId, requestDto);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.patch()
                    .uri("/hrm/time-records/time-record/{timerecordId}", timerecordId)
                    .body(requestDto)
                    .retrieve()
                    .body(Object.class);

            log.info("근태 기록 수정 성공 - timerecordId: {}", timerecordId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("근태 기록 수정", ex);
        } catch (Exception e) {
            log.error("근태 기록 수정 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "근태 기록 수정 중 오류가 발생했습니다.", "근태 기록 수정 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getAttendanceList(
            String department, String position, String name, String date,String statusCode, Integer page, Integer size) {
        log.debug("근태 기록 목록 조회 요청 - department: {}, position: {}, name: {}, date: {}, page: {}, size: {}",
                department, position, name, date, page, size);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
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
                    .body(Object.class);

            log.info("근태 기록 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("근태 기록 목록 조회", ex);
        } catch (Exception e) {
            log.error("근태 기록 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "근태 기록 목록 조회 중 오류가 발생했습니다.", "근태 기록 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    // ==================== Attendance ====================

    @Override
    public ResponseEntity<?> getAttendanceHistoryList(
            String employeeId, String startDate, String endDate, String status, Integer page, Integer size) {
        log.debug("출퇴근 기록 조회 요청 - employeeId: {}, startDate: {}, endDate: {}, status: {}, page: {}, size: {}",
                employeeId, startDate, endDate, status, page, size);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
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
                    .body(Object.class);

            log.info("출퇴근 기록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("출퇴근 기록 조회", ex);
        } catch (Exception e) {
            log.error("출퇴근 기록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "출퇴근 기록 조회 중 오류가 발생했습니다.", "출퇴근 기록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> checkIn(String internelUserId) {
        log.debug("출근 처리 요청 - internelUserId: {}", internelUserId);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Map<String, Object> requestBody = Map.of("employeeId", internelUserId);

            Object response = businessClient.patch()
                    .uri("/hrm/attendance/check-in")
                    .body(requestBody)
                    .retrieve()
                    .body(Object.class);

            log.info("출근 처리 성공 - internelUserId: {}", internelUserId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("출근 처리", ex);
        } catch (Exception e) {
            log.error("출근 처리 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "출근 처리 중 오류가 발생했습니다.", "출근 처리 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> checkOut(String internelUserId) {
        log.debug("퇴근 처리 요청 - internelUserId: {}", internelUserId);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Map<String, Object> requestBody = Map.of("employeeId", internelUserId);

            Object response = businessClient.patch()
                    .uri("/hrm/attendance/check-out")
                    .body(requestBody)
                    .retrieve()
                    .body(Object.class);

            log.info("퇴근 처리 성공 - internelUserId: {}", internelUserId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("퇴근 처리", ex);
        } catch (Exception e) {
            log.error("퇴근 처리 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "퇴근 처리 중 오류가 발생했습니다.", "퇴근 처리 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getAttendanceRecordsByInternelUserId(String internelUserId) {
        log.debug("InternelUser ID로 출퇴근 기록 목록 조회 요청 - internelUserId: {}", internelUserId);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
                    .uri("/hrm/employees/{internelUserId}/attendance-records", internelUserId)
                    .retrieve()
                    .body(Object.class);

            log.info("InternelUser ID로 출퇴근 기록 목록 조회 성공 - internelUserId: {}", internelUserId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("InternelUser ID로 출퇴근 기록 목록 조회", ex);
        } catch (Exception e) {
            log.error("InternelUser ID로 출퇴근 기록 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "InternelUser ID로 출퇴근 기록 목록 조회 중 오류가 발생했습니다.", "InternelUser ID로 출퇴근 기록 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> requestLeave(LeaveRequestDto requestDto, String internelUserId) {
        log.debug("휴가 신청 요청 - requestBody: {}", requestDto);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/hrm/leave/request")  // URL 경로 설정
                            .queryParam("InternelUserId", internelUserId)
                            .build())  // queryParam 오타 수정
                    .body(requestDto)
                    .retrieve()
                    .body(Object.class);  // 동기식 호출


            log.info("휴가 신청 성공 - employeeId: {}", internelUserId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("휴가 신청", ex);
        } catch (Exception e) {
            log.error("휴가 신청 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "휴가 신청 중 오류가 발생했습니다.", "휴가 신청 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> getTrainingPrograms(
            String programName, String status, String category, Integer page, Integer size) {
        log.debug("교육 프로그램 목록 조회 요청 - name: {}, status: {}, category: {}, page: {}, size: {}",
                programName, status, category, page, size);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.get()
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
                    .body(Object.class);

            log.info("교육 프로그램 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("교육 프로그램 목록 조회", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "교육 프로그램 목록 조회 중 오류가 발생했습니다.", "교육 프로그램 목록 조회 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    @Override
    public ResponseEntity<?> assignProgramToEmployee(String employeeId, ProgramAssignRequestDto requestDto) {
        log.debug("직원에게 교육 프로그램 할당 요청 - employeeId: {}, body: {}", employeeId, requestDto);

        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            Object response = businessClient.post()
                    .uri("/hrm/program/{employeeId}", employeeId)
                    .body(requestDto)
                    .retrieve()
                    .body(Object.class);

            log.info("직원에게 교육 프로그램 할당 성공 - employeeId: {}", employeeId);
            return ResponseEntity.ok(response);

        } catch (RestClientResponseException ex) {
            return handleWebClientError("직원 교육 프로그램 할당", ex);
        } catch (Exception e) {
            log.error("직원 교육 프로그램 할당 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProblemDetailFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "직원 교육 프로그램 할당 중 오류가 발생했습니다.", "직원 교육 프로그램 할당 중 오류가 발생했습니다.", null, null, ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            );
        }
    }

    /**
     * RestClient 오류를 처리하고 로깅하는 공통 메서드
     */
    private ResponseEntity<?> handleWebClientError(String operation, RestClientResponseException ex) {
        log.error("{} 실패 - Status: {}, Body: {}", operation, ex.getStatusCode(), ex.getResponseBodyAsString());
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return ResponseEntity.status(status)
            .body(ProblemDetailFactory.fromRestClientResponseException(ex, "business"));
    }
}
