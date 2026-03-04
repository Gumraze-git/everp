package org.ever._4ever_be_business.sd.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApproveOrderRequestDto {
    private String employeeId;  // 승인한 직원 ID (UUID)
}
