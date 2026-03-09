package org.ever._4ever_be_business.fcm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.dto.response.SupplierPurchaseInvoiceListItemDto;
import org.ever._4ever_be_business.fcm.service.CustomerDashboardInvoiceService;
import org.ever._4ever_be_business.fcm.service.SupplierDashboardInvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/fcm/dashboard")
@RequiredArgsConstructor
public class FcmDashboardController {

    private final SupplierDashboardInvoiceService supplierDashboardInvoiceService;
    private final CustomerDashboardInvoiceService customerDashboardInvoiceService;

    @GetMapping("/invoice/ap/supplier")
    public ResponseEntity<List<SupplierPurchaseInvoiceListItemDto>> getSupplierApInvoices(
            @RequestParam("userId") String userId,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        log.info("[DASHBOARD][FCM] 공급사 매입 전표 목록 조회 요청 - userId: {}, size: {}", userId, size);

        List<SupplierPurchaseInvoiceListItemDto> invoices =
                supplierDashboardInvoiceService.getSupplierInvoices(userId, size);

        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/invoice/ar/customer")
    public ResponseEntity<List<SupplierPurchaseInvoiceListItemDto>> getCustomerApInvoices(
            @RequestParam("userId") String userId,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        log.info("[DASHBOARD][FCM] 고객사 매입 전표 목록 조회 요청 - userId: {}, size: {}", userId, size);

        List<SupplierPurchaseInvoiceListItemDto> invoices =
                customerDashboardInvoiceService.getCustomerInvoices(userId, size);

        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/invoice/ar")
    public ResponseEntity<List<SupplierPurchaseInvoiceListItemDto>> getCompanyArInvoices(
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        log.info("[DASHBOARD][FCM] 기업 전체 매출 전표 목록 조회 요청 - size: {}", size);

        List<SupplierPurchaseInvoiceListItemDto> invoices =
                supplierDashboardInvoiceService.getCompanyArInvoices(size);

        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/invoice/ap")
    public ResponseEntity<List<SupplierPurchaseInvoiceListItemDto>> getCompanyApInvoices(
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        log.info("[DASHBOARD][FCM] 기업 전체 매입 전표 목록 조회 요청 - size: {}", size);

        List<SupplierPurchaseInvoiceListItemDto> invoices =
                supplierDashboardInvoiceService.getCompanyApInvoices(size);

        return ResponseEntity.ok(invoices);
    }
}
