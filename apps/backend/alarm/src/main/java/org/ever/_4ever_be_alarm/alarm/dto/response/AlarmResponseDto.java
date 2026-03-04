package org.ever._4ever_be_alarm.alarm.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "알람 응답 정보")
public class AlarmResponseDto {

    @Schema(description = "알람 ID", example = "abc123-def456")
    private String alarmId;

    @Schema(description = "주문 ID", example = "order-001")
    private String orderId;

    @Schema(description = "사용자 ID", example = "user-001")
    private String userId;

    @Schema(description = "알람 금액", example = "10000")
    private BigDecimal amount;

    @Schema(description = "알람 전송 방법", example = "EMAIL")
    private String alarmMethod;

    @Schema(description = "알람 상태", example = "SENT", allowableValues = {"PENDING", "SENDING", "SENT", "FAILED", "CANCELLED"})
    private String status;

    @Schema(description = "알람 설명", example = "주문 완료 알림")
    private String description;

    @Schema(description = "생성 시각", example = "2025-10-08T12:34:56")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시각", example = "2025-10-08T12:34:56")
    private LocalDateTime updatedAt;
}
