package org.ever._4ever_be_business.hr.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class EmployeeCreateRequestDto {

    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 최대 50자까지 입력 가능합니다")
    private String name;

    @NotNull(message = "부서 ID는 필수입니다")
    private String departmentId;

    @NotNull(message = "직급 ID는 필수입니다")
    private String positionId;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 100, message = "이메일은 최대 100자까지 입력 가능합니다")
    private String email;

    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호는 010-XXXX-XXXX 형식이어야 합니다")
    private String phoneNumber;

    @NotNull(message = "입사일은 필수입니다")
    private LocalDate hireDate;

    @NotNull(message = "생년월일은 필수입니다")
    private LocalDate birthDate;

    @Size(max = 200, message = "주소는 최대 200자까지 입력 가능합니다")
    private String BaseAddress;

    @Size(max = 200, message = "주소는 최대 200자까지 입력 가능합니다")
    private String DetailAddress;
}