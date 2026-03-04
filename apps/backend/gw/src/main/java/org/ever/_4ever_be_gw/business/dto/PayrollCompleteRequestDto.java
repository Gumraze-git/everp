package org.ever._4ever_be_gw.business.dto;

import jakarta.validation.constraints.NotBlank;
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
public class PayrollCompleteRequestDto {

    @NotBlank(message = "급여 ID는 필수입니다")
    private String payrollId;

}
