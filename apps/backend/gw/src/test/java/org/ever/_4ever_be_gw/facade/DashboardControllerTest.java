package org.ever._4ever_be_gw.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.dashboard.service.DashboardHttpService;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardHttpService dashboardHttpService;

    private DashboardController controller;

    @BeforeEach
    void setUp() {
        controller = new DashboardController(dashboardHttpService);
    }

    @Test
    void getWorkflowsThrowsUnauthorizedWhenPrincipalIsMissing() {
        assertThatThrownBy(() -> controller.getWorkflows(null))
            .isInstanceOf(BusinessException.class)
            .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(ErrorCode.AUTH_TOKEN_REQUIRED));

        verifyNoInteractions(dashboardHttpService);
    }

    @Test
    void getWorkflowsDelegatesWithPrincipalContext() {
        EverUserPrincipal principal = principal("user-1", "ALL_ADMIN", "INTERNAL");
        DashboardWorkflowResponseDto body = DashboardWorkflowResponseDto.builder().build();
        ResponseEntity<DashboardWorkflowResponseDto> expected = ResponseEntity.ok(body);
        when(dashboardHttpService.getWorkflows(principal, "INTERNAL", "ALL_ADMIN")).thenReturn(expected);

        ResponseEntity<DashboardWorkflowResponseDto> response = controller.getWorkflows(principal);

        assertThat(response).isSameAs(expected);
        verify(dashboardHttpService).getWorkflows(principal, "INTERNAL", "ALL_ADMIN");
    }

    private EverUserPrincipal principal(String userId, String userRole, String userType) {
        return EverUserPrincipal.builder()
            .userId(userId)
            .loginEmail(userId + "@example.com")
            .userRole(userRole)
            .userType(userType)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(300))
            .build();
    }
}
