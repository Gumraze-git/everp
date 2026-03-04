package org.ever.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* 내부 사용자 인증 계정 생성 결과 이벤트
* - success=true → 계정 생성 완료
* - success=false → 실패 또는 보상 플로우
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuthUserResultEvent {
    // 공통 메타데이터
    private String eventId;
    private String transactionId;
    private boolean success;

    // 성공 데이터
    private String userId;          // auth 서버에서 발급한 로그인용 사용자 ID (UUID등)

    // 실패 데이터
    private String failureReason;   // 실패 시 사유 (예: validation, DB 오류 등)
}