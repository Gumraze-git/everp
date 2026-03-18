package org.ever._4ever_be_business.fcm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.dto.request.SupplierPurchaseInvoiceRequestDto;
import org.ever._4ever_be_business.fcm.dto.request.UpdateAPInvoiceDto;
import org.ever._4ever_be_business.fcm.dto.request.UpdateARInvoiceDto;
import org.ever._4ever_be_business.fcm.dto.response.*;
import org.ever._4ever_be_business.fcm.service.*;
import org.ever._4ever_be_business.hr.dto.response.PageResponseDto;
import org.ever._4ever_be_business.sd.dto.response.PageInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/fcm")
@RequiredArgsConstructor
public class FcmController {

    private final FcmStatisticsService fcmStatisticsService;
    private final SalesStatementService salesStatementService;
    private final PurchaseStatementService purchaseStatementService;
    private final APInvoiceService apInvoiceService;
    private final ARInvoiceService arInvoiceService;
    private final VoucherStatusService voucherStatusService;
    private final SupplierPurchaseInvoiceService supplierPurchaseInvoiceService;

    // ==================== Statistics ====================

    /**
     * 재무관리 통계 조회 (주/월/분기/년)
     *
     */
    @GetMapping("/metrics")
    public ResponseEntity<FcmStatisticsDto> getFcmStatistics() {
        log.info("재무관리 통계 조회 API 호출");
        FcmStatisticsDto result = fcmStatisticsService.getFcmStatistics();
        log.info("재무관리 통계 조회 성공");
        return ResponseEntity.ok(result);
    }

    /**
     * 공급사별 총 매출 금액 조회 (PurchaseVoucher 기준)
     *
     * @param supplierUserId 공급사 사용자 ID
     */
    @GetMapping("/metrics/suppliers/{supplierUserId}/total-sales")
    public ResponseEntity<TotalAmountDto> getTotalPurchaseAmountBySupplierUserId(
            @PathVariable String supplierUserId) {
        log.info("공급사별 총 매출 금액 조회 API 호출 - supplierUserId: {}", supplierUserId);
        TotalAmountDto result =
                fcmStatisticsService.getTotalPurchaseAmountBySupplierUserId(supplierUserId);
        log.info("공급사별 총 매출 금액 조회 성공 - supplierUserId: {}", supplierUserId);
        return ResponseEntity.ok(result);
    }

    /**
     * 고객사별 총 매입 금액 조회 (SalesVoucher 기준)
     *
     * @param customerUserId 고객사 사용자 ID
     */
    @GetMapping("/metrics/customers/{customerUserId}/total-purchases")
    public ResponseEntity<TotalAmountDto> getTotalSalesAmountByCustomerUserId(
            @PathVariable String customerUserId) {
        log.info("고객사별 총 매입 금액 조회 API 호출 - customerUserId: {}", customerUserId);
        TotalAmountDto result =
                fcmStatisticsService.getTotalSalesAmountByCustomerUserId(customerUserId);
        log.info("고객사별 총 매입 금액 조회 성공 - customerUserId: {}", customerUserId);
        return ResponseEntity.ok(result);
    }

    // ==================== Sales Statements (매출전표) ====================

