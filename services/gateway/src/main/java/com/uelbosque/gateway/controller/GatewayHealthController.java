package com.uelbosque.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class GatewayHealthController {

    @GetMapping("/api/gateway/health")
    public Mono<ResponseEntity<Map<String, Object>>> health() {
        Map<String, Object> body = Map.of(
                "status", "UP",
                "service", "api-gateway",
                "timestamp", LocalDateTime.now().toString(),
                "routes", Map.of(
                        "userService", "http://localhost:8081/api/users/**",
                        "catalogService", "http://localhost:8082/api/products/**",
                        "cartService", "http://localhost:8084/api/cart/**",
                        "orderService", "http://localhost:8085/api/orders/**"
                )
        );
        return Mono.just(ResponseEntity.ok(body));
    }

    @GetMapping("/fallback")
    public Mono<ResponseEntity<Map<String, String>>> fallback() {
        Map<String, String> body = Map.of(
                "status", "DOWN",
                "message", "El servicio solicitado no se encuentra disponible temporalmente. Intente más tarde."
        );
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body));
    }
}
