package org.ever._4ever_be_scm.common.mock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class VehiclePartsMockCatalogTest {

    @Test
    void purchaseRequestScenarioUsesDeterministicRealPrefix() {
        var scenario = VehiclePartsMockCatalog.purchaseRequestScenario(0);

        assertThat(scenario.itemId()).isEqualTo("mm-pr-001");
        assertThat(scenario.itemNumber()).startsWith("PR-");
        assertThat(scenario.itemTitle()).doesNotContain("목업").doesNotContain("MOCK");
        assertThat(scenario.name()).doesNotContain("목업").doesNotContain("MOCK");
    }

    @Test
    void mesScenarioUsesManufacturingToneWithoutMockLabel() {
        var scenario = VehiclePartsMockCatalog.mesScenario(1);

        assertThat(scenario.itemNumber()).startsWith("MES-");
        assertThat(scenario.itemTitle()).contains("생산").doesNotContain("목업").doesNotContain("MOCK");
        assertThat(scenario.statusCode()).isIn("IN_PROGRESS", "PENDING", "COMPLETED");
    }
}
