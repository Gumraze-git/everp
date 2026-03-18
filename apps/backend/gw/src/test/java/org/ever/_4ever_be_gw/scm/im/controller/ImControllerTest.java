package org.ever._4ever_be_gw.scm.im.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
import org.ever._4ever_be_gw.scm.im.service.ImHttpService;
import org.ever._4ever_be_gw.scm.mm.dto.ItemInfoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ImControllerTest {

    @Mock
    private ImHttpService imHttpService;

    private ImController controller;

    @BeforeEach
    void setUp() {
        controller = new ImController(imHttpService);
    }

    @Test
    void getWarehouseManagerOptionsDelegatesToHttpService() {
        ResponseEntity<Object> expected = ResponseEntity.ok("managers");
        when(imHttpService.getWarehouseManagerOptions()).thenReturn(expected);

        ResponseEntity<Object> response = controller.getInventoryEmployees();

        assertThat(response).isSameAs(expected);
        verify(imHttpService).getWarehouseManagerOptions();
    }

    @Test
    void searchItemsDelegatesToHttpService() {
        ItemInfoRequest request = new ItemInfoRequest();
        ResponseEntity<Object> expected = ResponseEntity.ok("items");
        when(imHttpService.searchItems(request)).thenReturn(expected);

        ResponseEntity<Object> response = controller.getItemInfoList(request);

        assertThat(response).isSameAs(expected);
        verify(imHttpService).searchItems(request);
    }

    @Test
    void getWarehouseMetricsDelegatesToHttpService() {
        ResponseEntity<StatsResponseDto<StatsMetricsDto>> expected =
            ResponseEntity.ok(StatsResponseDto.<StatsMetricsDto>builder().build());
        when(imHttpService.getWarehouseMetrics()).thenReturn(expected);

        ResponseEntity<StatsResponseDto<StatsMetricsDto>> response = controller.getWarehouseStatistic();

        assertThat(response).isSameAs(expected);
        verify(imHttpService).getWarehouseMetrics();
    }
}
