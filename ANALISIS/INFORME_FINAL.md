# INFORME FINAL DE AUDITORÍA TÉCNICA INTEGRAL
## Proyecto: Tienda Genérica - Plataforma de Microservicios Distribuidos
**Repositorio:** `sistemas-distribuidos-2026-2`  
**Auditor Técnico Senior:** Antigravity  
**Fecha:** 2026-10-18  
**Estado del Inventario de Archivos:** 100% Auditado (359 de 359 archivos revisados exhaustivamente)  

---

## 1. Resumen Ejecutivo

La presente auditoría técnica ha evaluado la totalidad del código fuente, configuraciones, infraestructura, scripts, pruebas y documentación del repositorio `sistemas-distribuidos-2026-2`.

### Estado General del Proyecto
El sistema implementa una arquitectura distribuida orientada a microservicios en **Java 17 / Spring Boot 3.1.3 / Spring Cloud 4.0.4** y una aplicación cliente web en **Angular 16.2.0** estructurada con Atomic Design. 
El backend presenta diseños distribuidos avanzados (patrón Two-Phase Reservation en inventarios, exclusión mutua pesimista para importaciones masivas CSV, e idempotencia transaccional en ventas y pagos). No obstante, la plataforma adolece de **dos brechas estructurales críticas**:
1. **Fallo Fatal de Compilación en `user-service`:** Impide la construcción y empaquetado del servicio de autenticación y usuarios debido a un archivo duplicado huérfano (`DataInitializer.java:24`).
2. **Desconexión Total del Frontend respecto al Backend:** El frontend Angular no realiza ninguna llamada HTTP hacia los microservicios; toda la persistencia y operaciones CRUD operan sobre colecciones en memoria (`mock`) del navegador, incumpliendo además las directrices de versión y paradigmas funcionales dictadas en `AGENTS.md`.

### Fortalezas Destacadas
- **Consistencia y Concurrencia en Backend:** Implementación rigurosa de mecanismos de exclusión mutua distribuida (`CatalogMutex` con `LockModeType.PESSIMISTIC_WRITE` en `catalog-service` y locks pesimistas en `inventory-service`).
- **Idempotencia Transaccional:** Control estricto de duplicidad mediante cabeceras `Idempotency-Key` en ventas (`order-service`) y pagos (`payment-service`), evitando doble cobro o ventas repetidas ante reintentos de red.
- **Suite de Pruebas Robusta en Backend:** 54 pruebas unitarias y de integración pasando con 100% de éxito en los microservicios funcionales (`platform-common`, `customer-service`, `catalog-service`, `order-service`).
- **Modularidad Maven y Docker:** Estructura modular Maven multi-proyecto con empaquetado Docker multi-stage uniforme y uso de usuario sin privilegios (`USER 10001`).

### Principales Debilidades y Riesgos
- **Ruptura de Build:** `user-service` no compila con Maven.
- **Imagen PostgreSQL Inexistente en Docker:** `compose.yml` declara `postgres:18.6`, lo que hace imposible desplegar el entorno con `docker compose`.
- **Frontend Desacoplado y sin Tests:** Ausencia total de comunicación HTTP (`HttpClientModule` no importado) y cobertura de pruebas nula (< 2%).
- **Llamadas Síncronas Frágiles:** Ausencia de Circuit Breakers (Resilience4j) o timeouts en `RestTemplate` para llamadas inter-servicio en cascada.

---

## 2. Diagrama de Arquitectura Completo del Sistema

