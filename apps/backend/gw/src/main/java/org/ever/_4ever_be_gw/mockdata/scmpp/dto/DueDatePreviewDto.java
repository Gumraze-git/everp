package org.ever._4ever_be_gw.mockdata.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DueDatePreviewDto {
    private String quotationNumber;
    private String customerCompanyName;
    private String productName;
    private String confirmedDueDate;
    private List<WeekPlanDto> weeks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeekPlanDto {
        private String week;
        private Integer demand;
        private Integer requiredQuantity;
        private Integer productionQuantity;
        private Integer mps;
    }
}
