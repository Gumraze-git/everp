package org.ever._4ever_be_scm.scm.mm.controller;

import org.ever._4ever_be_scm.api.scm.mm.PurchaseRequisitionApi;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.scm.iv.dto.PagedResponseDto;
import org.ever._4ever_be_scm.scm.mm.dto.*;
import org.ever._4ever_be_scm.scm.mm.service.PurchaseRequisitionService;
import org.ever._4ever_be_scm.scm.mm.vo.PurchaseRequisitionCreateVo;
import org.ever._4ever_be_scm.scm.mm.vo.PurchaseRequisitionSearchVo;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/scm-pp/mm/purchase-requisitions")
@RequiredArgsConstructor
public class PurchaseRequisitionController implements PurchaseRequisitionApi {

    private final PurchaseRequisitionService purchaseRequisitionService;

    @GetMapping
    public ResponseEntity<PagedResponseDto<PurchaseRequisitionListResponseDto>> getPurchaseRequisitionList(
            @RequestParam(defaultValue = "ALL") String statusCode,

            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PurchaseRequisitionSearchVo searchVo = PurchaseRequisitionSearchVo.builder()
                .statusCode(statusCode)
                .type(type)
                .keyword(keyword)
                .startDate(startDate)
                .endDate(endDate)
                .page(page)
                .size(size)
                .build();

        Page<PurchaseRequisitionListResponseDto> purchaseRequisitions = purchaseRequisitionService.getPurchaseRequisitionList(searchVo);
        PagedResponseDto<PurchaseRequisitionListResponseDto> response = PagedResponseDto.from(purchaseRequisitions);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{purchaseRequisitionId}")
    public ResponseEntity<PurchaseRequisitionDetailResponseDto> getPurchaseRequisitionDetail(
            @PathVariable String purchaseRequisitionId) {

        PurchaseRequisitionDetailResponseDto detail = purchaseRequisitionService.getPurchaseRequisitionDetail(purchaseRequisitionId);

        return ResponseEntity.ok(detail);
    }

    @PostMapping
    public ResponseEntity<Void> createPurchaseRequisition(
            @RequestBody PurchaseRequisitionCreateRequestDto requestDto,
            @RequestParam String requesterId
            ) {

        // DTO to VO 변환
        PurchaseRequisitionCreateVo createVo = PurchaseRequisitionCreateVo.builder()
                .requesterId(requesterId)
                .items(requestDto.getItems().stream()
                        .map(item -> PurchaseRequisitionCreateVo.ItemVo.builder()
                                .itemName(item.getItemName())
                                .quantity(item.getQuantity())
                                .uomName(item.getUomName())
                                .expectedUnitPrice(item.getExpectedUnitPrice())
                                .preferredSupplierName(item.getPreferredSupplierName())
                                .dueDate(item.getDueDate())
                                .purpose(item.getPurpose())
                                .note(item.getNote())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        purchaseRequisitionService.createPurchaseRequisition(createVo);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{purchaseRequisitionId}/approve")
    public ResponseEntity<Void> approvePurchaseRequisition(
            @PathVariable String purchaseRequisitionId,
            @RequestParam String requesterId
    ) {

        purchaseRequisitionService.approvePurchaseRequisition(purchaseRequisitionId,requesterId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{purchaseRequisitionId}/reject")
    public ResponseEntity<Void> rejectPurchaseRequisition(
            @PathVariable String purchaseRequisitionId,
            @RequestParam String requesterId,
            @RequestBody PurchaseRequisitionRejectRequestDto requestDto) {

        purchaseRequisitionService.rejectPurchaseRequisition(purchaseRequisitionId, requestDto, requesterId);

        return ResponseEntity.noContent().build();
    }
}
