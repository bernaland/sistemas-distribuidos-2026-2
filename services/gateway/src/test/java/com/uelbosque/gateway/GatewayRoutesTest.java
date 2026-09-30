package com.uelbosque.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureWebTestClient
class GatewayRoutesTest {

    @Autowired
    private RouteLocator routeLocator;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Debe tener configuradas las 4 rutas de microservicios del MVP")
    void shouldHaveConfiguredRoutes() {
        List<Route> routes = routeLocator.getRoutes().collectList().block();
        assertNotNull(routes);
        assertTrue(routes.stream().anyMatch(r -> r.getId().equals("user-service")));
        assertTrue(routes.stream().anyMatch(r -> r.getId().equals("catalog-service")));
        assertTrue(routes.stream().anyMatch(r -> r.getId().equals("cart-service")));
        assertTrue(routes.stream().anyMatch(r -> r.getId().equals("order-service")));
        for (String id : List.of("customer-service", "supplier-service", "inventory-service", "payment-service", "notification-service", "admin-service")) {
            assertTrue(routes.stream().anyMatch(r -> r.getId().equals(id)), id);
        }
    }

    @Test
    @DisplayName("GET /api/gateway/health debe responder 200 con estado UP")
    void shouldReturnHealthStatus() {
        webTestClient.get().uri("/api/gateway/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP")
                .jsonPath("$.service").isEqualTo("api-gateway")
                .jsonPath("$.routes.userService").exists()
                .jsonPath("$.routes.catalogService").exists()
                .jsonPath("$.routes.cartService").exists()
                .jsonPath("$.routes.orderService").exists();
    }

    @Test
    @DisplayName("GET /fallback debe retornar 503 SERVICE_UNAVAILABLE")
    void shouldReturnFallbackResponse() {
        webTestClient.get().uri("/fallback")
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.status").isEqualTo("DOWN")
                .jsonPath("$.message").exists();
    }
}
