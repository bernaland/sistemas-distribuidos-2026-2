# Auditoría Técnica - Módulo 8: Order Service (`services/order-service`)

**Auditor Senior:** Antigravity  
**Fecha:** 2026-10-18  
**Total de Archivos Auditados en este Módulo:** 35  

---

## 1. Resumen Ejecutivo del Módulo

`order-service` es el componente transaccional central del sistema distribuido. Alberga una notable dualidad arquitectónica:
1. **Dominio de Ventas Académico/Empresarial (`Sale` / `/api/sales`):** Implementa el requisito estricto de ventas ("Ventas" en Tienda Genérica) con validación distribuida síncrona contra tres servicios (`customer-service`, `user-service`, `catalog-service`), control de idempotencia distribuida (`Idempotency-Key` en cabecera HTTP) y restricción de hasta 3 ítems por venta (`SaleRequest.java:7`).
2. **Dominio de Órdenes E-commerce Estándar (`Order` / `/api/orders`):** Implementa un flujo completo de órdenes para comercio electrónico con máquina de estados (`PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`), numeración generada tipo `ORD-yyyyMMdd-XXXXXXXX` y transiciones de estado validadas (`InvalidOrderStateException`).
3. **Persistencia Híbrida en PostgreSQL:** Utiliza dos esquemas de tablas en la base de datos `orders`: tablas `sales` / `sale_details` (usando `@ElementCollection`) y tablas `orders` / `order_items` (usando `@OneToMany(cascade = CascadeType.ALL)`).
4. **Soporte de Pruebas Multi-carpeta:** Al igual que `catalog-service`, utiliza `build-helper-maven-plugin:3.5.0` en `pom.xml:51` para enlazar tests situados tanto en `src/test/java/` como en `test/com/uelbosque/orderservice/`.

---

## 2. Inventario y Análisis Detallado Archivo por Archivo (35 Archivos)

### 2.1 Configuración de Construcción, Contenedores y Documentación (3 archivos)

