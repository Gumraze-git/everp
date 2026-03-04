package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PositionDetailDto {
    @JsonProperty("positionId")
    private String positionId;

    @JsonProperty("positionNumber")
    private String positionNumber;

    @JsonProperty("positionName")
    private String positionName;

    @JsonProperty("headCount")
    private Long headCount;

    @JsonProperty("payment")
    private BigDecimal payment;

    @JsonProperty("employees")
    private List<PositionEmployeeDto> employees;
}
