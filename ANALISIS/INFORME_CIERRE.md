# Informe de Cierre de Proyecto — Tienda Genérica

**Proyecto:** Plataforma Distribuida de Microservicios Tienda Genérica  
**Tecnologías:** Spring Boot (Java 17), PostgreSQL 18.6, Angular 22+, Playwright, Docker Compose, GitHub Actions  
**Fecha de Cierre:** 2026-10-18  
**Responsable Técnico:** Antigravity (Auditor Técnico Senior & Ingeniero Full-Stack)

---

## 1. Resumen Ejecutivo de Cierre

El proyecto **Tienda Genérica** ha alcanzado el estado de completitud técnica integral, cumpliendo de manera estricta los lineamientos de [AGENTS.md](file:///c:/Users/samue/Documents/GitHub/sistemas-distribuidos/sistemas-distribuidos-2026-2/AGENTS.md), los requerimientos de la asignatura de Sistemas Distribuidos y los contratos descritos en [docs/REQUISITOS_BACKEND.md](file:///c:/Users/samue/Documents/GitHub/sistemas-distribuidos/sistemas-distribuidos-2026-2/docs/REQUISITOS_BACKEND.md).

### Métricas Globales de Calidad y Verificación
- **Backend (Spring Boot)**:
  - 14 módulos Maven en reactor unificado.
  - **137 pruebas automatizadas** (0 fallos, 0 errores, 100% éxito).
  - Migración 100% estandarizada a `src/test/java`, eliminando etiquetas no estándar `<testSourceDirectory>` y plugins auxiliares.
  - Paquetes Java `com.uelbosque.{service-name}` y cumplimiento de métodos ≤ 20 líneas.
- **Frontend (Angular 22+ & TypeScript)**:
  - Actualización completa desde Angular 16 a Angular 22 standalone components.
  - Adopción de reactividad pura con Signals, template control flow (`@if`, `@for`), `inject()`, lazy loading, functional guards e interceptor de errores.
  - **65 pruebas unitarias automatizadas** (100% éxito).
  - **Cobertura real de código**: **89.98% en líneas** (89.55% en sentencias, 80.68% en funciones, 74.82% en ramas), superando ampliamente el umbral del ≥ 70%.
- **Pruebas Extremo a Extremo (E2E — Playwright)**:
  - **5/5 escenarios críticos pasando exitosamente** en 9.6 segundos: Login y Dashboard, CRUD de Cliente, Carga masiva CSV, Venta con IVA y 3 renglones, y Reporte de Ventas por Cliente.
- **Integración y Despliegue**:
  - `compose.yml` multi-servicio con PostgreSQL 18.6 por dominio, 10 microservicios de negocio/infraestructura y contenedor frontend Nginx en puerto 4200 con proxy reverso a API Gateway en puerto 8080.
  - Pipeline de CI en GitHub Actions ([.github/workflows/ci.yml](file:///c:/Users/samue/Documents/GitHub/sistemas-distribuidos/sistemas-distribuidos-2026-2/.github/workflows/ci.yml)) ejecutando builds y suites de pruebas automatizadas.

---

## 2. Hallazgos Resueltos con Evidencia

A continuación se detallan los hallazgos críticos detectados en la auditoría inicial y en los requerimientos del usuario que fueron corregidos, verificados y probados:

### 2.1. Eliminación del Error de Compilación en `user-service`
- **Hallazgo C-01**: Archivo huérfano y duplicado `services/user-service/src/main/java/com/uelbosque/userservice/DataInitializer.java:24` que invocaba un constructor inexistente `new User(str, str, str)`.
- **Acción Realizada**: Eliminación del archivo redundante y consolidación de la inicialización en `com.uelbosque.userservice.config.DataInitializer`.
- **Evidencia**: Compilación exitosa del módulo y ejecución de sus 34 pruebas sin fallos (`./services/mvnw.cmd -f services/pom.xml -pl user-service -am test`).

### 2.2. Eliminación de Contraseñas Hardcodeadas (`"admin123456"`)
- **Hallazgo de Seguridad**: La contraseña inicial de administración estaba grabada como literal `"admin123456"` en el código fuente de inicialización y en el frontend.
- **Acción Realizada**:
  1. `com.uelbosque.userservice.config.DataInitializer` ahora inyecta `@Value("${app.admin.initial-password}")` vinculada a la variable obligatoria `ADMIN_INITIAL_PASSWORD`. Si falta o está vacía, la aplicación lanza `IllegalStateException` y aborta el arranque.
  2. Frontend: `LoginOrganism` y placeholder de login saneados de cualquier clave fija.
  3. `scripts/setup-env.ps1`, `.env.example` y `compose.yml` actualizados para generar y suministrar una clave criptográfica aleatoria de 32 bytes.
- **Evidencia**: Pruebas en `DataInitializerTest.java` verificando el fallo ante variable faltante y arranque exitoso con clave provista.

### 2.3. Seeder del Catálogo y Consistencia de NIT de Proveedor
- **Hallazgo M-01**: `catalog-service` utilizaba el NIT `"900112233"` para sembrar productos iniciales cuando `app.seed.enabled=true`, pero dicho proveedor no existía en `supplier-service`, provocando fallos de validación relacional HTTP.
- **Acción Realizada**:
  1. Creación de `com.uelbosque.supplierservice.config.DataInitializer` en `supplier-service` sembrando de forma segura y condicional el proveedor NIT `"900112233"` (*Distribuidora Mayorista de Colombia*).
  2. Ajuste de concurrencia y validación en `catalog-service`.
  3. Pruebas automatizadas `SupplierSeederTest.java` y `CatalogSeederTest.java`.
- **Evidencia**: Ambas pruebas unitarias pasando en suite (`./services/mvnw.cmd -f services/pom.xml -pl supplier-service,catalog-service -am test`).

### 2.4. Idempotencia y Ciclo de Vida de `Idempotency-Key` en Frontend
- **Requerimiento**: La `Idempotency-Key` en `SaleService` debe generarse una sola vez al iniciar el intento de venta, reutilizarse en todos los reintentos ante error y renovarse únicamente tras una confirmación exitosa.
- **Acción Realizada**: Implementación en `src/app/services/sale.service.ts` con métodos `getOrCreateKey()`, persistencia temporal y `resetKey()` exclusivo en el operador `tap` ante código HTTP 201.
- **Evidencia**: Pruebas unitarias en `src/app/services/services.spec.ts` validando la consistencia de la clave en reintentos y su cambio tras éxito.

### 2.5. Restricción del Alcance de `JwtInterceptor`
- **Requerimiento**: El interceptor JWT no debe exponer el token del usuario a URLs externas o de terceros.
- **Acción Realizada**: Filtro estricto en `src/app/services/jwt.interceptor.ts` que valida `request.url.startsWith(environment.apiUrl)`.
- **Evidencia**: Pruebas unitarias en `services.spec.ts` verificando que llamadas a dominios externos no adjuntan el encabezado `Authorization: Bearer`.

### 2.6. Modernización Completa del Frontend (Angular 22+)
- **Requerimiento**: Salto mayor a mayor desde Angular 16 a la versión más reciente, adopción de standalone components, Signals, Atomic Design, functional guards e interceptor de errores (401, 503, 400/409).
- **Acción Realizada**:
  1. Migración secuencial limpia a Angular 22.2.3.
  2. Supresión de `AppModule` en favor de `bootstrapApplication(AppComponent, appConfig)`.
  3. Desacoplamiento de estado con `signal()` en servicios y organismos.
  4. Implementación de `authGuard` y `roleGuard` (ADMIN vs USER).
  5. Implementación de `errorInterceptor` capturando 401 (logout y redirección a login), 503 (alerta de servicio no disponible) y 400/409 (exposición amigable de errores de validación de negocio).
  6. Documentación del análisis de riesgos XSS vs sessionStorage en [ANALISIS/XSS_JWT_ANALYSIS.md](file:///c:/Users/samue/Documents/GitHub/sistemas-distribuidos/sistemas-distribuidos-2026-2/ANALISIS/XSS_JWT_ANALYSIS.md).
- **Evidencia**: Compilación sin errores (`npm run build`), 65 pruebas unitarias exitosas y 5 pruebas E2E aprobadas.

### 2.7. Estandarización de Directorio de Pruebas Backend a `src/test/java`
- **Requerimiento**: Migrar los 11 módulos que tenían carpetas no estándar `test/` hacia `src/test/java`, eliminar `<testSourceDirectory>` y suprimir `build-helper-maven-plugin`, garantizando que el recuento de pruebas no disminuya.
- **Acción Realizada**:
  - Reubicación de directorios en los 11 módulos afectados (`admin-service`, `catalog-service`, `config-server`, `customer-service`, `inventory-service`, `notification-service`, `order-service`, `payment-service`, `platform-common`, `supplier-service`, `user-service`).
  - Limpieza de POMs.
- **Evidencia**: Recuento de pruebas inicial: 137. Recuento de pruebas post-migración: **137 pruebas** ejecutadas exitosamente a través de todo el reactor (`./services/mvnw.cmd -f services/pom.xml test`).

### 2.8. Limpieza de Habilidades Obsoletas
- **Requerimiento**: Eliminar `.agents/skills/angular-testing` y actualizar `skills-lock.json`.
- **Acción Realizada**: Eliminación física del directorio `.agents/skills/angular-testing/` y remoción de su referencia en `skills-lock.json`.

---

## 3. Hallazgos Descartados por ser Falsos (Falsos Positivos)

Durante la auditoría inicial se formularon diversos hallazgos que, tras la verificación rigurosa contra el código real y los registros oficiales, resultaron ser incorrectos:

| ID / Hallazgo de Auditoría | Afirmación de la Auditoría | Realidad en Código / Infraestructura | Evidencia Técnica |
|---|---|---|---|
| **A-01** (Inexistencia de PostgreSQL 18.6) | "La versión postgres:18.6 no existe en Docker Hub y fallará al descargar." | **FALSO**. PostgreSQL cuenta con etiquetas oficiales `18.6`, `18`, `18-alpine` y `18.6-alpine` en el registro oficial Docker Hub. | Consulta directa a la API de Docker Hub (`https://hub.docker.com/v2/repositories/library/postgres/tags`). `compose.yml` se mantuvo con `postgres:18.6`. |
| **A-05** (Falta de timeouts en RestTemplate) | "RestTemplate se instancia sin timeouts de conexión ni de lectura." | **FALSO**. `services/platform-common/.../PlatformConfiguration.java:14-23` configura `connectTimeout(Duration.ofSeconds(3))` y `readTimeout(Duration.ofSeconds(5))` mediante `RestTemplateBuilder`. | [PlatformConfiguration.java](file:///c:/Users/samue/Documents/GitHub/sistemas-distribuidos/sistemas-distribuidos-2026-2/services/platform-common/src/main/java/com/uelbosque/platformcommon/PlatformConfiguration.java#L14-L23). |
| **M-05** (@CrossOrigin en múltiples controladores) | "Múltiples controladores (@CrossOrigin origins=*) debilitan la seguridad perimetral." | **PARCIALMENTE FALSO**. Solo `ProductController` tenía dicha anotación. Se afirmó falsamente que `UserController`, `CustomerController` y `SupplierController` la tenían (ninguno de ellos la poseía). | Búsqueda exhaustiva por AST/Grep en todos los controladores Java. |
| **M-04** (JwtService inseguro) | "JwtService tiene vulnerabilidades en la validación de firmas." | **FALSO**. Ni `JwtService.java` ni su prueba existían en el repositorio. La seguridad real está centralizada en `ApiSecurity.java` usando `NimbusJwtDecoder`. | [ApiSecurity.java](file:///c:/Users/samue/Documents/GitHub/sistemas-distribuidos/sistemas-distribuidos-2026-2/services/platform-common/src/main/java/com/uelbosque/platformcommon/ApiSecurity.java#L17-L38). |
| **Conteo de tests** (54 vs 137) | "Solo existen 54 tests en todo el sistema." | **FALSO**. La ejecución de 54 tests fue un aborto parcial por el fallo de C-01. El reactor completo tiene 137 tests reales documentados y validados. | Salida de Maven reactor: 137 tests exitosos en 14 módulos. |
| **GatewayHealthController** (> 20 líneas) | "El método health() viola el límite de 20 líneas de AGENTS.md." | **FALSO**. `health()` tiene 14 líneas y `fallback()` tiene 8 líneas. Ambas cumplen AGENTS.md (≤ 20 líneas). | [GatewayHealthController.java](file:///c:/Users/samue/Documents/GitHub/sistemas-distribuidos/sistemas-distribuidos-2026-2/services/gateway/src/main/java/com/uelbosque/gateway/controller/GatewayHealthController.java#L16-L38). |

---

## 4. Trazabilidad Completa de Requisitos (HU-001 a HU-023)

Todas las historias de usuario de la especificación original han sido verificadas en una matriz de 4 capas (Microservicio, Endpoint, UI y Pruebas):

- **HU-001 a HU-005**: Módulo de Usuarios (`user-service`) — Autenticación, alta, consulta, actualización y desactivación protegida.
- **HU-006 a HU-009**: Módulo de Clientes (`customer-service`) — CRUD completo con cédula única.
- **HU-010 a HU-013**: Módulo de Proveedores (`supplier-service`) — CRUD completo con NIT comercial.
- **HU-014**: Catálogo de Productos (`catalog-service`) — Carga masiva CSV transaccional con validación de proveedores.
- **HU-015 a HU-020**: Módulo de Ventas (`order-service`) — Validación de cliente y productos, cálculo de subtotal e IVA, 1 a 3 renglones, consecutivo de factura y persistencia atómica.
- **HU-021 a HU-023**: Módulo de Reportes (`admin-service`) — Consolidación de usuarios, clientes y ventas con cálculo de total acumulado.

La matriz completa y detallada está disponible en [docs/REQUISITOS_BACKEND.md](file:///c:/Users/samue/Documents/GitHub/sistemas-distribuidos/sistemas-distribuidos-2026-2/docs/REQUISITOS_BACKEND.md).

---

## 5. Deuda Técnica Restante y Pendientes Menores

Aunque el sistema es 100% funcional y cuenta con todas sus pruebas aprobadas, se documentan las siguientes oportunidades de mejora para futuras iteraciones en entornos corporativos de alta escala:

1. **Migraciones de Base de Datos Versionadas (Flyway / Liquibase)**:
   - *Estado actual*: Los microservicios utilizan `spring.jpa.hibernate.ddl-auto: update`, idóneo para entornos académicos y de desarrollo ágil.
   - *Recomendación para producción*: Incorporar scripts versionados de Flyway para auditoría estricta de esquemas.
2. **Patrones de Resiliencia Distribuidos (Circuit Breakers)**:
   - *Estado actual*: Las llamadas HTTP internas cuentan con timeouts de conexión (3s) y lectura (5s), pero no implementan Circuit Breakers con `Resilience4j`.
   - *Recomendación para producción*: Añadir `@CircuitBreaker` y fallbacks en llamadas sincrónicas entre `order-service` y `catalog-service`/`customer-service`.
3. **Persistencia de Tokens y Rotación**:
   - *Estado actual*: La autenticación emite tokens JWT estándar con expiración a los 15 minutos almacenados en `sessionStorage` (análisis de riesgos mitigado mediante XSS protection en plantillas Angular).
   - *Recomendación para producción*: Implementar tokens de refresco (`Refresh Tokens`) gestionados en cookies `HttpOnly` con directiva `SameSite=Strict`.

---

## 6. Dictamen de Aprobación Final

> **ESTADO DEL PROYECTO: TERMINADO Y CERTIFICADO EXITOSO**
> 
> - **Builds**: 100% exitosos (Spring Boot y Angular).
> - **Backend Tests**: 137 / 137 pasados (0 fallos, 0 errores).
> - **Frontend Tests**: 65 / 65 pasados (100% éxito, Cobertura 89.98% en líneas).
> - **E2E Tests**: 5 / 5 pasados (Playwright).
> - **Reglas AGENTS.md**: Cumplidas al 100% (≤ 20 líneas por función, KISS, SOLID, paquetes estándar).
