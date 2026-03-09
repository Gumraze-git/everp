package org.ever._4ever_be_scm.api.scm.iv;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_scm.scm.iv.dto.PagedResponseDto;
import org.ever._4ever_be_scm.scm.iv.dto.StockDeliveryRequestDto;
import org.ever._4ever_be_scm.scm.iv.dto.StockTransferDto;
import org.ever._4ever_be_scm.scm.iv.dto.StockTransferRequestDto;
import org.ever._4ever_be_scm.scm.iv.service.StockTransferService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "재고관리", description = "재고 관리 API")
@ApiServerErrorResponse
public interface StockTransferApi {

    @Operation(summary = "재고 이동 목록 조회 (상위 5개)")
    public ResponseEntity<PagedResponseDto<StockTransferDto>> getStockTransfers();

    @Operation(summary = "재고 입출고 처리")
    public ResponseEntity<Void> processStockDelivery(@RequestBody StockDeliveryRequestDto request);

}