#### 1. `services/order-service/pom.xml` (59 líneas)
- **Propósito:** Descriptor Maven del servicio de órdenes y ventas.
- **Detalles Técnicos:**
  - `parent`: `spring-boot-starter-parent` `3.1.3`, Java 17 (`pom.xml:10-16`).
  - Dependencias: `platform-common:0.0.1-SNAPSHOT` (seguridad JWT compartida), `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `postgresql`, `h2`, `spring-cloud-starter-config:4.0.4`.
  - Plugin `build-helper-maven-plugin:3.5.0` (`pom.xml:51-54`): Agrega el directorio `test` a la fase `generate-test-sources`.

#### 2. `services/order-service/Dockerfile` (10 líneas)
- **Propósito:** Definición de contenedor multi-stage.
- **Detalles Técnicos:**
  - Build: `maven:3.9.9-eclipse-temurin-17` ejecutando `mvn -B -f services/pom.xml -pl order-service -am package -DskipTests` (`Dockerfile:2-5`).
  - Run: `eclipse-temurin:17-jre`, usuario no privilegiado `USER 10001` (`Dockerfile:6-9`).

#### 3. `services/order-service/README.md` (7 líneas)
- **Propósito:** Documentación breve del servicio, puerto e instrucciones de compilación (`README.md:1-7`).

---

### 2.2 Dominio de Ventas (`Sale` / `/api/sales`) (8 archivos)

#### 4. `services/order-service/src/main/java/com/uelbosque/orderservice/Sale.java` (36 líneas)
- **Propósito:** Entidad JPA para la tabla `sales`.
- **Detalles Técnicos:**
  - Mapeo: Tabla `sales`. Clave primaria `@Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id`.
  - Control de idempotencia: `@Column(nullable=false, unique=true) String idempotencyKey` y `@Column(nullable=false, length=2000) String fingerprint` (`Sale.java:10-11`).
  - Auditoría de usuario y cliente: `customerCedula`, `customerName`, `userCedula`, `username`, `createdAt` (con truncado de microsegundos) (`Sale.java:12-16`).
  - Colección embebida: `@ElementCollection` sobre tabla `sale_details` con `@OrderColumn(name="line_number")` (`Sale.java:17-18`).
  - Totales financieros: `subtotal`, `taxTotal`, `grandTotal` con precisión 18 y escala 2 calculados a partir de los ítems (`Sale.java:23-28`).

#### 5. `services/order-service/src/main/java/com/uelbosque/orderservice/SaleLine.java` (31 líneas)
- **Propósito:** Objeto de valor JPA `@Embeddable` que representa una línea de detalle de venta.
- **Detalles Técnicos:**
  - Inmutable en concepto. Valida que la cantidad sea >= 1 (`SaleLine.java:14`).
  - Calcula automáticamente `unitPrice`, `ivaRate`, `subtotal` (multiplicación con redondeo `HALF_UP`) y `tax` (`subtotal * iva / 100` con redondeo `HALF_UP`) (`SaleLine.java:16-19`).

#### 6. `services/order-service/src/main/java/com/uelbosque/orderservice/SaleRepository.java` (7 líneas)
- **Propósito:** Repositorio Spring Data JPA para la entidad `Sale`.
- **Detalles Técnicos:** Define método de consulta `Optional<Sale> findByIdempotencyKey(String idempotencyKey)` (`SaleRepository.java:6`).

#### 7. `services/order-service/src/main/java/com/uelbosque/orderservice/SaleRequest.java` (9 líneas)
- **Propósito:** Record de solicitud para creación de ventas.
- **Detalles Técnicos:**
  - Validación Jakarta: `customerCedula` con `@NotBlank` y `@Pattern(regexp="[0-9]{1,20}")` (`SaleRequest.java:6`).
  - Restricción de negocio: `@Size(min=1, max=3) List<Item> items` — limita explícitamente cada venta a un máximo de 3 líneas de detalle (`SaleRequest.java:7`).
  - Sub-record `Item`: `productCode` (@NotBlank) y `quantity` (@Min(1) @Max(1000000)) (`SaleRequest.java:8`).

#### 8. `services/order-service/src/main/java/com/uelbosque/orderservice/SaleResponse.java` (15 líneas)
- **Propósito:** Record DTO inmutable de respuesta de venta.
- **Detalles Técnicos:**
  - Contiene información completa de cabecera (`id`, cliente, operador, fecha) y totales (`subtotal`, `taxTotal`, `grandTotal`).
  - Sub-record `Line`: Mapea desde `SaleLine` mediante el factory method estático `Line.from(SaleLine line)` (`SaleResponse.java:8-13`).

#### 9. `services/order-service/src/main/java/com/uelbosque/orderservice/SaleDirectory.java` (43 líneas)
- **Propósito:** Cliente HTTP distribuido síncrono para resolución de dependencias remotas.
- **Detalles Técnicos:**
  - Dependencias inyectadas con `@Value`:
    - `services.customers`: por defecto `http://localhost:8089` (`SaleDirectory.java:17`).
    - `services.users`: por defecto `http://localhost:8081` (`SaleDirectory.java:18`).
    - `services.products`: por defecto `http://localhost:8082` (`SaleDirectory.java:18`).
  - Métodos:
    - `customer(String cedula)`: Obtiene cliente de `/api/customers/{id}` (`SaleDirectory.java:21`).
    - `operator(String username)`: Obtiene usuario de `/api/users/username/{id}`, validando que esté habilitado (`enabled`) y posea cédula asignada (`SaleDirectory.java:22-26`). Lanza HTTP 403 Forbidden si no cumple.
    - `product(String code)`: Obtiene producto de `/api/products/code/{id}`, validando que `active == true` (`SaleDirectory.java:27-31`). Lanza HTTP 400 Bad Request si está inactivo.
  - Manejo de errores: Captura `HttpClientErrorException.NotFound` traduciéndolo a `ResponseStatusException(HttpStatus.BAD_REQUEST, "Referencia inexistente: " + id)` (`SaleDirectory.java:36-38`).

