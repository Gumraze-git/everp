package org.ever._4ever_be_auth.auth.client.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.auth.client.dto.OAuthRequestParams;
import org.ever._4ever_be_auth.auth.client.exception.ClientValidationException;
import org.ever._4ever_be_auth.auth.client.service.ClientValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.endpoint.PkceParameterNames;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
public class ClientValidationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_PATH = "/oauth2/authorize";
    private static final String AUTHZ_ORIGINAL_URL_KEY = "AUTHZ_ORIGINAL_URL";

    private final ClientValidationService clientValidationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        final String path = request.getServletPath();
        final String method = request.getMethod();

        // ✅ 인가 엔드포인트 GET에서만 전처리 검증
        if (AUTHORIZATION_PATH.equals(path) && "GET".equalsIgnoreCase(method)) {
            OAuthRequestParams params = extractParams(request);

            log.info("[INFO] OAuth2 인가 요청: client_id={}, redirect_uri={}, scope={}, state={}",
                    params.clientId(), params.redirectUri(), params.scope(), params.state());

            try {
                clientValidationService.validateClient(params.clientId(), params.redirectUri());
            } catch (ClientValidationException e) {
                // 표준 방식으로 400만 내려주고 종료 (예외를 던져도 무방)
                response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
                return;
            }
            // SavedRequest가 유실되는 환경(비-HTML Accept 등)을 대비해 원본 인가 요청 URL을 세션에 폴백으로 보관
            HttpSession session = request.getSession(true);
            String q = request.getQueryString();
            String full = request.getRequestURL() + (q != null ? ("?" + q) : "");
            session.setAttribute(AUTHZ_ORIGINAL_URL_KEY, full);
            // SavedRequest/세션 저장은 Spring이 처리함 (위 세션 저장은 폴백)
        }

        // 그 외 모든 경로(/login 포함)는 그대로 통과
        chain.doFilter(request, response);
    }

    private OAuthRequestParams extractParams(HttpServletRequest request) {
        return new OAuthRequestParams(
                request.getParameter(OAuth2ParameterNames.CLIENT_ID),
                request.getParameter(OAuth2ParameterNames.REDIRECT_URI),
                request.getParameter(OAuth2ParameterNames.RESPONSE_TYPE),
                request.getParameter(OAuth2ParameterNames.SCOPE),
                request.getParameter(OAuth2ParameterNames.STATE),
                request.getParameter(PkceParameterNames.CODE_CHALLENGE),
                request.getParameter(PkceParameterNames.CODE_CHALLENGE_METHOD)
        );
    }

    private boolean validateParam(
            String clientId,
            String redirectUri,
            HttpServletResponse response
    ) throws IOException {

        try {
            clientValidationService.validateClient(clientId, redirectUri);
            return true;
        } catch (ClientValidationException e) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return false;
        }
    }
}
