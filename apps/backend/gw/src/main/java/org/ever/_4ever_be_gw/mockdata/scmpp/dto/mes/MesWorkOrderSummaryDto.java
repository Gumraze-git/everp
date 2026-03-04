package org.ever._4ever_be_gw.mockdata.scmpp.dto.mes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWorkOrderSummaryDto {
    private String referenceDate;
    private String generatedAt;
    private CompareSummaryDto compare;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompareSummaryDto {
        private CompareItemDto inProgress;
        private CompareItemDto startedThisMonth;
        private CompareItemDto completedThisMonth;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompareItemDto {
        private CompareValueDto prevDay;
        private CompareValueDto prevMonth;
        private CompareValueDto prevYear;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompareValueDto {
        private Integer delta;
        private Double pct;
    }
}
