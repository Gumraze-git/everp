package org.ever._4ever_be_gw.config.security;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CsrfController {

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken csrfToken) {
        // 브라우저가 GW 변경 요청 전에 XSRF 쿠키와 메타 정보를 확보하는 용도임.
        return Map.of(
                "headerName", csrfToken.getHeaderName(),
                "parameterName", csrfToken.getParameterName(),
                "token", csrfToken.getToken()
        );
    }
}
