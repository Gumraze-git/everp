package org.ever._4ever_be_scm.api.scm.pp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_scm.scm.pp.dto.PpStatisticsResponseDto;
import org.ever._4ever_be_scm.scm.pp.service.PpStatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "생산관리", description = "생산 관리 통계 API")
@ApiServerErrorResponse
public interface PpStatisticsApi {

    @Operation(summary = "PP 통계 조회", description = "생산관리의 통계를 반환합니다. 생산중인 품목, 완료된 생산, 완제품 개수 포함")
    public ResponseEntity<PpStatisticsResponseDto> getPpStatistic();

}
