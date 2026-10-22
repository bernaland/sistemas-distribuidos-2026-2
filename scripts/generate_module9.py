# -*- coding: utf-8 -*-
"""
Script to generate ANALISIS/10_modulo9_servicios_auxiliares.md and update checklist for Module 9.
"""
import os

content = """# Auditoría Técnica - Módulo 9: Servicios Auxiliares (84 Archivos)

**Auditor Senior:** Antigravity  
**Fecha:** 2026-10-18  
**Servicios Comprendidos:** `cart-service` (24), `inventory-service` (17), `payment-service` (16), `notification-service` (15), `admin-service` (12).  
**Total de Archivos Auditados en este Módulo:** 84  

---

## 1. Resumen Ejecutivo del Módulo

Este bloque agrupa los cinco microservicios auxiliares y de soporte del ecosistema distribuido:
1. **`cart-service` (Puerto 8084):** Carrito de compras persistente por usuario (`/api/cart/{userId}`). Consulta síncronamente `catalog-service` (`CatalogReader.java`) para obtener precios e impuestos actualizados al momento de agregar productos.
2. **`inventory-service` (Puerto 8086):** Control de existencias y reservas atómicas en dos fases (`Two-Phase Reservation`). Aplica bloqueo pesimista `SELECT ... FOR UPDATE` (`StockRepository.java:8-10`, `ReservationRepository.java:8-10`) y control optimista con `@Version` para evitar sobreventa o condiciones de carrera concurrentes.
3. **`payment-service` (Puerto 8087):** Orquestación y simulación de pagos con garantía de unicidad (`@Column(unique=true) Long paidSaleId`) e idempotencia (`paymentKey`). Valida el monto total consultando síncronamente `order-service:8085` (`SaleLookup.java`).
4. **`notification-service` (Puerto 8088):** Cola y despacho asíncrono de notificaciones por correo (`MailDelivery.java` con `JavaMailSender`). Deshabilitado de forma segura por defecto (`app.mail.enabled: false`).
5. **`admin-service` (Puerto 8083):** Servicio sin estado (stateless) para agregación de reportes administrativos (`ReportController.java`). Consume datos desde `user-service`, `customer-service` y `order-service` calculando métricas consolidadas como `sales-by-customer`.

---

## 2. Inventario y Análisis Detallado por Servicio

---

### 2.1 `cart-service` (24 archivos)

#### Build, Docker y Documentación (3 archivos)
1. `services/cart-service/pom.xml` (59 líneas): Parent Spring Boot 3.1.3, Java 17, `platform-common`, JPA, PostgreSQL, H2, Actuator y `build-helper-maven-plugin:3.5.0` (`pom.xml:51-54`).
2. `services/cart-service/Dockerfile` (10 líneas): Build multi-stage con `maven:3.9.9-eclipse-temurin-17` y runtime `eclipse-temurin:17-jre`, usuario `10001`.
3. `services/cart-service/README.md` (7 líneas): Guía rápida de compilación y puerto 8084.

#### Código Fuente (`src/main/java`, 15 archivos)
4. `CartServiceApplication.java` (12 líneas): Clase de inicio Spring Boot (`@SpringBootApplication`).
5. `CatalogReader.java` (20 líneas): Cliente HTTP para consultar `catalog-service:8082` (`/api/products/code/{code}`). Valida que el producto exista y esté activo (`CatalogReader.java:14-18`).
6. `model/Cart.java` (140 líneas): Entidad JPA (`carts`). Vinculada a `userId` (único). Gestiona lista de `CartItem` (`@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)`). Métodos de mutación de ítems y recálculo automático de subtotales, IVA (por ítem o general) y total final con descuentos (`Cart.java:54-106`).
7. `model/CartItem.java` (75 líneas): Entidad JPA (`cart_items`). Almacena `productCode`, `productName`, `unitPrice`, `quantity`, `ivaRate`, y calcula subtotales e impuestos por línea.
8. `repository/CartRepository.java` (10 líneas): Repositorio JPA con `Optional<Cart> findByUserId(String userId)` y `boolean existsByUserId(String userId)`.
9. `service/CartService.java` (88 líneas): Lógica de negocio. Obtiene o crea carritos, agrega productos validando su estado en catálogo (`CatalogReader`), actualiza cantidades, aplica descuentos y vacía el carrito (`CartService.java:32-84`).
10. `controller/CartController.java` (64 líneas): Controlador REST en `/api/cart`. Endpoints: `GET /{userId}`, `POST /{userId}/items`, `PUT /{userId}/items/{code}`, `DELETE /{userId}/items/{code}`, `DELETE /{userId}`, `POST /{userId}/discount`.
11. `dto/AddItemRequest.java` (32 líneas): DTO de entrada con `@NotBlank productCode` y `@Min(1) quantity`.
12. `dto/ApplyDiscountRequest.java` (20 líneas): DTO de descuento con `@DecimalMin("0.0") discountAmount`.
13. `dto/UpdateQuantityRequest.java` (20 líneas): DTO para ajustar cantidad.
14. `dto/CartResponse.java` (48 líneas): DTO inmutable con desglose de ítems, totales y fechas.
15. `dto/CartItemResponse.java` (30 líneas): DTO de línea de carrito.
16. `exception/InvalidCartOperationException.java` (7 líneas): Excepción para operaciones inválidas en el carrito.
17. `exception/ResourceNotFoundException.java` (7 líneas): Excepción para recursos no encontrados.
18. `exception/GlobalExceptionHandler.java` (44 líneas): Manejador global (`@RestControllerAdvice`) para mapeo a HTTP 400, 404 y 500.

#### Configuración y Tests (6 archivos)
19. `src/main/resources/application.yml` (29 líneas): Puerto 8084, `jdbc:postgresql://localhost:5432/cart`, JWT y Actuator.
20. `src/main/resources/application-distributed.yml` (6 líneas): Configuración Spring Cloud Config.
21. `src/test/resources/application.properties` (7 líneas): Configuración H2 para pruebas unitarias.
22. `src/test/java/com/uelbosque/cartservice/controller/CartControllerTest.java` (134 líneas): Pruebas MockMvc para operaciones CRUD de carrito.
23. `src/test/java/com/uelbosque/cartservice/repository/CartRepositoryTest.java` (48 líneas): Pruebas DataJpaTest para persistencia en cascada.
24. `src/test/java/com/uelbosque/cartservice/service/CartServiceTest.java` (136 líneas): Pruebas unitarias de cálculo de totales, integración con CatalogReader simulado y descuentos.

---

### 2.2 `inventory-service` (17 archivos)

#### Build, Docker y Documentación (3 archivos)
25. `services/inventory-service/pom.xml` (4 líneas): Configurado con `<testSourceDirectory>test</testSourceDirectory>`, `platform-common`, Actuator, H2 y PostgreSQL.
26. `services/inventory-service/Dockerfile` (10 líneas): Multi-stage build con Java 17.
27. `services/inventory-service/README.md` (7 líneas): Puerto 8086 y comandos de ejecución.

#### Código Fuente (`src/main/java`, 10 archivos)
28. `InventoryServiceApplication.java` (7 líneas): Entrypoint Spring Boot.
29. `Stock.java` (26 líneas): Entidad JPA (`stock`). Campos: `productCode` (@Id), `available`, `reserved`, `@Version Long version`. Métodos de ajuste, reserva, liberación (`release`) y consolidación (`commit`) (`Stock.java:11-22`).
30. `StockRepository.java` (11 líneas): Repositorio con `@Lock(LockModeType.PESSIMISTIC_WRITE)` sobre consulta `locked(@Param("code") String code)` (`StockRepository.java:8-10`).
31. `StockResponse.java` (4 líneas): Record DTO (`productCode`, `available`, `reserved`, `total`).
32. `Reservation.java` (23 líneas): Entidad JPA (`reservations`). Campos: `reservationKey` (@Id), `productCode`, `quantity`, `status` (`RESERVED`, `COMMITTED`, `RELEASED`), `@Version Long version` (`Reservation.java:6-21`).
33. `ReservationRepository.java` (11 líneas): Repositorio con bloqueo pesimista `locked(@Param("key") String key)` (`ReservationRepository.java:8-10`).
34. `ReservationRequest.java` (5 líneas): Record DTO (`key`, `productCode`, `quantity`).
35. `ReservationResponse.java` (4 líneas): Record DTO de salida de reserva.
36. `InventoryService.java` (48 líneas): Servicio transaccional. Implementa patrón Two-Phase Reservation: `adjust`, `reserve` (con idempotencia sobre `key`), y `finish(commit/release)` bloqueando filas concurrentemente en orden determinista (`InventoryService.java:12-46`).
37. `InventoryController.java` (22 líneas): Controlador REST en `/api/inventory`. Endpoints: `GET /`, `GET /{code}`, `PUT /{code}`, `POST /reservations`, `POST /reservations/{key}/commit`, `POST /reservations/{key}/release` (`InventoryController.java:10-20`).

#### Configuración y Tests (4 archivos)
38. `src/main/resources/application.yml` (29 líneas): Puerto 8086, PostgreSQL `inventory`.
39. `src/main/resources/application-distributed.yml` (6 líneas): Config server.
40. `test/com/uelbosque/inventoryservice/EndpointTest.java` (21 líneas): Prueba MockMvc del endpoint de listado y ajuste.
41. `test/com/uelbosque/inventoryservice/InventoryRulesTest.java` (23 líneas): Prueba de lógica de reservas: stock insuficiente (HTTP 409), liberación de inventario y commit de reservas.

---

### 2.3 `payment-service` (16 archivos)

#### Build, Docker y Documentación (3 archivos)
42. `services/payment-service/pom.xml` (4 líneas): Maven descriptor con `<testSourceDirectory>test</testSourceDirectory>`.
43. `services/payment-service/Dockerfile` (10 líneas): Dockerfile multi-stage Java 17.
44. `services/payment-service/README.md` (7 líneas): Puerto 8087 y documentación de arquitectura.

#### Código Fuente (`src/main/java`, 8 archivos)
45. `PaymentServiceApplication.java` (7 líneas): Clase principal Spring Boot.
46. `Payment.java` (26 líneas): Entidad JPA (`payments`). Campos: `paymentKey` (@Id), `saleId`, `@Column(unique=true) Long paidSaleId`, `amount`, `status` (`APPROVED`/`DECLINED`), `method` (CASH, CARD, TRANSFER), `simulated`, `createdAt` (`Payment.java:7-24`).
47. `PaymentRepository.java` (6 líneas): Repositorio con `boolean existsByPaidSaleId(Long saleId)`.
48. `PaymentRequest.java` (7 líneas): Record DTO con enum `Method { CASH, CARD, TRANSFER }` y flag `simulateFailure`.
49. `PaymentResponse.java` (6 líneas): Record DTO con datos completos del pago.
50. `SaleLookup.java` (25 líneas): Cliente HTTP hacia `order-service:8085` (`/api/sales/{id}`) para consultar `grandTotal` de la venta antes de procesar el pago (`SaleLookup.java:14-23`).
51. `PaymentService.java` (29 líneas): Servicio transaccional. Implementa idempotencia por `key` y restricción de venta ya pagada (`HTTP 409 Conflict`) (`PaymentService.java:12-28`).
52. `PaymentController.java` (17 líneas): Controlador REST en `/api/payments`. Endpoints: `POST /`, `GET /`, `GET /{key}` (`PaymentController.java:10-16`).

#### Configuración y Tests (5 archivos)
53. `src/main/resources/application.yml` (29 líneas): Puerto 8087, base de datos `payments`.
54. `src/main/resources/application-distributed.yml` (6 líneas): Config server.
55. `test/com/uelbosque/paymentservice/EndpointTest.java` (21 líneas): Prueba MockMvc sobre `/api/payments`.
56. `test/com/uelbosque/paymentservice/PaymentInsertTest.java` (20 líneas): Prueba de persistencia con H2.
57. `test/com/uelbosque/paymentservice/PaymentRulesTest.java` (30 líneas): Prueba de reglas de negocio: rechazo de segundo pago sobre la misma venta (409 Conflict), idempotencia con misma clave y simulación de pagos declinados.

---

### 2.4 `notification-service` (15 archivos)

#### Build, Docker y Documentación (3 archivos)
58. `services/notification-service/pom.xml` (4 líneas): Dependencia de `spring-boot-starter-mail` y test directory `test`.
59. `services/notification-service/Dockerfile` (10 líneas): Contenedor multi-stage Java 17.
60. `services/notification-service/README.md` (7 líneas): Puerto 8088 y notas de operación.

#### Código Fuente (`src/main/java`, 8 archivos)
61. `NotificationServiceApplication.java` (7 líneas): Punto de entrada.
62. `Notification.java` (25 líneas): Entidad JPA (`notifications`). Campos: `notificationKey` (@Id), `recipient`, `subject`, `body`, `sent` (boolean), `createdAt` (`Notification.java:6-23`).
63. `NotificationRepository.java` (10 líneas): Repositorio con bloqueo pesimista `locked(@Param("key") String key)`.
64. `NotificationRequest.java` (5 líneas): Record DTO (`key`, `recipient`, `subject`, `body`).
65. `NotificationResponse.java` (4 líneas): Record DTO de salida con estado `sent`.
66. `MailDelivery.java` (26 líneas): Componente que encapsula `JavaMailSender` (`ObjectProvider<JavaMailSender>`). Valida que el servicio esté habilitado (`app.mail.enabled:false` por defecto) evitando errores si no hay servidor SMTP configurado (`MailDelivery.java:14-24`).
67. `NotificationService.java` (35 líneas): Encola notificaciones con idempotencia (`queue`) y envía mensajes pendientes (`send`), registrando el resultado de despacho (`NotificationService.java:13-33`).
68. `NotificationController.java` (16 líneas): Controlador REST en `/api/notifications`. Endpoints: `POST /`, `GET /`, `GET /{key}`, `POST /{key}/send`.

#### Configuración y Tests (4 archivos)
69. `src/main/resources/application.yml` (29 líneas): Puerto 8088, base de datos `notifications`.
70. `src/main/resources/application-distributed.yml` (6 líneas): Config server.
71. `test/com/uelbosque/notificationservice/EndpointTest.java` (21 líneas): Pruebas de endpoints REST.
72. `test/com/uelbosque/notificationservice/NotificationRulesTest.java` (23 líneas): Pruebas de encolado, idempotencia y comportamiento ante SMTP inactivo.

---

### 2.5 `admin-service` (12 archivos)

#### Build, Docker y Documentación (3 archivos)
73. `services/admin-service/pom.xml` (4 líneas): Microservicio liviano sin entidades JPA.
74. `services/admin-service/Dockerfile` (10 líneas): Contenedor multi-stage.
75. `services/admin-service/README.md` (7 líneas): Especifica que `admin-service` es stateless y no requiere base de datos de ejecución.

#### Código Fuente (`src/main/java`, 5 archivos)
76. `AdminServiceApplication.java` (7 líneas): Entrypoint Spring Boot.
77. `ReportClient.java` (28 líneas): Cliente HTTP para consumir `users:8081`, `customers:8089` y `orders:8085` (`ReportClient.java:13-26`). Mapea a records inmutables `UserRow`, `CustomerRow`, `SaleRow`.
78. `SalesReport.java` (7 líneas): Record DTO para reporte consolidado (`rows`, `totalSales`).
79. `ReportService.java` (22 líneas): Agrega y procesa ventas por cliente calculando acumulados en un `TreeMap<String, SalesReport.Row>` (`ReportService.java:13-21`).
80. `ReportController.java` (13 líneas): Controlador mapeado a `/api/reports` y `/api/admin/reports`. Endpoints: `GET /`, `GET /users`, `GET /customers`, `GET /sales-by-customer` (`ReportController.java:8-12`).

#### Configuración y Tests (4 archivos)
81. `src/main/resources/application.yml` (21 líneas): Puerto 8083, sin datasource configurado.
82. `src/main/resources/application-distributed.yml` (6 líneas): Config server.
83. `test/com/uelbosque/adminservice/EndpointTest.java` (21 líneas): Verifica respuesta de `/api/reports`.
84. `test/com/uelbosque/adminservice/ReportServiceTest.java` (28 líneas): Prueba unitaria de agregación de ventas por cliente y suma total acumulada.

---

## 3. Matriz de Hallazgos y Riesgos en Servicios Auxiliares

| ID | Severidad | Archivo y Línea | Descripción del Problema | Recomendación Técnica |
|---|---|---|---|---|
| **AUX-01** | **Medio** | `admin-service/src/main/java/.../ReportClient.java:19-21` | **Acoplamiento síncrono frágil:** `admin-service` invoca secuencialmente 3 servicios (`users`, `customers`, `orders`) con llamadas bloqueantes simples. Si cualquiera de ellos no responde o falla, la solicitud de reporte colapsa con error 500. | Introducir paralelismo (`CompletableFuture`), timeouts explícitos y degradación elegante ante indisponibilidad parcial. |
| **AUX-02** | **Medio** | `inventory-service` y `payment-service` | **Dependencias de base de datos distribuidas sin 2PC:** La reserva de inventario y el pago se ejecutan en microservicios separados sin un orquestador formal de Saga o compensación en caso de fallo intermedio durante una transacción de venta. | Documentar o implementar un patrón Saga (Coreografiado u Orquestado) para revertir reservas en caso de pago rechazado. |
| **AUX-03** | **Bajo** | `admin-service/pom.xml` | Dependencias innecesarias de `postgresql` y `h2` en un servicio declarado explícitamente como stateless sin base de datos. | Limpiar dependencias sobrantes de persistencia en `admin-service/pom.xml` para reducir tamaño del jar y tiempo de inicio. |
"""

with open('ANALISIS/10_modulo9_servicios_auxiliares.md', 'w', encoding='utf-8') as f:
    f.write(content.strip() + '\n')

print("Created ANALISIS/10_modulo9_servicios_auxiliares.md successfully.")
