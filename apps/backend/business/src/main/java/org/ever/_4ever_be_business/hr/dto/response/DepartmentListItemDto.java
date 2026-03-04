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
public class DepartmentListItemDto {
    @JsonProperty("departmentId")
    private String departmentId;

    @JsonProperty("departmentNumber")
    private String departmentNumber;

    @JsonProperty("departmentName")
    private String departmentName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("managerName")
    private String managerName;

    @JsonProperty("managerId")
    private String managerId;

    @JsonProperty("location")
    private String location;

    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("employeeCount")
    private Integer employeeCount;

    @JsonProperty("budget")
    private Long budget;

    @JsonProperty("budgetCurrency")
    private String budgetCurrency;

    @JsonProperty("establishedDate")
    private String establishedDate;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("updatedAt")
    private String updatedAt;

    @JsonProperty("responsibilities")
    private List<String> responsibilities;

    @JsonProperty("employees")
    private List<DepartmentEmployeeDto> employees;
}
