package org.ever._4ever_be_business.hr.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingStatusSearchConditionVo {
    private String department;  // department ID
    private String position;    // position ID
    private String name;        // employee name
}
