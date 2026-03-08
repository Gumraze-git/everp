package org.ever._4ever_be_business.sd.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.ever._4ever_be_business.sd.dto.request.CreateQuotationRequestDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationCountDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationCreatedResponseDto;
import org.ever._4ever_be_business.sd.dto.response.ScmQuotationListItemDto;
import org.ever._4ever_be_business.sd.dto.response.ScmQuotationListResponseDto;
import org.ever._4ever_be_business.sd.service.DashboardCustomerQuotationService;
import org.ever._4ever_be_business.sd.service.DashboardOrderService;
import org.ever._4ever_be_business.sd.service.DashboardStatisticsService;
import org.ever._4ever_be_business.sd.service.DashboardSupplierQuotationService;
import org.ever._4ever_be_business.sd.service.QuotationService;
import org.ever._4ever_be_business.sd.service.SalesAnalyticsService;
import org.ever._4ever_be_business.sd.service.SdCustomerService;
import org.ever._4ever_be_business.sd.service.SdOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class SdControllerTest {

    @Mock
    private DashboardStatisticsService dashboardStatisticsService;

    @Mock
    private SalesAnalyticsService salesAnalyticsService;

    @Mock
    private SdCustomerService customerService;

    @Mock
    private SdOrderService sdOrderService;

    @Mock
    private QuotationService quotationService;

    @Mock
    private DashboardOrderService dashboardOrderService;

    @Mock
    private DashboardSupplierQuotationService dashboardSupplierQuotationService;

    @Mock
    private DashboardCustomerQuotationService dashboardCustomerQuotationService;

    private SdController controller;

    @BeforeEach
    void setUp() {
        controller = new SdController(
                dashboardStatisticsService,
                salesAnalyticsService,
                customerService,
                sdOrderService,
                quotationService,
                dashboardOrderService,
                dashboardSupplierQuotationService,
                dashboardCustomerQuotationService
        );
    }

    @Test
    void createQuotationReturnsCreatedLocation() {
        CreateQuotationRequestDto request = new CreateQuotationRequestDto("customer-user-1", List.of(), "note");
        when(quotationService.createQuotation(request)).thenReturn("quotation-1");

        ResponseEntity<QuotationCreatedResponseDto> response = controller.createQuotation(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).hasToString("/sd/quotations/quotation-1");
        assertThat(response.getBody()).extracting(QuotationCreatedResponseDto::getQuotationId).isEqualTo("quotation-1");
    }

    @Test
    void createQuotationReviewUsesPathVariable() {
        ResponseEntity<Void> response = controller.createQuotationReview("quotation-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(quotationService).confirmQuotation("quotation-1");
    }

    @Test
    void getCustomerQuotationCountDelegatesToMetricService() {
        QuotationCountDto expected = new QuotationCountDto(null, null, null, null);
        when(quotationService.getQuotationCountByCustomerUserId("customer-user-1")).thenReturn(expected);

        ResponseEntity<QuotationCountDto> response = controller.getCustomerQuotationCount("customer-user-1");

        assertThat(response.getBody()).isSameAs(expected);
        verify(quotationService).getQuotationCountByCustomerUserId("customer-user-1");
    }

    @Test
    void getScmQuotationListWrapsPageIntoResponseDto() {
        ScmQuotationListItemDto item = new ScmQuotationListItemDto("quotation-1", "QT-001", "고객사", "2026-03-01", "2026-03-05", List.of(), "PENDING", "AVAILABLE");
        when(quotationService.getScmQuotationList(any(), any())).thenReturn(
                new PageImpl<>(List.of(item), PageRequest.of(0, 10), 1)
        );

        ResponseEntity<ScmQuotationListResponseDto> response = controller.getScmQuotationList(
                "PENDING",
                "AVAILABLE",
                "2026-03-01",
                "2026-03-31",
                0,
                10
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotal()).isEqualTo(1L);
        assertThat(response.getBody().getContent()).containsExactly(item);
        assertThat(response.getBody().getPage().getNumber()).isEqualTo(0);
        assertThat(response.getBody().getPage().getSize()).isEqualTo(10);
        assertThat(response.getBody().getPage().getTotalElements()).isEqualTo(1);
        assertThat(response.getBody().getPage().getTotalPages()).isEqualTo(1);
        assertThat(response.getBody().getPage().isHasNext()).isFalse();
    }
}
