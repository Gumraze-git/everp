package org.ever._4ever_be_gw.business.service;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;

public interface FcmHttpService {

    ResponseEntity<?> getFcmStatistics(String periods);

    ResponseEntity<?> getApInvoices(
        String company,
        String status,
        String startDate,
        String endDate,
        Integer page,
        Integer size
    );

    ResponseEntity<?> getApInvoicesBySupplierUserId(
        String supplierUserId,
        String status,
        String startDate,
        String endDate,
        Integer page,
        Integer size
    );

    ResponseEntity<?> getArInvoices(
        String company,
        String status,
        String startDate,
        String endDate,
        Integer page,
        Integer size
    );

    ResponseEntity<?> getArInvoicesByCustomerUserId(
        String customerUserId,
        String status,
        String startDate,
        String endDate,
        Integer page,
        Integer size
    );

    ResponseEntity<?> getApInvoiceDetail(String invoiceId);

    ResponseEntity<?> getArInvoiceDetail(String invoiceId);

    ResponseEntity<?> patchApInvoice(String invoiceId, Map<String, Object> requestBody);

    ResponseEntity<?> patchArInvoice(String invoiceId, Map<String, Object> requestBody);

    ResponseEntity<?> completeReceivable(String invoiceId);

    ResponseEntity<?> completePayable(String invoiceId);

    ResponseEntity<?> requestApReceivable(String invoiceId);

    ResponseEntity<?> updateArInvoicesResponsePending(List<String> invoiceIds);

    ResponseEntity<?> updateApInvoicesResponsePending(List<String> invoiceIds);

    ResponseEntity<?> getSupplierTotalSales(String supplierUserId);

    ResponseEntity<?> getCustomerTotalPurchases(String customerUserId);

    ResponseEntity<?> getDashboardSupplierInvoiceList(String userId, Integer size);

    ResponseEntity<?> getDashboardCustomerInvoiceList(String userId, Integer size);

    ResponseEntity<?> getDashboardCompanyArList(String userId, Integer size);

    ResponseEntity<?> getDashboardCompanyApList(String userId, Integer size);
}
