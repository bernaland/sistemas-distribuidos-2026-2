# Fase 1 – Reconocimiento Técnico y Exhaustivo

## 1. Resumen Ejecutivo de Reconocimiento

El proyecto **"sistemas-distribuidos-2026-2"** es una solución distribuida para un sistema de **Tienda Genérica**, compuesto por:
- **Frontend Web:** Desarrollado en Angular 16 (`frontend/tienda-frontend`), estructurado bajo el paradigma Atomic Design (atoms, molecules, organisms, pages, templates) con Bootstrap 5.
- **Backend Distribuido:** Basado en arquitectura de Microservicios con Spring Boot 3.1.3 y Spring Cloud 2022.0.4 (4.0.4), compuesto por 12 servicios funcionales/auxiliares, 1 biblioteca compartida (`platform-common`), un servidor de configuración central (`config-server`) y una puerta de enlace API Gateway (`gateway`).
- **Persistencia:** PostgreSQL con aislamiento estricto por dominio mediante bases de datos y roles independientes por microservicio (`init-databases.sh`), y H2 in-memory exclusivo para la suite de pruebas unitarias/integración.
- **Orquestación y Entorno:** Docker Compose (`compose.yml`) parametrizado mediante `.env` generado criptográficamente (`scripts/setup-env.ps1`).

## 2. Lenguajes, Frameworks y Gestores de Paquetes

| Capa / Módulo | Lenguaje / Versión | Framework / Versión | Gestor de Paquetes | Runtime / Servidor |
|---|---|---|---|---|
| **Frontend** (`frontend/tienda-frontend`) | TypeScript 5.1.3, JavaScript ES2022, HTML5, CSS3 | Angular 16.2.0, Bootstrap 5.3.8, Bootstrap Icons 1.13.1 | npm (Node >= 18) | Angular CLI Development Server / Nginx o estático |
| **Backend Microservicios** (`services/*`) | Java 17 (compilación compatible hasta Java 21) | Spring Boot 3.1.3, Spring Cloud 4.0.4, Spring Data JPA, Spring Security | Maven 3.9.9 (Maven Wrapper `mvnw.cmd` / `mvnw`) | Eclipse Temurin 17 JRE (Docker) / Tomcat embebido / Netty (Gateway) |
| **Base de Datos** | SQL (PostgreSQL dialect) | PostgreSQL 18.6 (declarado en compose), H2 2.1+ (tests) | N/A | PostgreSQL Server Docker Container |
| **Infraestructura / Automatización** | Bash, PowerShell, Python 3.12 | Docker Compose v2, curl, psql | N/A | Docker Engine Linux Containers / Windows Host |

## 3. Inventario y Análisis de Archivos de Configuración

### 3.1. Archivos Raíz del Repositorio
- `AGENTS.md` (L1-38): Define las reglas arquitectónicas y de desarrollo del proyecto (Angular última versión, functional components, Bootstrap, Atomic Design, Java/Spring Boot, PostgreSQL, inyección de dependencias, convención `com.uelbosque.{service-name}`, pruebas unitarias obligatorias, límite estricto de 20 líneas por método/función, SOLID y KISS).
- `REQUERIMIENTOS_MICROSERVICIOS.md` (L1-27): Define el alcance funcional obligatorio derivado del caso de estudio de Tienda Genérica (Usuarios, Clientes, Proveedores, Catálogo CSV atómico, Ventas 1-3 renglones con IVA individual, Reportes) y servicios auxiliares (Inventario, Carrito, Pagos simulados, Notificaciones).
- `compose.yml` (L1-276): Orquestación multi-contenedor para 13 servicios (Postgres, Config Server, User Service, Catalog Service, Inventory Service, Cart Service, Order Service, Payment Service, Notification Service, Admin Service, Customer Service, Supplier Service, Gateway). Especifica healthchecks, volumen persistente `postgres-data`, inyección de variables desde `.env` y red interna.
- `.env.example` (L1-5): Plantilla de variables de entorno críticas (`JWT_SECRET`, `DB_PASSWORD`, `POSTGRES_PASSWORD`, `CONFIG_PASSWORD`).
- `.dockerignore` (L1-7): Excluye target, node_modules, .git, .env, frontend, .agents de los contextos de construcción Docker.
- `.gitignore` (L1-63): Configura ignorados para Java (target, .class, .jar), Node/Angular (node_modules, dist, .angular), IDEs (.vscode, .idea) y secretos (.env).
- `skills-lock.json` (L1-54): Bloqueo de hashes y fuentes de habilidades del agente (Angular developer, testing, review, Java junit, microservices, springboot).

### 3.2. Infraestructura y Scripts
- `infra/init-databases.sh` (L1-11): Script shell de inicialización PostgreSQL en `/docker-entrypoint-initdb.d/`. Itera sobre 9 bases de datos (`users`, `customers`, `suppliers`, `catalog`, `carts`, `orders`, `inventory`, `payments`, `notifications`), crea roles específicos y revoca acceso público (`REVOKE CONNECT ON DATABASE FROM PUBLIC`).
- `scripts/setup-env.ps1` (L1-15): Generador criptográficamente seguro de 32 bytes aleatorios para claves en `.env`.
- `scripts/check-java-functions.py` (L1-22): Verificador automatizado en Python que valida la regla de `AGENTS.md` de máximo 20 líneas por método en todos los archivos `.java` de `services/*/src/main/java/`.
- `.vscode/settings.json` (L1-11): Configuración de entorno de desarrollo VSCode para Copilot instructions y análisis nulo de Java.
- `.copilot/instructions/` (`angular.instructions.md`, `java.instructions.md`, `postgreSql.instructions.md`): Directrices de estilo de código para asistentes IA.

