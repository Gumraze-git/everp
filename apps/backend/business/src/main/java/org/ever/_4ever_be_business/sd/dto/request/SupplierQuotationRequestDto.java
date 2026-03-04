package org.ever._4ever_be_business.sd.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 대시보드용(공급사) 발주서 목록 조회 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierQuotationRequestDto {

    @JsonProperty("userId")
    private String userId;   // 공급사 사용자 ID (required)

    @JsonProperty("size")
    private Integer size;    // 페이지 크기 (optional, 기본 5)
}

