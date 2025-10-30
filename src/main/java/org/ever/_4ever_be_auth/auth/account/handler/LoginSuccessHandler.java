package org.ever._4ever_be_auth.auth.account.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.user.entity.User;
import org.ever._4ever_be_auth.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private static final String OAUTH_REQUEST_SESSION_KEY = "OAUTH2_AUTHORIZATION_REQUEST";
    private static final String AUTHZ_ORIGINAL_URL_KEY = "AUTHZ_ORIGINAL_URL";

    private final UserRepository userRepository;
    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // 로그인 성공 흐름 로깅
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        log.info("[INFO] 저장된 request: {}", savedRequest);
        String targetUrl = (savedRequest != null) ? savedRequest.getRedirectUrl() : getDefaultTargetUrl();
        log.info("[INFO] 로그인 성공: principal = {}, sessionId = {}, targetUrl ={}",
                authentication.getName(),
                (session != null ? session.getId() : "세션 아이디를 확인할 수 없습니다."),
                targetUrl);

        if (session != null) {
            session.removeAttribute(OAUTH_REQUEST_SESSION_KEY);
        }

        // SavedRequest가 비어있으면, 인가 요청 폴백 URL로 복귀 시도
        if (savedRequest == null && session != null) {
            Object original = session.getAttribute(AUTHZ_ORIGINAL_URL_KEY);
            if (original instanceof String orig && orig.contains("/oauth2/authorize")) {
                session.removeAttribute(AUTHZ_ORIGINAL_URL_KEY);
                getRedirectStrategy().sendRedirect(request, response, orig);
                return;
            }
        }

        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            User user = userRepository.findByLoginEmail(userDetails.getUsername()).orElse(null);
            if (user != null && user.getPasswordLastChangedAt() == null) {
                getRedirectStrategy().sendRedirect(request, response, "/password/change");
                return;
            }
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
