package com.uelbosque.platformcommon;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.RestClientException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.util.Map;
@RestControllerAdvice
@org.springframework.core.annotation.Order(org.springframework.core.Ordered.HIGHEST_PRECEDENCE)
public class ApiErrors {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> status(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", e.getReason() == null ? "Solicitud rechazada" : e.getReason()));
    }
    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class, HttpMessageNotReadableException.class,
        org.springframework.web.bind.ServletRequestBindingException.class,
        org.springframework.web.multipart.support.MissingServletRequestPartException.class,
        org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class})
    public ResponseEntity<?> invalid(Exception e) {
        return ResponseEntity.badRequest().body(Map.of("message", "Datos invalidos o incompletos"));
    }
    @ExceptionHandler({DataIntegrityViolationException.class, OptimisticLockingFailureException.class})
    public ResponseEntity<?> conflict(Exception e) {
        return ResponseEntity.status(409).body(Map.of("message", "Conflicto: registro duplicado o modificado concurrentemente"));
    }
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<?> unavailable(Exception e) {
        return ResponseEntity.status(503).body(Map.of("message", "Servicio dependiente no disponible"));
    }
}
