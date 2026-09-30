# Implementación de microservicios de Tienda Genérica

Alcance autorizado: completar el backend según la revisión del PDF; conservar la interfaz actual. Java 17+, Spring Boot, PostgreSQL 18, inyección por constructor, métodos de hasta 20 líneas y pruebas por servicio.

1. Preparar reactor Maven y utilidades compartidas de seguridad JWT, errores HTTP y clientes con timeout. Las bases pertenecen a cada servicio; no se comparten entidades de negocio.
2. Crear customer-service (8089) y supplier-service (8090): CRUD por cédula/NIT, campos obligatorios, duplicados 409, ausencias 404. Pruebas de creación, actualización, duplicados y eliminación.
3. Completar catálogo: proveedor obligatorio, CSV UTF-8 de seis columnas, validación total antes de reemplazar, conservación de datos ante error, IVA porcentual por producto. Probar archivos inválidos y proveedores inexistentes.
4. Agregar ventas al order-service: cliente y operador existentes, precios consultados al catálogo, 1–3 renglones, IVA individual, cabecera y detalle atómicos, consecutivo persistente e idempotencia. Mantener la API histórica de pedidos documentada como compatibilidad.
5. Completar inventory-service con existencias y reservas idempotentes; payment-service con registro simulado e idempotente; notification-service con bandeja persistente y entrega SMTP opcional; admin-service con agregación de usuarios/clientes/ventas. Probar reglas y fallos de dependencias.
6. Corregir usuarios: cédula, administración protegida, cambios de usuario y desactivación del administrador inicial. Configurar JWT externo.
7. Completar config-server y rutas gateway parametrizadas. PostgreSQL como configuración predeterminada, H2 solamente para pruebas, contenedores y documentación de inicio.
8. Ejecutar pruebas Maven, empaquetar todos los servicios y verificar Docker Compose si hay motor disponible. Revisar fallos antes de declarar finalización.

Decisiones: HTTP síncrono con timeout, sin infraestructura de mensajería innecesaria para el PDF. Ventas y detalles residen juntos; reportes agregan por API. Pagos simulados se identifican como tales; no se capturan datos de tarjeta. No se confirma una venta desde datos de precios proporcionados por el cliente. Las instrucciones antiguas de limitar el PDF a 25 páginas no aplican al alcance autorizado.
