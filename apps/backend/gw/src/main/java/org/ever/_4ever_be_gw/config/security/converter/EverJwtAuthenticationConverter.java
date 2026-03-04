package org.ever._4ever_be_gw.config.security.converter;

import java.util.Collection;
import java.util.List;
import org.ever._4ever_be_gw.config.security.principal.EverJwtAuthenticationToken;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;

// Jwt를 입력으로 커스텀 인증 객체(EverJwtAuthenticationToken)으로 변환하는 클래스
public class EverJwtAuthenticationConverter implements Converter<Jwt, EverJwtAuthenticationToken> {

    // JWT 안의 클레임을 꺼내서 principal(주체) 객체로 만드는 메서드
    @Override
    public EverJwtAuthenticationToken convert(Jwt jwt) {
        EverUserPrincipal principal = buildPrincipal(jwt);
        Collection<GrantedAuthority> authorities = convertAuthorities(principal);
        return new EverJwtAuthenticationToken(jwt, authorities, principal);
    }

    // JWT 안에 클레임을 꺼내서 사용자 정보 객체를 만드는 메서드
    private EverUserPrincipal buildPrincipal(Jwt jwt) {

        return EverUserPrincipal.builder()
            .userId(jwt.getClaimAsString("user_id"))
            .loginEmail(jwt.getClaimAsString("login_email"))
            .userRole(jwt.getClaimAsString("user_role"))
            .userType(jwt.getClaimAsString("user_type"))
            .issuedAt(jwt.getIssuedAt())
            .expiresAt(jwt.getExpiresAt())
            .build();
    }

    // 사용자 Role을 권한으로 변환하는 메서드, 비어있으면 빈 리스트를 변환함.
    private Collection<GrantedAuthority> convertAuthorities(EverUserPrincipal principal) {
        if (!StringUtils.hasText(principal.getUserRole())) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority(principal.getUserRole()));
    }
}
