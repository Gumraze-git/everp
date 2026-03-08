package org.ever._4ever_be_gw.common.dto.stats;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import org.ever._4ever_be_gw.scm.PeriodStatDto;

public final class StatsResponseMapper {

    private StatsResponseMapper() {
    }

    public static StatsResponseDto<StatsMetricsDto> fromJson(JsonNode root) {
        if (root == null || root.isNull()) {
            return null;
        }

        return StatsResponseDto.<StatsMetricsDto>builder()
            .today(readMetrics(root.get("today")))
            .week(readMetrics(root.get("week")))
            .month(readMetrics(root.get("month")))
            .quarter(readMetrics(root.get("quarter")))
            .year(readMetrics(root.get("year")))
            .build();
    }

    private static StatsMetricsDto readMetrics(JsonNode periodNode) {
        if (periodNode == null || periodNode.isNull() || !periodNode.isObject()) {
            return null;
        }

        StatsMetricsDto.Builder builder = StatsMetricsDto.builder();
        Iterator<Map.Entry<String, JsonNode>> fields = periodNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            PeriodStatDto stat = readStat(field.getValue());
            if (stat != null) {
                builder.put(field.getKey(), stat);
            }
        }

        return builder.build();
    }

    private static PeriodStatDto readStat(JsonNode statNode) {
        if (statNode == null || statNode.isNull() || !statNode.isObject()) {
            return null;
        }

        BigDecimal deltaRate = null;
        JsonNode deltaRateNode = statNode.get("delta_rate");
        if (deltaRateNode != null && deltaRateNode.isNumber()) {
            deltaRate = deltaRateNode.decimalValue();
        }

        return PeriodStatDto.builder()
            .value(statNode.path("value").asLong())
            .deltaRate(deltaRate)
            .build();
    }
}
