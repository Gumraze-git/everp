package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDepartmentEmployeeDto {
    @JsonProperty("managerId")
    private String userId;

    @JsonProperty("managerName")
    private String name;

    @JsonProperty("managerEmail")
    private String email;

    @JsonProperty("managerPhone")
    private String phoneNumber;
}
