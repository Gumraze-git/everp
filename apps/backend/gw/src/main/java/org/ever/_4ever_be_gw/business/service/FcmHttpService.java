package org.ever._4ever_be_gw.business.service;

import org.ever._4ever_be_gw.business.dto.fcm.response.FcmStatisticsDto;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface FcmHttpService {

    /**
     * 재무관리 통계 조회
     */
    ResponseEntity<ApiResponse<FcmStatisticsDto>> getFcmStatistics(String periods);

    /**
     * 매입 전표 목록 조회 (AP)
     */
    ResponseEntity<ApiResponse<Object>> getApInvoices(
            String company,String status, String startDate, String endDate, Integer page, Integer size);

    /**
     * 공급사 사용자 ID로 매입 전표 목록 조회 (AP)
     * Business 서비스에서 SCM 서비스를 호출하여 supplierCompanyId를 얻어 조회
     */
    ResponseEntity<ApiResponse<Object>> getApInvoicesBySupplierUserId(
            String supplierUserId, String status, String startDate, String endDate, Integer page, Integer size);

    /**
     * 매출 전표 목록 조회 (AR)
     */
    ResponseEntity<ApiResponse<Object>> getArInvoices(
            String company, String status, String startDate, String endDate, Integer page, Integer size);

    /**
     * 고객사 사용자 ID로 매출 전표 목록 조회 (AR)
     * Business 서비스에서 CustomerUser 기반으로 조회
     */
    ResponseEntity<ApiResponse<Object>> getArInvoicesByCustomerUserId(
            String customerUserId, String status, String startDate, String endDate, Integer page, Integer size);

    /**
     * 매입 전표 상세 조회 (AP)
     */
    ResponseEntity<ApiResponse<Object>> getApInvoiceDetail(String invoiceId);

    /**
     * 매출 전표 상세 조회 (AR)
     */
    ResponseEntity<ApiResponse<Object>> getArInvoiceDetail(String invoiceId);

    /**
     * 매입 전표 수정 (AP)
     */
    ResponseEntity<ApiResponse<Object>> patchApInvoice(String invoiceId, Map<String, Object> requestBody);

    /**
     * 매출 전표 수정 (AR)
     */
    ResponseEntity<ApiResponse<Object>> patchArInvoice(String invoiceId, Map<String, Object> requestBody);

    /**
     * 미수 처리 완료 (AR)
     */
    ResponseEntity<ApiResponse<Object>> completeReceivable(String invoiceId);

    /**
     * 매입 전표 미수 처리 요청 (AP)
     */
    ResponseEntity<ApiResponse<Object>> requestApReceivable(String invoiceId);

    /**
     * 매출 전표 상태를 RESPONSE_PENDING으로 일괄 변경 (AR)
     */
    ResponseEntity<ApiResponse<Object>> updateArInvoicesResponsePending(java.util.List<String> invoiceIds);

    /**
     * 매입 전표 상태를 RESPONSE_PENDING으로 일괄 변경 (AP)
     */
    ResponseEntity<ApiResponse<Object>> updateApInvoicesResponsePending(java.util.List<String> invoiceIds);

    // 공급사 매출 전표(매입 전표가 공급사 입장에서는 매출 전표)
    ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardSupplierInvoiceList(String userId, Integer size);

    // 고객사 매입 전표(기업의 매출 전표가 고객사 입장에서 매입 전표)
    ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardCustomerInvoiceList(String userId, Integer size);

    // 기업 매출 전표(AR) 목록
    ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardCompanyArList(String userId, Integer size);

    // 기업 매입 전표(AP) 목록
    ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardCompanyApList(String userId, Integer size);

}
