package org.ever._4ever_be_business.common.mock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class VehiclePartsMockCatalogTest {

    @Test
    void workflowCodesAreDeterministicAndUseRealPrefixes() {
        var first = VehiclePartsMockCatalog.salesOrderScenario(0);
        var second = VehiclePartsMockCatalog.salesOrderScenario(0);

        assertThat(first.itemId()).isEqualTo(second.itemId());
        assertThat(first.itemNumber()).isEqualTo(second.itemNumber());
        assertThat(first.itemNumber()).startsWith("OR-");
        assertThat(first.itemTitle()).doesNotContain("목업").doesNotContain("MOCK");
        assertThat(first.name()).doesNotContain("목업").doesNotContain("MOCK");
    }

    @Test
    void productScenarioUsesVehiclePartsCatalog() {
        var product = VehiclePartsMockCatalog.productScenario("product-alpha");

        assertThat(product.productCode()).startsWith("MAT-");
        assertThat(product.productName()).doesNotContain("목업").doesNotContain("MOCK");
        assertThat(product.supplierName()).doesNotContain("목업").doesNotContain("MOCK");
        assertThat(product.unitPrice()).isPositive();
    }

    @Test
    void supplierCompanyScenarioProducesStableCompanyIdentity() {
        var company = VehiclePartsMockCatalog.supplierCompanyScenario("supplier-company-7F00AB");

        assertThat(company.companyId()).isEqualTo("supplier-company-7F00AB");
        assertThat(company.companyCode()).startsWith("SUP-");
        assertThat(company.companyName()).doesNotContain("목업").doesNotContain("MOCK");
        assertThat(VehiclePartsMockCatalog.syntheticSupplierCompanyId("supplier-user-a"))
                .startsWith("supplier-company-");
    }
}
