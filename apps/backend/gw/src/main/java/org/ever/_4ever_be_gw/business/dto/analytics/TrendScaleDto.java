package org.ever._4ever_be_gw.business.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendScaleDto {
    private ScaleDto sale;
    private ScaleDto orderCount; // min/max as Long; if needed, adapt
}

