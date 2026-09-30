package com.uelbosque.configserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(properties="spring.security.user.password=test-config-password") @AutoConfigureMockMvc
class ConfigServerTest {
    @Autowired MockMvc mvc;
    @Test @WithMockUser void servesNativeConfiguration() throws Exception {
        mvc.perform(get("/catalog-service/default")).andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("catalog-service")).andExpect(jsonPath("$.propertySources").isNotEmpty());
    }
    @Test void protectsConfiguration() throws Exception { mvc.perform(get("/catalog-service/default")).andExpect(status().isUnauthorized()); }
}
