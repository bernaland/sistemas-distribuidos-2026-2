package com.uelbosque.customerservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(properties={"spring.jpa.hibernate.ddl-auto=create-drop", "spring.datasource.url=jdbc:h2:mem:customers;DB_CLOSE_DELAY=-1", "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=", "app.jwt.secret=test-secret-at-least-thirty-two-characters"})
@AutoConfigureMockMvc
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WithMockUser(roles="ADMIN")
class CustomerApiTest {
    @Autowired MockMvc mvc;
    private final String body = """
        {"cedula":"123456","name":"Persona prueba","address":"Calle 1","phone":"3001234567","email":"test@example.com"}
        """;
    @Test void createsAndReadsByBusinessId() throws Exception {
        mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated());
        mvc.perform(get("/api/customers/123456")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Persona prueba"));
    }
    @Test void rejectsDuplicates() throws Exception {
        mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated());
        mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isConflict());
    }
    @Test void rejectsMissingFields() throws Exception {
        mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
    }
    @Test void updatesAndDeletes() throws Exception {
        mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated());
        mvc.perform(put("/api/customers/123456").contentType(MediaType.APPLICATION_JSON).content(body.replace("Persona prueba", "Nuevo nombre"))).andExpect(status().isOk());
        mvc.perform(get("/api/customers/123456")).andExpect(jsonPath("$.name").value("Nuevo nombre"));
        mvc.perform(delete("/api/customers/123456")).andExpect(status().isNoContent());
        mvc.perform(get("/api/customers/123456")).andExpect(status().isNotFound());
    }
}
