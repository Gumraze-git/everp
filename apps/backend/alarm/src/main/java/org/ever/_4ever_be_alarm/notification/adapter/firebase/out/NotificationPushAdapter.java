package org.ever._4ever_be_alarm.notification.adapter.firebase.out;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_alarm.notification.domain.model.Noti;
import org.ever._4ever_be_alarm.notification.domain.port.out.NotificationDispatchPort;

@Slf4j
@RequiredArgsConstructor
public class NotificationPushAdapter implements NotificationDispatchPort {

    private final FirebaseMessaging firebaseMessaging;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public void dispatch(Noti alarm) {
        log.info("[DISPATCH-PUSH] 푸시 알림 준비 - NotificationId: {}, TargetId: {}, Title: {}",
            alarm.getId(), alarm.getTargetId(), alarm.getTitle());

        // Noti 도메인 모델에 fcmToken이 포함되어야 함
        // NotificationServiceImpl에서 token 조회 후 Noti 객체에 포함시켜서 전달받음
        String fcmToken = alarm.getFcmToken();

        if (fcmToken == null || fcmToken.isBlank()) {
            log.warn(
                "[DISPATCH-PUSH] FCM 토큰이 없어 푸시 알림을 전송할 수 없습니다. - NotificationId: {}, TargetId: {}",
                alarm.getId(), alarm.getTargetId());
            return;
        }

        // 토큰 검증 로그
        log.info("[FCM-DISPATCH] ✅ FCM 토큰 검증 성공");

        try {
            // FCM Notification 생성
            log.debug("[FCM-DISPATCH] FCM Notification 객체 생성 중...");
            log.debug("[FCM-DISPATCH] Notification 정보 - Title: {}, Body: {}", alarm.getTitle(), alarm.getMessage());
            Notification notification = Notification.builder()
                .setTitle(alarm.getTitle())
                .setBody(alarm.getMessage())
                .build();

            // 추가 데이터 구성
            Map<String, String> data = new HashMap<>();
            data.put("notificationId", alarm.getId() != null ? alarm.getId().toString() : "");
            data.put("referenceType",
                alarm.getReferenceType() != null ? alarm.getReferenceType().name() : "");
            data.put("referenceId",
                alarm.getReferenceId() != null ? alarm.getReferenceId().toString() : "");
            data.put("source", alarm.getSource() != null ? alarm.getSource().name() : "");
            data.put("sendAt", alarm.getSendAt() != null ? alarm.getSendAt().toString() : "");

            // FCM Message 생성 (토큰 기반)
            Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(notification)
                .putAllData(data)
                .build();

            log.info("[DISPATCH-PUSH] 푸시 알림 전송 시작 - NotificationId: {}, TargetId: {}, Token: {}",
                alarm.getId(), alarm.getTargetId(), maskToken(fcmToken));

            // FCM 전송
            String messageId = firebaseMessaging.send(message);

            log.info("[DISPATCH-PUSH] FCM 전송 성공 - NotificationId: {}, MessageId: {}, TargetId: {}",
                alarm.getId(), messageId, alarm.getTargetId());

        } catch (FirebaseMessagingException e) {
            handleFirebaseMessagingException(e, fcmToken, alarm);
        } catch (Exception e) {
        
            log.error("===========================================");
            log.error("[FCM-DISPATCH] ❌❌❌ FCM 전송 실패 (예상치 못한 오류) ❌❌❌");
            log.error("[FCM-DISPATCH] 실패 시간: {}", LocalDateTime.now().format(FORMATTER));
            log.error("[FCM-DISPATCH] NotificationId: {}", alarm.getId());
            log.error("[FCM-DISPATCH] TargetId: {}", alarm.getTargetId());
            log.error("[FCM-DISPATCH] 예외 타입: {}", e.getClass().getName());
            log.error("[FCM-DISPATCH] 에러 메시지: {}", e.getMessage());
            log.error("[FCM-DISPATCH] 스택 트레이스:", e);
            log.error("===========================================");
            throw new RuntimeException("푸시 알림 전송 실패", e);
        }
    }

