package com.uelbosque.userservice.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String secret;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms:86400000}") long expirationMs) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username, String roles, String name, String email) {
        try {
            JWSSigner signer = new MACSigner(secret.getBytes(StandardCharsets.UTF_8));
            Date now = new Date();
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .claim("roles", roles)
                    .claim("name", name)
                    .claim("email", email)
                    .issueTime(now)
                    .expirationTime(new Date(now.getTime() + expirationMs))
                    .build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new IllegalStateException("Error al generar token JWT", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secret.getBytes(StandardCharsets.UTF_8));
            if (!signedJWT.verify(verifier)) {
                return false;
            }
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            return expiration != null && expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Token inv叩lido", e);
        }
    }

    public String getRolesFromToken(String token) {
        try {
            Object roles = SignedJWT.parse(token).getJWTClaimsSet().getClaim("roles");
            return roles != null ? roles.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }
}
