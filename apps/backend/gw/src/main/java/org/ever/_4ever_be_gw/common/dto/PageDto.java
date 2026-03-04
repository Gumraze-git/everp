package org.ever._4ever_be_gw.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 페이지네이션 공통 dto 입니다.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDto {
    private int number;
    private int size;
    private int totalElements;
    private int totalPages;
    private boolean hasNext;
}
