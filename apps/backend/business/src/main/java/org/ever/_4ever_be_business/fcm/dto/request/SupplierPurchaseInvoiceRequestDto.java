package org.ever._4ever_be_business.fcm.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Supplier User ID로 매입전표 목록 조회 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierPurchaseInvoiceRequestDto {
    @JsonProperty("userId")
    private String userId;

    @JsonProperty("size")
    private Integer size;
}
