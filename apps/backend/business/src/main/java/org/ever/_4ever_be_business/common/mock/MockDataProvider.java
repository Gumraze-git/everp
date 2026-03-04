package org.ever._4ever_be_business.common.mock;

import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.integration.dto.*;
import org.ever._4ever_be_business.hr.dto.response.UserInfoResponse;
import org.ever._4ever_be_business.sd.dto.response.InventoryCheckResponseDto;
import org.ever._4ever_be_business.sd.integration.dto.ProductInfoResponseDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mock 데이터를 제공하는 컴포넌트
 * dev 프로파일에서만 활성화
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "external.mock.enabled", havingValue = "true", matchIfMissing = true)
public class MockDataProvider {

    /**
     * Mock 주문 아이템 생성
     */
    public OrderItemsResponseDto createMockOrderItems(String orderId) {
        log.info("[MOCK] 주문 아이템 생성 - orderId: {}", orderId);
        // 실제 DTO 구조에 맞게 빈 리스트 또는 기본 데이터 반환
        return new OrderItemsResponseDto(orderId, "MOCK-" + orderId, new ArrayList<>());
    }

    /**
     * Mock 재고 확인 응답 생성
     */
    public InventoryCheckResponseDto createMockInventoryCheck(List<String> productIds) {
        log.info("[MOCK] 재고 확인 응답 생성 - productIds count: {}", productIds.size());
        // 실제 DTO 구조에 맞게 빈 리스트 또는 기본 데이터 반환
        return new InventoryCheckResponseDto(new ArrayList<>());
    }

    /**
     * Mock 제품 정보 응답 생성
     */
    public ProductInfoResponseDto createMockProductInfo(List<String> productIds) {
        log.info("[MOCK] 제품 정보 응답 생성 - productIds count: {}", productIds.size());
        // 실제 DTO 구조에 맞게 빈 리스트 또는 기본 데이터 반환
        return new ProductInfoResponseDto(new ArrayList<>());
    }

    /**
     * Mock 생산 주문 아이템 생성
     */
    public ProductOrderInfoResponseDto createMockProductOrderItems(String productOrderId) {
        log.info("[MOCK] 생산 주문 아이템 생성 - productOrderId: {}", productOrderId);
        // 실제 DTO 구조에 맞게 기본 데이터 반환
        return new ProductOrderInfoResponseDto(new ArrayList<>(), BigDecimal.ZERO, "po" + productOrderId);
    }

    /**
     * Mock 생산 주문 정보 목록 생성
     */
    public List<ProductOrderInfosResponseDto.ProductOrderInfoItem> createMockProductOrderInfos(List<String> productOrderIds) {
        log.info("[MOCK] 생산 주문 정보 목록 생성 - productOrderIds count: {}", productOrderIds.size());

        return productOrderIds.stream()
                .map(id -> new ProductOrderInfosResponseDto.ProductOrderInfoItem(
                        id,
                        "po" + id,
                        BigDecimal.valueOf(100000)
                ))
                .collect(Collectors.toList());
    }

    /**
     * Mock 공급업체 정보 생성
     */
    public SupplierCompanyResponseDto createMockSupplierCompany(String supplierCompanyId) {
        log.info("[MOCK] 공급업체 정보 생성 - supplierCompanyId: {}", supplierCompanyId);

        return new SupplierCompanyResponseDto(
                supplierCompanyId,                          // companyId
                "SUPP-" + supplierCompanyId,               // companyNumber
                "Mock공급업체-" + supplierCompanyId,        // companyName
                "서울시 강남구",                            // baseAddress
                "테헤란로 123",                             // detailAddress
                "제조업",                                   // category
                "02-1234-5678",                            // officePhone
                "manager-" + supplierCompanyId             // managerId
        );
    }

    /**
     * Mock 공급업체 목록 생성
     */
    public SupplierCompaniesResponseDto createMockSupplierCompanies(List<String> supplierCompanyIds) {
        log.info("[MOCK] 공급업체 목록 생성 - supplierCompanyIds count: {}", supplierCompanyIds.size());
        // 실제 DTO 구조에 맞게 빈 리스트 또는 기본 데이터 반환
        return new SupplierCompaniesResponseDto(new ArrayList<>());
    }

    /**
     * Mock 사용자 정보 생성
     */
    public UserInfoResponse createMockUserInfo(List<Long> internelUserIds) {
        log.info("[MOCK] 사용자 정보 생성 - internelUserIds count: {}", internelUserIds.size());
        // 실제 DTO 구조에 맞게 빈 리스트 또는 기본 데이터 반환
        return new UserInfoResponse();
    }
}
