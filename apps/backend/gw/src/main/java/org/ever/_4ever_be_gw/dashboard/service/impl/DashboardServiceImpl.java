package org.ever._4ever_be_gw.dashboard.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.service.SdHttpService;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.dashboard.service.DashboardService;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowResponseDto;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowTabDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final SdHttpService sdHttpService;          // 영업관리
    private final WebClientProvider webClientProvider;

    private static final int DEFAULT_SIZE = 5;
    private static final ParameterizedTypeReference<List<DashboardWorkflowItemDto>> WORKFLOW_ITEM_LIST_TYPE =
            new ParameterizedTypeReference<>() {};

    @Override
    public DashboardWorkflowResponseDto getDashboardWorkflow(
            EverUserPrincipal principal,
            Integer size
    ) {
        if (principal == null) throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);

        // 사용자 정보 추출
        final String userId = principal.getUserId();
        final String userRole = principal.getUserRole();
        final int limit = Optional.ofNullable(size).orElse(DEFAULT_SIZE);

        log.info("[INFO][DASHBOARD] 워크플로우 구성 시작 - userId: {}, role: {}, limit: {}", userId, userRole, limit);

        // 탭코드는 DashboardWorkflowTabDto 참고
        switch (userRole.split("_")[0]) {
            case "SUPPLIER": {
                log.info("[INFO][DASHBOARD][SUPPLIER] 공급사 워크플로우 데이터 조회");
                ResponseEntity<List<DashboardWorkflowItemDto>> supplierPurchaseOrderResponse =
                        fetchWorkflowItemsWithUserIdPath(
                                ApiClientKey.SCM_PP,
                                "공급사 발주서 워크플로우 조회",
                                "/scm-pp/mm/supplier-users/{userId}/workflow-items/purchase-orders",
                                userId,
                                limit
                        );
                ResponseEntity<List<DashboardWorkflowItemDto>> supplierInvoiceResponse =
                        fetchWorkflowItemsWithUserIdPath(
                                ApiClientKey.BUSINESS,
                                "공급사 매출 전표 워크플로우 조회",
                                "/fcm/supplier-users/{userId}/workflow-items/sales-invoices",
                                userId,
                                limit
                        );

                return DashboardWorkflowResponseDto.builder()
                        .tabs(List.of(
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("PO")
                                        .items(safeItems(supplierPurchaseOrderResponse))
                                        .build(),
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("AR")
                                        .items(safeItems(supplierInvoiceResponse))
                                        .build()
                        ))
                        .build();
            }

            case "CUSTOMER": {
                log.info("[INFO][DASHBOARD][CUSTOMER] 고객사 워크플로우 데이터 조회");
                ResponseEntity<List<DashboardWorkflowItemDto>> quotationResponse =
                        sdHttpService.getDashboardCustomerQuotationList(userId, limit);
                ResponseEntity<List<DashboardWorkflowItemDto>> customerInvoiceResponse =
                        fetchWorkflowItemsWithUserIdPath(
                                ApiClientKey.BUSINESS,
                                "고객사 매입 전표 워크플로우 조회",
                                "/fcm/customer-users/{userId}/workflow-items/purchase-invoices",
                                userId,
                                limit
                        );

                return DashboardWorkflowResponseDto.builder()
                        .tabs(List.of(
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("QT")
                                        .items(safeItems(quotationResponse))
                                        .build(),
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("AP")
                                        .items(safeItems(customerInvoiceResponse))
                                        .build()
                        ))
                        .build();
            }

            case "MM": {
                log.info("[INFO][DASHBOARD][MM] 구매 관리 워크플로우 데이터 조회 시작 - userId: {}", userId);
                ResponseEntity<List<DashboardWorkflowItemDto>> mmPurchaseOrderResponse =
                        fetchWorkflowItemsWithoutUserId(
                                ApiClientKey.SCM_PP,
                                "전체 발주서 워크플로우 조회",
                                "/scm-pp/mm/workflow-items/purchase-orders",
                                limit
                        );
                ResponseEntity<List<DashboardWorkflowItemDto>> mmPurchaseRequestResponse =
                        fetchWorkflowItemsWithoutUserId(
                                ApiClientKey.SCM_PP,
                                "전체 구매 요청 워크플로우 조회",
                                "/scm-pp/mm/workflow-items/purchase-requests",
                                limit
                        );

                return DashboardWorkflowResponseDto.builder()
                        .tabs(List.of(
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("PO")
                                        .items(safeItems(mmPurchaseRequestResponse))
                                        .build(),
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("SO")
                                        .items(safeItems(mmPurchaseOrderResponse))
                                        .build()
                        ))
                        .build();
            }

            case "SD": {
                log.info("[INFO][DASHBOARD][SD] 영업 관리 워크플로우 데이터 조회");
                // 영업 관리 부서의 대시보드 워크 플로우
                // [비즈니스] 전체 견적서 목록 조회(QT)
                ResponseEntity<List<DashboardWorkflowItemDto>> sdCustomerQuotationResponse =
                        sdHttpService.getDashboardInternalQuotationList(limit);
                // [비즈니스] 전체 주문서 목록 조회(SO)
                ResponseEntity<List<DashboardWorkflowItemDto>> sdOrderListResponse =
                        sdHttpService.getDashboardInternalOrderList(limit);

                return DashboardWorkflowResponseDto.builder()
                        .tabs(List.of(
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("QT")
                                        .items(safeItems(sdCustomerQuotationResponse))
                                        .build(),
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("SO")
                                        .items(safeItems(sdOrderListResponse))
                                        .build()
                        ))
                        .build();
            }

            case "FCM": {
                log.info("[INFO][DASHBOARD][FCM] 재무 관리 워크플로우 데이터 조회");
                ResponseEntity<List<DashboardWorkflowItemDto>> fcmArListResponse =
                        fetchWorkflowItemsWithoutUserId(
                                ApiClientKey.BUSINESS,
                                "기업 매출 전표 워크플로우 조회",
                                "/fcm/workflow-items/sales-invoices",
                                limit
                        );
                ResponseEntity<List<DashboardWorkflowItemDto>> fcmApListResponse =
                        fetchWorkflowItemsWithoutUserId(
                                ApiClientKey.BUSINESS,
                                "기업 매입 전표 워크플로우 조회",
                                "/fcm/workflow-items/purchase-invoices",
                                limit
                        );

                return buildFcmWorkflowResponse(fcmArListResponse, fcmApListResponse);
            }

            case "IM": {
                log.info("[INFO][DASHBOARD][IM] 재고 관리 워크플로우 데이터 조회");
                ResponseEntity<List<DashboardWorkflowItemDto>> imInboundListResponse =
                        fetchWorkflowItemsWithoutUserId(
                                ApiClientKey.SCM_PP,
                                "입고 워크플로우 조회",
                                "/scm-pp/iv/workflow-items/inbound",
                                limit
                        );
                ResponseEntity<List<DashboardWorkflowItemDto>> imOutboundListResponse =
                        fetchWorkflowItemsWithoutUserId(
                                ApiClientKey.SCM_PP,
                                "출고 워크플로우 조회",
                                "/scm-pp/iv/workflow-items/outbound",
                                limit
                        );

                return DashboardWorkflowResponseDto.builder()
                        .tabs(List.of(
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("IN")
                                        .items(safeItems(imInboundListResponse))
                                        .build(),
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("OUT")
                                        .items(safeItems(imOutboundListResponse))
                                        .build()
                        ))
                        .build();
            }

            case "HRM": {
                log.info("[INFO][DASHBOARD][HRM] 인사 워크플로우 데이터 조회");
                ResponseEntity<List<DashboardWorkflowItemDto>> hrmAttendanceListResponse =
                        fetchWorkflowItemsWithUserIdQuery(
                                ApiClientKey.BUSINESS,
                                "근태 워크플로우 조회",
                                "/hrm/workflow-items/attendance",
                                userId,
                                limit
                        );
                ResponseEntity<List<DashboardWorkflowItemDto>> hrmLeaveRequestListResponse =
                        fetchWorkflowItemsWithUserIdQuery(
                                ApiClientKey.BUSINESS,
                                "휴가 신청 워크플로우 조회",
                                "/hrm/workflow-items/leave-requests",
                                userId,
                                limit
                        );

                return DashboardWorkflowResponseDto.builder()
                        .tabs(List.of(
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("ATT")
                                        .items(safeItems(hrmAttendanceListResponse))
                                        .build(),
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("LV")
                                        .items(safeItems(hrmLeaveRequestListResponse))
                                        .build()
                        ))
                        .build();
            }

            case "PP": {
                log.info("[INFO][DASHBOARD][PP] 생산 관리 워크플로우 데이터 조회");
                ResponseEntity<List<DashboardWorkflowItemDto>> ppToProductionQuotationResponse =
                        fetchWorkflowItemsWithUserIdQuery(
                                ApiClientKey.SCM_PP,
                                "생산 전환 견적 워크플로우 조회",
                                "/scm-pp/pp/workflow-items/quotations",
                                userId,
                                limit
                        );
                ResponseEntity<List<DashboardWorkflowItemDto>> ppInProgressResponse =
                        fetchWorkflowItemsWithUserIdQuery(
                                ApiClientKey.SCM_PP,
                                "생산 진행 워크플로우 조회",
                                "/scm-pp/pp/workflow-items/mes",
                                userId,
                                limit
                        );

                return DashboardWorkflowResponseDto.builder()
                        .tabs(List.of(
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("QT")
                                        .items(safeItems(ppToProductionQuotationResponse))
                                        .build(),
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("MES")
                                        .items(safeItems(ppInProgressResponse))
                                        .build()
                        ))
                        .build();
            }

            default: {
                log.info("[INFO][DASHBOARD][ADMIN] 기본 워크플로우(재무 기준) 데이터 조회");
                ResponseEntity<List<DashboardWorkflowItemDto>> fcmArListResponse =
                        fetchWorkflowItemsWithoutUserId(
                                ApiClientKey.BUSINESS,
                                "관리자 매출 전표 워크플로우 조회",
                                "/fcm/workflow-items/sales-invoices",
                                limit
                        );
                ResponseEntity<List<DashboardWorkflowItemDto>> fcmApListResponse =
                        fetchWorkflowItemsWithoutUserId(
                                ApiClientKey.BUSINESS,
                                "관리자 매입 전표 워크플로우 조회",
                                "/fcm/workflow-items/purchase-invoices",
                                limit
                        );

                return buildFcmWorkflowResponse(fcmArListResponse, fcmApListResponse);
            }
        }
    }

    private DashboardWorkflowResponseDto buildFcmWorkflowResponse(
            ResponseEntity<List<DashboardWorkflowItemDto>> arResponse,
            ResponseEntity<List<DashboardWorkflowItemDto>> apResponse
    ) {
        return DashboardWorkflowResponseDto.builder()
                .tabs(List.of(
                        DashboardWorkflowTabDto.builder()
                                .tabCode("AR")
                                .items(safeItems(arResponse))
                                .build(),
                        DashboardWorkflowTabDto.builder()
                                .tabCode("AP")
                                .items(safeItems(apResponse))
                                .build()
                ))
                .build();
    }

    /**
     * null-safe로 items 뽑아오기
     */
    private static List<DashboardWorkflowItemDto> safeItems(
            ResponseEntity<List<DashboardWorkflowItemDto>> resp
    ) {
        if (resp == null || resp.getBody() == null) return List.of();
        return resp.getBody();
    }

    private ResponseEntity<List<DashboardWorkflowItemDto>> fetchWorkflowItemsWithUserIdQuery(
            ApiClientKey apiClientKey,
            String operation,
            String path,
            String userId,
            int size
    ) {
        requireUserId(userId, operation);
        return fetchWorkflowItems(
                apiClientKey,
                operation,
                path,
                uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("userId", userId)
                        .queryParam("size", size)
                        .build()
        );
    }

    private ResponseEntity<List<DashboardWorkflowItemDto>> fetchWorkflowItemsWithUserIdPath(
            ApiClientKey apiClientKey,
            String operation,
            String path,
            String userId,
            int size
    ) {
        requireUserId(userId, operation);
        return fetchWorkflowItems(
                apiClientKey,
                operation,
                path,
                uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("size", size)
                        .build(userId)
        );
    }

    private ResponseEntity<List<DashboardWorkflowItemDto>> fetchWorkflowItemsWithoutUserId(
            ApiClientKey apiClientKey,
            String operation,
            String path,
            int size
    ) {
        return fetchWorkflowItems(
                apiClientKey,
                operation,
                path,
                uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("size", size)
                        .build()
        );
    }

    private ResponseEntity<List<DashboardWorkflowItemDto>> fetchWorkflowItems(
            ApiClientKey apiClientKey,
            String operation,
            String path,
            java.util.function.Function<org.springframework.web.util.UriBuilder, java.net.URI> uriBuilderFunction
    ) {
        try {
            ResponseEntity<List<DashboardWorkflowItemDto>> response =
                    webClientProvider.getWebClient(apiClientKey)
                            .get()
                            .uri(uriBuilderFunction)
                            .retrieve()
                            .toEntity(WORKFLOW_ITEM_LIST_TYPE)
                            .block();

            if (response == null || response.getBody() == null) {
                log.error("[ERROR][DASHBOARD] {} 응답이 비어 있음 - path: {}", operation, path);
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, operation + " 응답이 비어 있습니다.");
            }

            return response;
        } catch (WebClientResponseException ex) {
            log.error("[ERROR][DASHBOARD] {} 실패 - path: {}, status: {}, body: {}", operation, path, ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        } catch (Exception e) {
            log.error("[ERROR][DASHBOARD] {} 중 예기치 않은 오류 발생 - path: {}", operation, path, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, operation + " 중 오류가 발생했습니다.", e);
        }
    }

    private void requireUserId(String userId, String operation) {
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(ErrorCode.MISSING_INPUT_VALUE, operation + " userId is required");
        }
    }
}
