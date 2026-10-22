package com.uelbosque.supplierservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
    "app.seed.enabled=true",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:suppliers-seed;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "app.jwt.secret=test-secret-at-least-thirty-two-characters"
})
class SupplierSeederTest {

    @Autowired
    private SupplierRepository repository;

    @Test
    @DisplayName("Debe sembrar el proveedor inicial 900112233 cuando app.seed.enabled=true")
    void shouldSeedSupplierWhenEnabled() {
        assertThat(repository.existsById("900112233")).isTrue();
        assertThat(repository.findById("900112233").get().data().name()).contains("Proveedor Principal");
    }
}
