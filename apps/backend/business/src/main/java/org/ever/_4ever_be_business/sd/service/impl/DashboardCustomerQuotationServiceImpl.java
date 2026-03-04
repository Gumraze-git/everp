package org.ever._4ever_be_business.sd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.hr.entity.CustomerUser;
import org.ever._4ever_be_business.hr.repository.CustomerUserRepository;
import org.ever._4ever_be_business.order.entity.Quotation;
import org.ever._4ever_be_business.order.entity.QuotationItem;
import org.ever._4ever_be_business.order.repository.QuotationItemRepository;
import org.ever._4ever_be_business.order.repository.QuotationRepository;
import org.ever._4ever_be_business.sd.dto.response.DashboardWorkflowItemDto;
import org.ever._4ever_be_business.sd.integration.dto.ProductInfoResponseDto;
import org.ever._4ever_be_business.sd.integration.port.ProductServicePort;
import org.ever._4ever_be_business.sd.service.DashboardCustomerQuotationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardCustomerQuotationServiceImpl implements DashboardCustomerQuotationService {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final CustomerUserRepository customerUserRepository;
    private final QuotationRepository quotationRepository;
    private final QuotationItemRepository quotationItemRepository;
    private final ProductServicePort productServicePort;

    @Override
    public List<DashboardWorkflowItemDto> getCustomerQuotations(String userId, int size) {
        int limit = size > 0 ? Math.min(size, 20) : 5;

        CustomerUser customerUser = customerUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

        var page = quotationRepository.findByCustomerUserIdOrderByCreatedAtDesc(
                customerUser.getId(),
                PageRequest.of(0, limit)
        );

        List<Quotation> quotations = page.getContent();
        String requesterName = customerUser.getCustomerName() != null
                ? customerUser.getCustomerName()
                : "고객 담당자";
        String companyName = customerUser.getCustomerCompany() != null
                ? customerUser.getCustomerCompany().getCompanyName()
                : "고객사 미지정";

        if (quotations == null || quotations.isEmpty()) {
            log.info("[DASHBOARD][MOCK] 고객사 견적서 목업 데이터 반환 - userId: {}", userId);
            return buildMockCustomerQuotations(limit, requesterName, companyName);
        }

        return quotations.stream()
                .map(quotation -> toDashboardItem(quotation, customerUser.getCustomerName(),
                        customerUser.getCustomerCompany() != null
                                ? customerUser.getCustomerCompany().getCompanyName()
                                : "고객사 미지정"))
                .toList();
    }

    @Override
    public List<DashboardWorkflowItemDto> getAllQuotations(int size) {
        int limit = size > 0 ? Math.min(size, 20) : 5;

        var page = quotationRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit));
        List<Quotation> quotations = page.getContent();

        if (quotations.isEmpty()) {
            log.info("[DASHBOARD][MOCK][SD][QT] 실데이터 없음 - 내부 견적서 목업 데이터 반환");
            return buildMockInternalQuotations(limit);
        }

        Map<String, List<QuotationItem>> itemsByQuotation = mapQuotationItems(quotations);
        Map<String, ProductInfoResponseDto.ProductDto> productMap = buildProductMap(itemsByQuotation);

        return IntStream.range(0, quotations.size())
                .mapToObj(index -> {
                    Quotation quotation = quotations.get(index);
                    return toInternalDashboardItem(
                            quotation,
                            itemsByQuotation.getOrDefault(quotation.getId(), List.of()),
                            productMap,
                            index
                    );
                })
                .toList();
    }

    private Map<String, List<QuotationItem>> mapQuotationItems(List<Quotation> quotations) {
        if (quotations == null || quotations.isEmpty()) {
            return Map.of();
        }

        List<String> quotationIds = quotations.stream()
                .map(Quotation::getId)
                .filter(Objects::nonNull)
                .toList();

        if (quotationIds.isEmpty()) {
            return Map.of();
        }

        return quotationItemRepository.findByQuotation_IdIn(quotationIds).stream()
                .filter(item -> item.getQuotation() != null && item.getQuotation().getId() != null)
                .collect(Collectors.groupingBy(item -> item.getQuotation().getId()));
    }

    private Map<String, ProductInfoResponseDto.ProductDto> buildProductMap(Map<String, List<QuotationItem>> itemsByQuotation) {
        if (itemsByQuotation.isEmpty()) {
            return Map.of();
        }

        Set<String> productIds = itemsByQuotation.values().stream()
                .flatMap(List::stream)
                .map(QuotationItem::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (productIds.isEmpty()) {
            return Map.of();
        }

        try {
            ProductInfoResponseDto response = productServicePort.getProductsByIds(new ArrayList<>(productIds));
            if (response == null || response.getProducts() == null) {
                log.warn("[DASHBOARD][SD][QT] 제품 정보 조회 결과가 비어 있습니다.");
                return Map.of();
            }

            return response.getProducts().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(
                            ProductInfoResponseDto.ProductDto::getProductId,
                            Function.identity(),
                            (left, right) -> left
                    ));
        } catch (Exception ex) {
            log.warn("[DASHBOARD][SD][QT] 제품 정보 조회 실패: {}", ex.getMessage());
            return Map.of();
        }
    }

    private DashboardWorkflowItemDto toInternalDashboardItem(
            Quotation quotation,
            List<QuotationItem> items,
            Map<String, ProductInfoResponseDto.ProductDto> productMap,
            int index
    ) {
        String fallbackTitle = String.format("견적 %d", index + 1);
        String fallbackName = String.format("담당자 %d", index + 1);

        return DashboardWorkflowItemDto.builder()
                .itemId(quotation.getId())
                .itemTitle(buildItemTitle(items, productMap, fallbackTitle))
                .itemNumber(quotation.getQuotationCode())
                .name(resolveCustomerName(quotation.getCustomerUserId(), fallbackName))
                .statusCode(quotation.getQuotationApproval() != null
                        ? quotation.getQuotationApproval().getApprovalStatus().name()
                        : "PENDING")
                .date(quotation.getCreatedAt() != null
                        ? quotation.getCreatedAt().toLocalDate().format(ISO_FORMATTER)
                        : null)
                .build();
    }

    private String buildItemTitle(
            List<QuotationItem> items,
            Map<String, ProductInfoResponseDto.ProductDto> productMap,
            String defaultTitle
    ) {
        if (items == null || items.isEmpty()) {
            return defaultTitle;
        }

        try {
            QuotationItem representative = items.get(0);
            String baseName = Optional.ofNullable(representative.getProductId())
                    .map(productMap::get)
                    .map(ProductInfoResponseDto.ProductDto::getProductName)
                    .filter(name -> !name.isBlank())
                    .orElse(null);

            if (baseName == null || baseName.isBlank()) {
                return defaultTitle;
            }

            int extraCount = Math.max(0, items.size() - 1);
            return extraCount > 0 ? String.format("%s 외 %d건", baseName, extraCount) : baseName;
        } catch (Exception ex) {
            log.warn("[DASHBOARD][SD][QT] itemTitle 생성 실패: {}", ex.getMessage());
            return defaultTitle;
        }
    }

    private String resolveCustomerName(String customerUserId, String defaultName) {
        if (customerUserId == null || customerUserId.isBlank()) {
            return defaultName;
        }

        return customerUserRepository.findById(customerUserId)
                .map(CustomerUser::getCustomerName)
                .filter(name -> name != null && !name.isBlank())
                .orElse(defaultName);
    }

    private DashboardWorkflowItemDto toDashboardItem(Quotation quotation, String requesterName, String companyName) {
        return DashboardWorkflowItemDto.builder()
                .itemId(quotation.getId())
                .itemTitle(companyName)
                .itemNumber(quotation.getQuotationCode())
                .name(requesterName)
                .statusCode(quotation.getQuotationApproval() != null
                        ? quotation.getQuotationApproval().getApprovalStatus().name()
                        : "PENDING")
                .date(quotation.getCreatedAt() != null
                        ? quotation.getCreatedAt().toLocalDate().format(ISO_FORMATTER)
                        : null)
                .build();
    }

    private List<DashboardWorkflowItemDto> buildMockCustomerQuotations(int size, String requesterName, String companyName) {
        int limit = size > 0 ? Math.min(size, 20) : 5;
        int itemCount = Math.min(limit, 5);

        return IntStream.range(0, itemCount)
                .mapToObj(i -> DashboardWorkflowItemDto.builder()
                        .itemId(UUID.randomUUID().toString())
                        .itemTitle(companyName)
                        .itemNumber(String.format("QT-MOCK-%04d", i + 1))
                        .name(requesterName)
                        .statusCode(i % 2 == 0 ? "PENDING" : "APPROVED")
                        .date(OffsetDateTime.now().minusDays(i).toLocalDate().format(ISO_FORMATTER))
                        .build())
                .toList();
    }

    private List<DashboardWorkflowItemDto> buildMockInternalQuotations(int size) {
        int limit = size > 0 ? Math.min(size, 20) : 5;
        int itemCount = Math.min(limit, 5);

        return IntStream.range(0, itemCount)
                .mapToObj(i -> DashboardWorkflowItemDto.builder()
                        .itemId(UUID.randomUUID().toString())
                        .itemTitle("내부 견적 요청 " + (i + 1))
                        .itemNumber(String.format("QT-MOCK-%04d", i + 1))
                        .name("영업 담당자 " + (i + 1))
                        .statusCode(i % 2 == 0 ? "IN_REVIEW" : "APPROVED")
                        .date(LocalDate.now().minusDays(i).toString())
                        .build())
                .toList();
    }
}
