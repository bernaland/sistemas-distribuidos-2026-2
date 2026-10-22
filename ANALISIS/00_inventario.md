# Inventario Completo y Checklist de Auditoría Técnica

## Resumen General del Inventario

- **Total de archivos auditables en el proyecto:** 359
- **Total de archivos binarios/assets no textuales excluidos:** 2
- **Directorios de build/cache/dependencias excluidos:** 6 (`.git`, `.angular`, `node_modules`, `dist`, `target`, `.idea`)

### Desglose por Módulo / Directorio

| Módulo / Directorio | Cantidad de Archivos | Estado |
|---|---|---|
| `.agents` | 56 | Pendiente |
| `.copilot` | 3 | Pendiente |
| `.vscode` | 1 | Pendiente |
| `docs` | 4 | Pendiente |
| `frontend/tienda-frontend` | 49 | Pendiente |
| `infra` | 1 | Pendiente |
| `root (archivos de configuración)` | 8 | Pendiente |
| `scripts` | 3 | Pendiente |
| `services (root/build)` | 5 | Pendiente |
| `services/admin-service` | 12 | Pendiente |
| `services/cart-service` | 24 | Pendiente |
| `services/catalog-service` | 28 | Pendiente |
| `services/config-server` | 8 | Pendiente |
| `services/customer-service` | 12 | Pendiente |
| `services/gateway` | 12 | Pendiente |
| `services/inventory-service` | 17 | Pendiente |
| `services/notification-service` | 15 | Pendiente |
| `services/order-service` | 35 | Pendiente |
| `services/payment-service` | 16 | Pendiente |
| `services/platform-common` | 7 | Pendiente |
| `services/supplier-service` | 12 | Pendiente |
| `services/user-service` | 31 | Pendiente |
| **TOTAL** | **359** | **Pendiente** |

## Archivos y Directorios Excluidos de Lectura Directa

Conforme a la **Regla Estricta 1**, se declara explícitamente qué carpetas y archivos no son leídos como código fuente y se justifica el motivo:

1. `.git/`: Metadatos internos y base de datos de objetos del sistema de control de versiones Git. No contienen lógica de negocio de la aplicación.
2. `frontend/tienda-frontend/.angular/`: Caché efímero interno del compilador y build tool de Angular CLI.
3. `frontend/tienda-frontend/node_modules/`: Dependencias de librerías de terceros gestionadas por npm. Se auditan a través de `package.json` y `package-lock.json`.
4. `frontend/tienda-frontend/dist/`: Salida compilada (build artifacts minificados/transpilados) de Angular generados a partir de `src/`. Se verifica el proceso de build en la fase correspondiente.
5. `**/target/`: Archivos binarios compilados Java (.class, .jar) producidos por Maven. Se audita el código fuente Java en `src/`.
6. `.idea/`: Archivos temporales de caché de IDE JetBrains/IntelliJ.

### Archivos binarios específicos excluidos:

- `frontend/tienda-frontend/src/favicon.ico`: Archivo binario / multimedia no textual (.ico)
- `output/documents/Informe_Tienda_Generica_Microservicios.docx`: Archivo binario / multimedia no textual (.docx)

---

## Checklist de Auditoría Archivo por Archivo

### Módulo: `.agents` (56 archivos)

