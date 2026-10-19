# Matriz de Trazabilidad de Historias de Usuario (HU-001 a HU-023)

Correspondencia y trazabilidad de los requerimientos funcionales del sistema Tienda Genérica distribuidos entre microservicios backend Spring Boot, endpoints REST, pantallas de frontend en Angular 22 y suites de pruebas automatizadas (unitarias y E2E).

---

| Historia | Descripción Funcional | Microservicio Backend | Endpoint REST | Pantalla / Organismo Frontend | Pruebas Automatizadas | Estado |
|:---:|---|---|---|---|---|:---:|
| **HU-001** | Acceso inicial y autenticación segura con JWT y BCrypt | `user-service` | `POST /api/users/login` | `/` (`LoginPage` / `LoginOrganism`) | `UserServiceTest`, `UserControllerTest`, `login.organism.spec.ts`, Playwright E2E Test 1 | **Implementada** |
| **HU-002** | Creación y registro de usuarios con cédula única | `user-service` | `POST /api/users` | `/users` (`UsersPage` / `UsersOrganism`) | `UserControllerTest`, `UserServiceTest`, `users.organism.spec.ts` | **Implementada** |
| **HU-003** | Consulta individual y listado general de usuarios | `user-service` | `GET /api/users/{cedula}`<br>`GET /api/users` | `/users` (`UsersPage` / `UsersOrganism`) | `UserControllerTest`, `UserServiceTest`, `users.organism.spec.ts` | **Implementada** |
| **HU-004** | Actualización de datos de usuario | `user-service` | `PUT /api/users/{cedula}` | `/users` (`UsersPage` / `UsersOrganism`) | `UserControllerTest`, `UserServiceTest`, `users.organism.spec.ts` | **Implementada** |
| **HU-005** | Borrado y desactivación de usuarios (protección último admin) | `user-service` | `DELETE /api/users/{cedula}` | `/users` (`UsersPage` / `UsersOrganism`) | `UserControllerTest`, `UserServiceTest`, `users.organism.spec.ts` | **Implementada** |
| **HU-006** | Creación y registro de clientes con cédula única | `customer-service` | `POST /api/customers` | `/clients` (`ClientsPage` / `ClientsOrganism`) | `CustomerControllerTest`, `CustomerServiceTest`, `clients.organism.spec.ts`, Playwright E2E Test 2 | **Implementada** |
| **HU-007** | Consulta individual por cédula y listado de clientes | `customer-service` | `GET /api/customers/{cedula}`<br>`GET /api/customers` | `/clients` (`ClientsPage` / `ClientsOrganism`) | `CustomerControllerTest`, `CustomerServiceTest`, `clients.organism.spec.ts`, Playwright E2E Test 2 | **Implementada** |
| **HU-008** | Actualización de información de clientes | `customer-service` | `PUT /api/customers/{cedula}` | `/clients` (`ClientsPage` / `ClientsOrganism`) | `CustomerControllerTest`, `CustomerServiceTest`, `clients.organism.spec.ts`, Playwright E2E Test 2 | **Implementada** |
| **HU-009** | Eliminación de clientes del sistema | `customer-service` | `DELETE /api/customers/{cedula}` | `/clients` (`ClientsPage` / `ClientsOrganism`) | `CustomerControllerTest`, `CustomerServiceTest`, `clients.organism.spec.ts`, Playwright E2E Test 2 | **Implementada** |
| **HU-010** | Creación y registro de proveedores con NIT comercial | `supplier-service` | `POST /api/suppliers` | `/providers` (`ProvidersPage` / `ProvidersOrganism`) | `SupplierControllerTest`, `SupplierServiceTest`, `providers.organism.spec.ts` | **Implementada** |
| **HU-011** | Consulta por NIT y directorio de proveedores | `supplier-service` | `GET /api/suppliers/{nit}`<br>`GET /api/suppliers` | `/providers` (`ProvidersPage` / `ProvidersOrganism`) | `SupplierControllerTest`, `SupplierServiceTest`, `providers.organism.spec.ts` | **Implementada** |
| **HU-012** | Actualización de información de proveedores | `supplier-service` | `PUT /api/suppliers/{nit}` | `/providers` (`ProvidersPage` / `ProvidersOrganism`) | `SupplierControllerTest`, `SupplierServiceTest`, `providers.organism.spec.ts` | **Implementada** |
| **HU-013** | Eliminación de proveedores de la base de datos | `supplier-service` | `DELETE /api/suppliers/{nit}` | `/providers` (`ProvidersPage` / `ProvidersOrganism`) | `SupplierControllerTest`, `SupplierServiceTest`, `providers.organism.spec.ts` | **Implementada** |
| **HU-014** | Carga masiva de catálogo vía archivo CSV de 6 columnas | `catalog-service` | `POST /api/products/import` | `/products` (`ProductsPage` / `ProductsOrganism`) | `ProductImportServiceTest`, `CatalogControllerTest`, `products.organism.spec.ts`, Playwright E2E Test 3 | **Implementada** |
| **HU-015** | Consulta y validación de cliente en formulario de venta | `customer-service` | `GET /api/customers/{cedula}` | `/sales` (`SalesPage` / `SalesOrganism`) | `CustomerControllerTest`, `sales.organism.spec.ts`, Playwright E2E Test 4 | **Implementada** |
| **HU-016** | Consulta y validación de productos por código en venta | `catalog-service` | `GET /api/products/code/{code}` | `/sales` (`SalesPage` / `SalesOrganism`) | `ProductServiceTest`, `CatalogControllerTest`, `sales.organism.spec.ts`, Playwright E2E Test 4 | **Implementada** |
| **HU-017** | Adición y eliminación dinámica de renglones (1 a 3 ítems) | `order-service` | `POST /api/sales` | `/sales` (`SalesPage` / `SalesOrganism`) | `SaleControllerTest`, `OrderServiceTest`, `sales.organism.spec.ts`, Playwright E2E Test 4 | **Implementada** |
| **HU-018** | Cálculo reactivo de subtotal, IVA individual y total | `order-service` | `POST /api/sales` | `/sales` (`SalesPage` / `SalesOrganism`) | `SaleCalculationTest`, `OrderServiceTest`, `sales.organism.spec.ts`, Playwright E2E Test 4 | **Implementada** |
| **HU-019** | Generación de consecutivo autoincremental de venta | `order-service` | `POST /api/sales` | `/sales` (`SalesPage` / `SalesOrganism`) | `SaleConsecutiveTest`, `OrderServiceTest`, `sales.organism.spec.ts`, Playwright E2E Test 4 | **Implementada** |
| **HU-020** | Confirmación e inserción atómica de venta con su detalle | `order-service` | `POST /api/sales` | `/sales` (`SalesPage` / `SalesOrganism`) | `OrderServiceTest`, `SaleControllerTest`, `sales.organism.spec.ts`, Playwright E2E Test 4 | **Implementada** |
| **HU-021** | Reporte consolidado y auditoría de usuarios | `admin-service` | `GET /api/reports/users` | `/reports` (`ReportsPage` / `ReportsOrganism`) | `AdminReportControllerTest`, `reports.organism.spec.ts` | **Implementada** |
| **HU-022** | Reporte consolidado y directorio de clientes | `admin-service` | `GET /api/reports/customers` | `/reports` (`ReportsPage` / `ReportsOrganism`) | `AdminReportControllerTest`, `reports.organism.spec.ts` | **Implementada** |
| **HU-023** | Reporte de ventas por cliente con total general acumulado | `admin-service` | `GET /api/reports/sales-by-customer` | `/reports` (`ReportsPage` / `ReportsOrganism`) | `AdminReportControllerTest`, `reports.organism.spec.ts`, Playwright E2E Test 5 | **Implementada** |

---

## Observaciones de Arquitectura y Aislamiento

1. **Aislamiento por Dominio**: No existen claves foráneas compartidas entre bases de datos de diferentes microservicios. Las relaciones inter-servicio (ej. validación de cédula de cliente o NIT de proveedor) se resuelven a través de llamadas HTTP sincronizadas por la red interna.
2. **Idempotencia Transaccional**: El endpoint `POST /api/sales` soporta el encabezado `Idempotency-Key` (HU-020). Reintentos con la misma clave entregan la misma respuesta sin duplicar el registro en la base de datos ni alterar los consecutivos.
3. **Resguardo de Seguridad**: Las contraseñas de usuarios están protegidas con hashing `BCrypt` y nunca se exponen en consultas REST ni reportes consolidados.
