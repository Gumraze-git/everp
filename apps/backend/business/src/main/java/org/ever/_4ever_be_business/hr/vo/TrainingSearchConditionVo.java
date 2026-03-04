package org.ever._4ever_be_business.hr.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ever._4ever_be_business.hr.enums.TrainingCategory;
import org.ever._4ever_be_business.hr.enums.TrainingStatus;

/**
 * 교육 프로그램 검색 조건 VO
 */
@Getter
@Setter
@NoArgsConstructor
public class TrainingSearchConditionVo {

    /**
     * 교육 프로그램명 (부분 검색)
     */
    private String name;

    /**
     * 교육 상태
     */
    private TrainingStatus status;

    /**
     * 교육 카테고리
     */
    private TrainingCategory category;

    public TrainingSearchConditionVo(String name, TrainingStatus status, TrainingCategory category) {
        this.name = name;
        this.status = status;
        this.category = category;
    }
}
