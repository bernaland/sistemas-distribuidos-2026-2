import os

content = """# Módulo 6 – Customer Service y Supplier Service

## Análisis Detallado Archivo por Archivo (24 archivos)

### Submódulo: `services/customer-service` (12 archivos)

#### 1. `services/customer-service/Dockerfile`
- **Ruta:** `services/customer-service/Dockerfile`
- **Responsabilidad:** Construcción en contenedor Docker multi-stage del servicio `customer-service`.
- **Líneas clave:** L5 (`mvn package -pl customer-service`), L9 (`USER 10001`).
- **Problemas detectados:** Ninguno.

#### 2. `services/customer-service/README.md`
- **Ruta:** `services/customer-service/README.md`
- **Responsabilidad:** Documentación de `customer-service` y comando de verificación con Maven.
- **Líneas clave:** L5 (compilación aislada), L7 (PostgreSQL en ejecución, H2 en pruebas).
- **Problemas detectados:** Ninguno.

#### 3. `services/customer-service/pom.xml`
- **Ruta:** `services/customer-service/pom.xml`
- **Responsabilidad:** Configuración Maven del servicio de clientes.
- **Líneas clave:** L2 (`spring-boot-starter-parent:3.1.3`), L3 (`platform-common`, `data-jpa`, `validation`, `postgresql`, `h2`), L4 (`<testSourceDirectory>test</testSourceDirectory>`).
- **Problemas detectados:** Ninguno.

#### 4. `services/customer-service/src/main/java/com/uelbosque/customerservice/Application.java`
- **Ruta:** `services/customer-service/src/main/java/com/uelbosque/customerservice/Application.java`
- **Responsabilidad:** Clase principal con método `main` para arrancar Spring Boot.
- **Líneas clave:** L6 (`@SpringBootApplication`), L8 (`SpringApplication.run`).
- **Problemas detectados:** Ninguno.

#### 5. `services/customer-service/src/main/java/com/uelbosque/customerservice/Customer.java`
- **Ruta:** `services/customer-service/src/main/java/com/uelbosque/customerservice/Customer.java`
- **Responsabilidad:** Entidad JPA para la tabla `customers`.
- **Líneas clave:** L7 (`@Version private Long version`), L8 (`@Id private String cedula`), L9-12 (`name`, `address`, `phone`, `email`), L14-21 (constructor y mapeo con `CustomerData`).
- **Problemas detectados:** Buen uso de control de concurrencia optimista (`@Version`) y clave primaria natural (`cedula`).

#### 6. `services/customer-service/src/main/java/com/uelbosque/customerservice/CustomerController.java`
- **Ruta:** `services/customer-service/src/main/java/com/uelbosque/customerservice/CustomerController.java`
- **Responsabilidad:** Endpoints REST para CRUD de clientes (`/api/customers`).
- **Líneas clave:** L12-13 (POST 201 Created), L14 (GET list), L15 (GET by id), L16-17 (PUT update), L18-19 (DELETE 204 No Content).
- **Problemas detectados:** Métodos de una sola línea, cumpliendo sobradamente la directriz de <= 20 líneas.

#### 7. `services/customer-service/src/main/java/com/uelbosque/customerservice/CustomerData.java`
- **Ruta:** `services/customer-service/src/main/java/com/uelbosque/customerservice/CustomerData.java`
- **Responsabilidad:** Record inmutable de transporte para datos de cliente con validaciones Bean Validation.
- **Líneas clave:** L3 (`@Pattern(regexp="[0-9]{1,20}") @NotBlank cedula`), L4 (`name`), L5 (`address`), L6 (`phone`), L7 (`@Email email`).
- **Problemas detectados:** Ninguno.

#### 8. `services/customer-service/src/main/java/com/uelbosque/customerservice/CustomerRepository.java`
- **Ruta:** `services/customer-service/src/main/java/com/uelbosque/customerservice/CustomerRepository.java`
- **Responsabilidad:** Repositorio Spring Data JPA para la entidad `Customer` indexado por `cedula` (String).
- **Líneas clave:** L3 (`JpaRepository<Customer, String>`).
- **Problemas detectados:** Ninguno.

#### 9. `services/customer-service/src/main/java/com/uelbosque/customerservice/CustomerService.java`
- **Ruta:** `services/customer-service/src/main/java/com/uelbosque/customerservice/CustomerService.java`
- **Responsabilidad:** Lógica transaccional para clientes.
- **Líneas clave:** L13-16 (create con validación de existencia 409 CONFLICT), L18 (list), L20 (get), L21-26 (update verificando inmutabilidad de ID), L27 (delete).
- **Problemas detectados:** Ninguno; transacciones bien delimitadas (`@Transactional`).

#### 10. `services/customer-service/src/main/resources/application-distributed.yml`
- **Ruta:** `services/customer-service/src/main/resources/application-distributed.yml`
- **Responsabilidad:** Conexión opcional con Config Server bajo el perfil `distributed`.
- **Líneas clave:** L3 (`optional:configserver:${CONFIG_SERVER_URL:http://localhost:8888}`).
- **Problemas detectados:** Ninguno.

#### 11. `services/customer-service/src/main/resources/application.yml`
- **Ruta:** `services/customer-service/src/main/resources/application.yml`
- **Responsabilidad:** Configuración de `customer-service` (puerto 8089, datasource PostgreSQL `customers`).
- **Líneas clave:** L2 (`port: 8089`), L10 (`jdbc:postgresql://localhost:5432/customers`).
- **Problemas detectados:** Ninguno.

#### 12. `services/customer-service/test/com/uelbosque/customerservice/CustomerApiTest.java`
- **Ruta:** `services/customer-service/test/com/uelbosque/customerservice/CustomerApiTest.java`
- **Responsabilidad:** Pruebas de integración sobre H2 para el CRUD completo de clientes.
- **Líneas clave:** L22-25 (creación y lectura), L26-29 (rechazo de duplicados 409), L30-32 (rechazo de campos faltantes 400), L33-39 (actualización y eliminación).
- **Problemas detectados:** Cobertura de pruebas completa.

---

### Submódulo: `services/supplier-service` (12 archivos)

#### 13. `services/supplier-service/Dockerfile`
- **Ruta:** `services/supplier-service/Dockerfile`
- **Responsabilidad:** Construcción Docker para `supplier-service`.
- **Líneas clave:** L5 (`mvn package -pl supplier-service`), L9 (`USER 10001`).
- **Problemas detectados:** Ninguno.

#### 14. `services/supplier-service/README.md`
- **Ruta:** `services/supplier-service/README.md`
- **Responsabilidad:** Documentación de `supplier-service`.
- **Líneas clave:** L5 (compilación aislada), L7 (PostgreSQL en ejecución, H2 en pruebas).
- **Problemas detectados:** Ninguno.

#### 15. `services/supplier-service/pom.xml`
- **Ruta:** `services/supplier-service/pom.xml`
- **Responsabilidad:** Configuración Maven para el servicio de proveedores.
- **Líneas clave:** L2 (`spring-boot-starter-parent:3.1.3`), L3 (dependencias), L4 (`<testSourceDirectory>test</testSourceDirectory>`).
- **Problemas detectados:** Ninguno.

#### 16. `services/supplier-service/src/main/java/com/uelbosque/supplierservice/Application.java`
- **Ruta:** `services/supplier-service/src/main/java/com/uelbosque/supplierservice/Application.java`
- **Responsabilidad:** Clase principal con método `main` para arrancar Spring Boot.
- **Líneas clave:** L6 (`@SpringBootApplication`), L8 (`SpringApplication.run`).
- **Problemas detectados:** Ninguno.

#### 17. `services/supplier-service/src/main/java/com/uelbosque/supplierservice/Supplier.java`
- **Ruta:** `services/supplier-service/src/main/java/com/uelbosque/supplierservice/Supplier.java`
- **Responsabilidad:** Entidad JPA para la tabla `suppliers`.
- **Líneas clave:** L7 (`@Version private Long version`), L8 (`@Id private String nit`), L9-12 (`name`, `address`, `phone`, `city`), L14-21 (constructor y mapeo con `SupplierData`).
- **Problemas detectados:** Control de concurrencia optimista (`@Version`).

#### 18. `services/supplier-service/src/main/java/com/uelbosque/supplierservice/SupplierController.java`
- **Ruta:** `services/supplier-service/src/main/java/com/uelbosque/supplierservice/SupplierController.java`
- **Responsabilidad:** Endpoints REST para CRUD de proveedores (`/api/suppliers`).
- **Líneas clave:** L12-13 (POST 201 Created), L14 (GET list), L15 (GET by id), L16-17 (PUT update), L18-19 (DELETE 204 No Content).
- **Problemas detectados:** Ninguno.

#### 19. `services/supplier-service/src/main/java/com/uelbosque/supplierservice/SupplierData.java`
- **Ruta:** `services/supplier-service/src/main/java/com/uelbosque/supplierservice/SupplierData.java`
- **Responsabilidad:** Record inmutable de transporte para datos de proveedor.
- **Líneas clave:** L3 (`@Pattern(regexp="[0-9]{1,20}") @NotBlank nit`), L4 (`name`), L5 (`address`), L6 (`phone`), L7 (`city`).
- **Problemas detectados:** Ninguno.

#### 20. `services/supplier-service/src/main/java/com/uelbosque/supplierservice/SupplierRepository.java`
- **Ruta:** `services/supplier-service/src/main/java/com/uelbosque/supplierservice/SupplierRepository.java`
- **Responsabilidad:** Repositorio Spring Data JPA para la entidad `Supplier` indexado por `nit` (String).
- **Líneas clave:** L3 (`JpaRepository<Supplier, String>`).
- **Problemas detectados:** Ninguno.

#### 21. `services/supplier-service/src/main/java/com/uelbosque/supplierservice/SupplierService.java`
- **Ruta:** `services/supplier-service/src/main/java/com/uelbosque/supplierservice/SupplierService.java`
- **Responsabilidad:** Lógica transaccional para proveedores.
- **Líneas clave:** L13-16 (create con validación de existencia 409 CONFLICT), L18 (list), L20 (get), L21-26 (update verificando inmutabilidad de NIT), L27 (delete).
- **Problemas detectados:** Ninguno.

#### 22. `services/supplier-service/src/main/resources/application-distributed.yml`
- **Ruta:** `services/supplier-service/src/main/resources/application-distributed.yml`
- **Responsabilidad:** Conexión opcional con Config Server bajo el perfil `distributed`.
- **Líneas clave:** L3 (`optional:configserver:${CONFIG_SERVER_URL:http://localhost:8888}`).
- **Problemas detectados:** Ninguno.

#### 23. `services/supplier-service/src/main/resources/application.yml`
- **Ruta:** `services/supplier-service/src/main/resources/application.yml`
- **Responsabilidad:** Configuración de `supplier-service` (puerto 8090, datasource PostgreSQL `suppliers`).
- **Líneas clave:** L2 (`port: 8090`), L10 (`jdbc:postgresql://localhost:5432/suppliers`).
- **Problemas detectados:** Ninguno.

#### 24. `services/supplier-service/test/com/uelbosque/supplierservice/SupplierApiTest.java`
- **Ruta:** `services/supplier-service/test/com/uelbosque/supplierservice/SupplierApiTest.java`
- **Responsabilidad:** Pruebas de integración sobre H2 para el CRUD completo de proveedores.
- **Líneas clave:** L22-25 (creación y lectura por NIT), L26-29 (rechazo de duplicados 409), L30-32 (rechazo de campos faltantes 400), L33-39 (actualización y eliminación).
- **Problemas detectados:** Pruebas rigurosas y completas.
"""

with open('ANALISIS/07_modulo6_customer_supplier_service.md', 'w', encoding='utf-8') as f:
    f.write(content)

print('Módulo 6 guardado en ANALISIS/07_modulo6_customer_supplier_service.md')
