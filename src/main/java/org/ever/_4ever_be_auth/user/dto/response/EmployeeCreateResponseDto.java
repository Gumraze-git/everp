package org.ever._4ever_be_auth.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreateResponseDto {
    private String userId;
    private String email;
    private String departmentName;
    private String positionName;
}
