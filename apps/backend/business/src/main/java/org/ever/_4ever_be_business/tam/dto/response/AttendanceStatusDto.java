package org.ever._4ever_be_business.tam.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStatusDto {
    @JsonProperty("key")
    private String key;

    @JsonProperty("value")
    private String value;
}
