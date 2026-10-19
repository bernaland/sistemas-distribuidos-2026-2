# Requerimientos de Tienda Genérica en Microservicios

Esta especificación formaliza los requisitos funcionales, técnicos y de arquitectura implementados en la plataforma distribuida de la Tienda Genérica.

## 1. Dominio Obligatorio Implementado

- **Usuarios (`user-service`)**: Autenticación JWT con cifrado BCrypt, control de cédula única, operaciones CRUD, inicialización segura vía variable de entorno `ADMIN_INITIAL_PASSWORD`, y protección que impide desactivar al último administrador.
- **Clientes (`customer-service`)**: Operaciones CRUD completas con atributos obligatorios (cédula única, nombre, dirección, teléfono, correo) y validaciones de formato.
- **Proveedores (`supplier-service`)**: Operaciones CRUD completas con atributos obligatorios (NIT comercial, nombre, dirección, teléfono, ciudad) y validación de existencia.
- **Catálogo (`catalog-service`)**: Gestión de productos e importación masiva mediante archivo CSV de 6 columnas (`codigo_producto`, `nombre_producto`, `nitproveedor`, `precio_compra`, `ivacompra`, `precio_venta`), con verificación previa de proveedores y reemplazo transaccional atómico.
- **Ventas (`order-service`)**: Validación previa de cliente y productos, cálculo dinámico de subtotal e IVA por producto, soporte de 1 a 3 renglones por factura, asignación de consecutivo autoincremental, idempotencia de transacciones (`Idempotency-Key`) y persistencia de cabecera con detalle de venta.
- **Reportes (`admin-service`)**: Consolidación y auditoría de datos del sistema para: listado de usuarios, listado de clientes, y ventas por cliente con sumatoria de gran total acumulado.

## 2. Distribución y Servicios de Apoyo

- **Infraestructura Base**:
  - `gateway`: Punto de entrada único que enruta tráfico hacia los microservicios backend y valida tokens JWT.
  - `config-server`: Servidor de configuración centralizada con perfil `distributed` protegido por HTTP Basic.
  - `platform-common`: Componente compartido para seguridad, filtros JWT y utilidades de transporte.
- **Servicios Auxiliares Existentes**:
  - `inventory-service`: Gestión de existencias y reservas de inventario.
  - `cart-service`: Operaciones de carrito de compras por identificador de usuario.
  - `payment-service`: Simulación y registro de transacciones de pago.
  - `notification-service`: Generación de notificaciones de pedidos y eventos.

## 3. Frontend Integrado (Angular 22+)

- Arquitectura Atomic Design completa: componentes modulares divididos en atoms, molecules, 8 organisms, templates y pages.
- Enrutamiento con lazy loading y protección mediante Functional Guards (`authGuard`, `roleGuard`).
- Reactividad moderna basada en Signals y nuevo control flow de Angular (`@if`, `@for`).
- Manejo integral de errores con interceptor funcional (401 redirección a login, 400/409 exposición de validaciones, 503 servicio no disponible).
- Suite de pruebas unitarias con cobertura del **89.98% en líneas** y pruebas E2E con Playwright.

## 4. Stack y Validación

- **Backend**: Java 17+, Spring Boot, 14 módulos Maven en reactor con **137 pruebas unitarias/integración** en `src/test/java`.
- **Frontend**: Angular 22, TypeScript, Bootstrap 5, **65 pruebas unitarias** (Karma/Jasmine) y **5 pruebas E2E** (Playwright).
- **Base de Datos**: PostgreSQL 18.6 con separación física por esquema/rol de dominio en Docker Compose.
- **Integración Continua**: Pipeline de GitHub Actions (`.github/workflows/ci.yml`) que compila y prueba automáticamente backend y frontend.
