package org.ever._4ever_be_auth.auth.account.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_auth.user.entity.User;
import org.ever._4ever_be_auth.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PasswordChangeEnforcementFilter extends OncePerRequestFilter {

    private static final Set<String> ALLOWED_PATHS = Set.of(
            "/password/change",
            "/logout",
            "/oauth2/authorize",
            "/css/",
            "/js/",
            "/images/",
            "/videos/",
            "/favicon.ico"
    );

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails userDetails) {
            if (!isPathAllowed(request)) {
                User user = userRepository.findByLoginEmail(userDetails.getUsername()).orElse(null);
                if (user != null && user.getPasswordLastChangedAt() == null) {
                    response.sendRedirect("/password/change");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPathAllowed(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path == null) {
            return false;
        }

        for (String allowed : ALLOWED_PATHS) {
            if (path.equals(allowed) || path.startsWith(allowed)) {
                return true;
            }
        }
        return false;
    }
}
