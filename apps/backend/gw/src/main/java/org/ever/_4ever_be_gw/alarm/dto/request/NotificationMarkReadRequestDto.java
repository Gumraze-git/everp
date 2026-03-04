package org.ever._4ever_be_gw.alarm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_gw.common.dto.validation.ValidUuidV7;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "알림 읽음 처리 요청")
public class NotificationMarkReadRequestDto {

    @NotEmpty(message = "알림 ID 목록은 비어있을 수 없습니다.")
    @Schema(description = "알림 ID 목록 (UUID v7 형식)", example = "[\"01932e9c-8f3a-7b2e-9e3d-4c5b6a7d8e9f\"]")
    private List<@ValidUuidV7 String> notificationId;
}