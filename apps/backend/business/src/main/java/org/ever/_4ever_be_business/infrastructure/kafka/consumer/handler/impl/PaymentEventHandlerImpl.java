package org.ever._4ever_be_business.infrastructure.kafka.consumer.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.infrastructure.kafka.consumer.handler.PaymentEventHandler;
import org.ever.event.PaymentEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventHandlerImpl implements PaymentEventHandler {

    @Override
    public void handlePaymentRequest(PaymentEvent event) {
        // 실제 결제 요청 처리 로직 구현
        log.debug("결제 요청 처리 중 - OrderId: {}, Amount: {}",
            event.getOrderId(), event.getAmount());

        // TODO: 실제 비즈니스 로직 구현
        // 1. 결제 정보 검증
        // 2. PG사 연동
        // 3. DB 저장
    }

    @Override
    public void handlePaymentComplete(PaymentEvent event) {
        // 실제 결제 완료 처리 로직 구현
        log.debug("결제 완료 처리 중 - PaymentId: {}", event.getPaymentId());

        // TODO: 실제 비즈니스 로직 구현
        // 1. 결제 완료 상태 업데이트
        // 2. 주문 서비스에 알림
        // 3. 사용자에게 알림 발송
    }

    @Override
    public void handlePaymentCancel(PaymentEvent event) {
        // 실제 결제 취소 처리 로직 구현
        log.debug("결제 취소 처리 중 - PaymentId: {}", event.getPaymentId());

        // TODO: 실제 비즈니스 로직 구현
        // 1. 결제 취소 처리
        // 2. 환불 처리
        // 3. 상태 업데이트
    }
}
