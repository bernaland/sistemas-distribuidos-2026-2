# Requerimientos de Tienda Genérica en microservicios

Esta especificación reemplaza la propuesta inicial de comercio electrónico. El alcance funcional se basa en el PDF de Tienda Genérica y en la revisión autorizada; no se limita a sus primeras 25 páginas.

## Dominio obligatorio

- Usuarios: autenticación, cédula única, CRUD, administración protegida y desactivación del usuario inicial.
- Clientes: CRUD de cédula, nombre, dirección, teléfono y correo.
- Proveedores: CRUD de NIT, nombre, dirección, teléfono y ciudad.
- Catálogo: productos y carga CSV de seis columnas; validar proveedores y reemplazar los productos atómicamente.
- Ventas: cliente por cédula, operador autenticado, uno a tres renglones, precios del catálogo, IVA individual, consecutivo y detalle persistente.
- Reportes: usuarios, clientes y total de ventas por cliente con consolidado.

## Distribución

Los dominios anteriores se implementan en user-service, customer-service, supplier-service, catalog-service, order-service y admin-service. Cada servicio persistente es propietario de su base PostgreSQL. La información entre dominios se consulta por HTTP; ventas y detalle se guardan en una sola transacción local.

API Gateway enruta solicitudes. Config Server distribuye configuración no secreta. JWT protege los servicios. Las claves y las credenciales se inyectan por entorno.

Inventario, carrito, pagos simulados y notificaciones son servicios auxiliares existentes que también se implementan. No sustituyen los módulos obligatorios y no hay un cobro externo ni envío de correo automático al registrar ventas.

## Stack y validación

Java 17+, Spring Boot, PostgreSQL 18.6 y pruebas JUnit por servicio. H2 se utiliza solo en las pruebas. El frontend Angular permanece pendiente de integración con las APIs de este incremento.

El funcionamiento, los contratos HTTP, los permisos y las limitaciones se documentan en [docs/BACKEND.md](docs/BACKEND.md). La correspondencia con historias del PDF está en [docs/REQUISITOS_BACKEND.md](docs/REQUISITOS_BACKEND.md).
