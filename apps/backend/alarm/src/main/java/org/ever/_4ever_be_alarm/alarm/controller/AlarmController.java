package org.ever._4ever_be_alarm.alarm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_alarm.common.response.ApiResponse;
import org.ever._4ever_be_alarm.alarm.dto.request.AlarmRequestDto;
import org.ever._4ever_be_alarm.alarm.dto.response.AlarmResponseDto;
import org.ever._4ever_be_alarm.alarm.service.AlarmService;
import org.ever._4ever_be_alarm.alarm.vo.AlarmRequestVo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService paymentService;

    @PostMapping
    public ApiResponse<AlarmResponseDto> createPayment(@Valid @RequestBody AlarmRequestDto requestDto) {
        log.info("결제 요청 수신 - orderId: {}, userId: {}", requestDto.getOrderId(), requestDto.getUserId());

        AlarmRequestVo requestVo = new AlarmRequestVo(
            requestDto.getOrderId(),
            requestDto.getUserId(),
            requestDto.getAmount(),
            requestDto.getAlarmMethod(),
            requestDto.getDescription()
        );

        AlarmResponseDto responseDto = paymentService.processPayment(requestVo);
        return ApiResponse.success(responseDto, "결제가 성공적으로 처리되었습니다.", HttpStatus.CREATED);
    }

    @GetMapping("/{paymentId}")
    public ApiResponse<AlarmResponseDto> getPayment(@PathVariable String paymentId) {
        log.info("결제 정보 조회 요청 - paymentId: {}", paymentId);
        AlarmResponseDto responseDto = paymentService.getPayment(paymentId);
        return ApiResponse.success(responseDto, "결제 정보 조회 성공", HttpStatus.OK);
    }

    @GetMapping
    public ApiResponse<List<AlarmResponseDto>> getAllPayments() {
        log.info("전체 결제 정보 조회 요청");
        List<AlarmResponseDto> responseDtoList = paymentService.getAllPayments();
        return ApiResponse.success(responseDtoList, "전체 결제 정보 조회 성공", HttpStatus.OK);
    }

    @DeleteMapping("/{paymentId}")
    public ApiResponse<AlarmResponseDto> cancelPayment(@PathVariable String paymentId) {
        log.info("결제 취소 요청 - paymentId: {}", paymentId);
        AlarmResponseDto responseDto = paymentService.cancelPayment(paymentId);
        return ApiResponse.success(responseDto, "결제가 성공적으로 취소되었습니다.", HttpStatus.OK);
    }

    @GetMapping("/health")
    public ApiResponse<String> healthCheck() {
        return ApiResponse.success("Payment Service is running", "헬스 체크 성공", HttpStatus.OK);
    }
}
