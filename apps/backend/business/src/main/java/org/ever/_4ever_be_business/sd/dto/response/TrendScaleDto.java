package org.ever._4ever_be_business.sd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrendScaleDto {
    private ScaleDto sale;
    private ScaleDto orderCount;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScaleDto {
        private BigDecimal min;
        private BigDecimal max;
    }
}
