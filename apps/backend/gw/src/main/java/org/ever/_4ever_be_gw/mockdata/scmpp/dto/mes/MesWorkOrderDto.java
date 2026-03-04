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
public class MesWorkOrderDto {
    private String mesId;
    private String mesNumber;
    private String productId;
    private String productName;
    private Integer quantity;
    private String uomName;
    private String quotationId;
    private String quotationNumber;
    private String status;
    private String currentOperation;
    private String startDate;
    private String endDate;
    private Integer progressRate;
    private List<String> sequence;
}
