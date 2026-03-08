package org.ever._4ever_be_gw.common.dto.stats;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class StatsResponseMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void fromJsonBuildsMetricsByPeriod() throws Exception {
        String json = """
            {
              "week": {
                "orderCount": {
                  "value": 12,
                  "delta_rate": 0.15
                }
              },
              "month": {
                "purchaseOrderAmount": {
                  "value": 1200,
                  "delta_rate": 0.25
                }
              }
            }
            """;

        StatsResponseDto<StatsMetricsDto> response = StatsResponseMapper.fromJson(objectMapper.readTree(json));

        assertThat(response.getWeek().toMap().get("orderCount").getValue()).isEqualTo(12L);
        assertThat(response.getWeek().toMap().get("orderCount").getDeltaRate()).hasToString("0.15");
        assertThat(response.getMonth().toMap().get("purchaseOrderAmount").getValue()).isEqualTo(1200L);
        assertThat(response.getQuarter()).isNull();
    }
}