- [x] `.agents/skills/angular-developer/SKILL.md`
- [x] `.agents/skills/angular-developer/references/angular-animations.md`
- [x] `.agents/skills/angular-developer/references/angular-aria.md`
- [x] `.agents/skills/angular-developer/references/cli.md`
- [x] `.agents/skills/angular-developer/references/component-harnesses.md`
- [x] `.agents/skills/angular-developer/references/component-styling.md`
- [x] `.agents/skills/angular-developer/references/components.md`
- [x] `.agents/skills/angular-developer/references/creating-services.md`
- [x] `.agents/skills/angular-developer/references/data-resolvers.md`
- [x] `.agents/skills/angular-developer/references/define-routes.md`
- [x] `.agents/skills/angular-developer/references/defining-providers.md`
- [x] `.agents/skills/angular-developer/references/di-fundamentals.md`
- [x] `.agents/skills/angular-developer/references/e2e-testing.md`
- [x] `.agents/skills/angular-developer/references/effects.md`
- [x] `.agents/skills/angular-developer/references/environment-configuration.md`
- [x] `.agents/skills/angular-developer/references/hierarchical-injectors.md`
- [x] `.agents/skills/angular-developer/references/host-elements.md`
- [x] `.agents/skills/angular-developer/references/http-client.md`
- [x] `.agents/skills/angular-developer/references/injection-context.md`
- [x] `.agents/skills/angular-developer/references/inputs.md`
- [x] `.agents/skills/angular-developer/references/linked-signal.md`
- [x] `.agents/skills/angular-developer/references/loading-strategies.md`
- [x] `.agents/skills/angular-developer/references/mcp.md`
- [x] `.agents/skills/angular-developer/references/migrations.md`
- [x] `.agents/skills/angular-developer/references/naming-conventions.md`
- [x] `.agents/skills/angular-developer/references/navigate-to-routes.md`
- [x] `.agents/skills/angular-developer/references/outputs.md`
- [x] `.agents/skills/angular-developer/references/pipes.md`
- [x] `.agents/skills/angular-developer/references/reactive-forms.md`
- [x] `.agents/skills/angular-developer/references/rendering-strategies.md`
- [x] `.agents/skills/angular-developer/references/resource.md`
- [x] `.agents/skills/angular-developer/references/route-animations.md`
- [x] `.agents/skills/angular-developer/references/route-guards.md`
- [x] `.agents/skills/angular-developer/references/router-lifecycle.md`
- [x] `.agents/skills/angular-developer/references/router-testing.md`
- [x] `.agents/skills/angular-developer/references/show-routes-with-outlets.md`
- [x] `.agents/skills/angular-developer/references/signal-forms.md`
- [x] `.agents/skills/angular-developer/references/signals-overview.md`
- [x] `.agents/skills/angular-developer/references/tailwind-css.md`
- [x] `.agents/skills/angular-developer/references/template-driven-forms.md`
- [x] `.agents/skills/angular-developer/references/testing-fundamentals.md`
- [x] `.agents/skills/angular-testing/SKILL.md`
- [x] `.agents/skills/code-review/SKILL.md`
- [x] `.agents/skills/code-review/agents/openai.yaml`
- [x] `.agents/skills/frontend-design/LICENSE.txt`
- [x] `.agents/skills/frontend-design/SKILL.md`
- [x] `.agents/skills/image-to-code/SKILL.md`
- [x] `.agents/skills/java-junit/SKILL.md`
- [x] `.agents/skills/java-microservices/SKILL.md`
- [x] `.agents/skills/java-microservices/assets/config.yaml`
- [x] `.agents/skills/java-microservices/assets/microservices-checklist.yaml`
- [x] `.agents/skills/java-microservices/assets/schema.json`
- [x] `.agents/skills/java-microservices/references/GUIDE.md`
- [x] `.agents/skills/java-microservices/references/PATTERNS.md`
- [x] `.agents/skills/java-microservices/scripts/validate.py`
- [x] `.agents/skills/java-springboot/SKILL.md`

### Módulo: `.copilot` (3 archivos)

- [x] `.copilot/instructions/angular.instructions.md`
- [x] `.copilot/instructions/java.instructions.md`
- [x] `.copilot/instructions/postgreSql.instructions.md`

### Módulo: `.vscode` (1 archivos)

- [x] `.vscode/settings.json`

### Módulo: `docs` (4 archivos)

- [x] `docs/BACKEND.md`
- [x] `docs/REQUISITOS_BACKEND.md`
- [x] `docs/VALIDACION_BACKEND.md`
- [x] `docs/superpowers/plans/2026-09-06-microservicios.md`

### Módulo: `frontend/tienda-frontend` (49 archivos)

