package org.ever._4ever_be_business.iv.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IvOrderResponseDto {
    private String orderId;
    private String orderCode;
    private String orderStatus;
}
