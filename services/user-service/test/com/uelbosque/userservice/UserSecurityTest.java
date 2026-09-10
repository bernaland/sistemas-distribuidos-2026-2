package com.uelbosque.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc
class UserSecurityTest {
    @Autowired MockMvc mvc;
    @Test void anonymousCannotCreateAdmin() throws Exception {
        mvc.perform(post("/api/users").contentType("application/json").content("{}")).andExpect(status().isUnauthorized());
    }
    @Test @WithMockUser(roles="USER") void operatorCannotManageUsers() throws Exception {
        mvc.perform(post("/api/users").contentType("application/json").content("{}")).andExpect(status().isForbidden());
    }
    @Test @WithMockUser(roles="ADMIN") void bootstrapHasBusinessIdentity() throws Exception {
        mvc.perform(get("/api/users/cedula/0")).andExpect(status().isOk()).andExpect(jsonPath("$.username").value("admininicial"))
            .andExpect(jsonPath("$.password").doesNotExist());
    }
    @Test void invalidPasswordIsRejected() throws Exception {
        mvc.perform(post("/api/users/login").contentType("application/json")
            .content("{\"username\":\"admininicial\",\"password\":\"wrong\"}")).andExpect(status().isUnauthorized());
    }
}