- [x] `frontend/tienda-frontend/.editorconfig`
- [x] `frontend/tienda-frontend/.gitignore`
- [x] `frontend/tienda-frontend/README.md`
- [x] `frontend/tienda-frontend/angular.json`
- [x] `frontend/tienda-frontend/angular_legacy.json`
- [x] `frontend/tienda-frontend/package-lock.json`
- [x] `frontend/tienda-frontend/package.json`
- [x] `frontend/tienda-frontend/package_legacy.json`
- [x] `frontend/tienda-frontend/src/app/app.component.css`
- [x] `frontend/tienda-frontend/src/app/app.component.html`
- [x] `frontend/tienda-frontend/src/app/app.component.spec.ts`
- [x] `frontend/tienda-frontend/src/app/app.component.ts`
- [x] `frontend/tienda-frontend/src/app/app.module.ts`
- [x] `frontend/tienda-frontend/src/app/atoms/button.atom.ts`
- [x] `frontend/tienda-frontend/src/app/atoms/index.ts`
- [x] `frontend/tienda-frontend/src/app/atoms/input.atom.ts`
- [x] `frontend/tienda-frontend/src/app/atoms/label.atom.ts`
- [x] `frontend/tienda-frontend/src/app/atoms/topbar.atom.ts`
- [x] `frontend/tienda-frontend/src/app/molecules/form-row.molecule.ts`
- [x] `frontend/tienda-frontend/src/app/molecules/index.ts`
- [x] `frontend/tienda-frontend/src/app/molecules/nav-tabs.molecule.ts`
- [x] `frontend/tienda-frontend/src/app/organisms/clients.organism.ts`
- [x] `frontend/tienda-frontend/src/app/organisms/dashboard.organism.ts`
- [x] `frontend/tienda-frontend/src/app/organisms/index.ts`
- [x] `frontend/tienda-frontend/src/app/organisms/login.organism.ts`
- [x] `frontend/tienda-frontend/src/app/organisms/products.organism.ts`
- [x] `frontend/tienda-frontend/src/app/organisms/providers.organism.ts`
- [x] `frontend/tienda-frontend/src/app/organisms/reports.organism.ts`
- [x] `frontend/tienda-frontend/src/app/organisms/sales.organism.ts`
- [x] `frontend/tienda-frontend/src/app/organisms/users.organism.ts`
- [x] `frontend/tienda-frontend/src/app/pages/clients.page.ts`
- [x] `frontend/tienda-frontend/src/app/pages/dashboard.page.ts`
- [x] `frontend/tienda-frontend/src/app/pages/index.ts`
- [x] `frontend/tienda-frontend/src/app/pages/login.page.ts`
- [x] `frontend/tienda-frontend/src/app/pages/products.page.ts`
- [x] `frontend/tienda-frontend/src/app/pages/providers.page.ts`
- [x] `frontend/tienda-frontend/src/app/pages/reports.page.ts`
- [x] `frontend/tienda-frontend/src/app/pages/sales.page.ts`
- [x] `frontend/tienda-frontend/src/app/pages/users.page.ts`
- [x] `frontend/tienda-frontend/src/app/templates/index.ts`
- [x] `frontend/tienda-frontend/src/app/templates/main-layout.template.ts`
- [x] `frontend/tienda-frontend/src/assets/.gitkeep`
- [x] `frontend/tienda-frontend/src/index.html`
- [x] `frontend/tienda-frontend/src/main.ts`
- [x] `frontend/tienda-frontend/src/polyfills.ts`
- [x] `frontend/tienda-frontend/src/styles.css`
- [x] `frontend/tienda-frontend/tsconfig.app.json`
- [x] `frontend/tienda-frontend/tsconfig.json`
- [x] `frontend/tienda-frontend/tsconfig.spec.json`

### Módulo: `infra` (1 archivos)

- [x] `infra/init-databases.sh`

### Módulo: `root (archivos de configuración)` (8 archivos)

- [x] `.dockerignore`
- [x] `.env.example`
- [x] `.gitignore`
- [x] `AGENTS.md`
- [x] `README.md`
- [x] `REQUERIMIENTOS_MICROSERVICIOS.md`
- [x] `compose.yml`
- [x] `skills-lock.json`

### Módulo: `scripts` (3 archivos)

