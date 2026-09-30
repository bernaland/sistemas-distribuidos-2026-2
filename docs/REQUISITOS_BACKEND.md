# Correspondencia del PDF con el backend

| Requisitos | Implementación |
|---|---|
| HU-001: acceso inicial | user-service, administrador inicial y validación BCrypt/JWT |
| HU-002 a HU-005: usuarios | CRUD, cédula única, consulta por cédula, cambios de usuario y desactivación |
| Clientes | customer-service: cédula, nombre, dirección, teléfono, correo y CRUD |
| Proveedores | supplier-service: NIT, nombre, dirección, teléfono, ciudad y CRUD |
| HU-014 / SP3-QA | catálogo: carga CSV con reemplazo transaccional y validación de proveedores |
| HU-015 a HU-020 / SP4-QA | order-service `/api/sales`: consulta de referencias, totales e IVA individual, consecutivo y detalle persistente |
| HU-021 a HU-023 / SP5-QA | admin-service: usuarios, clientes, ventas por cliente y consolidado |

El modelo relacional del PDF se distribuye por dominios. Ventas y detalle pertenecen a la misma base; las referencias a clientes, operadores y productos se resuelven por HTTP. No se usan claves foráneas entre bases de servicios. Los nombres de rutas REST se adaptan al proyecto y están documentados en BACKEND.md.

El stack actual del usuario (Spring Boot, Angular y PostgreSQL) tiene prioridad sobre las versiones antiguas, MySQL e instrucciones de despliegue históricas del PDF. Este incremento se limita al backend; para aceptar las historias desde la interfaz aún hay que sustituir los datos de demostración y consumir las APIs.
