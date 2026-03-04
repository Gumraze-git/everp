package org.ever._4ever_be_business.fcm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.fcm.dao.PurchaseStatementDAO;
import org.ever._4ever_be_business.fcm.dto.internal.PurchaseStatementInfoDto;
import org.ever._4ever_be_business.fcm.dto.internal.PurchaseStatementListItemInfoDto;
import org.ever._4ever_be_business.fcm.dto.response.PurchaseStatementConnectionDto;
import org.ever._4ever_be_business.fcm.dto.response.PurchaseStatementDetailDto;
import org.ever._4ever_be_business.fcm.dto.response.PurchaseStatementItemDto;
import org.ever._4ever_be_business.fcm.dto.response.PurchaseInvoiceListDto;
import org.ever._4ever_be_business.fcm.dto.response.PurchaseStatementReferenceDto;
import org.ever._4ever_be_business.fcm.integration.dto.ProductOrderInfoResponseDto;
import org.ever._4ever_be_business.fcm.integration.dto.ProductOrderInfosResponseDto;
import org.ever._4ever_be_business.fcm.integration.dto.SupplierCompaniesResponseDto;
import org.ever._4ever_be_business.fcm.integration.dto.SupplierCompanyResponseDto;
import org.ever._4ever_be_business.fcm.integration.port.ProductOrderServicePort;
import org.ever._4ever_be_business.fcm.integration.port.SupplierCompanyServicePort;
import org.ever._4ever_be_business.fcm.service.PurchaseStatementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseStatementServiceImpl implements PurchaseStatementService {

    private final PurchaseStatementDAO purchaseStatementDAO;
    private final ProductOrderServicePort productOrderServicePort;
    private final SupplierCompanyServicePort supplierCompanyServicePort;

    @Override
    @Transactional(readOnly = true)
    public PurchaseStatementDetailDto getPurchaseStatementDetail(String statementId) {
        log.info("매입전표 상세 정보 조회 요청 - statementId: {}", statementId);

        // 1. DAO에서 기본 정보 조회 (supplierCompanyId와 productOrderId 포함)
        PurchaseStatementInfoDto statementInfo = purchaseStatementDAO.findPurchaseStatementInfoById(statementId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "매입전표 정보를 찾을 수 없습니다."));

        // 2. SCM 서버에서 ProductOrder 아이템 정보 조회
        ProductOrderInfoResponseDto productOrderInfo = productOrderServicePort.getProductOrderItemsById(
                statementInfo.getProductOrderId()
        );

        // 3. items 변환
        List<PurchaseStatementItemDto> items = productOrderInfo.getItems().stream()
                .map(item -> new PurchaseStatementItemDto(
                        item.getItemName(),
                        item.getQuantity(),
                        item.getUomName(),
                        item.getUnitPrice(),
                        item.getTotalPrice()
                ))
                .toList();

        // 4. SCM 서버에서 Supplier Company 정보 조회
        SupplierCompanyResponseDto supplierCompany = supplierCompanyServicePort.getSupplierCompanyById(
                statementInfo.getSupplierCompanyId()
        );

        // 5. DTO 재조립 (supplierName과 items 채우기)
        PurchaseStatementDetailDto result = new PurchaseStatementDetailDto(
                statementInfo.getInvoiceId(),
                statementInfo.getInvoiceCode(),
                "AP", // invoiceType - 매입전표는 AP (Accounts Payable)
                statementInfo.getStatusCode(),
                statementInfo.getIssueDate(),
                statementInfo.getDueDate(),
                supplierCompany.getCompanyName(),
                productOrderInfo.getProductOrderNumber(), // referenceCode
                statementInfo.getTotalAmount(),
                statementInfo.getNote(),
                items
        );

        log.info("매입전표 상세 정보 조회 성공 - statementId: {}, invoiceCode: {}",
                statementId, result.getInvoiceCode());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseInvoiceListDto> getPurchaseStatementList(
            String company,
            String status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        log.info("매입전표 목록 조회 요청 - company: {}, status: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                company, status, startDate, endDate, pageable.getPageNumber(), pageable.getPageSize());

        // 1. DAO에서 기본 목록 조회 (company 필터 제외)
        Page<PurchaseStatementListItemInfoDto> statementInfoPage = purchaseStatementDAO.findPurchaseStatementList(
                company, status, startDate, endDate, pageable
        );

        List<PurchaseStatementListItemInfoDto> statementInfos = statementInfoPage.getContent();

        if (statementInfos.isEmpty()) {
            log.info("매입전표 목록 조회 완료 - 결과 없음");
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // 2. SCM에서 supplier company 정보 조회 (중복 제거)
        List<String> supplierCompanyIds = statementInfos.stream()
                .map(PurchaseStatementListItemInfoDto::getSupplierCompanyId)
                .distinct()
                .collect(Collectors.toList());

        SupplierCompaniesResponseDto supplierCompaniesResponse = supplierCompanyServicePort.getSupplierCompaniesByIds(supplierCompanyIds);

        Map<String, SupplierCompanyResponseDto> supplierCompanyMap = supplierCompaniesResponse.getSupplierCompanies().stream()
                .collect(Collectors.toMap(
                        SupplierCompanyResponseDto::getCompanyId,
                        company1 -> company1
                ));

        // 3. SCM에서 product order 정보 조회 (totalAmount, productOrderNumber)
        List<String> productOrderIds = statementInfos.stream()
                .map(PurchaseStatementListItemInfoDto::getProductOrderId)
                .distinct()
                .collect(Collectors.toList());

        List<ProductOrderInfosResponseDto.ProductOrderInfoItem> productOrderInfos = productOrderServicePort.getProductOrderInfosByIds(productOrderIds);

        Map<String, BigDecimal> productOrderTotalAmountMap = productOrderInfos.stream()
                .collect(Collectors.toMap(
                        ProductOrderInfosResponseDto.ProductOrderInfoItem::getProductOrderId,
                        ProductOrderInfosResponseDto.ProductOrderInfoItem::getTotalAmount
                ));

        Map<String, String> productOrderNumberMap = productOrderInfos.stream()
                .collect(Collectors.toMap(
                        ProductOrderInfosResponseDto.ProductOrderInfoItem::getProductOrderId,
                        ProductOrderInfosResponseDto.ProductOrderInfoItem::getProductOrderNumber
                ));

        // 4. DTO 조립
        List<PurchaseInvoiceListDto> content = statementInfos.stream()
                .map(info -> {
                    String supplierCompanyId = info.getSupplierCompanyId();
                    String productOrderId = info.getProductOrderId();

                    SupplierCompanyResponseDto supplierCompany = supplierCompanyMap.get(supplierCompanyId);
                    BigDecimal totalAmount = productOrderTotalAmountMap.get(productOrderId);
                    String productOrderNumber = productOrderNumberMap.get(productOrderId);

                    PurchaseStatementConnectionDto connection = new PurchaseStatementConnectionDto(
                            supplierCompany != null ? supplierCompany.getCompanyId() : null,
                            supplierCompany != null ? supplierCompany.getCompanyNumber() : null,
                            supplierCompany != null ? supplierCompany.getCompanyName() : null
                    );

                    PurchaseStatementReferenceDto reference = new PurchaseStatementReferenceDto(
                            info.getProductOrderId(),
                            productOrderNumber
                    );

                    return new PurchaseInvoiceListDto(
                            info.getInvoiceId(),
                            info.getInvoiceCode(),
                            connection,
                            totalAmount != null ? totalAmount : BigDecimal.ZERO,
                            info.getIssueDate(),
                            info.getDueDate(),
                            info.getStatus(),
                            productOrderNumber,
                            reference
                    );
                })
                .collect(Collectors.toList());

        // 5. company 필터 적용 (Service 레이어에서)
        if (company != null && !company.isBlank()) {
            String lowerCompany = company.toLowerCase();
            content = content.stream()
                    .filter(item -> item.getConnection().getConnectionName() != null &&
                            item.getConnection().getConnectionName().toLowerCase().contains(lowerCompany))
                    .collect(Collectors.toList());
        }

        log.info("매입전표 목록 조회 성공 - total: {}, size: {}", statementInfoPage.getTotalElements(), content.size());

        return new PageImpl<>(content, pageable, statementInfoPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseInvoiceListDto> getPurchaseStatementListBySupplierUserId(
            String supplierUserId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        log.info("Supplier User ID로 매입전표 목록 조회 요청 - supplierUserId: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                supplierUserId, startDate, endDate, pageable.getPageNumber(), pageable.getPageSize());

        // 1. SCM 서비스를 통해 supplierUserId로 supplierCompanyId 조회
        String supplierCompanyId = supplierCompanyServicePort.getSupplierCompanyIdByUserId(supplierUserId);
        log.info("Supplier Company ID 조회 성공 - supplierUserId: {}, supplierCompanyId: {}", supplierUserId, supplierCompanyId);

        // 2. DAO에서 해당 supplierCompanyId로 매입전표 목록 조회
        Page<PurchaseStatementListItemInfoDto> statementInfoPage = purchaseStatementDAO.findPurchaseStatementListBySupplierCompanyId(
                supplierCompanyId, startDate, endDate, pageable
        );

        List<PurchaseStatementListItemInfoDto> statementInfos = statementInfoPage.getContent();

        if (statementInfos.isEmpty()) {
            log.info("매입전표 목록 조회 완료 - 결과 없음 (supplierUserId: {})", supplierUserId);
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // 3. SCM에서 supplier company 정보 조회 (단일 조회)
        SupplierCompanyResponseDto supplierCompany = supplierCompanyServicePort.getSupplierCompanyById(supplierCompanyId);

        // 4. SCM에서 product order 정보 조회 (totalAmount, productOrderNumber)
        List<String> productOrderIds = statementInfos.stream()
                .map(PurchaseStatementListItemInfoDto::getProductOrderId)
                .distinct()
                .collect(Collectors.toList());

        List<ProductOrderInfosResponseDto.ProductOrderInfoItem> productOrderInfos = productOrderServicePort.getProductOrderInfosByIds(productOrderIds);

        Map<String, BigDecimal> productOrderTotalAmountMap = productOrderInfos.stream()
                .collect(Collectors.toMap(
                        ProductOrderInfosResponseDto.ProductOrderInfoItem::getProductOrderId,
                        ProductOrderInfosResponseDto.ProductOrderInfoItem::getTotalAmount
                ));

        Map<String, String> productOrderNumberMap = productOrderInfos.stream()
                .collect(Collectors.toMap(
                        ProductOrderInfosResponseDto.ProductOrderInfoItem::getProductOrderId,
                        ProductOrderInfosResponseDto.ProductOrderInfoItem::getProductOrderNumber
                ));

        // 5. DTO 조립
        List<PurchaseInvoiceListDto> content = statementInfos.stream()
                .map(info -> {
                    String productOrderId = info.getProductOrderId();
                    BigDecimal totalAmount = productOrderTotalAmountMap.get(productOrderId);
                    String productOrderNumber = productOrderNumberMap.get(productOrderId);

                    PurchaseStatementConnectionDto connection = new PurchaseStatementConnectionDto(
                            supplierCompany.getCompanyId(),
                            supplierCompany.getCompanyNumber(),
                            supplierCompany.getCompanyName()
                    );


                    PurchaseStatementReferenceDto reference = new PurchaseStatementReferenceDto(
                            info.getProductOrderId(),
                            productOrderNumber
                    );

                    return new PurchaseInvoiceListDto(
                            info.getInvoiceId(),
                            info.getInvoiceCode(),
                            connection,
                            totalAmount != null ? totalAmount : BigDecimal.ZERO,
                            info.getIssueDate(),
                            info.getDueDate(),
                            info.getStatus(),
                            productOrderNumber,
                            reference
                    );
                })
                .collect(Collectors.toList());

        log.info("Supplier User ID로 매입전표 목록 조회 성공 - supplierUserId: {}, total: {}, size: {}",
                supplierUserId, statementInfoPage.getTotalElements(), content.size());

        return new PageImpl<>(content, pageable, statementInfoPage.getTotalElements());
    }
}
