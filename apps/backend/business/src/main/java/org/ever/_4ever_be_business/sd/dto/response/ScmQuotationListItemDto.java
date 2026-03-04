package org.ever._4ever_be_business.sd.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScmQuotationListItemDto {
    @JsonProperty("quotationId")
    private String quotationId;

    @JsonProperty("quotationNumber")
    private String quotationNumber;

    @JsonProperty("customerName")
    private String customerName;

    @JsonProperty("requestDate")
    private String requestDate;

    @JsonProperty("dueDate")
    private String dueDate;

    @JsonProperty("items")
    private List<ScmQuotationItemDto> items;

    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("availableStatus")
    private String availableStatus;
}
