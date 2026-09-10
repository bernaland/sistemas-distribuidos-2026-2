# Backend de Tienda Genérica

Implementación del dominio del PDF adaptada a microservicios. El frontend existente todavía requiere integración HTTP; este trabajo implementa y verifica el backend.

## Inicio con Docker

Desde la raíz, con Docker Desktop y el motor Linux funcionando:

```powershell
./scripts/setup-env.ps1
docker compose up --build -d
```

La primera orden crea `.env` con claves aleatorias y conserva un archivo existente. No compartir ese archivo. El gateway queda en `http://localhost:8080`. PostgreSQL queda en `localhost:5432` para desarrollo. Los servicios de negocio solo están expuestos dentro de la red de Compose. Config Server está en `localhost:8888`, con usuario `config` y contraseña de `.env`.

Se usa PostgreSQL 18.6. El volumen conserva los datos al detener los contenedores. El script de inicialización crea una base y un rol propietario por dominio y restringe las conexiones entre bases. Hibernate `ddl-auto: update` permite iniciar este proyecto académico; un despliegue con datos de producción debe usar migraciones versionadas antes de cambiar esquemas existentes.

## Compilación y pruebas

Con Java 17 o superior (`JAVA_HOME` configurado). El wrapper descarga Maven 3.9.9 si hace falta:

```powershell
./services/mvnw.cmd -B -f services/pom.xml clean verify
```

Los proyectos nuevos tienen pruebas en `services/{service}/test`. Los servicios existentes conservan también sus pruebas en `src/test/java`, incluidas en el reactor. H2 se utiliza únicamente para pruebas. La biblioteca `platform-common` contiene seguridad y transporte HTTP, sin compartir entidades de negocio.

Para ejecutar servicios fuera de Docker, configurar `JWT_SECRET`, `DB_PASSWORD`, `DB_URL` y `DB_USER`. Todas las instancias deben usar la misma clave JWT. Las URL de dependencias se ajustan con `SERVICES_USERS`, `SERVICES_CUSTOMERS`, `SERVICES_SUPPLIERS`, `SERVICES_PRODUCTS` y `SERVICES_ORDERS`. El gateway usa `USER_SERVICE_URL`, `CUSTOMER_SERVICE_URL`, etc. Los valores predeterminados de URL apuntan a los puertos de la tabla.

El perfil `distributed` habilita Config Server. Compose lo activa y configura la autenticación. El perfil predeterminado permite ejecutar y probar cada servicio sin depender del servidor de configuración. Las configuraciones locales permiten arrancar incluso si el servidor central está temporalmente fuera de servicio.

## Servicios y contratos

| Servicio | Puerto | API |
|---|---:|---|
| user-service | 8081 | `/api/users`, `/api/users/login`, `/api/users/cedula/{cedula}` |
| catalog-service | 8082 | `/api/products`, `/api/products/code/{code}`, `/api/products/import` |
| inventory-service | 8083 | `/api/inventory`, `/api/inventory/reservations` |
| cart-service | 8084 | `/api/cart/{userId}` y operaciones existentes |
| order-service | 8085 | `/api/sales`, `/api/sales/{id}` |
| payment-service | 8086 | `/api/payments`, `/api/payments/{key}` |
| notification-service | 8087 | `/api/notifications`, `/api/notifications/{key}/send` |
| admin-service | 8088 | `/api/reports/users`, `/api/reports/customers`, `/api/reports/sales-by-customer` |
| customer-service | 8089 | `/api/customers`, `/api/customers/{cedula}` |
| supplier-service | 8090 | `/api/suppliers`, `/api/suppliers/{nit}` |
| gateway | 8080 | Todas las rutas anteriores |
| config-server | 8888 | `/{application}/{profile}` con HTTP Basic |

Clientes y proveedores: POST en la colección crea; GET lista; GET/PUT/DELETE en el identificador consulta, actualiza y elimina. La cédula/NIT es una cadena de entre 1 y 20 dígitos para evitar pérdida de precisión en JavaScript. No se cambia el identificador mediante PUT. Duplicados producen 409, referencias inexistentes 404 y datos inválidos 400.

## Flujo reproducible

Todas las llamadas siguientes, excepto login, requieren `Authorization: Bearer TOKEN` y pasan por el gateway.

1. Iniciar sesión: `POST /api/users/login`.

```json
{"username":"admininicial","password":"admin123456"}
```

El administrador inicial se crea solo cuando la base de usuarios está vacía y tiene cédula técnica `0`. Crear un administrador personal mediante `POST /api/users`:

```json
{"cedula":"1001234567","username":"administrador","password":"UnaClavePersonalSegura123","name":"Administrador tienda","email":"admin@example.com","roles":"ROLE_ADMIN,ROLE_USER"}
```

Ingresar con el nuevo administrador y desactivar el inicial con `PUT /api/users/{id}` y `{"enabled":false}`. El sistema impide eliminar o desactivar el último administrador activo. La cédula es obligatoria en altas. Se pueden actualizar nombre, correo, contraseña, usuario, roles y habilitación. Nunca se devuelven contraseñas ni hashes en consultas o reportes.

2. Crear proveedor: `POST /api/suppliers`.

```json
{"nit":"900112233","name":"Proveedor uno","address":"Calle 10","phone":"3001234567","city":"Bogota"}
```

3. Crear cliente: `POST /api/customers`.