- [x] `scripts/check-java-functions.py`
- [x] `scripts/generate_inventory.py`
- [x] `scripts/setup-env.ps1`

### Módulo: `services (root/build)` (5 archivos)

- [x] `services/.mvn/wrapper/maven-wrapper.properties`
- [x] `services/Dockerfile`
- [x] `services/mvnw`
- [x] `services/mvnw.cmd`
- [x] `services/pom.xml`

### Módulo: `services/admin-service` (12 archivos)

- [x] `services/admin-service/Dockerfile`
- [x] `services/admin-service/README.md`
- [x] `services/admin-service/pom.xml`
- [x] `services/admin-service/src/main/java/com/uelbosque/adminservice/AdminServiceApplication.java`
- [x] `services/admin-service/src/main/java/com/uelbosque/adminservice/ReportClient.java`
- [x] `services/admin-service/src/main/java/com/uelbosque/adminservice/ReportController.java`
- [x] `services/admin-service/src/main/java/com/uelbosque/adminservice/ReportService.java`
- [x] `services/admin-service/src/main/java/com/uelbosque/adminservice/SalesReport.java`
- [x] `services/admin-service/src/main/resources/application-distributed.yml`
- [x] `services/admin-service/src/main/resources/application.yml`
- [x] `services/admin-service/test/com/uelbosque/adminservice/EndpointTest.java`
- [x] `services/admin-service/test/com/uelbosque/adminservice/ReportServiceTest.java`

### Módulo: `services/cart-service` (24 archivos)

- [x] `services/cart-service/Dockerfile`
- [x] `services/cart-service/README.md`
- [x] `services/cart-service/pom.xml`
- [x] `services/cart-service/src/main/java/com/uelbosque/cartservice/CartServiceApplication.java`
- [x] `services/cart-service/src/main/java/com/uelbosque/cartservice/CatalogReader.java`
- [x] `services/cart-service/src/main/java/com/uelbosque/cartservice/controller/CartController.java`
- [x] `services/cart-service/src/main/java/com/uelbosque/cartservice/dto/AddItemRequest.java`
- [x] `services/cart-service/src/main/java/com/uelbosque/cartservice/dto/ApplyDiscountRequest.java`
- [x] `services/cart-service/src/main/java/com/uelbosque/cartservice/dto/CartItemResponse.java`
- [x] `services/cart-service/src/main/java/com/uelbosque/cartservice/dto/CartResponse.java`
- [x] `services/cart-service/src/main/java/com/uelbosque/cartservice/dto/UpdateQuantityRequest.java`
- [x] `services/cart-service/src/main/java/com/uelbosque/cartservice/exception/GlobalExceptionHandler.java`
- [x] `services/cart-service/src/main/java/com/uelbosque/cartservice/exception/InvalidCartOperationException.java`
- [x] `services/cart-service/src/main/java/com/uelbosque/cartservice/exception/ResourceNotFoundException.java`
- [x] `services/cart-service/src/main/java/com/uelbosque/cartservice/model/Cart.java`
- [x] `services/cart-service/src/main/java/com/uelbosque/cartservice/model/CartItem.java`
- [x] `services/cart-service/src/main/java/com/uelbosque/cartservice/repository/CartRepository.java`
- [x] `services/cart-service/src/main/java/com/uelbosque/cartservice/service/CartService.java`
- [x] `services/cart-service/src/main/resources/application-distributed.yml`
- [x] `services/cart-service/src/main/resources/application.yml`
- [x] `services/cart-service/src/test/java/com/uelbosque/cartservice/controller/CartControllerTest.java`
- [x] `services/cart-service/src/test/java/com/uelbosque/cartservice/repository/CartRepositoryTest.java`
- [x] `services/cart-service/src/test/java/com/uelbosque/cartservice/service/CartServiceTest.java`
- [x] `services/cart-service/src/test/resources/application.properties`

### Módulo: `services/catalog-service` (28 archivos)

