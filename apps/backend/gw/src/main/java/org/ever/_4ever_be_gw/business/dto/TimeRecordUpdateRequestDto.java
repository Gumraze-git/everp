package org.ever._4ever_be_gw.business.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TimeRecordUpdateRequestDto {

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    @Pattern(regexp = "^(ON_TIME|LATE|LEAVE)$", message = "출퇴근 상태는 ON_TIME, LATE, LEAVE 중 하나여야 합니다")
    private String statusCode;

}
