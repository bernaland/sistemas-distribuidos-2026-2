package com.uelbosque.platformcommon;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import java.time.Duration;
@AutoConfiguration
@Import({ApiErrors.class, ApiSecurity.class})
public class PlatformConfiguration {
    @Bean public RestTemplate serviceRestTemplate(RestTemplateBuilder builder) {
        return builder.setConnectTimeout(Duration.ofSeconds(3)).setReadTimeout(Duration.ofSeconds(5))
            .additionalInterceptors((request, body, execution) -> {
                var authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication instanceof JwtAuthenticationToken jwt) {
                    request.getHeaders().setBearerAuth(jwt.getToken().getTokenValue());
                }
                return execution.execute(request, body);
            }).build();
    }
}
