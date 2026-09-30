package com.uelbosque.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc @WithMockUser(username="admininicial",roles="ADMIN")
class SalesApiTest {
    @Autowired MockMvc mvc;
    @Test void rejectsInvalidQuantitiesBeforeCreatingSale() throws Exception {
        mvc.perform(post("/api/sales").header("Idempotency-Key","invalid").contentType(MediaType.APPLICATION_JSON)
            .content("{\"customerCedula\":\"123\",\"items\":[{\"productCode\":\"1\",\"quantity\":-1}]}"))
            .andExpect(status().isBadRequest());
    }
    @Test void listsInitiallyEmptySales() throws Exception {
        mvc.perform(get("/api/sales")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
    }
}
