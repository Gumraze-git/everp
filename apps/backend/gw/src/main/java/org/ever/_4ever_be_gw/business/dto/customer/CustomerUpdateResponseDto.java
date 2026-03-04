package org.ever._4ever_be_gw.business.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdateResponseDto {
    private String customerId;
    private String customerNumber;
    private String customerName;
    private String ceoName;
    private String businessNumber;
    private String statusCode;

    private String customerPhone;
    private String customerEmail;
    private String baseAddress;
    private String detailAddress;

    private CustomerManagerDto manager;
    private String note;
}

