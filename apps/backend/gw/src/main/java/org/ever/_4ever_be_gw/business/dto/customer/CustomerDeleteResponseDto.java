package org.ever._4ever_be_gw.business.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDeleteResponseDto {
    private String customerId;
    private String statusCode;       // 활성화 상태
    private boolean deleted;
    private String deletedAt; // ISO-8601
}

