package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PositionListItemDto {
    @JsonProperty("positionId")
    private String positionId;

    @JsonProperty("positionName")
    private String positionName;

    @JsonProperty("headCount")
    private Long headCount;

    @JsonProperty("payment")
    private BigDecimal payment;
}
