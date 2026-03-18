package org.ever._4ever_be_gw.scm.mm.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import org.ever._4ever_be_gw.common.dto.ValueKeyOptionDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.scm.PeriodStatDto;
import org.ever._4ever_be_gw.scm.mm.service.MmHttpService;
import org.ever._4ever_be_gw.scm.mm.service.MmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class MmControllerTest {

    @Mock
    private MmService mmService;

    @Mock
    private MmHttpService mmHttpService;

    private MmController controller;

    @BeforeEach
    void setUp() {
        controller = new MmController(mmService, mmHttpService);
    }

    @Test
    void getSupplierStatisticsUsesPrincipalUserId() {
        EverUserPrincipal principal = principal("supplier-1");
        ResponseEntity<StatsResponseDto<StatsMetricsDto>> expected = ResponseEntity.ok(
            StatsResponseDto.<StatsMetricsDto>builder()
                .week(StatsMetricsDto.builder()
                    .put("orderCount", PeriodStatDto.builder().value(10L).build())
                    .build())
                .build()
        );
        when(mmHttpService.getSupplierOrderMetrics("supplier-1")).thenReturn(expected);

        ResponseEntity<StatsResponseDto<StatsMetricsDto>> response = controller.getSupplierStatistics(principal);

        assertThat(response).isSameAs(expected);
        verify(mmHttpService).getSupplierOrderMetrics("supplier-1");
    }

    @Test
    void getPurchaseOrderStatusOptionsDelegatesToHttpService() {
        ResponseEntity<List<ValueKeyOptionDto>> expected = ResponseEntity.ok(
            List.of(new ValueKeyOptionDto("배송완료", "DELIVERED"))
        );
        when(mmHttpService.getPurchaseOrderStatusOptions()).thenReturn(expected);

        ResponseEntity<List<ValueKeyOptionDto>> response = controller.getPurchaseOrderStatusOptions();

        assertThat(response).isSameAs(expected);
        verify(mmHttpService).getPurchaseOrderStatusOptions();
    }

    private EverUserPrincipal principal(String userId) {
        return EverUserPrincipal.builder()
            .userId(userId)
            .loginEmail(userId + "@example.com")
            .userRole("SUPPLIER_USER")
            .userType("SUPPLIER")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(300))
            .build();
    }
}
