package org.ever._4ever_be_gw.config.security.principal;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

// JwtAuthentication을 확장함.
public class EverJwtAuthenticationToken extends JwtAuthenticationToken {


    // 토큰 클레임에 담겨있는 사용자의 기본 정보(userType, loginEmail, ...)
    private final EverUserPrincipal principal;

    public EverJwtAuthenticationToken(
        Jwt jwt,
        Collection<? extends GrantedAuthority> authorities,
        EverUserPrincipal principal
    ) {
        super(jwt, authorities,principal.getLoginEmail());
        this.principal = principal;
    }

    @Override
    public EverUserPrincipal getPrincipal() {
        return principal;
    }
}
