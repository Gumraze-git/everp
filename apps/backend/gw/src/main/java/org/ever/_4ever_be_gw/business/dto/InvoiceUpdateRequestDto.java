package org.ever._4ever_be_gw.business.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
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
public class InvoiceUpdateRequestDto {

    @Pattern(regexp = "^(UNPAID|PENDING|PAID)$", message = "status는 UNPAID, PENDING, PAID 중 하나여야 합니다")
    private String status;

    // yyyy-MM-dd 형식 날짜. 컨트롤러에서 LocalDate로 deserialize 됨
    private LocalDate dueDate;

    @Size(max = 200, message = "메모는 최대 200자까지 입력 가능합니다")
    private String memo;
}
