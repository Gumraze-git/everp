package org.ever._4ever_be_gw.business.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSummaryDto {
    private String customerId;              // 고객사 ID (UUID v7)
    private String customerName;            // 고객사 이름
    private String customerBaseAddress;     // 고객사 기본주소
    private String customerDetailAddress;   // 고객사 상세주소
    private ManagerDto manager;             // { managerName, managerPhone, managerEmail }
}
