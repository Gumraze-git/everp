package org.ever._4ever_be_scm.scm.pp.controller;

import org.ever._4ever_be_scm.api.scm.pp.MrpApi;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.scm.mm.dto.ToggleCodeLabelDto;
import org.ever._4ever_be_scm.scm.pp.dto.MrpRunConvertRequestDto;
import org.ever._4ever_be_scm.scm.pp.dto.MrpRunQueryResponseDto;
import org.ever._4ever_be_scm.scm.pp.service.MrpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/scm-pp/pp/mrp-runs")
@RequiredArgsConstructor
public class MrpController implements MrpApi {

    private final MrpService mrpService;

    /**
     * MRP → MRP_RUN 계획주문 전환
     */
    @PostMapping

    public ResponseEntity<Void> convertToMrpRun(
            @RequestBody MrpRunConvertRequestDto requestDto) {

        mrpService.convertToMrpRun(requestDto);

        return ResponseEntity.noContent().build();
    }

    /**
     * MRP 계획주문 목록 조회
     */
    @GetMapping

    public ResponseEntity<MrpRunQueryResponseDto> getMrpRunList(

            @RequestParam(defaultValue = "ALL") String status,

            @RequestParam(required = false) String quotationId,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size) {

        MrpRunQueryResponseDto result = mrpService.getMrpRunList(status, quotationId, page, size);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/status-options")

    public ResponseEntity<List<ToggleCodeLabelDto>> getMrpRunStatusOptions() {
        List<ToggleCodeLabelDto> list = List.of(
                new ToggleCodeLabelDto("전체 상태", "ALL"),
                new ToggleCodeLabelDto("신청전", "INITIAL"),
                new ToggleCodeLabelDto("반려", "REJECTED"),
                new ToggleCodeLabelDto("대기중", "PENDING"),
                new ToggleCodeLabelDto("요청 승인", "REQUEST_APPROVED"),
                new ToggleCodeLabelDto("발주서 승인", "ORDER_APPROVED"),
                new ToggleCodeLabelDto("배송중", "DELIVERING"),
                new ToggleCodeLabelDto("배송완료", "DELIVERED")
        );
        return ResponseEntity.ok(list);
    }

    @GetMapping("/quotation-options")

    public ResponseEntity<List<ToggleCodeLabelDto>> getMrpRunQuotationOptions() {
        List<ToggleCodeLabelDto> result = mrpService.getMrpRunQuotationList();
        return ResponseEntity.ok(result);
    }
}