```json
{"cedula":"1020304050","name":"Cliente uno","address":"Carrera 7","phone":"3007654321","email":"cliente@example.com"}
```

4. Importar productos: `POST /api/products/import`, multipart con campo `file`. Archivo UTF-8 terminado en `.csv`, máximo 5 MB, con estas seis columnas en orden (encabezado opcional):

```csv
codigo_producto,nombre_producto,nitproveedor,precio_compra,ivacompra,precio_venta
1,Leche,900112233,1000,0,1500
2,Arroz,900112233,2000,5,3000
3,Cafe,900112233,5000,19,7000
```

Se admiten campos entre comillas según CSV. Se validan columnas, números, importes no negativos, IVA de 0 a 100, nombre de hasta 50 caracteres, códigos duplicados y existencia de cada proveedor. Solo después de validar todas las filas se reemplaza el catálogo en una transacción. Un fallo conserva el catálogo anterior. Los importes usan punto decimal. El CRUD individual también exige `supplierNit`.

5. Registrar venta: `POST /api/sales`, encabezado adicional `Idempotency-Key: venta-ejemplo-001`.

```json
{"customerCedula":"1020304050","items":[{"productCode":"1","quantity":2},{"productCode":"2","quantity":1}]}
```

El operador se obtiene del token y del servicio de usuarios. El backend consulta los precios y tasas del catálogo y valida al cliente. La respuesta contiene identificador consecutivo, cédulas, productos, cantidades y totales: en este ejemplo subtotal 6000.00, IVA 150.00 y total 6150.00. Admite de uno a tres renglones; cantidades enteras positivas. Cabecera y detalle se guardan juntos, con una copia del nombre, precio e IVA que protege el historial frente a importaciones posteriores.

Repetir la misma clave y contenido devuelve la venta existente; reutilizarla para otros datos produce 409. Si dos solicitudes con la misma clave llegan simultáneamente, la restricción única puede devolver 409 a una de ellas; puede consultarse/reintentarse la misma operación sin crear una segunda venta. El consecutivo lo asigna la base de datos; una transacción abortada puede dejar un hueco.

La API histórica `/api/orders` conserva consultas de pedidos anteriores. Su POST está bloqueado porque aceptaba precios del cliente: las nuevas ventas se registran exclusivamente en `/api/sales`. El carrito es una herramienta opcional de preparación y vuelve a consultar precios al agregar productos; la venta siempre recalcula desde el catálogo y no toma descuentos del carrito.

6. Consultar `GET /api/reports/sales-by-customer`. Agrupa por cédula y devuelve `customers` y `grandTotal`. Los otros dos reportes consultan los servicios propietarios. Si una dependencia falla, la API informa indisponibilidad, sin reemplazar los resultados por datos de ejemplo.

## Servicios auxiliares

Estos servicios amplían el dominio del PDF y tienen contratos independientes. Registrar una venta no ejecuta automáticamente una pasarela, una reserva de inventario ni un envío de correo.

**Inventario:** `PUT /api/inventory/1` con `{"onHand":10}` establece existencias. `POST /api/inventory/reservations` con `{"key":"reserva-001","productCode":"1","quantity":2}` reserva unidades. POST a `/api/inventory/reservations/reserva-001/commit` confirma el consumo; `/release` libera. Las operaciones son idempotentes y no permiten liberar una reserva consumida ni reservar más de lo disponible. Los ajustes no pueden reducir existencias por debajo de las reservas vigentes.

**Pagos:** `POST /api/payments` con `{"key":"pago-001","saleId":1,"method":"CASH","simulateFailure":false}`. Métodos admitidos: `CASH`, `TRANSFER`, `SIMULATED_CARD`. Obtiene el importe de la venta, impide dos pagos aprobados para la misma venta y devuelve `simulated:true`. Con `simulateFailure:true` registra `DECLINED`. No conecta con bancos, no recibe números de tarjeta y no realiza cobros reales.

**Notificaciones:** `POST /api/notifications` con `{"key":"correo-001","recipient":"cliente@example.com","subject":"Venta registrada","body":"Gracias por su compra"}` guarda un mensaje `PENDING`. El envío se solicita con POST a `/{key}/send`. Requiere `APP_MAIL_ENABLED=true`, `APP_MAIL_FROM` y las propiedades estándar `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`, además de TLS según el servidor. Sin SMTP configurado responde 503 y conserva el pendiente. Un fallo SMTP queda `FAILED` y puede reintentarse. No hay envío automático; si el proceso cae después de entregar al servidor SMTP y antes del commit, un reintento podría duplicar el correo.

## Seguridad y operación

JWT HS256 con clave externa y vencimiento de 15 minutos. Administradores gestionan usuarios, clientes, proveedores, catálogo, inventario y notificaciones; operadores autenticados realizan ventas y consultas. El servicio de usuarios comprueba el estado actual de la cuenta en cada petición. Los demás servicios validan la firma y vencimiento del token; la revocación de tokens ya emitidos no es inmediata fuera del servicio de usuarios.

Las llamadas entre servicios propagan el token, con 3 segundos de conexión y 5 de lectura. El gateway limita la espera de respuesta a 10 segundos. No se reintentan automáticamente escrituras. Salud técnica: `/actuator/health`. Config Server no contiene secretos; estos se suministran por entorno.

La API se diseñó para operadores internos de la tienda, no para acceso directo de clientes finales. Los reportes no muestran contraseñas aunque el documento antiguo las incluyera.