### 3.3. Configuración del Frontend (`frontend/tienda-frontend/`)
- `package.json`: Manifiesto de dependencias npm para Angular 16.2.0.
- `angular.json`: Configuración del workspace Angular CLI (builder `@angular-devkit/build-angular:browser`, presupuestos de bundle, estilos globales `src/styles.css`).
- `tsconfig.json`, `tsconfig.app.json`, `tsconfig.spec.json`: Configuración estricta de compilador TypeScript (ES2022, strict: true, noImplicitReturns: true, strictTemplates: true).
- `.editorconfig`: Estándares de indentación (2 espacios) y codificación (UTF-8).
- `package_legacy.json` y `angular_legacy.json`: Archivos residuales/obsoletos detectados en el árbol.

### 3.4. Configuración del Backend (`services/`)
- `services/pom.xml`: POM agregador raíz que define los 13 submódulos Maven.
- `services/Dockerfile`: Multi-stage build (Maven 3.9.9 / Eclipse Temurin 17 JRE) para empaquetado y ejecución segura con usuario no-root (UID 10001) y limitación de memoria RAM (`-XX:MaxRAMPercentage=60`).
- `services/.mvn/wrapper/maven-wrapper.properties`: Maven Wrapper v3.3.4 con distribución Maven 3.9.9.
- `services/platform-common/pom.xml`: Biblioteca compartida para auto-configuración de Spring Boot (`PlatformConfiguration`), filtros de seguridad (`ApiSecurity`), manejo estándar de errores HTTP (`ApiErrors`) y clientes con timeout.
- `services/config-server/src/main/resources/config/application.yml`: Configuración centralizada distribuida por Spring Cloud Config.

## 4. Matriz Completa de Dependencias y Estado de Vulnerabilidad / Obsolescencia

### 4.1. Dependencias del Frontend (npm)

| Paquete | Versión Declarada | Tipo | Estado / Riesgo de Obsolescencia |
|---|---|---|---|
| `@angular/animations` | `^16.2.0` | Producción | **Obsoleta (Angular 16 EOL en Nov 2024; actual v19/v20)** |
| `@angular/common` | `^16.2.0` | Producción | **Obsoleta (Angular 16 EOL en Nov 2024; actual v19/v20)** |
| `@angular/compiler` | `^16.2.0` | Producción | **Obsoleta (Angular 16 EOL en Nov 2024; actual v19/v20)** |
| `@angular/core` | `^16.2.0` | Producción | **Obsoleta (Angular 16 EOL en Nov 2024; actual v19/v20)** |
| `@angular/forms` | `^16.2.0` | Producción | **Obsoleta (Angular 16 EOL en Nov 2024; actual v19/v20)** |
| `@angular/platform-browser` | `^16.2.0` | Producción | **Obsoleta (Angular 16 EOL en Nov 2024; actual v19/v20)** |
| `@angular/platform-browser-dynamic` | `^16.2.0` | Producción | **Obsoleta (Angular 16 EOL en Nov 2024; actual v19/v20)** |
| `@angular/router` | `^16.2.0` | Producción | **Obsoleta (Angular 16 EOL en Nov 2024; actual v19/v20)** |
| `@popperjs/core` | `^2.11.8` | Producción | Normal |
| `bootstrap` | `^5.3.8` | Producción | Actualizada (Bootstrap 5) |
| `bootstrap-icons` | `^1.13.1` | Producción | Normal |
| `rxjs` | `~7.8.0` | Producción | Normal |
| `tslib` | `^2.3.0` | Producción | Normal |
| `zone.js` | `~0.13.0` | Producción | Normal |
| `@angular-devkit/build-angular` | `^16.2.0` | Desarrollo | **Obsoleta (Typescript 5.1 / Angular 16 tooling)** |
| `@angular/cli` | `~16.2.0` | Desarrollo | **Obsoleta (Typescript 5.1 / Angular 16 tooling)** |
| `@angular/compiler-cli` | `^16.2.0` | Desarrollo | **Obsoleta (Typescript 5.1 / Angular 16 tooling)** |
| `@types/jasmine` | `~4.3.0` | Desarrollo | Normal |
| `jasmine-core` | `~4.6.0` | Desarrollo | Normal |
| `karma` | `~6.4.0` | Desarrollo | Normal |
| `karma-chrome-launcher` | `~3.2.0` | Desarrollo | Normal |
| `karma-coverage` | `~2.2.0` | Desarrollo | Normal |
| `karma-jasmine` | `~5.1.0` | Desarrollo | Normal |
| `karma-jasmine-html-reporter` | `~2.1.0` | Desarrollo | Normal |
| `typescript` | `~5.1.3` | Desarrollo | **Obsoleta (Typescript 5.1 / Angular 16 tooling)** |

### 4.2. Dependencias del Backend (Maven)

