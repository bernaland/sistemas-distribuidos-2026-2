package com.uelbosque.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
class GatewayCorsTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Debe responder con cabeceras CORS válidas para el origen Angular")
    void shouldAllowCorsForAngularOrigin() {
        webTestClient.options().uri("http://localhost:8080/api/gateway/health")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "GET")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Access-Control-Allow-Origin", "http://localhost:4200");
    }
}
