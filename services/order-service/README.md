# order-service

Microservicio responsable de la creación de órdenes de compra, ciclo de vida del pedido (máquina de estados), cálculo automático de subtotales, IVA (19%), descuentos y consulta de historial de pedidos por usuario.

## Requisitos
- JDK 17
- Apache Maven 3.8+

## Configuración y Puertos
- **Puerto:** `8085`
- **Base de datos:** H2 en memoria para desarrollo local (`jdbc:h2:mem:orderdb`) y consola en `/h2-console` (usuario `sa`, sin contraseña). Incluye soporte para PostgreSQL en producción (`org.postgresql:postgresql` en `pom.xml`).
- **Tasa de IVA por defecto:** 19% (`app.order.default-tax-rate: 0.19`).

## Máquina de Estados del Pedido

```
  [PENDING] ---------> [CONFIRMED] ---------> [SHIPPED] ---------> [DELIVERED]
      |                     |
      v                     v
 [CANCELLED]           [CANCELLED]
```
* Una orden en estado `DELIVERED` o `CANCELLED` no puede revertirse ni cancelarse.

## Endpoints REST (`/api/orders`)

| Método | Endpoint | Descripción | Body / Parámetros |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/orders` | Crear nueva orden de compra | `CreateOrderRequest` |
| `GET` | `/api/orders/{id}` | Consultar orden por ID | - |
| `GET` | `/api/orders/number/{orderNumber}` | Consultar orden por número de orden | - |
| `GET` | `/api/orders/user/{userId}` | Listar historial de órdenes de un usuario | - |
| `GET` | `/api/orders` | Listar todas las órdenes (monitoreo/admin) | - |
| `PUT` | `/api/orders/{id}/status` | Actualizar estado de la orden | `UpdateOrderStatusRequest` (`status`) |
| `PUT` | `/api/orders/{id}/cancel` | Cancelar orden | - |

### Ejemplo de Creación de Orden (`POST /api/orders`)

```json
{
  "userId": "user123",
  "customerName": "Carlos Gomez",
  "customerEmail": "carlos@example.com",
  "shippingAddress": "Calle 100 # 15-20, Bogotá",
  "discountAmount": 100.00,
  "items": [
    {
      "productCode": "P001",
      "productName": "Arroz 1kg",
      "unitPrice": 1000.00,
      "quantity": 2
    }
  ]
}
```

### Respuesta (`OrderResponse`)

```json
{
  "id": 1,
  "orderNumber": "ORD-20260905-A1B2C3D4",
  "userId": "user123",
  "customerName": "Carlos Gomez",
  "customerEmail": "carlos@example.com",
  "shippingAddress": "Calle 100 # 15-20, Bogotá",
  "status": "PENDING",
  "items": [
    {
      "id": 1,
      "productCode": "P001",
      "productName": "Arroz 1kg",
      "unitPrice": 1000.00,
      "quantity": 2,
      "itemSubtotal": 2000.00
    }
  ],
  "subtotal": 2000.00,
  "taxRate": 0.19,
  "taxTotal": 380.00,
  "discountTotal": 100.00,
  "grandTotal": 2280.00,
  "createdAt": "2026-09-05T23:25:00.123",
  "updatedAt": "2026-09-05T23:25:00.123"
}
```

## Ejecución y Pruebas

```powershell
# Ejecutar pruebas unitarias e integración
mvn clean test -f services/order-service/pom.xml

# Iniciar el microservicio
mvn spring-boot:run -f services/order-service/pom.xml
```
