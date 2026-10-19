import json

out_path = 'ANALISIS/02_fase2_arquitectura.md'

with open('ANALISIS/arch_data.json', 'r', encoding='utf-8') as f:
    arch_data = json.load(f)

content = """# Fase 2 – Arquitectura del Sistema

## 1. Propósito General del Proyecto y Flujo de Negocio

El proyecto **sistemas-distribuidos-2026-2** implementa una plataforma distribuida para la operación de una **Tienda Genérica**. El objetivo principal es descentralizar el monolito tradicional de comercio y gestión administrativa en una arquitectura de **microservicios autónomos**, garantizando:
- Alta cohesión y bajo acoplamiento por dominio funcional.
- Propiedad exclusiva de los datos (Database per Service) sobre PostgreSQL.
- Comunicación inter-servicio síncrona mediante REST/HTTP con propagación de contexto de seguridad (JWT).
- Tolerancia a fallos, idempotencia en operaciones de escritura y control de concurrencia.

### Flujo General de Operación:
```
1. Autenticación:
   Operador -> Gateway (8080) -> user-service (8081) [/api/users/login] -> Emisión de JWT (HS256)

2. Administración de Catálogo y Proveedores:
   Admin -> Gateway -> supplier-service (8090) -> Alta de proveedores (NIT)
   Admin -> Gateway -> catalog-service (8082) [/api/products/import] -> Validación CSV (6 cols)
                                                                     -> Validación HTTP con supplier-service
                                                                     -> Reemplazo atómico con bloqueo mutex

3. Registro de Clientes:
   Admin/Operador -> Gateway -> customer-service (8089) [/api/customers] -> CRUD por cédula

4. Proceso de Venta (Core):
   Operador -> Gateway -> order-service (8085) [/api/sales + Idempotency-Key]
                       -> Valida Cliente con customer-service (8089)
                       -> Valida Operador con user-service (8081)
                       -> Consulta Precios e IVA oficial con catalog-service (8082)
                       -> Valida regla de 1 a 3 ítems
                       -> Persiste cabecera (Sale) y renglones (SaleLine) atómicamente
                       -> Devuelve consecutivo oficial y totales calculados

5. Generación de Reportes:
   Admin -> Gateway -> admin-service (8088) [/api/reports/*]
                    -> Agrega datos vía HTTP de user-service, customer-service y order-service
                    -> Consolida ventas totales por cliente
```

---

## 2. Puntos de Entrada, Capas y Módulos

### 2.1. Puntos de Entrada
- **API Gateway (`services/gateway`):** Punto único de entrada exterior (puerto `8080`). Enruta dinámicamente según la ruta URL hacia los 10 microservicios funcionales. Configura políticas de CORS globales y límites de timeout (3s conexión, 10s respuesta).
- **Config Server (`services/config-server`):** Puerto `8888`. Punto de entrada de configuración distribuida para los microservicios que inicien con el perfil `distributed`. Protegido por autenticación HTTP Basic (`config:CONFIG_PASSWORD`).
- **PostgreSQL Database:** Puerto `5432` expuesto para tareas de mantenimiento/depuración del desarrollador. Cada microservicio se conecta internamente a su propia base de datos (`users`, `customers`, `suppliers`, `catalog`, `carts`, `orders`, `inventory`, `payments`, `notifications`).

### 2.2. Capas de la Solución
1. **Capa de Presentación (Frontend SPA):**
   - Angular 16 estructurado bajo Atomic Design (`atoms`, `molecules`, `organisms`, `pages`, `templates`).
   - *Hallazgo crítico de arquitectura:* Los componentes del frontend operan actualmente con datos mock en memoria y no consumen las APIs del API Gateway (falta integración HTTP y configuración de `HttpClientModule`).
2. **Capa de Enrutamiento y Seguridad Perimetral (API Gateway):**
   - Spring Cloud Gateway (basado en Reactor / WebFlux / Netty).
   - Manejo centralizado de CORS, balanceo estático/dinámico y control de fallbacks.
3. **Capa de Lógica de Negocio y Dominio (Microservicios Spring Boot):**
   - **Servicios de Dominio Obligatorio:** `user-service`, `customer-service`, `supplier-service`, `catalog-service`, `order-service`, `admin-service`.
   - **Servicios Auxiliares / Opcionales:** `cart-service`, `inventory-service`, `payment-service`, `notification-service`.
4. **Capa Compartida Transversal (`platform-common`):**
   - Configuración unificada de Spring Security con Resource Server JWT (HS256).
   - Manejo de excepciones globales y formateo homogéneo de errores HTTP (`ApiErrors`).
   - Bean de `RestTemplate` con timeouts configurados y propagador automático del encabezado `Authorization: Bearer <token>`.
5. **Capa de Persistencia e Infraestructura:**
   - PostgreSQL en contenedores Docker, con bases de datos y roles creados por script (`init-databases.sh`).
   - H2 Database en memoria utilizado estrictamente durante la ejecución de tests JUnit.

---

## 3. Diagrama de Arquitectura Completo (Mermaid)

```mermaid
graph TB
    subgraph Clientes ["Capa de Cliente"]
        Browser["Navegador Web / Frontend SPA (Angular 16)"]
        Curl["Clientes Externos / Operadores (cURL / Postman)"]
    end

    subgraph Perimetro ["Puerta de Enlace & Configuración"]
        Gateway["API Gateway (Port 8080)<br/>Spring Cloud Gateway"]
        ConfigServer["Config Server (Port 8888)<br/>Spring Cloud Config"]
    end

    subgraph CoreServices ["Microservicios de Dominio Obligatorio"]
        UserService["user-service (8081)<br/>Auth, JWT, Roles, CRUD"]
        CustomerService["customer-service (8089)<br/>CRUD Clientes"]
        SupplierService["supplier-service (8090)<br/>CRUD Proveedores"]
        CatalogService["catalog-service (8082)<br/>Catálogo & Importación CSV"]
        OrderService["order-service (8085)<br/>Ventas & Detalle (Idempotente)"]
        AdminService["admin-service (8088)<br/>Reportes y Consolidación"]
    end

    subgraph AuxServices ["Microservicios Auxiliares"]
        CartService["cart-service (8084)<br/>Carrito de Compras"]
        InventoryService["inventory-service (8083)<br/>Existencias & Reservas"]
        PaymentService["payment-service (8086)<br/>Pagos Simulados"]
        NotificationService["notification-service (8087)<br/>Notificaciones & SMTP"]
    end

    subgraph Persistence ["Persistencia Dedicada (PostgreSQL 18.6)"]
        DB_Users[(db: users)]
        DB_Customers[(db: customers)]
        DB_Suppliers[(db: suppliers)]
        DB_Catalog[(db: catalog)]
        DB_Orders[(db: orders)]
        DB_Carts[(db: carts)]
        DB_Inventory[(db: inventory)]
        DB_Payments[(db: payments)]
        DB_Notifications[(db: notifications)]
    end

    %% Routing
    Browser -->|HTTP Requests| Gateway
    Curl -->|HTTP Requests| Gateway

    Gateway -->|/api/users/**| UserService
    Gateway -->|/api/customers/**| CustomerService
    Gateway -->|/api/suppliers/**| SupplierService
    Gateway -->|/api/products/**| CatalogService
    Gateway -->|/api/sales/**, /api/orders/**| OrderService
    Gateway -->|/api/reports/**| AdminService
    Gateway -->|/api/cart/**| CartService
    Gateway -->|/api/inventory/**| InventoryService
    Gateway -->|/api/payments/**| PaymentService
    Gateway -->|/api/notifications/**| NotificationService

    %% Inter-service HTTP
    CatalogService -.->|Valida NIT Proveedor| SupplierService
    OrderService -.->|Valida Cliente| CustomerService
    OrderService -.->|Valida Operador| UserService
    OrderService -.->|Consulta Precios e IVA| CatalogService
    AdminService -.->|Consulta Usuarios| UserService
    AdminService -.->|Consulta Clientes| CustomerService
    AdminService -.->|Consulta Ventas| OrderService
    CartService -.->|Consulta Precios| CatalogService
    PaymentService -.->|Consulta Venta| OrderService

    %% Config Distribution
    ConfigServer -.->|Bootstrap Config| UserService
    ConfigServer -.->|Bootstrap Config| CustomerService
    ConfigServer -.->|Bootstrap Config| SupplierService
    ConfigServer -.->|Bootstrap Config| CatalogService
    ConfigServer -.->|Bootstrap Config| OrderService
    ConfigServer -.->|Bootstrap Config| AdminService

    %% Database Mapping
    UserService --> DB_Users
    CustomerService --> DB_Customers
    SupplierService --> DB_Suppliers
    CatalogService --> DB_Catalog
    OrderService --> DB_Orders
    CartService --> DB_Carts
    InventoryService --> DB_Inventory
    PaymentService --> DB_Payments
    NotificationService --> DB_Notifications
```

---

## 4. Modelo de Datos y Esquemas de Base de Datos

Cada servicio gestiona su propio esquema de base de datos de manera aislada (Database per Service pattern). No existen Foreign Keys físicas entre distintas bases de datos; la integridad referencial inter-dominio se valida en la capa de servicio mediante consultas HTTP síncronas.

### 4.1. Esquema `users` (`user-service`)
- **Tabla `users`:**
  - `id` (BIGINT, PK, Generated sequence)
  - `cedula` (VARCHAR(20), UNIQUE, NOT NULL): Documento de identidad del usuario.
  - `username` (VARCHAR(50), UNIQUE, NOT NULL): Nombre de usuario único.
  - `password` (VARCHAR(100), NOT NULL): Hash BCrypt de la contraseña.
  - `name` (VARCHAR(100), NOT NULL): Nombre completo.
  - `email` (VARCHAR(100), NOT NULL): Correo electrónico.
  - `roles` (VARCHAR(100), NOT NULL): Roles asignados (ej. `ROLE_ADMIN,ROLE_USER`).
  - `enabled` (BOOLEAN, NOT NULL): Indicador de usuario activo/inactivo.

### 4.2. Esquema `customers` (`customer-service`)
- **Tabla `customers`:**
  - `cedula` (VARCHAR(20), PK): Documento de identidad único del cliente.
  - `name` (VARCHAR(100), NOT NULL): Nombre completo o razón social.
  - `address` (VARCHAR(150), NOT NULL): Dirección física.
  - `phone` (VARCHAR(20), NOT NULL): Número de contacto.
  - `email` (VARCHAR(100), NOT NULL): Correo electrónico del cliente.

### 4.3. Esquema `suppliers` (`supplier-service`)
- **Tabla `suppliers`:**
  - `nit` (VARCHAR(20), PK): Número de Identificación Tributaria único del proveedor.
  - `name` (VARCHAR(100), NOT NULL): Razón social.
  - `address` (VARCHAR(150), NOT NULL): Dirección de la sede.
  - `phone` (VARCHAR(20), NOT NULL): Teléfono de contacto.
  - `city` (VARCHAR(50), NOT NULL): Ciudad de radicación.

### 4.4. Esquema `catalog` (`catalog-service`)
- **Tabla `products`:**
  - `id` (BIGINT, PK, Generated sequence)
  - `code` (VARCHAR(50), UNIQUE, NOT NULL): Código de producto (columna 1 del CSV).
  - `name` (VARCHAR(50), NOT NULL): Nombre del producto (columna 2 del CSV).
  - `supplier_nit` (VARCHAR(20), NOT NULL): NIT del proveedor (columna 3 del CSV, validado contra `supplier-service`).
  - `purchase_price` (NUMERIC(15,2), NOT NULL): Precio de compra (columna 4 del CSV).
  - `iva_rate` (NUMERIC(5,2), NOT NULL): Porcentaje de IVA de 0 a 100 (columna 5 del CSV).
  - `sale_price` (NUMERIC(15,2), NOT NULL): Precio oficial de venta (columna 6 del CSV).
  - `category` (VARCHAR(50)): Categoría opcional.
  - `image_url` (VARCHAR(255)): URL de imagen opcional.
  - `active` (BOOLEAN, NOT NULL): Estado del producto.
- **Tabla `catalogwritelock`:**
  - Bloqueo pesimista para evitar colisiones durante la importación simultánea de archivos CSV.

### 4.5. Esquema `orders` (`order-service`)
- **Tabla `sales`:**
  - `id` (BIGINT, PK, Generated sequence / Consecutivo oficial)
  - `customer_cedula` (VARCHAR(20), NOT NULL): Cédula del cliente.
  - `customer_name` (VARCHAR(100), NOT NULL): Copia inmutable del nombre del cliente.
  - `operator_cedula` (VARCHAR(20), NOT NULL): Cédula del operador autenticado.
  - `username` (VARCHAR(50), NOT NULL): Username del operador.
  - `idempotency_key` (VARCHAR(100), UNIQUE, NOT NULL): Clave de idempotencia para evitar registros duplicados.
  - `subtotal` (NUMERIC(15,2), NOT NULL): Suma de subtotales sin IVA.
  - `iva_total` (NUMERIC(15,2), NOT NULL): Suma de IVAs individuales.
  - `total` (NUMERIC(15,2), NOT NULL): Importe total a pagar (subtotal + iva_total).
  - `created_at` (TIMESTAMP, NOT NULL): Marca de tiempo del registro.
- **Tabla `sale_lines`:**
  - `id` (BIGINT, PK, Generated sequence)
  - `sale_id` (BIGINT, FK -> `sales.id`, NOT NULL): Relación obligatoria con la cabecera.
  - `product_code` (VARCHAR(50), NOT NULL): Código del producto al momento de venta.
  - `product_name` (VARCHAR(100), NOT NULL): Nombre histórico del producto.
  - `quantity` (INT, NOT NULL): Cantidad (de 1 a 3 ítems según restricción de negocio).
  - `unit_price` (NUMERIC(15,2), NOT NULL): Precio de venta congelado.
  - `iva_rate` (NUMERIC(5,2), NOT NULL): Tasa de IVA congelada.
  - `subtotal` (NUMERIC(15,2), NOT NULL): `quantity * unit_price`.
  - `iva_amount` (NUMERIC(15,2), NOT NULL): `subtotal * (iva_rate / 100)`.
  - `line_total` (NUMERIC(15,2), NOT NULL): `subtotal + iva_amount`.
- **Tablas heredadas (Legacy):** `orders` y `order_items` (conservadas para compatibilidad de consulta; creación deshabilitada).

### 4.6. Esquema `carts` (`cart-service`)
- **Tabla `carts`:** `id`, `user_id`, `subtotal`, `tax_total`, `discount_total`, `grand_total`.
- **Tabla `cart_items`:** `id`, `cart_id`, `product_code`, `product_name`, `unit_price`, `quantity`, `item_subtotal`.

### 4.7. Esquema `inventory` (`inventory-service`)
- **Tabla `stock`:** `product_code` (PK), `available`, `reserved`.
- **Tabla `reservations`:** `id`, `reservation_key` (UNIQUE), `product_code`, `quantity`, `status` (`RESERVED`, `COMMITTED`, `RELEASED`).

### 4.8. Esquema `payments` (`payment-service`)
- **Tabla `payments`:** `id`, `payment_key` (UNIQUE), `sale_id`, `amount`, `method` (`CASH`, `TRANSFER`, `SIMULATED_CARD`), `status` (`APPROVED`, `DECLINED`), `simulated` (BOOLEAN).

### 4.9. Esquema `notifications` (`notification-service`)
- **Tabla `notifications`:** `id`, `notification_key` (UNIQUE), `recipient`, `subject`, `body`, `status` (`PENDING`, `SENT`, `FAILED`), `attempts`, `created_at`.

---

## 5. Matriz Completa de Endpoints y APIs por Microservicio

| Servicio | Método | Ruta (Vía Gateway) | Descripción / Contrato | Seguridad |
|---|---|---|---|---|
| **user-service** | `POST` | `/api/users/login` | Autenticación de usuarios; retorna JWT Bearer | Pública |
| **user-service** | `POST` | `/api/users` | Creación de nuevo usuario (requiere cédula obligatoria) | `ROLE_ADMIN` |
| **user-service** | `GET` | `/api/users` | Listado de todos los usuarios registrados | `ROLE_ADMIN` |
| **user-service** | `GET` | `/api/users/cedula/{cedula}` | Consulta de usuario por número de cédula | Autenticado |
| **user-service** | `GET` | `/api/users/{id}` | Consulta de usuario por ID numérico | Autenticado |
| **user-service** | `PUT` | `/api/users/{id}` | Actualización de datos o estado enabled | `ROLE_ADMIN` |
| **user-service** | `DELETE` | `/api/users/{id}` | Eliminación de usuario (protegido contra último admin) | `ROLE_ADMIN` |
| **customer-service** | `POST` | `/api/customers` | Creación de cliente (cédula única, 409 si duplicado) | `ROLE_ADMIN` |
| **customer-service** | `GET` | `/api/customers` | Consulta y listado de clientes | Autenticado |
| **customer-service** | `GET` | `/api/customers/{cedula}` | Consulta de cliente por cédula | Autenticado |
| **customer-service** | `PUT` | `/api/customers/{cedula}` | Actualización de dirección, teléfono o correo | `ROLE_ADMIN` |
| **customer-service** | `DELETE` | `/api/customers/{cedula}` | Eliminación de cliente | `ROLE_ADMIN` |
| **supplier-service** | `POST` | `/api/suppliers` | Creación de proveedor (NIT único) | `ROLE_ADMIN` |
| **supplier-service** | `GET` | `/api/suppliers` | Consulta y listado de proveedores | Autenticado |
| **supplier-service** | `GET` | `/api/suppliers/{nit}` | Consulta de proveedor por NIT | Autenticado |
| **supplier-service** | `PUT` | `/api/suppliers/{nit}` | Actualización de proveedor | `ROLE_ADMIN` |
| **supplier-service** | `DELETE` | `/api/suppliers/{nit}` | Eliminación de proveedor | `ROLE_ADMIN` |
| **catalog-service** | `POST` | `/api/products/import` | Carga masiva CSV de 6 columnas con reemplazo transaccional | `ROLE_ADMIN` |
| **catalog-service** | `GET` | `/api/products` | Catálogo completo paginado o listado | Autenticado |
| **catalog-service** | `GET` | `/api/products/code/{code}` | Consulta de producto por código único | Autenticado |
| **catalog-service** | `POST` | `/api/products` | Creación individual de producto | `ROLE_ADMIN` |
| **catalog-service** | `PUT` | `/api/products/{id}` | Actualización individual de producto | `ROLE_ADMIN` |
| **catalog-service** | `DELETE` | `/api/products/{id}` | Eliminación individual de producto | `ROLE_ADMIN` |
| **order-service** | `POST` | `/api/sales` | Registro de venta atómica (1-3 ítems) con `Idempotency-Key` | Autenticado |
| **order-service** | `GET` | `/api/sales` | Listado histórico de ventas | Autenticado |
| **order-service** | `GET` | `/api/sales/{id}` | Detalle completo de venta por ID consecutivo | Autenticado |
| **admin-service** | `GET` | `/api/reports/users` | Reporte agregado de usuarios (sin hashes de password) | `ROLE_ADMIN` |
| **admin-service** | `GET` | `/api/reports/customers` | Reporte agregado de clientes | `ROLE_ADMIN` |
| **admin-service** | `GET` | `/api/reports/sales-by-customer` | Ventas agrupadas por cliente con consolidado general | `ROLE_ADMIN` |
| **inventory-service** | `GET` | `/api/inventory` | Consulta global de existencias | Autenticado |
| **inventory-service** | `PUT` | `/api/inventory/{code}` | Ajuste manual de existencias físicas | `ROLE_ADMIN` |
| **inventory-service** | `POST` | `/api/inventory/reservations` | Creación de reserva idempotente | Autenticado |
| **inventory-service** | `POST` | `/api/inventory/reservations/{key}/commit` | Confirmación definitiva de consumo de reserva | Autenticado |
| **inventory-service** | `POST` | `/api/inventory/reservations/{key}/release` | Liberación de reserva | Autenticado |
| **payment-service** | `POST` | `/api/payments` | Registro de pago simulado idempotente por `saleId` | Autenticado |
| **payment-service** | `GET` | `/api/payments/{key}` | Consulta de estado de pago por clave | Autenticado |
| **notification-service** | `POST` | `/api/notifications` | Registro de mensaje en outbox | Autenticado |
| **notification-service** | `POST` | `/api/notifications/{key}/send` | Despacho de correo vía SMTP | `ROLE_ADMIN` |
| **gateway** | `GET` | `/api/gateway/health` | Estado de salud del Gateway y rutas | Pública |
| **config-server** | `GET` | `/{app}/{profile}` | Obtención de propiedades de configuración Spring | HTTP Basic |
"""

with open(out_path, 'w', encoding='utf-8') as out:
    out.write(content)

print(f'Fase 2 guardada en {out_path}')
