package org.ever._4ever_be_business.fcm.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.service.VoucherStatusService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoucherStatusScheduler {

    private final VoucherStatusService voucherStatusService;

    /**
     * 매일 자정에 모든 바우처의 상태를 지급 기한 기준으로 자동 업데이트합니다.
     * - 현재가 dueDate 이전이고 PAID가 아니면 PENDING
     * - 현재가 dueDate 이후이고 PAID가 아니면 OVERDUE
     * - PAID 상태는 그대로 유지
     *
     * cron: 초 분 시 일 월 요일
     * 0 0 0 * * * = 매일 자정(00:00:00)
     */
    @Scheduled(cron = "0 0 0 * * *", scheduler = "voucherStatusTaskScheduler")
    public void updateVoucherStatusesDaily() {
        log.info("=== 바우처 상태 자동 업데이트 스케줄러 시작 ===");

        try {
            voucherStatusService.updateAllVoucherStatusesByDueDate();
            log.info("=== 바우처 상태 자동 업데이트 스케줄러 완료 ===");
        } catch (Exception e) {
            log.error("바우처 상태 자동 업데이트 중 오류 발생", e);
        }
    }
}
