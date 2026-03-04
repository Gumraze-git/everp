package org.ever._4ever_be_gw.business.dto.hrm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDepartmentRequestDto {
    @JsonProperty("managerId")
    private String managerId;

    @JsonProperty("description")
    private String description;
}
