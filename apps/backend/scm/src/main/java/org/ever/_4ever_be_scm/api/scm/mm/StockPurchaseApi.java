package org.ever._4ever_be_scm.api.scm.mm;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_scm.scm.mm.dto.StockPurchaseRequestDto;
import org.ever._4ever_be_scm.scm.mm.service.StockPurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "구매관리", description = "구매 관리 API")
@ApiServerErrorResponse
public interface StockPurchaseApi {

    public ResponseEntity<String> createStockPurchaseRequest(
            @RequestBody StockPurchaseRequestDto requestDto,
            @RequestParam String requesterId
            );

}
