package org.ever._4ever_be_business.hr.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PayrollSearchConditionVo {
    private final Integer year;
    private final Integer month;
    private final String name;
    private final String department;
    private final String position;
    private final String statusCode;
}