- [x] `services/catalog-service/Dockerfile`
- [x] `services/catalog-service/README.md`
- [x] `services/catalog-service/pom.xml`
- [x] `services/catalog-service/src/main/java/com/uelbosque/catalogservice/CatalogLockRepository.java`
- [x] `services/catalog-service/src/main/java/com/uelbosque/catalogservice/CatalogMutex.java`
- [x] `services/catalog-service/src/main/java/com/uelbosque/catalogservice/CatalogServiceApplication.java`
- [x] `services/catalog-service/src/main/java/com/uelbosque/catalogservice/CatalogWriteLock.java`
- [x] `services/catalog-service/src/main/java/com/uelbosque/catalogservice/ProductCsvParser.java`
- [x] `services/catalog-service/src/main/java/com/uelbosque/catalogservice/ProductImportController.java`
- [x] `services/catalog-service/src/main/java/com/uelbosque/catalogservice/ProductImportService.java`
- [x] `services/catalog-service/src/main/java/com/uelbosque/catalogservice/SupplierDirectory.java`
- [x] `services/catalog-service/src/main/java/com/uelbosque/catalogservice/config/DataInitializer.java`
- [x] `services/catalog-service/src/main/java/com/uelbosque/catalogservice/controller/ProductController.java`
- [x] `services/catalog-service/src/main/java/com/uelbosque/catalogservice/dto/ProductRequestDto.java`
- [x] `services/catalog-service/src/main/java/com/uelbosque/catalogservice/dto/ProductResponseDto.java`
- [x] `services/catalog-service/src/main/java/com/uelbosque/catalogservice/exception/DuplicateResourceException.java`
- [x] `services/catalog-service/src/main/java/com/uelbosque/catalogservice/exception/GlobalExceptionHandler.java`
- [x] `services/catalog-service/src/main/java/com/uelbosque/catalogservice/exception/ResourceNotFoundException.java`
- [x] `services/catalog-service/src/main/java/com/uelbosque/catalogservice/model/Product.java`
- [x] `services/catalog-service/src/main/java/com/uelbosque/catalogservice/repository/ProductRepository.java`
- [x] `services/catalog-service/src/main/java/com/uelbosque/catalogservice/service/ProductService.java`
- [x] `services/catalog-service/src/main/resources/application-distributed.yml`
- [x] `services/catalog-service/src/main/resources/application.yml`
- [x] `services/catalog-service/src/test/java/com/uelbosque/catalogservice/controller/ProductControllerTest.java`
- [x] `services/catalog-service/src/test/java/com/uelbosque/catalogservice/service/ProductServiceTest.java`
- [x] `services/catalog-service/src/test/resources/application.properties`
- [x] `services/catalog-service/test/com/uelbosque/catalogservice/CsvConcurrencyTest.java`
- [x] `services/catalog-service/test/com/uelbosque/catalogservice/CsvImportTest.java`

### Módulo: `services/config-server` (8 archivos)

- [x] `services/config-server/Dockerfile`
- [x] `services/config-server/README.md`
- [x] `services/config-server/pom.xml`
- [x] `services/config-server/src/main/java/com/uelbosque/configserver/ConfigSecurity.java`
- [x] `services/config-server/src/main/java/com/uelbosque/configserver/ConfigServerApplication.java`
- [x] `services/config-server/src/main/resources/application.yml`
- [x] `services/config-server/src/main/resources/config/application.yml`
- [x] `services/config-server/test/com/uelbosque/configserver/ConfigServerTest.java`

### Módulo: `services/customer-service` (12 archivos)

- [x] `services/customer-service/Dockerfile`
- [x] `services/customer-service/README.md`
- [x] `services/customer-service/pom.xml`
- [x] `services/customer-service/src/main/java/com/uelbosque/customerservice/Application.java`
- [x] `services/customer-service/src/main/java/com/uelbosque/customerservice/Customer.java`
- [x] `services/customer-service/src/main/java/com/uelbosque/customerservice/CustomerController.java`
- [x] `services/customer-service/src/main/java/com/uelbosque/customerservice/CustomerData.java`
- [x] `services/customer-service/src/main/java/com/uelbosque/customerservice/CustomerRepository.java`
- [x] `services/customer-service/src/main/java/com/uelbosque/customerservice/CustomerService.java`
- [x] `services/customer-service/src/main/resources/application-distributed.yml`
- [x] `services/customer-service/src/main/resources/application.yml`
- [x] `services/customer-service/test/com/uelbosque/customerservice/CustomerApiTest.java`

