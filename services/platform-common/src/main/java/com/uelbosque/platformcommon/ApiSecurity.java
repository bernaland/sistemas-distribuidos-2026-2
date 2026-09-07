package com.uelbosque.platformcommon;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
@Configuration
public class ApiSecurity {
    @Bean public JwtDecoder jwtDecoder(@Value("${app.jwt.secret}") String secret) {
        var key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
    @Bean public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable()).sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(a -> a.requestMatchers("/actuator/health").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/orders").denyAll()
            .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("ADMIN", "USER")
            .requestMatchers("/api/sales/**", "/api/cart/**", "/api/orders/**", "/api/payments/**").hasAnyRole("ADMIN", "USER")
            .anyRequest().hasRole("ADMIN"));
        http.oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(converter())));
        return http.build();
    }
    private JwtAuthenticationConverter converter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> Arrays.stream(java.util.Objects.toString(jwt.getClaimAsString("roles"), "").split(","))
            .map(String::trim).filter(r -> !r.isEmpty()).map(SimpleGrantedAuthority::new)
            .collect(java.util.stream.Collectors.toList()));
        return converter;
    }
}
