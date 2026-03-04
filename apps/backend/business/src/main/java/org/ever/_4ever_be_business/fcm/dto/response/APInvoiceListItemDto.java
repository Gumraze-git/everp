package org.ever._4ever_be_business.fcm.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class APInvoiceListItemDto {

    @JsonProperty("invoiceId")
    private String invoiceId;

    @JsonProperty("invoiceNumber")
    private String invoiceNumber;

    @JsonProperty("connection")
    private SupplierDto supplier;

    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;

    @JsonProperty("issueDate")
    private String issueDate;

    @JsonProperty("dueDate")
    private String dueDate;

    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("referenceNumber")
    private String referenceNumber;

    @JsonProperty("reference")
    private ReferenceDto reference;
}
