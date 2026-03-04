package org.ever._4ever_be_business.infrastructure.redis.service;

import java.time.Duration;

public interface OrderDeliverySchedulerService {

    /**
     * 주문 배송 완료 자동 처리 예약
     *
     * @param orderId 주문 ID
     * @param delay 지연 시간
     */
    void scheduleDeliveryCompletion(String orderId, Duration delay);

    /**
     * 예약된 배송 완료 작업 취소
     *
     * @param orderId 주문 ID
     * @return 취소 성공 여부
     */
    boolean cancelScheduledDelivery(String orderId);
}
