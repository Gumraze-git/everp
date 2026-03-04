package org.ever._4ever_be_gw.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.config.security.principal.EverJwtAuthenticationToken;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.user.dto.UserInfoResponse;
import org.ever._4ever_be_gw.user.service.UserInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;

    @GetMapping("/info")
    public ApiResponse<UserInfoResponse> getUserInfo(
            @AuthenticationPrincipal EverUserPrincipal principal,
            EverJwtAuthenticationToken authentication
    ) {
        final String token = (authentication != null && authentication.getToken() != null)
                ? authentication.getToken().getTokenValue()
                : null;

        log.info("[USER][INFO] 사용자 정보 조회 요청 수신: principalPresent={}, tokenPresent={}",
                principal != null, token != null);

        try {
            UserInfoResponse data = userInfoService.getUserInfo(principal, token);
            log.info("[USER][INFO] 사용자 정보 조회 성공: userId={}, username={}",
                    safeUserId(principal), safeUsername(principal));
            return ApiResponse.success(data, "사용자 기본 정보를 조회했습니다.", HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            // 클라이언트 잘못된 요청(파라미터/상태 문제)
            log.warn("[USER][WARN] 잘못된 사용자 정보 조회 요청: userId={}, username={}, tokenPreview={}, reason={}",
                    safeUserId(principal), safeUsername(principal), preview(token), e.getMessage(), e);
            throw e; // 전역 예외 처리기로 위임

        } catch (Exception e) {
            // 시스템/외부 연동/권한 등 일반 실패
            log.error("[USER][ERROR] 사용자 정보 조회 실패: principalPresent={}, tokenPreview={}, reason={}",
                    principal != null, preview(token), e.getMessage(), e);
            throw e; // 전역 예외 처리기로 위임
        }
    }

    private String preview(String v) {
        if (v == null) return "null";
        int n = Math.min(10, v.length());
        return v.substring(0, n) + "...";
    }

    private Object safeUserId(EverUserPrincipal p) {
        try { return (p == null) ? "null" : p.getUserId(); } catch (Exception ignore) { return "n/a"; }
    }
    private Object safeUsername(EverUserPrincipal p) {
        try { return (p == null) ? "null" : p.getUserId(); } catch (Exception ignore) { return "n/a"; }
    }
}
