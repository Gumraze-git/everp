package org.ever._4ever_be_auth.config;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CsrfAdvice {
    @ModelAttribute("_csrf")
    public CsrfToken csrfToken(CsrfToken token) {
        return token; // 모델에 _csrf 추가
    }
}