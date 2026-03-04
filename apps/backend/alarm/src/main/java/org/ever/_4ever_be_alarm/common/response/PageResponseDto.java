package org.ever._4ever_be_alarm.common.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class PageResponseDto<T> {

    @JsonProperty("content")
    private List<T> items;
    private PageDto page;
}