### Módulo: `services/gateway` (12 archivos)

- [x] `services/gateway/Dockerfile`
- [x] `services/gateway/README.md`
- [x] `services/gateway/pom.xml`
- [x] `services/gateway/src/main/java/com/uelbosque/gateway/ApiGatewayApplication.java`
- [x] `services/gateway/src/main/java/com/uelbosque/gateway/DownstreamErrorHandler.java`
- [x] `services/gateway/src/main/java/com/uelbosque/gateway/config/CorsConfig.java`
- [x] `services/gateway/src/main/java/com/uelbosque/gateway/controller/GatewayHealthController.java`
- [x] `services/gateway/src/main/resources/application-distributed.yml`
- [x] `services/gateway/src/main/resources/application.yml`
- [x] `services/gateway/src/test/java/com/uelbosque/gateway/GatewayApplicationTests.java`
- [x] `services/gateway/src/test/java/com/uelbosque/gateway/GatewayCorsTest.java`
- [x] `services/gateway/src/test/java/com/uelbosque/gateway/GatewayRoutesTest.java`

### Módulo: `services/inventory-service` (17 archivos)

- [x] `services/inventory-service/Dockerfile`
- [x] `services/inventory-service/README.md`
- [x] `services/inventory-service/pom.xml`
- [x] `services/inventory-service/src/main/java/com/uelbosque/inventoryservice/InventoryController.java`
- [x] `services/inventory-service/src/main/java/com/uelbosque/inventoryservice/InventoryService.java`
- [x] `services/inventory-service/src/main/java/com/uelbosque/inventoryservice/InventoryServiceApplication.java`
- [x] `services/inventory-service/src/main/java/com/uelbosque/inventoryservice/Reservation.java`
- [x] `services/inventory-service/src/main/java/com/uelbosque/inventoryservice/ReservationRepository.java`
- [x] `services/inventory-service/src/main/java/com/uelbosque/inventoryservice/ReservationRequest.java`
- [x] `services/inventory-service/src/main/java/com/uelbosque/inventoryservice/ReservationResponse.java`
- [x] `services/inventory-service/src/main/java/com/uelbosque/inventoryservice/Stock.java`
- [x] `services/inventory-service/src/main/java/com/uelbosque/inventoryservice/StockRepository.java`
- [x] `services/inventory-service/src/main/java/com/uelbosque/inventoryservice/StockResponse.java`
- [x] `services/inventory-service/src/main/resources/application-distributed.yml`
- [x] `services/inventory-service/src/main/resources/application.yml`
- [x] `services/inventory-service/test/com/uelbosque/inventoryservice/EndpointTest.java`
- [x] `services/inventory-service/test/com/uelbosque/inventoryservice/InventoryRulesTest.java`

### Módulo: `services/notification-service` (15 archivos)

- [x] `services/notification-service/Dockerfile`
- [x] `services/notification-service/README.md`
- [x] `services/notification-service/pom.xml`
- [x] `services/notification-service/src/main/java/com/uelbosque/notificationservice/MailDelivery.java`
- [x] `services/notification-service/src/main/java/com/uelbosque/notificationservice/Notification.java`
- [x] `services/notification-service/src/main/java/com/uelbosque/notificationservice/NotificationController.java`
- [x] `services/notification-service/src/main/java/com/uelbosque/notificationservice/NotificationRepository.java`
- [x] `services/notification-service/src/main/java/com/uelbosque/notificationservice/NotificationRequest.java`
- [x] `services/notification-service/src/main/java/com/uelbosque/notificationservice/NotificationResponse.java`
- [x] `services/notification-service/src/main/java/com/uelbosque/notificationservice/NotificationService.java`
- [x] `services/notification-service/src/main/java/com/uelbosque/notificationservice/NotificationServiceApplication.java`
- [x] `services/notification-service/src/main/resources/application-distributed.yml`
- [x] `services/notification-service/src/main/resources/application.yml`
- [x] `services/notification-service/test/com/uelbosque/notificationservice/EndpointTest.java`
- [x] `services/notification-service/test/com/uelbosque/notificationservice/NotificationRulesTest.java`