#### 10. `services/order-service/src/main/java/com/uelbosque/orderservice/SaleService.java` (33 líneas)
- **Propósito:** Lógica de negocio y orquestación transaccional de ventas.
- **Detalles Técnicos:**
  - Método `create(String key, SaleRequest request, String username)` (`SaleService.java:13-24`):
    1. Valida presencia y longitud de `Idempotency-Key` (hasta 100 caracteres).
    2. Construye huella digital (`fingerprint = username + ":" + request.toString()`).
    3. Si la clave ya existe en `repository.findByIdempotencyKey(key)`, verifica si la huella coincide; si coincide retorna la venta previa (idempotencia exitosa), si no coincide lanza HTTP 409 Conflict.
    4. Consulta síncronamente cliente, operador y cada producto vía `SaleDirectory`.
    5. Persiste y sincroniza la venta en base de datos (`saveAndFlush`).
  - Métodos de consulta de solo lectura: `list()` y `get(Long id)` (`SaleService.java:29-32`).

#### 11. `services/order-service/src/main/java/com/uelbosque/orderservice/SaleController.java` (18 líneas)
- **Propósito:** Controlador REST para el recurso `/api/sales`.
- **Detalles Técnicos:**
  - `POST /api/sales`: Requiere cabecera `@RequestHeader("Idempotency-Key") String key`, `@Valid @RequestBody SaleRequest request` y `Principal principal` para extraer el usuario autenticado (`SaleController.java:12-14`). Retorna HTTP 201 Created.
  - `GET /api/sales`: Retorna lista de ventas (`SaleController.java:15`).
  - `GET /api/sales/{id}`: Retorna detalle de venta por ID (`SaleController.java:16`).

---

### 2.3 Dominio de Órdenes (`Order` / `/api/orders`) (12 archivos)

#### 12. `services/order-service/src/main/java/com/uelbosque/orderservice/OrderServiceApplication.java` (12 líneas)
- **Propósito:** Punto de entrada Spring Boot (`OrderServiceApplication.java:7-11`).

#### 13. `services/order-service/src/main/java/com/uelbosque/orderservice/model/OrderStatus.java` (9 líneas)
- **Propósito:** Enumeración de estados de orden.
- **Detalles Técnicos:** Valores: `PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED` (`OrderStatus.java:3-9`).

#### 14. `services/order-service/src/main/java/com/uelbosque/orderservice/model/Order.java` (158 líneas)
- **Propósito:** Entidad JPA para la tabla `orders`.
- **Detalles Técnicos:**
  - Atributos: `id`, `orderNumber` (único, formato generado), `userId`, `customerName`, `customerEmail`, `shippingAddress`.
  - Estado: `@Enumerated(EnumType.STRING) OrderStatus status = OrderStatus.PENDING`.
  - Relación: `@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) List<OrderItem> items` (`Order.java:44-46`).
  - Lógica financiera: Maneja `subtotal`, `taxRate` (default 19.00%), `taxTotal`, `discountTotal` y `grandTotal` recalculados automáticamente al agregar ítems (`Order.java:118-132`).
  - Control de estados: Métodos `updateStatus(OrderStatus newStatus)` y `cancel()` que restringen transiciones ilegales (p.ej. no se puede cancelar una orden entregada ni modificar una orden cancelada) lanzando `InvalidOrderStateException` (`Order.java:134-155`).

#### 15. `services/order-service/src/main/java/com/uelbosque/orderservice/model/OrderItem.java` (88 líneas)
- **Propósito:** Entidad JPA para la tabla `order_items`.
- **Detalles Técnicos:**
  - Atributos: `id`, `productCode`, `productName`, `unitPrice`, `quantity`, `itemSubtotal`.
  - Relación: `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id") Order order` (`OrderItem.java:28-30`).

#### 16. `services/order-service/src/main/java/com/uelbosque/orderservice/repository/OrderRepository.java` (16 líneas)
- **Propósito:** Repositorio JPA para la entidad `Order`.
- **Detalles Técnicos:** Métodos: `findByOrderNumber`, `findByUserIdOrderByCreatedAtDesc`, `findByStatusOrderByCreatedAtDesc` (`OrderRepository.java:12-15`).