```mermaid
flowchart TB
    subgraph Client["Capa Cliente (Frontend)"]
        UI["Angular 16.2.0 (tienda-frontend)\nAtomic Design: Atoms, Molecules, Organisms, Pages\n(Estado actual: Datos mock en memoria)"]
    end

    subgraph Edge["Capa Perimetral / Gateway"]
        GW["API Gateway (Spring Cloud Gateway: 8080)\nEnrutamiento dinámico /api/**\nCorsWebFilter habilitado"]
        CS["Config Server (Spring Cloud Config: 8888)\nConfiguración externalizada (Git/Native)"]
    end

    subgraph CoreServices["Capa de Microservicios Core (Spring Boot 3.1.3 / Java 17)"]
        US["user-service (:8081)\nAuth JWT, BCrypt, Roles\n[ERROR: DataInitializer.java:24]"]
        CAT["catalog-service (:8082)\nCatálogo CRUD & Importador CSV\nCatalogMutex (Pessimistic Lock)"]
        CUST["customer-service (:8089)\nMaestro de Clientes Comerciales\nValidación de Cédula Única"]
        SUPP["supplier-service (:8090)\nMaestro de Proveedores (NIT)\nValidación de Identificador"]
        ORD["order-service (:8085)\nDualidad: /api/sales & /api/orders\nIdempotency-Key & Auditoría"]
    end

    subgraph AuxServices["Capa de Servicios Auxiliares"]
        CART["cart-service (:8084)\nCarrito persistente por usuario"]
        INV["inventory-service (:8086)\nTwo-Phase Reservation\nStock & Reservations Locks"]
        PAY["payment-service (:8087)\nPago único por venta\nSimulación Approved/Declined"]
        NOTIF["notification-service (:8088)\nCola de correos (MailDelivery)\napp.mail.enabled: false"]
        ADM["admin-service (:8083)\nStateless Reporting Aggregator\nConsolida users, customers, sales"]
    end

    subgraph DataTier["Capa de Persistencia (PostgreSQL)"]
        DB_USER[("db: users")]
        DB_CAT[("db: catalog")]
        DB_CUST[("db: customers")]
        DB_SUPP[("db: suppliers")]
        DB_ORD[("db: orders")]
        DB_CART[("db: cart")]
        DB_INV[("db: inventory")]
        DB_PAY[("db: payments")]
        DB_NOTIF[("db: notifications")]
    end

    %% Relaciones Cliente -> Gateway
    UI -.->|"Objetivo: HTTP /api/** (Actualmente Mock)"| GW

    %% Relaciones Gateway -> Microservicios
    GW -->|/api/users/**| US
    GW -->|/api/products/**| CAT
    GW -->|/api/customers/**| CUST
    GW -->|/api/suppliers/**| SUPP
    GW -->|/api/sales/** & /api/orders/**| ORD
    GW -->|/api/cart/**| CART
    GW -->|/api/inventory/**| INV
    GW -->|/api/payments/**| PAY
    GW -->|/api/notifications/**| NOTIF
    GW -->|/api/reports/**| ADM

    %% Comunicación Inter-Servicios Síncrona (HTTP RestTemplate)
    CAT -->|"Validar NIT Proveedor"| SUPP
    CART -->|"Consultar Precios e IVA"| CAT
    ORD -->|"Validar Cliente"| CUST
    ORD -->|"Validar Operador"| US
    ORD -->|"Validar Producto Activo"| CAT
    PAY -->|"Consultar Monto Total Venta"| ORD
    ADM -->|"Consolidar Usuarios"| US
    ADM -->|"Consolidar Clientes"| CUST
    ADM -->|"Consolidar Ventas"| ORD

    %% Conexiones a Bases de Datos
    US --> DB_USER
    CAT --> DB_CAT
    CUST --> DB_CUST
    SUPP --> DB_SUPP
    ORD --> DB_ORD
    CART --> DB_CART
    INV --> DB_INV
    PAY --> DB_PAY
    NOTIF --> DB_NOTIF
```

---

## 3. Matriz Consolidada de Hallazgos

