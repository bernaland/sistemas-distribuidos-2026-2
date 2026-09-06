# cart-service

Microservicio responsable de la gestión del carrito de compras, persistencia por usuario y cálculo automático de subtotales, impuestos (IVA 19%), descuentos y gran total.

## Requisitos
- JDK 17
- Apache Maven 3.8+

## Configuración y Puertos
- **Puerto:** `8084`
- **Base de datos:** H2 en memoria para desarrollo local (`jdbc:h2:mem:cartdb`) y consola en `/h2-console` (usuario `sa`, sin contraseña). Incluye soporte para PostgreSQL en producción (`org.postgresql:postgresql` en `pom.xml`).
- **Tasa de IVA por defecto:** 19% (`app.cart.default-tax-rate: 0.19`).

## Endpoints REST (`/api/cart`)

| Método | Endpoint | Descripción | Body / Parámetros |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/cart/{userId}` | Obtiene o crea el carrito activo del usuario | - |
| `POST` | `/api/cart/{userId}/items` | Agrega un producto al carrito (o incrementa cantidad) | `AddItemRequest` (`productCode`, `productName`, `unitPrice`, `quantity`) |
| `PUT` | `/api/cart/{userId}/items/{productCode}` | Actualiza la cantidad de un item (si es 0, lo elimina) | `UpdateQuantityRequest` (`quantity`) |
| `DELETE` | `/api/cart/{userId}/items/{productCode}` | Elimina un producto específico del carrito | - |
| `DELETE` | `/api/cart/{userId}` | Vacía por completo el carrito | - |
| `POST` | `/api/cart/{userId}/discount` | Aplica un monto de descuento sobre el total | `ApplyDiscountRequest` (`discountAmount`) |

### Ejemplo de Respuesta JSON (`CartResponse`)

```json
{
  "id": 1,
  "userId": "user123",
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
  "discountTotal": 0.00,
  "grandTotal": 2380.00,
  "updatedAt": "2026-09-05T23:09:30.123"
}
```

## Ejecución y Pruebas

```powershell
# Ejecutar pruebas unitarias e integración
mvn clean test -f services/cart-service/pom.xml

# Iniciar el microservicio
mvn spring-boot:run -f services/cart-service/pom.xml
```
