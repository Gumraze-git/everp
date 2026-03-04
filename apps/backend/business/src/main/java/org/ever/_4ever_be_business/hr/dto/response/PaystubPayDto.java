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
public class PaystubPayDto {
    @JsonProperty("basePay")
    private BigDecimal basePay;

    @JsonProperty("basePayItem")
    private List<PayItemDto> basePayItem;

    @JsonProperty("overtimePay")
    private BigDecimal overtimePay;

    @JsonProperty("overtimePayItem")
    private List<PayItemDto> overtimePayItem;

    @JsonProperty("deduction")
    private BigDecimal deduction;

    @JsonProperty("deductionItem")
    private List<PayItemDto> deductionItem;

    @JsonProperty("netPay")
    private BigDecimal netPay;
}
