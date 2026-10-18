# -*- coding: utf-8 -*-
"""
Script to generate ANALISIS/08_modulo7_catalog_service.md and update checklist for Module 7.
"""
import os
import subprocess

content = """# Auditoría Técnica - Módulo 7: Catalog Service (`services/catalog-service`)

**Auditor Senior:** Antigravity  
**Fecha:** 2026-10-18  
**Total de Archivos Auditados en este Módulo:** 28  

---

## 1. Resumen Ejecutivo del Módulo

`catalog-service` gestiona el maestro de productos y catálogo de la tienda genérica. Expone operaciones CRUD estándar sobre productos (`/api/products`) y un mecanismo masivo de importación vía archivos CSV (`/api/products/import`).
Sus aspectos técnicos más destacados incluyen:
1. **Control de Concurrencia Pesimista (`CatalogMutex` / `CatalogWriteLock`):** Para evitar inconsistencias o mezclas de datos durante la carga masiva concurrente de CSV, implementa un bloqueo pesimista a nivel de base de datos (`LockModeType.PESSIMISTIC_WRITE` sobre la fila `id=1`), garantizando exclusión mutua distribuida (`CatalogLockRepository.java:10`).
2. **Validación de Integridad Referencial Distribuida (`SupplierDirectory`):** Valida síncronamente vía HTTP contra `customer-service` / `supplier-service` (por defecto `http://localhost:8090/api/suppliers/{nit}`) que cada proveedor (`supplierNit`) exista antes de persistir o importar productos (`SupplierDirectory.java:18-22`).
3. **Manejo Dinámico de Tests en Maven (`build-helper-maven-plugin`):** Contiene pruebas de integración y concurrencia no solo en `src/test/java`, sino también en la carpeta raíz del servicio `test/com/uelbosque/catalogservice/`, conectadas formalmente mediante el plugin `build-helper-maven-plugin:3.5.0` en `pom.xml:51`.

---

## 2. Inventario y Análisis Detallado Archivo por Archivo (28 Archivos)

### 2.1 Configuración de Construcción, Contenedores y Documentación (3 archivos)

#### 1. `services/catalog-service/pom.xml` (59 líneas)
- **Propósito:** Descriptor Maven del microservicio.
- **Detalles Técnicos:**
  - `parent`: `spring-boot-starter-parent` versión `3.1.3`, Java 17 (`pom.xml:10-16`).
  - Dependencias: `commons-csv:1.10.0` para procesamiento robusto de CSV (`pom.xml:20`), `platform-common:0.0.1-SNAPSHOT` para seguridad JWT compartida (`pom.xml:20`), `spring-boot-starter-data-jpa`, `postgresql`, `spring-boot-starter-validation`, `spring-cloud-starter-config:4.0.4`.
  - Configuración especial de build (`pom.xml:50-54`): incluye la meta `build-helper-maven-plugin:3.5.0` agregando el directorio `test` como fuente de test adicional (`add-test-source`), permitiendo compilar y ejecutar los tests situados en `test/com/uelbosque/catalogservice/`.

#### 2. `services/catalog-service/Dockerfile` (10 líneas)
- **Propósito:** Definición de contenedor multi-stage.
- **Detalles Técnicos:**
  - Build stage: `maven:3.9.9-eclipse-temurin-17` ejecutando `mvn -B -f services/pom.xml -pl catalog-service -am package -DskipTests` (`Dockerfile:2-5`).
  - Runtime stage: `eclipse-temurin:17-jre`, usuario no privilegiado `USER 10001` (`Dockerfile:6-9`).

#### 3. `services/catalog-service/README.md` (7 líneas)
- **Propósito:** Documentación rápida de ejecución y arquitectura del servicio.
- **Detalles Técnicos:** Especifica puertos, referencia a `docs/BACKEND.md` y comando de verificación con Maven wrapper (`README.md:1-7`).

---

### 2.2 Núcleo de Concurrencia y Sincronización Distribuida (4 archivos)

#### 4. `services/catalog-service/src/main/java/com/uelbosque/catalogservice/CatalogWriteLock.java` (11 líneas)
- **Propósito:** Entidad JPA para el mecanismo de cerrojo distribuido.
- **Detalles Técnicos:**
  - Mapea a la tabla implícita `catalog_write_lock` con campos `@Id private Long id` y `@Version private Long version` (`CatalogWriteLock.java:6-10`).
  - Sirve como recurso atómico de bloqueo exclusivo en PostgreSQL.

#### 5. `services/catalog-service/src/main/java/com/uelbosque/catalogservice/CatalogLockRepository.java` (11 líneas)
- **Propósito:** Repositorio JPA con soporte para bloqueo pesimista.
- **Detalles Técnicos:**
  - Método `acquire()` (`CatalogLockRepository.java:8-10`): Anotado con `@Lock(LockModeType.PESSIMISTIC_WRITE)` y consulta `@Query("select l from CatalogWriteLock l where l.id=1")`, ejecutando un `SELECT ... FOR UPDATE` en SQL subyacente.

#### 6. `services/catalog-service/src/main/java/com/uelbosque/catalogservice/CatalogMutex.java` (25 líneas)
- **Propósito:** Componente de exclusión mutua para operaciones atómicas del catálogo.
- **Detalles Técnicos:**
  - Inicialización: Escucha `ApplicationReadyEvent` (`CatalogMutex.java:12-18`) para asegurar que la fila `id=1L` exista en la base de datos, atrapando colisiones de inserción concurrentes (`DataIntegrityViolationException`).
  - Adquisición: Método `acquire()` (`CatalogMutex.java:19-23`) con transacción obligatoria `@Transactional(propagation=Propagation.MANDATORY)` garantizando que el invocador esté dentro de una transacción activa antes de bloquear.

#### 7. `services/catalog-service/src/main/java/com/uelbosque/catalogservice/SupplierDirectory.java` (23 líneas)
- **Propósito:** Cliente HTTP síncrono para verificar la existencia del proveedor en `supplier-service`.
- **Detalles Técnicos:**
  - Consume endpoint `${services.suppliers:http://localhost:8090}/api/suppliers/{nit}` mediante `RestTemplate` (`SupplierDirectory.java:15-18`).
  - Lanza `ResponseStatusException(HttpStatus.BAD_REQUEST, "Proveedor no registrado: " + nit)` si el proveedor no existe (`SupplierDirectory.java:19-21`), o `IllegalArgumentException` si el formato del NIT es inválido.

---

### 2.3 Procesamiento e Importación de CSV (3 archivos)

#### 8. `services/catalog-service/src/main/java/com/uelbosque/catalogservice/ProductCsvParser.java` (56 líneas)
- **Propósito:** Parser estructurado para archivos CSV de catálogo.
- **Detalles Técnicos:**
  - Valida extensión `.csv` y codificación UTF-8 (`ProductCsvParser.java:14-17`).
  - Maneja y remueve marcas BOM (`\uFEFF`) en la primera columna (`ProductCsvParser.java:27, 36`).
  - Exige exactamente 6 columnas (`codigo_producto`, `nombre_producto`, `nitproveedor`, `precio_compra`, `ivacompra`, `precio_venta`) (`ProductCsvParser.java:26, 35`).
  - Valida límites de longitud, duplicados en memoria usando `Set<String> codes`, valores monetarios no negativos con máximo 2 decimales y escala hasta 10 dígitos (`ProductCsvParser.java:49-55`).

#### 9. `services/catalog-service/src/main/java/com/uelbosque/catalogservice/ProductImportService.java` (25 líneas)
- **Propósito:** Servicio transaccional para reemplazo atómico del catálogo.
- **Detalles Técnicos:**
  - Método `replace(MultipartFile file)` (`ProductImportService.java:17-24`):
    1. Parsea el archivo CSV en memoria.
    2. Valida todos los proveedores únicos contra `SupplierDirectory::require`.
    3. Adquiere el cerrojo pesimista `mutex.acquire()`.
    4. Ejecuta `repository.deleteAllInBatch()` para limpiar el catálogo anterior.
    5. Inserta los nuevos productos con `repository.saveAllAndFlush(products)`.

#### 10. `services/catalog-service/src/main/java/com/uelbosque/catalogservice/ProductImportController.java` (14 líneas)
- **Propósito:** Controlador REST para subida de archivos CSV.
- **Detalles Técnicos:**
  - Expone `POST /api/products/import` con `consumes = "multipart/form-data"` (`ProductImportController.java:10-13`), retornando el número de productos importados y mensaje de éxito.

---

### 2.4 Controladores, Servicios, Entidades y Repositorios (10 archivos)

#### 11. `services/catalog-service/src/main/java/com/uelbosque/catalogservice/CatalogServiceApplication.java` (12 líneas)
- **Propósito:** Clase principal de arranque de Spring Boot.
- **Detalles Técnicos:** Anotada con `@SpringBootApplication` y método `main` (`CatalogServiceApplication.java:7-11`).

#### 12. `services/catalog-service/src/main/java/com/uelbosque/catalogservice/model/Product.java` (88 líneas)
- **Propósito:** Entidad JPA `products`.
- **Detalles Técnicos:**
  - Campos: `id` (autoincremental), `code` (único, no nulo, length 50), `name` (length 150), `description` (length 500), `purchasePrice` (numeric 12,2), `salePrice` (numeric 12,2), `ivaRate` (numeric 5,2), `category` (length 100), `imageUrl` (length 500), `active` (boolean, default true), `supplierNit` (`Product.java:8-41`).

#### 13. `services/catalog-service/src/main/java/com/uelbosque/catalogservice/repository/ProductRepository.java` (21 líneas)
- **Propósito:** Repositorio JPA para productos.
- **Detalles Técnicos:**
  - Define métodos derivados: `findByCode`, `findByCodeAndActiveTrue`, `findByActiveTrue`, `findByCategoryIgnoreCaseAndActiveTrue`, `findByNameContainingIgnoreCaseAndActiveTrue`, `existsByCode` (`ProductRepository.java:11-16`).
  - Consulta personalizada JPQL `@Query` para listar categorías distintas activas: `findDistinctCategories()` (`ProductRepository.java:18-20`).

#### 14. `services/catalog-service/src/main/java/com/uelbosque/catalogservice/service/ProductService.java` (121 líneas)
- **Propósito:** Lógica de negocio para productos.
- **Detalles Técnicos:**
  - Maneja `createProduct`, `updateProduct`, `deleteProduct` (soft-delete seteando `active = false`), `getProductById`, `getProductByCode`, `searchProducts` y `getCategories`.
  - En `createProduct` y `updateProduct`, invoca `suppliers.require(dto.getSupplierNit())` y adquiere el cerrojo `mutex.acquire()` para garantizar consistencia distribuida (`ProductService.java:28-29, 62-63`).

#### 15. `services/catalog-service/src/main/java/com/uelbosque/catalogservice/controller/ProductController.java` (65 líneas)
- **Propósito:** Controlador REST para CRUD y búsqueda de productos.
- **Detalles Técnicos:**
  - Mapeado en `/api/products` con soporte CORS abierto (`ProductController.java:12-14`).
  - Endpoints: `POST /`, `GET /` (con filtros `query` y `category`), `GET /categories`, `GET /{id}`, `GET /code/{code}`, `PUT /{id}`, `DELETE /{id}` (`ProductController.java:21-63`).

#### 16. `services/catalog-service/src/main/java/com/uelbosque/catalogservice/dto/ProductRequestDto.java` (68 líneas)
- **Propósito:** DTO de entrada con validaciones Jakarta.
- **Detalles Técnicos:**
  - Validaciones: `@NotBlank` en código y nombre, `@NotNull` y `@DecimalMin("0.0")` en precios e IVA (`ProductRequestDto.java:12-32`).

#### 17. `services/catalog-service/src/main/java/com/uelbosque/catalogservice/dto/ProductResponseDto.java` (67 líneas)
- **Propósito:** DTO de salida con atributos completos del producto, incluyendo `supplierNit` y estado `active` (`ProductResponseDto.java:5-30`).

#### 18. `services/catalog-service/src/main/java/com/uelbosque/catalogservice/config/DataInitializer.java` (54 líneas)
- **Propósito:** Seeder inicial condicional de productos de prueba.
- **Detalles Técnicos:**
  - Anotado con `@ConditionalOnProperty(name="app.seed.enabled", havingValue="true")` (`DataInitializer.java:12`).
  - **Hallazgo Crítico:** Si se activa `app.seed.enabled=true`, el método `createSample` no asigna `supplierNit` en el DTO (`DataInitializer.java:43-52`), lo que provoca una excepción en tiempo de ejecución al invocar `ProductService.createProduct` debido a la validación de `SupplierDirectory.require(null)`.

#### 19. `services/catalog-service/src/main/java/com/uelbosque/catalogservice/exception/ResourceNotFoundException.java` (7 líneas)
- **Propósito:** Excepción para entidad no encontrada (`HttpStatus.NOT_FOUND`).

#### 20. `services/catalog-service/src/main/java/com/uelbosque/catalogservice/exception/DuplicateResourceException.java` (7 líneas)
- **Propósito:** Excepción para conflicto por código de producto duplicado (`HttpStatus.CONFLICT`).

#### 21. `services/catalog-service/src/main/java/com/uelbosque/catalogservice/exception/GlobalExceptionHandler.java` (54 líneas)
- **Propósito:** Manejador centralizado de errores REST (`@RestControllerAdvice`).
- **Detalles Técnicos:** Captura `ResourceNotFoundException`, `DuplicateResourceException`, `MethodArgumentNotValidException` y `Exception` general (`GlobalExceptionHandler.java:17-45`).

---

### 2.5 Configuración de Entornos y Recursos (3 archivos)

#### 22. `services/catalog-service/src/main/resources/application.yml` (29 líneas)
- **Propósito:** Configuración por defecto para ejecución local y PostgreSQL.
- **Detalles Técnicos:**
  - Puerto HTTP: `8082` (`application.yml:2`).
  - Base de datos: `jdbc:postgresql://localhost:5432/catalog` (`application.yml:9`).
  - Límite de carga Multipart: `5MB` (`application.yml:19`).
  - Parámetros JWT y endpoints de Actuator (`health`, `info`).

#### 23. `services/catalog-service/src/main/resources/application-distributed.yml` (6 líneas)
- **Propósito:** Perfil para configuración centralizada mediante Spring Cloud Config.
- **Detalles Técnicos:** Importa configuración de `${CONFIG_SERVER_URL:http://localhost:8888}` (`application-distributed.yml:3`).

#### 24. `services/catalog-service/src/test/resources/application.properties` (7 líneas)
- **Propósito:** Configuración de base de datos en memoria H2 para tests unitarios y de integración.
- **Detalles Técnicos:** `jdbc:h2:mem:testdb`, `ddl-auto=create-drop`, `app.seed.enabled=false`.

---

### 2.6 Batería de Pruebas Unitarias y de Integración (4 archivos)

#### 25. `services/catalog-service/src/test/java/com/uelbosque/catalogservice/controller/ProductControllerTest.java` (147 líneas)
- **Propósito:** Pruebas unitarias de la capa web con `MockMvc` y Mockito.
- **Detalles Técnicos:** Valida creación (201), validación de campos obligatorios (400), consulta por ID y código, actualización y eliminación.

#### 26. `services/catalog-service/src/test/java/com/uelbosque/catalogservice/service/ProductServiceTest.java` (158 líneas)
- **Propósito:** Pruebas unitarias de la capa de servicio con mocks para `ProductRepository`, `SupplierDirectory` y `CatalogMutex`.
- **Detalles Técnicos:** Verifica transacciones de creación, rechazo por código duplicado, búsquedas por categoría y soft-deletes.

#### 27. `services/catalog-service/test/com/uelbosque/catalogservice/CsvConcurrencyTest.java` (34 líneas)
- **Propósito:** Prueba de integración concurrente multi-hilo para carga de CSV.
- **Detalles Técnicos:**
  - Utiliza `CountDownLatch(1)` y `Executors.newFixedThreadPool(2)` para disparar dos peticiones simultáneas de importación de CSV (`CsvConcurrencyTest.java:20-29`).
  - Verifica que gracias a `CatalogMutex`, una operación no se sobreponga de manera inconsistente con la otra, terminando con exactamente 1 registro íntegro en base de datos.

#### 28. `services/catalog-service/test/com/uelbosque/catalogservice/CsvImportTest.java` (39 líneas)
- **Propósito:** Pruebas de integración HTTP para el endpoint `/api/products/import`.
- **Detalles Técnicos:**
  - Prueba reemplazo total exitoso con CSV válido (`CsvImportTest.java:18-20`).
  - Prueba atomicidad transaccional: un CSV con errores numéricos revierte toda la transacción y preserva el catálogo anterior (`CsvImportTest.java:21-25`).
  - Prueba rechazo de archivos sin formato CSV (`CsvImportTest.java:27-30`).
  - Prueba rechazo cuando el proveedor (`supplierNit`) no existe en el directorio (`CsvImportTest.java:35-41`).

---

## 3. Matriz de Hallazgos y Riesgos en `catalog-service`

| ID | Severidad | Archivo y Línea | Descripción del Problema | Recomendación Técnica |
|---|---|---|---|---|
| **CAT-01** | **Medio** | `config/DataInitializer.java:44-52` | `DataInitializer` no setea `supplierNit` en las muestras, causando `IllegalArgumentException` si `app.seed.enabled=true`. | Asignar un NIT de proveedor válido en `createSample` antes de llamar a `createProduct`. |
| **CAT-02** | **Bajo** | Estructura de carpetas (`test/` vs `src/test/java/`) | Separación inusual de tests en carpeta `test/` fuera del estándar Maven `src/test/java/`. | Si bien está resuelto mediante `build-helper-maven-plugin`, se aconseja unificar todos los tests dentro de `src/test/java` para evitar confusiones en IDEs. |
| **CAT-03** | **Bajo** | `service/ProductService.java:32, 65` | Caracteres de codificación dañados en mensajes de error (`cdigo`, `Lcteos`). | Corregir la codificación de archivo a UTF-8 explícito en los strings de excepciones. |
"""

with open('ANALISIS/08_modulo7_catalog_service.md', 'w', encoding='utf-8') as f:
    f.write(content.strip() + '\n')

print("Created ANALISIS/08_modulo7_catalog_service.md successfully.")
