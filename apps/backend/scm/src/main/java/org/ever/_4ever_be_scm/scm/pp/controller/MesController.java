package org.ever._4ever_be_scm.scm.pp.controller;

import org.ever._4ever_be_scm.api.scm.pp.MesApi;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.scm.iv.dto.PagedResponseDto;
import org.ever._4ever_be_scm.scm.mm.dto.ToggleCodeLabelDto;
import org.ever._4ever_be_scm.scm.pp.dto.MesDetailResponseDto;
import org.ever._4ever_be_scm.scm.pp.dto.MesQueryResponseDto;
import org.ever._4ever_be_scm.scm.pp.service.MesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;


@RestController
@RequestMapping("/scm-pp/pp/mes")
@RequiredArgsConstructor
public class MesController implements MesApi {

    private final MesService mesService;

    /**
     * MES 목록 조회
     */
    @GetMapping

    public ResponseEntity<PagedResponseDto<MesQueryResponseDto.MesItemDto>> getMesList(

            @RequestParam(required = false) String quotationId,

            @RequestParam(defaultValue = "ALL") String status,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "20") int size) {

        Page<MesQueryResponseDto.MesItemDto> mesList = mesService.getMesList(quotationId, status, PageRequest.of(page, size));
        PagedResponseDto<MesQueryResponseDto.MesItemDto> response = PagedResponseDto.from(mesList);

        return ResponseEntity.ok(response);
    }

    /**
     * MES 상세 조회
     */
    @GetMapping("/{mesId}")

    public ResponseEntity<MesDetailResponseDto> getMesDetail(

            @PathVariable String mesId) {

        MesDetailResponseDto result = mesService.getMesDetail(mesId);

        return ResponseEntity.ok(result);
    }

    /**
     * MES 시작
     */
    @PostMapping("/{mesId}/starts")

    public DeferredResult<ResponseEntity<?>> startMes(

            @PathVariable String mesId,
            @RequestParam String requesterId
            ) {

        return mesService.startMesAsync(mesId, requesterId);
    }

    /**
     * 공정 시작
     */
    @PostMapping("/{mesId}/operations/{logId}/starts")

    public ResponseEntity<Void> startOperation(

            @PathVariable String mesId,

            @PathVariable String logId,

            @RequestParam(required = false) String managerId) {

        mesService.startOperation(mesId, logId, managerId);

        return ResponseEntity.noContent().build();
    }

    /**
     * 공정 완료
     */
    @PostMapping("/{mesId}/operations/{logId}/completions")

    public ResponseEntity<Void> completeOperation(

            @PathVariable String mesId,

            @PathVariable String logId) {

        mesService.completeOperation(mesId, logId);

        return ResponseEntity.noContent().build();
    }

    /**
     * MES 완료
     */
    @PostMapping("/{mesId}/completions")

    public DeferredResult<ResponseEntity<?>> completeMes(

            @PathVariable String mesId,
            @RequestParam String requesterId
            ) {

        return mesService.completeMesAsync(mesId, requesterId);
    }

    @GetMapping("/status-options")
    public ResponseEntity<List<ToggleCodeLabelDto>> getMesStatusOptions() {
        List<ToggleCodeLabelDto> list = List.of(
                new ToggleCodeLabelDto("전체 상태", "ALL"),
                new ToggleCodeLabelDto("대기중", "PENDING"),
                new ToggleCodeLabelDto("진행중", "IN_PROGRESS"),
                new ToggleCodeLabelDto("완료", "COMPLETED")

        );
        return ResponseEntity.ok(list);
    }
}
