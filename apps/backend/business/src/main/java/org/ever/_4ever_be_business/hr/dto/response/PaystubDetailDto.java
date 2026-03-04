package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaystubDetailDto {
    @JsonProperty("payrollId")
    private String payrollId;

    @JsonProperty("employee")
    private PaystubEmployeeDto employee;

    @JsonProperty("pay")
    private PaystubPayDto pay;

    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("expectedDate")
    private String expectedDate;
}