### Módulo: `services/order-service` (35 archivos)

- [x] `services/order-service/Dockerfile`
- [x] `services/order-service/README.md`
- [x] `services/order-service/pom.xml`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/OrderServiceApplication.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/Sale.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/SaleController.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/SaleDirectory.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/SaleLine.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/SaleRepository.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/SaleRequest.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/SaleResponse.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/SaleService.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/controller/OrderController.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/dto/CreateOrderItemRequest.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/dto/CreateOrderRequest.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/dto/OrderItemResponse.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/dto/OrderResponse.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/dto/UpdateOrderStatusRequest.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/exception/GlobalExceptionHandler.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/exception/InvalidOrderStateException.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/exception/ResourceNotFoundException.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/model/Order.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/model/OrderItem.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/model/OrderStatus.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/repository/OrderRepository.java`
- [x] `services/order-service/src/main/java/com/uelbosque/orderservice/service/OrderService.java`
- [x] `services/order-service/src/main/resources/application-distributed.yml`
- [x] `services/order-service/src/main/resources/application.yml`
- [x] `services/order-service/src/test/java/com/uelbosque/orderservice/controller/OrderControllerTest.java`
- [x] `services/order-service/src/test/java/com/uelbosque/orderservice/repository/OrderRepositoryTest.java`
- [x] `services/order-service/src/test/java/com/uelbosque/orderservice/service/OrderServiceTest.java`
- [x] `services/order-service/src/test/resources/application.properties`
- [x] `services/order-service/test/com/uelbosque/orderservice/SaleServiceTest.java`
- [x] `services/order-service/test/com/uelbosque/orderservice/SalesApiTest.java`
- [x] `services/order-service/test/com/uelbosque/orderservice/SalesPersistenceTest.java`

### Módulo: `services/payment-service` (16 archivos)

- [x] `services/payment-service/Dockerfile`
- [x] `services/payment-service/README.md`
- [x] `services/payment-service/pom.xml`
- [x] `services/payment-service/src/main/java/com/uelbosque/paymentservice/Payment.java`
- [x] `services/payment-service/src/main/java/com/uelbosque/paymentservice/PaymentController.java`
- [x] `services/payment-service/src/main/java/com/uelbosque/paymentservice/PaymentRepository.java`
- [x] `services/payment-service/src/main/java/com/uelbosque/paymentservice/PaymentRequest.java`
- [x] `services/payment-service/src/main/java/com/uelbosque/paymentservice/PaymentResponse.java`
- [x] `services/payment-service/src/main/java/com/uelbosque/paymentservice/PaymentService.java`
- [x] `services/payment-service/src/main/java/com/uelbosque/paymentservice/PaymentServiceApplication.java`
- [x] `services/payment-service/src/main/java/com/uelbosque/paymentservice/SaleLookup.java`
- [x] `services/payment-service/src/main/resources/application-distributed.yml`
- [x] `services/payment-service/src/main/resources/application.yml`
- [x] `services/payment-service/test/com/uelbosque/paymentservice/EndpointTest.java`
- [x] `services/payment-service/test/com/uelbosque/paymentservice/PaymentInsertTest.java`
- [x] `services/payment-service/test/com/uelbosque/paymentservice/PaymentRulesTest.java`

### Módulo: `services/platform-common` (7 archivos)

- [x] `services/platform-common/README.md`
- [x] `services/platform-common/pom.xml`
- [x] `services/platform-common/src/main/java/com/uelbosque/platformcommon/ApiErrors.java`
- [x] `services/platform-common/src/main/java/com/uelbosque/platformcommon/ApiSecurity.java`
- [x] `services/platform-common/src/main/java/com/uelbosque/platformcommon/PlatformConfiguration.java`
- [x] `services/platform-common/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- [x] `services/platform-common/test/com/uelbosque/platformcommon/ApiSecurityTest.java`

### Módulo: `services/supplier-service` (12 archivos)

