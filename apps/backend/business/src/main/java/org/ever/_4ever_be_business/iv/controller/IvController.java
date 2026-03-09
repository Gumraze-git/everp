package org.ever._4ever_be_business.iv.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.iv.dto.request.IvOrderRequestDto;
import org.ever._4ever_be_business.iv.dto.response.IvOrderResponseDto;
import org.ever._4ever_be_business.iv.service.IvOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/iv")
@RequiredArgsConstructor
public class IvController {

    private final IvOrderService ivOrderService;

    /**
     * IV 주문서 조회
     */
    @PostMapping("/orders")
    public ResponseEntity<IvOrderResponseDto> getOrder(@RequestBody IvOrderRequestDto request) {
        log.info("IV 주문서 조회 API 호출 - orderId: {}", request.getOrderId());

        IvOrderResponseDto result = ivOrderService.getOrder(request);

        log.info("IV 주문서 조회 성공");
        return ResponseEntity.ok(result);
    }
}
