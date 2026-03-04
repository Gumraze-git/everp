package org.ever._4ever_be_gw.scm.mm.dto;

import lombok.Getter;

@Getter
public class MaterialItemsDto {     // 제공 자재 목록
    private String materialName;        // 자재 이름
    private String uomCode;             // 자재 단위 코드
    private Integer unitPrice;          // 자재의 단위 가격
}
