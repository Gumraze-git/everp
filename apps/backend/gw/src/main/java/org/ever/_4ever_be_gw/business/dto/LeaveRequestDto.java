package org.ever._4ever_be_gw.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class LeaveRequestDto {

    @NotBlank(message = "휴가 유형은 필수입니다")
    @Pattern(regexp = "^(ANNUAL|SICK)$", message = "휴가 유형은 ANNUAL 또는 SICK이어야 합니다")
    private String leaveType;

    @NotBlank(message = "시작일은 필수입니다")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "시작일은 YYYY-MM-DD 형식이어야 합니다")
    private String startDate;  // "YYYY-MM-DD" 형식

    @NotBlank(message = "종료일은 필수입니다")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "종료일은 YYYY-MM-DD 형식이어야 합니다")
    private String endDate;    // "YYYY-MM-DD" 형식

}