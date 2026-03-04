package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollListPayDto {
    @JsonProperty("basePay")
    private BigDecimal basePay;

    @JsonProperty("overtimePay")
    private BigDecimal overtimePay;

    @JsonProperty("deduction")
    private BigDecimal deduction;

    @JsonProperty("netPay")
    private BigDecimal netPay;

    @JsonProperty("statusCode")
    private String statusCode;
}
