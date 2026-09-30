package com.uelbosque.paymentservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:payments", "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=", "app.jwt.secret=test-secret-at-least-thirty-two-characters"})
@AutoConfigureMockMvc
class EndpointTest {
    @Autowired MockMvc mvc;
    @Test @WithMockUser(roles="ADMIN") void listsEmptyRecords() throws Exception {
        mvc.perform(get("/api/payments")).andExpect(status().isOk());
    }
    @Test void rejectsAnonymousAccess() throws Exception {
        mvc.perform(get("/api/payments")).andExpect(status().isUnauthorized());
    }
}
