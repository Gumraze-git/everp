package org.ever._4ever_be_gw.mockdata.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadyToShipOrderDto {
    private String salesOrderId;
    private String salesOrderNumber;
    private String customerCompanyName;
    private LocalDateTime orderDate;
    private LocalDateTime dueDate;
    private int totalAmount;
    private String statusCode;
}
