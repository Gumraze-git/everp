//package org.ever._4ever_be_gw.mockdata.scmpp.service;
//
//import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
//import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
//import org.ever._4ever_be_gw.scm.PeriodStatDto;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class MmStatisticsServiceImpl implements MmStatisticsService {
//    @Override
//    public StatsResponseDto<StatsMetricsDto> getStatistics(List<String> periods) {
//        Map<String, StatsMetricsDto> bucket = new LinkedHashMap<>();
//
//        if (periods.contains("week")) {
//            bucket.put("week", StatsMetricsDto.builder()
//                    .put("purchase_request_count", new PeriodStatDto(184L, new BigDecimal("0.0728")))
//                    .put("purchase_approval_pending_count", new PeriodStatDto(39L, new BigDecimal("-0.0532")))
//                    .put("purchase_order_amount", new PeriodStatDto(1_283_000_000L, new BigDecimal("0.1044")))
//                    .put("purchase_order_approval_pending_count", new PeriodStatDto(22L, new BigDecimal("0.1000")))
//                    .build());
//        }
//        if (periods.contains("month")) {
//            bucket.put("month", StatsMetricsDto.builder()
//                    .put("purchase_request_count", new PeriodStatDto(736L, new BigDecimal("0.0389")))
//                    .put("purchase_approval_pending_count", new PeriodStatDto(161L, new BigDecimal("-0.0417")))
//                    .put("purchase_order_amount", new PeriodStatDto(5_214_000_000L, new BigDecimal("0.0361")))
//                    .put("purchase_order_approval_pending_count", new PeriodStatDto(94L, new BigDecimal("0.0652")))
//                    .build());
//        }
//        if (periods.contains("quarter")) {
//            bucket.put("quarter", StatsMetricsDto.builder()
//                    .put("purchase_request_count", new PeriodStatDto(2_154L, new BigDecimal("0.0215")))
//                    .put("purchase_approval_pending_count", new PeriodStatDto(472L, new BigDecimal("-0.0186")))
//                    .put("purchase_order_amount", new PeriodStatDto(15_123_000_000L, new BigDecimal("0.0247")))
//                    .put("purchase_order_approval_pending_count", new PeriodStatDto(281L, new BigDecimal("0.0426")))
//                    .build());
//        }
//        if (periods.contains("year")) {
//            bucket.put("year", StatsMetricsDto.builder()
//                    .put("purchase_request_count", new PeriodStatDto(8_421L, new BigDecimal("0.0298")))
//                    .put("purchase_approval_pending_count", new PeriodStatDto(1_813L, new BigDecimal("-0.0221")))
//                    .put("purchase_order_amount", new PeriodStatDto(59_876_000_000L, new BigDecimal("0.0312")))
//                    .put("purchase_order_approval_pending_count", new PeriodStatDto(1_103L, new BigDecimal("0.0185")))
//                    .build());
//        }
//
//        return StatsResponseDto.<StatsMetricsDto>builder()
//                .week(bucket.get("week"))
//                .month(bucket.get("month"))
//                .quarter(bucket.get("quarter"))
//                .year(bucket.get("year"))
//                .build();
//    }
//}
