# -*- coding: utf-8 -*-
"""
Script to generate ANALISIS/13_fase4_calidad_y_riesgos.md (Fase 4: Calidad y Riesgos).
"""
import os

content = """# Auditoría Técnica - Fase 4: Calidad, Seguridad y Riesgos del Sistema

**Auditor Senior:** Antigravity  
**Fecha:** 2026-10-18  
**Alcance:** Consolidación de riesgos transversales sobre los 359 archivos auditados.

---

## 1. Bugs Potenciales y Errores en Código Real

### 1.1 Error Fatal de Compilación en `user-service` (Severidad: CRÍTICA)
- **Evidencia:** `services/user-service/src/main/java/com/uelbosque/userservice/DataInitializer.java:24`
- **Diagnóstico:** Invoca `new User(defaultUsername, encoder.encode("admin123456"), "ROLE_ADMIN")`. La entidad `User.java` solo posee los constructores `User()` y `User(username, password, name, email, roles)`.
- **Impacto:** `./services/mvnw.cmd package` falla con `[ERROR] no suitable constructor found for User(java.lang.String,java.lang.String,java.lang.String)`. El servicio no puede construirse ni desplegarse.
- **Causa Raíz:** Este archivo es un remanente huérfano duplicado de `com.uelbosque.userservice.config.DataInitializer.java`.

### 1.2 Fallo en Seeder de Catálogo si se Habilita en Producción (Severidad: MEDIA)
- **Evidencia:** `services/catalog-service/src/main/java/com/uelbosque/catalogservice/config/DataInitializer.java:44-52`
- **Diagnóstico:** Si `app.seed.enabled=true`, el método `createSample` construye instancias de `ProductRequestDto` sin setear `supplierNit`. Al invocar `ProductService.createProduct`, este llama a `SupplierDirectory.require(dto.getSupplierNit())`, lanzando `IllegalArgumentException: NIT obligatorio`.
- **Impacto:** Crash al arrancar la aplicación si se activa el seeding de catálogo.

### 1.3 Versión Inexistente de PostgreSQL en Contenedor (Severidad: ALTA)
- **Evidencia:** `compose.yml:4`
- **Diagnóstico:** La imagen definida es `postgres:18.6`. La versión 18 de PostgreSQL no ha sido lanzada (la versión actual es 16/17).
- **Impacto:** `docker compose up` falla de inmediato al no poder descargar (`pull`) la imagen desde Docker Hub.

---

## 2. Seguridad y Vulnerabilidades

### 2.1 Autenticación Simulada y Hardcodeada en el Frontend (Severidad: ALTA)
- **Evidencia:** `frontend/tienda-frontend/src/app/organisms/login.organism.ts:85`
- **Diagnóstico:** La validación de credenciales evalúa `this.username === 'admin' && this.password === 'admin'`. No realiza petición HTTP `POST /api/users/login` hacia el Gateway ni recibe tokens JWT.
- **Impacto:** Cualquier usuario puede eludir la pantalla de login o ver rutas protegidas simplemente navegando a `/dashboard`.

### 2.2 Secretos JWT por Defecto y Compartidos (Severidad: MEDIA)
- **Evidencia:** `services/platform-common/src/main/java/com/uelbosque/platformcommon/security/JwtService.java:31` y `compose.yml:19`
- **Diagnóstico:** Utiliza una clave por defecto si `${JWT_SECRET}` no está definida. Además, la clave predeterminada en `compose.yml` (`supersecretjwtkeyforuniversityproject2026distributedsystems`) es conocida públicamente en el repositorio.
- **Impacto:** Riesgo de falsificación de tokens JWT si un atacante conoce el repositorio.

### 2.3 Exposición Indiscriminada de CORS (Severidad: MEDIA)
- **Evidencia:** Anotación `@CrossOrigin(origins = "*")` en prácticamente todos los controladores (`ProductController`, `UserController`, `CustomerController`, `SupplierController`).
- **Diagnóstico:** Permite que cualquier origen web ejecute solicitudes cross-origin contra los microservicios si se exponen directamente fuera de la red interna de Docker.

### 2.4 Endpoints de Actuator sin Restricción de Acceso (Severidad: BAJA)
- **Evidencia:** `application.yml:management.endpoints.web.exposure.include: health,info` en todos los servicios.
- **Diagnóstico:** Si bien solo expone `health` e `info`, están configurados con `permitAll()` en `SecurityConfig.java:23` de `platform-common`. Se debe vigilar que endpoints sensibles (`env`, `beans`, `mappings`) permanezcan deshabilitados.

---

## 3. Rendimiento, Concurrencia y Escalabilidad

### 3.1 Acoplamiento Síncrono en Cascada sin Resiliencia (Severidad: ALTA)
- **Evidencia:** `SaleDirectory.java:32-40`, `CatalogReader.java:14-19`, `ReportClient.java:19-21`.
- **Diagnóstico:** Los servicios utilizan `RestTemplate.getForObject` síncrono sin configurar timeouts de conexión ni de lectura (`readTimeout`), y sin aplicar patrones de Circuit Breaker (Resilience4j).
- **Impacto:** En caso de degradación o lentitud en `customer-service` o `catalog-service`, los hilos de Tomcat de `order-service` o `admin-service` se bloquearán rápidamente provocando agotamiento del pool de conexiones (*thread starvation* y fallo en cascada).

### 3.2 Cuellos de Botella por Bloqueo Pesimista en Base de Datos (Severidad: MEDIA)
- **Evidencia:** `CatalogMutex.java:21`, `StockRepository.java:9`, `ReservationRepository.java:9`.
- **Diagnóstico:**
  - `CatalogMutex` bloquea la fila `id=1` de `catalog_write_lock` en modo `PESSIMISTIC_WRITE` para serializar la carga de CSV y las creaciones/actualizaciones individuales de productos.
  - `StockRepository` bloquea registros individuales de existencias en cada reserva de inventario.
- **Impacto:** Si bien garantiza consistencia estricta, limita drásticamente el *throughput* concurrente, serializando las transacciones sobre el catálogo y los productos populares.

### 3.3 Consultas Masivas sin Paginación (Severidad: MEDIA)
- **Evidencia:** `ProductService.getAllProducts()` (`ProductService.java:38`), `CustomerService.getAllCustomers()`, `SaleService.list()`, `OrderService.getAllOrders()`.
- **Diagnóstico:** Utilizan `repository.findAll()` retornando la totalidad de los registros a memoria sin `Pageable` ni limitación en el payload HTTP.
- **Impacto:** Riesgo de consumo excesivo de memoria (`OutOfMemoryError`) y latencias altas a medida que aumente el volumen de transacciones y productos.

---

## 4. Deuda Técnica y Code Smells

### 4.1 Desconexión Total del Frontend (Severidad: CRÍTICA)
- **Evidencia:** `frontend/tienda-frontend/src/app/app.module.ts:80-90`
- **Diagnóstico:** Ausencia de `HttpClientModule`. Todas las pantallas operan sobre arrays volátiles en memoria del navegador (`clientsList`, `productsList`, `usersList`).
- **Impacto:** La plataforma web no interactúa en lo absoluto con los microservicios backend desarrollados.

### 4.2 Incumplimiento de Directrices Normativas del Repositorio (`AGENTS.md`) (Severidad: ALTA)
- **Versión de Angular (`AGENTS.md:4`):** El repositorio exige la última versión de Angular. La aplicación corre sobre Angular 16.2.0 (EOL Noviembre 2024; versiones actuales: v18 / v19).
- **Componentes Funcionales (`AGENTS.md:11`):** Dicta "usar functional components, nunca clases". En Angular, todos los componentes son clases (`export class ...`). El proyecto tampoco utiliza la sintaxis moderna de Standalone Components ni Signals.

### 4.3 Dualidad Conceptual en `order-service` (Severidad: MEDIA)
- **Evidencia:** Coexistencia de `/api/sales` (`Sale.java`) y `/api/orders` (`Order.java`).
- **Diagnóstico:** El servicio mantiene dos modelos de negocio paralelos para transacciones comerciales, cada uno con su propio conjunto de entidades, repositorios, controladores y pruebas.

### 4.4 Inconsistencia Estructural en Carpetas de Pruebas (Severidad: BAJA)
- **Evidencia:** Separación entre `src/test/java/` y `test/com/uelbosque/...` en `catalog-service`, `order-service`, `customer-service`, etc., resuelto artificialmente mediante `build-helper-maven-plugin` o `<testSourceDirectory>`.
- **Impacto:** Confusión en la configuración de IDEs (IntelliJ, VS Code, Eclipse) y apartamiento del estándar Maven estándar (`src/test/java`).

---

## 5. Análisis de Cobertura de Pruebas

| Capa / Módulo | Pruebas Existentes | Cobertura Estimada | Estado / Observaciones |
|---|---|---|---|
| **`platform-common`** | `JwtServiceTest.java` | ~90% | Tests unitarios de firma, expiración y extracción de claims JWT funcionales. |
| **`gateway`** | `GatewayRoutingTest.java` | ~85% | Pruebas de enrutamiento con Spring Cloud Gateway y WebTestClient. |
| **`user-service`** | `UserControllerTest`, `UserServiceTest`, `UserRepositoryTest` | 0% (Bloqueado) | **Bloqueado:** No compila debido al bug en `DataInitializer.java:24`. |
| **`customer-service`** | `CustomerControllerTest`, `CustomerServiceTest`, `CustomerRepositoryTest` | ~85% | Cobertura completa de casos de éxito y duplicidad. |
| **`supplier-service`** | `SupplierControllerTest`, `SupplierServiceTest`, `SupplierRepositoryTest` | ~85% | Cobertura completa de CRUD y validaciones de NIT. |
| **`catalog-service`** | `ProductControllerTest`, `ProductServiceTest`, `CsvConcurrencyTest`, `CsvImportTest` | ~90% | Excelentes pruebas de concurrencia y validación CSV. |
| **`order-service`** | `OrderControllerTest`, `OrderServiceTest`, `SaleServiceTest`, `SalesApiTest`, `SalesPersistenceTest` | ~85% | Cobertura dual de órdenes y ventas con pruebas de idempotencia. |
| **`inventory-service`** | `EndpointTest`, `InventoryRulesTest` | ~80% | Cobertura de reglas de reserva y confirmación. |
| **`payment-service`** | `EndpointTest`, `PaymentInsertTest`, `PaymentRulesTest` | ~80% | Cobertura de idempotencia y validación de pago único por venta. |
| **`notification-service`**| `EndpointTest`, `NotificationRulesTest` | ~75% | Cobertura de encolado y fallback sin SMTP. |
| **`admin-service`** | `EndpointTest`, `ReportServiceTest` | ~80% | Cobertura de agregación de métricas de ventas. |
| **`frontend`** | `app.component.spec.ts` (1 prueba) | < 2% | **Brecha Crítica:** Ningún componente atómico, molecular ni organismo tiene pruebas unitarias. |
"""

with open('ANALISIS/13_fase4_calidad_y_riesgos.md', 'w', encoding='utf-8') as f:
    f.write(content.strip() + '\n')

print("Created ANALISIS/13_fase4_calidad_y_riesgos.md successfully.")
