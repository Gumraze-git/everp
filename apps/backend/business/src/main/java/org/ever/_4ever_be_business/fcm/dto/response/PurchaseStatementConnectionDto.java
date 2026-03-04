package org.ever._4ever_be_business.fcm.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseStatementConnectionDto {
    @JsonProperty("connectionId")
    private String connectionId;

    @JsonProperty("connectionCode")
    private String connectionCode;

    @JsonProperty("connectionName")
    private String connectionName;
}