| ID | Severidad | Categoría | Archivo y Línea | Descripción del Problema | Impacto Real / Potencial | Solución Técnica Recomendada |
|---|---|---|---|---|---|---|
| **C-01** | **Crítico** | **Bug / Build** | `services/user-service/.../DataInitializer.java:24` | Invoca constructor inexistente `new User(str, str, str)`. | `user-service` falla compilación Maven (`BUILD FAILURE`). No se puede generar jar ni imagen Docker. | Eliminar el archivo duplicado huérfano `DataInitializer.java` en la raíz del paquete y mantener `config/DataInitializer.java`. |
| **C-02** | **Crítico** | **Arquitectura / Integración** | `frontend/tienda-frontend/src/app/app.module.ts:80-90` | `HttpClientModule` no está importado. Los 8 organismos usan arrays en memoria. | Desconexión completa: el frontend es una maqueta aislada sin persistencia ni integración con los microservicios. | Importar `HttpClientModule` y crear servicios Angular (`UserService`, `ProductService`, `SaleService`, etc.) consumiendo el Gateway (`http://localhost:8080`). |
| **A-01** | **Alto** | **Infraestructura** | `compose.yml:4` | Imagen Docker declarada como `postgres:18.6`. | `docker compose up` falla inmediatamente al intentar descargar una versión inexistente en Docker Hub. | Modificar la imagen a `postgres:16-alpine` o `postgres:17` en `compose.yml`. |
| **A-02** | **Alto** | **Conformidad Normativa** | `frontend/tienda-frontend/package.json:14-22` | Uso de Angular 16.2.0 (EOL noviembre 2024). Incumple regla `AGENTS.md:4`. | Deuda técnica por obsolescencia de dependencias y vulnerabilidades en paquetes desactualizados. | Actualizar el frontend a la última versión LTS (Angular 18/19) mediante `ng update`. |
| **A-03** | **Alto** | **Seguridad** | `frontend/.../organisms/login.organism.ts:85` | Autenticación mock hardcodeada (`admin` / `admin`). | Bypass trivial de control de acceso. El frontend no genera ni almacena Bearer tokens JWT. | Conectar formulario con `POST /api/users/login`, almacenar token en `sessionStorage` e inyectarlo vía `HttpInterceptor`. |
| **A-04** | **Alto** | **Calidad / Pruebas** | `frontend/.../app.component.spec.ts` | Solo 1 spec en todo el frontend. 0 pruebas para átomos, moléculas y organismos. | Riesgo severo de regresiones y violación explícita de `AGENTS.md:6`. | Escribir suite de pruebas Jasmine/Karma para cada componente con cobertura mínima del 80%. |
| **A-05** | **Alto** | **Resiliencia** | `SaleDirectory.java:32-40`, `ReportClient.java:19-21` | Llamadas `RestTemplate` síncronas sin timeouts ni Circuit Breaker. | Fallo en cascada y agotamiento de hilos Tomcat (*thread starvation*) si un servicio dependiente se degrada. | Configurar `connectTimeout` (2s) y `readTimeout` (3s) en `RestTemplate` e incorporar Resilience4j (`@CircuitBreaker`). |
| **M-01** | **Medio** | **Bug Potencial** | `services/catalog-service/.../DataInitializer.java:44-52` | `DataInitializer` no setea `supplierNit` en muestras. | Crash al iniciar el servicio si se activa `app.seed.enabled=true` por fallo en `SupplierDirectory.require(null)`. | Settear un NIT válido en `createSample` antes de invocar `ProductService.createProduct`. |
| **M-02** | **Medio** | **Arquitectura** | `services/order-service/.../Sale.java` y `Order.java` | Coexistencia no vinculada de dos dominios: `/api/sales` y `/api/orders`. | Confusión conceptual, duplicidad de mantenimiento y dispersión de esquemas de base de datos. | Unificar o documentar la separación funcional entre ventas directas de mostrador y órdenes e-commerce. |
| **M-03** | **Medio** | **Performance** | `ProductService.java:38`, `CustomerService.java:28` | Consultas masivas `findAll()` sin paginación en endpoints de listado. | Alto consumo de memoria (`OutOfMemoryError`) y payloads HTTP desproporcionados ante crecimiento de datos. | Implementar paginación con Spring Data `Pageable` (`Page<ProductResponseDto>`). |
| **M-04** | **Medio** | **Seguridad** | `platform-common/.../JwtService.java:31` | Clave secreta JWT fallback débil y hardcodeada en `compose.yml`. | Riesgo de falsificación de firmas JWT si el atacante conoce el repositorio. | Exigir obligatoriamente `${JWT_SECRET}` en el arranque y eliminar valores por defecto predecibles. |
| **M-05** | **Medio** | **Seguridad** | `ProductController.java:14`, `UserController.java:18` | `@CrossOrigin(origins = "*")` en microservicios internos. | Peticiones directas no filtradas saltando el Gateway si los puertos se exponen al host. | Restringir CORS exclusivamente al API Gateway y eliminar `@CrossOrigin` indiscriminado en los servicios de dominio. |
| **B-01** | **Bajo** | **Estructura** | `catalog-service/pom.xml:51`, `order-service/pom.xml:51` | Tests repartidos entre `src/test/java` y `test/` mediante `build-helper-maven-plugin`. | Discrepancia con el estándar Maven convencional; confusión en IDEs. | Mover todos los archivos de `test/` a `src/test/java` y remover el plugin adicional. |
| **B-02** | **Bajo** | **Deuda Técnica** | `services/admin-service/pom.xml:4` | Incluye dependencias de `postgresql` y `h2` en un servicio declarado explícitamente como stateless. | Sobrecarga de dependencias innecesarias en el empaquetado del jar. | Remover starters de base de datos de `admin-service`. |
| **B-03** | **Bajo** | **Calidad de Código** | Varios archivos `.java` y `.ts` | Caracteres de acentuación corruptos por codificación (`cdigo`, `nmero`, `Sesin`). | Impacto cosmético en mensajes de error al usuario. | Convertir y guardar todos los archivos con codificación UTF-8 pura sin BOM. |
| **B-04** | **Bajo** | **Gobernanza Agentes** | `.agents/skills/angular-testing/SKILL.md:4` | Skill declarada obsoleta (`DEPRECATED`) presente en el workspace. | Ruido y posibles desvíos para agentes de IA que asistan en el proyecto. | Eliminar la carpeta obsoleta o consolidarla en `angular-developer`. |

---

## 4. Plan de Acción Priorizado

