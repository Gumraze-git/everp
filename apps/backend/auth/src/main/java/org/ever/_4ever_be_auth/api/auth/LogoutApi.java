package org.ever._4ever_be_auth.api.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.ever._4ever_be_auth.api.common.ApiServerErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "인증", description = "인증 및 세션 종료 API")
@ApiServerErrorResponse
public interface LogoutApi {

    @Operation(summary = "로그아웃", description = "액세스 토큰과 리프레시 토큰을 무효화하고 세션을 종료합니다.")
    ResponseEntity<Map<String, Object>> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    );
}
