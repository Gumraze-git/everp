package org.ever._4ever_be_business.common.mock;

import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.integration.dto.*;
import org.ever._4ever_be_business.hr.dto.response.UserInfoResponse;
import org.ever._4ever_be_business.sd.dto.request.InventoryCheckItemDto;
import org.ever._4ever_be_business.sd.dto.response.InventoryCheckResponseDto;
import org.ever._4ever_be_business.sd.dto.response.InventoryCheckResultItemDto;
import org.ever._4ever_be_business.sd.integration.dto.ProductInfoResponseDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

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

        String safeOrderId = defaultId(orderId, "order");
        List<OrderItemsResponseDto.OrderItemDto> items = List.of(
                buildOrderItem(safeOrderId, 0),
                buildOrderItem(safeOrderId, 1)
        );

        return new OrderItemsResponseDto(safeOrderId, "OR-" + shortCode(safeOrderId), items);
    }

    /**
     * Mock 재고 확인 응답 생성
     */
    public InventoryCheckResponseDto createMockInventoryCheck(List<InventoryCheckItemDto> requestItems) {
        List<InventoryCheckItemDto> safeItems = requestItems == null ? List.of() : requestItems;
        log.info("[MOCK] 재고 확인 응답 생성 - productIds count: {}", safeItems.size());

        List<InventoryCheckResultItemDto> items = safeItems.stream()
                .map(this::buildInventoryItem)
                .toList();

        return new InventoryCheckResponseDto(items);
    }

    /**
     * Mock 제품 정보 응답 생성
     */
    public ProductInfoResponseDto createMockProductInfo(List<String> productIds) {
        List<String> safeProductIds = distinctNonBlankIds(productIds);
        log.info("[MOCK] 제품 정보 응답 생성 - productIds count: {}", safeProductIds.size());

        List<ProductInfoResponseDto.ProductDto> products = safeProductIds.stream()
                .map(this::buildSdProduct)
                .toList();

        return new ProductInfoResponseDto(products);
    }

    public ProductMultipleResponseDto createMockProductsMultiple(List<String> productIds) {
        List<String> safeProductIds = distinctNonBlankIds(productIds);
        log.info("[MOCK] FCM 제품 정보 응답 생성 - productIds count: {}", safeProductIds.size());

        List<ProductMultipleResponseDto.ProductDto> products = safeProductIds.stream()
                .map(this::buildFcmProduct)
                .toList();

        return new ProductMultipleResponseDto(products);
    }

    /**
     * Mock 생산 주문 아이템 생성
     */
    public ProductOrderInfoResponseDto createMockProductOrderItems(String productOrderId) {
        log.info("[MOCK] 생산 주문 아이템 생성 - productOrderId: {}", productOrderId);

        String safeProductOrderId = defaultId(productOrderId, "product-order");
        List<ProductOrderInfoResponseDto.ProductOrderItemDto> items = buildProductOrderItems(safeProductOrderId);
        BigDecimal totalPrice = items.stream()
                .map(ProductOrderInfoResponseDto.ProductOrderItemDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ProductOrderInfoResponseDto(items, totalPrice, "PO-" + shortCode(safeProductOrderId));
    }

    /**
     * Mock 생산 주문 정보 목록 생성
     */
    public List<ProductOrderInfosResponseDto.ProductOrderInfoItem> createMockProductOrderInfos(List<String> productOrderIds) {
        List<String> safeProductOrderIds = distinctNonBlankIds(productOrderIds);
        log.info("[MOCK] 생산 주문 정보 목록 생성 - productOrderIds count: {}", safeProductOrderIds.size());

        return safeProductOrderIds.stream()
                .map(this::buildProductOrderSummary)
                .toList();
    }

    /**
     * Mock 공급업체 정보 생성
     */
    public SupplierCompanyResponseDto createMockSupplierCompany(String supplierCompanyId) {
        log.info("[MOCK] 공급업체 정보 생성 - supplierCompanyId: {}", supplierCompanyId);
        return buildSupplierCompany(defaultId(supplierCompanyId, "supplier-company"));
    }

    /**
     * Mock 공급업체 목록 생성
     */
    public SupplierCompaniesResponseDto createMockSupplierCompanies(List<String> supplierCompanyIds) {
        List<String> safeSupplierCompanyIds = distinctNonBlankIds(supplierCompanyIds);
        log.info("[MOCK] 공급업체 목록 생성 - supplierCompanyIds count: {}", safeSupplierCompanyIds.size());

        List<SupplierCompanyResponseDto> supplierCompanies = safeSupplierCompanyIds.stream()
                .map(this::buildSupplierCompany)
                .toList();

        return new SupplierCompaniesResponseDto(supplierCompanies);
    }

    public String createMockSupplierCompanyId(String supplierUserId) {
        return VehiclePartsMockCatalog.syntheticSupplierCompanyId(defaultId(supplierUserId, "supplier-user"));
    }

    /**
     * Mock 사용자 정보 생성
     */
    public UserInfoResponse createMockUserInfo(List<Long> internelUserIds) {
        int size = internelUserIds == null ? 0 : internelUserIds.size();
        log.info("[MOCK] 사용자 정보 생성 - internelUserIds count: {}", size);
        // 실제 DTO 구조에 맞게 빈 리스트 또는 기본 데이터 반환
        return new UserInfoResponse();
    }

    private InventoryCheckResultItemDto buildInventoryItem(InventoryCheckItemDto requestItem) {
        String itemId = defaultId(requestItem != null ? requestItem.getItemId() : null, "inventory-item");
        MockProduct product = buildMockProduct(itemId);
        int requiredQuantity = sanitizeQuantity(requestItem != null ? requestItem.getRequiredQuantity() : null, itemId);
        int inventoryQuantity = Math.max(0, requiredQuantity + Math.floorMod(positiveHash(itemId) / 17, 8) - 2);
        int shortageQuantity = Math.max(0, requiredQuantity - inventoryQuantity);

        return new InventoryCheckResultItemDto(
                itemId,
                product.productName(),
                requiredQuantity,
                inventoryQuantity,
                shortageQuantity,
                shortageQuantity > 0 ? "SHORTAGE" : "AVAILABLE",
                shortageQuantity > 0
        );
    }

    private ProductInfoResponseDto.ProductDto buildSdProduct(String productId) {
        MockProduct product = buildMockProduct(productId);
        return new ProductInfoResponseDto.ProductDto(
                product.productId(),
                product.productCode(),
                product.productName(),
                product.uomName(),
                product.unitPrice()
        );
    }

    private ProductMultipleResponseDto.ProductDto buildFcmProduct(String productId) {
        MockProduct product = buildMockProduct(productId);
        return new ProductMultipleResponseDto.ProductDto(
                product.productId(),
                product.productCode(),
                product.productName(),
                product.uomName(),
                product.unitPrice()
        );
    }

    private List<ProductOrderInfoResponseDto.ProductOrderItemDto> buildProductOrderItems(String productOrderId) {
        return List.of(
                buildProductOrderItem(productOrderId, 0),
                buildProductOrderItem(productOrderId, 1)
        );
    }

    private ProductOrderInfoResponseDto.ProductOrderItemDto buildProductOrderItem(String productOrderId, int offset) {
        String itemId = productOrderId + "-item-" + (offset + 1);
        MockProduct product = buildMockProduct(itemId);
        int quantity = 2 + Math.floorMod(positiveHash(productOrderId) + (offset * 3), 6);
        BigDecimal totalPrice = product.unitPrice().multiply(BigDecimal.valueOf(quantity));

        return new ProductOrderInfoResponseDto.ProductOrderItemDto(
                itemId,
                product.productName(),
                quantity,
                product.uomName(),
                product.unitPrice(),
                totalPrice
        );
    }

    private ProductOrderInfosResponseDto.ProductOrderInfoItem buildProductOrderSummary(String productOrderId) {
        List<ProductOrderInfoResponseDto.ProductOrderItemDto> items = buildProductOrderItems(productOrderId);
        BigDecimal totalAmount = items.stream()
                .map(ProductOrderInfoResponseDto.ProductOrderItemDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ProductOrderInfosResponseDto.ProductOrderInfoItem(
                productOrderId,
                "PO-" + shortCode(productOrderId),
                totalAmount
        );
    }

    private OrderItemsResponseDto.OrderItemDto buildOrderItem(String orderId, int offset) {
        String itemId = orderId + "-item-" + (offset + 1);
        MockProduct product = buildMockProduct(itemId);
        int quantity = 1 + Math.floorMod(positiveHash(orderId) + offset, 5);
        BigDecimal totalPrice = product.unitPrice().multiply(BigDecimal.valueOf(quantity));

        return new OrderItemsResponseDto.OrderItemDto(
                itemId,
                product.productName(),
                quantity,
                product.uomName(),
                product.unitPrice(),
                totalPrice
        );
    }

    private SupplierCompanyResponseDto buildSupplierCompany(String supplierCompanyId) {
        var scenario = VehiclePartsMockCatalog.supplierCompanyScenario(supplierCompanyId);

        return new SupplierCompanyResponseDto(
                scenario.companyId(),
                scenario.companyCode(),
                scenario.companyName(),
                scenario.baseAddress(),
                scenario.detailAddress(),
                scenario.category(),
                scenario.officePhone(),
                scenario.managerId()
        );
    }

    private MockProduct buildMockProduct(String productId) {
        String safeProductId = defaultId(productId, "item");
        var scenario = VehiclePartsMockCatalog.productScenario(safeProductId);

        return new MockProduct(
                scenario.productId(),
                scenario.productCode(),
                scenario.productName(),
                scenario.uomName(),
                scenario.unitPrice()
        );
    }

    private List<String> distinctNonBlankIds(List<String> ids) {
        if (ids == null) {
            return List.of();
        }
        return ids.stream()
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
    }

    private String defaultId(String rawValue, String prefix) {
        if (StringUtils.hasText(rawValue)) {
            return rawValue;
        }
        return "mock-" + prefix;
    }

    private String shortCode(String value) {
        String compact = value.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
        if (compact.isBlank()) {
            compact = "000000";
        }
        return compact.length() <= 6 ? compact : compact.substring(compact.length() - 6);
    }

    private int sanitizeQuantity(Integer requestedQuantity, String itemId) {
        if (requestedQuantity != null && requestedQuantity > 0) {
            return requestedQuantity;
        }
        return 1 + Math.floorMod(positiveHash(itemId), 6);
    }

    private int positiveHash(String value) {
        return Math.floorMod(StringUtils.hasText(value) ? value.hashCode() : 0, Integer.MAX_VALUE);
    }

    private record MockProduct(
            String productId,
            String productCode,
            String productName,
            String uomName,
            BigDecimal unitPrice
    ) {}
}