    /**
     * FirebaseMessagingException 처리
     */
    private void handleFirebaseMessagingException(
        FirebaseMessagingException e,
        String fcmToken,
        Noti alarm
    ) {
        
        MessagingErrorCode errorCode = e.getMessagingErrorCode();
        String errorCodeName = errorCode != null ? errorCode.name() : "UNKNOWN";
        String errorMessage = e.getMessage();
        String httpStatusCode = e.getHttpResponse() != null
            ? String.valueOf(e.getHttpResponse().getStatusCode())
            : "N/A";

        log.error("===========================================");
        log.error("[FCM-DISPATCH] ❌❌❌ FCM 전송 실패 ❌❌❌");
        log.error("[FCM-DISPATCH] 실패 시간: {}", LocalDateTime.now().format(FORMATTER));
        log.error("[FCM-DISPATCH] NotificationId: {}", alarm.getId());
        log.error("[FCM-DISPATCH] TargetId: {}", alarm.getTargetId());
        log.error("[FCM-DISPATCH] 에러 코드: {}", errorCodeName);
        log.error("[FCM-DISPATCH] HTTP 상태 코드: {}", httpStatusCode);
        log.error("[FCM-DISPATCH] 에러 메시지: {}", errorMessage);
        log.error("[FCM-DISPATCH] 토큰: {}", maskToken(fcmToken));

        // 에러 코드별 상세 정보
        logErrorDetails(errorCodeName, alarm, fcmToken);

        log.error("[FCM-DISPATCH] 스택 트레이스:", e);
        log.error("===========================================");

        // TODO: 토큰 삭제 또는 비활성화 처리
        // userDeviceTokenRepository.deleteByFcmToken(fcmToken);

        throw new RuntimeException("FCM 전송 실패 - ErrorCode: " + errorCodeName, e);
    }

    /**
     * 에러 코드별 상세 정보 로깅
     */
    private void logErrorDetails(String errorCode, Noti alarm, String fcmToken) {
        switch (errorCode) {
            case "UNREGISTERED":
                log.error("[FCM-DISPATCH] ⚠️ 토큰이 등록 해제되었거나 유효하지 않습니다.");
                log.error("[FCM-DISPATCH] ⚠️ 조치: 해당 토큰을 DB에서 삭제하거나 비활성화해야 합니다.");
                log.error("[FCM-DISPATCH] ⚠️ 재시도 가능: ❌ (토큰 삭제 필요)");
                log.warn("[FCM-DISPATCH] 🔧 TODO: 토큰 삭제 또는 비활성화 처리 필요 - tokenId: {}", maskToken(fcmToken));
                break;

            case "INVALID_ARGUMENT":
                log.error("[FCM-DISPATCH] ⚠️ 잘못된 인자가 전달되었습니다.");
                log.error("[FCM-DISPATCH] ⚠️ 조치: 토큰 형식이나 메시지 구조를 확인해야 합니다.");
                log.error("[FCM-DISPATCH] ⚠️ 재시도 가능: ❌ (토큰 형식 오류)");
                break;

            case "INVALID_REGISTRATION":
                log.error("[FCM-DISPATCH] ⚠️ 등록 ID가 유효하지 않습니다.");
                log.error("[FCM-DISPATCH] ⚠️ 조치: 토큰을 재발급받아야 합니다.");
                log.error("[FCM-DISPATCH] ⚠️ 재시도 가능: ❌ (토큰 재발급 필요)");
                break;

            case "MISMATCHED_CREDENTIAL":
                log.error("[FCM-DISPATCH] ⚠️ 인증 정보가 일치하지 않습니다.");
                log.error("[FCM-DISPATCH] ⚠️ 조치: Firebase 프로젝트 설정을 확인해야 합니다.");
                log.error("[FCM-DISPATCH] ⚠️ 재시도 가능: ❌ (설정 확인 필요)");
                break;

            case "UNAVAILABLE":
                log.error("[FCM-DISPATCH] ⚠️ FCM 서비스가 일시적으로 사용할 수 없습니다.");
                log.error("[FCM-DISPATCH] ⚠️ 조치: 잠시 후 재시도가 필요합니다.");
                log.error("[FCM-DISPATCH] ⚠️ 재시도 가능: ✅ (일시적 오류)");
                break;

            case "INTERNAL":
                log.error("[FCM-DISPATCH] ⚠️ FCM 서버 내부 오류가 발생했습니다.");
                log.error("[FCM-DISPATCH] ⚠️ 조치: 잠시 후 재시도가 필요합니다.");
                log.error("[FCM-DISPATCH] ⚠️ 재시도 가능: ✅ (일시적 오류)");
                break;

            case "QUOTA_EXCEEDED":
                log.error("[FCM-DISPATCH] ⚠️ FCM 할당량을 초과했습니다.");
                log.error("[FCM-DISPATCH] ⚠️ 조치: Firebase Console에서 할당량을 확인하거나 업그레이드가 필요합니다.");
                log.error("[FCM-DISPATCH] ⚠️ 재시도 가능: ❌ (할당량 확인 필요)");
                break;

            case "SENDER_ID_MISMATCH":
                log.error("[FCM-DISPATCH] ⚠️ 발신자 ID가 일치하지 않습니다.");
                log.error("[FCM-DISPATCH] ⚠️ 조치: Firebase 프로젝트 설정을 확인해야 합니다.");
                log.error("[FCM-DISPATCH] ⚠️ 재시도 가능: ❌ (설정 확인 필요)");
                break;

            default:
                log.error("[FCM-DISPATCH] ⚠️ 알 수 없는 에러 코드입니다.");
                log.error("[FCM-DISPATCH] ⚠️ 재시도 가능: ❓ (상황에 따라 다름)");
                break;
        }
    }

    /**
     * 토큰 마스킹 (로그용)
     */
    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 10) + "...";
    }
}
