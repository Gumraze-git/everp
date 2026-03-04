package org.ever._4ever_be_business.fcm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.fcm.dto.request.APInvoiceSearchConditionDto;
import org.ever._4ever_be_business.fcm.dto.response.APInvoiceDetailDto;
import org.ever._4ever_be_business.fcm.dto.response.APInvoiceItemDto;
import org.ever._4ever_be_business.fcm.dto.response.APInvoiceListItemDto;
import org.ever._4ever_be_business.fcm.integration.dto.ProductOrderInfoResponseDto;
import org.ever._4ever_be_business.fcm.integration.dto.SupplierCompanyResponseDto;
import org.ever._4ever_be_business.fcm.integration.port.ProductOrderServicePort;
import org.ever._4ever_be_business.fcm.integration.port.SupplierCompanyServicePort;
import org.ever._4ever_be_business.fcm.service.APInvoiceService;
import org.ever._4ever_be_business.voucher.entity.PurchaseVoucher;
import org.ever._4ever_be_business.voucher.enums.PurchaseVoucherStatus;
import org.ever._4ever_be_business.voucher.enums.SalesVoucherStatus;
import org.ever._4ever_be_business.voucher.repository.PurchaseVoucherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class APInvoiceServiceImpl implements APInvoiceService {

    private final PurchaseVoucherRepository purchaseVoucherRepository;
    private final SupplierCompanyServicePort supplierCompanyServicePort;
    private final ProductOrderServicePort productOrderServicePort;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    @Transactional(readOnly = true)
    public Page<APInvoiceListItemDto> getAPInvoiceList(String company, LocalDate startDate, LocalDate endDate, int page, int size) {
        log.info("AP 전표 목록 조회 - company: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                company, startDate, endDate, page, size);

        // 검색 조건 생성
        APInvoiceSearchConditionDto condition = new APInvoiceSearchConditionDto(company,null, null, startDate, endDate);

        // 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 목록 조회
        Page<APInvoiceListItemDto> result = purchaseVoucherRepository.findAPInvoiceList(condition, pageable);

        // SCM에서 추가 정보 조회하여 채우기
        List<APInvoiceListItemDto> enrichedContent = enrichAPInvoiceList(result.getContent());
        Page<APInvoiceListItemDto> enrichedResult = new PageImpl<>(enrichedContent, pageable, result.getTotalElements());

        log.info("AP 전표 목록 조회 완료 - totalElements: {}, totalPages: {}",
                enrichedResult.getTotalElements(), enrichedResult.getTotalPages());

        return enrichedResult;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<APInvoiceListItemDto> getAPInvoiceListBySupplierCompanyId(String supplierUserId, String status, LocalDate startDate, LocalDate endDate, int page, int size) {
        log.info("SupplierCompanyId 기반 AP 전표 목록 조회 - supplierCompanyId: {}, status: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                supplierUserId, status, startDate, endDate, page, size);

        String supplierCompanyId = supplierCompanyServicePort.getSupplierCompanyIdByUserId(supplierUserId);

        // 검색 조건 생성 (supplierCompanyId 포함)
        APInvoiceSearchConditionDto condition = new APInvoiceSearchConditionDto(null, status, supplierCompanyId, startDate, endDate);

        // 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 목록 조회
        Page<APInvoiceListItemDto> result = purchaseVoucherRepository.findAPInvoiceList(condition, pageable);

        // SCM에서 추가 정보 조회하여 채우기
        List<APInvoiceListItemDto> enrichedContent = enrichAPInvoiceList(result.getContent());
        Page<APInvoiceListItemDto> enrichedResult = new PageImpl<>(enrichedContent, pageable, result.getTotalElements());

        log.info("SupplierCompanyId 기반 AP 전표 목록 조회 완료 - totalElements: {}, totalPages: {}",
                enrichedResult.getTotalElements(), enrichedResult.getTotalPages());

        return enrichedResult;
    }

    /**
     * AP 전표 목록에 SCM 서버로부터 추가 정보를 조회하여 새로운 리스트 반환
     */
    private List<APInvoiceListItemDto> enrichAPInvoiceList(List<APInvoiceListItemDto> invoices) {
        if (invoices == null || invoices.isEmpty()) {
            return invoices;
        }

        // 1. Supplier Company IDs 추출
        List<String> supplierCompanyIds = invoices.stream()
                .map(invoice -> invoice.getSupplier().getCompanyId())
                .distinct()
                .collect(Collectors.toList());

        // 2. Product Order IDs 추출
        List<String> productOrderIds = invoices.stream()
                .map(invoice -> invoice.getReference().getReferenceId())
                .distinct()
                .collect(Collectors.toList());

        // 3. SCM에서 Supplier Company 정보 일괄 조회
        var supplierCompaniesResponse = supplierCompanyServicePort.getSupplierCompaniesByIds(supplierCompanyIds);
        Map<String, org.ever._4ever_be_business.fcm.integration.dto.SupplierCompanyResponseDto> supplierMap =
                supplierCompaniesResponse.getSupplierCompanies().stream()
                .collect(Collectors.toMap(
                        org.ever._4ever_be_business.fcm.integration.dto.SupplierCompanyResponseDto::getCompanyId,
                        supplier -> supplier
                ));

        // 4. SCM에서 Product Order 정보 일괄 조회
        var productOrderInfos = productOrderServicePort.getProductOrderInfosByIds(productOrderIds);
        Map<String, org.ever._4ever_be_business.fcm.integration.dto.ProductOrderInfosResponseDto.ProductOrderInfoItem> productOrderMap =
                productOrderInfos.stream()
                .collect(Collectors.toMap(
                        org.ever._4ever_be_business.fcm.integration.dto.ProductOrderInfosResponseDto.ProductOrderInfoItem::getProductOrderId,
                        order -> order
                ));

        // 5. 각 invoice에 정보를 채운 새로운 객체 생성
        return invoices.stream()
                .map(invoice -> {
                    // Supplier 정보 가져오기
                    var supplier = supplierMap.get(invoice.getSupplier().getCompanyId());
                    var enrichedSupplier = supplier != null
                            ? new org.ever._4ever_be_business.fcm.dto.response.SupplierDto(
                                    supplier.getCompanyId(),
                                    supplier.getCompanyNumber(),
                                    supplier.getCompanyName())
                            : invoice.getSupplier();

                    // Product Order 정보 가져오기
                    var productOrder = productOrderMap.get(invoice.getReference().getReferenceId());
                    var enrichedReference = productOrder != null
                            ? new org.ever._4ever_be_business.fcm.dto.response.ReferenceDto(
                                    productOrder.getProductOrderId(),
                                    productOrder.getProductOrderNumber())
                            : invoice.getReference();

                    String enrichedReferenceNumber = productOrder != null
                            ? productOrder.getProductOrderNumber()
                            : invoice.getReferenceNumber();

                    // 새로운 APInvoiceListItemDto 생성
                    return new APInvoiceListItemDto(
                            invoice.getInvoiceId(),
                            invoice.getInvoiceNumber(),
                            enrichedSupplier,
                            invoice.getTotalAmount(),
                            invoice.getIssueDate(),
                            invoice.getDueDate(),
                            invoice.getStatusCode(),
                            enrichedReferenceNumber,
                            enrichedReference
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public APInvoiceDetailDto getAPInvoiceDetail(String invoiceId) {
        log.info("AP 전표 상세 정보 조회 - invoiceId: {}", invoiceId);

        // 1. PurchaseVoucher 조회
        PurchaseVoucher voucher = purchaseVoucherRepository.findById(invoiceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "존재하지 않는 전표입니다."));

        // 2. SCM에서 Supplier Company 정보 조회
        SupplierCompanyResponseDto supplierCompany = supplierCompanyServicePort
                .getSupplierCompanyById(String.valueOf(voucher.getSupplierCompanyId()));

        // 3. SCM에서 Product Order 정보 조회
        ProductOrderInfoResponseDto productOrderInfo = productOrderServicePort
                .getProductOrderItemsById(voucher.getProductOrderId());

        // 4. Items 변환
        List<APInvoiceItemDto> items = productOrderInfo.getItems().stream()
                .map(item -> new APInvoiceItemDto(
                        item.getItemId(),
                        item.getItemName(),
                        item.getQuantity(),
                        item.getUomName(),
                        item.getUnitPrice(),
                        item.getTotalPrice()
                ))
                .collect(Collectors.toList());

        // 5. APInvoiceDetailDto 생성
        APInvoiceDetailDto result = new APInvoiceDetailDto(
                voucher.getId(),
                voucher.getVoucherCode(),
                "AP",
                voucher.getStatus().name(),
                voucher.getIssueDate().format(DATE_FORMATTER),
                voucher.getDueDate().format(DATE_FORMATTER),
                supplierCompany.getCompanyName(),
                productOrderInfo.getProductOrderNumber(),
                voucher.getTotalAmount(),
                voucher.getMemo(),
                items
        );

        log.info("AP 전표 상세 정보 조회 성공 - invoiceId: {}, invoiceNumber: {}",
                invoiceId, voucher.getVoucherCode());

        return result;
    }

    @Override
    @Transactional
    public void completePayable(String invoiceId) {
        log.info("AP 전표 미지급 처리 완료 - invoiceId: {}", invoiceId);

        // 1. PurchaseVoucher 조회
        PurchaseVoucher voucher = purchaseVoucherRepository.findById(invoiceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "존재하지 않는 전표입니다."));

        if(voucher.getStatus() == PurchaseVoucherStatus.PENDING){
            // 2. 상태를 PAID로 변경
            voucher.updateStatus(org.ever._4ever_be_business.voucher.enums.PurchaseVoucherStatus.PAID);
        }
        else{
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "대기 상태만 수정 가능합니다");
        }

        // 3. 저장
        purchaseVoucherRepository.save(voucher);

        log.info("AP 전표 미지급 처리 완료 성공 - invoiceId: {}, status: PAID", invoiceId);
    }
}
