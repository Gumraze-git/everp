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
public class ProgramModifyRequestDto {

    @NotBlank(message = "프로그램 이름은 필수입니다")
    private String programName;

    @NotBlank(message = "상태 코드는 필수입니다")
    private String statusCode;

}
