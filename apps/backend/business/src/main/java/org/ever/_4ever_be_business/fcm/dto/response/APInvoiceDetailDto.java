package org.ever._4ever_be_business.fcm.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class APInvoiceDetailDto {
    @JsonProperty("invoiceId")
    private String invoiceId;

    @JsonProperty("invoiceNumber")
    private String invoiceNumber;

    @JsonProperty("invoiceType")
    private String invoiceType;

    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("issueDate")
    private String issueDate;

    @JsonProperty("dueDate")
    private String dueDate;

    @JsonProperty("name")
    private String name;

    @JsonProperty("referenceNumber")
    private String referenceNumber;

    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;

    @JsonProperty("note")
    private String note;

    @JsonProperty("items")
    private List<APInvoiceItemDto> items;
}
