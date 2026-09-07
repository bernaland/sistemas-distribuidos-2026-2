package com.uelbosque.gateway;

import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
@Component @Order(-2)
public class DownstreamErrorHandler implements WebExceptionHandler {
    public Mono<Void> handle(ServerWebExchange exchange,Throwable error) {
        if (!unavailable(error)) return Mono.error(error);
        var response=exchange.getResponse();
        response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body="{\"message\":\"Servicio no disponible\"}".getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
    }
    private boolean unavailable(Throwable error) {
        if (error instanceof java.net.ConnectException || error instanceof java.util.concurrent.TimeoutException) return true;
        return error.getCause()!=null && error.getCause()!=error && unavailable(error.getCause());
    }
}
