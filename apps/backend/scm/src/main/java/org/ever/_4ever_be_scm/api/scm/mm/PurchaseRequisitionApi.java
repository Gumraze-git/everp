package org.ever._4ever_be_scm.api.scm.mm;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_scm.scm.iv.dto.PagedResponseDto;
import org.ever._4ever_be_scm.scm.mm.dto.*;
import org.ever._4ever_be_scm.scm.mm.service.PurchaseRequisitionService;
import org.ever._4ever_be_scm.scm.mm.vo.PurchaseRequisitionCreateVo;
import org.ever._4ever_be_scm.scm.mm.vo.PurchaseRequisitionSearchVo;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "구매관리", description = "구매 관리 API")
@ApiServerErrorResponse
public interface PurchaseRequisitionApi {

    public ResponseEntity<PagedResponseDto<PurchaseRequisitionListResponseDto>> getPurchaseRequisitionList(
            @RequestParam(defaultValue = "ALL") String statusCode,
            
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    public ResponseEntity<PurchaseRequisitionDetailResponseDto> getPurchaseRequisitionDetail(
            @PathVariable String purchaseRequisitionId);

    public ResponseEntity<Void> createPurchaseRequisition(
            @RequestBody PurchaseRequisitionCreateRequestDto requestDto,
            @RequestParam String requesterId
            );

    public ResponseEntity<Void> approvePurchaseRequisition(
            @PathVariable String purchaseRequisitionId,
            @RequestParam String requesterId
    );

    public ResponseEntity<Void> rejectPurchaseRequisition(
            @PathVariable String purchaseRequisitionId,
            @RequestParam String requesterId,
            @RequestBody PurchaseRequisitionRejectRequestDto requestDto);

}
