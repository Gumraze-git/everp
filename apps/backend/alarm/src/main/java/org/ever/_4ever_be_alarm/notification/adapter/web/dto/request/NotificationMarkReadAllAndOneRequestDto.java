package org.ever._4ever_be_alarm.notification.adapter.web.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_alarm.common.validation.ValidUuidV7;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMarkReadAllAndOneRequestDto {

    @ValidUuidV7
    @NotEmpty(message = "사용자 ID는 필수입니다.")
    private String userId;
}
