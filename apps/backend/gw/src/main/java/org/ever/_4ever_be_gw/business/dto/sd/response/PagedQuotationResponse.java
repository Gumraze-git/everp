package org.ever._4ever_be_gw.business.dto.sd.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Business 서비스의 Spring Page 응답을 역직렬화하기 위한 DTO
 */
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagedQuotationResponse {
    private List<QuotationListItemDto> content;
    private int number;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
