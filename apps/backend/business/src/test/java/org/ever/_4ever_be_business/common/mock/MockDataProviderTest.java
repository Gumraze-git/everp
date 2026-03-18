package org.ever._4ever_be_business.common.mock;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.ever._4ever_be_business.fcm.integration.dto.ProductMultipleResponseDto;
import org.ever._4ever_be_business.sd.dto.request.InventoryCheckItemDto;
import org.ever._4ever_be_business.sd.integration.dto.ProductInfoResponseDto;
import org.junit.jupiter.api.Test;

class MockDataProviderTest {

    private final MockDataProvider mockDataProvider = new MockDataProvider();

    @Test
    void createMockProductInfoPreservesRequestedIdsAndBuildsDisplayFields() {
        ProductInfoResponseDto response = mockDataProvider.createMockProductInfo(List.of("item-alpha", "item-beta", "item-alpha"));

        assertThat(response.getProducts()).hasSize(2);
        assertThat(response.getProducts())
                .extracting(ProductInfoResponseDto.ProductDto::getProductId)
                .containsExactly("item-alpha", "item-beta");
        assertThat(response.getProducts())
                .extracting(ProductInfoResponseDto.ProductDto::getProductName)
                .allMatch(name -> name != null && !name.isBlank() && !name.contains("목업") && !name.contains("MOCK"));
        assertThat(response.getProducts())
                .extracting(ProductInfoResponseDto.ProductDto::getProductCode)
                .allMatch(code -> code != null && code.startsWith("MAT-") && !code.contains("MOCK"));
        assertThat(response.getProducts())
                .extracting(ProductInfoResponseDto.ProductDto::getUnitPrice)
                .allMatch(price -> price != null && price.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void createMockInventoryCheckReflectsRequestedQuantityAndShortageConsistency() {
        var response = mockDataProvider.createMockInventoryCheck(List.of(
                new InventoryCheckItemDto("item-alpha", 7),
                new InventoryCheckItemDto("item-beta", 3)
        ));

        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getItems())
                .extracting(item -> item.getItemId(), item -> item.getRequiredQuantity())
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("item-alpha", 7),
                        org.assertj.core.groups.Tuple.tuple("item-beta", 3)
                );
        assertThat(response.getItems()).allSatisfy(item -> {
            assertThat(item.getShortageQuantity()).isEqualTo(Math.max(0, item.getRequiredQuantity() - item.getInventoryQuantity()));
            assertThat(item.getProductionRequired()).isEqualTo(item.getShortageQuantity() > 0);
            assertThat(item.getStatusCode()).isIn("AVAILABLE", "SHORTAGE");
        });
    }

    @Test
    void createMockProductsMultipleSharesSameContractForFcmConsumers() {
        ProductMultipleResponseDto response = mockDataProvider.createMockProductsMultiple(List.of("item-alpha", "item-beta"));

        assertThat(response.getProducts()).hasSize(2);
        assertThat(response.getProducts())
                .extracting(ProductMultipleResponseDto.ProductDto::getItemId)
                .containsExactly("item-alpha", "item-beta");
        assertThat(response.getProducts())
                .extracting(ProductMultipleResponseDto.ProductDto::getItemName)
                .allMatch(name -> name != null && !name.isBlank() && !name.contains("목업") && !name.contains("MOCK"));
    }

    @Test
    void createMockSupplierCompanyUsesVehiclePartsNamingPolicy() {
        var response = mockDataProvider.createMockSupplierCompany("supplier-company-8B48D2");

        assertThat(response.getCompanyId()).isEqualTo("supplier-company-8B48D2");
        assertThat(response.getCompanyNumber()).startsWith("SUP-");
        assertThat(response.getCompanyName()).doesNotContain("목업").doesNotContain("MOCK");
        assertThat(response.getOfficePhone()).startsWith("02-");
    }
}
