package org.ever._4ever_be_gw.business.service;

import org.ever._4ever_be_gw.business.dto.analytics.SalesAnalyticsResponseDto;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * SD(영업관리) HTTP 서비스 인터페이스
 * Business 서비스의 SD 엔드포인트와 통신
 */
public interface SdHttpService {

    // ==================== Statistics ====================

    /**
     * 대시보드 통계 조회
     */
    ResponseEntity<ApiResponse<Object>> getDashboardStatistics();

    /**
     * 매출 분석 통계 조회
     */
    ResponseEntity<ApiResponse<SalesAnalyticsResponseDto>> getSalesAnalytics(String startDate, String endDate);

    // ==================== Customers ====================

    /**
     * 고객사 목록 조회
     */
    ResponseEntity<ApiResponse<Object>> getCustomerList(
            String status, String type, String search, Integer page, Integer size);

    /**
     * 고객사 등록
     */
    ResponseEntity<ApiResponse<Object>> createCustomer(Map<String, Object> requestBody);

    /**
     * 고객사 상세 정보 조회
     */
    ResponseEntity<ApiResponse<Object>> getCustomerDetail(String customerId);

    /**
     * 고객사 정보 수정
     */
    ResponseEntity<ApiResponse<Object>> updateCustomer(String customerId, Map<String, Object> requestBody);

    /**
     * 고객사 삭제
     */
    ResponseEntity<ApiResponse<Object>> deleteCustomer(String customerId);

    // ==================== Orders ====================

    /**
     * 주문 목록 조회
     */
    ResponseEntity<ApiResponse<Object>> getOrderList(
            String customerId, String employeeId, String startDate, String endDate, String search, String type, String status, Integer page, Integer size);

    /**
     * 주문서 상세 조회
     */
    ResponseEntity<ApiResponse<Object>> getOrderDetail(String salesOrderId);

    // ==================== Quotations ====================

    /**
     * 견적 목록 조회
     */
    ResponseEntity<ApiResponse<Object>> getQuotationList(
            String customerId, String startDate, String endDate, String status, String type, String search, String sort, Integer page, Integer size);

    /**
     * 견적 상세 조회
     */
    ResponseEntity<ApiResponse<Object>> getQuotationDetail(String quotationId);

    /**
     * 견적서 생성
     */
    ResponseEntity<ApiResponse<Object>> createQuotation(Map<String, Object> requestBody);

    /**
     * 견적서 승인 및 주문 생성
     */
    ResponseEntity<ApiResponse<Object>> approveQuotation(String quotationId, Map<String, Object> requestBody);

    /**
     * 견적서 검토 확정
     */
    ResponseEntity<ApiResponse<Object>> confirmQuotation(Map<String, Object> requestBody);

    /**
     * 견적서 거부
     */
    ResponseEntity<ApiResponse<Object>> rejectQuotation(String quotationId, Map<String, Object> requestBody);

    /**
     * 재고 확인
     */
    ResponseEntity<ApiResponse<Object>> checkInventory(Map<String, Object> requestBody);


    // 고객사 대시보드 견적 목록 요청
    ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardCustomerQuotationList(String userId, int size);

    // 내부 사용자 대시보드 견적 목록 요청
    ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardInternalQuotationList(int size);

    // 내부 사용자 대시보드 주문서 목록 요청
    ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardInternalOrderList(int size);

    // 공급사 대시보드 발주서(Quotation) 목록 요청
    ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardSupplierOrderList(String userId, int size);
}
