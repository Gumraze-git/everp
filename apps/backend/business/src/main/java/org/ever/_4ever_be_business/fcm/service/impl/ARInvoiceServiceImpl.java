package org.ever._4ever_be_business.fcm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.fcm.dto.request.ARInvoiceSearchConditionDto;
import org.ever._4ever_be_business.fcm.dto.response.ARInvoiceDetailDto;
import org.ever._4ever_be_business.fcm.dto.response.ARInvoiceItemDto;
import org.ever._4ever_be_business.fcm.dto.response.ARInvoiceListItemDto;
import org.ever._4ever_be_business.fcm.integration.dto.ProductMultipleResponseDto;
import org.ever._4ever_be_business.fcm.integration.port.ProductsServicePort;
import org.ever._4ever_be_business.fcm.service.ARInvoiceService;
import org.ever._4ever_be_business.order.entity.OrderItem;
import org.ever._4ever_be_business.order.repository.OrderItemRepository;
import org.ever._4ever_be_business.voucher.entity.SalesVoucher;
import org.ever._4ever_be_business.voucher.enums.SalesVoucherStatus;
import org.ever._4ever_be_business.voucher.repository.SalesVoucherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ARInvoiceServiceImpl implements ARInvoiceService {

    private final SalesVoucherRepository salesVoucherRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductsServicePort productServicePort;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    @Transactional(readOnly = true)
    public Page<ARInvoiceListItemDto> getARInvoiceList(String company, String status, LocalDate startDate, LocalDate endDate, int page, int size) {
        log.info("AR 전표 목록 조회 - company: {}, status: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                company, status, startDate, endDate, page, size);

        // 검색 조건 생성
        ARInvoiceSearchConditionDto condition = new ARInvoiceSearchConditionDto(company, status, null, startDate, endDate);

        // 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 목록 조회
        Page<ARInvoiceListItemDto> result = salesVoucherRepository.findARInvoiceList(condition, pageable);

        log.info("AR 전표 목록 조회 완료 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ARInvoiceListItemDto> getARInvoiceListByCustomerUserId(String customerUserId, String status, LocalDate startDate, LocalDate endDate, int page, int size) {
        log.info("CustomerUserId 기반 AR 전표 목록 조회 - customerUserId: {}, status: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                customerUserId, status, startDate, endDate, page, size);

        // 검색 조건 생성 (customerUserId 포함)
        ARInvoiceSearchConditionDto condition = new ARInvoiceSearchConditionDto(null, status, customerUserId, startDate, endDate);

        // 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 목록 조회
        Page<ARInvoiceListItemDto> result = salesVoucherRepository.findARInvoiceList(condition, pageable);

        log.info("CustomerUserId 기반 AR 전표 목록 조회 완료 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ARInvoiceDetailDto getARInvoiceDetail(String invoiceId) {
        log.info("AR 전표 상세 정보 조회 - invoiceId: {}", invoiceId);

        // 1. SalesVoucher 조회
        SalesVoucher voucher = salesVoucherRepository.findById(invoiceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "존재하지 않는 전표입니다."));

        // 2. Business 서버의 OrderItem 조회
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(voucher.getOrder().getId());
        log.info("OrderItem 조회 완료 - orderId: {}, itemCount: {}",
                voucher.getOrder().getId(), orderItems.size());

        // 3. OrderItem에서 productId 추출
        List<String> productIds = orderItems.stream()
                .map(OrderItem::getProductId)
                .collect(Collectors.toList());

        // 4. SCM에서 Product 정보만 조회 (itemName, uomName)
        ProductMultipleResponseDto productInfo = productServicePort.getProductsMultiple(productIds);

        // 5. Product 정보를 Map으로 변환 (productId -> ProductDto)
        Map<String, ProductMultipleResponseDto.ProductDto> productMap = productInfo.getProducts().stream()
                .collect(Collectors.toMap(
                        ProductMultipleResponseDto.ProductDto::getItemId,
                        product -> product
                ));

        // 6. OrderItem과 Product 정보를 조합하여 ARInvoiceItemDto 생성
        List<ARInvoiceItemDto> items = orderItems.stream()
                .map(orderItem -> {
                    ProductMultipleResponseDto.ProductDto product = productMap.get(orderItem.getProductId());
                    String itemName = product != null ? product.getItemName() : "Unknown Product";
                    String uomName = product != null ? product.getUomName() : "EA";

                    return new ARInvoiceItemDto(
                            orderItem.getProductId(),
                            itemName,
                            orderItem.getCount().intValue(),
                            uomName,
                            BigDecimal.valueOf(orderItem.getPrice()),
                            BigDecimal.valueOf(orderItem.getPrice() * orderItem.getCount())
                    );
                })
                .collect(Collectors.toList());

        // 7. ARInvoiceDetailDto 생성
        ARInvoiceDetailDto result = new ARInvoiceDetailDto(
                voucher.getId(),
                voucher.getVoucherCode(),
                "AR",
                voucher.getStatus().name(),
                voucher.getIssueDate().format(DATE_FORMATTER),
                voucher.getDueDate().format(DATE_FORMATTER),
                voucher.getCustomerCompany().getCompanyName(),
                voucher.getOrder().getOrderCode(),
                voucher.getTotalAmount(),
                voucher.getMemo(),
                items
        );

        log.info("AR 전표 상세 정보 조회 성공 - invoiceId: {}, invoiceNumber: {}, itemCount: {}",
                invoiceId, voucher.getVoucherCode(), items.size());

        return result;
    }

    @Override
    @Transactional
    public void updateARInvoice(String invoiceId, String status, String dueDate, String memo) {
        log.info("AR 전표 정보 업데이트 - invoiceId: {}, status: {}, dueDate: {}, memo: {}",
                invoiceId, status, dueDate, memo);

        // 1. SalesVoucher 조회
        SalesVoucher voucher = salesVoucherRepository.findById(invoiceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "존재하지 않는 전표입니다."));

        // 2. 파라미터 변환
        SalesVoucherStatus newStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                newStatus = SalesVoucherStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "유효하지 않은 상태 코드입니다: " + status);
            }
        }

        LocalDateTime newDueDate = null;
        if (dueDate != null && !dueDate.isBlank()) {
            try {
                newDueDate = LocalDate.parse(dueDate, DATE_FORMATTER).atStartOfDay();
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "유효하지 않은 날짜 형식입니다: " + dueDate);
            }
        }

        // 3. 엔티티 업데이트
        voucher.updateARInvoice(newStatus, newDueDate, memo);

        // 4. 저장 (더티 체킹으로 자동 저장되지만 명시적으로 호출)
        salesVoucherRepository.save(voucher);

        log.info("AR 전표 정보 업데이트 완료 - invoiceId: {}", invoiceId);
    }

    @Override
    @Transactional
    public void completeReceivable(String invoiceId) {
        log.info("AR 전표 미수 처리 완료 - invoiceId: {}", invoiceId);

        // 1. SalesVoucher 조회
        SalesVoucher voucher = salesVoucherRepository.findById(invoiceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "존재하지 않는 전표입니다."));

        if(voucher.getStatus() == SalesVoucherStatus.PENDING){
            // 2. 상태를 PAID로 변경
            voucher.updateStatus(SalesVoucherStatus.PAID);
        }
        else{
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "대기상태만 수정 가능합니다");
        }


        // 3. 저장
        salesVoucherRepository.save(voucher);

        log.info("AR 전표 미수 처리 완료 성공 - invoiceId: {}, status: PAID", invoiceId);
    }
}
