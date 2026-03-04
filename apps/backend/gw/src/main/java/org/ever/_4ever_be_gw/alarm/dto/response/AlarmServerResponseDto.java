package org.ever._4ever_be_gw.alarm.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AlarmServerResponseDto {

    // 알림 목록 조회 응답
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationListResponse {

        private List<NotificationItem> items;
        private PageInfo page;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class NotificationItem {

            private String notificationId;
            private String notificationTitle;
            private String notificationMessage;
            private String linkType;
            private String linkId;
            private String source;
            private LocalDateTime createdAt;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class PageInfo {

            private int number;
            private int size;
            private int totalElements;
            private int totalPages;
            private boolean hasNext;
        }
    }

    // 알림 갯수 조회 응답
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationCountResponse {

        private int count;
    }

    // 알림 읽음 처리 응답
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationMarkReadResponse {

        private int processedCount;
    }

    // FCM 토큰 등록 응답
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationFcmTokenResponse {

        private boolean success;
        private String message;
    }
}