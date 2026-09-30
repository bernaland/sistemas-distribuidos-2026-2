package com.uelbosque.userservice.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private final String secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long expirationMs = 3600000;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(secret, expirationMs);
    }

    @Test
    @DisplayName("Debe generar y validar token JWT correctamente")
    void shouldGenerateAndValidateToken() {
        String token = tokenProvider.generateToken("testuser", "ROLE_USER", "Test User", "test@example.com");

        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("Debe extraer username y roles del token")
    void shouldExtractClaims() {
        String token = tokenProvider.generateToken("testuser", "ROLE_USER,ROLE_ADMIN", "Test User", "test@example.com");

        assertEquals("testuser", tokenProvider.getUsernameFromToken(token));
        assertEquals("ROLE_USER,ROLE_ADMIN", tokenProvider.getRolesFromToken(token));
    }

    @Test
    @DisplayName("Debe retornar false para un token inválido o corrupto")
    void shouldReturnFalseForInvalidToken() {
        assertFalse(tokenProvider.validateToken("invalid.token.structure"));
        assertFalse(tokenProvider.validateToken(null));
    }
}
