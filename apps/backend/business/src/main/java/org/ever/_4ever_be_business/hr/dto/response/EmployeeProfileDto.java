package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeProfileDto {
    @JsonProperty("name")
    private String name;

    @JsonProperty("employeeNumber")
    private String employeeNumber;

    @JsonProperty("department")
    private String department;

    @JsonProperty("position")
    private String position;

    @JsonProperty("hireDate")
    private String hireDate;

    @JsonProperty("serviceYears")
    private String serviceYears;  // 예: "1년 2개월 10일" 또는 "2개월 15일" 또는 "10일"

    @JsonProperty("email")
    private String email;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("address")
    private String address;
}