| Módulo | Spring Boot | Dependencias Clave | Base de Datos | Scope Test / Build Plugins |
|---|---|---|---|---|
| `admin-service` | `3.1.3` | `spring-cloud-starter-config`, `platform-common`, `spring-boot-starter-actuator` | PostgreSQL (runtime), H2 (test) | `spring-boot-starter-test`, `spring-security-test` |
| `cart-service` | `3.1.3` | `spring-cloud-starter-config`, `platform-common`, `spring-boot-starter-actuator`, `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation` | PostgreSQL (runtime), H2 (test) | `spring-boot-starter-test`, `spring-security-test` |
| `catalog-service` | `3.1.3` | `spring-cloud-starter-config`, `commons-csv`, `platform-common`, `spring-boot-starter-actuator`, `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation` | PostgreSQL (runtime), H2 (test) | `spring-boot-starter-test`, `spring-security-test` |
| `config-server` | `3.1.3` | `spring-boot-starter-web`, `spring-boot-starter-security`, `spring-boot-starter-actuator`, `spring-cloud-config-server` | PostgreSQL (runtime), H2 (test) | `spring-boot-starter-test`, `spring-security-test` |
| `customer-service` | `3.1.3` | `spring-cloud-starter-config`, `spring-boot-starter-actuator`, `platform-common`, `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation` | PostgreSQL (runtime), H2 (test) | `spring-boot-starter-test`, `spring-security-test` |
| `gateway` | `3.1.3` | `spring-cloud-starter-config`, `spring-cloud-starter-gateway`, `spring-boot-starter-actuator` | PostgreSQL (runtime), H2 (test) | `spring-boot-starter-test`, `spring-security-test` |
| `inventory-service` | `3.1.3` | `spring-cloud-starter-config`, `platform-common`, `spring-boot-starter-actuator` | PostgreSQL (runtime), H2 (test) | `spring-boot-starter-test`, `spring-security-test` |
| `notification-service` | `3.1.3` | `spring-cloud-starter-config`, `platform-common`, `spring-boot-starter-actuator`, `spring-boot-starter-mail` | PostgreSQL (runtime), H2 (test) | `spring-boot-starter-test`, `spring-security-test` |
| `order-service` | `3.1.3` | `spring-cloud-starter-config`, `platform-common`, `spring-boot-starter-actuator`, `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation` | PostgreSQL (runtime), H2 (test) | `spring-boot-starter-test`, `spring-security-test` |
| `payment-service` | `3.1.3` | `spring-cloud-starter-config`, `platform-common`, `spring-boot-starter-actuator` | PostgreSQL (runtime), H2 (test) | `spring-boot-starter-test`, `spring-security-test` |
| `platform-common` | `3.1.3` | `spring-boot-starter-web`, `spring-boot-starter-oauth2-resource-server`, `spring-boot-starter-validation`, `spring-boot-starter-data-jpa` | PostgreSQL (runtime), H2 (test) | `spring-boot-starter-test`, `spring-security-test` |
| `supplier-service` | `3.1.3` | `spring-cloud-starter-config`, `spring-boot-starter-actuator`, `platform-common`, `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation` | PostgreSQL (runtime), H2 (test) | `spring-boot-starter-test`, `spring-security-test` |
| `user-service` | `3.1.3` | `spring-cloud-starter-config`, `spring-boot-starter-actuator`, `spring-boot-starter-web`, `spring-boot-starter-security`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `nimbus-jose-jwt` | PostgreSQL (runtime), H2 (test) | `spring-boot-starter-test`, `spring-security-test` |

### 4.3. Análisis de Obsolescencia y Riesgos de Configuración Detectados

1. **Versión de Angular (Frontend):**
   - *Evidencia:* `frontend/tienda-frontend/package.json:13-20` (`@angular/core: ^16.2.0`).
   - *Conflicto con directriz:* `AGENTS.md:4` estipula: *"El front-end debe utilizar Angular en su ultima version"*. Angular 16 llegó a End-Of-Life (EOL) en noviembre de 2024. La última versión mayor actual es Angular 19/20.
   - *Impacto:* Brecha de cumplimiento de la regla estricta del proyecto; falta de características modernas como Signals nativos, nuevo control flow `@if/@for`, y componentes funcionales completos.

2. **Versión de Spring Boot y Spring Cloud (Backend):**
   - *Evidencia:* `services/*/pom.xml` (`spring-boot-starter-parent: 3.1.3`, `spring-cloud-starter-config: 4.0.4`).
   - *Diagnóstico:* Spring Boot 3.1.3 (lanzado en agosto de 2023) ya superó la ventana de soporte de código abierto OSS (mayo de 2024). Aunque es funcional y estable sobre Java 17 y Java 21, requiere plan de migración hacia Spring Boot 3.3.x o 3.4.x para soporte de seguridad a largo plazo.

3. **Imagen de PostgreSQL en Docker Compose:**
   - *Evidencia:* `compose.yml:4` (`image: postgres:18.6`) y `REQUERIMIENTOS_MICROSERVICIOS.md:24`.
   - *Diagnóstico:* En Docker Hub no existe la etiqueta `postgres:18.6` (las versiones estables de PostgreSQL son 16.x y 17.x; la versión 18 no ha sido liberada con versión menor 18.6). Si un operador ejecuta `docker compose up --build -d` sin construir una imagen personalizada con esa etiqueta, Docker arrojará error de pull (`manifest unknown` o `image not found`).

4. **Archivos de configuración duplicados y obsoletos (Legacy):**
   - *Evidencia:* `frontend/tienda-frontend/package_legacy.json` y `frontend/tienda-frontend/angular_legacy.json`.
   - *Diagnóstico:* Son artefactos huérfanos generados durante pruebas o migraciones previas que confunden a desarrolladores y herramientas de CI/CD.

## 5. Árbol Completo de Directorios y Archivos

A continuación se presenta el árbol estructural íntegro del proyecto (excluyendo únicamente los directorios declarados en el inventario: `.git`, `.angular`, `node_modules`, `target`, `dist`, `.idea`):

