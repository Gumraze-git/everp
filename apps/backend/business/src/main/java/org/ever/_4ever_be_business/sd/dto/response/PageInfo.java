package org.ever._4ever_be_business.sd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {
    private int number;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
}
