package org.ever._4ever_be_business.fcm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.dto.response.ARInvoiceListItemDto;
import org.ever._4ever_be_business.fcm.dto.response.SupplierPurchaseInvoiceListItemDto;
import org.ever._4ever_be_business.fcm.service.ARInvoiceService;
import org.ever._4ever_be_business.fcm.service.CustomerDashboardInvoiceService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerDashboardInvoiceServiceImpl implements CustomerDashboardInvoiceService {

    private final ARInvoiceService arInvoiceService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public List<SupplierPurchaseInvoiceListItemDto> getCustomerInvoices(String userId, int size) {
        int limit = size > 0 ? Math.min(size, 20) : 5;

        List<ARInvoiceListItemDto> invoices = arInvoiceService.getARInvoiceList(
                userId, // company
                null,
                null,
                null,
                0,
                limit
        ).getContent();

        if (invoices == null || invoices.isEmpty()) {
            log.info("[DASHBOARD][FCM] 고객사 매입 전표 실데이터 없음 - 목업 데이터 반환");
            return buildMockCustomerInvoices(limit);
        }

        return invoices.stream()
                .map(this::toDashboardItem)
                .toList();
    }

    private SupplierPurchaseInvoiceListItemDto toDashboardItem(ARInvoiceListItemDto src) {
        String companyName = src.getSupply() != null ? src.getSupply().getSupplierName() : "고객사 미지정";
        return SupplierPurchaseInvoiceListItemDto.builder()
                .itemId(src.getInvoiceId())
                .itemNumber(src.getInvoiceNumber())
                .itemTitle(companyName)
                .name(companyName)
                .statusCode(src.getStatusCode())
                .date(src.getIssueDate())
                .build();
    }

    private List<SupplierPurchaseInvoiceListItemDto> buildMockCustomerInvoices(int size) {
        int limit = size > 0 ? Math.min(size, 20) : 5;
        int itemCount = Math.min(limit, 5);

        return IntStream.range(0, itemCount)
                .mapToObj(i -> SupplierPurchaseInvoiceListItemDto.builder()
                        .itemId(UUID.randomUUID().toString())
                        .itemNumber(String.format("AP-MOCK-%04d", i + 1))
                        .itemTitle("고객사 목업 매입 전표 " + (i + 1))
                        .name("고객사 담당자 " + (i + 1))
                        .statusCode(i % 2 == 0 ? "PENDING" : "APPROVED")
                        .date(OffsetDateTime.now().minusDays(i).toLocalDate().format(DATE_FORMATTER))
                        .build())
                .toList();
    }
}