```
- `.dockerignore`
- `.env.example`
- `.gitignore`
- `AGENTS.md`
- `README.md`
- `REQUERIMIENTOS_MICROSERVICIOS.md`
- `compose.yml`
- `skills-lock.json`
- **.agents/** (`.agents/`)
  - **skills/** (`.agents/skills/`)
    - **angular-developer/** (`.agents/skills/angular-developer/`)
      - `SKILL.md`
      - **references/** (`.agents/skills/angular-developer/references/`)
        - `angular-animations.md`
        - `angular-aria.md`
        - `cli.md`
        - `component-harnesses.md`
        - `component-styling.md`
        - `components.md`
        - `creating-services.md`
        - `data-resolvers.md`
        - `define-routes.md`
        - `defining-providers.md`
        - `di-fundamentals.md`
        - `e2e-testing.md`
        - `effects.md`
        - `environment-configuration.md`
        - `hierarchical-injectors.md`
        - `host-elements.md`
        - `http-client.md`
        - `injection-context.md`
        - `inputs.md`
        - `linked-signal.md`
        - `loading-strategies.md`
        - `mcp.md`
        - `migrations.md`
        - `naming-conventions.md`
        - `navigate-to-routes.md`
        - `outputs.md`
        - `pipes.md`
        - `reactive-forms.md`
        - `rendering-strategies.md`
        - `resource.md`
        - `route-animations.md`
        - `route-guards.md`
        - `router-lifecycle.md`
        - `router-testing.md`
        - `show-routes-with-outlets.md`
        - `signal-forms.md`
        - `signals-overview.md`
        - `tailwind-css.md`
        - `template-driven-forms.md`
        - `testing-fundamentals.md`
    - **angular-testing/** (`.agents/skills/angular-testing/`)
      - `SKILL.md`
    - **code-review/** (`.agents/skills/code-review/`)
      - `SKILL.md`
      - **agents/** (`.agents/skills/code-review/agents/`)
        - `openai.yaml`
    - **frontend-design/** (`.agents/skills/frontend-design/`)
      - `LICENSE.txt`
      - `SKILL.md`
    - **image-to-code/** (`.agents/skills/image-to-code/`)
      - `SKILL.md`
    - **java-junit/** (`.agents/skills/java-junit/`)
      - `SKILL.md`
    - **java-microservices/** (`.agents/skills/java-microservices/`)
      - `SKILL.md`
      - **assets/** (`.agents/skills/java-microservices/assets/`)
        - `config.yaml`
        - `microservices-checklist.yaml`
        - `schema.json`
      - **references/** (`.agents/skills/java-microservices/references/`)
        - `GUIDE.md`
        - `PATTERNS.md`
      - **scripts/** (`.agents/skills/java-microservices/scripts/`)
        - `validate.py`
    - **java-springboot/** (`.agents/skills/java-springboot/`)
      - `SKILL.md`
- **.copilot/** (`.copilot/`)
  - **instructions/** (`.copilot/instructions/`)
    - `angular.instructions.md`
    - `java.instructions.md`
    - `postgreSql.instructions.md`
- **.vscode/** (`.vscode/`)
  - `settings.json`
- **docs/** (`docs/`)
  - `BACKEND.md`
  - `REQUISITOS_BACKEND.md`
  - `VALIDACION_BACKEND.md`
  - **superpowers/** (`docs/superpowers/`)
    - **plans/** (`docs/superpowers/plans/`)
      - `2026-09-06-microservicios.md`
- **frontend/** (`frontend/`)
  - **tienda-frontend/** (`frontend/tienda-frontend/`)
    - `.editorconfig`
    - `.gitignore`
    - `README.md`
    - `angular.json`
    - `angular_legacy.json`
    - `package-lock.json`
    - `package.json`
    - `package_legacy.json`
    - `tsconfig.app.json`
    - `tsconfig.json`
    - `tsconfig.spec.json`
    - **src/** (`frontend/tienda-frontend/src/`)
      - `favicon.ico`
      - `index.html`
      - `main.ts`
      - `polyfills.ts`
      - `styles.css`
      - **app/** (`frontend/tienda-frontend/src/app/`)
        - `app.component.css`
        - `app.component.html`
        - `app.component.spec.ts`
        - `app.component.ts`
        - `app.module.ts`
        - **atoms/** (`frontend/tienda-frontend/src/app/atoms/`)
          - `button.atom.ts`
          - `index.ts`
          - `input.atom.ts`
          - `label.atom.ts`
          - `topbar.atom.ts`
        - **molecules/** (`frontend/tienda-frontend/src/app/molecules/`)
          - `form-row.molecule.ts`
          - `index.ts`
          - `nav-tabs.molecule.ts`
        - **organisms/** (`frontend/tienda-frontend/src/app/organisms/`)
          - `clients.organism.ts`
          - `dashboard.organism.ts`
          - `index.ts`
          - `login.organism.ts`
          - `products.organism.ts`
          - `providers.organism.ts`
          - `reports.organism.ts`
          - `sales.organism.ts`
          - `users.organism.ts`
        - **pages/** (`frontend/tienda-frontend/src/app/pages/`)
          - `clients.page.ts`
          - `dashboard.page.ts`
          - `index.ts`
          - `login.page.ts`
          - `products.page.ts`
          - `providers.page.ts`
          - `reports.page.ts`
          - `sales.page.ts`
          - `users.page.ts`
        - **templates/** (`frontend/tienda-frontend/src/app/templates/`)
          - `index.ts`
          - `main-layout.template.ts`
      - **assets/** (`frontend/tienda-frontend/src/assets/`)
        - `.gitkeep`
- **infra/** (`infra/`)
  - `init-databases.sh`
- **output/** (`output/`)
  - **documents/** (`output/documents/`)
    - `Informe_Tienda_Generica_Microservicios.docx`
- **scripts/** (`scripts/`)
  - `check-java-functions.py`
  - `generate_inventory.py`
  - `generate_phase1.py`
  - `setup-env.ps1`
- **services/** (`services/`)
  - `Dockerfile`
  - `mvnw`
  - `mvnw.cmd`
  - `pom.xml`
  - **.mvn/** (`services/.mvn/`)
    - **wrapper/** (`services/.mvn/wrapper/`)
      - `maven-wrapper.properties`
  - **admin-service/** (`services/admin-service/`)
    - `Dockerfile`
    - `README.md`
    - `pom.xml`
    - **src/** (`services/admin-service/src/`)
      - **main/** (`services/admin-service/src/main/`)
        - **java/** (`services/admin-service/src/main/java/`)
          - **com/** (`services/admin-service/src/main/java/com/`)
            - **uelbosque/** (`services/admin-service/src/main/java/com/uelbosque/`)
              - **adminservice/** (`services/admin-service/src/main/java/com/uelbosque/adminservice/`)
                - `AdminServiceApplication.java`
                - `ReportClient.java`
                - `ReportController.java`
                - `ReportService.java`
                - `SalesReport.java`
        - **resources/** (`services/admin-service/src/main/resources/`)
          - `application-distributed.yml`
          - `application.yml`
    - **test/** (`services/admin-service/test/`)
      - **com/** (`services/admin-service/test/com/`)
        - **uelbosque/** (`services/admin-service/test/com/uelbosque/`)
          - **adminservice/** (`services/admin-service/test/com/uelbosque/adminservice/`)
            - `EndpointTest.java`
            - `ReportServiceTest.java`
  - **cart-service/** (`services/cart-service/`)
    - `Dockerfile`
    - `README.md`
    - `pom.xml`
    - **src/** (`services/cart-service/src/`)
      - **main/** (`services/cart-service/src/main/`)
        - **java/** (`services/cart-service/src/main/java/`)
          - **com/** (`services/cart-service/src/main/java/com/`)
            - **uelbosque/** (`services/cart-service/src/main/java/com/uelbosque/`)
              - **cartservice/** (`services/cart-service/src/main/java/com/uelbosque/cartservice/`)
                - `CartServiceApplication.java`
                - `CatalogReader.java`
                - **controller/** (`services/cart-service/src/main/java/com/uelbosque/cartservice/controller/`)
                  - `CartController.java`
                - **dto/** (`services/cart-service/src/main/java/com/uelbosque/cartservice/dto/`)
                  - `AddItemRequest.java`
                  - `ApplyDiscountRequest.java`
                  - `CartItemResponse.java`
                  - `CartResponse.java`
                  - `UpdateQuantityRequest.java`
                - **exception/** (`services/cart-service/src/main/java/com/uelbosque/cartservice/exception/`)
                  - `GlobalExceptionHandler.java`
                  - `InvalidCartOperationException.java`
                  - `ResourceNotFoundException.java`
                - **model/** (`services/cart-service/src/main/java/com/uelbosque/cartservice/model/`)
                  - `Cart.java`
                  - `CartItem.java`
                - **repository/** (`services/cart-service/src/main/java/com/uelbosque/cartservice/repository/`)
                  - `CartRepository.java`
                - **service/** (`services/cart-service/src/main/java/com/uelbosque/cartservice/service/`)
                  - `CartService.java`
        - **resources/** (`services/cart-service/src/main/resources/`)
          - `application-distributed.yml`
          - `application.yml`
      - **test/** (`services/cart-service/src/test/`)
        - **java/** (`services/cart-service/src/test/java/`)
          - **com/** (`services/cart-service/src/test/java/com/`)
            - **uelbosque/** (`services/cart-service/src/test/java/com/uelbosque/`)
              - **cartservice/** (`services/cart-service/src/test/java/com/uelbosque/cartservice/`)
                - **controller/** (`services/cart-service/src/test/java/com/uelbosque/cartservice/controller/`)
                  - `CartControllerTest.java`
                - **repository/** (`services/cart-service/src/test/java/com/uelbosque/cartservice/repository/`)
                  - `CartRepositoryTest.java`
                - **service/** (`services/cart-service/src/test/java/com/uelbosque/cartservice/service/`)
                  - `CartServiceTest.java`
        - **resources/** (`services/cart-service/src/test/resources/`)
          - `application.properties`
  - **catalog-service/** (`services/catalog-service/`)
    - `Dockerfile`
    - `README.md`
    - `pom.xml`
    - **src/** (`services/catalog-service/src/`)
      - **main/** (`services/catalog-service/src/main/`)
        - **java/** (`services/catalog-service/src/main/java/`)
          - **com/** (`services/catalog-service/src/main/java/com/`)
            - **uelbosque/** (`services/catalog-service/src/main/java/com/uelbosque/`)
              - **catalogservice/** (`services/catalog-service/src/main/java/com/uelbosque/catalogservice/`)
                - `CatalogLockRepository.java`
                - `CatalogMutex.java`
                - `CatalogServiceApplication.java`
                - `CatalogWriteLock.java`
                - `ProductCsvParser.java`
                - `ProductImportController.java`
                - `ProductImportService.java`
                - `SupplierDirectory.java`
                - **config/** (`services/catalog-service/src/main/java/com/uelbosque/catalogservice/config/`)
                  - `DataInitializer.java`
                - **controller/** (`services/catalog-service/src/main/java/com/uelbosque/catalogservice/controller/`)
                  - `ProductController.java`
                - **dto/** (`services/catalog-service/src/main/java/com/uelbosque/catalogservice/dto/`)
                  - `ProductRequestDto.java`
                  - `ProductResponseDto.java`
                - **exception/** (`services/catalog-service/src/main/java/com/uelbosque/catalogservice/exception/`)
                  - `DuplicateResourceException.java`
                  - `GlobalExceptionHandler.java`
                  - `ResourceNotFoundException.java`
                - **model/** (`services/catalog-service/src/main/java/com/uelbosque/catalogservice/model/`)
                  - `Product.java`
                - **repository/** (`services/catalog-service/src/main/java/com/uelbosque/catalogservice/repository/`)
                  - `ProductRepository.java`
                - **service/** (`services/catalog-service/src/main/java/com/uelbosque/catalogservice/service/`)
                  - `ProductService.java`
        - **resources/** (`services/catalog-service/src/main/resources/`)
          - `application-distributed.yml`
          - `application.yml`
      - **test/** (`services/catalog-service/src/test/`)
        - **java/** (`services/catalog-service/src/test/java/`)
          - **com/** (`services/catalog-service/src/test/java/com/`)
            - **uelbosque/** (`services/catalog-service/src/test/java/com/uelbosque/`)
              - **catalogservice/** (`services/catalog-service/src/test/java/com/uelbosque/catalogservice/`)
                - **controller/** (`services/catalog-service/src/test/java/com/uelbosque/catalogservice/controller/`)
                  - `ProductControllerTest.java`
                - **service/** (`services/catalog-service/src/test/java/com/uelbosque/catalogservice/service/`)
                  - `ProductServiceTest.java`
        - **resources/** (`services/catalog-service/src/test/resources/`)
          - `application.properties`
    - **test/** (`services/catalog-service/test/`)
      - **com/** (`services/catalog-service/test/com/`)
        - **uelbosque/** (`services/catalog-service/test/com/uelbosque/`)
          - **catalogservice/** (`services/catalog-service/test/com/uelbosque/catalogservice/`)
            - `CsvConcurrencyTest.java`
            - `CsvImportTest.java`
  - **config-server/** (`services/config-server/`)
    - `Dockerfile`
    - `README.md`
    - `pom.xml`
    - **src/** (`services/config-server/src/`)
      - **main/** (`services/config-server/src/main/`)
        - **java/** (`services/config-server/src/main/java/`)
          - **com/** (`services/config-server/src/main/java/com/`)
            - **uelbosque/** (`services/config-server/src/main/java/com/uelbosque/`)
              - **configserver/** (`services/config-server/src/main/java/com/uelbosque/configserver/`)
                - `ConfigSecurity.java`
                - `ConfigServerApplication.java`
        - **resources/** (`services/config-server/src/main/resources/`)
          - `application.yml`
          - **config/** (`services/config-server/src/main/resources/config/`)
            - `application.yml`
    - **test/** (`services/config-server/test/`)
      - **com/** (`services/config-server/test/com/`)
        - **uelbosque/** (`services/config-server/test/com/uelbosque/`)
          - **configserver/** (`services/config-server/test/com/uelbosque/configserver/`)
            - `ConfigServerTest.java`
  - **customer-service/** (`services/customer-service/`)
    - `Dockerfile`
    - `README.md`
    - `pom.xml`
    - **src/** (`services/customer-service/src/`)
      - **main/** (`services/customer-service/src/main/`)
        - **java/** (`services/customer-service/src/main/java/`)
          - **com/** (`services/customer-service/src/main/java/com/`)
            - **uelbosque/** (`services/customer-service/src/main/java/com/uelbosque/`)
              - **customerservice/** (`services/customer-service/src/main/java/com/uelbosque/customerservice/`)
                - `Application.java`
                - `Customer.java`
                - `CustomerController.java`
                - `CustomerData.java`
                - `CustomerRepository.java`
                - `CustomerService.java`
        - **resources/** (`services/customer-service/src/main/resources/`)
          - `application-distributed.yml`
          - `application.yml`
    - **test/** (`services/customer-service/test/`)
      - **com/** (`services/customer-service/test/com/`)
        - **uelbosque/** (`services/customer-service/test/com/uelbosque/`)
          - **customerservice/** (`services/customer-service/test/com/uelbosque/customerservice/`)
            - `CustomerApiTest.java`
  - **gateway/** (`services/gateway/`)
    - `Dockerfile`
    - `README.md`
    - `pom.xml`
    - **src/** (`services/gateway/src/`)
      - **main/** (`services/gateway/src/main/`)
        - **java/** (`services/gateway/src/main/java/`)
          - **com/** (`services/gateway/src/main/java/com/`)
            - **uelbosque/** (`services/gateway/src/main/java/com/uelbosque/`)
              - **gateway/** (`services/gateway/src/main/java/com/uelbosque/gateway/`)
                - `ApiGatewayApplication.java`
                - `DownstreamErrorHandler.java`
                - **config/** (`services/gateway/src/main/java/com/uelbosque/gateway/config/`)
                  - `CorsConfig.java`
                - **controller/** (`services/gateway/src/main/java/com/uelbosque/gateway/controller/`)
                  - `GatewayHealthController.java`
        - **resources/** (`services/gateway/src/main/resources/`)
          - `application-distributed.yml`
          - `application.yml`
      - **test/** (`services/gateway/src/test/`)
        - **java/** (`services/gateway/src/test/java/`)
          - **com/** (`services/gateway/src/test/java/com/`)
            - **uelbosque/** (`services/gateway/src/test/java/com/uelbosque/`)
              - **gateway/** (`services/gateway/src/test/java/com/uelbosque/gateway/`)
                - `GatewayApplicationTests.java`
                - `GatewayCorsTest.java`
                - `GatewayRoutesTest.java`
  - **inventory-service/** (`services/inventory-service/`)
    - `Dockerfile`
    - `README.md`
    - `pom.xml`
    - **src/** (`services/inventory-service/src/`)
      - **main/** (`services/inventory-service/src/main/`)
        - **java/** (`services/inventory-service/src/main/java/`)
          - **com/** (`services/inventory-service/src/main/java/com/`)
            - **uelbosque/** (`services/inventory-service/src/main/java/com/uelbosque/`)
              - **inventoryservice/** (`services/inventory-service/src/main/java/com/uelbosque/inventoryservice/`)
                - `InventoryController.java`
                - `InventoryService.java`
                - `InventoryServiceApplication.java`
                - `Reservation.java`
                - `ReservationRepository.java`
                - `ReservationRequest.java`
                - `ReservationResponse.java`
                - `Stock.java`
                - `StockRepository.java`
                - `StockResponse.java`
        - **resources/** (`services/inventory-service/src/main/resources/`)
          - `application-distributed.yml`
          - `application.yml`
    - **test/** (`services/inventory-service/test/`)
      - **com/** (`services/inventory-service/test/com/`)
        - **uelbosque/** (`services/inventory-service/test/com/uelbosque/`)
          - **inventoryservice/** (`services/inventory-service/test/com/uelbosque/inventoryservice/`)
            - `EndpointTest.java`
            - `InventoryRulesTest.java`
  - **notification-service/** (`services/notification-service/`)
    - `Dockerfile`
    - `README.md`
    - `pom.xml`
    - **src/** (`services/notification-service/src/`)
      - **main/** (`services/notification-service/src/main/`)
        - **java/** (`services/notification-service/src/main/java/`)
          - **com/** (`services/notification-service/src/main/java/com/`)
            - **uelbosque/** (`services/notification-service/src/main/java/com/uelbosque/`)
              - **notificationservice/** (`services/notification-service/src/main/java/com/uelbosque/notificationservice/`)
                - `MailDelivery.java`
                - `Notification.java`
                - `NotificationController.java`
                - `NotificationRepository.java`
                - `NotificationRequest.java`
                - `NotificationResponse.java`
                - `NotificationService.java`
                - `NotificationServiceApplication.java`
        - **resources/** (`services/notification-service/src/main/resources/`)
          - `application-distributed.yml`
          - `application.yml`
    - **test/** (`services/notification-service/test/`)
      - **com/** (`services/notification-service/test/com/`)
        - **uelbosque/** (`services/notification-service/test/com/uelbosque/`)
          - **notificationservice/** (`services/notification-service/test/com/uelbosque/notificationservice/`)
            - `EndpointTest.java`
            - `NotificationRulesTest.java`
  - **order-service/** (`services/order-service/`)
    - `Dockerfile`
    - `README.md`
    - `pom.xml`
    - **src/** (`services/order-service/src/`)
      - **main/** (`services/order-service/src/main/`)
        - **java/** (`services/order-service/src/main/java/`)
          - **com/** (`services/order-service/src/main/java/com/`)
            - **uelbosque/** (`services/order-service/src/main/java/com/uelbosque/`)
              - **orderservice/** (`services/order-service/src/main/java/com/uelbosque/orderservice/`)
                - `OrderServiceApplication.java`
                - `Sale.java`
                - `SaleController.java`
                - `SaleDirectory.java`
                - `SaleLine.java`
                - `SaleRepository.java`
                - `SaleRequest.java`
                - `SaleResponse.java`
                - `SaleService.java`
                - **controller/** (`services/order-service/src/main/java/com/uelbosque/orderservice/controller/`)
                  - `OrderController.java`
                - **dto/** (`services/order-service/src/main/java/com/uelbosque/orderservice/dto/`)
                  - `CreateOrderItemRequest.java`
                  - `CreateOrderRequest.java`
                  - `OrderItemResponse.java`
                  - `OrderResponse.java`
                  - `UpdateOrderStatusRequest.java`
                - **exception/** (`services/order-service/src/main/java/com/uelbosque/orderservice/exception/`)
                  - `GlobalExceptionHandler.java`
                  - `InvalidOrderStateException.java`
                  - `ResourceNotFoundException.java`
                - **model/** (`services/order-service/src/main/java/com/uelbosque/orderservice/model/`)
                  - `Order.java`
                  - `OrderItem.java`
                  - `OrderStatus.java`
                - **repository/** (`services/order-service/src/main/java/com/uelbosque/orderservice/repository/`)
                  - `OrderRepository.java`
                - **service/** (`services/order-service/src/main/java/com/uelbosque/orderservice/service/`)
                  - `OrderService.java`
        - **resources/** (`services/order-service/src/main/resources/`)
          - `application-distributed.yml`
          - `application.yml`
      - **test/** (`services/order-service/src/test/`)
        - **java/** (`services/order-service/src/test/java/`)
          - **com/** (`services/order-service/src/test/java/com/`)
            - **uelbosque/** (`services/order-service/src/test/java/com/uelbosque/`)
              - **orderservice/** (`services/order-service/src/test/java/com/uelbosque/orderservice/`)
                - **controller/** (`services/order-service/src/test/java/com/uelbosque/orderservice/controller/`)
                  - `OrderControllerTest.java`
                - **repository/** (`services/order-service/src/test/java/com/uelbosque/orderservice/repository/`)
                  - `OrderRepositoryTest.java`
                - **service/** (`services/order-service/src/test/java/com/uelbosque/orderservice/service/`)
                  - `OrderServiceTest.java`
        - **resources/** (`services/order-service/src/test/resources/`)
          - `application.properties`
    - **test/** (`services/order-service/test/`)
      - **com/** (`services/order-service/test/com/`)
        - **uelbosque/** (`services/order-service/test/com/uelbosque/`)
          - **orderservice/** (`services/order-service/test/com/uelbosque/orderservice/`)
            - `SaleServiceTest.java`
            - `SalesApiTest.java`
            - `SalesPersistenceTest.java`
  - **payment-service/** (`services/payment-service/`)
    - `Dockerfile`
    - `README.md`
    - `pom.xml`
    - **src/** (`services/payment-service/src/`)
      - **main/** (`services/payment-service/src/main/`)
        - **java/** (`services/payment-service/src/main/java/`)
          - **com/** (`services/payment-service/src/main/java/com/`)
            - **uelbosque/** (`services/payment-service/src/main/java/com/uelbosque/`)
              - **paymentservice/** (`services/payment-service/src/main/java/com/uelbosque/paymentservice/`)
                - `Payment.java`
                - `PaymentController.java`
                - `PaymentRepository.java`
                - `PaymentRequest.java`
                - `PaymentResponse.java`
                - `PaymentService.java`
                - `PaymentServiceApplication.java`
                - `SaleLookup.java`
        - **resources/** (`services/payment-service/src/main/resources/`)
          - `application-distributed.yml`
          - `application.yml`
    - **test/** (`services/payment-service/test/`)
      - **com/** (`services/payment-service/test/com/`)
        - **uelbosque/** (`services/payment-service/test/com/uelbosque/`)
          - **paymentservice/** (`services/payment-service/test/com/uelbosque/paymentservice/`)
            - `EndpointTest.java`
            - `PaymentInsertTest.java`
            - `PaymentRulesTest.java`
  - **platform-common/** (`services/platform-common/`)
    - `README.md`
    - `pom.xml`
    - **src/** (`services/platform-common/src/`)
      - **main/** (`services/platform-common/src/main/`)
        - **java/** (`services/platform-common/src/main/java/`)
          - **com/** (`services/platform-common/src/main/java/com/`)
            - **uelbosque/** (`services/platform-common/src/main/java/com/uelbosque/`)
              - **platformcommon/** (`services/platform-common/src/main/java/com/uelbosque/platformcommon/`)
                - `ApiErrors.java`
                - `ApiSecurity.java`
                - `PlatformConfiguration.java`
        - **resources/** (`services/platform-common/src/main/resources/`)
          - **META-INF/** (`services/platform-common/src/main/resources/META-INF/`)
            - **spring/** (`services/platform-common/src/main/resources/META-INF/spring/`)
              - `org.springframework.boot.autoconfigure.AutoConfiguration.imports`
    - **test/** (`services/platform-common/test/`)
      - **com/** (`services/platform-common/test/com/`)
        - **uelbosque/** (`services/platform-common/test/com/uelbosque/`)
          - **platformcommon/** (`services/platform-common/test/com/uelbosque/platformcommon/`)
            - `ApiSecurityTest.java`
  - **supplier-service/** (`services/supplier-service/`)
    - `Dockerfile`
    - `README.md`
    - `pom.xml`
    - **src/** (`services/supplier-service/src/`)
      - **main/** (`services/supplier-service/src/main/`)
        - **java/** (`services/supplier-service/src/main/java/`)
          - **com/** (`services/supplier-service/src/main/java/com/`)
            - **uelbosque/** (`services/supplier-service/src/main/java/com/uelbosque/`)
              - **supplierservice/** (`services/supplier-service/src/main/java/com/uelbosque/supplierservice/`)
                - `Application.java`
                - `Supplier.java`
                - `SupplierController.java`
                - `SupplierData.java`
                - `SupplierRepository.java`
                - `SupplierService.java`
        - **resources/** (`services/supplier-service/src/main/resources/`)
          - `application-distributed.yml`
          - `application.yml`
    - **test/** (`services/supplier-service/test/`)
      - **com/** (`services/supplier-service/test/com/`)
        - **uelbosque/** (`services/supplier-service/test/com/uelbosque/`)
          - **supplierservice/** (`services/supplier-service/test/com/uelbosque/supplierservice/`)
            - `SupplierApiTest.java`
  - **user-service/** (`services/user-service/`)
    - `Dockerfile`
    - `README.md`
    - `pom.xml`
    - **src/** (`services/user-service/src/`)
      - **main/** (`services/user-service/src/main/`)
        - **java/** (`services/user-service/src/main/java/`)
          - **com/** (`services/user-service/src/main/java/com/`)
            - **uelbosque/** (`services/user-service/src/main/java/com/uelbosque/`)
              - **userservice/** (`services/user-service/src/main/java/com/uelbosque/userservice/`)
                - `DataInitializer.java`
                - `UserServiceApplication.java`
                - **config/** (`services/user-service/src/main/java/com/uelbosque/userservice/config/`)
                  - `DataInitializer.java`
                  - `SecurityConfig.java`
                - **controller/** (`services/user-service/src/main/java/com/uelbosque/userservice/controller/`)
                  - `UserController.java`
                - **dto/** (`services/user-service/src/main/java/com/uelbosque/userservice/dto/`)
                  - `AuthResponse.java`
                  - `CreateUserRequest.java`
                  - `LoginRequest.java`
                  - `UpdateUserRequest.java`
                  - `UserResponse.java`
                - **exception/** (`services/user-service/src/main/java/com/uelbosque/userservice/exception/`)
                  - `DuplicateResourceException.java`
                  - `GlobalExceptionHandler.java`
                  - `ResourceNotFoundException.java`
                - **model/** (`services/user-service/src/main/java/com/uelbosque/userservice/model/`)
                  - `User.java`
                - **repository/** (`services/user-service/src/main/java/com/uelbosque/userservice/repository/`)
                  - `UserRepository.java`
                - **security/** (`services/user-service/src/main/java/com/uelbosque/userservice/security/`)
                  - `JwtAuthenticationFilter.java`
                  - `JwtTokenProvider.java`
                - **service/** (`services/user-service/src/main/java/com/uelbosque/userservice/service/`)
                  - `DatabaseUserDetailsService.java`
                  - `UserService.java`
        - **resources/** (`services/user-service/src/main/resources/`)
          - `application-distributed.yml`
          - `application.yml`
      - **test/** (`services/user-service/src/test/`)
        - **java/** (`services/user-service/src/test/java/`)
          - **com/** (`services/user-service/src/test/java/com/`)
            - **uelbosque/** (`services/user-service/src/test/java/com/uelbosque/`)
              - **userservice/** (`services/user-service/src/test/java/com/uelbosque/userservice/`)
                - **controller/** (`services/user-service/src/test/java/com/uelbosque/userservice/controller/`)
                  - `UserControllerTest.java`
                - **repository/** (`services/user-service/src/test/java/com/uelbosque/userservice/repository/`)
                  - `UserRepositoryTest.java`
                - **security/** (`services/user-service/src/test/java/com/uelbosque/userservice/security/`)
                  - `JwtTokenProviderTest.java`
                - **service/** (`services/user-service/src/test/java/com/uelbosque/userservice/service/`)
                  - `DatabaseUserDetailsServiceTest.java`
                  - `UserServiceTest.java`
        - **resources/** (`services/user-service/src/test/resources/`)
          - `application.properties`
    - **test/** (`services/user-service/test/`)
      - **com/** (`services/user-service/test/com/`)
        - **uelbosque/** (`services/user-service/test/com/uelbosque/`)
          - **userservice/** (`services/user-service/test/com/uelbosque/userservice/`)
            - `UserSecurityTest.java`
```
