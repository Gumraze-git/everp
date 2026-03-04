package org.ever._4ever_be_business.fcm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.dto.response.PurchaseInvoiceListDto;
import org.ever._4ever_be_business.fcm.dto.response.SupplierPurchaseInvoiceListItemDto;
import org.ever._4ever_be_business.fcm.service.PurchaseStatementService;
import org.ever._4ever_be_business.fcm.service.SupplierCompanyResolver;
import org.ever._4ever_be_business.fcm.service.SupplierDashboardInvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierDashboardInvoiceServiceImpl implements SupplierDashboardInvoiceService {

    private final PurchaseStatementService purchaseStatementService;
    private final SupplierCompanyResolver supplierCompanyResolver;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public List<SupplierPurchaseInvoiceListItemDto> getSupplierInvoices(String supplierUserId, int size) {
        int limit = size > 0 ? Math.min(size, 20) : 5;

        SupplierCompanyResolver.SupplierCompanyInfo companyInfo = supplierCompanyResolver.resolve(supplierUserId);
        log.info("[DASHBOARD][FCM] 공급사 매핑 - userId={}, companyId={}", supplierUserId, companyInfo.supplierCompanyId());

        Page<PurchaseInvoiceListDto> page = purchaseStatementService.getPurchaseStatementListBySupplierUserId(
                supplierUserId,
                null,
                null,
                PageRequest.of(0, limit)
        );

        List<PurchaseInvoiceListDto> invoices = page.getContent();
        String supplierName = companyInfo.supplierCompanyName() != null
                ? companyInfo.supplierCompanyName()
                : "공급사";

        if (invoices == null || invoices.isEmpty()) {
            log.info("[DASHBOARD][MOCK][SUPPLIER][AR] 실데이터 없음 - 목업 전표 반환, userId: {}", supplierUserId);
            return buildMockSupplierInvoices(limit, supplierName);
        }

        return invoices.stream()
                .map(this::toDashboardItem)
                .toList();
    }

    @Override
    public List<SupplierPurchaseInvoiceListItemDto> getCompanyArInvoices(int size) {
        int limit = size > 0 ? Math.min(size, 20) : 5;

        Page<PurchaseInvoiceListDto> page = purchaseStatementService.getPurchaseStatementList(
                null,
                null,
                null,
                null,
                PageRequest.of(0, limit)
        );

        List<PurchaseInvoiceListDto> invoices = page.getContent();

        if (invoices == null || invoices.isEmpty()) {
            log.info("[DASHBOARD][MOCK][FCM][AR] 실데이터 없음 - 기업 매출 전표 목업 데이터 반환");
            return buildMockCompanyArInvoices(limit);
        }

        return invoices.stream()
                .map(this::toDashboardItem)
                .toList();
    }

    @Override
    public List<SupplierPurchaseInvoiceListItemDto> getCompanyApInvoices(int size) {
        int limit = size > 0 ? Math.min(size, 20) : 5;

        Page<PurchaseInvoiceListDto> page = purchaseStatementService.getPurchaseStatementList(
                null,
                null,
                null,
                null,
                PageRequest.of(0, limit)
        );

        List<PurchaseInvoiceListDto> invoices = page.getContent();

        if (invoices == null || invoices.isEmpty()) {
            log.info("[DASHBOARD][MOCK][FCM][AP] 실데이터 없음 - 기업 매입 전표 목업 데이터 반환");
            return buildMockCompanyApInvoices(limit);
        }

        return invoices.stream()
                .map(this::toDashboardItem)
                .toList();
    }

    private SupplierPurchaseInvoiceListItemDto toDashboardItem(PurchaseInvoiceListDto src) {
        String title = src.getConnection() != null ? src.getConnection().getConnectionName() : "";
        return SupplierPurchaseInvoiceListItemDto.builder()
                .itemId(src.getInvoiceId())
                .itemNumber(src.getInvoiceCode())
                .itemTitle(title)
                .name(title)
                .statusCode(src.getStatus())
                .date(src.getIssueDate())
                .build();
    }

    private List<SupplierPurchaseInvoiceListItemDto> buildMockSupplierInvoices(int size, String supplierName) {
        int itemCount = Math.min(size > 0 ? size : 5, 5);

        return IntStream.range(0, itemCount)
                .mapToObj(i -> SupplierPurchaseInvoiceListItemDto.builder()
                        .itemId(UUID.randomUUID().toString())
                        .itemNumber(String.format("AR-MOCK-%04d", i + 1))
                        .itemTitle(supplierName + " 목업 매출 전표 " + (i + 1))
                        .name(supplierName)
                        .statusCode(i % 2 == 0 ? "ISSUED" : "PAID")
                        .date(OffsetDateTime.now().minusDays(i).toLocalDate().format(DATE_FORMATTER))
                        .build())
                .toList();
    }

    private List<SupplierPurchaseInvoiceListItemDto> buildMockCompanyArInvoices(int size) {
        int itemCount = Math.min(size > 0 ? size : 5, 5);

        return IntStream.range(0, itemCount)
                .mapToObj(i -> SupplierPurchaseInvoiceListItemDto.builder()
                        .itemId(UUID.randomUUID().toString())
                        .itemNumber(String.format("AR-MOCK-%04d", i + 1))
                        .itemTitle("기업 매출 전표 목업 " + (i + 1))
                        .name("재무 담당자 " + (i + 1))
                        .statusCode(i % 2 == 0 ? "ISSUED" : "PAID")
                        .date(LocalDate.now().minusDays(i).toString())
                        .build())
                .toList();
    }

    private List<SupplierPurchaseInvoiceListItemDto> buildMockCompanyApInvoices(int size) {
        int itemCount = Math.min(size > 0 ? size : 5, 5);

        return IntStream.range(0, itemCount)
                .mapToObj(i -> SupplierPurchaseInvoiceListItemDto.builder()
                        .itemId(UUID.randomUUID().toString())
                        .itemNumber(String.format("AP-MOCK-%04d", i + 1))
                        .itemTitle("기업 매입 전표 목업 " + (i + 1))
                        .name("재무 담당자 " + (i + 1))
                        .statusCode(i % 2 == 0 ? "REQUESTED" : "APPROVED")
                        .date(LocalDate.now().minusDays(i).toString())
                        .build())
                .toList();
    }
}
