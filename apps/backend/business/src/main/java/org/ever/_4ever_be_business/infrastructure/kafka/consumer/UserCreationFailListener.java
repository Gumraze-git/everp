//package org.ever._4ever_be_business.infrastructure.kafka.consumer;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.ever.event.ProcessCompletedEvent;
//import org.ever._4ever_be_business.common.async.AsyncResultManager;
//import org.ever._4ever_be_business.common.saga.SagaCompensationService;
//import org.ever._4ever_be_business.msapractice.dto.CustomerResponseDto;
//import org.springframework.http.HttpStatus;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
///**
// * 사용자 생성 결과 리스너
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class UserCreationFailListener {
//
//    private final AsyncResultManager<CustomerResponseDto> asyncResultManager;
//    private final SagaCompensationService sagaCompensationService;
//
//
//    /**
//     * 사용자 생성 실패 이벤트 수신 처리
//     */
//    @KafkaListener(topics = "${spring.kafka.topic.process-completed}", groupId = "${spring.kafka.consumer.group-id}")
//    public void handleUserCreationFailed(ProcessCompletedEvent event) {
//        log.warn("사용자 생성 실패 이벤트 수신: {}", event);
//
//        // 트랜잭션 ID 추출
//        String transactionId = event.getTransactionId();
//        if (transactionId == null) {
//            log.error("트랜잭션 ID가 없는 사용자 생성 실패 이벤트: {}", event);
//            return;
//        }
//
//        // 오류 결과 설정
//        asyncResultManager.setErrorResult(
//                transactionId,
//                "사용자 생성에 실패했습니다: ",
//                HttpStatus.BAD_REQUEST
//        );
//        sagaCompensationService.compensate(transactionId);
//
//        log.info("사용자 생성 실패 결과 설정: customerUserId={}, transactionId={}",
//                event.getCustomerUserId(), transactionId);
//    }
//}
