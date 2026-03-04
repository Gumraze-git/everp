package org.ever._4ever_be_business.fcm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.fcm.dao.SalesStatementDAO;
import org.ever._4ever_be_business.fcm.dto.response.SalesStatementDetailDto;
import org.ever._4ever_be_business.fcm.dto.response.SalesStatementItemDto;
import org.ever._4ever_be_business.fcm.dto.response.SalesStatementListItemDto;
import org.ever._4ever_be_business.fcm.service.SalesStatementService;
import org.ever._4ever_be_business.sd.integration.dto.ProductInfoResponseDto;
import org.ever._4ever_be_business.sd.integration.port.ProductServicePort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesStatementServiceImpl implements SalesStatementService {

    private final SalesStatementDAO salesStatementDAO;
    private final ProductServicePort productServicePort;

    @Override
    @Transactional(readOnly = true)
    public SalesStatementDetailDto getSalesStatementDetail(String statementId) {
        log.info("매출전표 상세 정보 조회 요청 - statementId: {}", statementId);

        // 1. DAO에서 기본 정보 조회 (productName 제외)
        SalesStatementDetailDto statementDetail = salesStatementDAO.findSalesStatementDetailById(statementId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "매출전표 정보를 찾을 수 없습니다."));

        // 2. productId 리스트 추출
        List<String> productIds = statementDetail.getItems().stream()
                .map(SalesStatementItemDto::getItemId)
                .collect(Collectors.toList());

        if (!productIds.isEmpty()) {
            // 3. SCM 서버에서 Product 정보 조회
            ProductInfoResponseDto productInfoResponse = productServicePort.getProductsByIds(productIds);

            // 4. productId -> productName 맵 생성
            Map<String, String> productNameMap = productInfoResponse.getProducts().stream()
                    .collect(Collectors.toMap(
                            ProductInfoResponseDto.ProductDto::getProductId,
                            ProductInfoResponseDto.ProductDto::getProductName
                    ));

            // 5. items에 productName 채우기
            List<SalesStatementItemDto> itemsWithName = statementDetail.getItems().stream()
                    .map(item -> new SalesStatementItemDto(
                            item.getItemId(),
                            productNameMap.getOrDefault(item.getItemId(), "Unknown Product"),
                            item.getQuantity(),
                            item.getUomName(),
                            item.getUnitPrice(),
                            item.getTotalPrice()
                    ))
                    .toList();

            // 6. DTO 재조립
            statementDetail = new SalesStatementDetailDto(
                    statementDetail.getInvoiceId(),
                    statementDetail.getInvoiceCode(),
                    statementDetail.getInvoiceType(),
                    statementDetail.getStatusCode(),
                    statementDetail.getIssueDate(),
                    statementDetail.getDueDate(),
                    statementDetail.getName(),
                    statementDetail.getReferenceCode(),
                    itemsWithName,
                    statementDetail.getTotalAmount(),
                    statementDetail.getNote()
            );
        }

        log.info("매출전표 상세 정보 조회 성공 - statementId: {}, invoiceCode: {}",
                statementId, statementDetail.getInvoiceCode());

        return statementDetail;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalesStatementListItemDto> getSalesStatementList(String company, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        log.info("매출전표 목록 조회 요청 - company: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                company, startDate, endDate, pageable.getPageNumber(), pageable.getPageSize());

        Page<SalesStatementListItemDto> result = salesStatementDAO.findSalesStatementList(company, startDate, endDate, pageable);

        log.info("매출전표 목록 조회 성공 - total: {}, size: {}",
                result.getTotalElements(), result.getContent().size());

        return result;
    }
}