- [x] `services/supplier-service/Dockerfile`
- [x] `services/supplier-service/README.md`
- [x] `services/supplier-service/pom.xml`
- [x] `services/supplier-service/src/main/java/com/uelbosque/supplierservice/Application.java`
- [x] `services/supplier-service/src/main/java/com/uelbosque/supplierservice/Supplier.java`
- [x] `services/supplier-service/src/main/java/com/uelbosque/supplierservice/SupplierController.java`
- [x] `services/supplier-service/src/main/java/com/uelbosque/supplierservice/SupplierData.java`
- [x] `services/supplier-service/src/main/java/com/uelbosque/supplierservice/SupplierRepository.java`
- [x] `services/supplier-service/src/main/java/com/uelbosque/supplierservice/SupplierService.java`
- [x] `services/supplier-service/src/main/resources/application-distributed.yml`
- [x] `services/supplier-service/src/main/resources/application.yml`
- [x] `services/supplier-service/test/com/uelbosque/supplierservice/SupplierApiTest.java`

### Módulo: `services/user-service` (31 archivos)

- [x] `services/user-service/Dockerfile`
- [x] `services/user-service/README.md`
- [x] `services/user-service/pom.xml`
- [x] `services/user-service/src/main/java/com/uelbosque/userservice/DataInitializer.java`
- [x] `services/user-service/src/main/java/com/uelbosque/userservice/UserServiceApplication.java`
- [x] `services/user-service/src/main/java/com/uelbosque/userservice/config/DataInitializer.java`
- [x] `services/user-service/src/main/java/com/uelbosque/userservice/config/SecurityConfig.java`
- [x] `services/user-service/src/main/java/com/uelbosque/userservice/controller/UserController.java`
- [x] `services/user-service/src/main/java/com/uelbosque/userservice/dto/AuthResponse.java`
- [x] `services/user-service/src/main/java/com/uelbosque/userservice/dto/CreateUserRequest.java`
- [x] `services/user-service/src/main/java/com/uelbosque/userservice/dto/LoginRequest.java`
- [x] `services/user-service/src/main/java/com/uelbosque/userservice/dto/UpdateUserRequest.java`
- [x] `services/user-service/src/main/java/com/uelbosque/userservice/dto/UserResponse.java`
- [x] `services/user-service/src/main/java/com/uelbosque/userservice/exception/DuplicateResourceException.java`
- [x] `services/user-service/src/main/java/com/uelbosque/userservice/exception/GlobalExceptionHandler.java`
- [x] `services/user-service/src/main/java/com/uelbosque/userservice/exception/ResourceNotFoundException.java`
- [x] `services/user-service/src/main/java/com/uelbosque/userservice/model/User.java`
- [x] `services/user-service/src/main/java/com/uelbosque/userservice/repository/UserRepository.java`
- [x] `services/user-service/src/main/java/com/uelbosque/userservice/security/JwtAuthenticationFilter.java`
- [x] `services/user-service/src/main/java/com/uelbosque/userservice/security/JwtTokenProvider.java`
- [x] `services/user-service/src/main/java/com/uelbosque/userservice/service/DatabaseUserDetailsService.java`
- [x] `services/user-service/src/main/java/com/uelbosque/userservice/service/UserService.java`
- [x] `services/user-service/src/main/resources/application-distributed.yml`
- [x] `services/user-service/src/main/resources/application.yml`
- [x] `services/user-service/src/test/java/com/uelbosque/userservice/controller/UserControllerTest.java`
- [x] `services/user-service/src/test/java/com/uelbosque/userservice/repository/UserRepositoryTest.java`
- [x] `services/user-service/src/test/java/com/uelbosque/userservice/security/JwtTokenProviderTest.java`
- [x] `services/user-service/src/test/java/com/uelbosque/userservice/service/DatabaseUserDetailsServiceTest.java`
- [x] `services/user-service/src/test/java/com/uelbosque/userservice/service/UserServiceTest.java`
- [x] `services/user-service/src/test/resources/application.properties`
- [x] `services/user-service/test/com/uelbosque/userservice/UserSecurityTest.java`

