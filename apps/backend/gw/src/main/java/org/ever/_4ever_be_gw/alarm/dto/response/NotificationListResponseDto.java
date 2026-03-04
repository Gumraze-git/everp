package org.ever._4ever_be_gw.alarm.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationListResponseDto {

    private UUID notificationId;
    private String notificationTitle;
    private String notificationMessage;
    private String linkType;
    private UUID linkId;
    private String source;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
