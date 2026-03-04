package org.ever._4ever_be_business.infrastructure.kafka.consumer.handler;

import org.ever.event.PaymentEvent;

/**
 * Payment 이벤트 처리 핸들러 인터페이스
 */
public interface PaymentEventHandler {

    /**
     * 결제 요청 이벤트 처리
     */
    void handlePaymentRequest(PaymentEvent event);

    /**
     * 결제 완료 이벤트 처리
     */
    void handlePaymentComplete(PaymentEvent event);

    /**
     * 결제 취소 이벤트 처리
     */
    void handlePaymentCancel(PaymentEvent event);
}
