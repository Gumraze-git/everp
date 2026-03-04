package org.ever._4ever_be_gw.business.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerDto {
    private String managerName;
    private String managerPhone;
    private String managerEmail; // 목록에선 null 가능, 상세에선 제공
}

