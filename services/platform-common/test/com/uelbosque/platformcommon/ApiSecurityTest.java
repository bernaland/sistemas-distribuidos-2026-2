package com.uelbosque.platformcommon;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;
import org.springframework.test.web.servlet.MockMvc;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.*;
import java.util.Date;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(classes=ApiSecurityTest.TestApplication.class,properties="app.jwt.secret=test-secret-at-least-thirty-two-characters")
@AutoConfigureMockMvc
class ApiSecurityTest {
    @SpringBootConfiguration @EnableAutoConfiguration(exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class)
    @Import(Probe.class) static class TestApplication {}
    @RestController static class Probe {
        @GetMapping("/api/probe") public String read() { return "ok"; }
        @PostMapping("/api/probe") public String write() { return "ok"; }
    }
    @Autowired MockMvc mvc;
    @Test void verifiesSignatureAndExpiration() throws Exception {
        mvc.perform(get("/api/probe").header("Authorization","Bearer "+token("ROLE_USER",300000))).andExpect(status().isOk());
        mvc.perform(get("/api/probe").header("Authorization","Bearer "+token("ROLE_USER",-300000))).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/probe").header("Authorization","Bearer malformed")).andExpect(status().isUnauthorized());
    }
    @Test void requiresAdminForManagement() throws Exception {
        mvc.perform(post("/api/probe").header("Authorization","Bearer "+token("ROLE_USER",300000))).andExpect(status().isForbidden());
        mvc.perform(post("/api/probe").header("Authorization","Bearer "+token("ROLE_ADMIN",300000))).andExpect(status().isOk());
    }
    private String token(String roles,long expiry) throws Exception {
        var claims=new JWTClaimsSet.Builder().subject("test").claim("roles",roles).expirationTime(new Date(System.currentTimeMillis()+expiry)).build();
        var token=new SignedJWT(new JWSHeader(JWSAlgorithm.HS256),claims);
        token.sign(new MACSigner("test-secret-at-least-thirty-two-characters"));
        return token.serialize();
    }
}
