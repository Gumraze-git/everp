package org.ever._4ever_be_gw.mockdata.scmpp.dto.mes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWorkOrderDetailDto {
    private String mesId;
    private String mesNumber;
    private String productId;
    private String productName;
    private Integer quantity;
    private String uomName;
    private Integer progressPercent;
    private String statusCode;
    private PlanInfo plan;
    private String currentOperation;
    private List<OperationDto> operations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanInfo {
        private String startDate;
        private String dueDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperationDto {
        private String operationNumber;
        private String operationName;
        private Integer sequence;
        private String statusCode;
        private String startedAt;
        private String finishedAt;
        private Double durationHours;
        private AssigneeDto manager;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssigneeDto {
        private Long id;
        private String name;
    }
}
