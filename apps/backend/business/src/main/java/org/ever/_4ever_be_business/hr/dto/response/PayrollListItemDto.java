package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollListItemDto {
    @JsonProperty("payrollId")
    private String payrollId;

    @JsonProperty("employee")
    private PayrollListEmployeeDto employee;

    @JsonProperty("pay")
    private PayrollListPayDto pay;
}
