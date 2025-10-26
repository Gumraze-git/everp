package org.ever._4ever_be_auth.auth.account.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_auth.user.entity.User;
import org.ever._4ever_be_auth.user.enums.UserStatus;
import org.ever._4ever_be_auth.user.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EverUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username == loginEmail
        User user = userRepository.findByLoginEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        var authority = new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getLoginEmail())
                .password(user.getPasswordHash())
                .authorities(Collections.singleton(authority))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(user.getUserStatus() != UserStatus.ACTIVE)
                .build();
    }

    /**
     * 엔티티(User)를 분리한 경량 Principal.
     * JSON 직렬화 시 안전(기본 타입만 포함)하고, 비밀번호 해시는 @JsonIgnore 처리.
     */
    public record EverUserPrincipal(
            UUID userId,
            String loginEmail,
            String roleName,            // ex) "ADMIN"
            UserStatus userStatus,
            @JsonIgnore String passwordHash // 직렬화 제외
    ) implements UserDetails {

        public static EverUserPrincipal from(User user) {
            return new EverUserPrincipal(
                    user.getUserId(),
                    user.getLoginEmail(),
                    user.getUserRole().name(),
                    user.getUserStatus(),
                    user.getPasswordHash()
            );
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + roleName));
        }

        @Override
        public String getPassword() {
            return passwordHash;
        }

        @Override
        public String getUsername() {
            return loginEmail;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return userStatus == UserStatus.ACTIVE;
        }
    }
}