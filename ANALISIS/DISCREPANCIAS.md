# Registro de Discrepancias entre Auditoría y Código Real

**Auditor / Ingeniero:** Antigravity  
**Fecha:** 2026-10-18  

Este archivo documenta los hallazgos reportados durante la auditoría que, tras verificación exhaustiva contra el código fuente real, no se confirman, tienen un alcance diferente, o presentan particularidades no identificadas inicialmente.

---

## Registro de Hallazgos Verificados

| ID Auditoría / Tema | Archivo y Línea Reportada | Estado de Confirmación | Evidencia en Código Real | Análisis Técnico y Observaciones |
|---|---|---|---|---|
| **C-01** (User Constructor) | `user-service/DataInitializer.java:24` | **CONFIRMADO** | `services/user-service/src/main/java/com/uelbosque/userservice/DataInitializer.java:24` | El archivo suelto en la raíz del paquete invocaba constructor inexistente `new User(str, str, str)`. Corregido eliminando la clase duplicada y consolidando en `config/DataInitializer.java`. |
| **A-01** (PostgreSQL 18.6) | `compose.yml:4` | **FALSO** | `compose.yml:4: image: postgres:18.6` | La auditoría asumió que la versión 18.6 no existía. Consulta directa al API v2 de Docker Hub (`https://hub.docker.com/v2/repositories/library/postgres/tags`) confirma la existencia activa de `18.6`, `18`, `18-alpine` y `18.6-alpine`. `compose.yml` se mantiene en `postgres:18.6`. |
| **M-01** (Supplier Nit Seeder) | `catalog-service/config/DataInitializer.java:44-52` | **CONFIRMADO** | `services/catalog-service/.../DataInitializer.java:43-52` | `dto.setSupplierNit(...)` no se invocaba al crear muestras. Se corrigió asignando NIT `"900112233"` y asegurando su persistencia en el seeder de `supplier-service`. |
| **C-02** (Frontend HTTP) | `frontend/tienda-frontend/src/app/app.module.ts:80-90` | **CONFIRMADO** | `app.module.ts:24-34` | `HttpClientModule` no estaba importado y los organismos utilizaban colecciones en memoria. Corregido importando `HttpClientModule`, interceptor JWT y servicios HTTP reactivos. |
| **A-05 / RestTemplate Timeouts** | `SaleDirectory.java:32-40`, `ReportClient.java:19-21` | **PARCIAL** | `services/platform-common/src/main/java/com/uelbosque/platformcommon/PlatformConfiguration.java:14-23` | **Falso respecto a la ausencia de timeouts:** `PlatformConfiguration` define el `@Bean RestTemplate` con `connectTimeout(3s)` y `readTimeout(5s)` inyectados vía `RestTemplateBuilder`. **Confirmado respecto a Circuit Breakers:** no se hace uso de Resilience4j `@CircuitBreaker`. |
| **M-05 / @CrossOrigin** | `ProductController.java:14`, `UserController.java:18` | **PARCIAL** | `services/catalog-service/src/main/java/com/uelbosque/catalogservice/controller/ProductController.java:15` | Solo `ProductController.java:15` tiene `@CrossOrigin(origins = "*")`. El reporte de que `UserController.java:18`, `CustomerController` o `SupplierController` contenían dicha anotación es **FALSO** (ninguno de ellos la tiene). |
| **M-04 / JwtService vs ApiSecurity** | `platform-common/.../JwtService.java:31` | **FALSO** | `services/platform-common/src/main/java/com/uelbosque/platformcommon/ApiSecurity.java:17-38` | `JwtService.java` y `JwtServiceTest.java` **no existen en el repositorio**. La seguridad centralizada reside en `ApiSecurity.java` (configurando `NimbusJwtDecoder` y `SecurityFilterChain`) y su prueba unitaria es `ApiSecurityTest.java`. |
| **Conteo Tests (138 vs 54)** | `ANALISIS/INFORME_FINAL.md:23` vs `docs/VALIDACION_BACKEND.md:21` | **PARCIAL** | `services/*/test/` (13 microservicios) | 54 tests fue una ejecución parcial durante la auditoría que omitió módulos debido al error C-01. El recuento real y completo de la suite es de 138 tests documentados en `VALIDACION_BACKEND.md`, incrementado con nuevos tests de seeder. |
| **Secretos por defecto** | `compose.yml:19`, `application.yml` | **CONFIRMADO** | `compose.yml:19`, `services/*/src/main/resources/application.yml` | Si no se provee la variable de entorno `JWT_SECRET`, la configuración recurre al valor por defecto fallback predecible. |
| **Métodos GatewayHealthController** | `services/gateway/.../GatewayHealthController.java:16-38` | **PARCIAL** | `services/gateway/src/main/java/com/uelbosque/gateway/controller/GatewayHealthController.java:16-38` | **Falso respecto a la longitud de métodos:** `health()` tiene 14 líneas y `fallback()` tiene 8 líneas (ambos <= 20 líneas de AGENTS.md). **Confirmado respecto al contenido:** el mapa de rutas estático solo enumera 4 de las 10 rutas registradas. |
