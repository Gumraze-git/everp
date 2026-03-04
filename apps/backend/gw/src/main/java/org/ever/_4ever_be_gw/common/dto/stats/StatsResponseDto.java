package org.ever._4ever_be_gw.common.dto.stats;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatsResponseDto<T> {
    private final T today;
    private final T week;
    private final T month;
    private final T quarter;
    private final T year;
}
