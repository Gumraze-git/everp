package org.ever._4ever_be_business.fcm.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseStatementReferenceDto {
    @JsonProperty("referenceId")
    private String referenceId;

    @JsonProperty("referenceCode")
    private String referenceCode;
}
