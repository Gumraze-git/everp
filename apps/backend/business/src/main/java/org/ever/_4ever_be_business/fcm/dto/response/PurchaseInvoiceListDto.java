package org.ever._4ever_be_business.fcm.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseInvoiceListDto {
    @JsonProperty("invoiceId")
    private String invoiceId;

    @JsonProperty("invoiceNumber")
    private String invoiceCode;

    @JsonProperty("connection")
    private PurchaseStatementConnectionDto connection;

    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;

    @JsonProperty("issueDate")
    private String issueDate;

    @JsonProperty("dueDate")
    private String dueDate;

    @JsonProperty("statusCode")
    private String status;

    @JsonProperty("referenceNumber")
    private String referenceCode;

    @JsonProperty("reference")
    private PurchaseStatementReferenceDto reference;
}
