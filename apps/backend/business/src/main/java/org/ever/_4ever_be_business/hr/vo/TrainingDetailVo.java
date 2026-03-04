package org.ever._4ever_be_business.hr.vo;

import lombok.Getter;

/**
 * Service 계층에서 사용하는 Value Object
 */
@Getter
public class TrainingDetailVo {
    private final String programId;

    public TrainingDetailVo(String programId) {
        this.programId = programId;
    }
}
