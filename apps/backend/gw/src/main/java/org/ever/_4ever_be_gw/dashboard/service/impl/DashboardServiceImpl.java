package org.ever._4ever_be_gw.dashboard.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.service.FcmHttpService;
import org.ever._4ever_be_gw.business.service.HrmHttpService;
import org.ever._4ever_be_gw.business.service.SdHttpService;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.dashboard.service.DashboardService;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowResponseDto;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowTabDto;
import org.ever._4ever_be_gw.scm.im.service.ImHttpService;
import org.ever._4ever_be_gw.scm.mm.service.MmHttpService;
import org.ever._4ever_be_gw.scm.pp.PpHttpService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final int DEFAULT_SIZE = 5;
    private static final TypeReference<List<DashboardWorkflowItemDto>> WORKFLOW_ITEM_LIST =
        new TypeReference<>() {};

    private final SdHttpService sdHttpService;
    private final FcmHttpService fcmHttpService;
    private final HrmHttpService hrmHttpService;
    private final MmHttpService mmHttpService;
    private final ImHttpService imHttpService;
    private final PpHttpService ppHttpService;
    private final ObjectMapper objectMapper;

    @Override
    public DashboardWorkflowResponseDto getDashboardWorkflow(EverUserPrincipal principal, Integer size) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);
        }

        final String userId = principal.getUserId();
        final String userRole = principal.getUserRole();
        final int limit = Optional.ofNullable(size).orElse(DEFAULT_SIZE);

        log.info("[INFO][DASHBOARD] 워크플로우 구성 시작 - userId: {}, role: {}, limit: {}", userId, userRole, limit);

        return switch (userRole.split("_")[0]) {
            case "SUPPLIER" -> buildWorkflowResponse(
                tab("purchase-orders", "발주 프로세스", mmHttpService.getDashboardPurchaseOrderList(userId, limit)),
                tab("sales-invoices", "매출 프로세스", fcmHttpService.getDashboardSupplierInvoiceList(userId, limit))
            );
            case "CUSTOMER" -> buildWorkflowResponse(
                tab("quotations", "견적 프로세스", sdHttpService.getDashboardCustomerQuotationList(userId, limit)),
                tab("purchase-invoices", "매입 프로세스", fcmHttpService.getDashboardCustomerInvoiceList(userId, limit))
            );
            case "MM" -> buildWorkflowResponse(
                tab("purchase-requests", "구매 요청 프로세스", mmHttpService.getDashboardPurchaseRequestsOverall(limit)),
                tab("purchase-orders", "발주 프로세스", mmHttpService.getDashboardPurchaseOrdersOverall(limit))
            );
            case "SD" -> buildWorkflowResponse(
                tab("quotations", "견적 프로세스", sdHttpService.getDashboardInternalQuotationList(limit)),
                tab("orders", "주문 프로세스", sdHttpService.getDashboardInternalOrderList(limit))
            );
            case "FCM" -> buildFinanceWorkflowResponse(limit);
            case "IM" -> buildWorkflowResponse(
                tab("inbound", "입고 프로세스", imHttpService.getDashboardInboundList(userId, limit)),
                tab("outbound", "출고 프로세스", imHttpService.getDashboardOutboundList(userId, limit))
            );
            case "HRM" -> buildWorkflowResponse(
                tab("attendance", "근태 프로세스", hrmHttpService.getDashboardAttendanceList(userId, limit)),
                tab("leave-requests", "휴가 프로세스", hrmHttpService.getDashboardLeaveRequestList(userId, limit))
            );
            case "PP" -> buildWorkflowResponse(
                tab("quotations", "견적 프로세스", ppHttpService.getDashboardQuotationsToProduction(userId, limit)),
                tab("mes", "생산 프로세스", ppHttpService.getDashboardProductionInProgress(userId, limit))
            );
            default -> buildFinanceWorkflowResponse(limit);
        };
    }

    private DashboardWorkflowResponseDto buildFinanceWorkflowResponse(int limit) {
        return buildWorkflowResponse(
            tab("sales-invoices", "매출 프로세스", fcmHttpService.getDashboardCompanyArList(null, limit)),
            tab("purchase-invoices", "매입 프로세스", fcmHttpService.getDashboardCompanyApList(null, limit))
        );
    }

    private DashboardWorkflowResponseDto buildWorkflowResponse(
        DashboardWorkflowTabDto firstTab,
        DashboardWorkflowTabDto secondTab
    ) {
        return DashboardWorkflowResponseDto.builder()
            .tabs(List.of(firstTab, secondTab))
            .build();
    }

    private DashboardWorkflowTabDto tab(String tabId, String label, ResponseEntity<?> response) {
        return DashboardWorkflowTabDto.builder()
            .tabId(tabId)
            .label(label)
            .items(safeItems(response))
            .build();
    }

    private List<DashboardWorkflowItemDto> safeItems(ResponseEntity<?> response) {
        if (response == null || response.getBody() == null) {
            return List.of();
        }
        try {
            return objectMapper.convertValue(response.getBody(), WORKFLOW_ITEM_LIST);
        } catch (IllegalArgumentException ex) {
            log.error("[ERROR][DASHBOARD] 워크플로우 응답 변환 실패 - bodyType: {}", response.getBody().getClass(), ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "워크플로우 응답 변환에 실패했습니다.", ex);
        }
    }
}
