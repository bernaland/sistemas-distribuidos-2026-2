package com.uelbosque.configserver;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class ConfigSecurity {
    @Bean public SecurityFilterChain security(HttpSecurity http) throws Exception {
        return http.csrf(c -> c.disable()).authorizeHttpRequests(a -> a.requestMatchers("/actuator/health").permitAll()
            .anyRequest().authenticated()).httpBasic(org.springframework.security.config.Customizer.withDefaults()).build();
    }
}
