package org.ever._4ever_be_business.fcm.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReferenceDto {

    @JsonProperty("referenceId")
    private String referenceId;

    @JsonProperty("referenceNumber")
    private String referenceNumber;
}