### Fase 1: "Quick Wins" (Inmediato - Esfuerzo: < 1 día)
1. **Reparar compilación de `user-service`:** Eliminar `services/user-service/src/main/java/com/uelbosque/userservice/DataInitializer.java`. Reejecutar `mvnw.cmd test` para validar compilación limpia.
2. **Corregir versión de PostgreSQL en Docker:** Cambiar `postgres:18.6` por `postgres:16-alpine` en `compose.yml`.
3. **Corregir `supplierNit` en `catalog-service/config/DataInitializer.java`:** Asignar `dto.setSupplierNit("900112233-1")` para evitar crashes en seeding.
4. **Depurar skills obsoletas:** Retirar `.agents/skills/angular-testing/` para evitar confusiones operativas.

### Fase 2: Cambios Estructurales (Mediano Plazo - Esfuerzo: 1 a 2 semanas)
1. **Conexión Real Frontend-Backend:**
   - Importar `HttpClientModule` en `app.module.ts`.
   - Implementar capa de servicios Angular (`AuthService`, `CustomerService`, `ProductService`, `SaleService`).
   - Crear interceptor HTTP que adjunte el token JWT y cabeceras `Idempotency-Key` en operaciones de venta.
   - Reemplazar los arrays en memoria de los organismos por suscripciones reactivas a los servicios HTTP.
2. **Resiliencia en Microservicios:**
   - Configurar timeouts en todos los `RestTemplate` beans de `platform-common`.
   - Implementar circuit breakers con Resilience4j para llamadas distribuidas críticas (`SaleDirectory`, `CatalogReader`, `ReportClient`).
3. **Unificación del Dominio de Transacciones:**
   - Consolidar `/api/orders` y `/api/sales` en un modelo coherente o definir claramente sus límites de contexto (Bounded Contexts) en la arquitectura.

### Fase 3: Mejoras de Largo Plazo y Excelencia Técnica (Largo Plazo - Esfuerzo: 2 a 4 semanas)
1. **Modernización del Frontend:**
   - Actualizar a Angular 18/19 con soporte para Standalone Components y Signals.
   - Implementar suite completa de pruebas unitarias con Jasmine/Karma (>80% cobertura).
2. **Paginación Global en Backend:**
   - Refactorizar endpoints de listado para recibir `Pageable` y retornar `Page<T>` o `Slice<T>`.
3. **Seguridad y Secretos:**
   - Externalizar secretos en Docker Secrets o HashiCorp Vault.
   - Forzar validación estricta de variables de entorno en arranque de servicios.

---

## 5. Declaración de Exclusiones y Justificación

Durante la fase de inventario se identificaron y excluyeron del análisis línea a línea los siguientes artefactos:
1. **Directorios de dependencias y construcción (`.git`, `.angular`, `node_modules`, `target`, `dist`, `.idea`):** Archivos autogenerados, librerías de terceros y binarios de compilación.
2. **Archivos binarios y multimedia (`.ico`, `.docx`):**
   - `frontend/tienda-frontend/src/favicon.ico`: Icono gráfico binario del navegador.
   - `output/documentos/*.docx`: Documentos binarios ofimáticos no analizables como código fuente.

Todos los demás archivos del repositorio (**359 archivos**) fueron leídos, analizados y documentados en sus respectivos módulos de auditoría.

---

## 6. Preguntas Abiertas y Supuestos

1. **¿Cuál es el rol definitivo de `/api/orders` frente a `/api/sales`?**  
   *Supuesto adoptado:* `/api/sales` implementa el requerimiento formal de la Tienda Genérica (cédula cliente, cédula usuario, hasta 3 productos, idempotencia), mientras que `/api/orders` es una implementación e-commerce genérica añadida durante la fase de scaffolding.
2. **¿Por qué `admin-service` incluye drivers de PostgreSQL si su documentación indica que es stateless?**  
   *Supuesto adoptado:* Se originó a partir de una plantilla base compartida de Spring Boot sin depurar las dependencias de persistencia no utilizadas.
3. **¿Cuál es la política esperada para la carga de productos masivos en el Frontend?**  
   *Supuesto adoptado:* Aunque el backend posee el endpoint `POST /api/products/import`, el frontend no lo incluyó en su prototipo inicial de UI y debe incorporarse como un componente de subida de archivos CSV.

---

## 7. Confirmación de Cierre del Inventario

Se certifica formalmente que el archivo [ANALISIS/00_inventario.md](file:///c:/Users/samue/Documents/GitHub/sistemas-distribuidos/sistemas-distribuidos-2026-2/ANALISIS/00_inventario.md) ha sido actualizado al **100% de cumplimiento**:
- **Archivos Auditables Identificados:** 359
- **Archivos Marcados como Revisados (`[x]`):** 359
- **Archivos Pendientes (`[ ]`):** 0
- **Porcentaje de Cobertura de Auditoría:** 100.0%

---
*Fin del Informe de Auditoría Técnica.*
