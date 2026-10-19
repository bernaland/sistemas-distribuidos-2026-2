package com.uelbosque.catalogservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc @WithMockUser(roles="ADMIN")
class CsvImportTest {
    @Autowired MockMvc mvc;
    @MockBean SupplierDirectory suppliers;
    @Test void replacesCatalogAndPreservesPreviousDataWhenInvalid() throws Exception {
        var valid = new MockMultipartFile("file", "productos.csv", "text/csv", "1,Leche,123,1000,0,1500\n2,Arroz,123,2000,5,3000".getBytes());
        mvc.perform(multipart("/api/products/import").file(valid)).andExpect(status().isOk()).andExpect(jsonPath("$.imported").value(2));
        var invalid = new MockMultipartFile("file", "bad.csv", "text/csv", "3,Producto,123,1000,19,1500\n4,Malo,123,error,19,10".getBytes());
        mvc.perform(multipart("/api/products/import").file(invalid)).andExpect(status().isBadRequest());
        mvc.perform(get("/api/products/code/1")).andExpect(status().isOk()).andExpect(jsonPath("$.supplierNit").value("123"));
        mvc.perform(get("/api/products/code/3")).andExpect(status().isNotFound());
    }
    @Test void rejectsNonCsv() throws Exception {
        var file = new MockMultipartFile("file", "productos.txt", "text/plain", "1,A,123,10,19,20".getBytes());
        mvc.perform(multipart("/api/products/import").file(file)).andExpect(status().isBadRequest());
    }
    @Test void rejectsMissingFile() throws Exception {
        mvc.perform(multipart("/api/products/import")).andExpect(status().isBadRequest());
    }
    @Test void rejectsUnknownSupplier() throws Exception {
        org.mockito.Mockito.doThrow(new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Proveedor inexistente"))
            .when(suppliers).require("999");
        var file=new MockMultipartFile("file","productos.csv","text/csv","99,Producto,999,10,19,20".getBytes());
        mvc.perform(multipart("/api/products/import").file(file)).andExpect(status().isBadRequest());
        mvc.perform(get("/api/products/code/99")).andExpect(status().isNotFound());
    }
}
