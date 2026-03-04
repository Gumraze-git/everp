package org.ever._4ever_be_gw.business.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PositionSimpleDto {
    @JsonProperty("positionId")
    private String positionId;

    @JsonProperty("positionName")
    private String positionName;
}
