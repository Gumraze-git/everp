package org.ever._4ever_be_gw.business.dto.fcm.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseStatementListItemDto {
    @JsonProperty("invoiceId")
    private String invoiceId;

    @JsonProperty("invoiceCode")
    private String invoiceCode;

    @JsonProperty("connection")
    private PurchaseStatementConnectionDto connection;

    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;

    @JsonProperty("issueDate")
    private String issueDate;

    @JsonProperty("dueDate")
    private String dueDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("referenceCode")
    private String referenceCode;

    @JsonProperty("reference")
    private PurchaseStatementReferenceDto reference;
}
