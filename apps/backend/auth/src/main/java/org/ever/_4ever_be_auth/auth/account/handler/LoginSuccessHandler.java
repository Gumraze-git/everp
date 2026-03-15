package org.ever._4ever_be_auth.auth.account.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.user.entity.User;
import org.ever._4ever_be_auth.user.enums.UserType;
import org.ever._4ever_be_auth.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private static final String OAUTH_REQUEST_SESSION_KEY = "OAUTH2_AUTHORIZATION_REQUEST";
    private static final String AUTHZ_ORIGINAL_URL_KEY = "AUTHZ_ORIGINAL_URL";
    private static final Set<String> RESTRICTED_CLIENTS = Set.of("everp-ios", "everp-aos");
    private static final EnumSet<UserType> ALLOWED_USER_TYPES = EnumSet.of(UserType.CUSTOMER, UserType.SUPPLIER);

    private final UserRepository userRepository;
    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Value("${EVERP_FRONTEND_ORIGIN:http://localhost:13000}")
    private String frontendOrigin;

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

        User user = null;
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            user = userRepository.findByLoginEmail(userDetails.getUsername()).orElse(null);
        }

        String originalAuthUrl = extractOriginalAuthorizationUrl(savedRequest, session);
        if (shouldDenyForClient(user, originalAuthUrl)) {
            handleRestrictedClientLogin(request, response, authentication, originalAuthUrl);
            return;
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

        if (user != null && user.getPasswordLastChangedAt() == null) {
            getRedirectStrategy().sendRedirect(request, response, "/password/change");
            return;
        }

        // 직접 /login으로 진입한 경우에는 프론트 앱으로 보내고 인증 부트스트랩을 이어서 수행함.
        if (savedRequest == null && originalAuthUrl == null) {
            getRedirectStrategy().sendRedirect(request, response, buildDirectLoginFallbackUrl());
            return;
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private String buildDirectLoginFallbackUrl() {
        return UriComponentsBuilder.fromUriString(frontendOrigin)
                .path("/dashboard")
                .build(true)
                .toUriString();
    }

    private boolean shouldDenyForClient(User user, String originalAuthUrl) {
        if (user == null || user.getUserType() == null || originalAuthUrl == null) {
            return false;
        }
        String clientId = extractClientId(originalAuthUrl);
        if (clientId == null || !RESTRICTED_CLIENTS.contains(clientId)) {
            return false;
        }
        return !ALLOWED_USER_TYPES.contains(user.getUserType());
    }

    private String extractOriginalAuthorizationUrl(SavedRequest savedRequest, HttpSession session) {
        if (savedRequest != null) {
            String redirectUrl = savedRequest.getRedirectUrl();
            if (redirectUrl != null && redirectUrl.contains("/oauth2/authorize")) {
                return redirectUrl;
            }
        }
        if (session != null) {
            Object original = session.getAttribute(AUTHZ_ORIGINAL_URL_KEY);
            if (original instanceof String orig) {
                return orig;
            }
        }
        return null;
    }

    private String extractClientId(String url) {
        try {
            MultiValueMap<String, String> params = UriComponentsBuilder.fromUriString(url)
                    .build()
                    .getQueryParams();
            return params.getFirst("client_id");
        } catch (Exception e) {
            log.warn("인가 요청 URL에서 client_id를 추출하지 못했습니다. url={}", url, e);
            return null;
        }
    }

    private void handleRestrictedClientLogin(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication,
            String originalAuthUrl
    ) throws IOException {
        log.warn("허용되지 않은 사용자 유형이 모바일 클라이언트 로그인을 시도했습니다. clientUrl={}, principal={}",
                originalAuthUrl, authentication.getName());

        String authUrlToRestore = originalAuthUrl;
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        if (authUrlToRestore != null) {
            request.getSession(true).setAttribute(AUTHZ_ORIGINAL_URL_KEY, authUrlToRestore);
        }
        getRedirectStrategy().sendRedirect(request, response, "/login?error");
    }
}
