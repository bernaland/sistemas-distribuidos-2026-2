package com.uelbosque.catalogservice;

import com.uelbosque.catalogservice.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
    "app.seed.enabled=true",
    "spring.datasource.url=jdbc:h2:mem:catalog-seed;DB_CLOSE_DELAY=-1"
})
class CatalogSeederTest {

    @MockBean
    private SupplierDirectory suppliers;

    @Autowired
    private ProductRepository repository;

    @Test
    @DisplayName("Debe ejecutar el seeder de catálogo sin fallos y cargar productos iniciales")
    void shouldSeedInitialProductsSuccessfully() {
        assertThat(repository.count()).isGreaterThanOrEqualTo(4);
        assertThat(repository.findByCodeAndActiveTrue("PROD-001")).isPresent();
        assertThat(repository.findByCodeAndActiveTrue("PROD-001").get().getSupplierNit()).isEqualTo("900112233");
    }
}
