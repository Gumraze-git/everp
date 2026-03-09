package org.ever._4ever_be_gw.business.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import org.ever._4ever_be_gw.business.dto.customer.CustomerCreateRequestDto;
import org.ever._4ever_be_gw.business.dto.hrm.CreateAuthUserResultDto;
import org.ever._4ever_be_gw.business.service.SdHttpService;
import org.ever._4ever_be_gw.business.service.SdService;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class SdControllerTest {

    @Mock
    private SdHttpService sdHttpService;

    @Mock
    private SdService sdService;

    private SdController controller;

    @BeforeEach
    void setUp() {
        controller = new SdController(sdHttpService, sdService);
    }

    @Test
    void getCurrentCustomerQuotationCountUsesPrincipalUserId() {
        EverUserPrincipal principal = principal("customer-1", "CUSTOMER");
        ResponseEntity<Object> expected = ResponseEntity.ok("metric");
        when(sdHttpService.getQuotationCountByCustomerUserId("customer-1")).thenReturn(expected);

        ResponseEntity<Object> response = controller.getCurrentCustomerQuotationCount(principal);

        assertThat(response).isSameAs(expected);
        verify(sdHttpService).getQuotationCountByCustomerUserId("customer-1");
    }

    @Test
    void createQuotationReviewUsesPathVariable() {
        ResponseEntity<Object> expected = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(sdHttpService.confirmQuotation("quotation-1")).thenReturn(expected);

        ResponseEntity<Object> response = controller.createQuotationReview("quotation-1");

        assertThat(response).isSameAs(expected);
        verify(sdHttpService).confirmQuotation("quotation-1");
    }

    @Test
    void getScmQuotationsDelegatesToHttpService() {
        ResponseEntity<Object> expected = ResponseEntity.ok("quotations");
        when(sdHttpService.getScmQuotationList(
                "PENDING",
                "AVAILABLE",
                "2026-03-01",
                "2026-03-31",
                0,
                20
        )).thenReturn(expected);

        ResponseEntity<Object> response = controller.getScmQuotations(
                "PENDING",
                "AVAILABLE",
                "2026-03-01",
                "2026-03-31",
                0,
                20
        );

        assertThat(response).isSameAs(expected);
        verify(sdHttpService).getScmQuotationList(
                "PENDING",
                "AVAILABLE",
                "2026-03-01",
                "2026-03-31",
                0,
                20
        );
    }

    @Test
    void createCustomerReturnsCreatedWhenSagaResultContainsUserId() {
        CustomerCreateRequestDto request = new CustomerCreateRequestDto();
        CreateAuthUserResultDto result = new CreateAuthUserResultDto();
        result.setUserId("customer-user-1");
        when(sdService.createCustomer(request))
                .thenReturn(Mono.just(result));

        ResponseEntity<CreateAuthUserResultDto> response = controller.createCustomer(request).block();

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(result);
    }

    private EverUserPrincipal principal(String userId, String userType) {
        return EverUserPrincipal.builder()
                .userId(userId)
                .loginEmail(userId + "@example.com")
                .userRole("SD_USER")
                .userType(userType)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();
    }
}