    /**
     * 매출전표 목록 조회
     */
    @GetMapping("/statement/as")
    public ResponseEntity<PageResponseDto<SalesStatementListItemDto>> getSalesStatementList(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("매출전표 목록 조회 API 호출 - company: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                company, startDate, endDate, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<SalesStatementListItemDto> result = salesStatementService.getSalesStatementList(company, startDate, endDate, pageable);

        PageInfo pageInfo = new PageInfo(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext()
        );

        PageResponseDto<SalesStatementListItemDto> responseDto = new PageResponseDto<>(
                (int) result.getTotalElements(),
                result.getContent(),
                pageInfo
        );

        log.info("매출전표 목록 조회 성공 - total: {}, size: {}", result.getTotalElements(), result.getContent().size());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 매출전표 상세 조회
     */
    @GetMapping("/statement/as/{statementId}")
    public ResponseEntity<SalesStatementDetailDto> getSalesStatementDetail(@PathVariable String statementId) {
        log.info("매출전표 상세 조회 API 호출 - statementId: {}", statementId);
        SalesStatementDetailDto result = salesStatementService.getSalesStatementDetail(statementId);
        log.info("매출전표 상세 조회 성공 - statementId: {}, invoiceCode: {}", statementId, result.getInvoiceCode());
        return ResponseEntity.ok(result);
    }

    // ==================== Purchase Statements (매입전표) ====================

    /**
     * 매입전표 목록 조회
     */
    @GetMapping("/statement/ap")
    public ResponseEntity<PageResponseDto<PurchaseInvoiceListDto>> getPurchaseStatementList(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("매입전표 목록 조회 API 호출 - company: {}, status: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                company, status, startDate, endDate, page, size);

        if ("ALL".equals(status))
            status = null;

        Pageable pageable = PageRequest.of(page, size);
        Page<PurchaseInvoiceListDto> result = purchaseStatementService.getPurchaseStatementList(
                company, status, startDate, endDate, pageable
        );

        PageInfo pageInfo = new PageInfo(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext()
        );

        PageResponseDto<PurchaseInvoiceListDto> responseDto = new PageResponseDto<>(
                (int) result.getTotalElements(),
                result.getContent(),
                pageInfo
        );

        log.info("매입전표 목록 조회 성공 - total: {}, size: {}", result.getTotalElements(), result.getContent().size());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 매입전표 상세 조회
     */
    @GetMapping("/statement/ap/{statementId}")
    public ResponseEntity<PurchaseStatementDetailDto> getPurchaseStatementDetail(@PathVariable String statementId) {
        log.info("매입전표 상세 조회 API 호출 - statementId: {}", statementId);
        PurchaseStatementDetailDto result = purchaseStatementService.getPurchaseStatementDetail(statementId);
        log.info("매입전표 상세 조회 성공 - statementId: {}, invoiceCode: {}", statementId, result.getInvoiceCode());
        return ResponseEntity.ok(result);
    }

    /**
     * 대시보드용(공급사) 매입 전표 목록 조회
     * GW 대시보드 스키마에 맞춘 아이템 DTO로 반환
     * - GET /fcm/invoice/ap/supplier?userId={userId}&size={size}
     */
    @GetMapping("/invoice/ap/supplier")
    public ResponseEntity<org.ever._4ever_be_business.hr.dto.response.PageResponseDto<SupplierPurchaseInvoiceListItemDto>> getSupplierPurchaseList(
            @ModelAttribute SupplierPurchaseInvoiceRequestDto request
    ) {
        int size = (request.getSize() != null && request.getSize() > 0) ? request.getSize() : 5;
        Pageable pageable = PageRequest.of(0, size);

        Page<SupplierPurchaseInvoiceListItemDto> result = supplierPurchaseInvoiceService.getSupplierPurchaseList(request, pageable);

        PageInfo pageInfo = new PageInfo(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext()
        );

        org.ever._4ever_be_business.hr.dto.response.PageResponseDto<SupplierPurchaseInvoiceListItemDto> responseDto =
                new org.ever._4ever_be_business.hr.dto.response.PageResponseDto<>(
                        (int) result.getTotalElements(),
                        result.getContent(),
                        pageInfo
                );

        return ResponseEntity.ok(responseDto);
    }
    // ==================== AP Invoices (매입 전표) ====================

    /**
     * AP 전표 상세 정보 조회
     */
    @GetMapping("/invoices/purchase/{invoiceId}")
    public ResponseEntity<APInvoiceDetailDto> getAPInvoiceDetail(@PathVariable String invoiceId) {
        log.info("AP 전표 상세 정보 조회 API 호출 - invoiceId: {}", invoiceId);
        APInvoiceDetailDto result = apInvoiceService.getAPInvoiceDetail(invoiceId);
        log.info("AP 전표 상세 정보 조회 성공 - invoiceId: {}, invoiceNumber: {}", invoiceId, result.getInvoiceNumber());
        return ResponseEntity.ok(result);
    }

    /**
     * 바우처 상태 수동 업데이트
     */
    @PatchMapping("/invoices/purchase/{invoiceId}")
    public ResponseEntity<Void> updateAPInvoice(
            @PathVariable String invoiceId,
            @RequestBody UpdateAPInvoiceDto requestDto) {
        log.info("AP 전표 정보 업데이트 API 호출 - invoiceId: {}, status: {}", invoiceId, requestDto.getStatus());

        if ("PAID".equalsIgnoreCase(requestDto.getStatus())) {
            apInvoiceService.completePayable(invoiceId);
        } else {
            voucherStatusService.updateVoucherStatus(invoiceId, requestDto.getStatus());
        }

        log.info("AP 전표 정보 업데이트 성공 - invoiceId: {}", invoiceId);
        return ResponseEntity.noContent().build();
    }

    // ==================== AR Invoices (매출 전표) ====================

    /**
     * AR 전표 목록 조회
     */
    @GetMapping("/invoices/sales")
    public ResponseEntity<PageResponseDto<ARInvoiceListItemDto>> getARInvoiceList(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("AR 전표 목록 조회 API 호출 - company: {}, status: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                company, status, startDate, endDate, page, size);

        if ("ALL".equals(status))
            status = null;

        Page<ARInvoiceListItemDto> result = arInvoiceService.getARInvoiceList(company, status, startDate, endDate, page, size);

        PageInfo pageInfo = new PageInfo(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext()
        );

        PageResponseDto<ARInvoiceListItemDto> responseDto = new PageResponseDto<>(
                (int) result.getTotalElements(),
                result.getContent(),
                pageInfo
        );

        log.info("AR 전표 목록 조회 성공 - totalElements: {}", result.getTotalElements());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * CustomerUserId 기반 AR 전표 목록 조회 (CUSTOMER 사용자용)
     */
    @GetMapping("/invoices/sales/customers/{customerUserId}")
    public ResponseEntity<PageResponseDto<ARInvoiceListItemDto>> getARInvoiceListByCustomerUserId(
            @PathVariable String customerUserId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("CustomerUserId 기반 AR 전표 목록 조회 API 호출 - customerUserId: {}, status: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                customerUserId, status, startDate, endDate, page, size);

        if(status != null && status.equals("ALL"))
            status = null;

        Page<ARInvoiceListItemDto> result = arInvoiceService.getARInvoiceListByCustomerUserId(customerUserId, status, startDate, endDate, page, size);

        PageInfo pageInfo = new PageInfo(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext()
        );

        PageResponseDto<ARInvoiceListItemDto> responseDto = new PageResponseDto<>(
                (int) result.getTotalElements(),
                result.getContent(),
                pageInfo
        );

        log.info("CustomerUserId 기반 AR 전표 목록 조회 성공 - totalElements: {}", result.getTotalElements());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * SupplierCompanyId 기반 AP 전표 목록 조회 (SUPPLIER 사용자용)
     */
    @GetMapping("/invoices/purchase/suppliers/{supplierUserId}")
    public ResponseEntity<PageResponseDto<APInvoiceListItemDto>> getAPInvoiceListBySupplierCompanyId(
            @PathVariable String supplierUserId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("SupplierCompanyId 기반 AP 전표 목록 조회 API 호출 - supplierCompanyId: {}, status: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                supplierUserId, status, startDate, endDate, page, size);

        if(status != null && status.equals("ALL"))
            status = null;

        Page<APInvoiceListItemDto> result = apInvoiceService.getAPInvoiceListBySupplierCompanyId(supplierUserId, status, startDate, endDate, page, size);

        PageInfo pageInfo = new PageInfo(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext()
        );

        PageResponseDto<APInvoiceListItemDto> responseDto = new PageResponseDto<>(
                (int) result.getTotalElements(),
                result.getContent(),
                pageInfo
        );

        log.info("SupplierCompanyId 기반 AP 전표 목록 조회 성공 - totalElements: {}", result.getTotalElements());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * AR 전표 상세 정보 조회
     */
    @GetMapping("/invoices/sales/{invoiceId}")
    public ResponseEntity<ARInvoiceDetailDto> getARInvoiceDetail(@PathVariable String invoiceId) {
        log.info("AR 전표 상세 정보 조회 API 호출 - invoiceId: {}", invoiceId);
        ARInvoiceDetailDto result = arInvoiceService.getARInvoiceDetail(invoiceId);
        log.info("AR 전표 상세 정보 조회 성공 - invoiceId: {}, invoiceNumber: {}", invoiceId, result.getInvoiceNumber());
        return ResponseEntity.ok(result);
    }

    /**
     * AR 전표 정보 업데이트
     */
    @PatchMapping("/invoices/sales/{invoiceId}")
    public ResponseEntity<Void> updateARInvoice(
            @PathVariable String invoiceId,
            @RequestBody UpdateARInvoiceDto requestDto) {
        log.info("AR 전표 정보 업데이트 API 호출 - invoiceId: {}, status: {}, dueDate: {}, memo: {}",
                invoiceId, requestDto.getStatus(), requestDto.getDueDate(), requestDto.getMemo());

        arInvoiceService.updateARInvoice(invoiceId, requestDto.getStatus(), requestDto.getDueDate(), requestDto.getMemo());
        log.info("AR 전표 정보 업데이트 성공 - invoiceId: {}", invoiceId);
        return ResponseEntity.noContent().build();
    }

    /**
     * AR 전표 미수 처리 완료
     */
    @PostMapping("/invoice/ar/{invoiceId}/receivable/complete")
    public ResponseEntity<Void> completeReceivable(@PathVariable String invoiceId) {
        log.info("AR 전표 미수 처리 완료 API 호출 - invoiceId: {}", invoiceId);
        arInvoiceService.completeReceivable(invoiceId);
        log.info("AR 전표 미수 처리 완료 성공 - invoiceId: {}", invoiceId);
        return ResponseEntity.noContent().build();
    }

    /**
     * AP 전표 미지급 처리 완료
     */
    @PostMapping("/invoice/ap/{invoiceId}/payable/complete")
    public ResponseEntity<Void> completePayable(@PathVariable String invoiceId) {
        log.info("AP 전표 미지급 처리 완료 API 호출 - invoiceId: {}", invoiceId);
        apInvoiceService.completePayable(invoiceId);
        log.info("AP 전표 미지급 처리 완료 성공 - invoiceId: {}", invoiceId);
        return ResponseEntity.noContent().build();
    }

    /**
     * AR 전표 상태 일괄 변경 (RESPONSE_PENDING)
     */
    @PostMapping("/invoice/ar/customer/response-pending")
    public ResponseEntity<Void> updateArInvoicesResponsePending(
            @RequestBody org.ever._4ever_be_business.fcm.dto.request.UpdateInvoiceIdsDto requestDto) {
        log.info("AR 전표 상태 일괄 변경 API 호출 - invoiceIds: {}", requestDto.getInvoiceIds());
        voucherStatusService.updateSalesVouchersToResponsePending(requestDto.getInvoiceIds());
        log.info("AR 전표 상태 일괄 변경 성공 - count: {}", requestDto.getInvoiceIds().size());
        return ResponseEntity.noContent().build();
    }

    /**
     * AP 전표 상태 일괄 변경 (RESPONSE_PENDING)
     */
    @PostMapping("/invoice/ap/supplier/response-pending")
    public ResponseEntity<Void> updateApInvoicesResponsePending(
            @RequestBody org.ever._4ever_be_business.fcm.dto.request.UpdateInvoiceIdsDto requestDto) {
        log.info("AP 전표 상태 일괄 변경 API 호출 - invoiceIds: {}", requestDto.getInvoiceIds());
        voucherStatusService.updatePurchaseVouchersToResponsePending(requestDto.getInvoiceIds());
        log.info("AP 전표 상태 일괄 변경 성공 - count: {}", requestDto.getInvoiceIds().size());
        return ResponseEntity.noContent().build();
    }
}
