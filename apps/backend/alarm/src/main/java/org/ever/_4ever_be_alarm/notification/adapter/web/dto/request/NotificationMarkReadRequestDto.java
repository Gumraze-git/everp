package org.ever._4ever_be_alarm.notification.adapter.web.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_alarm.common.validation.ValidUuidV7;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMarkReadRequestDto {

    @ValidUuidV7
    @NotEmpty(message = "사용자 ID는 필수입니다.")
    private String userId;

    @NotEmpty(message = "알림 ID 목록은 비어있을 수 없습니다.")
    private List<@ValidUuidV7 String> notificationIds;
}

