package org.ever.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuthUserEvent {
    // Kafka
    private String eventId;             // 카프카 브로커에 발행되는 이벤트 자체의 고유 식별자
    private String transactionId;       // saga 코레오그래피 흐름에서 공통으로 사용되는 트랜잭션 상관 키
    private boolean success;            // 해당 이벤트가 성공인지 실패 및 보상 흐름인지 나타내는 flag

    // 카프카 이벤트로 전송할 데이터
    private String userId;
    private String email;
    private String positionCode;
    private String departmentCode;
}
