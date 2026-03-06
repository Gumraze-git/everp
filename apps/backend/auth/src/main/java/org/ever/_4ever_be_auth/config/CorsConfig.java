package org.ever._4ever_be_auth.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;

@Configuration
public class CorsConfig {

    private final String frontendOrigin;

    public CorsConfig(@Value("${EVERP_FRONTEND_ORIGIN:http://localhost:13000}") String frontendOrigin) {
        this.frontendOrigin = frontendOrigin;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(buildAllowedOrigins());
        cfg.setAllowedMethods(List.of("GET","POST","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization","Content-Type","Accept","X-Requested-With"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    private List<String> buildAllowedOrigins() {
        List<String> origins = new ArrayList<>();
        origins.add(frontendOrigin);
        if (frontendOrigin.contains("://localhost:")) {
            origins.add(frontendOrigin.replace("://localhost:", "://127.0.0.1:"));
        }
        origins.add("https://everp.co.kr");
        return origins;
    }
}
