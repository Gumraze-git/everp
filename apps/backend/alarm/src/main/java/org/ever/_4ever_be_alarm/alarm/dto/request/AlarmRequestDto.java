package org.ever._4ever_be_alarm.alarm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "알람 요청 정보")
public class AlarmRequestDto {

    @Schema(description = "주문 ID", example = "order-001", required = true)
    @NotBlank(message = "주문 ID는 필수입니다.")
    private String orderId;

    @Schema(description = "사용자 ID", example = "user-001", required = true)
    @NotBlank(message = "사용자 ID는 필수입니다.")
    private String userId;

    @Schema(description = "알람 금액", example = "10000", required = true)
    @NotNull(message = "알람 금액은 필수입니다.")
    @Min(value = 100, message = "알람 금액은 최소 100원 이상이어야 합니다.")
    private BigDecimal amount;

    @Schema(description = "알람 전송 방법", example = "EMAIL", allowableValues = {"EMAIL", "SMS", "PUSH", "KAKAO"}, required = true)
    @NotBlank(message = "알람 전송 방법은 필수입니다.")
    private String alarmMethod;

    @Schema(description = "알람 설명", example = "주문 완료 알림")
    private String description;
}
