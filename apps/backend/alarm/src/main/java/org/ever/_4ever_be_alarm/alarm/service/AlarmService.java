package org.ever._4ever_be_alarm.alarm.service;

import org.ever._4ever_be_alarm.alarm.dto.response.AlarmResponseDto;
import org.ever._4ever_be_alarm.alarm.vo.AlarmRequestVo;

import java.util.List;

/**
 * 결제 서비스 인터페이스
 */
public interface AlarmService {

    /**
     * 결제 요청 처리
     */
    AlarmResponseDto processPayment(AlarmRequestVo request);

    /**
     * 결제 정보 조회
     */
    AlarmResponseDto getPayment(String paymentId);

    /**
     * 모든 결제 정보 조회
     */
    List<AlarmResponseDto> getAllPayments();

    /**
     * 결제 취소
     */
    AlarmResponseDto cancelPayment(String paymentId);
}
