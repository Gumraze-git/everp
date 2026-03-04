package org.ever._4ever_be_gw.business.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerManagerDto {
    private String managerName;   // 담당자 이름
    private String managerPhone;  // 담당자 전화번호
    private String managerEmail;  // 담당자 이메일
}