#### 17. `services/order-service/src/main/java/com/uelbosque/orderservice/dto/CreateOrderRequest.java` (61 líneas)
- **Propósito:** DTO para creación de órdenes en `/api/orders`.
- **Detalles Técnicos:** Validaciones en `userId`, `customerName`, `customerEmail` (@Email), `shippingAddress` y lista `items` (@NotEmpty) (`CreateOrderRequest.java:12-28`).

#### 18. `services/order-service/src/main/java/com/uelbosque/orderservice/dto/CreateOrderItemRequest.java` (46 líneas)
- **Propósito:** DTO de ítem individual para creación de orden (`CreateOrderItemRequest.java:8-20`).

#### 19. `services/order-service/src/main/java/com/uelbosque/orderservice/dto/OrderResponse.java` (82 líneas)
- **Propósito:** DTO de respuesta con datos completos de orden, lista de `OrderItemResponse` y desglose de subtotales y fechas (`OrderResponse.java:10-40`).

#### 20. `services/order-service/src/main/java/com/uelbosque/orderservice/dto/OrderItemResponse.java` (42 líneas)
- **Propósito:** DTO de respuesta para líneas de orden (`OrderItemResponse.java:6-20`).

#### 21. `services/order-service/src/main/java/com/uelbosque/orderservice/dto/UpdateOrderStatusRequest.java` (19 líneas)
- **Propósito:** DTO para actualización de estado con `@NotNull OrderStatus status` (`UpdateOrderStatusRequest.java:8-17`).

#### 22. `services/order-service/src/main/java/com/uelbosque/orderservice/service/OrderService.java` (104 líneas)
- **Propósito:** Servicio para operaciones sobre el dominio `Order`.
- **Detalles Técnicos:**
  - `createOrder`: Genera número `ORD-yyyyMMdd-XXXXXXXX`, inicializa orden, calcula montos y persiste (`OrderService.java:28-40`).
  - Consultas: `getOrderById`, `getOrderByOrderNumber`, `getOrdersByUserId`, `getAllOrders`.
  - Mutaciones: `updateOrderStatus` y `cancelOrder` invocando métodos de transición en la entidad (`OrderService.java:64-77`).

#### 23. `services/order-service/src/main/java/com/uelbosque/orderservice/controller/OrderController.java` (67 líneas)
- **Propósito:** Controlador REST en `/api/orders`.
- **Detalles Técnicos:** Endpoints: `POST /`, `GET /{id}`, `GET /number/{orderNumber}`, `GET /user/{userId}`, `GET /`, `PUT /{id}/status`, `PUT /{id}/cancel` (`OrderController.java:26-65`).

---

### 2.4 Excepciones y Manejadores (3 archivos)

#### 24. `services/order-service/src/main/java/com/uelbosque/orderservice/exception/InvalidOrderStateException.java` (7 líneas)
- **Propósito:** Excepción lanzada ante transiciones de estado ilegales en una orden.

#### 25. `services/order-service/src/main/java/com/uelbosque/orderservice/exception/ResourceNotFoundException.java` (7 líneas)
- **Propósito:** Excepción lanzada cuando una orden no es encontrada.

#### 26. `services/order-service/src/main/java/com/uelbosque/orderservice/exception/GlobalExceptionHandler.java` (39 líneas)
- **Propósito:** Manejador central de excepciones para el dominio `Order`.
- **Detalles Técnicos:** Mapea `ResourceNotFoundException` a HTTP 404, `InvalidOrderStateException` a HTTP 400, y validaciones Jakarta a HTTP 400 (`GlobalExceptionHandler.java:16-37`).

---

### 2.5 Configuración de Entornos y Recursos (3 archivos)

#### 27. `services/order-service/src/main/resources/application.yml` (29 líneas)
- **Propósito:** Configuración local del microservicio.
- **Detalles Técnicos:** Puerto `8085`, URL de base de datos `jdbc:postgresql://localhost:5432/orders`, usuario `orders`, `open-in-view: false`, configuración JWT y Actuator.

#### 28. `services/order-service/src/main/resources/application-distributed.yml` (6 líneas)
- **Propósito:** Perfil distribuido con cliente de Spring Cloud Config (`application-distributed.yml:1-6`).

#### 29. `services/order-service/src/test/resources/application.properties` (7 líneas)
- **Propósito:** Base de datos H2 en memoria para ejecución de pruebas unitarias (`application.properties:1-7`).

