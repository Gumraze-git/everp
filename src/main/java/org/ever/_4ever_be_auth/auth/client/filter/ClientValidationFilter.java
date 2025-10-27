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

    private static final String LOGIN_PATH = "/login";
    private static final String AUTHORIZATION_PATH = "/oauth2/authorize";
    private static final String OAUTH_REQUEST_SESSION_KEY = "OAUTH2_AUTHORIZATION_REQUEST";

    private final ClientValidationService clientValidationService;
    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getServletPath(); // 요청(request)에 대한 서블릿 경로를 가져옴, ex) /oauth2/authorize

        if (AUTHORIZATION_PATH.equals(path)) {
            OAuthRequestParams params = extractParams(request);

            log.info("[INFO] OAuth2 인가 요청: 클라이언트 Id={}, 리다이렉트 uri={}, scope={}, state={}",
                    params.clientId(), params.redirectUri(), params.scope(), params.state());

            if (!validateParam(params.clientId(), params.redirectUri(), response)) {
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute(OAUTH_REQUEST_SESSION_KEY, params);
            request.setAttribute("oauth2Params", params);
        } else if (LOGIN_PATH.equals(path)) {
            SavedRequest savedRequest = requestCache.getRequest(request, response);

            if (savedRequest == null) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), "직접 접근할 수 없는 경로입니다.");
                return;
            }

            HttpSession session = request.getSession(false);
            if (session == null) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), "만료된 인가 요청입니다.");
                return;
            }

            OAuthRequestParams params =
                    (OAuthRequestParams) session.getAttribute(OAUTH_REQUEST_SESSION_KEY);
            if (params == null) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), "만료된 인가 요청입니다.");
                return;
            }

            request.setAttribute("oauth2Params", params);
        }
        filterChain.doFilter(request, response);
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
