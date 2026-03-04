package org.ever._4ever_be_business.sd.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSearchConditionVo {
    private String status;  // ALL, ACTIVE, DEACTIVE
    private String type;    // customerNumber, customerName, managerName
    private String search;  // 검색어
}