---

### 2.6 Batería de Pruebas Unitarias y de Integración (6 archivos)

#### 30. `services/order-service/src/test/java/com/uelbosque/orderservice/controller/OrderControllerTest.java` (150 líneas)
- **Propósito:** Pruebas unitarias de la capa web de `/api/orders` con `MockMvc`.
- **Detalles Técnicos:** Verifica creación exitosa (201), validación de campos obligatorios (400), consulta por ID y número de orden, actualización de estado y cancelación.

#### 31. `services/order-service/src/test/java/com/uelbosque/orderservice/repository/OrderRepositoryTest.java` (59 líneas)
- **Propósito:** Pruebas de integración sobre la capa de persistencia de `orders` usando `@DataJpaTest`.
- **Detalles Técnicos:** Prueba consultas por número de orden, búsqueda por `userId` y búsqueda por estado.

#### 32. `services/order-service/src/test/java/com/uelbosque/orderservice/service/OrderServiceTest.java` (146 líneas)
- **Propósito:** Pruebas unitarias de la capa de servicio `OrderService` con Mockito.
- **Detalles Técnicos:** Verifica cálculo de totales, transiciones de estado, excepción ante orden inexistente y cancelación.

#### 33. `services/order-service/test/com/uelbosque/orderservice/SaleServiceTest.java` (40 líneas)
- **Propósito:** Pruebas unitarias sobre `SaleService`.
- **Detalles Técnicos:** Verifica obtención de precios del catálogo, cálculo individualizado de tasas de IVA, rechazo de cantidades inválidas y verificación de que fallos de dependencias remotas no persistan ventas (`SaleServiceTest.java:18-36`).

#### 34. `services/order-service/test/com/uelbosque/orderservice/SalesApiTest.java` (25 líneas)
- **Propósito:** Pruebas de integración web para `/api/sales` con `MockMvc` y seguridad `@WithMockUser(username="admininicial", roles="ADMIN")`.
- **Detalles Técnicos:** Verifica rechazo ante cantidades negativas (400) y listado inicial de ventas (`SalesApiTest.java:18-24`).

#### 35. `services/order-service/test/com/uelbosque/orderservice/SalesPersistenceTest.java` (39 líneas)
- **Propósito:** Pruebas de persistencia e idempotencia distribuida para ventas.
- **Detalles Técnicos:**
  - Prueba persistencia completa con H2 y retorno de la misma venta al reintentar con la misma `Idempotency-Key` (`SalesPersistenceTest.java:19-27`).
  - Prueba conflicto HTTP 409 cuando se intenta reutilizar una misma clave de idempotencia con una solicitud diferente (`SalesPersistenceTest.java:28-33`).

---

## 3. Matriz de Hallazgos y Riesgos en `order-service`

| ID | Severidad | Archivo y Línea | Descripción del Problema | Recomendación Técnica |
|---|---|---|---|---|
| **ORD-01** | **Medio** | Arquitectura general del microservicio | **Dualidad conceptual de dominio:** Coexisten `/api/sales` (modelo `Sale`) y `/api/orders` (modelo `Order`) sin vinculación entre ellos. El frontend utiliza conceptualmente "ventas" pero el backend mantiene ambas entidades en paralelo. | Documentar explícitamente en el diseño arquitectónico si `/api/orders` será un módulo e-commerce futuro o si debe consolidarse con `/api/sales`. |
| **ORD-02** | **Medio** | `SaleDirectory.java:32-40` | **Falta de resiliencia y timeouts en llamadas distribuidas:** Las llamadas `RestTemplate.getForObject` no configuran timeouts de conexión o lectura ni circuit breaker (Resilience4j). Si `catalog-service` o `customer-service` sufren degradación de latencia, los hilos de `order-service` se agotarán. | Configurar timeouts explícitos en `RestTemplate` e implementar patrones de resiliencia (Retry / Circuit Breaker). |
| **ORD-03** | **Bajo** | `service/OrderService.java:55` | Carácter con codificación incorrecta en mensaje de error (`nmero`). | Corregir string a `número` con UTF-8 íntegro. |
